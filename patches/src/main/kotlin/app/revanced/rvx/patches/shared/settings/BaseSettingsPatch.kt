package app.revanced.rvx.patches.shared.settings

import app.revanced.patcher.patch.bytecodePatch
import app.revanced.rvx.patches.shared.extension.Constants.EXTENSION_THEME_UTILS_CLASS_DESCRIPTOR
import app.revanced.rvx.util.findMethodsOrThrow
import app.revanced.rvx.util.returnEarly

private const val THEME_FOREGROUND_COLOR = "@color/yt_white1"
private const val THEME_BACKGROUND_COLOR = "@color/yt_black3"

val baseSettingsPatch = bytecodePatch(
    description = "baseSettingsPatch"
) {
    apply {
        findMethodsOrThrow(EXTENSION_THEME_UTILS_CLASS_DESCRIPTOR).apply {
            find { method -> method.name == "getThemeLightColorResourceName" }
                ?.returnEarly(THEME_FOREGROUND_COLOR)
            find { method -> method.name == "getThemeDarkColorResourceName" }
                ?.returnEarly(THEME_BACKGROUND_COLOR)
        }
    }
}


