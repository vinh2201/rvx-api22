package app.revanced.rvx.patches.youtube.utils.toolbar

import app.revanced.rvx.patches.youtube.utils.extension.Constants.UTILS_PATH
import app.revanced.rvx.util.fingerprint.legacyFingerprint
import app.revanced.rvx.util.or
import com.android.tools.smali.dexlib2.AccessFlags

internal val toolBarPatchFingerprint = legacyFingerprint(
    name = "toolBarPatchFingerprint",
    accessFlags = AccessFlags.PRIVATE or AccessFlags.STATIC,
    customFingerprint = { method, _ ->
        method.definingClass == "$UTILS_PATH/ToolBarPatch;"
                && method.name == "hookToolBar"
    }
)


