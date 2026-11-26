package app.revanced.rvx.patches.music.layout.translations

import app.revanced.patcher.patch.resourcePatch
import app.revanced.patcher.patch.stringOption
import app.revanced.rvx.patches.music.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.rvx.patches.music.utils.patch.PatchList.TRANSLATIONS_FOR_YOUTUBE_MUSIC
import app.revanced.rvx.patches.music.utils.settings.ResourceUtils.updatePatchStatus
import app.revanced.rvx.patches.music.utils.settings.settingsPatch
import app.revanced.rvx.patches.shared.translations.APP_LANGUAGES
import app.revanced.rvx.patches.shared.translations.baseTranslationsPatch

// Array of supported translations, each represented by its language code.
private val SUPPORTED_TRANSLATIONS = setOf(
    "el-rGR", "es-rES", "fr-rFR", "hu-rHU", "id-rID", "in", "it-rIT",
    "ja-rJP", "ko-rKR", "pl-rPL", "pt-rBR", "ru-rRU", "tr-rTR", "uk-rUA",
    "vi-rVN", "zh-rCN", "zh-rTW"
)

@Suppress("unused")
val translationsPatch = resourcePatch(
    TRANSLATIONS_FOR_YOUTUBE_MUSIC.patchName,
    TRANSLATIONS_FOR_YOUTUBE_MUSIC.summary,
) {
    val customTranslations by stringOption(
        default = "",
        name = "Custom translations",
        description = """
            The path to the 'strings.xml' file.
            Please note that applying the 'strings.xml' file will overwrite all existing translations.
            """.trimIndent(),
        required = true)

    val selectedTranslations by stringOption(
        default = SUPPORTED_TRANSLATIONS.joinToString(", "),
        name = "Translations to add",
        description = "A list of translations to be added for the RVX settings, separated by commas.",
        required = true)

    val selectedStringResources by stringOption(
        default = APP_LANGUAGES.joinToString(", "),
        name = "String resources to keep",
        description = """
            A list of string resources to be kept, separated by commas.
            String resources not in the list will be removed from the app.

            Default string resource, English, is not removed.
            """.trimIndent(),
        required = true)

    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        settingsPatch,
    )

    apply {
        baseTranslationsPatch(
            customTranslations, selectedTranslations, selectedStringResources,
            SUPPORTED_TRANSLATIONS, "music"
        )

        updatePatchStatus(TRANSLATIONS_FOR_YOUTUBE_MUSIC)

    }
}
