package com.exteragram.messenger.boost.encryption;

import org.telegram.messenger.MessageObject;

public class EncryptionHelper {

    public static String[] names = new String[]{"None"};

    public static boolean isEncrypted(String message) {
        return false;
    }

    public static boolean isEncrypted(MessageObject messageObject, Object dummy) {
        return false;
    }

    public static MessageObject decryptMessage(MessageObject obj, Object dummy) {
        return obj;
    }

    public static MessageObject decryptMessage(MessageObject obj) {
        return obj;
    }

    public static String encryptMessage(String text, long dialogId, int type) {
        return text;
    }

    public static String encryptMessage(String text, long dialogId, BaseEncryptor encryptor) {
        return text;
    }

    public interface EncryptCallback {
        void onEncrypted(String message);
    }

    public static void encryptMessage(String text, long dialogId, int type, EncryptCallback callback) {
        if (callback != null) {
            callback.onEncrypted(text);
        }
    }

    public static int getEncryptorTypeFor(long dialogId) {
        return 0;
    }

    public static void setEncryptorTypeFor(long dialogId, int type) {
    }

    private static final BaseEncryptor defaultEncryptor = new BaseEncryptor();

    public static BaseEncryptor getEncryptorBy(long dialogId) {
        return defaultEncryptor;
    }
}
