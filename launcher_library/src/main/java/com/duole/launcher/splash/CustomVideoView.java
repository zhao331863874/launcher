package com.duole.launcher.splash;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.VideoView;

public class CustomVideoView extends VideoView {
    private float mScale ;
    private boolean mIsLandscape = true;

    public CustomVideoView(Context context) {
        super(context);
    }
    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScale(float scale) {
        this.mScale = scale;
    }
    public void setOrientation(boolean isLandscape) { mIsLandscape = isLandscape; }

    @Override
    //重写onMeasure方法，改变长宽
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getDefaultSize(0,widthMeasureSpec);
        int height = getDefaultSize(0,heightMeasureSpec);

        // 调整尺寸，适配高度
        if (width > 0 && height > 0 && mScale > 0.0) {
            if (mIsLandscape) {
                width = (int) (height * mScale);
            } else {
                height = (int) (width / mScale);
            }
        }

        setMeasuredDimension(width,height);
    }
    @Override
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        super.setOnPreparedListener(l);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}

/*
横版适配高度
视频尺寸：1920 x 864   宽高比：2.222

适配高度：
width = height * 2.222;

示例：
屏幕尺寸：
1920 x 1080   =>  2400 x 1080

计算方式：
width = 1080 * 2.222 = 2400
显示尺寸：2400 x 1080

竖版适配宽度

视频尺寸：720 x 1600   宽高比：0.45

适配宽度：
height = width / 0.45

示例：
屏幕尺寸：
1080 x 1920 => 1080 X 2400
计算方式：
height = 1080/0.45
显示尺寸：1080 X 2400

 */
