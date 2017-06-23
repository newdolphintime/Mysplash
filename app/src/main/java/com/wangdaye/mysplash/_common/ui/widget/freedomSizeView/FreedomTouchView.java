package com.wangdaye.mysplash._common.ui.widget.freedomSizeView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.wangdaye.mysplash._common.utils.DisplayUtils;

/**
 * Photo touch view.
 * */

public class FreedomTouchView extends View {
    // data
    private float width = 1;
    private float height = 0.666F;

    /** <br> life cycle. */

    public FreedomTouchView(Context context) {
        super(context);
    }

    public FreedomTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FreedomTouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FreedomTouchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /** <br> UI. */

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] size = getMeasureSize(MeasureSpec.getSize(widthMeasureSpec));
        setMeasuredDimension(size[0], size[1]);
    }

    /** <br> data. */

    public void setSize(int w, int h) {
        width = w;
        height = h;
        if (getMeasuredWidth() != 0) {
            /*
            int[] size = getMeasureSize(getMeasuredWidth());

            ViewGroup.LayoutParams params = getLayoutParams();
            params.width = size[0];
            params.height = size[1];
            setLayoutParams(params);
            */
            requestLayout();
        }
    }

    public float[] getSize() {
        return new float[] {width, height};
    }

    private int[] getMeasureSize(int measureWidth) {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        float limitHeight = screenHeight
                - new DisplayUtils(getContext()).dpToPx(300);

        if (1.0 * height / width * screenWidth <= limitHeight) {
            return new int[] {
                    screenWidth,
                    (int) limitHeight};
        } else {
            return new int[] {
                    measureWidth,
                    (int) (measureWidth * height / width)};
        }
    }
}
