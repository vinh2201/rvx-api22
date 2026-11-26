package app.revanced.rvx.patches.youtube.layout.playerbuttonbg

import app.revanced.patcher.patch.resourcePatch
import app.revanced.rvx.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.rvx.patches.youtube.utils.patch.PatchList.FORCE_HIDE_PLAYER_BUTTONS_BACKGROUND
import app.revanced.rvx.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.revanced.rvx.patches.youtube.utils.settings.settingsPatch
import app.revanced.rvx.util.doRecursively
import org.w3c.dom.Element

@Suppress("unused")
val playerButtonBackgroundPatch = resourcePatch(
    FORCE_HIDE_PLAYER_BUTTONS_BACKGROUND.patchName,
    FORCE_HIDE_PLAYER_BUTTONS_BACKGROUND.summary,
    false,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(settingsPatch)

    apply {
        document("res/drawable/player_button_circle_background.xml").use { document ->

            document.doRecursively node@{ node ->
                if (node !is Element) return@node

                node.getAttributeNode("android:color")?.let { attribute ->
                    attribute.textContent = "@android:color/transparent"
                }
            }
        }

        addPreference(FORCE_HIDE_PLAYER_BUTTONS_BACKGROUND)

    }
}
