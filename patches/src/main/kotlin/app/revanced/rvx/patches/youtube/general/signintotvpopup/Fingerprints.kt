package app.revanced.rvx.patches.youtube.general.signintotvpopup

import app.revanced.rvx.patches.youtube.utils.resourceid.mdxSeamlessTVSignInDrawerFragmentTitle
import app.revanced.rvx.util.fingerprint.legacyFingerprint
import app.revanced.rvx.util.or
import com.android.tools.smali.dexlib2.AccessFlags

internal val signInToTvPopupFingerprint = legacyFingerprint(
    name = "signInToTvPopupFingerprint",
    returnType = "Z",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    literals = listOf(mdxSeamlessTVSignInDrawerFragmentTitle),
    customFingerprint = { method, _ ->
        method.parameterTypes.firstOrNull() == "Ljava/lang/String;"
    }
)
