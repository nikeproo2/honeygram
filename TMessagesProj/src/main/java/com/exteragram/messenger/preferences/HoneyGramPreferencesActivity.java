package com.exteragram.messenger.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.honeygram.HoneyConfig;
import org.telegram.honeygram.auth.ServerManager;
import org.telegram.honeygram.auth.SessionManager;
import org.telegram.honeygram.network.WebProxyManager;
import org.telegram.honeygram.tools.KeywordWatcher;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

/**
 * HoneyGramPreferencesActivity - Full Material 3 settings hub for HoneyGram.
 */
public class HoneyGramPreferencesActivity extends BasePreferencesActivity {

    private ListAdapter listAdapter;
    private RecyclerListView listView;

    private int rowCount;
    // Multi-Server & Sessions
    private int serverHeaderRow;
    private int serverEnvironmentRow;
    private int importSessionRow;
    private int exportSessionRow;
    private int serverSectionRow;

    // Ghost & Privacy
    private int privacyHeaderRow;
    private int ghostModeRow;
    private int dontSendReadRow;
    private int dontSendTypingRow;
    private int hideStoriesSeenRow;
    private int privacySectionRow;

    // Bypass
    private int bypassHeaderRow;
    private int bypassRestrictedRow;
    private int allowScreenshotsRow;
    private int bypassDisappearingTimerRow;
    private int bypassSectionRow;

    // Network & WebProxy
    private int networkHeaderRow;
    private int webProxyModeRow;
    private int networkSectionRow;

    // AI & Auto-Responder
    private int aiHeaderRow;
    private int aiAutoResponderRow;
    private int aiBaseUrlRow;
    private int aiApiKeyRow;
    private int aiModelNameRow;
    private int aiSystemPromptRow;
    private int aiSectionRow;

