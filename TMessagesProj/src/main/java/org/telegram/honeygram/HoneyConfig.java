package org.telegram.honeygram;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * HoneyConfig - Centralized configuration and state manager for HoneyGram.
 * Manages all custom switches, Material You settings, AI keys, privacy modes, and bypasses.
 */
public class HoneyConfig {

    private static final String PREFS_NAME = "honeygram_config";
    private static SharedPreferences preferences;

    // Keys
    public static final String KEY_GHOST_MODE = "hg_ghost_mode";
    public static final String KEY_DONT_SEND_READ = "hg_dont_send_read";
    public static final String KEY_DONT_SEND_TYPING = "hg_dont_send_typing";
    public static final String KEY_HIDE_STORIES_SEEN = "hg_hide_stories_seen";
    public static final String KEY_HIDE_ONLINE = "hg_hide_online";

    public static final String KEY_BYPASS_RESTRICTED_CONTENT = "hg_bypass_restricted_content";
    public static final String KEY_ALLOW_SCREENSHOTS = "hg_allow_screenshots";
    public static final String KEY_BYPASS_DISAPPEARING_TIMER = "hg_bypass_disappearing_timer";

    public static final String KEY_WEBPROXY_MODE = "hg_webproxy_mode"; // 0: Auto fallback, 1: Always on, 2: Disabled
    public static final String KEY_ACTIVE_SERVER_TYPE = "hg_active_server_type"; // 0: Prod, 1: Test, 2: Custom
    public static final String KEY_CUSTOM_DC_IP = "hg_custom_dc_ip";
    public static final String KEY_CUSTOM_DC_PORT = "hg_custom_dc_port";

    public static final String KEY_AI_BASE_URL = "hg_ai_base_url";
    public static final String KEY_AI_API_KEY = "hg_ai_api_key";
    public static final String KEY_AI_MODEL_NAME = "hg_ai_model_name";
    public static final String KEY_AI_SYSTEM_PROMPT = "hg_ai_system_prompt";
    public static final String KEY_AI_AUTO_RESPONDER_ENABLED = "hg_ai_auto_responder_enabled";
    public static final String KEY_RULES_AUTO_RESPONDER_ENABLED = "hg_rules_auto_responder_enabled";

    public static final String KEY_VISUAL_PREMIUM = "hg_visual_premium";
    public static final String KEY_VISUAL_STARS_COUNT = "hg_visual_stars_count";
    public static final String KEY_SHOW_HONEY_BADGES = "hg_show_honey_badges";

    public static final String KEY_PANIC_PIN = "hg_panic_pin";
    public static final String KEY_PANIC_MODE_TRIGGERED = "hg_panic_mode_triggered";
    public static final String KEY_TURBO_DOWNLOADER = "hg_turbo_downloader";
    public static final String KEY_TURBO_DOWNLOADER_THREADS = "hg_turbo_downloader_threads";
    public static final String KEY_VOICE_CHANGER_ENABLED = "hg_voice_changer_enabled";
    public static final String KEY_VOICE_CHANGER_PITCH = "hg_voice_changer_pitch";

    public static void init(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    public static SharedPreferences getPrefs() {
        return preferences;
    }

    // Ghost Mode & Privacy
    public static boolean isGhostMode() {
        return preferences.getBoolean(KEY_GHOST_MODE, false);
    }

    public static void setGhostMode(boolean enabled) {
        preferences.edit().putBoolean(KEY_GHOST_MODE, enabled).apply();
    }

    public static boolean isDontSendRead() {
        return isGhostMode() || preferences.getBoolean(KEY_DONT_SEND_READ, false);
    }

    public static boolean isDontSendTyping() {
        return isGhostMode() || preferences.getBoolean(KEY_DONT_SEND_TYPING, false);
    }

    public static boolean isHideStoriesSeen() {
        return isGhostMode() || preferences.getBoolean(KEY_HIDE_STORIES_SEEN, false);
    }

    // Bypass Features
    public static boolean isBypassRestrictedContent() {
        return preferences.getBoolean(KEY_BYPASS_RESTRICTED_CONTENT, true);
    }

    public static boolean isAllowScreenshots() {
        return preferences.getBoolean(KEY_ALLOW_SCREENSHOTS, true);
    }

    public static boolean isBypassDisappearingTimer() {
        return preferences.getBoolean(KEY_BYPASS_DISAPPEARING_TIMER, true);
    }

    // Network & WebProxy
    public static int getWebProxyMode() {
        return preferences.getInt(KEY_WEBPROXY_MODE, 0); // Default: 0 (Auto Fallback)
    }

    public static void setWebProxyMode(int mode) {
        preferences.edit().putInt(KEY_WEBPROXY_MODE, mode).apply();
    }

    // Server DC Switcher
    public static int getActiveServerType() {
        return preferences.getInt(KEY_ACTIVE_SERVER_TYPE, 0); // 0: Prod, 1: Test, 2: Custom
    }

    public static void setActiveServerType(int serverType) {
        preferences.edit().putInt(KEY_ACTIVE_SERVER_TYPE, serverType).apply();
    }

    // AI Configuration
    public static String getAiBaseUrl() {
        return preferences.getString(KEY_AI_BASE_URL, "https://api.openai.com/v1");
    }

    public static String getAiApiKey() {
        return preferences.getString(KEY_AI_API_KEY, "");
    }

    public static String getAiModelName() {
        return preferences.getString(KEY_AI_MODEL_NAME, "gpt-4o-mini");
    }

    public static String getAiSystemPrompt() {
        return preferences.getString(KEY_AI_SYSTEM_PROMPT, "You are a helpful and polite assistant in Telegram chat.");
    }

    public static boolean isAiAutoResponderEnabled() {
        return preferences.getBoolean(KEY_AI_AUTO_RESPONDER_ENABLED, false);
    }

    public static boolean isRulesAutoResponderEnabled() {
        return preferences.getBoolean(KEY_RULES_AUTO_RESPONDER_ENABLED, false);
    }

    // Visual Spoofers
    public static boolean isVisualPremium() {
        return preferences.getBoolean(KEY_VISUAL_PREMIUM, false);
    }

    public static int getVisualStarsCount() {
        return preferences.getInt(KEY_VISUAL_STARS_COUNT, 0);
    }

    public static boolean isShowHoneyBadges() {
        return preferences.getBoolean(KEY_SHOW_HONEY_BADGES, true);
    }

    // Power Tools
    public static boolean isTurboDownloaderEnabled() {
        return preferences.getBoolean(KEY_TURBO_DOWNLOADER, true);
    }

    public static int getTurboThreads() {
        return preferences.getInt(KEY_TURBO_DOWNLOADER_THREADS, 4);
    }

    public static boolean isPanicModeTriggered() {
        return preferences.getBoolean(KEY_PANIC_MODE_TRIGGERED, false);
    }

    public static void setPanicModeTriggered(boolean triggered) {
        preferences.edit().putBoolean(KEY_PANIC_MODE_TRIGGERED, triggered).apply();
    }

    public static String getPanicPin() {
        return preferences.getString(KEY_PANIC_PIN, "");
    }
}
