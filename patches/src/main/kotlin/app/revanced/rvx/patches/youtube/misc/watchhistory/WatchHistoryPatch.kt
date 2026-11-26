package app.revanced.rvx.patches.youtube.misc.watchhistory

import app.revanced.patcher.patch.bytecodePatch
import app.revanced.rvx.patches.shared.trackingurlhook.hookWatchHistory
import app.revanced.rvx.patches.shared.trackingurlhook.trackingUrlHookPatch
import app.revanced.rvx.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.rvx.patches.youtube.utils.patch.PatchList.WATCH_HISTORY
import app.revanced.rvx.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.revanced.rvx.patches.youtube.utils.settings.settingsPatch

@Suppress("unused")
val watchHistoryPatch = bytecodePatch(
    WATCH_HISTORY.patchName,
    WATCH_HISTORY.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        settingsPatch,
        trackingUrlHookPatch,
    )

    apply {

        hookWatchHistory()

        // region add settings

        addPreference(
            arrayOf(
                "SETTINGS: WATCH_HISTORY"
            ),
            WATCH_HISTORY
        )

        // endregion

    }
}
