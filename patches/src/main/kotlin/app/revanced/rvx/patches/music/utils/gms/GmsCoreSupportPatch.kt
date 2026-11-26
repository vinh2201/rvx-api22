package app.revanced.rvx.patches.music.utils.gms

import app.revanced.patcher.patch.Option
import app.revanced.rvx.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.rvx.patches.music.utils.compatibility.Constants.YOUTUBE_MUSIC_PACKAGE_NAME
import app.revanced.rvx.patches.music.utils.extension.sharedExtensionPatch
import app.revanced.rvx.patches.music.utils.fix.fileprovider.fileProviderPatch
import app.revanced.rvx.patches.music.utils.fix.streamingdata.spoofStreamingDataPatch
import app.revanced.rvx.patches.music.utils.mainactivity.mainActivityFingerprint
import app.revanced.rvx.patches.music.utils.patch.PatchList.GMSCORE_SUPPORT
import app.revanced.rvx.patches.music.utils.settings.ResourceUtils.updatePackageName
import app.revanced.rvx.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.revanced.rvx.patches.music.utils.settings.settingsPatch
import app.revanced.rvx.patches.shared.gms.gmsCoreSupportPatch
import app.revanced.rvx.patches.shared.spoof.useragent.baseSpoofUserAgentPatch
import app.revanced.rvx.util.valueOrThrow

@Suppress("unused")
val gmsCoreSupportPatch = gmsCoreSupportPatch(
    fromPackageName = YOUTUBE_MUSIC_PACKAGE_NAME,
    mainActivityOnCreateFingerprint = mainActivityFingerprint.second,
    extensionPatch = sharedExtensionPatch,
    gmsCoreSupportResourcePatchFactory = ::gmsCoreSupportResourcePatch,
) {
    compatibleWith(COMPATIBLE_PACKAGE)
}

private fun gmsCoreSupportResourcePatch(
    gmsCoreVendorGroupIdOption: Option<String>,
    packageNameYouTubeOption: Option<String>,
    packageNameYouTubeMusicOption: Option<String>,
) = app.revanced.rvx.patches.shared.gms.gmsCoreSupportResourcePatch(
    fromPackageName = YOUTUBE_MUSIC_PACKAGE_NAME,
    spoofedPackageSignature = "afb0fed5eeaebdd86f56a97742f4b6b33ef59875",
    gmsCoreVendorGroupIdOption = gmsCoreVendorGroupIdOption,
    packageNameYouTubeOption = packageNameYouTubeOption,
    packageNameYouTubeMusicOption = packageNameYouTubeMusicOption,
    executeBlock = {
        updatePackageName(
            gmsCoreVendorGroupIdOption.valueOrThrow() + ".android.gms",
            packageNameYouTubeMusicOption.valueOrThrow()
        )

        updatePatchStatus(GMSCORE_SUPPORT)

    },
) {
    dependsOn(
        baseSpoofUserAgentPatch(YOUTUBE_MUSIC_PACKAGE_NAME),
        spoofStreamingDataPatch,
        settingsPatch,
        fileProviderPatch(
            packageNameYouTubeOption.valueOrThrow(),
            packageNameYouTubeMusicOption.valueOrThrow()
        ),
    )
}
