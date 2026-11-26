package app.revanced.rvx.patches.music.utils.flyoutmenu

import app.revanced.patcher.patch.bytecodePatch
import app.revanced.rvx.patches.music.utils.extension.Constants.EXTENSION_PATH
import app.revanced.rvx.patches.music.utils.extension.sharedExtensionPatch
import app.revanced.rvx.patches.music.utils.playbackRateBottomSheetClassFingerprint
import app.revanced.rvx.patches.music.utils.resourceid.sharedResourceIdPatch
import app.revanced.rvx.util.addStaticFieldToExtension
import app.revanced.rvx.util.fingerprint.methodOrThrow

private const val EXTENSION_VIDEO_UTILS_CLASS_DESCRIPTOR =
    "$EXTENSION_PATH/utils/VideoUtils;"

val flyoutMenuHookPatch = bytecodePatch(
    description = "flyoutMenuHookPatch",
) {
    dependsOn(
        sharedExtensionPatch,
        sharedResourceIdPatch,
    )

    apply {

        playbackRateBottomSheetClassFingerprint.methodOrThrow().apply {
            val smaliInstructions =
                """
                    if-eqz v0, :ignore
                    invoke-virtual {v0}, $definingClass->$name()V
                    :ignore
                    return-void
                    """

            addStaticFieldToExtension(
                EXTENSION_VIDEO_UTILS_CLASS_DESCRIPTOR,
                "showPlaybackSpeedFlyoutMenu",
                "playbackRateBottomSheetClass",
                definingClass,
                smaliInstructions
            )
        }

    }
}
