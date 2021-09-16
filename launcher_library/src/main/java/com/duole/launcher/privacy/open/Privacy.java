package com.duole.launcher.privacy.open;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import com.duole.launcher.privacy.utils.PrivacyConstants;
import com.duole.launcher.privacy.utils.PrivacyUtils;
import com.duole.launcher.privacy.utils.PrivacySettings;
import com.duole.launcher.privacy.utils.SharedPreferencesUtils;

public class Privacy {
    public static Intent mIntent = null;

    public static Intent getLauncherIntent() {
        return mIntent;
    }

    /**
     * 显示多乐游戏用户协议；
     * @param activity
     */
    public static void showPrivacyAgreement(Activity activity) {
        PrivacyUtils.showPrivacyContent(activity, PrivacySettings.mPrivacyAgreementUrl);
    }

    /**
     * 显示多乐游戏隐私保护指引
     * @param activity
     */
    public static void showPrivacyGuide(Activity activity) {
        PrivacyUtils.showPrivacyContent(activity, PrivacySettings.mPrivacyGuidetUrl);
    }
    /**
     * 读取隐私标记
     * @param activity
     * @return 0 - 玩家拒绝，1 - 玩家同意
     */
    public static int getPrivacyAgreeFlag(Activity activity) {
        return (Integer) SharedPreferencesUtils.getParam(activity, PrivacyConstants.PRIVACY_AGREE_TAG, 0);
    }
    public static int getPrivacyUpdateFlag(Activity activity) {
        return (Integer) SharedPreferencesUtils.getParam(activity, PrivacyConstants.PRIVACY_UPDATE_TAG, 0);
    }
    public static int getPassbyMode(Activity activity) {
        return (Integer) SharedPreferencesUtils.getParam(activity, PrivacyConstants.PRIVACY_PASSBY_TAG, 0);
    }

    public static void setPrivacyAgreeFlag(Activity activity, int nAgree) {
        SharedPreferencesUtils.setParam(activity, PrivacyConstants.PRIVACY_AGREE_TAG, nAgree);
    }
    public static void setPrivacyUpdateFlag(Activity activity, int nUpdate) {
        SharedPreferencesUtils.setParam(activity, PrivacyConstants.PRIVACY_UPDATE_TAG, nUpdate);
    }
    public static void setPassbyMode(Activity activity, int nPassbyMode) {
        SharedPreferencesUtils.setParam(activity, PrivacyConstants.PRIVACY_PASSBY_TAG, nPassbyMode);
    }

    /**
     * 玩家是否已经同意隐私协议。拒绝或首次进入游戏，均视为未同意隐私协议；
     * @param activity
     * @return
     */
    public static boolean isPrivacyAgree(Activity activity) {
        //  检查玩家是否已同意隐私协议；
        if (PrivacyConstants.PRIVACY_AGREE_YES == getPrivacyAgreeFlag(activity)) {
            return true;
        }
        return false;
    }

    /**
     * 检测隐私协议是否有更新；
     * @param activity
     * @return
     */
    public static boolean isPrivacyUpdate(Activity activity) {
        //  检查隐私协议是否已更新；
        if (PrivacyConstants.PRIVACY_UPDATE_YES == getPrivacyUpdateFlag(activity)) {
            return true;
        }
        return false;
    }

    public static void setPrivacyVersion(Activity activity, String version) {
        if (activity != null && (!TextUtils.isEmpty(version))) {
            //  读取本地存储的隐私政策版本号;
            String localVer = (String) SharedPreferencesUtils.getParam( activity, PrivacyConstants.PRIVACY_VERSION, "");
            //  本地未记录隐私版本，判断为首次查看隐私协议
            if (TextUtils.isEmpty(localVer)) {
                SharedPreferencesUtils.setParam( activity, PrivacyConstants.PRIVACY_VERSION, version);
                setPrivacyUpdateFlag(activity, PrivacyConstants.PRIVACY_UPDATE_NO);
            } else if (!version.equals(localVer)) {
                //  隐私版本有更新
                SharedPreferencesUtils.setParam( activity, PrivacyConstants.PRIVACY_VERSION, version);
                setPrivacyUpdateFlag(activity, PrivacyConstants.PRIVACY_UPDATE_YES);
            } else {
                setPrivacyUpdateFlag(activity, PrivacyConstants.PRIVACY_UPDATE_NO);
            }
        }
    }
}
