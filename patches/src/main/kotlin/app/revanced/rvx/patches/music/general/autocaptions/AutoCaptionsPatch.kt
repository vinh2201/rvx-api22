package app.revanced.rvx.patches.music.general.autocaptions

import app.revanced.patcher.patch.bytecodePatch
import app.revanced.rvx.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.rvx.patches.music.utils.patch.PatchList.DISABLE_FORCED_AUTO_CAPTIONS
import app.revanced.rvx.patches.music.utils.settings.CategoryType
import app.revanced.rvx.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.revanced.rvx.patches.music.utils.settings.addSwitchPreference
import app.revanced.rvx.patches.music.utils.settings.settingsPatch
import app.revanced.rvx.patches.shared.captions.baseAutoCaptionsPatch

@Suppress("unused")
val autoCaptionsPatch = bytecodePatch(
    DISABLE_FORCED_AUTO_CAPTIONS.patchName,
    DISABLE_FORCED_AUTO_CAPTIONS.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        baseAutoCaptionsPatch,
        settingsPatch
    )

    apply {
        addSwitchPreference(
            CategoryType.GENERAL,
            "revanced_disable_auto_captions",
            "false"
        )

        updatePatchStatus(DISABLE_FORCED_AUTO_CAPTIONS)

    }
}