package com.wangdaye.mysplash.main.presenter.fragment;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.i.presenter.SearchBarPresenter;
import com.wangdaye.mysplash.common.i.view.SearchBarView;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.main.view.activity.MainActivity;

/**
 * Search bar implementor.
 *
 * A {@link SearchBarPresenter} for {@link com.wangdaye.mysplash.main.view.fragment.SearchFragment}.
 *
 * */

public class SearchBarImplementor
        implements SearchBarPresenter {

    private SearchBarView view;

    public SearchBarImplementor(SearchBarView view) {
        this.view = view;
    }

    @Override
    public void touchNavigatorIcon(MysplashActivity a) {
        ((MainActivity) a).changeFragment(R.id.action_home);
    }

    @Override
    public boolean touchMenuItem(MysplashActivity a, int itemId) {
        switch (itemId) {
            case R.id.action_clear_text:
                view.clearSearchBarText();
                break;
        }
        return true;
    }

    @Override
    public void showKeyboard() {
        view.showKeyboard();
    }

    @Override
    public void hideKeyboard() {
        view.hideKeyboard();
    }

    @Override
    public void submitSearchInfo(String text) {
        view.submitSearchInfo(text);
    }
}
