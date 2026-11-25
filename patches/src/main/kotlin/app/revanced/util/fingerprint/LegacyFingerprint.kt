package app.revanced.util.fingerprint

import app.revanced.com.android.tools.smali.dexlib2.mutable.MutableClassDef
import app.revanced.com.android.tools.smali.dexlib2.mutable.MutableMethod
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.getInstruction
import app.revanced.patcher.firstClassDef
import app.revanced.patcher.firstMethod
import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.PatchException
import app.revanced.util.containsLiteralInstruction
import app.revanced.util.indexOfFirstInstructionOrThrow
import app.revanced.util.indexOfFirstLiteralInstruction
import app.revanced.util.injectLiteralInstructionViewCall
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.StringReference

/**
 * Patcher v22 removed the `Fingerprint` class and the `fingerprint {}` DSL in favour of
 * `BytecodePatchContext.xxxMethod by gettingFirstMethodDeclaratively {}`.
 *
 * RVX declares ~700 fingerprints through [legacyFingerprint]. Rewriting every one of them by hand would be
 * error-prone, so the declaration surface (`legacyFingerprint(...)`, `Pair<String, LegacyFingerprint>`,
 * `.methodOrThrow()`, `.matchOrThrow()`, ...) is kept and only the matching engine is re-implemented here
 * on top of the v22 primitives ([BytecodePatchContext.classDefs], [firstMethod], [firstClassDef]).
 *
 * Matching semantics are intentionally identical to the Patcher v21 `Fingerprint`:
 *  - accessFlags: exact equality.
 *  - returnType: `startsWith`.
 *  - parameters: same size, every parameter `startsWith` the expected one.
 *  - strings: every string must be contained (`contains`) by some `const-string` of the method.
 *  - opcodes: sliding window, `null` = wildcard, `fuzzyPatternScanThreshold` = allowed mismatches.
 */
class PatternMatch(val startIndex: Int, val endIndex: Int)

class StringMatch(val string: String, val index: Int)

class LegacyMatch internal constructor(
    val originalMethod: Method,
    val originalClassDef: ClassDef,
    val method: MutableMethod,
    val classDef: MutableClassDef,
    val patternMatch: PatternMatch?,
    val stringMatches: List<StringMatch>?,
)

