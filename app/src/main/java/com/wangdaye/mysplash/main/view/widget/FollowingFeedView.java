package com.wangdaye.mysplash.main.view.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.FollowingResult;
import com.wangdaye.mysplash._common.data.entity.unsplash.User;
import com.wangdaye.mysplash._common.i.model.FollowingModel;
import com.wangdaye.mysplash._common.i.model.LoadModel;
import com.wangdaye.mysplash._common.i.model.ScrollModel;
import com.wangdaye.mysplash._common.i.presenter.FollowingPresenter;
import com.wangdaye.mysplash._common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash._common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash._common.i.view.FollowingView;
import com.wangdaye.mysplash._common.i.view.LoadView;
import com.wangdaye.mysplash._common.i.view.ScrollView;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.ui.adapter.FollowingAdapter;
import com.wangdaye.mysplash._common.ui.widget.CircleImageView;
import com.wangdaye.mysplash._common.ui.widget.nestedScrollView.NestedScrollFrameLayout;
import com.wangdaye.mysplash._common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.BackToTopUtils;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.helper.ImageHelper;
import com.wangdaye.mysplash._common.utils.helper.IntentHelper;
import com.wangdaye.mysplash.main.model.widget.FollowingObject;
import com.wangdaye.mysplash.main.model.widget.LoadObject;
import com.wangdaye.mysplash.main.model.widget.ScrollObject;
import com.wangdaye.mysplash.main.presenter.widget.FollowingImplementor;
import com.wangdaye.mysplash.main.presenter.widget.LoadImplementor;
import com.wangdaye.mysplash.main.presenter.widget.ScrollImplementor;
import com.wangdaye.mysplash.user.view.activity.UserActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Following feed view.
 * */

