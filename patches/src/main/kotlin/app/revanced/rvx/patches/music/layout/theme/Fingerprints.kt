package app.revanced.rvx.patches.music.layout.theme

import app.revanced.rvx.patches.music.utils.resourceid.elementsContainer
import app.revanced.rvx.util.fingerprint.legacyFingerprint
import app.revanced.rvx.util.or
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal val elementsContainerFingerprint = legacyFingerprint(
    name = "elementsContainerFingerprint",
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    opcodes = listOf(Opcode.INVOKE_DIRECT_RANGE),
    literals = listOf(elementsContainer)
)