class LegacyFingerprint internal constructor(
    private val fuzzyPatternScanThreshold: Int,
    private val accessFlags: Int?,
    private val returnType: String?,
    private val parameters: List<String>?,
    private val opcodes: List<Opcode?>?,
    private val strings: List<String>?,
    private val literals: List<Long>?,
    private val customFingerprint: ((methodDef: Method, classDef: ClassDef) -> Boolean)?,
) {
    // Cache of the whole-app resolution. Tied to the context instance that produced it.
    private var cachedContext: BytecodePatchContext? = null
    private var cachedMatch: LegacyMatch? = null

    // region Pure (immutable) matching

    private class Result(val patternMatch: PatternMatch?, val stringMatches: List<StringMatch>?)

    private fun matchesParameters(method: Method): Boolean {
        val expected = parameters ?: return true
        val actual = method.parameterTypes
        if (expected.size != actual.size) return false
        for (i in expected.indices) {
            if (!actual[i].startsWith(expected[i])) return false
        }
        return true
    }

    private fun matchStrings(method: Method): Pair<Boolean, List<StringMatch>?> {
        val wanted = strings ?: return true to null
        val instructions = method.implementation?.instructions ?: return false to null

        val pending = wanted.toMutableList()
        val matches = mutableListOf<StringMatch>()

        instructions.forEachIndexed { index, instruction ->
            if (pending.isEmpty()) return@forEachIndexed
            if (instruction.opcode != Opcode.CONST_STRING && instruction.opcode != Opcode.CONST_STRING_JUMBO)
                return@forEachIndexed

            val string = ((instruction as ReferenceInstruction).reference as StringReference).string
            val i = pending.indexOfFirst { string.contains(it) }
            if (i < 0) return@forEachIndexed

            matches += StringMatch(pending[i], index)
            pending.removeAt(i)
        }

        return (pending.isEmpty()) to matches
    }

    private fun matchPattern(method: Method): Pair<Boolean, PatternMatch?> {
        val pattern = opcodes ?: return true to null
        val instructions = method.implementation?.instructions?.toList() ?: return false to null

        val patternLength = pattern.size
        val instructionLength = instructions.size
        if (patternLength == 0) return true to null

        for (index in 0 until instructionLength) {
            var patternIndex = 0
            var threshold = fuzzyPatternScanThreshold

            while (index + patternIndex < instructionLength) {
                val originalOpcode = instructions[index + patternIndex].opcode
                val patternOpcode = pattern[patternIndex]

                if (patternOpcode != null && patternOpcode.ordinal != originalOpcode.ordinal) {
                    // Reaching the maximum threshold (0) means the pattern does not match here.
                    if (threshold-- == 0) break
                }

                if (patternIndex < pattern.lastIndex) {
                    patternIndex++
                } else {
                    return true to PatternMatch(index, index + patternIndex)
                }
            }
        }

        return false to null
    }

    private fun matchesMethod(method: Method, classDef: ClassDef): Result? {
        val accessFlags = accessFlags
        if (accessFlags != null && accessFlags != method.accessFlags) return null

        val returnType = returnType
        if (returnType != null && !method.returnType.startsWith(returnType)) return null

        if (!matchesParameters(method)) return null

        val (stringsOk, stringMatches) = matchStrings(method)
        if (!stringsOk) return null

        val literals = literals
        if (literals != null) {
            for (literal in literals) {
                if (!method.containsLiteralInstruction(literal)) return null
            }
        }

        val customFingerprint = customFingerprint
        if (customFingerprint != null && !customFingerprint(method, classDef)) return null

        val (patternOk, patternMatch) = matchPattern(method)
        if (!patternOk) return null

        return Result(patternMatch, stringMatches)
    }

    // endregion

    context(context: BytecodePatchContext)
    private fun matchInClass(classDef: ClassDef): LegacyMatch? {
        for (method in classDef.methods) {
            val result = matchesMethod(method, classDef) ?: continue

            return LegacyMatch(
                originalMethod = method,
                originalClassDef = classDef,
                method = context.firstMethod(method),
                classDef = context.firstClassDef(classDef.type),
                patternMatch = result.patternMatch,
                stringMatches = result.stringMatches,
            )
        }
        return null
    }

    /**
     * Match this fingerprint against the whole app. The result is cached per context.
     */
    context(context: BytecodePatchContext)
    fun matchOrNull(): LegacyMatch? {
        if (cachedContext === context) cachedMatch?.let { return it }

        for (classDef in context.classDefs) {
            val match = matchInClass(classDef) ?: continue

            cachedContext = context
            cachedMatch = match
            return match
        }

        return null
    }

    /**
     * Match this fingerprint only inside the given class. Not cached.
     */
    context(_: BytecodePatchContext)
    fun matchOrNull(classDef: ClassDef): LegacyMatch? = matchInClass(classDef)

    context(_: BytecodePatchContext)
    fun match(classDef: ClassDef): LegacyMatch =
        matchOrNull(classDef) ?: throw PatchException("Failed to resolve fingerprint in ${classDef.type}")

    context(_: BytecodePatchContext)
    val methodOrNull: MutableMethod?
        get() = matchOrNull()?.method

    context(_: BytecodePatchContext)
    val originalMethodOrNull: Method?
        get() = matchOrNull()?.originalMethod

    context(_: BytecodePatchContext)
    val classDefOrNull: MutableClassDef?
        get() = matchOrNull()?.classDef

    // Patcher v21 `Fingerprint` parity: these throw when the fingerprint can not be resolved.

    context(_: BytecodePatchContext)
    val method: MutableMethod
        get() = methodOrNull ?: throw PatchException("Failed to resolve fingerprint")

    context(_: BytecodePatchContext)
    val originalMethod: Method
        get() = originalMethodOrNull ?: throw PatchException("Failed to resolve fingerprint")

    context(_: BytecodePatchContext)
    val classDef: MutableClassDef
        get() = classDefOrNull ?: throw PatchException("Failed to resolve fingerprint")
}

private val String.exception
    get() = PatchException("Failed to resolve $this")

context(_: BytecodePatchContext)
internal fun Pair<String, LegacyFingerprint>.resolvable(): Boolean =
    second.methodOrNull != null

context(_: BytecodePatchContext)
internal fun Pair<String, LegacyFingerprint>.definingClassOrThrow(): String =
    second.classDefOrNull?.type ?: throw first.exception

context(_: BytecodePatchContext)
internal fun Pair<String, LegacyFingerprint>.matchOrThrow(): LegacyMatch =
    second.matchOrNull() ?: throw first.exception

