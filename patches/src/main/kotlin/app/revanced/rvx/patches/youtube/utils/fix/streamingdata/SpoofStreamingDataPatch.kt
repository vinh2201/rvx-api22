package app.revanced.rvx.patches.youtube.utils.fix.streamingdata

import app.revanced.rvx.patches.shared.spoof.streamingdata.EXTENSION_CLASS_DESCRIPTOR
import app.revanced.rvx.patches.shared.spoof.streamingdata.EXTENSION_RELOAD_VIDEO_BUTTON_CLASS_DESCRIPTOR
import app.revanced.rvx.patches.shared.spoof.streamingdata.spoofStreamingDataPatch
import app.revanced.rvx.patches.shared.spoof.useragent.baseSpoofUserAgentPatch
import app.revanced.rvx.patches.youtube.player.overlaybuttons.overlayButtonsIconTypeOption
import app.revanced.rvx.patches.youtube.utils.compatibility.Constants.YOUTUBE_PACKAGE_NAME
import app.revanced.rvx.patches.youtube.utils.dismiss.dismissPlayerHookPatch
import app.revanced.rvx.patches.youtube.utils.playercontrols.addTopControl
import app.revanced.rvx.patches.youtube.utils.playercontrols.injectControl
import app.revanced.rvx.patches.youtube.utils.playercontrols.playerControlsPatch
import app.revanced.rvx.patches.youtube.utils.playservice.is_19_34_or_greater
import app.revanced.rvx.patches.youtube.utils.playservice.is_19_50_or_greater
import app.revanced.rvx.patches.youtube.utils.playservice.is_20_10_or_greater
import app.revanced.rvx.patches.youtube.utils.playservice.is_20_14_or_greater
import app.revanced.rvx.patches.youtube.utils.playservice.versionCheckPatch
import app.revanced.rvx.patches.youtube.utils.request.buildRequestPatch
import app.revanced.rvx.patches.youtube.utils.request.hookBuildRequest
import app.revanced.rvx.patches.youtube.utils.request.hookBuildRequestUrl
import app.revanced.rvx.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.revanced.rvx.patches.youtube.utils.settings.settingsPatch
import app.revanced.rvx.patches.youtube.video.information.videoInformationPatch
import app.revanced.rvx.patches.youtube.video.playerresponse.Hook
import app.revanced.rvx.patches.youtube.video.playerresponse.addPlayerResponseMethodHook
import app.revanced.rvx.patches.youtube.video.videoid.videoIdPatch
import app.revanced.rvx.util.lowerCaseOrThrow

val spoofStreamingDataPatch = spoofStreamingDataPatch(
    block = {
        dependsOn(
            settingsPatch,
            versionCheckPatch,
            baseSpoofUserAgentPatch(YOUTUBE_PACKAGE_NAME),
            buildRequestPatch,
            playerControlsPatch,
            videoIdPatch,
            videoInformationPatch,
            dismissPlayerHookPatch,
        )
    },
    isYouTube = {
        true
    },
    outlineIcon = {
        val iconType = overlayButtonsIconTypeOption
            .lowerCaseOrThrow()
        iconType == "thin"
    },
    fixMediaFetchHotConfigChanges = {
        is_19_34_or_greater
    },
    fixMediaFetchHotConfigAlternativeChanges = {
        // In 20.14 the flag was merged with 19.50 start playback flag.
        is_20_10_or_greater && !is_20_14_or_greater
    },
    fixParsePlaybackResponseFeatureFlag = {
        is_19_50_or_greater
    },
    executeBlock = {

        // region Get replacement streams at player requests.

        hookBuildRequest("$EXTENSION_CLASS_DESCRIPTOR->fetchStreams(Ljava/lang/String;Ljava/util/Map;)V")
        hookBuildRequestUrl("$EXTENSION_CLASS_DESCRIPTOR->blockGetAttRequest(Ljava/lang/String;)Ljava/lang/String;")

        // endregion

        addPlayerResponseMethodHook(
            Hook.PlayerParameterBeforeVideoId(
                "$EXTENSION_CLASS_DESCRIPTOR->newPlayerResponseParameter(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)Ljava/lang/String;"
            )
        )

        // region Player buttons

        injectControl(EXTENSION_RELOAD_VIDEO_BUTTON_CLASS_DESCRIPTOR)

        // endregion

        addPreference(
            arrayOf(
                "SETTINGS: SPOOF_STREAMING_DATA"
            )
        )
    },
    finalizeBlock = {
        addTopControl(
            "youtube/spoof/shared",
            "@+id/revanced_reload_video_button",
            "@+id/revanced_reload_video_button"
        )
    }
)