public class FollowingFeedView extends NestedScrollFrameLayout
        implements FollowingView, LoadView, ScrollView,
        View.OnClickListener, BothWaySwipeRefreshLayout.OnRefreshAndLoadListener {
    // model.
    private FollowingModel followingModel;
    private LoadModel loadModel;
    private ScrollModel scrollModel;

    // view.
    private CircularProgressView progressView;
    private RelativeLayout feedbackContainer;
    private TextView feedbackText;

    private BothWaySwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private RelativeLayout avatarContainer;
    private CircleImageView avatar;

    // presenter.
    private FollowingPresenter followingPresenter;
    private LoadPresenter loadPresenter;
    private ScrollPresenter scrollPresenter;

    // data
    private float offsetY = 0;
    private float AVATAR_SIZE;
    private float STATUS_BAR_HEIGHT;

    /** <br> life cycle. */

    public FollowingFeedView(Context context) {
        super(context);
        this.initialize();
    }

    public FollowingFeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public FollowingFeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FollowingFeedView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize();
    }

    @SuppressLint("InflateParams")
    private void initialize() {
        View searchingView = LayoutInflater.from(getContext()).inflate(R.layout.container_loading_in_following_view_large, this, false);
        addView(searchingView);

        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.container_photo_list, null);
        addView(contentView);

        View avatarView = LayoutInflater.from(getContext()).inflate(R.layout.container_following_avatar, null);
        addView(avatarView);

        initModel();
        initPresenter();
        initView();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return new SavedState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        followingPresenter.setNextPage(ss.nextPage);
        followingPresenter.setOver(ss.over);
        setOffsetY(ss.offsetY);
    }

    @Override
    public boolean isParentOffset() {
        return false;
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.followingPresenter = new FollowingImplementor(followingModel, this);
        this.loadPresenter = new LoadImplementor(loadModel, this);
        this.scrollPresenter = new ScrollImplementor(scrollModel, this);
    }

    /** <br> view. */

    // init.

    private void initView() {
        this.initAvatarView();
        this.initContentView();
        this.initLoadingView();
    }

    private void initAvatarView() {
        this.avatarContainer = (RelativeLayout) findViewById(R.id.container_following_avatar_avatarContainer);
        avatarContainer.setOnClickListener(this);

        this.avatar = (CircleImageView) findViewById(R.id.container_following_avatar_avatar);
        avatar.setOnClickListener(this);

        DisplayUtils utils = new DisplayUtils(getContext());

        FrameLayout.LayoutParams containerParams = (FrameLayout.LayoutParams) avatarContainer.getLayoutParams();
        int size = (int) utils.dpToPx(56);
        containerParams.width = size;
        containerParams.height = size + DisplayUtils.getStatusBarHeight(getResources());
        avatarContainer.setLayoutParams(containerParams);
    }

    private void initContentView() {
        this.refreshLayout = (BothWaySwipeRefreshLayout) findViewById(R.id.container_photo_list_swipeRefreshLayout);
        refreshLayout.setOnRefreshAndLoadListener(this);
        refreshLayout.setVisibility(GONE);
        if (Mysplash.getInstance().isLightTheme()) {
            refreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorTextContent_light));
            refreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorPrimary_light);
        } else {
            refreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorTextContent_dark));
            refreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorPrimary_dark);
        }

        int navigationBarHeight = DisplayUtils.getNavigationBarHeight(getResources());
        refreshLayout.setDragTriggerDistance(
                BothWaySwipeRefreshLayout.DIRECTION_BOTTOM,
                (int) (navigationBarHeight + new DisplayUtils(getContext()).dpToPx(16)));

        this.recyclerView = (RecyclerView) findViewById(R.id.container_photo_list_recyclerView);
        recyclerView.setAdapter(followingPresenter.getAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addOnScrollListener(scrollListener);
        avatarScrollListener = new AvatarScrollListener();
        recyclerView.addOnScrollListener(avatarScrollListener);

        followingPresenter.getAdapter().setRecyclerView(recyclerView);
    }

    private void initLoadingView() {
        this.progressView = (CircularProgressView) findViewById(R.id.container_loading_in_following_view_large_progressView);
        progressView.setVisibility(VISIBLE);

        this.feedbackContainer = (RelativeLayout) findViewById(R.id.container_loading_in_following_view_large_feedbackContainer);
        feedbackContainer.setVisibility(GONE);

        ImageView feedbackImg = (ImageView) findViewById(R.id.container_loading_in_following_view_large_feedbackImg);
        ImageHelper.loadIcon(getContext(), feedbackImg, R.drawable.feedback_no_photos);

        this.feedbackText = (TextView) findViewById(R.id.container_loading_in_following_view_large_feedbackTxt);

        Button retryButton = (Button) findViewById(R.id.container_loading_in_following_view_large_feedbackBtn);
        retryButton.setOnClickListener(this);
    }

    // interface.

    public void pagerScrollToTop() {
        scrollPresenter.scrollToTop();
    }

    /** <br> model. */

    // init

    private void initModel() {
        this.followingModel = new FollowingObject(
                new FollowingAdapter(
                        getContext(),
                        new ArrayList<FollowingResult>(Mysplash.DEFAULT_PER_PAGE)));
        this.loadModel = new LoadObject(LoadObject.LOADING_STATE);
        this.scrollModel = new ScrollObject(true);

        this.AVATAR_SIZE = new DisplayUtils(getContext()).dpToPx(56);
        this.STATUS_BAR_HEIGHT = DisplayUtils.getStatusBarHeight(getResources());
    }

    // interface.

    public void setActivity(MysplashActivity a) {
        followingPresenter.setActivityForAdapter(a);
    }

    public List<FollowingResult> getFeeds() {
        return followingPresenter.getAdapter().getFeeds();
    }

    public void setFeeds(List<FollowingResult> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        followingPresenter.getAdapter().setFeeds(list);
        if (list.size() == 0) {
            initRefresh();
        } else {
            setNormalState();
        }
    }

    public void cancelRequest() {
        followingPresenter.cancelRequest();
    }

    public void initRefresh() {
        followingPresenter.initRefresh(getContext());
    }

    public boolean needPagerBackToTop() {
        return scrollPresenter.needBackToTop();
    }

    public void setOffsetY(float offsetY) {
        if (this.offsetY != offsetY) {
            this.offsetY = offsetY;
            if (avatarScrollListener != null && followingPresenter.getAdapter().getRealItemCount() > 0) {
                avatarScrollListener.onScrolled(recyclerView, 0, 0);
            }
        }
    }

    public float getOffsetY() {
        return offsetY;
    }

    private float getRealOffset() {
        return Math.max(getOffsetY() - AVATAR_SIZE, 0);
    }

    /** <br> interface. */

    // on click swipeListener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.container_following_avatar_avatarContainer:
                // do nothing.
                break;

            case R.id.container_following_avatar_avatar:
                int adapterPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findFirstVisibleItemPosition();
                IntentHelper.startUserActivity(
                        Mysplash.getInstance().getTopActivity(),
                        avatar,
                        followingPresenter.getAdapter().getActor(adapterPosition),
                        UserActivity.PAGE_PHOTO);
                break;

            case R.id.container_loading_in_following_view_large_feedbackBtn:
                followingPresenter.initRefresh(getContext());
                break;
        }
    }

    // on refresh an load swipeListener.

    @Override
    public void onRefresh() {
        followingPresenter.refreshNew(getContext(), false);
    }

    @Override
    public void onLoad() {
        followingPresenter.loadMore(getContext(), false);
    }

    // on scroll swipeListener.

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            scrollPresenter.autoLoad(dy);
        }
    };

    private AvatarScrollListener avatarScrollListener;
    private class AvatarScrollListener extends RecyclerView.OnScrollListener {
        // widget
        private LinearLayoutManager manager;

        // data
        private User lastActor;

        // life cycle.

        AvatarScrollListener() {
            this.manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            this.lastActor = null;
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy){
            int firstVisibleItemPosition = manager.findFirstVisibleItemPosition();
            if (followingPresenter.getAdapter().isFooterView(firstVisibleItemPosition)) {

                View firstVisibleView = manager.findViewByPosition(firstVisibleItemPosition);
                View secondVisibleView = manager.findViewByPosition(firstVisibleItemPosition + 1);
                if (firstVisibleView != null && secondVisibleView != null) {
                    float footerBottom = firstVisibleView.getY() + firstVisibleView.getMeasuredHeight();
                    float headerTop = secondVisibleView.getY();

                    if (footerBottom < AVATAR_SIZE + getRealOffset() && headerTop > getRealOffset()) {
                        avatarContainer.setTranslationY(footerBottom - AVATAR_SIZE - STATUS_BAR_HEIGHT);
                        setAvatarImage(firstVisibleItemPosition);
                    } else {
                        avatarContainer.setTranslationY(-STATUS_BAR_HEIGHT + getRealOffset());
                        setAvatarImage(firstVisibleItemPosition + (headerTop <= getRealOffset() ? 1 : 0));
                    }
                }
            } else {
                avatarContainer.setTranslationY(-STATUS_BAR_HEIGHT + getRealOffset());
                if (lastActor == null) {
                    setAvatarImage(firstVisibleItemPosition);
                }
            }
        }

        private void setAvatarImage(int position) {
            User user = followingPresenter.getAdapter().getActor(position);
            if (lastActor == null || !lastActor.username.equals(user.username)) {
                lastActor = user;
                ImageHelper.loadAvatar(getContext(), avatar, user, null);
            }
        }
    }

    // view.

    // category view.

    @Override
    public void setRefreshing(boolean refreshing) {
        refreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void setLoading(boolean loading) {
        refreshLayout.setLoading(loading);
    }

    @Override
    public void setPermitRefreshing(boolean permit) {
        refreshLayout.setPermitRefresh(permit);
    }

    @Override
    public void setPermitLoading(boolean permit) {
        refreshLayout.setPermitLoad(permit);
    }

    @Override
    public void initRefreshStart() {
        loadPresenter.setLoadingState();
    }

    @Override
    public void requestFollowingFeedSuccess() {
        loadPresenter.setNormalState();
    }

    @Override
    public void requestFollowingFeedFailed(String feedback) {
        feedbackText.setText(feedback);
        loadPresenter.setFailedState();
    }

    // load view.

    @Override
    public void animShow(View v) {
        AnimUtils.animShow(v);
    }

    @Override
    public void animHide(final View v) {
        AnimUtils.animHide(v);
    }

    @Override
    public void setLoadingState() {
        animShow(progressView);
        animHide(feedbackContainer);
    }

    @Override
    public void setFailedState() {
        animShow(feedbackContainer);
        animHide(progressView);
    }

    @Override
    public void setNormalState() {
        animShow(refreshLayout);
        animHide(progressView);
    }

    @Override
    public void resetLoadingState() {
        animShow(progressView);
        animHide(refreshLayout);
    }

    // scroll view.

    @Override
    public void scrollToTop() {
        avatarScrollListener.setAvatarImage(0);
        BackToTopUtils.scrollToTop(recyclerView);
    }

    @Override
    public void autoLoad(int dy) {
        int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        int totalItemCount = followingPresenter.getAdapter().getRealItemCount();
        if (followingPresenter.canLoadMore()
                && lastVisibleItem >= totalItemCount - 10 && totalItemCount > 0 && dy > 0) {
            followingPresenter.loadMore(getContext(), false);
        }
        if (!ViewCompat.canScrollVertically(recyclerView, -1)) {
            scrollPresenter.setToTop(true);
        } else {
            scrollPresenter.setToTop(false);
        }
        if (!ViewCompat.canScrollVertically(recyclerView, 1) && followingPresenter.isLoading()) {
            refreshLayout.setLoading(true);
        }
    }

    @Override
    public boolean needBackToTop() {
        return !scrollPresenter.isToTop()
                && loadPresenter.getLoadState() == LoadObject.NORMAL_STATE;
    }

    /** <br> inner class. */

    private static class SavedState extends BaseSavedState {
        // data
        String nextPage;
        boolean over;
        float offsetY;

        // life cycle.

        SavedState(FollowingFeedView view, Parcelable superState) {
            super(superState);
            this.nextPage = view.followingModel.getNextPage();
            this.over = view.followingModel.isOver();
            this.offsetY = view.offsetY;
        }

        private SavedState(Parcel in) {
            super(in);
            this.nextPage = in.readString();
            this.over = in.readByte() != 0;
            this.offsetY = in.readFloat();
        }

        // interface.

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(this.nextPage);
            out.writeByte(this.over ? (byte) 1 : (byte) 0);
            out.writeFloat(this.offsetY);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}