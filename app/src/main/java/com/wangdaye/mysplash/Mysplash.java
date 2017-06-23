package com.wangdaye.mysplash;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.text.TextUtils;

import com.wangdaye.mysplash.common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash.common._basic.MysplashActivity;
import com.wangdaye.mysplash.common.utils.manager.CustomApiManager;
import com.wangdaye.mysplash.main.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Mysplash.
 *
 * Application class for Mysplash.
 *
 * */

public class Mysplash extends Application {

    private static Mysplash instance;

    public static Mysplash getInstance() {
        return instance;
    }

    private List<MysplashActivity> activityList;

    private Photo photo;

    public static final String UNSPLASH_API_BASE_URL = "https://api.unsplash.com/";
    public static final String STREAM_API_BASE_URL = "https://api.getstream.io/";
    public static final String UNSPLASH_FOLLOWING_FEED_URL = "napi/feeds/following";
    public static final String UNSPLASH_NOTIFICATION_URL = "napi/feeds/enrich";
    public static final String UNSPLASH_URL = "https://unsplash.com/";
    public static final String UNSPLASH_JOIN_URL = "https://unsplash.com/join";
    public static final String UNSPLASH_SUBMIT_URL = "https://unsplash.com/submit";
    public static final String UNSPLASH_LOGIN_CALLBACK = "unsplash-auth-callback";

    public static final String DATE_FORMAT = "yyyy/MM/dd";
    public static final String DOWNLOAD_PATH = "/Pictures/Mysplash/";
    public static final String DOWNLOAD_PHOTO_FORMAT = ".jpg";
    public static final String DOWNLOAD_COLLECTION_FORMAT = ".zip";
    @StringDef({DOWNLOAD_PHOTO_FORMAT, DOWNLOAD_COLLECTION_FORMAT})
    public @interface DownloadFormatRule {}

    public static final int DEFAULT_PER_PAGE = 15;
    @IntRange(from = 1, to = 30)
    public @interface PerPageRule {}

    @IntRange(from = 1)
    public @interface PageRule {}

    public static final int CATEGORY_TOTAL_NEW = 0;
    public static final int CATEGORY_TOTAL_FEATURED = 1;
    public static final int CATEGORY_BUILDINGS_ID = 2;
    public static final int CATEGORY_FOOD_DRINK_ID = 3;
    public static final int CATEGORY_NATURE_ID = 4;
    public static final int CATEGORY_OBJECTS_ID = 8;
    public static final int CATEGORY_PEOPLE_ID = 6;
    public static final int CATEGORY_TECHNOLOGY_ID = 7;
    @IntDef({CATEGORY_TOTAL_NEW, CATEGORY_TOTAL_FEATURED})
    public @interface PhotosTypeRule {}
    @IntDef({
            CATEGORY_BUILDINGS_ID,
            CATEGORY_FOOD_DRINK_ID,
            CATEGORY_NATURE_ID,
            CATEGORY_OBJECTS_ID,
            CATEGORY_PEOPLE_ID,
            CATEGORY_TECHNOLOGY_ID})
    public @interface CategoryIdRule {}

    public static int TOTAL_NEW_PHOTOS_COUNT = 17444;
    public static int TOTAL_FEATURED_PHOTOS_COUNT = 1192;
    public static int BUILDING_PHOTOS_COUNT = 2720;
    public static int FOOD_DRINK_PHOTOS_COUNT = 650;
    public static int NATURE_PHOTOS_COUNT = 54208;
    public static int OBJECTS_PHOTOS_COUNT = 2150;
    public static int PEOPLE_PHOTOS_COUNT = 3410;
    public static int TECHNOLOGY_PHOTOS_COUNT = 350;

    public static final int PHOTO_ACTIVITY = 1;
    public static final int COLLECTION_ACTIVITY = 2;
    public static final int USER_ACTIVITY = 3;
    public static final int ME_ACTIVITY = 4;
    public static final int CUSTOM_API_ACTIVITY = 5;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        activityList = new ArrayList<>();
    }

    public static String getAppId(Context c, boolean auth) {
        if (isDebug(c)) {
            return BuildConfig.APP_ID_BETA;
        } else if (TextUtils.isEmpty(CustomApiManager.getInstance(c).getCustomApiKey())
                || TextUtils.isEmpty(CustomApiManager.getInstance(c).getCustomApiSecret())) {
            if (auth) {
                return BuildConfig.APP_ID_RELEASE;
            } else {
                return BuildConfig.APP_ID_RELEASE_UNAUTH;
            }
        } else {
            return CustomApiManager.getInstance(c).getCustomApiKey();
        }
    }

    public static String getSecret(Context c) {
        if (isDebug(c)) {
            return BuildConfig.SECRET_BETA;
        } else if (TextUtils.isEmpty(CustomApiManager.getInstance(c).getCustomApiKey())
                || TextUtils.isEmpty(CustomApiManager.getInstance(c).getCustomApiSecret())) {
            return BuildConfig.SECRET_RELEASE;
        } else {
            return CustomApiManager.getInstance(c).getCustomApiSecret();
        }
    }

    public static boolean isDebug(Context c) {
        try {
            return (c.getApplicationInfo().flags
                    & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception ignored) {

        }
        return false;
    }

    public static String getLoginUrl(Context c) {
        return Mysplash.UNSPLASH_URL + "oauth/authorize"
                + "?client_id=" + getAppId(c, true)
                + "&redirect_uri=" + "mysplash%3A%2F%2F" + UNSPLASH_LOGIN_CALLBACK
                + "&response_type=" + "code"
                + "&scope=" + "public+read_user+write_user+read_photos+write_photos+write_likes+write_followers+read_collections+write_collections";
    }

    public void addActivity(@NonNull MysplashActivity a) {
        for (MysplashActivity activity : activityList) {
            if (activity.equals(a)) {
                return;
            }
        }
        activityList.add(a);
    }

    public void addActivityToFirstPosition(@NonNull MysplashActivity a) {
        for (MysplashActivity activity : activityList) {
            if (activity.equals(a)) {
                return;
            }
        }
        activityList.add(0, a);
    }

    public void removeActivity(MysplashActivity a) {
        activityList.remove(a);
    }

    @Nullable
    public MysplashActivity getTopActivity() {
        if (activityList != null && activityList.size() > 0) {
            return activityList.get(activityList.size() - 1);
        } else {
            return null;
        }
    }

    @Nullable
    public MainActivity getMainActivity() {
        if (activityList != null && activityList.size() > 0) {
            for (int i = 0; i < activityList.size(); i ++) {
                if (activityList.get(i) instanceof MainActivity) {
                    return (MainActivity) activityList.get(i);
                }
            }
        }
        return null;
    }

    public int getActivityCount() {
        if (activityList != null) {
            return activityList.size();
        } else {
            return 0;
        }
    }

    @Nullable
    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }
}
