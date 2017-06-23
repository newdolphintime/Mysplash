package com.wangdaye.mysplash.main.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.i.model.MultiFilterBarModel;
import com.wangdaye.mysplash.common.i.presenter.MessageManagePresenter;
import com.wangdaye.mysplash.common.i.presenter.MultiFilterBarPresenter;
import com.wangdaye.mysplash.common.i.presenter.PopupManagePresenter;
import com.wangdaye.mysplash.common.i.view.MessageManageView;
import com.wangdaye.mysplash.common.i.view.MultiFilterBarView;
import com.wangdaye.mysplash.common.i.view.PopupManageView;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common._basic.MysplashFragment;
import com.wangdaye.mysplash.common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash.common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash.common.utils.BackToTopUtils;
import com.wangdaye.mysplash.common.utils.DisplayUtils;
import com.wangdaye.mysplash.common.utils.manager.ThemeManager;
import com.wangdaye.mysplash.common.utils.widget.SafeHandler;
import com.wangdaye.mysplash.common.utils.ValueUtils;
import com.wangdaye.mysplash.main.model.fragment.MultiFilterBarObject;
import com.wangdaye.mysplash.main.presenter.fragment.MessageManageImplementor;
import com.wangdaye.mysplash.main.presenter.fragment.MultiFilterBarImplementor;
import com.wangdaye.mysplash.main.presenter.fragment.MultiFilterFragmentPopupManageImplementor;
import com.wangdaye.mysplash.main.view.activity.MainActivity;
import com.wangdaye.mysplash.main.view.widget.MultiFilterPhotosView;
import com.wangdaye.mysplash.photo.view.activity.PhotoActivity;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Multi filter fragment.
 *
 * This fragment is used to search photos by multiple parameters.
 *
 * */

