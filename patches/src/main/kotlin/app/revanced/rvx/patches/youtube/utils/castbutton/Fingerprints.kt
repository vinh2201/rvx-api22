package app.revanced.rvx.patches.youtube.utils.castbutton

import app.revanced.rvx.patches.youtube.utils.resourceid.castMediaRouteButton
import app.revanced.rvx.util.fingerprint.legacyFingerprint
import app.revanced.rvx.util.getReference
import app.revanced.rvx.util.indexOfFirstInstruction
import app.revanced.rvx.util.or
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

internal val menuItemInitializeFingerprint = legacyFingerprint(
    name = "menuItemInitializeFingerprint",
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("Landroid/view/MenuItem;"),
    literals = listOf(castMediaRouteButton),
)

internal val menuItemVisibilityFingerprint = legacyFingerprint(
    name = "menuItemVisibilityFingerprint",
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("Z"),
    customFingerprint = { method, _ ->
        method.indexOfFirstInstruction {
            getReference<MethodReference>()?.name == "setVisible"
        } >= 0
    }
)

internal val playerButtonFingerprint = legacyFingerprint(
    name = "playerButtonFingerprint",
    returnType = "V",
    accessFlags = AccessFlags.PRIVATE or AccessFlags.FINAL,
    parameters = emptyList(),
    literals = listOf(11208L),
)
