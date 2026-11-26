package app.revanced.rvx.patches.music.layout.playeroverlay

import app.revanced.patcher.patch.resourcePatch
import app.revanced.rvx.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.rvx.patches.music.utils.patch.PatchList.HIDE_PLAYER_OVERLAY_FILTER
import app.revanced.rvx.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.revanced.rvx.util.removeOverlayBackground

@Suppress("unused")
val playerOverlayFilterPatch = resourcePatch(
    HIDE_PLAYER_OVERLAY_FILTER.patchName,
    HIDE_PLAYER_OVERLAY_FILTER.summary,
    use = false,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    apply {
        removeOverlayBackground(
            arrayOf("music_controls_overlay.xml"),
            arrayOf("player_control_screen")
        )

        updatePatchStatus(HIDE_PLAYER_OVERLAY_FILTER)

    }
}

