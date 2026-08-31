package org.telegram.honeygram.tools;

import org.telegram.tgnet.TLRPC;

/**
 * IdInspector - Extracts detailed MTProto metadata, User/Chat/DC IDs and formatting for power users.
 */
public class IdInspector {

    public static class InspectionResult {
        public long id;
        public String type;
        public int dcId;
        public String username;
        public boolean isBot;
        public boolean isVerified;
        public boolean isRestricted;

        public String getFormattedDetails() {
            StringBuilder sb = new StringBuilder();
            sb.append("🆔 ID: ").append(id).append("\n");
            sb.append("🏷 Type: ").append(type).append("\n");
            if (dcId > 0) sb.append("🌐 DataCenter: DC").append(dcId).append("\n");
            if (username != null && !username.isEmpty()) sb.append("🔗 Username: @").append(username).append("\n");
            sb.append("🤖 Is Bot: ").append(isBot ? "Yes" : "No").append("\n");
            sb.append("✅ Verified: ").append(isVerified ? "Yes" : "No").append("\n");
            sb.append("🔒 Restricted: ").append(isRestricted ? "Yes" : "No");
            return sb.toString();
        }
    }

    public static InspectionResult inspectUser(TLRPC.User user) {
        if (user == null) return null;
        InspectionResult res = new InspectionResult();
        res.id = user.id;
        res.type = "User";
        res.username = user.username;
        res.isBot = user.bot;
        res.isVerified = user.verified;
        res.isRestricted = user.restricted;
        if (user.photo != null && user.photo.photo_small != null) {
            res.dcId = user.photo.photo_small.dc_id;
        }
        return res;
    }

    public static InspectionResult inspectChat(TLRPC.Chat chat) {
        if (chat == null) return null;
        InspectionResult res = new InspectionResult();
        res.id = -chat.id;
        res.type = chat.megagroup ? "Supergroup" : (chat.broadcast ? "Channel" : "Group");
        res.username = chat.username;
        res.isBot = false;
        res.isVerified = chat.verified;
        res.isRestricted = chat.restricted;
        if (chat.photo != null && chat.photo.photo_small != null) {
            res.dcId = chat.photo.photo_small.dc_id;
        }
        return res;
    }
}
