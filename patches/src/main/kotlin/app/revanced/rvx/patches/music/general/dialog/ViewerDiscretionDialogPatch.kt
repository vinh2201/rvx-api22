package app.revanced.rvx.patches.music.general.dialog

import app.revanced.patcher.patch.bytecodePatch
import app.revanced.rvx.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.rvx.patches.music.utils.extension.Constants.GENERAL_CLASS_DESCRIPTOR
import app.revanced.rvx.patches.music.utils.patch.PatchList.REMOVE_VIEWER_DISCRETION_DIALOG
import app.revanced.rvx.patches.music.utils.settings.CategoryType
import app.revanced.rvx.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.revanced.rvx.patches.music.utils.settings.addSwitchPreference
import app.revanced.rvx.patches.music.utils.settings.settingsPatch
import app.revanced.rvx.patches.shared.dialog.baseViewerDiscretionDialogPatch

@Suppress("unused")
val viewerDiscretionDialogPatch = bytecodePatch(
    REMOVE_VIEWER_DISCRETION_DIALOG.patchName,
    REMOVE_VIEWER_DISCRETION_DIALOG.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        baseViewerDiscretionDialogPatch(GENERAL_CLASS_DESCRIPTOR),
        settingsPatch,
    )

    apply {
        addSwitchPreference(
            CategoryType.GENERAL,
            "revanced_remove_viewer_discretion_dialog",
            "false"
        )

        updatePatchStatus(REMOVE_VIEWER_DISCRETION_DIALOG)

    }
}
