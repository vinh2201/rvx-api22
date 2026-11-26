package app.revanced.rvx.patches.youtube.utils.returnyoutubeusername

import app.revanced.patcher.patch.bytecodePatch
import app.revanced.rvx.patches.shared.returnyoutubeusername.baseReturnYouTubeUsernamePatch
import app.revanced.rvx.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.rvx.patches.youtube.utils.patch.PatchList.RETURN_YOUTUBE_USERNAME
import app.revanced.rvx.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.revanced.rvx.patches.youtube.utils.settings.settingsPatch

@Suppress("unused")
val returnYouTubeUsernamePatch = bytecodePatch(
    RETURN_YOUTUBE_USERNAME.patchName,
    RETURN_YOUTUBE_USERNAME.summary,
    false,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        baseReturnYouTubeUsernamePatch,
        settingsPatch,
    )

    apply {

        // region add settings

        addPreference(
            arrayOf(
                "PREFERENCE_SCREEN: RETURN_YOUTUBE_USERNAME"
            ),
            RETURN_YOUTUBE_USERNAME
        )

        // endregion
    }
}
