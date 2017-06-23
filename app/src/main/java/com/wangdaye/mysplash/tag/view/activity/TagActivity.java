package com.wangdaye.mysplash.tag.view.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.i.model.DownloadModel;
import com.wangdaye.mysplash._common.i.presenter.DownloadPresenter;
import com.wangdaye.mysplash._common.i.presenter.SwipeBackManagePresenter;
import com.wangdaye.mysplash._common.i.presenter.ToolbarPresenter;
import com.wangdaye.mysplash._common.i.view.SwipeBackManageView;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash._common.ui.widget.SwipeBackCoordinatorLayout;
import com.wangdaye.mysplash._common.ui.widget.coordinatorView.StatusBarView;
import com.wangdaye.mysplash._common.ui.widget.nestedScrollView.NestedScrollAppBarLayout;
import com.wangdaye.mysplash._common.utils.BackToTopUtils;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.helper.NotificationHelper;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash.tag.model.activity.DownloadObject;
import com.wangdaye.mysplash.tag.presenter.activity.DownloadImplementor;
import com.wangdaye.mysplash.tag.presenter.activity.SwipeBackManageImplementor;
import com.wangdaye.mysplash.tag.presenter.activity.ToolbarImplementor;
import com.wangdaye.mysplash.tag.view.widget.TagPhotosView;

import java.util.List;

/**
 * Category activity.
 * */

