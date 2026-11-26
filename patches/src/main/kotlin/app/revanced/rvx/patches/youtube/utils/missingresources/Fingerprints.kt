package app.revanced.rvx.patches.youtube.utils.missingresources

import app.revanced.rvx.util.fingerprint.legacyFingerprint
import app.revanced.rvx.util.or
import com.android.tools.smali.dexlib2.AccessFlags

internal val navigationBarGetDrawableFingerprint = legacyFingerprint(
    name = "getDrawableFingerprint",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.STATIC,
    returnType = "Landroid/graphics/drawable/Drawable;",
    parameters = listOf("Landroid/content/Context;", "I"),
    customFingerprint = { methodDef, classDef ->
        methodDef.name == "Y"
        // && classDef.type == "Lzv;"
    }
)
