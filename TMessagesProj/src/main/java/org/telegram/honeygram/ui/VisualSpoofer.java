package org.telegram.honeygram.ui;

import org.telegram.honeygram.HoneyConfig;
import org.telegram.tgnet.TLRPC;

/**
 * VisualSpoofer - Manages local visual spoofs (Visual Premium, Visual Stars).
 */
public class VisualSpoofer {

    /**
     * Spoofs the User's Premium status locally for UI rendering.
     */
    public static boolean isPremiumUser(TLRPC.User user, boolean realPremium) {
        if (realPremium) {
            return true;
        }
        if (user != null && user.self && HoneyConfig.isVisualPremium()) {
            return true;
        }
        return false;
    }

    /**
     * Spoofs the Telegram Stars balance locally for UI rendering.
     */
    public static long getStarsBalance(long realBalance) {
        int spoofCount = HoneyConfig.getVisualStarsCount();
        if (spoofCount > 0) {
            return spoofCount;
        }
        return realBalance;
    }
}
