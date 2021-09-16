package com.duole.launcher.privacy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.view.TouchDelegate;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.duole.launcher.R;
import com.duole.launcher.privacy.ui.PrivacyContent;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

public class PrivacyUtils {
    /**
     * activity界面主题、布局设置；
     * @param activity
     */
    public static void initActivityUI(Activity activity) {
        // 隐藏标题栏
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideBottomUIMenu(activity);
        fullScreenImmersive(activity.getWindow().getDecorView());
        displayAdapterShortEdges(activity, false);
    }
    /**
     * 隐藏底部虚拟键盘
     */
    public static void hideBottomUIMenu(Activity activity) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT < 19) { // lower api
            View v = activity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            v.setSystemUiVisibility(uiOptions);
        } else {
            //for new api versions.
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    /**
     * 完全隐藏虚拟键盘
     *
     * @param activity
     */
    public static void completeHideBottomUIMenu(Activity activity) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = activity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            Window window = activity.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
            window.setAttributes(params);
        }
    }

    /**
     * 扩大view的点击区域
     *
     * @param view
     * @param size 在view四周增加size pix大小的点击区域
     */
    public static void expandTouchArea(final View view, final int size) {
        final View parentView = (View) view.getParent();
        parentView.post(new Runnable() {
            @Override
            public void run() {
                Rect rect = new Rect();
                view.getHitRect(rect);
                rect.top -= size;
                rect.bottom += size;
                rect.left -= size;
                rect.right += size;
                parentView.setTouchDelegate(new TouchDelegate(rect, view));
            }
        });
    }
    /**
     * 隐藏view
     * @param view
     */
    public static void goneView(View view) {
        if (null != view) {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * 显示view
     * @param view
     */
    public static void showView(View view) {
        if (null != view) {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 富文本文字
     * @param strPermission
     * @return
     */
    public static String getRichTextMessage(String strPermission) {
        String and = "<font>和</font>";
        String caesuraSign = "<font>、</font>";

        String[] array = strPermission.split(",");
        StringBuilder permissionDetails = new StringBuilder();
        try {
            for (int i = 0; i < array.length; i++) {
                if (array.length == 1) {
                    permissionDetails.append("<font color=\"#d28119\">" + array[i].toString() + "</font>");
                } else if (array.length == 2) {
                    permissionDetails.append("<font color=\"#d28119\">" + array[i].toString() + "</font>" + (i == 1 ? "" : and));
                } else {
                    permissionDetails.append("<font color=\"#d28119\">" + array[i].toString() + "</font>" + (i == array.length - 2 ? and : (i == array.length - 1 ? "" : caesuraSign)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return permissionDetails.toString();
    }
    /**
     * 获取屏幕方向
     *
     * @param context
     * @return
     */
    public static boolean getOrientation(Context context) {
        return context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT;
    }

    public static String getAppName(Activity activity) {
        try {
            PackageManager packageManager = activity.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    activity.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return activity.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 延迟关闭activity
     *
     * @param activity
     * @param time
     */
    public static void finishActivity(final Activity activity, final long time) {
        if (null == activity) {
            return;
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                activity.finish();
            }
        }, time);
    }

    /**
     * 跳转到主游戏
     */
    public static void startAppActivity(final Activity activity, String appActivityName) {
        if (null == activity || TextUtils.isEmpty(appActivityName)) {
            return;
        }

        Class clz = null;
        try {
            clz = Class.forName(appActivityName);
            activity.startActivity(new Intent(activity, clz));
            activity.overridePendingTransition(R.anim.privacy_fade_in, R.anim.privacy_fade_out);
            activity.finish();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void showPrivacyContent(Activity activity, String url) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(activity, PrivacyContent.class);
                intent.putExtra("url", url);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.privacy_anim_in, R.anim.privacy_anim_out);
            }
        });
    }

    /**
     * 加载指定资源文件；
     *
     * @param context
     * @param fileName
     * @return
     */
    public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    // 设置页面全屏显示
    private static void fullScreenImmersive(View view) {
        if (Build.VERSION.SDK_INT >= 19) {
            int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            view.setSystemUiVisibility(uiOptions);
        }
    }

    // 延伸显示区域到刘海
    public static void displayAdapterShortEdges(Activity activity, boolean showHortEdges) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        try {
            Field field = lp.getClass().getField("layoutInDisplayCutoutMode");
            if (field != null) {
                Field constValue;
                if(showHortEdges) {
                    constValue = lp.getClass().getDeclaredField("LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER");
                } else {
                    constValue = lp.getClass().getDeclaredField("LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES");
                }
                field.setInt(lp, constValue.getInt(null));
                activity.getWindow().setAttributes(lp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
