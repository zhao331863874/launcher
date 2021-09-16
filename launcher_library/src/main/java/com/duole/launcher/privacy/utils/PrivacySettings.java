package com.duole.launcher.privacy.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class PrivacySettings {
    // 配置文件标签定义；
    public static final String PRIVACY_AGREEMENT_URL = "privacy_agreement_url";
    public static final String PRIVACY_GUIDE_URL = "privacy_guide_url";
    public static final String PRIVACY_MESSAGE = "privacy_message";
    public static final String PRIVACY_UPDATE_MESSAGE = "privacy_update_message";
    public static final String PERMISSION = "permission";
    public static final String PERMISSION_MESSAGE = "permission_message";

    //  配置项默认值
    public static  String mPrivacyDialogBg = "";
    public static  String mPrivacyAgreementUrl = "";
    public static  String mPrivacyGuidetUrl = "";
    public static  String mPrivacyMessage = "";
    public static  String mPrivacyUpdateMessage = "";
    public static  String mPermission = "";
    public static  String mPermissionMessage = "";

    public static void init(Activity activity) {
        try {
            ApplicationInfo appInfo = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
            mPrivacyAgreementUrl = appInfo.metaData.getString(PRIVACY_AGREEMENT_URL);
            mPrivacyGuidetUrl = appInfo.metaData.getString(PRIVACY_GUIDE_URL);
            mPrivacyMessage = appInfo.metaData.getString(PRIVACY_MESSAGE);
            mPrivacyUpdateMessage = appInfo.metaData.getString(PRIVACY_UPDATE_MESSAGE);
            mPermission = PrivacyUtils.getRichTextMessage(appInfo.metaData.getString(PERMISSION));
            mPermissionMessage = appInfo.metaData.getString(PERMISSION_MESSAGE);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
