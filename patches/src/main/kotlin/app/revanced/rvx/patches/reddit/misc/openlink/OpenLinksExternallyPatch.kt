package app.revanced.rvx.patches.reddit.misc.openlink

import app.revanced.patcher.extensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.getInstruction
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patcher.extensions.ExternalLabel
import app.revanced.rvx.patches.reddit.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.rvx.patches.reddit.utils.extension.Constants.PATCHES_PATH
import app.revanced.rvx.patches.reddit.utils.patch.PatchList.OPEN_LINKS_EXTERNALLY
import app.revanced.rvx.patches.reddit.utils.settings.settingsPatch
import app.revanced.rvx.patches.reddit.utils.settings.updatePatchStatus
import app.revanced.rvx.util.indexOfFirstStringInstructionOrThrow

private const val EXTENSION_METHOD_DESCRIPTOR =
    "$PATCHES_PATH/OpenLinksExternallyPatch;" +
            "->" +
            "openLinksExternally(Landroid/app/Activity;Landroid/net/Uri;)Z"

@Suppress("unused")
val openLinksExternallyPatch = bytecodePatch(
    OPEN_LINKS_EXTERNALLY.patchName,
    OPEN_LINKS_EXTERNALLY.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        settingsPatch,
        screenNavigatorMethodResolverPatch
    )

    apply {
        screenNavigatorMethod.apply {
            val insertIndex = indexOfFirstStringInstructionOrThrow("uri") + 2

            addInstructionsWithLabels(
                insertIndex, """
                    invoke-static {p1, p2}, $EXTENSION_METHOD_DESCRIPTOR
                    move-result v0
                    if-eqz v0, :dismiss
                    return-void
                    """, ExternalLabel("dismiss", getInstruction(insertIndex))
            )
        }

        updatePatchStatus(
            "enableOpenLinksExternally",
            OPEN_LINKS_EXTERNALLY
        )
    }
}
