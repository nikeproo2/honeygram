package org.telegram.honeygram.tools;

import org.telegram.honeygram.HoneyConfig;
import org.telegram.messenger.FileLog;

/**
 * PanicPinManager - Handles decoy PINs to hide private accounts and custom servers on demand.
 */
public class PanicPinManager {

    /**
     * Evaluates the entered passcode on app unlock.
     * If it matches the configured Panic PIN, triggers panic mode (hides secondary accounts).
     */
    public static boolean checkPasscode(String enteredCode) {
        String panicPin = HoneyConfig.getPanicPin();
        if (!panicPin.isEmpty() && panicPin.equals(enteredCode)) {
            FileLog.d("HoneyGram Security: Panic PIN entered! Activating stealth mode.");
            HoneyConfig.setPanicModeTriggered(true);
            return true; // Unlocks into decoy mode
        }
        HoneyConfig.setPanicModeTriggered(false);
        return false;
    }

    /**
     * Determines if a specific account index should be hidden from UI.
     */
    public static boolean isAccountVisible(int accountIndex) {
        if (HoneyConfig.isPanicModeTriggered()) {
            // Only account 0 (decoy primary account) is visible in panic mode
            return accountIndex == 0;
        }
        return true;
    }
}
