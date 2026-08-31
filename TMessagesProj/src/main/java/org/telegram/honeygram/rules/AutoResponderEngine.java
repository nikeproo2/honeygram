package org.telegram.honeygram.rules;

import org.json.JSONArray;
import org.telegram.honeygram.HoneyConfig;
import org.telegram.messenger.FileLog;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * AutoResponderEngine - Rule engine matching incoming messages against custom user rules.
 */
public class AutoResponderEngine {

    private static final String PREF_RULES_JSON = "hg_custom_rules_json";
    private static final List<AutoResponderRule> rules = new ArrayList<>();

    public interface RuleMatchCallback {
        void onRuleMatched(AutoResponderRule matchedRule, String processedReply);
    }

    public static synchronized void loadRules() {
        rules.clear();
        String json = HoneyConfig.getPrefs().getString(PREF_RULES_JSON, "[]");
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                AutoResponderRule rule = AutoResponderRule.fromJson(arr.getJSONObject(i));
                if (rule != null) {
                    rules.add(rule);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static synchronized void saveRules() {
        try {
            JSONArray arr = new JSONArray();
            for (AutoResponderRule rule : rules) {
                arr.put(rule.toJson());
            }
            HoneyConfig.getPrefs().edit().putString(PREF_RULES_JSON, arr.toString()).apply();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static synchronized List<AutoResponderRule> getRules() {
        if (rules.isEmpty()) {
            loadRules();
        }
        return rules;
    }

    public static synchronized void addRule(AutoResponderRule rule) {
        rules.add(rule);
        saveRules();
    }

    public static synchronized void removeRule(String ruleId) {
        rules.removeIf(r -> r.id.equals(ruleId));
        saveRules();
    }

    /**
     * Checks if an incoming message matches any active rule.
     */
    public static AutoResponderRule evaluateMessage(String text, boolean isPrivateChat, boolean isContact) {
        if (!HoneyConfig.isRulesAutoResponderEnabled() || text == null) {
            return null;
        }

        String normalizedText = text.trim().toLowerCase();

        for (AutoResponderRule rule : getRules()) {
            if (!rule.enabled) continue;

            // Check chat target
            if (rule.chatTarget == AutoResponderRule.ChatTarget.PRIVATE_ONLY && !isPrivateChat) continue;
            if (rule.chatTarget == AutoResponderRule.ChatTarget.GROUPS_ONLY && isPrivateChat) continue;
            if (rule.chatTarget == AutoResponderRule.ChatTarget.UNKNOWN_CONTACTS_ONLY && isContact) continue;

            // Check pattern matching
            boolean matches = false;
            String pattern = rule.triggerPattern.toLowerCase();

            switch (rule.matchType) {
                case ALL_MESSAGES:
                    matches = true;
                    break;
                case EXACT:
                    matches = normalizedText.equals(pattern);
                    break;
                case CONTAINS:
                    matches = normalizedText.contains(pattern);
                    break;
                case STARTS_WITH:
                    matches = normalizedText.startsWith(pattern);
                    break;
                case REGEX:
                    try {
                        matches = Pattern.compile(rule.triggerPattern, Pattern.CASE_INSENSITIVE).matcher(text).find();
                    } catch (Exception ignored) {}
                    break;
            }

            if (matches) {
                return rule;
            }
        }
        return null;
    }
}
