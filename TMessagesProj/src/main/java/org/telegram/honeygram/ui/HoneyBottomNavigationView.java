package org.telegram.honeygram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;

/**
 * HoneyBottomNavigationView - Google Pixel / Android 15 Material You Bottom Navigation Bar.
 * Features 4 central tabs: Chats, Contacts, Settings, Profile.
 * Uses Monet Amber / Gold palette (#FFB800) with rounded M3 indicator pills and fluid spring transitions.
 */
public class HoneyBottomNavigationView extends FrameLayout {

    public static final int TAB_CHATS = 0;
    public static final int TAB_CONTACTS = 1;
    public static final int TAB_SETTINGS = 2;
    public static final int TAB_PROFILE = 3;
    public static final int TAB_COUNT = 4;

    // Google Pixel / Android 15 Material You Amber Monet Palette
    public static final int MONET_PRIMARY = 0xFFFFB800;
    public static final int MONET_CONTAINER = 0x33FFB800; // 20% alpha amber
    public static final int MONET_SURFACE = 0xFF101214;
    public static final int MONET_DIVIDER = 0xFF22252A;
    public static final int MONET_UNSELECTED = 0xFF8C877D;

    public interface OnTabSelectedListener {
        void onTabSelected(int tabIndex, boolean reselected);
    }

    private OnTabSelectedListener listener;
    private int selectedTab = TAB_CHATS;

    private final View topDivider;
    private final LinearLayout tabsContainer;
    private final TabItem[] tabItems = new TabItem[TAB_COUNT];

    private boolean isHidden = false;

    private static class TabItem {
        FrameLayout container;
        FrameLayout indicatorPill;
        GradientDrawable pillDrawable;
        ImageView iconView;
        View badgeDot;
        TextView labelView;
    }

    public HoneyBottomNavigationView(Context context) {
        super(context);
        setBackgroundColor(MONET_SURFACE);
        setClickable(true);

        // Top 1dp subtle divider line
        topDivider = new View(context);
        topDivider.setBackgroundColor(MONET_DIVIDER);
        addView(topDivider, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 1, Gravity.TOP));

