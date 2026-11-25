package app.revanced.patches.shared.extension

import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.com.android.tools.smali.dexlib2.iface.value.MutableLongEncodedValue
import app.revanced.patches.shared.extension.Constants.EXTENSION_PATCH_STATUS_CLASS_DESCRIPTOR
import app.revanced.patches.shared.extension.Constants.EXTENSION_UTILS_CLASS_DESCRIPTOR
import app.revanced.util.findMethodsOrThrow
import app.revanced.util.returnEarly
import app.revanced.util.fingerprint.LegacyFingerprint
import app.revanced.util.fingerprint.legacyFingerprint
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.immutable.value.ImmutableLongEncodedValue
import java.util.jar.Manifest

fun sharedExtensionPatch(
    vararg hooks: ExtensionHook,
) = bytecodePatch(
    description = "sharedExtensionPatch"
) {
    extendWith("extensions/rvx-shared.rve")

    apply {
        if (classDefs.none { EXTENSION_UTILS_CLASS_DESCRIPTOR == it.type }) {
            throw PatchException(
                "Shared extension has not been merged yet. This patch can not succeed without merging it.",
            )
        }
        hooks.forEach { hook -> hook(EXTENSION_UTILS_CLASS_DESCRIPTOR) }
    }

    afterDependents finalize@{
        findMethodsOrThrow(EXTENSION_PATCH_STATUS_CLASS_DESCRIPTOR).apply {
            find { method -> method.name == "PatchedTime" }
                ?.replaceInstruction(
                    0,
                    "const-wide v0, ${MutableLongEncodedValue(ImmutableLongEncodedValue(System.currentTimeMillis()))}L"
                )

            find { method -> method.name == "PatchVersion" }
                ?.apply {
                    val manifest = object {}
                        .javaClass
                        .classLoader
                        .getResources("META-INF/MANIFEST.MF")

                    while (manifest.hasMoreElements()) {
                        Manifest(manifest.nextElement().openStream())
                            .mainAttributes
                            .getValue("Version")
                            ?.let {
                                returnEarly(it)
                                return@finalize
                            }
                    }
                }
        }
    }
}

class ExtensionHook internal constructor(
    val fingerprint: LegacyFingerprint,
    private val insertIndexResolver: ((Method) -> Int),
    private val contextRegisterResolver: (Method) -> String,
) {
    context(_: BytecodePatchContext)
    operator fun invoke(extensionClassDescriptor: String) {
        val method = fingerprint.methodOrNull
            ?: throw PatchException("Failed to resolve the extension hook fingerprint")

        val insertIndex = insertIndexResolver(method)
        val contextRegister = contextRegisterResolver(method)

        method.addInstruction(
            insertIndex,
            "invoke-static/range { $contextRegister .. $contextRegister }, " +
                    "$extensionClassDescriptor->setContext(Landroid/content/Context;)V",
        )
    }
}

fun extensionHook(
    insertIndexResolver: ((Method) -> Int) = { 0 },
    contextRegisterResolver: (Method) -> String = { "p0" },
    returnType: String? = null,
    parameters: List<String>? = null,
    strings: List<String>? = null,
    customFingerprint: ((methodDef: Method, classDef: ClassDef) -> Boolean)? = null,
) = ExtensionHook(
    legacyFingerprint(
        name = "extensionHookFingerprint",
        returnType = returnType,
        parameters = parameters,
        strings = strings,
        customFingerprint = customFingerprint,
    ).second,
    insertIndexResolver,
    contextRegisterResolver
)
