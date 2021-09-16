package com.duole.launcher.splash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.duole.launcher.R;
import com.duole.launcher.privacy.callback.PrivacyDialogCallback;
import com.duole.launcher.privacy.open.Privacy;
import com.duole.launcher.privacy.utils.PrivacyConstants;
import com.duole.launcher.privacy.utils.PrivacyController;
import com.duole.launcher.privacy.utils.PrivacySettings;
import com.duole.launcher.privacy.utils.PrivacyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {
    private final String TAG = "SplashActivity";
    //  视频显示和控制
    private CustomVideoView mVideoView = null;
    //  视频是否播放完毕
    private boolean mIsVideoFinished = false;
    //  屏幕方向: 默认横屏
    private boolean mIsLandscape = true;
    //  设计尺寸：1280 X 720
    private final int mDesignWidth = 1280;
    private final int mDesignHeight = 720;

    //  版号展示结束
    private boolean mIsSolganFinished = false;
    //  视频开始播放后多少毫秒，版号展示结束；
    private int mSolganShowTime = 4000;
    private ImageView mSolgan = null;
    private Handler mHandler = new Handler();

    private SplashActivity.OnFinishedListener mOnFinishedListener = null;
    public interface OnFinishedListener {
        void onFinished();
    }

    private void checkStartFromGameCenter(Intent intent) {
        Privacy.mIntent = (Intent)intent.clone();
    }
    @Override
    protected void onNewIntent(Intent   intent) {
        super.onNewIntent(intent);
        checkStartFromGameCenter(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!isTaskRoot()) {
            //如果是在栈底
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                //当作为启动页时候。如果是其他页面跳转进来，就不需要finish
                finish();
            }
        }

        checkStartFromGameCenter(getIntent());
        //  获取屏幕方向；
        initOrientation();
        SplashSettings.init(this);
        PrivacySettings.init(this);

        PrivacyUtils.initActivityUI(this);
        initView();
    }

    private void initOrientation() {
        int ori = this.getResources().getConfiguration().orientation; //获取屏幕方向
        if(ori == Configuration.ORIENTATION_LANDSCAPE) {
            mIsLandscape = true;
        } else {
            mIsLandscape = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void initView() {
        createVideoView();
        if (SplashSettings.mLoadVideo) {
            mVideoView = (CustomVideoView) findViewById(R.id.video_view);
            mVideoView.setBackgroundColor(Color.WHITE);

            Uri videoPath = null;

            if(mIsLandscape) {
                videoPath = Uri.parse("android.resource://" + getPackageName() + "/raw/duole_splash_1920_864");
            } else {
                videoPath = Uri.parse("android.resource://" + getPackageName() + "/raw/duole_splash_720_1600");
            }
            mVideoView.setVideoURI(videoPath);
            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mVideoView.setBackgroundColor(Color.TRANSPARENT);
                    mIsVideoFinished = true;
                    finished();
                }
            });
            playVideo();
        } else {
            showLogo();
        }
    }

    private void createVideoView() {
        LayoutInflater mLayoutInflater = LayoutInflater.from(this);
        View view = mLayoutInflater.inflate(R.layout.duole_splash, null);
        RelativeLayout.LayoutParams relLayoutParams = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        addContentView(view, relLayoutParams);
    }

    private boolean isPlayFinished() {
        return (mIsVideoFinished && mIsSolganFinished) ;
    }
    private void finished() {
        //  视频和版号均展示完毕；
        if (isPlayFinished()) {
            //  如果设置了回调函数，播放完毕后通知调用方；
            if (mOnFinishedListener != null) {
                mOnFinishedListener.onFinished();
            }
            startNextActivity();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void adjustSlogan(Bitmap imgVer ) {
        if (mSolgan == null || imgVer == null) {
            return;
        }
        //  适配版号信息显示: 横竖版都按高度适配，同比例缩放；
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metric);
        int width = metric.widthPixels; // 宽度（PX）
        int height = metric.heightPixels; // 高度（PX）

        float scale = (float)height / (float)mDesignHeight;

        ViewGroup.LayoutParams lp = mSolgan.getLayoutParams();
        lp.width = width;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mSolgan.setLayoutParams(lp);

        mSolgan.setMaxWidth((int)((float)imgVer.getWidth() * scale));
        mSolgan.setMaxHeight((int)((float)imgVer.getHeight() * scale));
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private synchronized void  showSlogan() {
        mSolgan = (ImageView) findViewById(R.id.imgSlogan);
        mSolgan.setVisibility(View.GONE);
        mSolgan.clearAnimation();
        Bitmap imgVer = getImageFromAssetsFile(SplashActivity.this, SplashSettings.mVersionInfoFile);
        if (imgVer == null) {
            imgVer = BitmapFactory.decodeResource(getResources(),R.drawable.version_info);
        }
        mSolgan.setVisibility(View.VISIBLE);
        mSolgan.setImageBitmap(imgVer);
        //  显示适配；
        adjustSlogan(imgVer);
        //  版号信息渐现效果；
        AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(SplashActivity.this, R.anim.solgan_fade_in);
        mSolgan.startAnimation(alphaAnimation);

        //  确保展示 mSolganShowTime-2000 毫秒；
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mIsSolganFinished = true;
                finished();
            }
        }, mSolganShowTime);
    }

    public void setFinishedListener(SplashActivity.OnFinishedListener listener) {
        mOnFinishedListener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void playVideo() {
        //  视频加载侦听
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (isPlayFinished()) {
                    //  未解决问题：从隐私详情页回退时，splash视频被重新加载；
                    Log.i(TAG, "Replay");
                } else {
                    // 获取视频资源的宽度
                    int nVideoWidth = mp.getVideoWidth();
                    // 获取视频资源的高度
                    int nVideoHeight = mp.getVideoHeight();
                    float fScale = (float) nVideoWidth / (float) nVideoHeight;
                    mVideoView.setScale(fScale);
                    mVideoView.setOrientation(mIsLandscape);
                    //  准备完成后开始播放；
                    mp.start();
                    Log.i(TAG, "play");
                    //  控制slogan显示时机
                    showSlogan();
                    Log.i(TAG, "showSlogan");
                }
            }
        });

        //  播放信息侦听
        mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                //  视频帧开始渲染时设置背景透明；
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    mVideoView.setBackgroundColor(Color.TRANSPARENT);
                }
                return false;
            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }

    private void showLastFrame() {
        //  隐藏视频，替换显示最后一帧；
        ImageView imgFrameview = findViewById(R.id.imgLastFrame);
        if (mIsLandscape) {
            imgFrameview.setImageResource(R.drawable.duole_logo_landscape);
        } else {
            imgFrameview.setImageResource(R.drawable.duole_logo_portrait);
        }

        imgFrameview.setVisibility(View.VISIBLE);
        //mVideoView.setVisibility(View.GONE);
    }

    /**
     * 视频Logo展示结束后进行跳转；
     * 1.检查隐私标记，已授权直接进入游戏窗口；
     * 2.未授权或有更新，进入隐私授权窗口；
     */
    private void startNextActivity() {
        boolean isPrivacyAgree = Privacy.isPrivacyAgree(this);
        boolean isPrivacyUpdate = Privacy.isPrivacyUpdate(this);
        //  同意隐私协议，且隐私无更新，直接进入游戏；
        if (isPrivacyAgree && (!isPrivacyUpdate)) {
            Privacy.setPassbyMode(this, PrivacyConstants.PRIVACY_PASSBY_NONE);
            PrivacyUtils.startAppActivity(this, SplashSettings.mAppActivityName);
        } else {
            PrivacySettings.init(this);
            showPrivacyDialog();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showLastFrame();
                        }
                    });
                }
            }, 200);
        }
    }

    /**
     * 跳转到隐私启动窗口
     */
    private void startPrivacyStartActivity() {
        Class clz = null;
        try {
            clz = Class.forName(SplashSettings.PRIVACY_START_ACTIVITY);
            startActivity(new Intent(this, clz));
            overridePendingTransition(R.anim.privacy_anim_in, R.anim.privacy_anim_out);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void showPrivacyDialog() {
        PrivacyController.getsInstance().showPrivacyDialog(this, new PrivacyDialogCallback() {
            @Override
            public void agree() {
                Timer timer = new Timer();// 实例化Timer类
                timer.schedule(new TimerTask() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PrivacyUtils.startAppActivity(SplashActivity.this, SplashSettings.mAppActivityName);
                            }
                        });
                    }
                }, 10);
            }

            @Override
            public void reject() {
                Timer timer = new Timer();// 实例化Timer类
                timer.schedule(new TimerTask() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }
                }, 1);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showLogo() {
        //  显示静态Logo界面；
        ImageView imgFrameview = findViewById(R.id.imgLastFrame);
        if (mIsLandscape) {
            imgFrameview.setImageResource(R.drawable.duole_logo_landscape);
        } else {
            imgFrameview.setImageResource(R.drawable.duole_logo_portrait);
        }
        imgFrameview.setVisibility(View.VISIBLE);
        //  显示版本信息
        mSolgan = (ImageView) findViewById(R.id.imgSlogan);
        Bitmap imgVer = getImageFromAssetsFile(SplashActivity.this, SplashSettings.mVersionInfoFile);
        if (imgVer == null) {
            imgVer = BitmapFactory.decodeResource(getResources(),R.drawable.version_info);
        }
        mSolgan.setVisibility(View.VISIBLE);
        mSolgan.setImageBitmap(imgVer);
        //  显示适配；
        adjustSlogan(imgVer);

        //  展示静态Logo闪屏界面时间2000 毫秒；
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isPrivacyAgree = Privacy.isPrivacyAgree(SplashActivity.this);
                boolean isPrivacyUpdate = Privacy.isPrivacyUpdate(SplashActivity.this);
                //  同意隐私协议，且隐私无更新，直接进入游戏；
                if (isPrivacyAgree && (!isPrivacyUpdate)) {
                    Privacy.setPassbyMode(SplashActivity.this, PrivacyConstants.PRIVACY_PASSBY_NONE);
                    PrivacyUtils.startAppActivity(SplashActivity.this, SplashSettings.mAppActivityName);
                } else {
                    PrivacySettings.init(SplashActivity.this);
                    showPrivacyDialog();
                }
            }
        }, 2000);
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}