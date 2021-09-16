package com.duole.launcher.privacy.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duole.launcher.R;
import com.duole.launcher.privacy.callback.PrivacyDialogCallback;
import com.duole.launcher.privacy.open.Privacy;
import com.duole.launcher.privacy.utils.PrivacyConstants;
import com.duole.launcher.privacy.utils.PrivacyUtils;
import com.duole.launcher.privacy.utils.PrivacySettings;

public class PrivacyDialog  extends Activity {
    private static PrivacyDialogCallback mDialogCallback = null;
    private TextView mTitle;
    private ImageView mClose;
    private TextView mServiceAgreement;
    private TextView mUserGuide;
    private TextView mServiceAgreementHorizontal; // 横屏
    private TextView mUserGuideHorizontal; // 横屏
    private TextView mMessage;
    private TextView mMessageDetailedView;
    private TextView mNegtive;
    private Button mPositive;

    private LinearLayout mLinearLayout;
    private LinearLayout mGuideLayout;
    private LinearLayout mGuideLayoutHorizontal;

    private boolean mIsUpdate; // 更新标志
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  设置activity界面布局
        PrivacyUtils.initActivityUI(this);
        setContentView(R.layout.privacy_dialog_layout);
        // 防止点击空白区域消失
        setFinishOnTouchOutside(false);

        initView();
        //  加载并展示对话框
        doPrepared();
        //  挂接事件
        setListener();
    }

    private void initView() {
        mLinearLayout = findViewById(R.id.privacy_linearlayout);
        mGuideLayout = findViewById(R.id.guide_layout);
        mGuideLayoutHorizontal = findViewById(R.id.guide_layout_horizontal);
        mTitle = findViewById(R.id.duole_sdk_title);
        mClose = findViewById(R.id.duole_sdk_close);
        mServiceAgreement = findViewById(R.id.serviceagreement);
        mUserGuide = findViewById(R.id.userguide);
        mServiceAgreementHorizontal = findViewById(R.id.serviceagreement_horizontal);
        mUserGuideHorizontal = findViewById(R.id.userguide_horizontal);
        mMessage = findViewById(R.id.message);
        mMessageDetailedView = findViewById(R.id.message_detailed);
        mNegtive = findViewById(R.id.negtive);
        mPositive = findViewById(R.id.positive);
    }

    private void doPrepared() {
        updateData();
    }

    private void updateData() {
        mIsUpdate = Privacy.isPrivacyUpdate(this);
        //  隐私协议有更新
        if (mIsUpdate) {
            setDialog("重要提示" , PrivacySettings.mPrivacyUpdateMessage);
            if (null != mPositive) {
                mPositive.setText("同意并继续");
            }
            setOrientation(true);
            PrivacyUtils.goneView(mMessageDetailedView);
        } else {
            String appName = PrivacyUtils.getAppName(this);
            if (TextUtils.isEmpty(appName)) {
                appName = "多乐游戏";
            }

            setDialog("欢迎来到" + appName, PrivacySettings.mPrivacyMessage);
            setOrientation(false);
        }
    }

    private void setOrientation(boolean isNewMessage) {
        // 取控件当前的布局参数
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mLinearLayout.getLayoutParams();
        if (PrivacyUtils.getOrientation(this)) {
            if (isNewMessage) {
                PrivacyUtils.showView(mGuideLayout);
            } else {
                PrivacyUtils.showView(mGuideLayoutHorizontal);
            }
            // 设置宽度值
            params.width = (int) this.getResources().getDimension(R.dimen.qb_px_400);
        } else {
            PrivacyUtils.showView(mGuideLayout);
            // 设置宽度值
            params.width = (int) this.getResources().getDimension(R.dimen.qb_px_333);
        }
        // 使设置好的布局参数应用到控件
        mLinearLayout.setLayoutParams(params);
    }

    private void setDialog(String title, String dlgText) {
        mTitle.setText(title);
        PrivacyUtils.goneView(mClose);
        mServiceAgreement.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
        mServiceAgreement.getPaint().setAntiAlias(true);
        mUserGuide.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
        mUserGuide.getPaint().setAntiAlias(true);
        mNegtive.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
        mNegtive.getPaint().setAntiAlias(true);
        mUserGuideHorizontal.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
        mUserGuideHorizontal.getPaint().setAntiAlias(true);
        mServiceAgreementHorizontal.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
        mServiceAgreementHorizontal.getPaint().setAntiAlias(true);
        this.mMessage.setText(Html.fromHtml("<font>\u3000\u3000</font><font>" + dlgText + "</font>"));
        // 使用Html.fromHtml会导致textview布局包裹不生效
        mMessageDetailedView.setText(Html.fromHtml(String.format("<font>\u3000\u3000</font><font>" + PrivacySettings.mPermissionMessage + "</font>", PrivacySettings.mPermission)));
        PrivacyUtils.expandTouchArea(mNegtive, 30);// 放大点击区域
        PrivacyUtils.expandTouchArea(mPositive, 30);// 放大点击区域
        PrivacyUtils.expandTouchArea(mServiceAgreement, 30);// 放大点击区域
        PrivacyUtils.expandTouchArea(mUserGuide, 30);// 放大点击区域
        PrivacyUtils.expandTouchArea(mUserGuideHorizontal, 30);// 放大点击区域
        PrivacyUtils.expandTouchArea(mServiceAgreementHorizontal, 30);// 放大点击区域
    }

    public static void setDialogCallback(PrivacyDialogCallback dialogCallback) {
        mDialogCallback = dialogCallback;
    }
    private void setListener() {
        mServiceAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWebActivity(PrivacySettings.mPrivacyAgreementUrl);
            }
        });
        mUserGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWebActivity(PrivacySettings.mPrivacyGuidetUrl);
            }
        });
        mServiceAgreementHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWebActivity(PrivacySettings.mPrivacyAgreementUrl);
            }
        });
        mUserGuideHorizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWebActivity(PrivacySettings.mPrivacyGuidetUrl);
            }
        });
        //  拒绝
        mNegtive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  用户拒绝授权；
                Privacy.setPrivacyAgreeFlag(PrivacyDialog.this, PrivacyConstants.PRIVACY_AGREE_NO);

                mDialogCallback.reject();
                finish();
                System.exit(0);
                overridePendingTransition(0, R.anim.privacy_anim_out);
            }
        });
        //  同意
        mPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  用户同意授权；
                Privacy.setPrivacyAgreeFlag(PrivacyDialog.this, PrivacyConstants.PRIVACY_AGREE_YES);
                if (mIsUpdate) {
                    //  隐私更新标志复位
                    Privacy.setPrivacyUpdateFlag(PrivacyDialog.this, PrivacyConstants.PRIVACY_UPDATE_NO);
                    //  本地记录，玩家同意更新隐私协议后进入游戏；
                    Privacy.setPassbyMode(PrivacyDialog.this, PrivacyConstants.PRIVACY_PASSBY_UPDATE);
                } else {
                    //  本地记录，玩家同意更新隐私协议后进入游戏；
                    Privacy.setPassbyMode(PrivacyDialog.this, PrivacyConstants.PRIVACY_PASSBY_AGREE);
                }
                mDialogCallback.agree();
                finish();
                overridePendingTransition(0, R.anim.privacy_anim_out);
            }
        });
    }
    private void gotoWebActivity(String url) {
        Intent intent = new Intent(this, PrivacyContent.class);
        intent.putExtra("url", url);
        startActivity(intent);
        overridePendingTransition(R.anim.privacy_anim_in, R.anim.privacy_anim_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("PrivacyDialog", "onDestory()");
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
