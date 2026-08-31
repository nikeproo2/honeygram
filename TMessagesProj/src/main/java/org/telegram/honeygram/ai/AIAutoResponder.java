package org.telegram.honeygram.ai;

import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.honeygram.HoneyConfig;
import org.telegram.messenger.FileLog;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * AIAutoResponder - Manages automated AI responses to incoming Telegram messages.
 * Buffers conversation history per chat, simulates human typing delay, and sends replies.
 */
public class AIAutoResponder {

    private static final Map<Long, LinkedList<JSONObject>> chatHistories = new HashMap<>();
    private static final int MAX_CONTEXT_MESSAGES = 10;

    public interface AutoReplyListener {
        void onSendReply(long dialogId, String replyText);
    }

    public static void onIncomingMessage(long dialogId, String senderName, String messageText, AutoReplyListener listener) {
        if (!HoneyConfig.isAiAutoResponderEnabled() || messageText == null || messageText.trim().isEmpty()) {
            return;
        }

        try {
            LinkedList<JSONObject> history = chatHistories.computeIfAbsent(dialogId, k -> new LinkedList<>());

            // Add user message to history
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", (senderName != null ? senderName + ": " : "") + messageText);
            history.add(userMsg);

            while (history.size() > MAX_CONTEXT_MESSAGES) {
                history.removeFirst();
            }

            // Build request payload
            JSONArray messages = new JSONArray();

            // System prompt
            JSONObject sysMsg = new JSONObject();
            sysMsg.put("role", "system");
            sysMsg.put("content", HoneyConfig.getAiSystemPrompt());
            messages.put(sysMsg);

            // History messages
            for (JSONObject msg : history) {
                messages.put(msg);
            }

            // Call AI
            OpenAIClient.generateResponse(messages, new OpenAIClient.AICallback() {
                @Override
                public void onResponse(String text) {
                    try {
                        JSONObject assistantMsg = new JSONObject();
                        assistantMsg.put("role", "assistant");
                        assistantMsg.put("content", text);
                        history.add(assistantMsg);

                        if (listener != null) {
                            listener.onSendReply(dialogId, text);
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    FileLog.e("HoneyGram AI AutoResponder Error: " + errorMessage);
                }
            });

        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void clearChatHistory(long dialogId) {
        chatHistories.remove(dialogId);
    }
}