public class TagActivity extends MysplashActivity
        implements SwipeBackManageView,
        View.OnClickListener, PhotoAdapter.OnDownloadPhotoListener,
        NestedScrollAppBarLayout.OnNestedScrollingListener, SwipeBackCoordinatorLayout.OnSwipeListener {
    // model.
    private DownloadModel downloadModel;

    // view.
    private CoordinatorLayout container;
    private StatusBarView statusBar;

    private NestedScrollAppBarLayout appBar;
    private TagPhotosView photosView;

    // presenter.
    private ToolbarPresenter toolbarPresenter;
    private DownloadPresenter downloadPresenter;
    private SwipeBackManagePresenter swipeBackManagePresenter;

    //data
    public static final String KEY_TAG_ACTIVITY_TAG = "tag_activity_tag";

    /** <br> life cycle. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        initModel();
        initPresenter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isStarted()) {
            setStarted();
            initView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (photosView != null) {
            photosView.cancelRequest();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // save large data.
        SavedStateFragment f = new SavedStateFragment();
        if (photosView != null) {
            f.setPhotoList(photosView.getPhotos());
        }
        f.saveData(this);

        // save normal data.
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void setTheme() {
        if (Mysplash.getInstance().isLightTheme()) {
            setTheme(R.style.MysplashTheme_light_Translucent_Common);
        } else {
            setTheme(R.style.MysplashTheme_dark_Translucent_Common);
        }
    }

    @Override
    protected void backToTop() {
        statusBar.animToInitAlpha();
        DisplayUtils.setStatusBarStyle(this, false);
        BackToTopUtils.showTopBar(appBar, photosView);
        photosView.scrollToTop();
    }

    @Override
    protected boolean isFullScreen() {
        return true;
    }

    @Override
    public void finishActivity(int dir) {
        finish();
        overridePendingTransition(0, R.anim.activity_slide_out_bottom);
    }

    @Override
    public void handleBackPressed() {
        if (photosView.needPagerBackToTop()
                && BackToTopUtils.isSetBackToTop(false)) {
            backToTop();
        } else {
            finishActivity(SwipeBackCoordinatorLayout.DOWN_DIR);
        }
    }

    @Override
    public View getSnackbarContainer() {
        return container;
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.toolbarPresenter = new ToolbarImplementor();
        this.downloadPresenter = new DownloadImplementor(downloadModel);
        this.swipeBackManagePresenter = new SwipeBackManageImplementor(this);
    }

    /** <br> view. */

    // init.

    private void initView() {
        this.container = (CoordinatorLayout) findViewById(R.id.activity_tag_container);

        this.statusBar = (StatusBarView) findViewById(R.id.activity_tag_statusBar);
        if (getBundle() ==null) {
            statusBar.setInitMaskAlpha();
        }

        SwipeBackCoordinatorLayout swipeBackView
                = (SwipeBackCoordinatorLayout) findViewById(R.id.activity_tag_swipeBackView);
        swipeBackView.setOnSwipeListener(this);

        this.appBar = (NestedScrollAppBarLayout) findViewById(R.id.activity_tag_appBar);
        appBar.setOnNestedScrollingListener(this);

        String tag = getIntent().getStringExtra(KEY_TAG_ACTIVITY_TAG).toLowerCase();
        if (TextUtils.isEmpty(tag)) {
            tag = "unsplash";
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_tag_toolbar);
        if (Mysplash.getInstance().isLightTheme()) {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_light);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_toolbar_back_dark);
        }
        toolbar.setTitle(tag.substring(0, 1).toUpperCase() + tag.substring(1));
        toolbar.setNavigationOnClickListener(this);
        toolbar.setOnClickListener(this);

        this.photosView = (TagPhotosView) findViewById(R.id.activity_tag_tagPhotosView);
        photosView.setActivity(this);
        photosView.setTag(tag);

        BaseSavedStateFragment f = SavedStateFragment.getData(this);
        if (f != null && f instanceof SavedStateFragment) {
            photosView.setPhotos(((SavedStateFragment) f).getPhotoList());
        } else {
            photosView.initRefresh();
        }
    }

    // interface.

    public void touchToolbar() {
        backToTop();
    }

    /** <br> model. */

    // init.

    private void initModel() {
        this.downloadModel = new DownloadObject();
    }

    /** <br> permission. */

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission(int permissionCode, int type) {
        switch (permissionCode) {
            case Mysplash.WRITE_EXTERNAL_STORAGE:
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(
                            new String[] {
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            type);
                } else {
                    downloadPresenter.download(this);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permission, grantResult);
        for (int i = 0; i < permission.length; i ++) {
            switch (permission[i]) {
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    if (grantResult[i] == PackageManager.PERMISSION_GRANTED) {
                        downloadPresenter.download(this);
                    } else {
                        NotificationHelper.showSnackbar(
                                getString(R.string.feedback_need_permission),
                                Snackbar.LENGTH_SHORT);
                    }
                    break;
            }
        }
    }

    /** <br> interface. */

    // on click swipeListener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case -1:
                toolbarPresenter.touchNavigatorIcon(this);
                break;

            case R.id.activity_tag_toolbar:
                toolbarPresenter.touchToolbar(this);
                break;
        }
    }

    // on download photo swipeListener. (photo adapter)

    @Override
    public void onDownload(Photo photo) {
        downloadPresenter.setDownloadKey(photo);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            downloadPresenter.download(this);
        } else {
            requestPermission(Mysplash.WRITE_EXTERNAL_STORAGE, DownloadHelper.DOWNLOAD_TYPE);
        }
    }

    // on nested scrolling swipeListener.

    @Override
    public void onStartNestedScroll() {
        // do nothing.
    }

    @Override
    public void onNestedScrolling() {
        if (appBar.getY() > -appBar.getMeasuredHeight()) {
            if (!statusBar.isInitAlpha()) {
                statusBar.animToInitAlpha();
                DisplayUtils.setStatusBarStyle(this, false);
            }
        } else {
            if (statusBar.isInitAlpha()) {
                statusBar.animToDarkerAlpha();
                DisplayUtils.setStatusBarStyle(this, true);
            }
        }
    }

    @Override
    public void onStopNestedScroll() {
        // do nothing.
    }

    // on swipe swipeListener.(swipe back swipeListener)

    @Override
    public boolean canSwipeBack(int dir) {
        return swipeBackManagePresenter.checkCanSwipeBack(dir);
    }

    @Override
    public void onSwipeProcess(float percent) {
        container.setBackgroundColor(SwipeBackCoordinatorLayout.getBackgroundColor(percent));
    }

    @Override
    public void onSwipeFinish(int dir) {
        swipeBackManagePresenter.swipeBackFinish(this, dir);
    }

    // view.

    // swipe back manage view.

    @Override
    public boolean checkCanSwipeBack(int dir) {
        if (dir == SwipeBackCoordinatorLayout.UP_DIR) {
            return photosView.canSwipeBack(dir);
        } else {
            return photosView.canSwipeBack(dir);
        }
    }

    /** <br> inner class. */

    public static class SavedStateFragment extends BaseSavedStateFragment {
        // data
        private List<Photo> photoList;

        // data.

        public List<Photo> getPhotoList() {
            return photoList;
        }

        public void setPhotoList(List<Photo> photoList) {
            this.photoList = photoList;
        }
    }
}
