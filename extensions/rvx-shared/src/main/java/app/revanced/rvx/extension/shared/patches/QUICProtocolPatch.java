package app.revanced.rvx.extension.shared.patches;

import app.revanced.rvx.extension.shared.settings.BaseSettings;

@SuppressWarnings("unused")
public class QUICProtocolPatch {

    public static boolean disableQUICProtocol(boolean original) {
        return !BaseSettings.DISABLE_QUIC_PROTOCOL.get() && original;
    }
}
