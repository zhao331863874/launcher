package com.duole.launcher.privacy.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.duole.launcher.R;
import com.duole.launcher.privacy.callback.PrivacyDialogCallback;
import com.duole.launcher.privacy.open.Privacy;
import com.duole.launcher.privacy.ui.PrivacyDialog;

public class PrivacyController {
    private static final String TAG = "PrivacyController";
    private static PrivacyController sInstance = null;
    private Activity mActivity = null;

    /**
     * 单例
     *
     * @return
     */
    public static PrivacyController getsInstance() {
        if (sInstance == null) {
            sInstance = new PrivacyController();
        }
        return sInstance;
    }

    /**
     * 显示隐私对话框；
     */
    public void showPrivacyDialog(Activity activity, PrivacyDialogCallback dialogCallback) {
        if (null == activity) {
            return;
        }
        mActivity = activity;
        PrivacyDialog.setDialogCallback(dialogCallback);

        //  展示弹窗；
        Intent intent = new Intent(mActivity, PrivacyDialog.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(intent);
        activity.overridePendingTransition(R.anim.privacy_anim_in, R.anim.privacy_anim_out);
        Log.i(TAG, "showPrivacyDialog");
    }

}
