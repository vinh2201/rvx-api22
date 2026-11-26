package app.revanced.rvx.patches.music.utils.webview

import app.revanced.rvx.util.fingerprint.legacyFingerprint

internal val carAppPermissionActivityOnCreateFingerprint = legacyFingerprint(
    name = "carAppPermissionActivityOnCreateFingerprint",
    returnType = "V",
    parameters = listOf("Landroid/os/Bundle;"),
    customFingerprint = { method, classDef ->
        classDef.type == "Landroidx/car/app/CarAppPermissionActivity;"
                && method.name == "onCreate"
    }
)
