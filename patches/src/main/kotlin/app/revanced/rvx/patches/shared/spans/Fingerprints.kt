package app.revanced.rvx.patches.shared.spans

import app.revanced.rvx.util.fingerprint.legacyFingerprint

internal val customCharacterStyleFingerprint = legacyFingerprint(
    name = "customCharacterStyleFingerprint",
    returnType = "Landroid/graphics/Path;",
    parameters = listOf("Landroid/text/Layout;"),
)

