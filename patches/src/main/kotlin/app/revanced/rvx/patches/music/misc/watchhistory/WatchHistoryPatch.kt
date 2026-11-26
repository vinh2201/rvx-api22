package app.revanced.rvx.patches.music.misc.watchhistory

import app.revanced.patcher.patch.bytecodePatch
import app.revanced.rvx.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.rvx.patches.music.utils.patch.PatchList.WATCH_HISTORY
import app.revanced.rvx.patches.music.utils.settings.CategoryType
import app.revanced.rvx.patches.music.utils.settings.addPreferenceWithIntent
import app.revanced.rvx.patches.music.utils.settings.settingsPatch
import app.revanced.rvx.patches.shared.trackingurlhook.hookWatchHistory
import app.revanced.rvx.patches.shared.trackingurlhook.trackingUrlHookPatch

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

        addPreferenceWithIntent(
            CategoryType.MISC,
            "revanced_watch_history_type"
        )
    }

}