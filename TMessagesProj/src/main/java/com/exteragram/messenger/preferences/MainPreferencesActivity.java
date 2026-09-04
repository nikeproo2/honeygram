package com.exteragram.messenger.preferences;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.InputType;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.exteragram.messenger.preferences.components.HeaderSettingsCell;

import org.telegram.honeygram.HoneyConfig;
import org.telegram.honeygram.auth.ServerManager;
import org.telegram.honeygram.auth.SessionManager;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.DataSettingsActivity;
import org.telegram.ui.FiltersSetupActivity;
import org.telegram.ui.LanguageSelectActivity;
import org.telegram.ui.NotificationsSettingsActivity;
import org.telegram.ui.PrivacySettingsActivity;
import org.telegram.ui.SessionsActivity;

import java.io.File;
import java.util.Locale;

public class MainPreferencesActivity extends BasePreferencesActivity {

    private static final int AMBER_PRIMARY = 0xFFFFB800;
    private static final int AMBER_CONTAINER = 0xFF3E2D00;

    private View actionBarBackground;
    private AnimatorSet actionBarAnimator;
    private HeaderSettingsCell headerSettingsCell;

    // Header & Heroes
    private int headerRow;
    private int heroHeaderRow;
    private int premiumHubRow;
    private int phantomStarsRow;
    private int proModulesRow;
    private int heroDividerRow;

    // Mod Customization Categories
    private int categoriesHeaderRow;
    private int appearanceRow;
    private int chatsRow;
    private int generalRow;
    private int sessionsRow;
    private int otherRow;
    private int categoriesDividerRow;

    // Pixel System Settings
    private int systemHeaderRow;
    private int notificationsRow;
    private int privacyRow;
    private int storageRow;
    private int foldersRow;
    private int languageRow;
    private int systemDividerRow;

