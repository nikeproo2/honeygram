package org.telegram.honeygram.privacy;

import org.telegram.honeygram.HoneyConfig;
import org.telegram.messenger.FileLog;

/**
 * GhostController - Intercepts outbound telemetry to keep user state completely invisible.
 */
public class GhostController {

    /**
     * Intercepts messages_readHistory requests.
     * Returns true if the read receipt should be blocked.
     */
    public static boolean shouldBlockReadReceipt(long dialogId) {
        if (HoneyConfig.isDontSendRead()) {
            FileLog.d("HoneyGram Ghost: Blocked read receipt for dialog " + dialogId);
            return true;
        }
        return false;
    }

    /**
     * Intercepts messages_setTyping requests.
     * Returns true if typing/audio/video recording actions should be suppressed.
     */
    public static boolean shouldBlockTypingStatus(long dialogId) {
        if (HoneyConfig.isDontSendTyping()) {
            FileLog.d("HoneyGram Ghost: Blocked typing status for dialog " + dialogId);
            return true;
        }
        return false;
    }

    /**
     * Intercepts stories_readStories requests.
     * Returns true if story view tracking should be blocked.
     */
    public static boolean shouldBlockStoryView(long storyId) {
        if (HoneyConfig.isHideStoriesSeen()) {
            FileLog.d("HoneyGram Ghost: Blocked story view tracking for story " + storyId);
            return true;
        }
        return false;
    }
}
