package org.telegram.honeygram.ai;

import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.honeygram.HoneyConfig;
import org.telegram.messenger.FileLog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * OpenAIClient - Universal async client compatible with any OpenAI API format
 * Supports OpenAI, DeepSeek, Groq, OpenRouter, vLLM, and local Ollama instances.
 */
public class OpenAIClient {

    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface AICallback {
        void onResponse(String text);
        void onError(String errorMessage);
    }

    public static void generateResponse(JSONArray messages, AICallback callback) {
        executor.execute(() -> {
            try {
                String baseUrl = HoneyConfig.getAiBaseUrl().trim();
                if (!baseUrl.endsWith("/")) {
                    baseUrl += "/";
                }
                String endpoint = baseUrl.endsWith("chat/completions/") ? baseUrl : baseUrl + "chat/completions";
                String apiKey = HoneyConfig.getAiApiKey().trim();
                String model = HoneyConfig.getAiModelName().trim();

                URL url = new URL(endpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                if (!apiKey.isEmpty()) {
                    conn.setRequestProperty("Authorization", "Bearer " + apiKey);
                }
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(30000);
                conn.setDoOutput(true);

                JSONObject requestBody = new JSONObject();
                requestBody.put("model", model.isEmpty() ? "gpt-4o-mini" : model);
                requestBody.put("messages", messages);
                requestBody.put("temperature", 0.7);

                byte[] postData = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postData);
                }

                int statusCode = conn.getResponseCode();
                if (statusCode >= 200 && statusCode < 300) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray choices = jsonResponse.getJSONArray("choices");
                    if (choices.length() > 0) {
                        JSONObject choice = choices.getJSONObject(0);
                        JSONObject message = choice.getJSONObject("message");
                        String content = message.getString("content");

                        mainHandler.post(() -> callback.onResponse(content.trim()));
                        return;
                    }
                    mainHandler.post(() -> callback.onError("Empty response from AI"));
                } else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder err = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        err.append(line);
                    }
                    br.close();
                    mainHandler.post(() -> callback.onError("HTTP " + statusCode + ": " + err.toString()));
                }
            } catch (Exception e) {
                FileLog.e(e);
                mainHandler.post(() -> callback.onError("Network error: " + e.getMessage()));
            }
        });
    }
}