context(_: BytecodePatchContext)
internal fun Pair<String, LegacyFingerprint>.matchOrThrow(
    parentFingerprint: Pair<String, LegacyFingerprint>
): LegacyMatch {
    val parentClassDef = parentFingerprint.second.classDefOrNull
        ?: throw parentFingerprint.first.exception
    return second.matchOrNull(parentClassDef)
        ?: throw first.exception
}

context(_: BytecodePatchContext)
internal fun Pair<String, LegacyFingerprint>.matchOrNull(): LegacyMatch? =
    second.matchOrNull()

context(_: BytecodePatchContext)
internal fun Pair<String, LegacyFingerprint>.matchOrNull(
    parentFingerprint: Pair<String, LegacyFingerprint>
): LegacyMatch? =
    parentFingerprint.second.classDefOrNull?.let { parentClassDef ->
        second.matchOrNull(parentClassDef)
    }

context(_: BytecodePatchContext)
internal fun Pair<String, LegacyFingerprint>.methodOrNull(): MutableMethod? =
    matchOrNull()?.method

context(_: BytecodePatchContext)
internal fun Pair<String, LegacyFingerprint>.methodOrThrow(): MutableMethod =
    second.methodOrNull ?: throw first.exception

context(_: BytecodePatchContext)
internal fun Pair<String, LegacyFingerprint>.methodOrThrow(
    parentFingerprint: Pair<String, LegacyFingerprint>
): MutableMethod =
    matchOrThrow(parentFingerprint).method

context(_: BytecodePatchContext)
internal fun Pair<String, LegacyFingerprint>.originalMethodOrThrow(): Method =
    second.originalMethodOrNull ?: throw first.exception

context(_: BytecodePatchContext)
internal fun Pair<String, LegacyFingerprint>.originalMethodOrThrow(
    parentFingerprint: Pair<String, LegacyFingerprint>
): Method =
    matchOrThrow(parentFingerprint).originalMethod

context(_: BytecodePatchContext)
internal fun Pair<String, LegacyFingerprint>.mutableClassOrThrow(): MutableClassDef =
    second.classDefOrNull ?: throw first.exception

context(_: BytecodePatchContext)
internal fun Pair<String, LegacyFingerprint>.methodCall() =
    methodOrThrow().methodCall()

internal fun MutableMethod.methodCall(): String {
    var methodCall = "$definingClass->$name("
    for (i in 0 until parameters.size) {
        methodCall += parameterTypes[i]
    }
    methodCall += ")$returnType"
    return methodCall
}

context(_: BytecodePatchContext)
fun Pair<String, LegacyFingerprint>.injectLiteralInstructionBooleanCall(
    literal: Long,
    descriptor: String
) {
    methodOrThrow().apply {
        val literalIndex = indexOfFirstLiteralInstruction(literal)
        val index = indexOfFirstInstructionOrThrow(literalIndex, Opcode.MOVE_RESULT)
        val register = getInstruction<OneRegisterInstruction>(index).registerA

        val smaliInstruction =
            if (descriptor.startsWith("0x")) """
                const/16 v$register, $descriptor
                """
            else if (descriptor.endsWith("(Z)Z")) """
                invoke-static/range { v$register .. v$register }, $descriptor
                move-result v$register
                """
            else """
                invoke-static {}, $descriptor
                move-result v$register
                """

        addInstructions(
            index + 1,
            smaliInstruction
        )
    }
}

context(_: BytecodePatchContext)
fun Pair<String, LegacyFingerprint>.injectLiteralInstructionViewCall(
    literal: Long,
    smaliInstruction: String
) {
    val method = methodOrThrow()
    method.injectLiteralInstructionViewCall(literal, smaliInstruction)
}

internal fun legacyFingerprint(
    name: String,
    fuzzyPatternScanThreshold: Int = 0,
    accessFlags: Int? = null,
    returnType: String? = null,
    parameters: List<String>? = null,
    opcodes: List<Opcode?>? = null,
    strings: List<String>? = null,
    literals: List<Long>? = null,
    customFingerprint: ((methodDef: Method, classDef: ClassDef) -> Boolean)? = null,
) = Pair(
    name,
    LegacyFingerprint(
        fuzzyPatternScanThreshold = fuzzyPatternScanThreshold,
        accessFlags = accessFlags,
        returnType = returnType,
        parameters = parameters,
        opcodes = opcodes,
        strings = strings,
        literals = literals,
        customFingerprint = customFingerprint,
    )
)
