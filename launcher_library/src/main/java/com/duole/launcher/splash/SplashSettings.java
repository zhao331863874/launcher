package com.duole.launcher.splash;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class SplashSettings {
    //  游戏启动窗口标签名；
    public static final String TAG_APP_ACTIVITY_NAME = "app_activity_name";
    public static final String TAG_VERSION_INFO_FILE = "version_info_file";
    public static final String TAG_SHADE_IMAGE_FILE = "shade_image_file";
    //  隐私启动窗口类名；
    public static final String PRIVACY_START_ACTIVITY = "com.duole.launcher.privacy.ui.PrivacyDialog";
    //  是否播放视频配置
    public static final String TAG_LOAD_VIDEO = "load_video";

    public static  String mAppActivityName = "";
    public static  String mVersionInfoFile = "";
    public static  String mShadeImageFile = "";
    public static  boolean mLoadVideo = true;
    public static void init(Activity activity) {
        mAppActivityName = readMetaDataFromApplication(activity, TAG_APP_ACTIVITY_NAME);
        mVersionInfoFile = readMetaDataFromApplication(activity, TAG_VERSION_INFO_FILE);
        mShadeImageFile = readMetaDataFromApplication(activity, TAG_SHADE_IMAGE_FILE);
        mLoadVideo = Boolean.parseBoolean(readMetaDataFromApplication(activity, TAG_LOAD_VIDEO));
    }

    public static String readMetaDataFromActivity(Activity activity, String strKey)
    {
        String value = "";
        try {
            ActivityInfo info = activity.getPackageManager().getActivityInfo(activity.getComponentName(), PackageManager.GET_META_DATA);
            value = info.metaData.getString(strKey);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String readMetaDataFromApplication(Activity activity, String strKey) {
        try {
            ApplicationInfo appInfo = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                Object value = appInfo.metaData.get(strKey);
                return value == null ? "" : value.toString();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

}
