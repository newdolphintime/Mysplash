package com.wangdaye.mysplash.photo.presenter;

import android.content.Context;

import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.utils.helper.DownloadHelper;
import com.wangdaye.mysplash._common.i.model.DownloadModel;
import com.wangdaye.mysplash._common.i.presenter.DownloadPresenter;

/**
 * Download implementor.
 * */

public class DownloadImplementor
        implements DownloadPresenter {
    // model & view.
    private DownloadModel model;

    /** <br> life cycle. */

    public DownloadImplementor(DownloadModel model) {
        this.model = model;
    }

    /** <br> presenter. */

    @Override
    public void download(Context context) {
        doDownload(context, DownloadHelper.DOWNLOAD_TYPE);
    }

    @Override
    public void share(Context context) {
        doDownload(context, DownloadHelper.SHARE_TYPE);
    }

    @Override
    public void setWallpaper(Context context) {
        doDownload(context, DownloadHelper.WALLPAPER_TYPE);
    }

    @Override
    public Object getDownloadKey() {
        return model.getDownloadKey();
    }

    @Override
    public void setDownloadKey(Object key) {
        model.setDownloadKey(key);
    }

    /** <br> utils. */

    private void doDownload(Context context, int type) {
        Photo p = (Photo) model.getDownloadKey();
        DownloadHelper.getInstance(context).addMission(context, p, type);
    }
}