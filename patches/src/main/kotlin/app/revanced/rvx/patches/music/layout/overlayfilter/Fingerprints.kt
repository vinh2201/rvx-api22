package app.revanced.rvx.patches.music.layout.overlayfilter

import app.revanced.rvx.patches.music.utils.resourceid.designBottomSheetDialog
import app.revanced.rvx.util.fingerprint.legacyFingerprint
import com.android.tools.smali.dexlib2.Opcode

internal val designBottomSheetDialogFingerprint = legacyFingerprint(
    name = "designBottomSheetDialogFingerprint",
    returnType = "V",
    parameters = emptyList(),
    opcodes = listOf(
        Opcode.IF_NEZ,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT
    ),
    literals = listOf(designBottomSheetDialog)
)
