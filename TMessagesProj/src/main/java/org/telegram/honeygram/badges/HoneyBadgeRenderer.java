package org.telegram.honeygram.badges;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ReplacementSpan;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * HoneyBadgeRenderer - Renders custom HoneyGram badges (🍯, 👑, ⚡) next to user names.
 */
public class HoneyBadgeRenderer {

    public static CharSequence attachBadgeToName(long userId, CharSequence name) {
        BadgeManager.BadgeType badge = BadgeManager.getUserBadge(userId);
        if (badge == BadgeManager.BadgeType.NONE || name == null) {
            return name;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder(name);
        builder.append(" ");

        String badgeEmoji;
        switch (badge) {
            case DEVELOPER:
                badgeEmoji = " ⚡";
                break;
            case VIP:
                badgeEmoji = " 👑";
                break;
            case SUPPORTER:
            default:
                badgeEmoji = " 🍯";
                break;
        }

        int start = builder.length();
        builder.append(badgeEmoji);
        return builder;
    }
}
