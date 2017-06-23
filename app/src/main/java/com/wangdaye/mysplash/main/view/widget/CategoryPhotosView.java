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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.data.entity.unsplash.User;
import com.wangdaye.mysplash._common.i.model.CategoryModel;
import com.wangdaye.mysplash._common.i.model.LoadModel;
import com.wangdaye.mysplash._common.i.model.ScrollModel;
import com.wangdaye.mysplash._common.i.presenter.CategoryPresenter;
import com.wangdaye.mysplash._common.i.presenter.LoadPresenter;
import com.wangdaye.mysplash._common.i.presenter.ScrollPresenter;
import com.wangdaye.mysplash._common.ui.adapter.PhotoAdapter;
import com.wangdaye.mysplash._common.ui.dialog.SelectCollectionDialog;
import com.wangdaye.mysplash._common.ui.widget.nestedScrollView.NestedScrollFrameLayout;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.BackToTopUtils;
import com.wangdaye.mysplash._common.i.view.CategoryView;
import com.wangdaye.mysplash._common.i.view.LoadView;
import com.wangdaye.mysplash._common.i.view.ScrollView;
import com.wangdaye.mysplash._common.utils.DisplayUtils;
import com.wangdaye.mysplash._common.utils.helper.ImageHelper;
import com.wangdaye.mysplash.main.model.widget.CategoryObject;
import com.wangdaye.mysplash._common.ui.widget.swipeRefreshView.BothWaySwipeRefreshLayout;
import com.wangdaye.mysplash.main.model.widget.LoadObject;
import com.wangdaye.mysplash.main.model.widget.ScrollObject;
import com.wangdaye.mysplash.main.presenter.widget.CategoryImplementor;
import com.wangdaye.mysplash.main.presenter.widget.LoadImplementor;
import com.wangdaye.mysplash.main.presenter.widget.ScrollImplementor;
import com.wangdaye.mysplash.main.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Category photos view.
 * */

