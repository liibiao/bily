package com.example.bilyli.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.gw.common.view.LoadingButton;
import com.gw.common.view.TintEditLayout;
import com.gw.common.view.utils.CacheCountDownTimer;
import com.gw.plugin.domain.communicate.ui.send.event.config.ResponseNewCustomerVerifiCodeEvent;
import com.gyf.barlibrary.ImmersionBar;

/**
 * @author bily.li
 */
public class EnableAccountActivity extends Activity implements View.OnClickListener, CacheCountDownTimer.TimerListener{
    private TextView mGetCodeTv;
    private LoadingButton mConfirmButton;
    private TintEditLayout mNameEdit;
    private TintEditLayout mIdCardEdit;
    private TintEditLayout mEmailEdit;
    private TintEditLayout mVerificationCodeEdit;

    private boolean mTimeStart;
    private ImmersionBar mImmersionBar;

    private EnableAccountReqeustManager accountReqeustManager = EnableAccountReqeustManager.getInstance();

    private CacheCountDownTimer mTimer = CacheCountDownTimer.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatusBar();
//        setTransparentBar(true);
//        AndroidBug5497Workaround.assistActivity(this);
        setContentView(R.layout.activity_enable_account);
        ImmersionBar.setStatusBarView(this, findViewById(R.id.status_bar));
        initTimer();
        initView();
    }

    private void initStatusBar() {
        mImmersionBar = ImmersionBar.with(this)
                .navigationBarEnable(false)
                .keyboardEnable(false, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                .statusBarDarkFont(true, 0.1f).fitsSystemWindows(false)
                .statusBarColor(R.color.common_set_white);
        mImmersionBar.init();
    }


    /**
     * 设置透明状态栏
     * isTransparent 标识有些界面从状态栏处开始绘制ui
     */
    public void setTransparentBar(boolean isTransparent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //状态栏文字颜色设置为深色
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(option);
            if (isTransparent) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            } else {
                if (DeviceUtil.instance().FlymeSetStatusBarLightMode(getWindow(), true)) {
                    return;
                } else if (DeviceUtil.instance().MIUISetStatusBarLightMode(getWindow(), true)) {
                    return;
                } else {
                    getWindow().setStatusBarColor(Color.TRANSPARENT);
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            if (isTransparent) {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            } else {
                if (DeviceUtil.instance().FlymeSetStatusBarLightMode(getWindow(), true)) {
                    return;
                } else if (DeviceUtil.instance().MIUISetStatusBarLightMode(getWindow(), true)) {
                    return;
                } else {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.theme_blue_color));
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }

    private void initTimer() {
        mTimer.setOnTimerLisener(this);
        mTimer.initTimer(60*1000);
    }


    private void initView() {
        mGetCodeTv = findViewById(R.id.get_code_btn);
        mConfirmButton = findViewById(R.id.confirm_button);
        mNameEdit = findViewById(R.id.name_edit_layout);
        mIdCardEdit = findViewById(R.id.idcard_edit_layout);
        mEmailEdit = findViewById(R.id.email_edit_layout);
        mVerificationCodeEdit = findViewById(R.id.code_edit_layout);

        mNameEdit.addTintTextWatcher(textWatch);
        mIdCardEdit.addTintTextWatcher(textWatch);
        mEmailEdit.addTintTextWatcher(textWatch);
        mVerificationCodeEdit.addTintTextWatcher(textWatch);
        mConfirmButton.setOnClickListener(this);
        mGetCodeTv.setOnClickListener(this);

//        mIdCardEdit.getContentEditText().setKeyListener(DigitsKeyListener.getInstance("1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM"));
//        mIdCardEdit.getContentEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.get_code_btn:
                doGetCode();
                break;
            case R.id.confirm_button:
                doCommit();
                break;
                default:
        }
    }

    private void doGetCode() {
        accountReqeustManager.requestNewCustomerVerifiCode("18777771495",
                new EnableAccountReqeustManager.EnableAcountResponse<ResponseNewCustomerVerifiCodeEvent>() {
                    @Override
                    public void onResponse(ResponseNewCustomerVerifiCodeEvent verifiCodeEvent) {
                        setGetCodeTvEanable(false);
                        if(mTimer != null){
                            mTimer.start();
                            mTimeStart = true;
                        }
                    }
                });

    }

    private void doCommit() {
        if(checkInputEmpty()){
            mConfirmButton.showLoading();
            switchEditType(TintEditLayout.DISABLE_TYPE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mConfirmButton.post(new Runnable() {
                        @Override
                        public void run() {
                            mConfirmButton.hideLoading();
                            switchEditType(TintEditLayout.DEFULAT_TYPE);
                            AccountCommitSuccessAcitvity.jump(EnableAccountActivity.this, false);
                        }
                    });
                }
            }).start();
        }
    }

    private TintEditLayout.TintTextWatcher textWatch = new TintEditLayout.TintTextWatcher(){

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int i1, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(checkInputEmpty()){
                mConfirmButton.switchEnable(true);
            }else{
                mConfirmButton.switchEnable(false);
            }
        }
    };

    private boolean checkInputEmpty(){
        return !TextUtils.isEmpty(mEmailEdit.getContentText()) &&
                !TextUtils.isEmpty(mIdCardEdit.getContentText()) &&
                !TextUtils.isEmpty(mNameEdit.getContentText()) &&
                !TextUtils.isEmpty(mVerificationCodeEdit.getContentText());
    }

    private void switchEditType(int type){
        mEmailEdit.setEditType(type);
        mIdCardEdit.setEditType(type);
        mNameEdit.setEditType(type);
        mVerificationCodeEdit.setEditType(type);
        if(mTimeStart) {
            return;
        }
        if(type == TintEditLayout.DISABLE_TYPE){
            setGetCodeTvEanable(false);
        }else{
            setGetCodeTvEanable(true);
        }
    }

    private void setGetCodeTvEanable(boolean enable){
        mGetCodeTv.setEnabled(enable);
        if(enable){
            mGetCodeTv.setTextColor(getResources().getColor(R.color.theme_blue_color));
        }else{
            mGetCodeTv.setTextColor(getResources().getColor(R.color.message_desc));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accountReqeustManager.release();
        mImmersionBar.destroy();
    }

    @Override
    public void onTick(long millisUntilFinished) {
        long cacheTime = millisUntilFinished / 1000;
        setGetCodeTvEanable(false);
        mGetCodeTv.setText(String.format(getString(R.string.get_verification_code_again), cacheTime));
    }

    @Override
    public void onFinish() {
        mTimeStart = false;
        mGetCodeTv.setText(getString(R.string.get_verification_code));
        setGetCodeTvEanable(true);
    }
}
