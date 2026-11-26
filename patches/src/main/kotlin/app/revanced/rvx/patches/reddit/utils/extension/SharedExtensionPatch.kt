package app.revanced.rvx.patches.reddit.utils.extension

import app.revanced.rvx.patches.reddit.utils.extension.hooks.applicationInitHook
import app.revanced.rvx.patches.shared.extension.sharedExtensionPatch

val sharedExtensionPatch = sharedExtensionPatch(applicationInitHook)
