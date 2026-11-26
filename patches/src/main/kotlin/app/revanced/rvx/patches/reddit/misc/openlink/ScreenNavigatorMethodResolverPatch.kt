package app.revanced.rvx.patches.reddit.misc.openlink

import app.revanced.patcher.patch.bytecodePatch
import app.revanced.com.android.tools.smali.dexlib2.mutable.MutableMethod
import app.revanced.rvx.util.fingerprint.methodOrThrow
import app.revanced.rvx.util.getWalkerMethod

lateinit var screenNavigatorMethod: MutableMethod

val screenNavigatorMethodResolverPatch = bytecodePatch(
    description = "screenNavigatorMethodResolverPatch"
) {
    apply {
        screenNavigatorMethod =
                // ~ Reddit 2024.25.3
            screenNavigatorFingerprint.second.methodOrNull
                    // Reddit 2024.26.1 ~
                ?: with(customReportsFingerprint.methodOrThrow()) {
                    getWalkerMethod(indexOfScreenNavigatorInstruction(this))
                }
    }
}