        // Horizontal tabs container
        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setGravity(Gravity.CENTER_VERTICAL);
        addView(tabsContainer, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 64, Gravity.TOP, 0, 1, 0, 0));

        createTab(context, TAB_CHATS, R.drawable.hg_ic_tab_chats, LocaleController.getString("TabChats", R.string.TabChats));
        createTab(context, TAB_CONTACTS, R.drawable.hg_ic_tab_contacts, LocaleController.getString("TabContacts", R.string.TabContacts));
        createTab(context, TAB_SETTINGS, R.drawable.hg_ic_tab_settings, LocaleController.getString("TabSettings", R.string.TabSettings));
        createTab(context, TAB_PROFILE, R.drawable.hg_ic_tab_profile, LocaleController.getString("TabProfile", R.string.TabProfile));

        // Profile tab dot badge (Live status)
        if (tabItems[TAB_PROFILE] != null && tabItems[TAB_PROFILE].badgeDot != null) {
            tabItems[TAB_PROFILE].badgeDot.setVisibility(View.VISIBLE);
        }

        setSelectedTab(TAB_CHATS, false);
    }

    private void createTab(Context context, final int index, int iconResId, String title) {
        final TabItem item = new TabItem();

        // Single Tab Clickable Container
        item.container = new FrameLayout(context);
        item.container.setClickable(true);
        item.container.setBackground(Theme.createSelectorDrawable(MONET_CONTAINER, 2));

        LinearLayout itemContent = new LinearLayout(context);
        itemContent.setOrientation(LinearLayout.VERTICAL);
        itemContent.setGravity(Gravity.CENTER);
        item.container.addView(itemContent, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        // 1. M3 Pill Active Indicator (58dp x 30dp)
        item.indicatorPill = new FrameLayout(context);
        item.pillDrawable = new GradientDrawable();
        item.pillDrawable.setShape(GradientDrawable.RECTANGLE);
        item.pillDrawable.setCornerRadius(AndroidUtilities.dp(15));
        item.pillDrawable.setColor(MONET_CONTAINER);
        item.indicatorPill.setBackground(item.pillDrawable);
        itemContent.addView(item.indicatorPill, LayoutHelper.createLinear(58, 30, Gravity.CENTER_HORIZONTAL, 0, 4, 0, 0));

        // Icon inside Pill (22dp)
        item.iconView = new ImageView(context);
        item.iconView.setImageResource(iconResId);
        item.iconView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        item.indicatorPill.addView(item.iconView, LayoutHelper.createFrame(22, 22, Gravity.CENTER));

        // Badge Dot on Pill
        item.badgeDot = new View(context);
        GradientDrawable badgeDrawable = new GradientDrawable();
        badgeDrawable.setShape(GradientDrawable.OVAL);
        badgeDrawable.setColor(MONET_PRIMARY);
        item.badgeDot.setBackground(badgeDrawable);
        item.badgeDot.setVisibility(View.GONE);
        item.indicatorPill.addView(item.badgeDot, LayoutHelper.createFrame(6, 6, Gravity.TOP | Gravity.RIGHT, 0, 3, 10, 0));

        // 2. Tab Label Text (11sp)
        item.labelView = new TextView(context);
        item.labelView.setText(title);
        item.labelView.setTextSize(11);
        item.labelView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        item.labelView.setGravity(Gravity.CENTER_HORIZONTAL);
        itemContent.addView(item.labelView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 2, 0, 4));

        item.container.setOnClickListener(v -> {
            boolean reselected = (selectedTab == index);
            if (!reselected) {
                setSelectedTab(index, true);
            }
            if (listener != null) {
                listener.onTabSelected(index, reselected);
            }
        });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LayoutHelper.MATCH_PARENT, 1.0f);
        tabsContainer.addView(item.container, lp);
        tabItems[index] = item;
    }

    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        this.listener = listener;
    }

    public int getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedTab(int tabIndex) {
        setSelectedTab(tabIndex, true);
    }

    public void setSelectedTab(int tabIndex, boolean animate) {
        if (tabIndex < 0 || tabIndex >= TAB_COUNT) return;
        this.selectedTab = tabIndex;

        for (int i = 0; i < TAB_COUNT; i++) {
            TabItem item = tabItems[i];
            if (item == null) continue;
            boolean isSelected = (i == tabIndex);

            if (isSelected) {
                item.iconView.setColorFilter(new PorterDuffColorFilter(MONET_PRIMARY, PorterDuff.Mode.SRC_IN));
                item.labelView.setTextColor(MONET_PRIMARY);
                item.labelView.setAlpha(1.0f);

                if (animate) {
                    item.indicatorPill.setAlpha(0.0f);
                    item.indicatorPill.setScaleX(0.85f);
                    item.indicatorPill.setScaleY(0.85f);
                    item.indicatorPill.setVisibility(View.VISIBLE);
                    item.indicatorPill.animate()
                            .alpha(1.0f)
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(200)
                            .setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT)
                            .start();
                } else {
                    item.indicatorPill.setAlpha(1.0f);
                    item.indicatorPill.setScaleX(1.0f);
                    item.indicatorPill.setScaleY(1.0f);
                    item.indicatorPill.setVisibility(View.VISIBLE);
                }
            } else {
                item.iconView.setColorFilter(new PorterDuffColorFilter(MONET_UNSELECTED, PorterDuff.Mode.SRC_IN));
                item.labelView.setTextColor(MONET_UNSELECTED);
                item.labelView.setAlpha(0.85f);

                if (animate && item.indicatorPill.getVisibility() == View.VISIBLE) {
                    item.indicatorPill.animate()
                            .alpha(0.0f)
                            .scaleX(0.85f)
                            .scaleY(0.85f)
                            .setDuration(150)
                            .setInterpolator(CubicBezierInterpolator.EASE_IN_QUINT)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    item.indicatorPill.setVisibility(View.INVISIBLE);
                                }
                            })
                            .start();
                } else {
                    item.indicatorPill.setVisibility(View.INVISIBLE);
                    item.indicatorPill.setAlpha(0.0f);
                }
            }
        }
    }

    public void setBadge(int tabIndex, boolean visible) {
        if (tabIndex >= 0 && tabIndex < TAB_COUNT && tabItems[tabIndex] != null && tabItems[tabIndex].badgeDot != null) {
            tabItems[tabIndex].badgeDot.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public void show(boolean animated) {
        if (!isHidden && getVisibility() == View.VISIBLE) return;
        isHidden = false;
        animate().cancel();
        if (animated) {
            setVisibility(View.VISIBLE);
            animate()
                    .translationY(0)
                    .alpha(1.0f)
                    .setDuration(220)
                    .setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT)
                    .setListener(null)
                    .start();
        } else {
            setVisibility(View.VISIBLE);
            setTranslationY(0);
            setAlpha(1.0f);
        }
    }

    public void hide(boolean animated) {
        if (isHidden && getVisibility() == View.GONE) return;
        isHidden = true;
        animate().cancel();
        int targetY = getMeasuredHeight() > 0 ? getMeasuredHeight() : AndroidUtilities.dp(80);
        if (animated) {
            animate()
                    .translationY(targetY)
                    .alpha(0.0f)
                    .setDuration(180)
                    .setInterpolator(CubicBezierInterpolator.EASE_IN_QUINT)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (isHidden) {
                                setVisibility(View.GONE);
                            }
                        }
                    })
                    .start();
        } else {
            setVisibility(View.GONE);
            setTranslationY(targetY);
            setAlpha(0.0f);
        }
    }

    public boolean isHidden() {
        return isHidden || getVisibility() != View.VISIBLE;
    }

    public void updateColors() {
        setBackgroundColor(MONET_SURFACE);
        topDivider.setBackgroundColor(MONET_DIVIDER);
        setSelectedTab(selectedTab, false);
    }
}
