package com.duole.launcher.privacy.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.duole.launcher.R;
import com.duole.launcher.privacy.utils.PrivacyUtils;

public class PrivacyContent  extends Activity {
    private WebView mWebView = null;
    private ImageView mBackButton = null;
    private TextView mWelcomeTextView = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  设置activity界面布局
        PrivacyUtils.initActivityUI(this);
        initView();
        loadData();
        setListener();
    }

    /**
     * 网页视图初始化；
     */
    private void initView() {
        setContentView(R.layout.privacy_content_layout);
        mWebView = findViewById(R.id.webView);
        mBackButton = findViewById(R.id.backButton);
        mWelcomeTextView = findViewById(R.id.welcomeTextView);
        mBackButton.setImageResource(R.drawable.duole_privacy_back);
    }

    /**
     * 返回按钮侦听
     */
    private void setListener() {
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.privacy_anim_in, R.anim.privacy_anim_out);
            }
        });
    }

    /**
     * 加载数据
     */
    private void loadData() {
        if (null != this.getIntent()) {
            String url = this.getIntent().getStringExtra("url");
            if (!TextUtils.isEmpty(url)) {
                loadUrl(url);
            } else {
                finish();
                overridePendingTransition(R.anim.privacy_anim_in, R.anim.privacy_anim_out);
            }
        }
    }

    /**
     * 加载网页
     *
     * @param url 链接地址
     */
    private void loadUrl(String url) {
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mWelcomeTextView.setVisibility(View.GONE);
                } else {
                    mWelcomeTextView.setVisibility(View.VISIBLE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.loadUrl(url);
        mWebView.getSettings().setJavaScriptEnabled(true);
        // 系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            // 链接跳转都会走这个方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);// 强制在当前 WebView 中加载 url，不调用会打开系统浏览器
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed(); // 兼容https
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;
            ViewParent parent = mWebView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mWebView);
            }
            mWebView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            mWebView.getSettings().setJavaScriptEnabled(false);
            mWebView.clearHistory();
            mWebView.clearView();
            mWebView.removeAllViews();
            mWebView.destroy();
        }
        super.onDestroy();
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
