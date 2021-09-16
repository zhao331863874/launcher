package com.duole.launcher.privacy.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.duole.launcher.splash.SplashActivity;
import com.duole.launcher.splash.SplashSettings;

public class ShadeViewUtils {
    private static final String TAG = "ShadeViewUtils";
    //	遮罩层控制相关变量；
    private static Handler mUIHandler = null;
    private static ImageView mShadeIm = null;

    // 显示ShadeImage遮罩（遮住加载游戏期间黑屏）
    public static void showShadeImageView(Activity activity) {
        if (createShadeImageView(activity) == null) {
            Log.e(TAG, "遮罩图片资源为null");
            return;
        }
        mUIHandler = new Handler();
        // 显示launch image 遮住「黑屏」
        activity.addContentView(createShadeImageView(activity),
                new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN));
    }

    public static void removeShadeImageView() {
        if(mUIHandler == null) {
            return;
        }
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mShadeIm != null) {
                    mShadeIm.setImageDrawable(null);
                }
            }
        });
    }

    // 创建一个ImageView，mShadeIm是遮罩图片
    private static ImageView createShadeImageView(Activity activity) {
        //  构造加载进入游戏遮罩图片
        Bitmap imgVer = SplashActivity.getImageFromAssetsFile(activity, SplashSettings.mShadeImageFile);
        if (imgVer == null) {
            return null;
        }

        mShadeIm = new ImageView(activity);
        mShadeIm.setImageBitmap(imgVer);
        mShadeIm.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return mShadeIm;
    }
}
