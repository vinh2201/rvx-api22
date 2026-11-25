package app.revanced.patches.reddit.layout.communities

import app.revanced.patcher.extensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.getInstruction
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patcher.extensions.ExternalLabel
import app.revanced.patches.reddit.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.patches.reddit.utils.extension.Constants.PATCHES_PATH
import app.revanced.patches.reddit.utils.patch.PatchList.HIDE_RECOMMENDED_COMMUNITIES_SHELF
import app.revanced.patches.reddit.utils.settings.settingsPatch
import app.revanced.patches.reddit.utils.settings.updatePatchStatus
import app.revanced.util.fingerprint.methodOrThrow

private const val EXTENSION_METHOD_DESCRIPTOR =
    "$PATCHES_PATH/RecommendedCommunitiesPatch;->hideRecommendedCommunitiesShelf()Z"

@Suppress("unused")
val recommendedCommunitiesPatch = bytecodePatch(
    HIDE_RECOMMENDED_COMMUNITIES_SHELF.patchName,
    HIDE_RECOMMENDED_COMMUNITIES_SHELF.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(settingsPatch)

    apply {
        communityRecommendationSectionFingerprint.methodOrThrow(
            communityRecommendationSectionParentFingerprint
        ).apply {
            addInstructionsWithLabels(
                0,
                """
                    invoke-static {}, $EXTENSION_METHOD_DESCRIPTOR
                    move-result v0
                    if-eqz v0, :off
                    return-void
                    """, ExternalLabel("off", getInstruction(0))
            )
        }

        updatePatchStatus(
            "enableRecommendedCommunitiesShelf",
            HIDE_RECOMMENDED_COMMUNITIES_SHELF
        )
    }
}
