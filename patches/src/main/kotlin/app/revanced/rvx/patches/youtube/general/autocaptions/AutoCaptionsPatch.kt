package app.revanced.rvx.patches.youtube.general.autocaptions

import app.revanced.patcher.patch.bytecodePatch
import app.revanced.rvx.patches.shared.captions.baseAutoCaptionsPatch
import app.revanced.rvx.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.rvx.patches.youtube.utils.patch.PatchList.DISABLE_FORCED_AUTO_CAPTIONS
import app.revanced.rvx.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.revanced.rvx.patches.youtube.utils.settings.settingsPatch

@Suppress("unused")
val autoCaptionsPatch = bytecodePatch(
    DISABLE_FORCED_AUTO_CAPTIONS.patchName,
    DISABLE_FORCED_AUTO_CAPTIONS.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        baseAutoCaptionsPatch,
        settingsPatch,
    )

    apply {

        // region add settings

        addPreference(
            arrayOf(
                "PREFERENCE_SCREEN: GENERAL",
                "SETTINGS: DISABLE_AUTO_CAPTIONS"
            ),
            DISABLE_FORCED_AUTO_CAPTIONS
        )

        // endregion

    }
}
