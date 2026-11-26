package app.revanced.rvx.patches.reddit.layout.toolbar

import app.revanced.rvx.util.fingerprint.legacyFingerprint
import app.revanced.rvx.util.or
import com.android.tools.smali.dexlib2.AccessFlags

internal val homePagerScreenFingerprint = legacyFingerprint(
    name = "homePagerScreenFingerprint",
    returnType = "Landroid/view/View;",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("Landroid/view/LayoutInflater;", "Landroid/view/ViewGroup;"),
    strings = listOf("recapNavEntryPointDelegate"),
    customFingerprint = { method, _ ->
        method.definingClass.endsWith("/HomePagerScreen;")
    }
)