    // About & Honey Features
    private int aboutHeaderRow;
    private int honeyBadgesRow;
    private int githubRow;
    private int infoDividerRow;

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setBackground(null);
        actionBar.setTitleColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), false);
        actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_listSelector), false);
        actionBar.setCastShadows(false);
        actionBar.setAddToContainer(false);
        actionBar.setOccupyStatusBar(!AndroidUtilities.isTablet());
        actionBar.setTitle(getTitle());
        actionBar.getTitleTextView().setAlpha(0.0f);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        fragmentView = new FrameLayout(context) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) actionBarBackground.getLayoutParams();
                layoutParams.height = ActionBar.getCurrentActionBarHeight() + (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.dp(3);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                checkScroll(false);
            }
        };
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        fragmentView.setTag(Theme.key_windowBackgroundGray);
        FrameLayout frameLayout = (FrameLayout) fragmentView;

        listView = new RecyclerListView(context);
        listView.setLayoutManager(layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setAdapter(listAdapter = createAdapter(context));
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setOnItemClickListener(this::onItemClick);
        listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                checkScroll(true);
            }
        });

        actionBarBackground = new View(context) {
            private final Paint paint = new Paint();

            @Override
            protected void onDraw(Canvas canvas) {
                paint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                int h = getMeasuredHeight() - AndroidUtilities.dp(3);
                canvas.drawRect(0, 0, getMeasuredWidth(), h, paint);
                parentLayout.drawHeaderShadow(canvas, h);
            }
        };
        actionBarBackground.setAlpha(0.0f);
        frameLayout.addView(actionBarBackground, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        frameLayout.addView(actionBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        updateRowsId();
        return fragmentView;
    }

    private final int[] location = new int[2];

    private void checkScroll(boolean animated) {
        int first = layoutManager.findFirstVisibleItemPosition();
        boolean show;
        if (first != 0) {
            show = true;
        } else {
            RecyclerView.ViewHolder holder = listView.findViewHolderForAdapterPosition(first);
            if (holder == null) {
                show = true;
            } else {
                headerSettingsCell = (HeaderSettingsCell) holder.itemView;
                headerSettingsCell.titleTextView.getLocationOnScreen(location);
                show = location[1] + headerSettingsCell.titleTextView.getMeasuredHeight() < actionBar.getBottom();
            }
        }
        boolean visible = actionBarBackground.getTag() == null;
        if (show != visible) {
            actionBarBackground.setTag(show ? null : 1);
            if (actionBarAnimator != null) {
                actionBarAnimator.cancel();
                actionBarAnimator = null;
            }
            if (animated) {
                actionBarAnimator = new AnimatorSet();
                actionBarAnimator.playTogether(
                        ObjectAnimator.ofFloat(actionBarBackground, View.ALPHA, show ? 1.0f : 0.0f),
                        ObjectAnimator.ofFloat(actionBar.getTitleTextView(), View.ALPHA, show ? 1.0f : 0.0f)
                );
                actionBarAnimator.setDuration(250);
                actionBarAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (animation.equals(actionBarAnimator)) {
                            actionBarAnimator = null;
                        }
                    }
                });
                actionBarAnimator.start();
            } else {
                actionBarBackground.setAlpha(show ? 1.0f : 0.0f);
                actionBar.getTitleTextView().setAlpha(show ? 1.0f : 0.0f);
            }
        }
    }

    @Override
    protected void updateRowsId() {
        super.updateRowsId();

        headerRow = newRow();

        heroHeaderRow = newRow();
        premiumHubRow = newRow();
        phantomStarsRow = newRow();
        proModulesRow = newRow();
        heroDividerRow = newRow();

        categoriesHeaderRow = newRow();
        appearanceRow = newRow();
        chatsRow = newRow();
        generalRow = newRow();
        sessionsRow = newRow();
        otherRow = newRow();
        categoriesDividerRow = newRow();

        systemHeaderRow = newRow();
        notificationsRow = newRow();
        privacyRow = newRow();
        storageRow = newRow();
        foldersRow = newRow();
        languageRow = newRow();
        systemDividerRow = newRow();

        aboutHeaderRow = newRow();
        honeyBadgesRow = newRow();
        githubRow = newRow();
        infoDividerRow = newRow();
    }

    @Override
    protected void onItemClick(View view, int position, float x, float y) {
        if (position == premiumHubRow) {
            boolean active = !HoneyConfig.isVisualPremium();
            HoneyConfig.getPrefs().edit().putBoolean(HoneyConfig.KEY_VISUAL_PREMIUM, active).apply();
            if (listAdapter != null) listAdapter.notifyDataSetChanged();
            Toast.makeText(getParentActivity(), active ? "Telegram Premium: ACTIVE (Visual Spoofed)" : "Telegram Premium: Inactive", Toast.LENGTH_SHORT).show();
        } else if (position == phantomStarsRow) {
            showStarsDialog();
        } else if (position == proModulesRow) {
            presentFragment(new HoneyGramPreferencesActivity());
        } else if (position == appearanceRow) {
            presentFragment(new AppearancePreferencesActivity());
        } else if (position == chatsRow) {
            presentFragment(new ChatsPreferencesActivity());
        } else if (position == generalRow) {
            presentFragment(new GeneralPreferencesActivity());
        } else if (position == sessionsRow) {
            showSessionsMenu();
        } else if (position == otherRow) {
            presentFragment(new OtherPreferencesActivity());
        } else if (position == notificationsRow) {
            presentFragment(new NotificationsSettingsActivity());
        } else if (position == privacyRow) {
            presentFragment(new PrivacySettingsActivity());
        } else if (position == storageRow) {
            presentFragment(new DataSettingsActivity());
        } else if (position == foldersRow) {
            presentFragment(new FiltersSetupActivity());
        } else if (position == languageRow) {
            presentFragment(new LanguageSelectActivity());
        } else if (position == honeyBadgesRow) {
            boolean val = !HoneyConfig.isShowHoneyBadges();
            HoneyConfig.getPrefs().edit().putBoolean(HoneyConfig.KEY_SHOW_HONEY_BADGES, val).apply();
            if (view instanceof TextCheckCell) ((TextCheckCell) view).setChecked(val);
        } else if (position == githubRow) {
            Browser.openUrl(getParentActivity(), "https://github.com/nikeproo2/honeygram");
        }
    }

    private void showStarsDialog() {
        if (getParentActivity() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("VisualStars", R.string.VisualStars));
        final EditTextBoldCursor editText = new EditTextBoldCursor(getParentActivity());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setText(String.valueOf(HoneyConfig.getVisualStarsCount()));
        builder.setView(editText);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), (dialog, which) -> {
            try {
                int count = Integer.parseInt(editText.getText().toString().trim());
                HoneyConfig.getPrefs().edit().putInt(HoneyConfig.KEY_VISUAL_STARS_COUNT, count).apply();
            } catch (Exception ignore) {}
            if (listAdapter != null) listAdapter.notifyDataSetChanged();
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.show();
    }

    private void showSessionsMenu() {
        if (getParentActivity() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle("Сессии & Мультисервер");
        String[] items = new String[]{
                LocaleController.getString("ImportSession", R.string.ImportSession),
                LocaleController.getString("ExportSession", R.string.ExportSession),
                LocaleController.getString("ServerEnvironment", R.string.ServerEnvironment),
                "Активные сеансы (Telegram Sessions)"
        };
        builder.setItems(items, (dialog, which) -> {
            if (which == 0) {
                showImportSessionDialog();
            } else if (which == 1) {
                showExportSessionDialog();
            } else if (which == 2) {
                showServerSelectorDialog();
            } else if (which == 3) {
                presentFragment(new SessionsActivity(0));
            }
        });
        builder.show();
    }

    private void showImportSessionDialog() {
        if (getParentActivity() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("ImportSession", R.string.ImportSession));
        builder.setMessage(LocaleController.getString("ImportSessionNotice", R.string.ImportSessionNotice));
        final EditTextBoldCursor editText = new EditTextBoldCursor(getParentActivity());
        File defaultDir = new File(getParentActivity().getExternalFilesDir(null), "HoneyGram_Sessions");
        editText.setHint(defaultDir.getAbsolutePath() + "/account.session");
        builder.setView(editText);
        builder.setPositiveButton(LocaleController.getString("ImportSession", R.string.ImportSession), (dialog, which) -> {
            String path = editText.getText().toString().trim();
            if (path.isEmpty()) {
                Toast.makeText(getParentActivity(), "Please provide a valid .session file path", Toast.LENGTH_SHORT).show();
                return;
            }
            File file = new File(path);
            SessionManager.importTelethonSession(file, new SessionManager.SessionCallback() {
                @Override
                public void onSuccess(SessionManager.SessionData sessionData) {
                    AndroidUtilities.runOnUIThread(() -> {
                        if (getParentActivity() == null) return;
                        AlertDialog.Builder success = new AlertDialog.Builder(getParentActivity());
                        success.setTitle(LocaleController.getString("SessionImportSuccess", R.string.SessionImportSuccess));
                        success.setMessage("DC: " + sessionData.dcId + "\nUser ID: " + sessionData.userId + "\nAddress: " + sessionData.serverAddress);
                        success.setPositiveButton("OK", null);
                        success.show();
                    });
                }

                @Override
                public void onError(String error) {
                    AndroidUtilities.runOnUIThread(() -> {
                        if (getParentActivity() == null) return;
                        Toast.makeText(getParentActivity(), "Import Error: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.show();
    }

    private void showExportSessionDialog() {
        if (getParentActivity() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("ExportSession", R.string.ExportSession));
        builder.setMessage("Export credentials for user ID " + UserConfig.getInstance(currentAccount).clientUserId + " to Telethon SQLite .session file?");
        builder.setPositiveButton("Export", (dialog, which) -> {
            int dcId = ConnectionsManager.getInstance(currentAccount).getCurrentDatacenterId();
            long userId = UserConfig.getInstance(currentAccount).clientUserId;
            SessionManager.SessionData data = new SessionManager.SessionData(dcId > 0 ? dcId : 2, "149.154.167.50", 443, new byte[256], userId);
            File exported = SessionManager.exportActiveAccountToSession(getParentActivity(), currentAccount, data);
            if (exported != null && exported.exists()) {
                AlertDialog.Builder success = new AlertDialog.Builder(getParentActivity());
                success.setTitle("Session Exported");
                success.setMessage(LocaleController.formatString("ExportSessionSuccess", R.string.ExportSessionSuccess, exported.getAbsolutePath()));
                success.setPositiveButton("OK", null);
                success.show();
            } else {
                Toast.makeText(getParentActivity(), LocaleController.getString("ExportSessionFailed", R.string.ExportSessionFailed), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.show();
    }

    private void showServerSelectorDialog() {
        if (getParentActivity() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("ServerEnvironment", R.string.ServerEnvironment));
        String[] items = new String[]{
                LocaleController.getString("ServerProd", R.string.ServerProd),
                LocaleController.getString("ServerTest", R.string.ServerTest),
                LocaleController.getString("ServerCustom", R.string.ServerCustom)
        };
        builder.setItems(items, (dialog, which) -> {
            ServerManager.switchEnvironment(getParentActivity(), which);
            if (listAdapter != null) listAdapter.notifyDataSetChanged();
            Toast.makeText(getParentActivity(), items[which], Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }

    @Override
    protected String getTitle() {
        return LocaleController.getString("HoneyGramSettings", R.string.HoneyGramSettings);
    }

    @Override
    protected boolean hasWhiteActionBar() {
        return true;
    }

    @Override
    protected BaseListAdapter createAdapter(Context context) {
        return new ListAdapter(context);
    }

    private class ListAdapter extends BaseListAdapter {

        public ListAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 1:
                    holder.itemView.setBackground(Theme.getThemedDrawable(mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 2: {
                    TextCell textCell = (TextCell) holder.itemView;
                    if (position == premiumHubRow) {
                        String status = HoneyConfig.isVisualPremium() ? "АКТИВЕН (Visual)" : "Неактивен";
                        textCell.setTextAndValueAndColorfulIcon("Telegram Premium Хаб", status, false, R.drawable.msg_fave, AMBER_PRIMARY, true);
                    } else if (position == phantomStarsRow) {
                        String stars = String.format(Locale.US, "%,d Stars", HoneyConfig.getVisualStarsCount());
                        textCell.setTextAndValueAndColorfulIcon("Эмулятор Фантомных Stars", stars, false, R.drawable.photo_star_fill, AMBER_PRIMARY, true);
                    } else if (position == proModulesRow) {
                        String ghostStatus = HoneyConfig.isGhostMode() ? "Ghost: ВКЛ" : "Настроить";
                        textCell.setTextAndValueAndColorfulIcon("Модули HoneyGram Pro", ghostStatus, false, R.drawable.msg2_secret, AMBER_PRIMARY, false);
                    } else if (position == appearanceRow) {
                        textCell.setTextAndValueAndColorfulIcon(LocaleController.getString("Appearance", R.string.Appearance), "Monet & Стили", false, R.drawable.msg_theme, AMBER_PRIMARY, true);
                    } else if (position == chatsRow) {
                        textCell.setTextAndValueAndColorfulIcon("Кастомизация чатов", "Стикеры & Тапы", false, R.drawable.msg_discussion, AMBER_PRIMARY, true);
                    } else if (position == generalRow) {
                        textCell.setTextAndValueAndColorfulIcon(LocaleController.getString("General", R.string.General), "CameraX & Скорость", false, R.drawable.msg_media, AMBER_PRIMARY, true);
                    } else if (position == sessionsRow) {
                        textCell.setTextAndValueAndColorfulIcon("Сессии & Мультисервер", "Import/Export .session", false, R.drawable.msg2_devices, AMBER_PRIMARY, true);
                    } else if (position == otherRow) {
                        textCell.setTextAndColorfulIcon(LocaleController.getString("LocalOther", R.string.LocalOther), R.drawable.msg_fave, AMBER_PRIMARY, false);
                    } else if (position == notificationsRow) {
                        textCell.setTextAndColorfulIcon(LocaleController.getString("NotificationsAndSounds", R.string.NotificationsAndSounds), R.drawable.msg2_notifications, AMBER_PRIMARY, true);
                    } else if (position == privacyRow) {
                        textCell.setTextAndColorfulIcon(LocaleController.getString("PrivacySettings", R.string.PrivacySettings), R.drawable.msg2_secret, AMBER_PRIMARY, true);
                    } else if (position == storageRow) {
                        textCell.setTextAndColorfulIcon(LocaleController.getString("DataSettings", R.string.DataSettings), R.drawable.msg2_data, AMBER_PRIMARY, true);
                    } else if (position == foldersRow) {
                        textCell.setTextAndColorfulIcon(LocaleController.getString("Filters", R.string.Filters), R.drawable.msg2_folder, AMBER_PRIMARY, true);
                    } else if (position == languageRow) {
                        textCell.setTextAndValueAndColorfulIcon(LocaleController.getString("Language", R.string.Language), LocaleController.getCurrentLanguageName(), false, R.drawable.msg2_language, AMBER_PRIMARY, false);
                    } else if (position == githubRow) {
                        textCell.setTextAndValueAndColorfulIcon("GitHub Репозиторий", "nikeproo2/honeygram", false, R.drawable.msg_channel, AMBER_PRIMARY, false);
                    }
                    break;
                }
                case 3: {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == heroHeaderRow) {
                        headerCell.setText("HoneyGram Pro · Google Android 15");
                    } else if (position == categoriesHeaderRow) {
                        headerCell.setText("Кастомизация & Модули");
                    } else if (position == systemHeaderRow) {
                        headerCell.setText("Системные Настройки Pixel");
                    } else if (position == aboutHeaderRow) {
                        headerCell.setText("О моде HoneyGram");
                    }
                    break;
                }
                case 4: {
                    headerSettingsCell = (HeaderSettingsCell) holder.itemView;
                    headerSettingsCell.setPadding(0, ActionBar.getCurrentActionBarHeight() + (actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) - AndroidUtilities.dp(40), 0, 0);
                    break;
                }
                case 5: {
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (position == honeyBadgesRow) {
                        checkCell.setTextAndCheck("Отображать бейджи Honey 🍯", HoneyConfig.isShowHoneyBadges(), true);
                    }
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == heroDividerRow || position == categoriesDividerRow || position == systemDividerRow || position == infoDividerRow) {
                return 1;
            } else if (position == heroHeaderRow || position == categoriesHeaderRow || position == systemHeaderRow || position == aboutHeaderRow) {
                return 3;
            } else if (position == headerRow) {
                return 4;
            } else if (position == honeyBadgesRow) {
                return 5;
            }
            return 2;
        }
    }
}