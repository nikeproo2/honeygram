package org.telegram.honeygram.tools;

import android.content.Context;
import org.telegram.honeygram.HoneyConfig;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.NotificationsController;

import java.util.HashSet;
import java.util.Set;

/**
 * KeywordWatcher - Monitors incoming messages across all chats and triggers high-priority alerts on keyword match.
 */
public class KeywordWatcher {

    private static final String PREF_KEYWORDS = "hg_watched_keywords";
    private static final Set<String> watchedKeywords = new HashSet<>();

    public static synchronized void loadKeywords() {
        watchedKeywords.clear();
        Set<String> saved = HoneyConfig.getPrefs().getStringSet(PREF_KEYWORDS, null);
        if (saved != null) {
            watchedKeywords.addAll(saved);
        }
    }

    public static synchronized void addKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return;
        watchedKeywords.add(keyword.trim().toLowerCase());
        HoneyConfig.getPrefs().edit().putStringSet(PREF_KEYWORDS, new HashSet<>(watchedKeywords)).apply();
    }

    public static synchronized void removeKeyword(String keyword) {
        watchedKeywords.remove(keyword.trim().toLowerCase());
        HoneyConfig.getPrefs().edit().putStringSet(PREF_KEYWORDS, new HashSet<>(watchedKeywords)).apply();
    }

    public static synchronized Set<String> getKeywords() {
        if (watchedKeywords.isEmpty()) {
            loadKeywords();
        }
        return new HashSet<>(watchedKeywords);
    }

    /**
     * Checks if a message text contains any watched keyword.
     */
    public static boolean checkMessage(String text) {
        if (text == null || text.isEmpty()) return false;
        String normalized = text.toLowerCase();
        for (String kw : getKeywords()) {
            if (normalized.contains(kw)) {
                return true;
            }
        }
        return false;
    }
}
