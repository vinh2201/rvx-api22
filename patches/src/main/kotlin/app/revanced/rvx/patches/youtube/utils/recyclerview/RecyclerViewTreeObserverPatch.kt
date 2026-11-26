package app.revanced.rvx.patches.youtube.utils.recyclerview

import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.com.android.tools.smali.dexlib2.mutable.MutableMethod
import app.revanced.rvx.util.fingerprint.injectLiteralInstructionBooleanCall
import app.revanced.rvx.util.fingerprint.methodOrThrow
import app.revanced.rvx.util.getReference
import app.revanced.rvx.util.indexOfFirstInstructionOrThrow
import app.revanced.rvx.util.indexOfFirstInstructionReversedOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

private lateinit var recyclerViewTreeObserverMutableMethod: MutableMethod
private var recyclerViewTreeObserverInsertIndex = 0

val recyclerViewTreeObserverPatch = bytecodePatch(
    description = "recyclerViewTreeObserverPatch"
) {
    apply {
        /**
         * If this value is false, RecyclerViewTreeObserver is not initialized.
         * This value is usually true so this patch is not strictly necessary,
         * But in very rare cases this value may be false.
         * Therefore, we need to force this to be true.
         */
        recyclerViewBuilderFingerprint.injectLiteralInstructionBooleanCall(
            RECYCLER_VIEW_BUILDER_FEATURE_FLAG,
            "0x1"
        )

        recyclerViewTreeObserverFingerprint.methodOrThrow().apply {
            recyclerViewTreeObserverMutableMethod = this

            val onDrawListenerIndex = indexOfFirstInstructionOrThrow {
                opcode == Opcode.IPUT_OBJECT &&
                        getReference<FieldReference>()?.type == "Landroid/view/ViewTreeObserver${'$'}OnDrawListener;"
            }
            recyclerViewTreeObserverInsertIndex =
                indexOfFirstInstructionReversedOrThrow(onDrawListenerIndex, Opcode.CHECK_CAST) + 1
        }
    }
}

fun recyclerViewTreeObserverHook(descriptor: String) =
    recyclerViewTreeObserverMutableMethod.addInstruction(
        recyclerViewTreeObserverInsertIndex++,
        "invoke-static/range { p2 .. p2 }, $descriptor"
    )
