package com.wangdaye.mysplash.common.ui.widget.freedomSizeView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.Size;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash.common.utils.DisplayUtils;

/**
 * Freedom image view.
 *
 * This ImageView can measure size according to the preset.
 *
 * */

public class FreedomImageView extends ImageView {

    private Paint paint;

    // measure size according the width and height. (proportion)
    private float width = 1;
    private float height = 0.6f;
    @IntRange(from = 0)
    private @interface SizeRule {}

    private boolean notFree = false; // if set false, there will be no different between this view and a ImageView.
    private boolean coverMode = false; // if set true, it means this ImageView is a cover in PhotoActivity.
    private boolean showShadow = false;

    private int textPosition;
    private static final int POSITION_NONE = 0;
    private static final int POSITION_TOP = 1;
    private static final int POSITION_BOTTOM = -1;
    private static final int POSITION_BOTH = 2;

    public FreedomImageView(Context context) {
        super(context);
    }

    public FreedomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context, attrs, 0, 0);
    }

    public FreedomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FreedomImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initialize(Context c, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.FreedomImageView, defStyleAttr, defStyleRes);
        this.notFree = a.getBoolean(R.styleable.FreedomImageView_fiv_not_free, false);
        this.coverMode = a.getBoolean(R.styleable.FreedomImageView_fiv_cover_mode, false);
        this.textPosition = a.getInt(R.styleable.FreedomImageView_fiv_shadow_position, POSITION_NONE);
        a.recycle();

        this.paint = new Paint();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (width >= 0 && height >= 0) {
            if (notFree) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            } else {
                int[] size = getMeasureSize(
                        getContext(),
                        MeasureSpec.getSize(widthMeasureSpec), width, height, coverMode);
                setMeasuredDimension(size[0], size[1]);
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showShadow) {
            switch (textPosition) {
                case POSITION_TOP: {
                    int topTextHeight = (int) new DisplayUtils(getContext()).dpToPx(128);
                    paint.setShader(new LinearGradient(
                            0, 0,
                            0, topTextHeight,
                            new int[]{
                                    Color.argb((int) (255 * 0.25), 0, 0, 0),
                                    Color.argb((int) (255 * 0.09), 0, 0, 0),
                                    Color.argb((int) (255 * 0.03), 0, 0, 0),
                                    Color.argb(0, 0, 0, 0)},
                            null,
                            Shader.TileMode.CLAMP));
                    canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);
                    break;
                }
                case POSITION_BOTTOM: {
                    int bottomTextHeight = (int) new DisplayUtils(getContext()).dpToPx(72);
                    paint.setShader(new LinearGradient(
                            0, getMeasuredHeight(),
                            0, getMeasuredHeight() - bottomTextHeight,
                            new int[]{
                                    Color.argb((int) (255 * 0.3), 0, 0, 0),
                                    Color.argb((int) (255 * 0.1), 0, 0, 0),
                                    Color.argb((int) (255 * 0.03), 0, 0, 0),
                                    Color.argb(0, 0, 0, 0)},
                            null,
                            Shader.TileMode.CLAMP));
                    canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);
                    break;
                }
                case POSITION_BOTH: {
                    int topTextHeight = (int) new DisplayUtils(getContext()).dpToPx(128);
                    paint.setShader(new LinearGradient(
                            0, 0,
                            0, topTextHeight,
                            new int[]{
                                    Color.argb((int) (255 * 0.25), 0, 0, 0),
                                    Color.argb((int) (255 * 0.09), 0, 0, 0),
                                    Color.argb((int) (255 * 0.03), 0, 0, 0),
                                    Color.argb(0, 0, 0, 0)},
                            null,
                            Shader.TileMode.CLAMP));
                    canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);

                    int bottomTextHeight = (int) new DisplayUtils(getContext()).dpToPx(72);
                    paint.setShader(new LinearGradient(
                            0, getMeasuredHeight(),
                            0, getMeasuredHeight() - bottomTextHeight,
                            new int[]{
                                    Color.argb((int) (255 * 0.3), 0, 0, 0),
                                    Color.argb((int) (255 * 0.1), 0, 0, 0),
                                    Color.argb((int) (255 * 0.03), 0, 0, 0),
                                    Color.argb(0, 0, 0, 0)},
                            null,
                            Shader.TileMode.CLAMP));
                    canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);
                    break;
                }
            }
        }
    }

    @Size(2)
    public float[] getSize() {
        return new float[] {width, height};
    }

    public void setSize(@SizeRule int w, @SizeRule int h) {
        if (!notFree) {
            width = w;
            height = h;
            if (getMeasuredWidth() != 0) {
                requestLayout();
            }
        }
    }

    @Size(2)
    public static int[] getMeasureSize(Context c,
                                       int measureWidth, float w, float h, boolean coverMode) {
        if (coverMode) {
            int screenWidth = c.getResources().getDisplayMetrics().widthPixels;
            int screenHeight = c.getResources().getDisplayMetrics().heightPixels;
            if (DisplayUtils.isLandscape(c)) {
                return new int[] {
                        measureWidth,
                        screenHeight};
            }

            float limitHeight;
            if (c.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                limitHeight = screenHeight;
            } else {
                limitHeight = screenHeight
                        - c.getResources().getDimensionPixelSize(R.dimen.photo_info_base_view_height);
            }

            if (1.0 * h / w * screenWidth <= limitHeight) {
                return new int[] {
                        // (int) (limitHeight * w / h),
                        measureWidth,
                        (int) limitHeight};
            }
        }
        return new int[] {
                measureWidth,
                (int) (measureWidth * h / w)};
    }

    public void setShowShadow(boolean show) {
        this.showShadow = show;
    }

/*
    @Size(4) // l, t, r, b.
    public static int[] getLayoutArea(Context c, int[] parentSizes, int[] childSizes) {
        int deltaWidth = childSizes[0] - parentSizes[0];
        int deltaHeight = childSizes[1]
                - (c.getResources()
                .getConfiguration()
                .orientation == Configuration.ORIENTATION_LANDSCAPE ?
                c.getResources().getDisplayMetrics().heightPixels : childSizes[1]);
        return new int[] {
                (int) (-deltaWidth / 2.0),
                (int) (-deltaHeight / 2.0),
                (int) (childSizes[0] - deltaWidth / 2.0),
                (int) (childSizes[1] - deltaHeight / 2.0)};
    }
*/
}
