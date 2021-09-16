package com.duole.launcher.privacy.utils;

public class PrivacyConstants {

    // 本地存储文件:SharedPreferences
    public static final String PRIVACY_SP_NAME = "DUOLE_PRIVACY_SP";
    /**
     * 玩家进入游戏的方式:[0, 1, 2]；
     * 0,此前已同意隐私政策且隐私协议无更新，直接进入游戏；
     * 1,首次弹出隐私政策，点击同意后进入游戏;
     * 2,隐私协议有更新，点击同意后进入游戏;
     */
    public static final String PRIVACY_PASSBY_TAG = "privacy_passby_flag";
    public static final int PRIVACY_PASSBY_NONE = 0;
    public static final int PRIVACY_PASSBY_AGREE = 1;
    public static final int PRIVACY_PASSBY_UPDATE = 2;

    // 隐私政策版本号，应用层通过接口设置；
    public static final String PRIVACY_VERSION = "privacy_version";

    // 隐私协议更新标识：[0, 1]
    // 应用层通过接口设置隐私版本号时与本地存储的版本号进行比对，相同则此标识的值设置为 0，反之为 1;
    public static final String PRIVACY_UPDATE_TAG = "privacy_update_flag";
    public static final int PRIVACY_UPDATE_YES = 1; // 有更新
    public static final int PRIVACY_UPDATE_NO = 0; // 无更新

    // 玩家首次进来，同意或拒绝隐私协议；
    public static final String PRIVACY_AGREE_TAG = "privacy_agree_flag";
    public static final int PRIVACY_AGREE_YES = 1; // 玩家同意隐私协议
    public static final int PRIVACY_AGREE_NO = 0; // 玩家拒绝隐私协议

}
