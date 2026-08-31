package org.telegram.honeygram.network;

import org.telegram.honeygram.HoneyConfig;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.ConnectionsManager;

/**
 * WebProxyManager - Smart priority pipeline for network connections & anti-blocking.
 * Guarantees zero conflicts with user-defined proxies.
 */
public class WebProxyManager {

    public static final int MODE_AUTO_FALLBACK = 0; // Auto-enable WebProxy when direct fails
    public static final int MODE_ALWAYS_WEBPROXY = 1; // Always use Telegram WebProxy / WebSocket TLS
    public static final int MODE_DISABLED = 2; // Only direct connection or manual proxy

    /**
     * Determines whether to enable the native WebProxy/WebSocket layer for a connection attempt.
     * @param hasUserCustomProxyActive true if the user has manually enabled a SOCKS5/MTProto proxy.
     * @param isDirectConnectionBlocked true if previous direct connection timed out.
     */
    public static boolean shouldUseWebProxyTransport(boolean hasUserCustomProxyActive, boolean isDirectConnectionBlocked) {
        // Priority 1: User custom proxy is active -> NEVER override with WebProxy
        if (hasUserCustomProxyActive) {
            FileLog.d("HoneyGram Network: User custom proxy is active. WebProxy bypassed.");
            return false;
        }

        int mode = HoneyConfig.getWebProxyMode();

        switch (mode) {
            case MODE_ALWAYS_WEBPROXY:
                return true;
            case MODE_AUTO_FALLBACK:
                return isDirectConnectionBlocked;
            case MODE_DISABLED:
            default:
                return false;
        }
    }

    /**
     * Configures the native WebSocket over TLS endpoint for WebProxy transport in Telegram network core.
     */
    public static String getWebProxyHost(int dcId) {
        // Telegram Official WebSocket Gateway / WebProxy endpoints (Port 443 / TLS)
        switch (dcId) {
            case 1:
                return "pluto.web.telegram.org";
            case 2:
                return "venus.web.telegram.org";
            case 3:
                return "aurora.web.telegram.org";
            case 4:
                return "vesta.web.telegram.org";
            case 5:
                return "flora.web.telegram.org";
            default:
                return "web.telegram.org";
        }
    }

    public static int getWebProxyPort() {
        return 443; // Standard HTTPS / WSS port bypasses DPI filters
    }
}
