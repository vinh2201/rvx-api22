package app.revanced.patches.music.utils.extension.hooks

import app.revanced.patches.shared.extension.extensionHook
import app.revanced.util.getReference
import app.revanced.util.indexOfFirstInstruction
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

internal val applicationInitHook = extensionHook(
    returnType = "V",
    parameters = listOf(),
    strings = listOf("activity"),
    customFingerprint = { method, _ ->
        method.name == "onCreate" &&
                method.indexOfFirstInstruction {
                    opcode == Opcode.INVOKE_VIRTUAL
                            && getReference<MethodReference>()?.name == "getRunningAppProcesses"
                } >= 0
    },
)
