package org.telegram.honeygram.auth;

import android.content.Context;
import org.telegram.honeygram.HoneyConfig;
import org.telegram.messenger.FileLog;

import java.util.ArrayList;
import java.util.List;

/**
 * ServerManager - Manages multi-server DC switching and multi-account state partitioning.
 * Supports Official Production DCs, Telegram Test DCs, and Custom private MTProto servers.
 */
public class ServerManager {

    public static final int SERVER_PROD = 0;
    public static final int SERVER_TEST = 1;
    public static final int SERVER_CUSTOM = 2;

    public static class ServerEnvironment {
        public final int type;
        public final String name;
        public final String host;
        public final int port;
        public final boolean isTest;

        public ServerEnvironment(int type, String name, String host, int port, boolean isTest) {
            this.type = type;
            this.name = name;
            this.host = host;
            this.port = port;
            this.isTest = isTest;
        }
    }

    private static final List<ServerEnvironment> environments = new ArrayList<>();

    static {
        environments.add(new ServerEnvironment(SERVER_PROD, "Production (Official)", null, 443, false));
        environments.add(new ServerEnvironment(SERVER_TEST, "Test DC (Staging)", "149.154.167.40", 443, true));
        environments.add(new ServerEnvironment(SERVER_CUSTOM, "Custom MTProto Server", "", 443, false));
    }

    public static List<ServerEnvironment> getEnvironments() {
        return environments;
    }

    public static ServerEnvironment getCurrentEnvironment() {
        int currentType = HoneyConfig.getActiveServerType();
        for (ServerEnvironment env : environments) {
            if (env.type == currentType) {
                return env;
            }
        }
        return environments.get(0);
    }

    public static void switchEnvironment(Context context, int serverType) {
        if (serverType < 0 || serverType > 2) return;
        HoneyConfig.setActiveServerType(serverType);
        FileLog.d("HoneyGram: Switched active server environment to " + serverType);
    }

    /**
     * Returns account database prefix based on server type to allow independent multi-account pools.
     */
    public static String getAccountStoragePrefix(int accountNum) {
        int serverType = HoneyConfig.getActiveServerType();
        switch (serverType) {
            case SERVER_TEST:
                return "tgnet_test_" + accountNum;
            case SERVER_CUSTOM:
                return "tgnet_custom_" + accountNum;
            default:
                return "tgnet" + (accountNum == 0 ? "" : "_" + accountNum);
        }
    }
}
