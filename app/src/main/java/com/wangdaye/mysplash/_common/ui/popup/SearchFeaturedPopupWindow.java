package com.wangdaye.mysplash._common.ui.popup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common._basic.MysplashPopupWindow;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * Search featured popup window.
 * */

public class SearchFeaturedPopupWindow extends MysplashPopupWindow
        implements View.OnClickListener {
    // widget
    private OnSearchFeaturedChangedListener listener;

    // data
    private boolean valueNow;

    /** <br> life cycle. */

    public SearchFeaturedPopupWindow(Context c, View anchor, String valueNow) {
        super(c);
        this.initialize(c, anchor, valueNow);
    }

    @SuppressLint("InflateParams")
    private void initialize(Context c, View anchor, String valueNow) {
        View v = LayoutInflater.from(c).inflate(R.layout.popup_search_featured, null);
        setContentView(v);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        initData(valueNow);
        initWidget();

        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(10);
        }
        showAsDropDown(anchor, 0, 0, Gravity.CENTER);
    }

    /** <br> UI. */

    private void initWidget() {
        View v = getContentView();

        v.findViewById(R.id.popup_search_featured_all).setOnClickListener(this);
        v.findViewById(R.id.popup_search_featured_featured).setOnClickListener(this);

        TextView allTxt = (TextView) v.findViewById(R.id.popup_search_featured_allTxt);
        DisplayUtils.setTypeface(v.getContext(), allTxt);
        allTxt.setText(v.getContext().getText(R.string.all));
        if (!valueNow) {
            if (Mysplash.getInstance().isLightTheme()) {
                allTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_light));
            } else {
                allTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_dark));
            }
        }

        TextView featuredTxt = (TextView) v.findViewById(R.id.popup_search_featured_featuredTxt);
        DisplayUtils.setTypeface(v.getContext(), featuredTxt);
        featuredTxt.setText(v.getContext().getResources().getStringArray(R.array.home_tabs)[1]);
        if (valueNow) {
            if (Mysplash.getInstance().isLightTheme()) {
                featuredTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_light));
            } else {
                featuredTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_dark));
            }
        }

        if (Mysplash.getInstance().isLightTheme()) {
            ((ImageView) v.findViewById(R.id.popup_search_featured_allIcon))
                    .setImageResource(R.drawable.ic_infinity_light);
            ((ImageView) v.findViewById(R.id.popup_search_featured_featuredIcon))
                    .setImageResource(R.drawable.ic_feature_light);
        } else {
            ((ImageView) v.findViewById(R.id.popup_search_featured_allIcon))
                    .setImageResource(R.drawable.ic_infinity_dark);
            ((ImageView) v.findViewById(R.id.popup_search_featured_featuredIcon))
                    .setImageResource(R.drawable.ic_feature_dark);
        }
    }

    /** <br> data. */

    private void initData(String valueNow) {
        this.valueNow = Boolean.parseBoolean(valueNow);
    }

    /** <br> interface. */

    public interface OnSearchFeaturedChangedListener {
        void onSearchFeaturedChanged(boolean newValue);
    }

    public void setOnSearchFeaturedChangedListener(OnSearchFeaturedChangedListener l) {
        listener = l;
    }

    @Override
    public void onClick(View view) {
        boolean newValue = valueNow;
        switch (view.getId()) {
            case R.id.popup_search_featured_all:
                newValue = false;
                break;

            case R.id.popup_search_featured_featured:
                newValue = true;
                break;
        }

        if (!newValue == valueNow && listener != null) {
            listener.onSearchFeaturedChanged(newValue);
            dismiss();
        }
    }
}
