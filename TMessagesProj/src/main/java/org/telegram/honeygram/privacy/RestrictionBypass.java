package org.telegram.honeygram.privacy;

import android.view.Window;
import android.view.WindowManager;
import org.telegram.honeygram.HoneyConfig;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.TLRPC;

/**
 * RestrictionBypass - Bypasses restrictions on forward-protected channels, screenshots, and ephemeral media.
 */
public class RestrictionBypass {

    /**
     * Overrides TLRPC.Chat and Message `noforwards` restriction flags.
     */
    public static boolean canSaveMedia(TLRPC.Chat chat, TLRPC.Message message) {
        if (HoneyConfig.isBypassRestrictedContent()) {
            return true; // Always allow saving media & forwarding
        }
        if (chat != null && chat.noforwards) {
            return false;
        }
        return message == null || !message.noforwards;
    }

    /**
     * Prevents FLAG_SECURE from being applied to Android Window, enabling screenshots everywhere.
     */
    public static void applyScreenshotPolicy(Window window) {
        if (window == null) return;
        try {
            if (HoneyConfig.isAllowScreenshots()) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /**
     * Bypasses disappearing/ephemeral photo & video countdown timer.
     */
    public static int getEffectiveTtl(int originalTtl) {
        if (HoneyConfig.isBypassDisappearingTimer()) {
            return 0; // Infinite view time
        }
        return originalTtl;
    }
}
