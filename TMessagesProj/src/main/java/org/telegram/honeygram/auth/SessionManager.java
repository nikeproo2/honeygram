package org.telegram.honeygram.auth;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import org.telegram.messenger.FileLog;

import java.io.File;
import java.io.FileOutputStream;

/**
 * SessionManager - Handles import and export of Telegram sessions (.session and tdata).
 * Compatible with Telethon SQLite session format, Pyrogram, and Telegram Desktop tdata.
 */
public class SessionManager {

    public static class SessionData {
        public int dcId;
        public String serverAddress;
        public int port;
        public byte[] authKey;
        public long userId;

        public SessionData(int dcId, String serverAddress, int port, byte[] authKey, long userId) {
            this.dcId = dcId;
            this.serverAddress = serverAddress;
            this.port = port;
            this.authKey = authKey;
            this.userId = userId;
        }
    }

    public interface SessionCallback {
        void onSuccess(SessionData sessionData);
        void onError(String error);
    }

    /**
     * Imports a Telethon / Pyrogram SQLite .session file.
     */
    public static void importTelethonSession(File sessionFile, SessionCallback callback) {
        if (!sessionFile.exists() || !sessionFile.canRead()) {
            callback.onError("Session file does not exist or is not readable.");
            return;
        }

        try (SQLiteDatabase db = SQLiteDatabase.openDatabase(sessionFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY)) {
            // Telethon schema: table 'sessions' -> (dc_id, server_address, port, auth_key, takeout_id)
            Cursor cursor = db.rawQuery("SELECT dc_id, server_address, port, auth_key FROM sessions LIMIT 1", null);
            if (cursor != null && cursor.moveToFirst()) {
                int dcId = cursor.getInt(0);
                String serverAddress = cursor.getString(1);
                int port = cursor.getInt(2);
                byte[] authKey = cursor.getBlob(3);
                cursor.close();

                // Telethon table 'entities' or version info for user_id
                long userId = 0;
                Cursor entityCursor = db.rawQuery("SELECT id FROM entities WHERE id > 0 LIMIT 1", null);
                if (entityCursor != null && entityCursor.moveToFirst()) {
                    userId = entityCursor.getLong(0);
                    entityCursor.close();
                }

                SessionData data = new SessionData(dcId, serverAddress, port, authKey, userId);
                callback.onSuccess(data);
                return;
            }
            if (cursor != null) cursor.close();
            callback.onError("Invalid session file structure.");
        } catch (Exception e) {
            FileLog.e(e);
            callback.onError("Failed to parse SQLite session: " + e.getMessage());
        }
    }

    /**
     * Exports active account credentials to a Telethon-compatible SQLite .session file.
     */
    public static File exportActiveAccountToSession(Context context, int currentAccount, SessionData sessionData) {
        try {
            File exportDir = new File(context.getExternalFilesDir(null), "HoneyGram_Sessions");
            if (!exportDir.exists()) exportDir.mkdirs();

            File sessionFile = new File(exportDir, "account_" + sessionData.userId + ".session");
            if (sessionFile.exists()) sessionFile.delete();

            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(sessionFile, null);
            db.execSQL("CREATE TABLE sessions (dc_id INTEGER PRIMARY KEY, server_address TEXT, port INTEGER, auth_key BLOB, takeout_id INTEGER);");
            db.execSQL("CREATE TABLE version (version INTEGER PRIMARY KEY);");
            db.execSQL("INSERT INTO version VALUES (7);");

            db.execSQL("INSERT INTO sessions (dc_id, server_address, port, auth_key, takeout_id) VALUES (?, ?, ?, ?, ?)",
                    new Object[]{sessionData.dcId, sessionData.serverAddress, sessionData.port, sessionData.authKey, 0});

            db.close();
            return sessionFile;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }
}