public class MultiFilterFragment extends MysplashFragment
        implements MultiFilterBarView, PopupManageView, MessageManageView,
        View.OnClickListener, EditText.OnEditorActionListener,
        NestedScrollAppBarLayout.OnNestedScrollingListener, SafeHandler.HandlerContainer,
        MultiFilterPhotosView.OnMultiFilterDataInputInterface {

    @BindView(R.id.fragment_multi_filter_statusBar)
    StatusBarView statusBar;

    @BindView(R.id.fragment_multi_filter_container)
    CoordinatorLayout container;

    @BindView(R.id.fragment_multi_filter_appBar)
    NestedScrollAppBarLayout appBar;

    @BindViews({
            R.id.fragment_multi_filter_photos_editText,
            R.id.fragment_multi_filter_users_editText})
    EditText[] editTexts;
    @BindViews({
            R.id.fragment_multi_filter_categoryTxt,
            R.id.fragment_multi_filter_orientationTxt,
            R.id.fragment_multi_filter_featuredTxt})
    TextView[] menuTexts;

    @BindViews({
            R.id.fragment_multi_filter_categoryBtn,
            R.id.fragment_multi_filter_orientationBtn,
            R.id.fragment_multi_filter_featuredBtn})
    ImageButton[] menuIcons;

    @BindView(R.id.fragment_multi_filter_photosView)
    MultiFilterPhotosView photosView;

    private SafeHandler<MultiFilterFragment> handler;

    private MultiFilterBarModel multiFilterBarModel;
    private MultiFilterBarPresenter multiFilterBarPresenter;

    private PopupManagePresenter popupManagePresenter;

    private MessageManagePresenter messageManagePresenter;

    private final String KEY_MULTI_FILTER_FRAGMENT_QUERY = "key_multi_filter_fragment_query";
    private final String KEY_MULTI_FILTER_FRAGMENT_USER = "key_multi_filter_fragment_user";
    private final String KEY_MULTI_FILTER_FRAGMENT_PHOTO_CATEGORY = "key_multi_filter_fragment_photo_category";
    private final String KEY_MULTI_FILTER_FRAGMENT_PHOTO_ORIENTATION = "key_multi_filter_fragment_photo_orientation";
    private final String KEY_MULTI_FILTER_FRAGMENT_PHOTO_TYPE = "key_multi_filter_fragment_photo_type";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multi_filter, container, false);
        ButterKnife.bind(this, view);
        initModel();
        initPresenter(savedInstanceState);
        initView(view);
        messageManagePresenter.sendMessage(1, null);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        multiFilterBarPresenter.hideKeyboard();
        photosView.cancelRequest();
    }

    @Override
    public boolean needSetOnlyWhiteStatusBarText() {
        return appBar.getY() <= -appBar.getMeasuredHeight();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            hideKeyboard();
        } else {
            showKeyboard();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_MULTI_FILTER_FRAGMENT_QUERY, editTexts[0].getText().toString());
        outState.putString(KEY_MULTI_FILTER_FRAGMENT_USER, editTexts[1].getText().toString());
        outState.putInt(KEY_MULTI_FILTER_FRAGMENT_PHOTO_CATEGORY, multiFilterBarPresenter.getCategory());
        outState.putString(KEY_MULTI_FILTER_FRAGMENT_PHOTO_ORIENTATION, multiFilterBarPresenter.getOrientation());
        outState.putBoolean(KEY_MULTI_FILTER_FRAGMENT_PHOTO_TYPE, multiFilterBarPresenter.isFeatured());
    }

    @Override
    public void writeLargeData(MysplashActivity.BaseSavedStateFragment outState) {
        if (photosView != null) {
            ((MainActivity.SavedStateFragment) outState).setMultiFilterList(photosView.getPhotos());
        }
    }

    @Override
    public void readLargeData(MysplashActivity.BaseSavedStateFragment savedInstanceState) {
        if (photosView != null) {
            photosView.setPhotos(((MainActivity.SavedStateFragment) savedInstanceState).getMultiFilterList());
        }
    }

    @Override
    public boolean needBackToTop() {
        return photosView.needPagerBackToTop();
    }

    @Override
    public void backToTop() {
        statusBar.animToInitAlpha();
        setStatusBarStyle(false);
        BackToTopUtils.showTopBar(appBar, photosView);
        photosView.pagerScrollToTop();
    }

    @Override
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Mysplash.PHOTO_ACTIVITY) {
            Photo photo = data.getParcelableExtra(PhotoActivity.KEY_PHOTO_ACTIVITY_PHOTO);
            if (photo != null) {
                photosView.updatePhoto(photo);
            }
        }
    }

    @Override
    public CoordinatorLayout getSnackbarContainer() {
        return container;
    }

    // init.

    private void initModel() {
        this.multiFilterBarModel = new MultiFilterBarObject();
    }

    private void initView(View v) {
        this.handler = new SafeHandler<>(this);

        appBar.setOnNestedScrollingListener(this);

        Toolbar toolbar = ButterKnife.findById(v, R.id.fragment_multi_filter_toolbar);
        toolbar.setTitle(getString(R.string.action_multi_filter));
        ThemeManager.setNavigationIcon(
                toolbar, R.drawable.ic_toolbar_menu_light, R.drawable.ic_toolbar_menu_dark);
        toolbar.setNavigationOnClickListener(this);

        editTexts[0].setText(multiFilterBarPresenter.getQuery());
        editTexts[0].setOnEditorActionListener(this);
        editTexts[1].setText(multiFilterBarPresenter.getUsername());
        editTexts[1].setOnEditorActionListener(this);
        DisplayUtils.setTypeface(getActivity(), editTexts[0]);
        DisplayUtils.setTypeface(getActivity(), editTexts[1]);

        editTexts[0].setFocusable(true);
        editTexts[0].requestFocus();

        ImageButton searchBtn = ButterKnife.findById(v, R.id.fragment_multi_filter_searchBtn);
        ThemeManager.setImageResource(
                searchBtn, R.drawable.ic_toolbar_search_light, R.drawable.ic_toolbar_search_dark);

        for (TextView t : menuTexts) {
            DisplayUtils.setTypeface(getActivity(), t);
        }
        responsePopup(String.valueOf(multiFilterBarPresenter.getCategory()), 0);
        responsePopup(String.valueOf(multiFilterBarPresenter.getOrientation()), 1);
        responsePopup(String.valueOf(multiFilterBarPresenter.isFeatured()), 2);

        for (ImageButton b : menuIcons) {
            ThemeManager.setImageResource(
                    b, R.drawable.ic_menu_down_light, R.drawable.ic_menu_down_dark);
        }

        photosView.setActivity((MainActivity) getActivity());
        photosView.setOnMultiFilterDataInputInterface(this);
        photosView.setClickListenerForFeedbackView(hideKeyboardListener);
    }

    private void initPresenter(Bundle saveInstanceState) {
        this.multiFilterBarPresenter = new MultiFilterBarImplementor(multiFilterBarModel, this);
        if (saveInstanceState != null) {
            multiFilterBarPresenter.setQuery(saveInstanceState.getString(KEY_MULTI_FILTER_FRAGMENT_QUERY, ""));
            multiFilterBarPresenter.setUsername(saveInstanceState.getString(KEY_MULTI_FILTER_FRAGMENT_USER, ""));
            multiFilterBarPresenter.setCategory(saveInstanceState.getInt(KEY_MULTI_FILTER_FRAGMENT_PHOTO_CATEGORY, 0));
            multiFilterBarPresenter.setOrientation(saveInstanceState.getString(KEY_MULTI_FILTER_FRAGMENT_PHOTO_ORIENTATION, ""));
            multiFilterBarPresenter.setFeatured(saveInstanceState.getBoolean(KEY_MULTI_FILTER_FRAGMENT_PHOTO_TYPE, false));
        }
        this.popupManagePresenter = new MultiFilterFragmentPopupManageImplementor(this);
        this.messageManagePresenter = new MessageManageImplementor(this);
    }

    // control.

    public void showPopup(int position) {
        switch (position) {
            case 0:
                popupManagePresenter.showPopup(
                        getActivity(),
                        menuIcons[0],
                        String.valueOf(multiFilterBarPresenter.getCategory()),
                        0);
                break;

            case 1:
                popupManagePresenter.showPopup(
                        getActivity(),
                        menuIcons[1],
                        multiFilterBarPresenter.getOrientation(),
                        1);
                break;

            case 2:
                popupManagePresenter.showPopup(
                        getActivity(),
                        menuIcons[2],
                        String.valueOf(multiFilterBarPresenter.isFeatured()),
                        2);
                break;
        }
    }

    // interface.

    // on click listener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                multiFilterBarPresenter.touchNavigatorIcon();
                break;
        }
    }

    private View.OnClickListener hideKeyboardListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            multiFilterBarPresenter.hideKeyboard();
        }
    };

    @OnClick(R.id.fragment_multi_filter_toolbar) void clickToolbar() {
        multiFilterBarPresenter.touchToolbar((MysplashActivity) getActivity());
    }

    @OnClick(R.id.fragment_multi_filter_searchBtn) void clickSearchButton() {
        multiFilterBarPresenter.touchSearchButton();
    }

    @OnClick({
            R.id.fragment_multi_filter_categoryBtn,
            R.id.fragment_multi_filter_categoryContainer}) void showCategoryList() {
        showPopup(0);
    }

    @OnClick({
            R.id.fragment_multi_filter_orientationBtn,
            R.id.fragment_multi_filter_orientationContainer}) void showOrientationList() {
        showPopup(1);
    }

    @OnClick({
            R.id.fragment_multi_filter_featuredBtn,
            R.id.fragment_multi_filter_featuredContainer}) void showFeaturedList() {
        showPopup(2);
    }

    // on editor action listener.

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        multiFilterBarPresenter.setQuery(editTexts[0].getText().toString());
        multiFilterBarPresenter.setUsername(editTexts[1].getText().toString());
        multiFilterBarPresenter.hideKeyboard();
        multiFilterBarPresenter.submitSearchInfo();
        return true;
    }

    // on multi-filter data input interface.

    @Override
    public String onQueryInput() {
        multiFilterBarPresenter.setQuery(editTexts[0].getText().toString());
        return multiFilterBarPresenter.getQuery();
    }

    @Override
    public String onUsernameInput() {
        multiFilterBarPresenter.setUsername(editTexts[1].getText().toString());
        return multiFilterBarPresenter.getUsername();
    }

    @Override
    public int onCategoryInput() {
        return multiFilterBarPresenter.getCategory();
    }

    @Override
    public String onOrientationInput() {
        return multiFilterBarPresenter.getOrientation();
    }

    @Override
    public boolean onFeaturedInput() {
        return multiFilterBarPresenter.isFeatured();
    }

    // on nested scrolling listener.

    @Override
    public void onStartNestedScroll() {
        // do nothing.
    }

    @Override
    public void onNestedScrolling() {
        if (needSetOnlyWhiteStatusBarText()) {
            if (statusBar.isInitState()) {
                statusBar.animToDarkerAlpha();
                setStatusBarStyle(true);
            }
        } else {
            if (!statusBar.isInitState()) {
                statusBar.animToInitAlpha();
                setStatusBarStyle(false);
            }
        }
    }

    @Override
    public void onStopNestedScroll() {
        // do nothing.
    }

    // handler container.

    @Override
    public void handleMessage(Message message) {
        messageManagePresenter.responseMessage(message.what, message.obj);
    }

    // view.

    // multi-filter view.

    @Override
    public void touchNavigationIcon() {
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.activity_main_drawerLayout);
        drawer.openDrawer(GravityCompat.START);
    }

    @Override
    public void touchSearchButton() {
        multiFilterBarPresenter.setQuery(editTexts[0].getText().toString());
        multiFilterBarPresenter.setUsername(editTexts[1].getText().toString());
        multiFilterBarPresenter.submitSearchInfo();
    }

    @Override
    public void touchMenuContainer(int position) {
        showPopup(position);
    }

    @Override
    public void showKeyboard() {
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.showSoftInput(editTexts[0], 0);
    }

    @Override
    public void hideKeyboard() {
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(editTexts[0].getWindowToken(), 0);
        manager.hideSoftInputFromWindow(editTexts[1].getWindowToken(), 0);
    }

    @Override
    public void submitSearchInfo() {
        multiFilterBarPresenter.hideKeyboard();
        photosView.doSearch(
                multiFilterBarPresenter.getCategory(),
                multiFilterBarPresenter.isFeatured(),
                multiFilterBarPresenter.getUsername(),
                multiFilterBarPresenter.getQuery(),
                multiFilterBarPresenter.getOrientation());
    }

    // popup manage view.

    @Override
    public void responsePopup(String value, int position) {
        switch (position) {
            case 0:
                multiFilterBarPresenter.setCategory(Integer.parseInt(value));
                if (Integer.parseInt(value) == 0) {
                    menuTexts[0].setText(R.string.all);
                } else {
                    menuTexts[0].setText(
                            ValueUtils.getToolbarTitleByCategory(
                                    getContext(),
                                    Integer.parseInt(value)));
                }
                break;

            case 1:
                multiFilterBarPresenter.setOrientation(value);
                if (TextUtils.isEmpty(value)) {
                    menuTexts[1].setText(R.string.all);
                } else {
                    menuTexts[1].setText(value);
                }
                break;

            case 2:
                multiFilterBarPresenter.setFeatured(Boolean.parseBoolean(value));
                if (Boolean.parseBoolean(value)) {
                    menuTexts[2].setText(getResources().getStringArray(R.array.collection_types)[2]);
                } else {
                    menuTexts[2].setText(R.string.all);
                }
                break;
        }
    }

    // message manage view.

    @Override
    public void sendMessage(final int what, final Object o) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.obtainMessage(what, o).sendToTarget();
            }
        }, 400);
    }

    @Override
    public void responseMessage(int what, Object o) {
        switch (what) {
            case 1:
                showKeyboard();
                editTexts[0].clearFocus();
                break;
        }
    }
}
