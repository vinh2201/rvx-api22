package app.revanced.rvx.patches.youtube.utils.bottomsheet

import app.revanced.rvx.patches.youtube.utils.resourceid.designBottomSheet
import app.revanced.rvx.util.fingerprint.legacyFingerprint

internal val bottomSheetBehaviorFingerprint = legacyFingerprint(
    name = "bottomSheetBehaviorFingerprint",
    returnType = "V",
    literals = listOf(designBottomSheet),
)
