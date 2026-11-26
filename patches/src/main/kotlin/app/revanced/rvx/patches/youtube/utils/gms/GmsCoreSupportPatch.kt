package app.revanced.rvx.patches.youtube.utils.gms

import app.revanced.patcher.patch.Option
import app.revanced.rvx.patches.shared.gms.gmsCoreSupportPatch
import app.revanced.rvx.patches.shared.spoof.useragent.baseSpoofUserAgentPatch
import app.revanced.rvx.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.rvx.patches.youtube.utils.compatibility.Constants.YOUTUBE_PACKAGE_NAME
import app.revanced.rvx.patches.youtube.utils.extension.sharedExtensionPatch
import app.revanced.rvx.patches.youtube.utils.fix.streamingdata.spoofStreamingDataPatch
import app.revanced.rvx.patches.youtube.utils.mainactivity.mainActivityFingerprint
import app.revanced.rvx.patches.youtube.utils.patch.PatchList.GMSCORE_SUPPORT
import app.revanced.rvx.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.revanced.rvx.patches.youtube.utils.settings.ResourceUtils.updateGmsCorePackageName
import app.revanced.rvx.patches.youtube.utils.settings.ResourceUtils.updatePackageName
import app.revanced.rvx.patches.youtube.utils.settings.settingsPatch
import app.revanced.rvx.util.valueOrThrow

@Suppress("unused")
val gmsCoreSupportPatch = gmsCoreSupportPatch(
    fromPackageName = YOUTUBE_PACKAGE_NAME,
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
    fromPackageName = YOUTUBE_PACKAGE_NAME,
    spoofedPackageSignature = "24bb24c05e47e0aefa68a58a766179d9b613a600",
    gmsCoreVendorGroupIdOption = gmsCoreVendorGroupIdOption,
    packageNameYouTubeOption = packageNameYouTubeOption,
    packageNameYouTubeMusicOption = packageNameYouTubeMusicOption,
    executeBlock = {
        updatePackageName(
            packageNameYouTubeOption.valueOrThrow()
        )
        updateGmsCorePackageName(
            "app.revanced",
            gmsCoreVendorGroupIdOption.valueOrThrow()
        )
        addPreference(
            arrayOf(
                "PREFERENCE: GMS_CORE_SETTINGS"
            ),
            GMSCORE_SUPPORT
        )
    },
) {
    dependsOn(
        baseSpoofUserAgentPatch(YOUTUBE_PACKAGE_NAME),
        spoofStreamingDataPatch,
        settingsPatch,
        accountCredentialsInvalidTextPatch,
    )
}
