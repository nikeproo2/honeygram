package com.exteragram.messenger.preferences.components;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.exteragram.messenger.utils.MonetUtils;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class HeaderSettingsCell extends FrameLayout {

    public final TextView titleTextView;

    public HeaderSettingsCell(Context context) {
        super(context);

        Theme.ThemeInfo theme = Theme.getActiveTheme();
        boolean isDark = theme == null || theme.isDark();

        int amberPrimary = 0xFFFFB800;
        int amberLight = 0xFFFFD54F;

        GradientDrawable logoBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{amberPrimary, amberLight}
        );
        logoBg.setCornerRadius(AndroidUtilities.dp(30));

        Drawable arrow = ContextCompat.getDrawable(context, R.drawable.ic_logo_foreground);
        if (arrow != null) {
            arrow = arrow.mutate();
            arrow.setColorFilter(new PorterDuffColorFilter(0xFF1F1500, PorterDuff.Mode.SRC_IN));
        }

        ImageView logo = new ImageView(context);
        logo.setScaleType(ImageView.ScaleType.CENTER);
        logo.setBackground(logoBg);
        logo.setImageDrawable(arrow);
        addView(logo, LayoutHelper.createFrame(88, 88, Gravity.CENTER | Gravity.TOP, 0, 18, 0, 0));

        titleTextView = new TextView(context);
        titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        titleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
        titleTextView.setText(String.format("HoneyGram %s", BuildVars.BUILD_VERSION_STRING));
        titleTextView.setLines(1);
        titleTextView.setMaxLines(1);
        titleTextView.setSingleLine(true);
        titleTextView.setPadding(0, 0, 0, 0);
        titleTextView.setGravity(Gravity.CENTER);
        addView(titleTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER | Gravity.TOP, 40, 118, 40, 0));

        TextView subtitleTextView = new TextView(context);
        subtitleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        subtitleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_REGULAR));
        subtitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        subtitleTextView.setLineSpacing(AndroidUtilities.dp(2), 1f);
        subtitleTextView.setText(LocaleController.getString("AboutExteraDescription", R.string.AboutExteraDescription));
        subtitleTextView.setGravity(Gravity.CENTER);
        subtitleTextView.setLines(0);
        subtitleTextView.setMaxLines(0);
        subtitleTextView.setSingleLine(false);
        subtitleTextView.setPadding(0, 0, 0, 0);
        addView(subtitleTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER | Gravity.TOP, 40, 146, 40, 18));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    }
}
