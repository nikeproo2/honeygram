package org.telegram.honeygram.badges;

import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.honeygram.HoneyConfig;
import org.telegram.messenger.FileLog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * BadgeManager - Synchronizes and caches the list of HoneyGram supporters & donors.
 * Anyone who donates gets a special 🍯 Honey badge visible to all HoneyGram users worldwide.
 */
public class BadgeManager {

    // Remote URL pointing to the donors JSON list (GitHub RAW or Cloudflare Worker)
    private static final String REMOTE_BADGES_URL = "https://raw.githubusercontent.com/HoneyGram-App/badges/main/supporters.json";

    public enum BadgeType {
        NONE,
        SUPPORTER,  // 🍯 Gold Honey Comb
        VIP,        // 👑 Gold Crown
        DEVELOPER   // ⚡ Honey Dev
    }

    private static final Set<Long> supporterIds = new HashSet<>();
    private static final Set<Long> vipIds = new HashSet<>();
    private static final Set<Long> developerIds = new HashSet<>();

    public static void syncBadgesAsync() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                URL url = new URL(REMOTE_BADGES_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder json = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        json.append(line);
                    }
                    reader.close();

                    JSONObject root = new JSONObject(json.toString());

                    // Parse supporters
                    JSONArray supporters = root.optJSONArray("supporters");
                    if (supporters != null) {
                        supporterIds.clear();
                        for (int i = 0; i < supporters.length(); i++) {
                            supporterIds.add(supporters.getLong(i));
                        }
                    }

                    // Parse VIPs
                    JSONArray vips = root.optJSONArray("vips");
                    if (vips != null) {
                        vipIds.clear();
                        for (int i = 0; i < vips.length(); i++) {
                            vipIds.add(vips.getLong(i));
                        }
                    }

                    // Parse Developers
                    JSONArray devs = root.optJSONArray("developers");
                    if (devs != null) {
                        developerIds.clear();
                        for (int i = 0; i < devs.length(); i++) {
                            developerIds.add(devs.getLong(i));
                        }
                    }

                    FileLog.d("HoneyGram Badges: Synchronized " + (supporterIds.size() + vipIds.size() + developerIds.size()) + " badges.");
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        });
    }

    public static BadgeType getUserBadge(long userId) {
        if (!HoneyConfig.isShowHoneyBadges()) {
            return BadgeType.NONE;
        }
        if (developerIds.contains(userId)) return BadgeType.DEVELOPER;
        if (vipIds.contains(userId)) return BadgeType.VIP;
        if (supporterIds.contains(userId)) return BadgeType.SUPPORTER;
        return BadgeType.NONE;
    }
}