public class CategoryPhotosView extends NestedScrollFrameLayout
        implements CategoryView, LoadView, ScrollView,
        View.OnClickListener, BothWaySwipeRefreshLayout.OnRefreshAndLoadListener,
        SelectCollectionDialog.OnCollectionsChangedListener {
    // model.
    private CategoryModel categoryModel;
    private LoadModel loadModel;
    private ScrollModel scrollModel;

    // view.
    private CircularProgressView progressView;
    private RelativeLayout feedbackContainer;
    private TextView feedbackText;

    private BothWaySwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    // presenter.
    private CategoryPresenter categoryPresenter;
    private LoadPresenter loadPresenter;
    private ScrollPresenter scrollPresenter;

    /** <br> life cycle. */

    public CategoryPhotosView(Context context) {
        super(context);
        this.initialize();
    }

    public CategoryPhotosView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public CategoryPhotosView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CategoryPhotosView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize();
    }

    @SuppressLint("InflateParams")
    private void initialize() {
        View searchingView = LayoutInflater.from(getContext()).inflate(R.layout.container_loading_in_category_view_large, this, false);
        addView(searchingView);

        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.container_photo_list, null);
        addView(contentView);

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

        categoryPresenter.setCategory(ss.category);
        categoryPresenter.setOrder(ss.order);
        categoryPresenter.setPage(ss.page);
        categoryPresenter.setPageList(ss.pageList);
        categoryPresenter.setOver(ss.over);
    }

    @Override
    public boolean isParentOffset() {
        return false;
    }

    /** <br> presenter. */

    private void initPresenter() {
        this.categoryPresenter = new CategoryImplementor(categoryModel, this);
        this.loadPresenter = new LoadImplementor(loadModel, this);
        this.scrollPresenter = new ScrollImplementor(scrollModel, this);
    }

    /** <br> view. */

    // init.

    private void initView() {
        this.initContentView();
        this.initLoadingView();
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
        recyclerView.setAdapter(categoryPresenter.getAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addOnScrollListener(scrollListener);

        categoryPresenter.getAdapter().setRecyclerView(recyclerView);
    }

    private void initLoadingView() {
        this.progressView = (CircularProgressView) findViewById(R.id.container_loading_in_category_view_large_progressView);
        progressView.setVisibility(VISIBLE);

        this.feedbackContainer = (RelativeLayout) findViewById(R.id.container_loading_in_category_view_large_feedbackContainer);
        feedbackContainer.setVisibility(GONE);

        ImageView feedbackImg = (ImageView) findViewById(R.id.container_loading_in_category_view_large_feedbackImg);
        ImageHelper.loadIcon(getContext(), feedbackImg, R.drawable.feedback_no_photos);

        this.feedbackText = (TextView) findViewById(R.id.container_loading_in_category_view_large_feedbackTxt);

        Button retryButton = (Button) findViewById(R.id.container_loading_in_category_view_large_feedbackBtn);
        retryButton.setOnClickListener(this);
    }

    // interface.

    public void pagerScrollToTop() {
        scrollPresenter.scrollToTop();
    }

    /** <br> model. */

    // init

    private void initModel() {
        this.categoryModel = new CategoryObject(
                getContext(),
                new PhotoAdapter(
                        getContext(),
                        new ArrayList<Photo>(Mysplash.DEFAULT_PER_PAGE),
                        this,
                        null));
        this.loadModel = new LoadObject(LoadObject.LOADING_STATE);
        this.scrollModel = new ScrollObject(true);
    }

    // interface.

    public void setActivity(MainActivity a) {
        categoryPresenter.setActivityForAdapter(a);
        categoryPresenter.getAdapter().setOnDownloadPhotoListener(a);
    }

    public List<Photo> getPhotos() {
        return categoryPresenter.getAdapter().getPhotoData();
    }

    public void setPhotos(List<Photo> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        categoryPresenter.getAdapter().setPhotoData(list);
        if (list.size() == 0) {
            initRefresh();
        } else {
            setNormalState();
        }
    }

    public void setCategory(int id) {
        categoryPresenter.setCategory(id);
    }

    public String getOrder() {
        return categoryPresenter.getOrder();
    }

    public void setOrder(String order) {
        categoryPresenter.setOrder(order);
    }

    public void cancelRequest() {
        categoryPresenter.cancelRequest();
    }

    public void initRefresh() {
        categoryPresenter.initRefresh(getContext());
    }

    public boolean needPagerBackToTop() {
        return scrollPresenter.needBackToTop();
    }

    /** <br> interface. */

    // on click swipeListener.

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.container_loading_in_category_view_large_feedbackBtn:
                categoryPresenter.initRefresh(getContext());
                break;
        }
    }

    // on refresh an load swipeListener.

    @Override
    public void onRefresh() {
        categoryPresenter.refreshNew(getContext(), false);
    }

    @Override
    public void onLoad() {
        categoryPresenter.loadMore(getContext(), false);
    }

    // on scroll swipeListener.

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            scrollPresenter.autoLoad(dy);
        }
    };

    // on collections changed swipeListener.

    @Override
    public void onAddCollection(Collection c) {
        // do nothing.
    }

    @Override
    public void onUpdateCollection(Collection c, User u, Photo p) {
        categoryPresenter.getAdapter().updatePhoto(p, false);
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
    public void requestPhotosSuccess() {
        loadPresenter.setNormalState();
    }

    @Override
    public void requestPhotosFailed(String feedback) {
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
        BackToTopUtils.scrollToTop(recyclerView);
    }

    @Override
    public void autoLoad(int dy) {
        int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        int totalItemCount = categoryPresenter.getAdapter().getRealItemCount();
        if (categoryPresenter.canLoadMore()
                && lastVisibleItem >= totalItemCount - 10 && totalItemCount > 0 && dy > 0) {
            categoryPresenter.loadMore(getContext(), false);
        }
        if (!ViewCompat.canScrollVertically(recyclerView, -1)) {
            scrollPresenter.setToTop(true);
        } else {
            scrollPresenter.setToTop(false);
        }
        if (!ViewCompat.canScrollVertically(recyclerView, 1) && categoryPresenter.isLoading()) {
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
        int category;
        String order;

        int page;
        List<Integer> pageList;

        boolean over;

        // life cycle.

        SavedState(CategoryPhotosView view, Parcelable superState) {
            super(superState);
            this.category = view.categoryModel.getPhotosCategory();
            this.order = view.categoryModel.getPhotosOrder();
            this.page = view.categoryModel.getPhotosPage();
            this.pageList = new ArrayList<>();
            this.pageList.addAll(view.categoryModel.getPageList());
            this.over = view.categoryModel.isOver();
        }

        private SavedState(Parcel in) {
            super(in);
            this.category = in.readInt();
            this.order = in.readString();
            this.page = in.readInt();

            this.pageList = new ArrayList<>();
            int[] pages = new int[in.readInt()];
            in.readIntArray(pages);
            pageList = new ArrayList<>(pages.length);
            for (int p : pages) {
                pageList.add(p);
            }

            this.over = in.readByte() != 0;
        }

        // interface.

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.category);
            out.writeString(this.order);
            out.writeInt(this.page);

            int[] pages = new int[pageList.size()];
            for (int i = 0; i < pages.length; i ++) {
                pages[i] = pageList.get(i);
            }
            out.writeInt(pages.length);
            out.writeIntArray(pages);

            out.writeByte(this.over ? (byte) 1 : (byte) 0);
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