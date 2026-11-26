package app.revanced.rvx.patches.reddit.utils.extension.hooks

import app.revanced.rvx.patches.shared.extension.extensionHook

internal val applicationInitHook = extensionHook(
    customFingerprint = { method, _ ->
        method.definingClass.endsWith("/FrontpageApplication;") &&
                method.name == "onCreate"
    },
)
