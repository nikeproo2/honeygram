package org.telegram.honeygram.rules;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * AutoResponderRule - Data structure representing a custom auto-reply scenario rule.
 */
public class AutoResponderRule {

    public enum MatchType {
        CONTAINS,
        EXACT,
        REGEX,
        STARTS_WITH,
        ALL_MESSAGES
    }

    public enum ChatTarget {
        ALL,
        PRIVATE_ONLY,
        GROUPS_ONLY,
        UNKNOWN_CONTACTS_ONLY
    }

    public String id;
    public boolean enabled = true;
    public String title;
    public MatchType matchType = MatchType.CONTAINS;
    public String triggerPattern = "";
    public String replyTemplate = "";
    public ChatTarget chatTarget = ChatTarget.ALL;
    public int delaySeconds = 2;
    public boolean passToAi = false; // Hybrid chain: if true, passes to OpenAI instead of fixed text

    public AutoResponderRule(String id, String title, String triggerPattern, String replyTemplate) {
        this.id = id;
        this.title = title;
        this.triggerPattern = triggerPattern;
        this.replyTemplate = replyTemplate;
    }

    public JSONObject toJson() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", id);
            obj.put("enabled", enabled);
            obj.put("title", title);
            obj.put("matchType", matchType.name());
            obj.put("triggerPattern", triggerPattern);
            obj.put("replyTemplate", replyTemplate);
            obj.put("chatTarget", chatTarget.name());
            obj.put("delaySeconds", delaySeconds);
            obj.put("passToAi", passToAi);
            return obj;
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public static AutoResponderRule fromJson(JSONObject obj) {
        try {
            AutoResponderRule rule = new AutoResponderRule(
                    obj.getString("id"),
                    obj.getString("title"),
                    obj.optString("triggerPattern", ""),
                    obj.optString("replyTemplate", "")
            );
            rule.enabled = obj.optBoolean("enabled", true);
            rule.matchType = MatchType.valueOf(obj.optString("matchType", MatchType.CONTAINS.name()));
            rule.chatTarget = ChatTarget.valueOf(obj.optString("chatTarget", ChatTarget.ALL.name()));
            rule.delaySeconds = obj.optInt("delaySeconds", 2);
            rule.passToAi = obj.optBoolean("passToAi", false);
            return rule;
        } catch (Exception e) {
            return null;
        }
    }
}
