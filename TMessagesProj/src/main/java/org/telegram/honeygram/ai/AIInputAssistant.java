package org.telegram.honeygram.ai;

import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.FileLog;

/**
 * AIInputAssistant - Quick OpenAI-powered actions directly inside the message input bar
 * (Fix Grammar, Make Polite, Summarize, Translate, Tone adjustments).
 */
public class AIInputAssistant {

    public enum ActionType {
        FIX_GRAMMAR("Correct all grammar and spelling errors in the following text. Return only the corrected text without explanations:"),
        MAKE_POLITE("Rewrite the following text to sound polite, professional, and friendly. Return only the revised text:"),
        SHORTEN("Make the following text concise and short while preserving the core meaning. Return only the shortened text:"),
        TRANSLATE_EN("Translate the following text to fluent English. Return only the translated text:"),
        TRANSLATE_RU("Translate the following text to fluent Russian. Return only the translated text:");

        public final String prompt;

        ActionType(String prompt) {
            this.prompt = prompt;
        }
    }

    public interface AssistantCallback {
        void onResult(String resultText);
        void onError(String error);
    }

    public static void processText(String inputText, ActionType action, AssistantCallback callback) {
        if (inputText == null || inputText.trim().isEmpty()) {
            callback.onError("Input text is empty");
            return;
        }

        try {
            JSONArray messages = new JSONArray();

            JSONObject sysMsg = new JSONObject();
            sysMsg.put("role", "system");
            sysMsg.put("content", action.prompt);
            messages.put(sysMsg);

            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", inputText);
            messages.put(userMsg);

            OpenAIClient.generateResponse(messages, new OpenAIClient.AICallback() {
                @Override
                public void onResponse(String text) {
                    callback.onResult(text);
                }

                @Override
                public void onError(String errorMessage) {
                    callback.onError(errorMessage);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
            callback.onError(e.getMessage());
        }
    }
}
