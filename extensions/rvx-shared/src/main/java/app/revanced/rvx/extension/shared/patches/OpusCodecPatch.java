package app.revanced.rvx.extension.shared.patches;

import app.revanced.rvx.extension.shared.settings.BaseSettings;

@SuppressWarnings("unused")
public class OpusCodecPatch {

    public static boolean enableOpusCodec() {
        return BaseSettings.ENABLE_OPUS_CODEC.get();
    }
}
