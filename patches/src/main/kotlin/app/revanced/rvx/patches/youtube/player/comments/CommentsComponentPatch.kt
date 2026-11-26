package app.revanced.rvx.patches.youtube.player.comments

import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.getInstruction
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.rvx.patches.shared.comments.commentsPanelPatch
import app.revanced.rvx.patches.shared.litho.addLithoFilter
import app.revanced.rvx.patches.shared.litho.lithoFilterPatch
import app.revanced.rvx.patches.shared.spans.addSpanFilter
import app.revanced.rvx.patches.shared.spans.inclusiveSpanPatch
import app.revanced.rvx.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.rvx.patches.youtube.utils.componentlist.hookElementList
import app.revanced.rvx.patches.youtube.utils.componentlist.lazilyConvertedElementHookPatch
import app.revanced.rvx.patches.youtube.utils.extension.Constants.COMPONENTS_PATH
import app.revanced.rvx.patches.youtube.utils.extension.Constants.PLAYER_CLASS_DESCRIPTOR
import app.revanced.rvx.patches.youtube.utils.extension.Constants.SPANS_PATH
import app.revanced.rvx.patches.youtube.utils.fix.litho.lithoLayoutPatch
import app.revanced.rvx.patches.youtube.utils.patch.PatchList.HIDE_COMMENTS_COMPONENTS
import app.revanced.rvx.patches.youtube.utils.playertype.playerTypeHookPatch
import app.revanced.rvx.patches.youtube.utils.resourceid.sharedResourceIdPatch
import app.revanced.rvx.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.revanced.rvx.patches.youtube.utils.settings.settingsPatch
import app.revanced.rvx.util.fingerprint.methodOrThrow
import app.revanced.rvx.util.getWalkerMethod
import app.revanced.rvx.util.indexOfFirstInstructionOrThrow
import app.revanced.rvx.util.indexOfFirstLiteralInstructionOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

private const val COMMENTS_FILTER_CLASS_DESCRIPTOR =
    "$COMPONENTS_PATH/CommentsFilter;"
private const val SEARCH_LINKS_FILTER_CLASS_DESCRIPTOR =
    "$SPANS_PATH/SearchLinksFilter;"

@Suppress("unused")
val commentsComponentPatch = bytecodePatch(
    HIDE_COMMENTS_COMPONENTS.patchName,
    HIDE_COMMENTS_COMPONENTS.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        settingsPatch,
        inclusiveSpanPatch,
        lithoFilterPatch,
        lithoLayoutPatch,
        lazilyConvertedElementHookPatch,
        playerTypeHookPatch,
        sharedResourceIdPatch,
        commentsPanelPatch,
    )

    apply {

        // region patch for emoji picker button in shorts

        shortsLiveStreamEmojiPickerOpacityFingerprint.methodOrThrow().apply {
            val insertIndex = implementation!!.instructions.lastIndex
            val insertRegister = getInstruction<OneRegisterInstruction>(insertIndex).registerA

            addInstruction(
                insertIndex,
                "invoke-static {v$insertRegister}, $PLAYER_CLASS_DESCRIPTOR->changeEmojiPickerOpacity(Landroid/widget/ImageView;)V"
            )
        }

        shortsLiveStreamEmojiPickerOnClickListenerFingerprint.methodOrThrow().apply {
            val emojiPickerEndpointIndex =
                indexOfFirstLiteralInstructionOrThrow(126326492L)
            val emojiPickerOnClickListenerIndex =
                indexOfFirstInstructionOrThrow(emojiPickerEndpointIndex, Opcode.INVOKE_DIRECT)
            val emojiPickerOnClickListenerMethod =
                getWalkerMethod(emojiPickerOnClickListenerIndex)

            emojiPickerOnClickListenerMethod.apply {
                val insertIndex = indexOfFirstInstructionOrThrow(Opcode.IF_EQZ)
                val insertRegister =
                    getInstruction<OneRegisterInstruction>(insertIndex).registerA

                addInstructions(
                    insertIndex, """
                        invoke-static {v$insertRegister}, $PLAYER_CLASS_DESCRIPTOR->disableEmojiPickerOnClickListener(Ljava/lang/Object;)Ljava/lang/Object;
                        move-result-object v$insertRegister
                        """
                )
            }
        }

        // endregion

        addSpanFilter(SEARCH_LINKS_FILTER_CLASS_DESCRIPTOR)
        addLithoFilter(COMMENTS_FILTER_CLASS_DESCRIPTOR)
        hookElementList("$PLAYER_CLASS_DESCRIPTOR->sanitizeCommentsCategoryBar")

        // region add settings

        addPreference(
            arrayOf(
                "PREFERENCE_SCREEN: PLAYER",
                "SETTINGS: HIDE_COMMENTS_COMPONENTS"
            ),
            HIDE_COMMENTS_COMPONENTS
        )

        // endregion

    }
}
