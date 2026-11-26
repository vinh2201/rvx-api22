package app.revanced.rvx.patches.youtube.misc.openlinks.directly

import app.revanced.patcher.extensions.getInstruction
import app.revanced.patcher.extensions.replaceInstruction
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.rvx.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.rvx.patches.youtube.utils.extension.Constants.MISC_PATH
import app.revanced.rvx.patches.youtube.utils.patch.PatchList.BYPASS_URL_REDIRECTS
import app.revanced.rvx.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.revanced.rvx.patches.youtube.utils.settings.settingsPatch
import app.revanced.rvx.util.fingerprint.methodOrThrow
import app.revanced.rvx.util.getReference
import app.revanced.rvx.util.indexOfFirstInstructionOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Suppress("unused")
val openLinksDirectlyPatch = bytecodePatch(
    BYPASS_URL_REDIRECTS.patchName,
    BYPASS_URL_REDIRECTS.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(settingsPatch)

    apply {

        arrayOf(
            openLinksDirectlyFingerprintPrimary,
            openLinksDirectlyFingerprintSecondary
        ).forEach { fingerprint ->
            fingerprint.methodOrThrow().apply {
                val insertIndex = indexOfFirstInstructionOrThrow {
                    opcode == Opcode.INVOKE_STATIC &&
                            getReference<MethodReference>()?.name == "parse"
                }
                val insertRegister =
                    getInstruction<FiveRegisterInstruction>(insertIndex).registerC

                replaceInstruction(
                    insertIndex,
                    "invoke-static {v$insertRegister}, $MISC_PATH/OpenLinksDirectlyPatch;->parseRedirectUri(Ljava/lang/String;)Landroid/net/Uri;"
                )
            }
        }

        // region add settings

        addPreference(
            arrayOf(
                "SETTINGS: BYPASS_URL_REDIRECTS"
            ),
            BYPASS_URL_REDIRECTS
        )

        // endregion

    }
}
