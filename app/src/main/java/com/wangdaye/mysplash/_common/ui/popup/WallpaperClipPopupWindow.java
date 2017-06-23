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
import android.widget.TextView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common._basic.MysplashPopupWindow;
import com.wangdaye.mysplash._common.ui.activity.SetWallpaperActivity;
import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * Wallpaper clip popup window.
 * */

public class WallpaperClipPopupWindow extends MysplashPopupWindow
        implements View.OnClickListener {
    // widget
    private OnClipTypeChangedListener listener;

    // data
    private int valueNow;

    /** <br> life cycle. */

    public WallpaperClipPopupWindow(Context c, View anchor, int valueNow) {
        super(c);
        this.initialize(c, anchor, valueNow);
    }

    @SuppressLint("InflateParams")
    private void initialize(Context c, View anchor, int valueNow) {
        View v = LayoutInflater.from(c).inflate(R.layout.popup_wallpaper_clip, null);
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

        v.findViewById(R.id.popup_wallpaper_clip_square).setOnClickListener(this);
        v.findViewById(R.id.popup_wallpaper_clip_rect).setOnClickListener(this);

        TextView squareTxt = (TextView) v.findViewById(R.id.popup_wallpaper_clip_squareTxt);
        DisplayUtils.setTypeface(v.getContext(), squareTxt);
        if (valueNow == SetWallpaperActivity.CLIP_TYPE_SQUARE) {
            squareTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_light));
        }

        TextView rectTxt = (TextView) v.findViewById(R.id.popup_wallpaper_clip_rectTxt);
        DisplayUtils.setTypeface(v.getContext(), rectTxt);
        if (valueNow == SetWallpaperActivity.CLIP_TYPE_RECT) {
            rectTxt.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorTextSubtitle_light));
        }
    }

    /** <br> data. */

    private void initData(int valueNow) {
        this.valueNow = valueNow;
    }

    /** <br> interface. */

    public interface OnClipTypeChangedListener {
        void onClipTypeChanged(int type);
    }

    public void setOnClipTypeChangedListener(OnClipTypeChangedListener l) {
        listener = l;
    }

    @Override
    public void onClick(View view) {
        int newValue = valueNow;
        switch (view.getId()) {
            case R.id.popup_wallpaper_clip_square:
                newValue = SetWallpaperActivity.CLIP_TYPE_SQUARE;
                break;

            case R.id.popup_wallpaper_clip_rect:
                newValue = SetWallpaperActivity.CLIP_TYPE_RECT;
                break;
        }

        if (newValue != valueNow && listener != null) {
            listener.onClipTypeChanged(newValue);
            dismiss();
        }
    }
}