    // Power Tools & Visuals
    private int toolsHeaderRow;
    private int panicPinRow;
    private int turboDownloaderRow;
    private int visualPremiumRow;
    private int visualStarsRow;
    private int showHoneyBadgesRow;
    private int toolsSectionRow;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        return true;
    }

    private void updateRows() {
        rowCount = 0;

        serverHeaderRow = rowCount++;
        serverEnvironmentRow = rowCount++;
        importSessionRow = rowCount++;
        exportSessionRow = rowCount++;
        serverSectionRow = rowCount++;

        privacyHeaderRow = rowCount++;
        ghostModeRow = rowCount++;
        dontSendReadRow = rowCount++;
        dontSendTypingRow = rowCount++;
        hideStoriesSeenRow = rowCount++;
        privacySectionRow = rowCount++;

        bypassHeaderRow = rowCount++;
        bypassRestrictedRow = rowCount++;
        allowScreenshotsRow = rowCount++;
        bypassDisappearingTimerRow = rowCount++;
        bypassSectionRow = rowCount++;

        networkHeaderRow = rowCount++;
        webProxyModeRow = rowCount++;
        networkSectionRow = rowCount++;

        aiHeaderRow = rowCount++;
        aiAutoResponderRow = rowCount++;
        aiBaseUrlRow = rowCount++;
        aiApiKeyRow = rowCount++;
        aiModelNameRow = rowCount++;
        aiSystemPromptRow = rowCount++;
        aiSectionRow = rowCount++;

        toolsHeaderRow = rowCount++;
        panicPinRow = rowCount++;
        turboDownloaderRow = rowCount++;
        visualPremiumRow = rowCount++;
        visualStarsRow = rowCount++;
        showHoneyBadgesRow = rowCount++;
        toolsSectionRow = rowCount++;
    }

    @Override
    protected int getTitleRes() {
        return R.string.HoneyGramSettings;
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(LocaleController.getString("HoneyGramSettings", R.string.HoneyGramSettings));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) finishFragment();
            }
        });

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(listAdapter = new ListAdapter(context));
        listView.setOnItemClickListener((view, position) -> {
            SharedPreferences prefs = HoneyConfig.getPrefs();

            if (position == ghostModeRow) {
                boolean val = !HoneyConfig.isGhostMode();
                HoneyConfig.setGhostMode(val);
                if (view instanceof TextCheckCell) ((TextCheckCell) view).setChecked(val);
                listAdapter.notifyDataSetChanged();
            } else if (position == dontSendReadRow) {
                boolean val = !prefs.getBoolean(HoneyConfig.KEY_DONT_SEND_READ, false);
                prefs.edit().putBoolean(HoneyConfig.KEY_DONT_SEND_READ, val).apply();
                if (view instanceof TextCheckCell) ((TextCheckCell) view).setChecked(val);
            } else if (position == dontSendTypingRow) {
                boolean val = !prefs.getBoolean(HoneyConfig.KEY_DONT_SEND_TYPING, false);
                prefs.edit().putBoolean(HoneyConfig.KEY_DONT_SEND_TYPING, val).apply();
                if (view instanceof TextCheckCell) ((TextCheckCell) view).setChecked(val);
            } else if (position == hideStoriesSeenRow) {
                boolean val = !prefs.getBoolean(HoneyConfig.KEY_HIDE_STORIES_SEEN, false);
                prefs.edit().putBoolean(HoneyConfig.KEY_HIDE_STORIES_SEEN, val).apply();
                if (view instanceof TextCheckCell) ((TextCheckCell) view).setChecked(val);
            } else if (position == bypassRestrictedRow) {
                boolean val = !HoneyConfig.isBypassRestrictedContent();
                prefs.edit().putBoolean(HoneyConfig.KEY_BYPASS_RESTRICTED_CONTENT, val).apply();
                if (view instanceof TextCheckCell) ((TextCheckCell) view).setChecked(val);
            } else if (position == allowScreenshotsRow) {
                boolean val = !HoneyConfig.isAllowScreenshots();
                prefs.edit().putBoolean(HoneyConfig.KEY_ALLOW_SCREENSHOTS, val).apply();
                if (view instanceof TextCheckCell) ((TextCheckCell) view).setChecked(val);
            } else if (position == bypassDisappearingTimerRow) {
                boolean val = !HoneyConfig.isBypassDisappearingTimer();
                prefs.edit().putBoolean(HoneyConfig.KEY_BYPASS_DISAPPEARING_TIMER, val).apply();
                if (view instanceof TextCheckCell) ((TextCheckCell) view).setChecked(val);
            } else if (position == aiAutoResponderRow) {
                boolean val = !HoneyConfig.isAiAutoResponderEnabled();
                prefs.edit().putBoolean(HoneyConfig.KEY_AI_AUTO_RESPONDER_ENABLED, val).apply();
                if (view instanceof TextCheckCell) ((TextCheckCell) view).setChecked(val);
            } else if (position == turboDownloaderRow) {
                boolean val = !HoneyConfig.isTurboDownloaderEnabled();
                prefs.edit().putBoolean(HoneyConfig.KEY_TURBO_DOWNLOADER, val).apply();
                if (view instanceof TextCheckCell) ((TextCheckCell) view).setChecked(val);
            } else if (position == visualPremiumRow) {
                boolean val = !HoneyConfig.isVisualPremium();
                prefs.edit().putBoolean(HoneyConfig.KEY_VISUAL_PREMIUM, val).apply();
                if (view instanceof TextCheckCell) ((TextCheckCell) view).setChecked(val);
            } else if (position == showHoneyBadgesRow) {
                boolean val = !HoneyConfig.isShowHoneyBadges();
                prefs.edit().putBoolean(HoneyConfig.KEY_SHOW_HONEY_BADGES, val).apply();
                if (view instanceof TextCheckCell) ((TextCheckCell) view).setChecked(val);
            } else if (position == serverEnvironmentRow) {
                showServerSelectorDialog();
            } else if (position == webProxyModeRow) {
                showWebProxySelectorDialog();
            } else if (position == aiApiKeyRow) {
                showEditTextDialog(LocaleController.getString("AIApiKey", R.string.AIApiKey), HoneyConfig.KEY_AI_API_KEY, HoneyConfig.getAiApiKey());
            } else if (position == aiBaseUrlRow) {
                showEditTextDialog(LocaleController.getString("AIBaseUrl", R.string.AIBaseUrl), HoneyConfig.KEY_AI_BASE_URL, HoneyConfig.getAiBaseUrl());
            } else if (position == aiModelNameRow) {
                showEditTextDialog(LocaleController.getString("AIModelName", R.string.AIModelName), HoneyConfig.KEY_AI_MODEL_NAME, HoneyConfig.getAiModelName());
            } else if (position == panicPinRow) {
                showEditTextDialog(LocaleController.getString("PanicPinTitle", R.string.PanicPinTitle), HoneyConfig.KEY_PANIC_PIN, HoneyConfig.getPanicPin());
            } else if (position == visualStarsRow) {
                showEditTextDialog(LocaleController.getString("VisualStars", R.string.VisualStars), HoneyConfig.KEY_VISUAL_STARS_COUNT, String.valueOf(HoneyConfig.getVisualStarsCount()));
            }
        });

        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        return fragmentView;
    }

    private void showServerSelectorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("ServerEnvironment", R.string.ServerEnvironment));
        String[] items = new String[]{
                LocaleController.getString("ServerProd", R.string.ServerProd),
                LocaleController.getString("ServerTest", R.string.ServerTest),
                LocaleController.getString("ServerCustom", R.string.ServerCustom)
        };
        builder.setItems(items, (dialog, which) -> {
            ServerManager.switchEnvironment(getParentActivity(), which);
            listAdapter.notifyDataSetChanged();
            Toast.makeText(getParentActivity(), items[which], Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }

    private void showWebProxySelectorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("WebProxyBypass", R.string.WebProxyBypass));
        String[] items = new String[]{
                LocaleController.getString("WebProxyModeAuto", R.string.WebProxyModeAuto),
                LocaleController.getString("WebProxyModeAlways", R.string.WebProxyModeAlways),
                LocaleController.getString("WebProxyModeDisabled", R.string.WebProxyModeDisabled)
        };
        builder.setItems(items, (dialog, which) -> {
            HoneyConfig.setWebProxyMode(which);
            listAdapter.notifyDataSetChanged();
        });
        builder.show();
    }

    private void showEditTextDialog(String title, String prefKey, String currentValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(title);
        final EditTextBoldCursor editText = new EditTextBoldCursor(getParentActivity());
        editText.setText(currentValue);
        builder.setView(editText);
        builder.setPositiveButton("OK", (dialog, which) -> {
            HoneyConfig.getPrefs().edit().putString(prefKey, editText.getText().toString().trim()).apply();
            listAdapter.notifyDataSetChanged();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private final Context context;

        public ListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0 || type == 1 || type == 2;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0: // TextCheckCell
                    view = new TextCheckCell(context);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1: // TextDetailSettingsCell
                    view = new TextDetailSettingsCell(context);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2: // TextCell
                    view = new TextCell(context);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3: // HeaderCell
                    view = new HeaderCell(context);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4: // ShadowSectionCell
                default:
                    view = new ShadowSectionCell(context);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0: { // TextCheckCell
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (position == ghostModeRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("GhostMode", R.string.GhostMode), HoneyConfig.isGhostMode(), true);
                    } else if (position == dontSendReadRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("DontSendRead", R.string.DontSendRead), HoneyConfig.isDontSendRead(), true);
                    } else if (position == dontSendTypingRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("DontSendTyping", R.string.DontSendTyping), HoneyConfig.isDontSendTyping(), true);
                    } else if (position == hideStoriesSeenRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("HideStoriesSeen", R.string.HideStoriesSeen), HoneyConfig.isHideStoriesSeen(), false);
                    } else if (position == bypassRestrictedRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("BypassRestrictions", R.string.BypassRestrictions), HoneyConfig.isBypassRestrictedContent(), true);
                    } else if (position == allowScreenshotsRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("AllowScreenshots", R.string.AllowScreenshots), HoneyConfig.isAllowScreenshots(), true);
                    } else if (position == bypassDisappearingTimerRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("BypassDisappearingTimer", R.string.BypassDisappearingTimer), HoneyConfig.isBypassDisappearingTimer(), false);
                    } else if (position == aiAutoResponderRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("AIAutoResponder", R.string.AIAutoResponder), HoneyConfig.isAiAutoResponderEnabled(), true);
                    } else if (position == turboDownloaderRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("TurboDownloader", R.string.TurboDownloader), HoneyConfig.isTurboDownloaderEnabled(), false);
                    } else if (position == visualPremiumRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("VisualPremium", R.string.VisualPremium), HoneyConfig.isVisualPremium(), true);
                    } else if (position == showHoneyBadgesRow) {
                        checkCell.setTextAndCheck("Show 🍯 Honey Supporter Badges", HoneyConfig.isShowHoneyBadges(), false);
                    }
                    break;
                }
                case 1: { // TextDetailSettingsCell
                    TextDetailSettingsCell detailCell = (TextDetailSettingsCell) holder.itemView;
                    if (position == serverEnvironmentRow) {
                        detailCell.setTextAndValue(LocaleController.getString("ServerEnvironment", R.string.ServerEnvironment), ServerManager.getCurrentEnvironment().name, true);
                    } else if (position == webProxyModeRow) {
                        detailCell.setTextAndValue(LocaleController.getString("WebProxyBypass", R.string.WebProxyBypass), "Auto Fallback", true);
                    } else if (position == aiBaseUrlRow) {
                        detailCell.setTextAndValue(LocaleController.getString("AIBaseUrl", R.string.AIBaseUrl), HoneyConfig.getAiBaseUrl(), true);
                    } else if (position == aiApiKeyRow) {
                        String key = HoneyConfig.getAiApiKey();
                        detailCell.setTextAndValue(LocaleController.getString("AIApiKey", R.string.AIApiKey), key.isEmpty() ? "Not Set" : "••••••••", true);
                    } else if (position == aiModelNameRow) {
                        detailCell.setTextAndValue(LocaleController.getString("AIModelName", R.string.AIModelName), HoneyConfig.getAiModelName(), true);
                    } else if (position == panicPinRow) {
                        String pin = HoneyConfig.getPanicPin();
                        detailCell.setTextAndValue(LocaleController.getString("PanicPinTitle", R.string.PanicPinTitle), pin.isEmpty() ? "Disabled" : "••••", true);
                    } else if (position == visualStarsRow) {
                        detailCell.setTextAndValue(LocaleController.getString("VisualStars", R.string.VisualStars), String.valueOf(HoneyConfig.getVisualStarsCount()), true);
                    }
                    break;
                }
                case 2: { // TextCell
                    TextCell textCell = (TextCell) holder.itemView;
                    if (position == importSessionRow) {
                        textCell.setText(LocaleController.getString("ImportSession", R.string.ImportSession), true);
                    } else if (position == exportSessionRow) {
                        textCell.setText(LocaleController.getString("ExportSession", R.string.ExportSession), false);
                    }
                    break;
                }
                case 3: { // HeaderCell
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == serverHeaderRow) {
                        headerCell.setText(LocaleController.getString("ServerEnvironment", R.string.ServerEnvironment));
                    } else if (position == privacyHeaderRow) {
                        headerCell.setText(LocaleController.getString("GhostMode", R.string.GhostMode));
                    } else if (position == bypassHeaderRow) {
                        headerCell.setText(LocaleController.getString("BypassRestrictions", R.string.BypassRestrictions));
                    } else if (position == networkHeaderRow) {
                        headerCell.setText(LocaleController.getString("WebProxyBypass", R.string.WebProxyBypass));
                    } else if (position == aiHeaderRow) {
                        headerCell.setText(LocaleController.getString("AIAutoResponder", R.string.AIAutoResponder));
                    } else if (position == toolsHeaderRow) {
                        headerCell.setText("Power Tools & Visuals");
                    }
                    break;
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == serverHeaderRow || position == privacyHeaderRow || position == bypassHeaderRow ||
                    position == networkHeaderRow || position == aiHeaderRow || position == toolsHeaderRow) {
                return 3;
            }
            if (position == serverSectionRow || position == privacySectionRow || position == bypassSectionRow ||
                    position == networkSectionRow || position == aiSectionRow || position == toolsSectionRow) {
                return 4;
            }
            if (position == importSessionRow || position == exportSessionRow) {
                return 2;
            }
            if (position == serverEnvironmentRow || position == webProxyModeRow || position == aiBaseUrlRow ||
                    position == aiApiKeyRow || position == aiModelNameRow || position == panicPinRow || position == visualStarsRow) {
                return 1;
            }
            return 0;
        }
    }
}
