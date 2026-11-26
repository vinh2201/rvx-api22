package app.revanced.rvx.patches.music.utils.webview

import app.revanced.rvx.patches.shared.webview.webViewPatch
import app.revanced.rvx.patches.youtube.utils.extension.sharedExtensionPatch

val webViewPatch = webViewPatch(
    block = {
        dependsOn(sharedExtensionPatch)
    },
    targetActivityFingerprint = carAppPermissionActivityOnCreateFingerprint,
)
