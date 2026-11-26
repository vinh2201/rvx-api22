package app.revanced.rvx.patches.youtube.general.loadingscreen

import app.revanced.patcher.patch.bytecodePatch
import app.revanced.rvx.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.rvx.patches.youtube.utils.extension.Constants.GENERAL_CLASS_DESCRIPTOR
import app.revanced.rvx.patches.youtube.utils.patch.PatchList.ENABLE_GRADIENT_LOADING_SCREEN
import app.revanced.rvx.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.revanced.rvx.patches.youtube.utils.settings.settingsPatch
import app.revanced.rvx.util.fingerprint.injectLiteralInstructionBooleanCall

@Suppress("unused")
val gradientLoadingScreenPatch = bytecodePatch(
    ENABLE_GRADIENT_LOADING_SCREEN.patchName,
    ENABLE_GRADIENT_LOADING_SCREEN.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(settingsPatch)

    apply {

        useGradientLoadingScreenFingerprint.injectLiteralInstructionBooleanCall(
            GRADIENT_LOADING_SCREEN_AB_CONSTANT,
            "$GENERAL_CLASS_DESCRIPTOR->enableGradientLoadingScreen()Z"
        )

        // region add settings

        addPreference(
            arrayOf(
                "PREFERENCE_SCREEN: GENERAL",
                "SETTINGS: ENABLE_GRADIENT_LOADING_SCREEN"
            ),
            ENABLE_GRADIENT_LOADING_SCREEN
        )

        // endregion

    }
}
