package app.revanced.rvx.patches.youtube.misc.accessibility

import app.revanced.patcher.extensions.getInstruction
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.rvx.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.rvx.patches.youtube.utils.patch.PatchList.HIDE_ACCESSIBILITY_CONTROLS_DIALOG
import app.revanced.rvx.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.revanced.rvx.patches.youtube.utils.settings.settingsPatch
import app.revanced.rvx.util.findMethodOrThrow
import app.revanced.rvx.util.fingerprint.mutableClassOrThrow
import app.revanced.rvx.util.indexOfFirstInstruction
import app.revanced.rvx.util.indexOfFirstInstructionReversedOrThrow
import app.revanced.rvx.util.or
import app.revanced.rvx.util.returnEarly
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction

@Suppress("unused")
val accessibilityPatch = bytecodePatch(
    HIDE_ACCESSIBILITY_CONTROLS_DIALOG.patchName,
    HIDE_ACCESSIBILITY_CONTROLS_DIALOG.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(settingsPatch)

    apply {

        playerAccessibilitySettingsEduControllerParentFingerprint
            .mutableClassOrThrow()
            .methods
            .first { method -> method.name == "<init>" }
            .apply {
                val lifecycleObserverIndex =
                    indexOfFirstInstructionReversedOrThrow(Opcode.NEW_INSTANCE)
                val lifecycleObserverClass =
                    getInstruction<ReferenceInstruction>(lifecycleObserverIndex).reference.toString()

                findMethodOrThrow(lifecycleObserverClass) {
                    accessFlags == AccessFlags.PUBLIC or AccessFlags.FINAL &&
                            parameterTypes.size == 1 &&
                            indexOfFirstInstruction(Opcode.INVOKE_DIRECT) >= 0
                }.returnEarly()
            }

        addPreference(HIDE_ACCESSIBILITY_CONTROLS_DIALOG)

    }
}
