package com.wangdaye.mysplash.main.model.widget;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.data.service.CollectionService;
import com.wangdaye.mysplash._common.i.model.CollectionsModel;
import com.wangdaye.mysplash._common.ui.adapter.CollectionAdapter;

/**
 * Collections object.
 * */

public class CollectionsObject
        implements CollectionsModel {
    // data
    private CollectionAdapter adapter;
    private CollectionService service;

    private String collectionsType;

    private int collectionsPage;

    private boolean refreshing;
    private boolean loading;
    private boolean over;

    /** <br> life cycle. */

    public CollectionsObject(CollectionAdapter adapter) {
        this.adapter = adapter;
        this.service = CollectionService.getService();

        this.collectionsType = Mysplash.getInstance().getDefaultCollectionType();

        this.collectionsPage = adapter.getItemCount() / Mysplash.DEFAULT_PER_PAGE;

        this.refreshing = false;
        this.loading = false;
        this.over = false;
    }

    /** <br> model. */

    @Override
    public CollectionAdapter getAdapter() {
        return adapter;
    }

    @Override
    public CollectionService getService() {
        return service;
    }

    @Override
    public Object getRequestKey() {
        return null;
    }

    @Override
    public void setRequestKey(Object key) {
        // do nothing.
    }

    @Override
    public String getCollectionsType() {
        return collectionsType;
    }

    @Override
    public void setCollectionsType(String order) {
        collectionsType = order;
    }

    @Override
    public int getCollectionsPage() {
        return collectionsPage;
    }

    @Override
    public void setCollectionsPage(int page) {
        collectionsPage = page;
    }

    @Override
    public boolean isRefreshing() {
        return refreshing;
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
    }

    @Override
    public boolean isLoading() {
        return loading;
    }

    @Override
    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    @Override
    public boolean isOver() {
        return over;
    }

    @Override
    public void setOver(boolean over) {
        this.over = over;
    }
}
