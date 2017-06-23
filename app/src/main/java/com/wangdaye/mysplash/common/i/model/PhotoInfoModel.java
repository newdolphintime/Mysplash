package com.wangdaye.mysplash.common.i.model;

import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common.data.service.PhotoInfoService;
import com.wangdaye.mysplash.common.data.service.PhotoService;
import com.wangdaye.mysplash.common.ui.adapter.PhotoInfoAdapter;

/**
 * Photo info model.
 *
 * Model for {@link com.wangdaye.mysplash.common.i.view.PhotoInfoView}.
 *
 * */

public interface PhotoInfoModel {

    PhotoInfoService getPhotoInfoService();
    PhotoService getPhotoService();
    PhotoInfoAdapter getAdapter();

    Photo getPhoto();
    void setPhoto(Photo p);

    /** The flag to mark if loading photo details failed. */
    boolean isFailed();
    void setFailed(boolean b);
}
