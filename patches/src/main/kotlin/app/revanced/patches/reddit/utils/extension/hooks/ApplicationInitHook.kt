package app.revanced.patches.reddit.utils.extension.hooks

import app.revanced.patches.shared.extension.extensionHook

internal val applicationInitHook = extensionHook(
    customFingerprint = { method, _ ->
        method.definingClass.endsWith("/FrontpageApplication;") &&
                method.name == "onCreate"
    },
)
