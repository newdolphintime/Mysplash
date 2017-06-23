package com.wangdaye.mysplash.user.presenter.activity;

import android.net.Uri;

import com.wangdaye.mysplash._common.data.entity.unsplash.User;
import com.wangdaye.mysplash._common.data.service.UserService;
import com.wangdaye.mysplash._common.i.model.BrowsableModel;
import com.wangdaye.mysplash._common.i.presenter.BrowsablePresenter;
import com.wangdaye.mysplash._common.i.view.BrowsableView;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Browsable implementor.
 * */

public class BrowsableImplementor
        implements BrowsablePresenter,
        UserService.OnRequestUserProfileListener {
    // model & view.
    private BrowsableModel model;
    private BrowsableView view;

    /** <br> life cycle. */

    public BrowsableImplementor(BrowsableModel model, BrowsableView view) {
        this.model = model;
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public Uri getIntentUri() {
        return model.getIntentUri();
    }

    @Override
    public boolean isBrowsable() {
        return model.isBrowsable();
    }

    @Override
    public void requestBrowsableData() {
        view.showRequestDialog();
        requestUser();
    }

    @Override
    public void visitParentView() {
        view.visitParentView();
    }

    @Override
    public void cancelRequest() {
        ((UserService) model.getService()).cancel();
    }

    /** <br> utils. */

    private void requestUser() {
        String[] keys = model.getBrowsableDataKey().split(",");
        ((UserService) model.getService()).requestUserProfile(keys[0].substring(1), this);
    }

    /** <br> swipeListener. */

    @Override
    public void onRequestUserProfileSuccess(Call<User> call, Response<User> response) {
        if (response.isSuccessful() && response.body() != null) {
            view.dismissRequestDialog();
            view.drawBrowsableView(response.body());
        } else {
            requestUser();
        }
    }

    @Override
    public void onRequestUserProfileFailed(Call<User> call, Throwable t) {
        requestUser();
    }
}
