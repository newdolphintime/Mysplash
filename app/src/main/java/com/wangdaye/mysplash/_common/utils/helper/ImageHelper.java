package com.wangdaye.mysplash._common.utils.helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.bumptech.glide.request.target.Target;
import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.entity.unsplash.Collection;
import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common.data.entity.unsplash.User;
import com.wangdaye.mysplash._common.utils.AnimUtils;
import com.wangdaye.mysplash._common.utils.widget.glide.CircleTransformation;
import com.wangdaye.mysplash._common.utils.widget.glide.FadeAnimator;

import org.greenrobot.greendao.annotation.NotNull;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Image utils.
 * */

public class ImageHelper {

    /** <br> UI. */

    // photo.

    public static void loadRegularPhoto(Context context, final ImageView view, Photo photo,
                                        @Nullable OnLoadImageListener l) {
        if (photo != null && photo.urls != null
                && photo.width != 0 && photo.height != 0) {
            DrawableRequestBuilder<String> thumbnailRequest = Glide
                    .with(context)
                    .load(photo.urls.thumb)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                                   boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                                       boolean isFromMemoryCache, boolean isFirstResource) {
                            view.setEnabled(true);
                            return false;
                        }
                    });
            if (l != null && !photo.hasFadedIn && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AnimUtils.ObservableColorMatrix matrix = new AnimUtils.ObservableColorMatrix();
                matrix.setSaturation(0);
                view.setColorFilter(new ColorMatrixColorFilter(matrix));
                view.setEnabled(false);
            }
            loadImage(
                    context, view,
                    photo.urls.regular, photo.getRegularWidth(), photo.getRegularHeight(), false, false,
                    l == null ? null : thumbnailRequest, null, l == null ? null : new FadeAnimator(),
                    l);
        }
    }

    public static void loadFullPhoto(Context context, ImageView view, String url, String thumbnail,
                                     @Nullable OnLoadImageListener l) {
        DrawableRequestBuilder<String> thumbnailRequest = Glide
                .with(context)
                .load(thumbnail)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);
        loadImage(context, view, url, 0, 0, false, false, thumbnailRequest, null, null, l);
    }

    public static void loadPhoto(Context context, ImageView view, String url, boolean lowPriority,
                                 @Nullable OnLoadImageListener l) {
        loadImage(context, view, url, 0, 0, false, lowPriority, null, null, null, l);
    }

    // collection cover.

    public static void loadCollectionCover(Context context, ImageView view, Collection collection,
                                           @Nullable OnLoadImageListener l) {
        if (collection != null) {
            loadRegularPhoto(context, view, collection.cover_photo, l);
        }
    }

    // avatar.

    public static void loadAvatar(Context context, ImageView view, User user,
                                  @Nullable OnLoadImageListener l) {
        if (user != null && user.profile_image != null) {
            loadAvatar(context, view, user.profile_image.large, l);
        } else {
            loadImage(
                    context, view,
                    R.drawable.default_avatar, 128, 128, false,
                    new CircleTransformation(context),
                    l);
        }
    }

    public static void loadAvatar(Context context, ImageView view, @NotNull String url,
                                  @Nullable OnLoadImageListener l) {
        DrawableRequestBuilder<Integer> thumbnailRequest = Glide.with(context)
                .load(R.drawable.default_avatar)
                .override(128, 128)
                .transform(new CircleTransformation(context))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);
        loadImage(
                context, view,
                url, 128, 128, false, false,
                thumbnailRequest, new CircleTransformation(context), null,
                l);
    }

    // icon.

    public static void loadIcon(Context context, ImageView view, int resId) {
        loadImage(context, view, resId, 0, 0, true, null, null);
    }

    // builder.

    private static void loadImage(Context context, ImageView view,
                                  String url, int width, int height, boolean dontAnimate, boolean lowPriority,
                                  @Nullable DrawableRequestBuilder thumbnail,
                                  @Nullable BitmapTransformation transformation,
                                  @Nullable ViewPropertyAnimation.Animator animator,
                                  @Nullable final OnLoadImageListener l) {
        DrawableRequestBuilder<String> builder = Glide
                .with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e,
                                               String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (l != null) {
                            l.onLoadFailed();
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        if (l != null) {
                            l.onLoadSucceed();
                        }
                        return false;
                    }
                });
        if (width != 0 && height != 0) {
            builder.override(width, height);
        }
        if (dontAnimate) {
            builder.dontAnimate();
        }
        if (lowPriority) {
            builder.priority(Priority.LOW);
        } else {
            builder.priority(Priority.NORMAL);
        }
        if (thumbnail != null) {
            builder.thumbnail(thumbnail);
        }
        if (transformation != null) {
            builder.transform(transformation);
        }
        if (animator != null) {
            builder.animate(animator);
        }
        builder.into(view);
    }

    private static void loadImage(Context context, ImageView view,
                                  int resId, int width, int height, boolean dontAnimate,
                                  @Nullable BitmapTransformation transformation,
                                  @Nullable final OnLoadImageListener l) {
        DrawableRequestBuilder<Integer> builder = Glide
                .with(context)
                .load(resId)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<Integer, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e,
                                               Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (l != null) {
                            l.onLoadFailed();
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache, boolean isFirstResource) {
                        if (l != null) {
                            l.onLoadSucceed();
                        }
                        return false;
                    }
                });
        if (width != 0 && height != 0) {
            builder.override(width, height);
        }
        if (dontAnimate) {
            builder.dontAnimate();
        }
        if (transformation != null) {
            builder.transform(transformation);
        }
        builder.into(view);
    }

    public static void loadImage(Context context, ImageView view, File file) {
        if (file.exists()) {
            Glide.with(context)
                    .load(file)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(view);
        }
    }

    // animation.

    public static void startSaturationAnimation(Context c, final ImageView target) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            target.setHasTransientState(true);
            final AnimUtils.ObservableColorMatrix matrix = new AnimUtils.ObservableColorMatrix();
            final ObjectAnimator saturation = ObjectAnimator.ofFloat(
                    matrix, AnimUtils.ObservableColorMatrix.SATURATION, 0f, 1f);
            saturation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener
                    () {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    target.setColorFilter(new ColorMatrixColorFilter(matrix));
                }
            });
            saturation.setDuration(2000L);
            saturation.setInterpolator(AnimUtils.getFastOutSlowInInterpolator(c));
            saturation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    target.clearColorFilter();
                    target.setHasTransientState(false);
                }
            });
            saturation.start();
        }
    }

    // background.

    public static int computeCardBackgroundColor(String color) {
        if (TextUtils.isEmpty(color)
                || (!Pattern.compile("^#[a-fA-F0-9]{6}").matcher(color).matches()
                && !Pattern.compile("^[a-fA-F0-9]{6}").matcher(color).matches())) {
            return Color.argb(0, 0, 0, 0);
        } else {
            if (Pattern.compile("^[a-fA-F0-9]{6}").matcher(color).matches()) {
                color = "#" + color;
            }
            int backgroundColor = Color.parseColor(color);
            int red = ((backgroundColor & 0x00FF0000) >> 16);
            int green = ((backgroundColor & 0x0000FF00) >> 8);
            int blue = (backgroundColor & 0x000000FF);
            if (Mysplash.getInstance().isLightTheme()) {
                return Color.rgb(
                        (int) (red + (255 - red) * 0.7),
                        (int) (green + (255 - green) * 0.7),
                        (int) (blue + (255 - blue) * 0.7));
            } else {
                return Color.rgb(
                        (int) (red * 0.3),
                        (int) (green * 0.3),
                        (int) (blue * 0.3));
            }
        }
    }

    // data

    public static void releaseImageView(ImageView view) {
        Glide.clear(view);
    }

    /** <br> swipeListener. */

    public interface OnLoadImageListener {
        void onLoadSucceed();
        void onLoadFailed();
    }
}
