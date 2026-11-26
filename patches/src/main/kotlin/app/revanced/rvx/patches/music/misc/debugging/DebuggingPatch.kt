package app.revanced.rvx.patches.music.misc.debugging

import app.revanced.patcher.patch.resourcePatch
import app.revanced.rvx.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.rvx.patches.music.utils.patch.PatchList.ENABLE_DEBUG_LOGGING
import app.revanced.rvx.patches.music.utils.settings.CategoryType
import app.revanced.rvx.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.revanced.rvx.patches.music.utils.settings.addSwitchPreference
import app.revanced.rvx.patches.music.utils.settings.settingsPatch

@Suppress("unused")
val debuggingPatch = resourcePatch(
    ENABLE_DEBUG_LOGGING.patchName,
    ENABLE_DEBUG_LOGGING.summary,
    // Unlike YouTube, YouTube Music's Litho components do not change very often.
    // That's why it seems better to selectively include patches for only those users who need them.
    false,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(settingsPatch)

    apply {
        addSwitchPreference(
            CategoryType.MISC,
            "revanced_debug",
            "false"
        )
        addSwitchPreference(
            CategoryType.MISC,
            "revanced_debug_protobuffer",
            "false",
            "revanced_debug"
        )
        addSwitchPreference(
            CategoryType.MISC,
            "revanced_debug_spannable",
            "false",
            "revanced_debug"
        )

        updatePatchStatus(ENABLE_DEBUG_LOGGING)

    }
}
