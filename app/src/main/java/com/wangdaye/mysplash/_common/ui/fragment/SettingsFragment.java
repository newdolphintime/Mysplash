package com.wangdaye.mysplash._common.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.api.PhotoApi;
import com.wangdaye.mysplash._common.ui.activity.SettingsActivity;
import com.wangdaye.mysplash._common.ui.widget.preference.MysplashListPreference;
import com.wangdaye.mysplash._common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash._common.utils.ValueUtils;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.main.view.activity.MainActivity;

/**
 * Settings fragment.
 * */

public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener, NestedScrollingChild {
    // widget
    private NestedScrollingChildHelper nestedScrollingChildHelper;
    private ListView listView;

    /** <br> life cycle. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.perference);
        initView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(android.R.id.list);
        if (listView != null) {
            listView.setOnTouchListener(new ScrollListener(getActivity()));
            nestedScrollingChildHelper = new NestedScrollingChildHelper(listView);
            nestedScrollingChildHelper.setNestedScrollingEnabled(true);
        }
    }

    /** <br> UI. */

    // init.

    private void initView() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        initBasicPart(sharedPreferences);
        initFilterPart(sharedPreferences);
        initDownloadPart(sharedPreferences);
    }

    private void initBasicPart(SharedPreferences sharedPreferences) {
        // back to top.
        MysplashListPreference backToTop = (MysplashListPreference) findPreference(getString(R.string.key_back_to_top));
        String backToTopValue = sharedPreferences.getString(getString(R.string.key_back_to_top), "all");
        String backToTopName = ValueUtils.getBackToTopName(getActivity(), backToTopValue);
        backToTop.setSummary(getString(R.string.now) + " : " + backToTopName);
        backToTop.setOnPreferenceChangeListener(this);

        // language.
        MysplashListPreference language = (MysplashListPreference) findPreference(getString(R.string.key_language));
        String languageValue = sharedPreferences.getString(getString(R.string.key_language), "follow_system");
        String languageName = ValueUtils.getLanguageName(getActivity(), languageValue);
        language.setSummary(getString(R.string.now) + " : " + languageName);
        language.setOnPreferenceChangeListener(this);
    }

    private void initFilterPart(SharedPreferences sharedPreferences) {
        // default order.
        MysplashListPreference defaultOrder = (MysplashListPreference) findPreference(getString(R.string.key_default_photo_order));
        String orderValue = sharedPreferences.getString(getString(R.string.key_default_photo_order), PhotoApi.ORDER_BY_LATEST);
        String orderName = ValueUtils.getOrderName(getActivity(), orderValue);
        defaultOrder.setSummary(getString(R.string.now) + " : " + orderName);
        defaultOrder.setOnPreferenceChangeListener(this);

        // collection type.
        MysplashListPreference collectionType = (MysplashListPreference) findPreference(getString(R.string.key_default_collection_type));
        String typeValue = sharedPreferences.getString(getString(R.string.key_default_collection_type), "featured");
        String valueName = ValueUtils.getCollectionName(getActivity(), typeValue);
        collectionType.setSummary(getString(R.string.now) + " : " + valueName);
        collectionType.setOnPreferenceChangeListener(this);
    }

    private void initDownloadPart(SharedPreferences sharedPreferences) {
        // download scale.
        MysplashListPreference downloadScale = (MysplashListPreference) findPreference(getString(R.string.key_download_scale));
        String scaleValue = sharedPreferences.getString(getString(R.string.key_download_scale), "compact");
        String scaleName = ValueUtils.getScaleName(getActivity(), scaleValue);
        downloadScale.setSummary(getString(R.string.now) + " : " + scaleName);
        downloadScale.setOnPreferenceChangeListener(this);
    }

    // interface.

    private void showRebootSnackbar() {
        NotificationHelper.showActionSnackbar(
                getString(R.string.feedback_notify_restart),
                getString(R.string.restart),
                Snackbar.LENGTH_SHORT,
                rebootListener);
    }

    @Nullable
    public ListView getScrolledView() {
        if (listView != null) {
            return listView;
        } else {
            return null;
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().equals(getString(R.string.key_custom_api_key))) {
            IntentHelper.startCustomApiActivity((SettingsActivity) getActivity());
        }
        return true;
    }

    /** <br> interface. */

    // on preference_widget changed swipeListener.

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference.getKey().equals(getString(R.string.key_back_to_top))) {
            // back to top.
            Mysplash.getInstance().setBackToTopType((String) o);
            String backType = ValueUtils.getBackToTopName(getActivity(), (String) o);
            preference.setSummary(getString(R.string.now) + " : " + backType);
        } else if (preference.getKey().equals(getString(R.string.key_language))) {
            // language.
            Mysplash.getInstance().setLanguage((String) o);
            String language = ValueUtils.getLanguageName(getActivity(), (String) o);
            preference.setSummary(getString(R.string.now) + " : " + language);
            showRebootSnackbar();
        } else if (preference.getKey().equals(getString(R.string.key_default_photo_order))) {
            // default order.
            Mysplash.getInstance().setDefaultPhotoOrder((String) o);
            String order = ValueUtils.getOrderName(getActivity(), (String) o);
            preference.setSummary(getString(R.string.now) + " : " + order);
            showRebootSnackbar();
        } else if (preference.getKey().equals(getString(R.string.key_default_collection_type))) {
            // collection type.
            Mysplash.getInstance().setDefaultCollectionType((String) o);
            String type = ValueUtils.getCollectionName(getActivity(), (String) o);
            preference.setSummary(getString(R.string.now) + " : " + type);
            showRebootSnackbar();
        } else if (preference.getKey().equals(getString(R.string.key_download_scale))) {
            // download scale.
            Mysplash.getInstance().setDownloadScale((String) o);
            String scale = ValueUtils.getScaleName(getActivity(), (String) o);
            preference.setSummary(getString(R.string.now) + " : " + scale);
        }
        return true;
    }

    // on action click swipeListener.

    private View.OnClickListener rebootListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MainActivity a = Mysplash.getInstance().getMainActivity();
            if (a != null) {
                a.reboot();
            }
        }
    };

    // on touch swipeListener.

    private class ScrollListener implements View.OnTouchListener {
        // data
        private float oldY;
        private boolean isBeingDragged;
        private float touchSlop;

        ScrollListener(Context context) {
            this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        }

        // interface.

        @Override
        public boolean onTouch(View v, MotionEvent ev) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                    oldY = ev.getY();
                    isBeingDragged = false;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (!isBeingDragged) {
                        if (Math.abs(ev.getY() - oldY) > touchSlop) {
                            isBeingDragged = true;
                        }
                    }
                    if (isBeingDragged) {
                        int[] total = new int[] {0, (int) (oldY - ev.getY())};
                        int[] consumed = new int[] {0, 0};
                        dispatchNestedPreScroll(
                                total[0], total[1], consumed, null);
                        dispatchNestedScroll(
                                consumed[0], consumed[1], total[0] - consumed[0], total[1] - consumed[1], null);
                    }
                    oldY = ev.getY();
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    stopNestedScroll();
                    if (isBeingDragged) {
                        isBeingDragged = false;
                    }
                    break;
            }
            return false;
        }
    }

    // nested scrolling child.

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        nestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return nestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return nestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        nestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return nestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed,
                                        int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return nestedScrollingChildHelper.dispatchNestedScroll(
                dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return nestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return nestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return nestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}
