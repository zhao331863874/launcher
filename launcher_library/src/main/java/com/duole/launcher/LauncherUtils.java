package com.duole.launcher;

import android.app.Activity;
import android.content.Intent;

import com.duole.launcher.privacy.open.Privacy;
import com.duole.launcher.privacy.utils.ShadeViewUtils;

public class LauncherUtils {
    /**
     * 展示《多乐游戏用户服务协议》；
     * @param activity
     */
    public static void showPrivacyAgreement(Activity activity) {
        Privacy.showPrivacyAgreement(activity);
    }

    /**
     * 展示《多乐游戏隐私保护指引》
     * @param activity
     */
    public static void showPrivacyGuide(Activity activity) {
        Privacy.showPrivacyGuide(activity);
    }

    /**
     * 业务层设置当前隐私协议版本号；
     * @param activity  上下文对象
     * @param version   隐私协议版本
     */
    public static void setPrivacyVersion(Activity activity, String version) {
        Privacy.setPrivacyVersion(activity, version);
    }

    /**
     * 设置复选框状态（是否同意隐私协议）；
     * @param activity  上下文对象
     * @param nFlag     0 - 取消隐私协议授权，1 - 同意隐私协议
     */
    public static void setPrivacyCheckBox(Activity activity, int nFlag) {
        Privacy.setPrivacyAgreeFlag(activity, nFlag);
    }

    /**
     * 获取启动游戏Intent传值（判断是否从游戏中心启动）
     * @return
     */
    public static Intent getLauncherIntent() {
        return Privacy.getLauncherIntent();
    }

    // 显示ShadeImage遮罩（遮住加载游戏期间黑屏）
    public static void showShadeImageView(Activity activity) {
        ShadeViewUtils.showShadeImageView(activity);
    }

    // 移除ShadeImage遮罩
    public static void removeShadeImageView() {
        ShadeViewUtils.removeShadeImageView();
    }

}
