package app.revanced.rvx.patches.reddit.layout.premiumicon

import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.rvx.patches.reddit.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.rvx.patches.reddit.utils.fix.signature.spoofSignaturePatch
import app.revanced.rvx.patches.reddit.utils.patch.PatchList.PREMIUM_ICON
import app.revanced.rvx.patches.reddit.utils.settings.updatePatchStatus
import app.revanced.rvx.util.fingerprint.methodOrThrow

@Suppress("unused")
val premiumIconPatch = bytecodePatch(
    PREMIUM_ICON.patchName,
    PREMIUM_ICON.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(spoofSignaturePatch)

    apply {
        premiumIconFingerprint.methodOrThrow().addInstructions(
            0, """
                const/4 v0, 0x1
                return v0
                """
        )

        updatePatchStatus(PREMIUM_ICON)
    }
}
