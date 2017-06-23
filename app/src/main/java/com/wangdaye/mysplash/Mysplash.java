package com.wangdaye.mysplash;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.wangdaye.mysplash._common.data.entity.unsplash.Photo;
import com.wangdaye.mysplash._common._basic.MysplashActivity;
import com.wangdaye.mysplash._common.utils.manager.ApiManager;
import com.wangdaye.mysplash._common.utils.manager.AuthManager;
import com.wangdaye.mysplash.main.view.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * My application.
 * */

public class Mysplash extends Application {
    // data
    private List<MysplashActivity> activityList;
    private boolean lightTheme;
    private String language;
    private String defaultPhotoOrder;
    private String defaultCollectionType;
    private String downloadScale;
    private String backToTopType;
    private boolean notifiedSetBackToTop;

    private String customApiKey;
    private String customApiSecret;

    // transfer
    private Photo photo;

    // Unsplash url.
    public static final String UNSPLASH_API_BASE_URL = "https://api.unsplash.com/";
    public static final String UNSPLASH_URL = "https://unsplash.com/";
    public static final String UNSPLASH_JOIN_URL = "https://unsplash.com/join";
    public static final String UNSPLASH_SUBMIT_URL = "https://unsplash.com/submit";
    public static final String UNSPLASH_LOGIN_CALLBACK = "unsplash-auth-callback";

    // application data.
    public static final String DATE_FORMAT = "yyyy/MM/dd";
    public static final String DOWNLOAD_PATH = "/Pictures/Mysplash/";
    public static final String DOWNLOAD_PHOTO_FORMAT = ".jpg";
    public static final String DOWNLOAD_COLLECTION_FORMAT = ".zip";

    public static final int DEFAULT_PER_PAGE = 15;

    public static final int CATEGORY_TOTAL_NEW = 0;
    public static final int CATEGORY_TOTAL_FEATURED = 1;
    public static final int CATEGORY_BUILDINGS_ID = 2;
    public static final int CATEGORY_FOOD_DRINK_ID = 3;
    public static final int CATEGORY_NATURE_ID = 4;
    public static final int CATEGORY_OBJECTS_ID = 8;
    public static final int CATEGORY_PEOPLE_ID = 6;
    public static final int CATEGORY_TECHNOLOGY_ID = 7;

    public static int TOTAL_NEW_PHOTOS_COUNT = 17444;
    public static int TOTAL_FEATURED_PHOTOS_COUNT = 1192;
    public static int BUILDING_PHOTOS_COUNT = 2720;
    public static int FOOD_DRINK_PHOTOS_COUNT = 650;
    public static int NATURE_PHOTOS_COUNT = 54208;
    public static int OBJECTS_PHOTOS_COUNT = 2150;
    public static int PEOPLE_PHOTOS_COUNT = 3410;
    public static int TECHNOLOGY_PHOTOS_COUNT = 350;

    // activity code.
    public static final int ME_ACTIVITY = 1;
    public static final int CUSTOM_API_ACTIVITY = 2;

    // permission code.
    public static final int WRITE_EXTERNAL_STORAGE = 1;
    public static final int READ_EXTERNAL_STORAGE = 2;

    /** <br> singleton. */

    private static Mysplash instance;

    public static Mysplash getInstance() {
        return instance;
    }

    /** <br> life cycle. */

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        activityList = new ArrayList<>();

        ApiManager.getInstance(this);
        AuthManager.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        lightTheme = sharedPreferences.getBoolean(getString(R.string.key_light_theme), true);
        language = sharedPreferences.getString(getString(R.string.key_language), "follow_system");
        defaultPhotoOrder = sharedPreferences.getString(getString(R.string.key_default_photo_order), "latest");
        defaultCollectionType = sharedPreferences.getString(getString(R.string.key_default_collection_type), "featured");
        downloadScale = sharedPreferences.getString(getString(R.string.key_download_scale), "compact");
        backToTopType = sharedPreferences.getString(getString(R.string.key_back_to_top), "all");
        notifiedSetBackToTop = sharedPreferences.getBoolean(getString(R.string.key_notified_set_back_to_top), false);

        String[] customApis = ApiManager.getInstance(this).readCustomApi();
        ApiManager.getInstance(this).destroy();
        customApiKey = customApis[0];
        customApiSecret = customApis[1];
    }

    /** <br> data. */

    public static String getAppId(Context c, boolean auth) {
        if (isDebug(c)) {
            return BuildConfig.APP_ID_BETA;
        } else if (TextUtils.isEmpty(getInstance().getCustomApiKey())
                || TextUtils.isEmpty(getInstance().getCustomApiSecret())) {
            if (auth) {
                return BuildConfig.APP_ID_RELEASE;
            } else {
                return BuildConfig.APP_ID_RELEASE_UNAUTH;
            }
        } else {
            return getInstance().getCustomApiKey();
        }
    }

    public static String getSecret(Context c) {
        if (isDebug(c)) {
            return BuildConfig.SECRET_BETA;
        } else if (TextUtils.isEmpty(getInstance().getCustomApiKey())
                || TextUtils.isEmpty(getInstance().getCustomApiSecret())) {
            return BuildConfig.SECRET_RELEASE;
        } else {
            return getInstance().getCustomApiSecret();
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

    public void addActivity(MysplashActivity a) {
        for (MysplashActivity activity : activityList) {
            if (activity.equals(a)) {
                return;
            }
        }
        activityList.add(a);
    }

    public void removeActivity(MysplashActivity a) {
        activityList.remove(a);
    }

    public MysplashActivity getTopActivity() {
        if (activityList != null && activityList.size() > 0) {
            return activityList.get(activityList.size() - 1);
        } else {
            return null;
        }
    }

    public MainActivity getMainActivity() {
        if (activityList != null && activityList.size() > 0
                && activityList.get(0) instanceof MainActivity) {
            return (MainActivity) activityList.get(0);
        } else {
            return null;
        }
    }

    public int getActivityCount() {
        if (activityList != null) {
            return activityList.size();
        } else {
            return 0;
        }
    }

    public boolean isLightTheme() {
        return lightTheme;
    }

    public void changeTheme() {
        this.lightTheme = !lightTheme;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDefaultPhotoOrder() {
        return defaultPhotoOrder;
    }

    public void setDefaultPhotoOrder(String order) {
        this.defaultPhotoOrder = order;
    }

    public String getDefaultCollectionType() {
        return defaultCollectionType;
    }

    public void setDefaultCollectionType(String type) {
        this.defaultCollectionType = type;
    }

    public String getDownloadScale() {
        return downloadScale;
    }

    public void setDownloadScale(String scale) {
        this.downloadScale = scale;
    }

    public String getBackToTopType() {
        return backToTopType;
    }

    public void setBackToTopType(String type) {
        this.backToTopType = type;
    }

    public boolean isNotifiedSetBackToTop() {
        return notifiedSetBackToTop;
    }

    public void setNotifiedSetBackToTop() {
        this.notifiedSetBackToTop = true;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public String getCustomApiKey() {
        return customApiKey;
    }

    public void setCustomApiKey(String customApiKey) {
        this.customApiKey = customApiKey;
    }

    public String getCustomApiSecret() {
        return customApiSecret;
    }

    public void setCustomApiSecret(String customApiSecret) {
        this.customApiSecret = customApiSecret;
    }
}
