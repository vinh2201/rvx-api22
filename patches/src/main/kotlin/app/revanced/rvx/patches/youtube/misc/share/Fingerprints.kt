package app.revanced.rvx.patches.youtube.misc.share

import app.revanced.rvx.util.fingerprint.legacyFingerprint
import app.revanced.rvx.util.getReference
import app.revanced.rvx.util.indexOfFirstInstruction
import app.revanced.rvx.util.or
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

internal val queryIntentListFingerprint = legacyFingerprint(
    name = "queryIntentListFingerprint",
    returnType = "Ljava/util/List;",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.STATIC,
    parameters = listOf("Landroid/content/pm/PackageManager;"),
    customFingerprint = { method, _ ->
        method.indexOfFirstInstruction {
            opcode == Opcode.INVOKE_VIRTUAL &&
                    getReference<MethodReference>()?.name == "queryIntentActivities"
        } >= 0
    }
)
