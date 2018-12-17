package com.example.bilyli.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author bily.li
 */
public class LoadingButton extends FrameLayout {
    public TextView mClickButon;
    public ProgressBar mLoading;
    private Context mContext;
    private boolean mEnable;
    private int mEnableBackground;
    private int mDisableBackground;
    private String mButtonText;

    public LoadingButton(@NonNull Context context) {
        super(context);
        mContext = context;
        init();
    }

    public LoadingButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
        obtainAttributeSet(context, attrs);
    }

    public LoadingButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
        obtainAttributeSet(context, attrs);
    }

    public void init(){
        View view = LayoutInflater.from(mContext).inflate(R.layout.loading_button, null);
        mClickButon = view.findViewById(R.id.button);
        mLoading = view.findViewById(R.id.loading);
        addView(view);
        FrameLayout.LayoutParams params = (LayoutParams) view.getLayoutParams();
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;

    }



    /**
     * 属性初始化
     * @param c
     * @param attrs
     */
    private void obtainAttributeSet(Context c, AttributeSet attrs) {
        TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.LoadingButton);

        mEnableBackground = a.getResourceId(R.styleable.LoadingButton_enable_bg, 0);
        mDisableBackground = a.getResourceId(R.styleable.LoadingButton_disable_bg, 0);
        float buttonTextSize = a.getDimensionPixelSize(R.styleable.LoadingButton_button_text_size, 0);
        float buttonPaddingTop = a.getDimensionPixelSize(R.styleable.LoadingButton_button_padding_bottom, 0);
        float buttonPaddingBottom = a.getDimensionPixelSize(R.styleable.LoadingButton_button_padding_bottom, 0);
        int buttonTextColor = a.getColor(R.styleable.LoadingButton_button_text_color, 0);
        int loadingSrc = a.getResourceId(R.styleable.LoadingButton_loading_src, 0);
        int buttonBg = a.getResourceId(R.styleable.LoadingButton_button_bg, 0);
        mButtonText = a.getString(R.styleable.LoadingButton_button_text);
        mEnable = a.getBoolean(R.styleable.LoadingButton_enable, true);

        if(buttonTextSize > 0){
            mClickButon.setTextSize(DeviceUtil.px2dip(mContext, buttonTextSize));
        }
        if(buttonTextColor > 0){
            mClickButon.setTextColor(buttonTextColor);
        }
        if(!TextUtils.isEmpty(mButtonText)){
            mClickButon.setText(mButtonText);
        }
        if(buttonBg > 0){
            mClickButon.setBackgroundResource(buttonBg);
        }
        switchEnable(mEnable);
        if(loadingSrc > 0){
            Drawable drawable= getResources().getDrawable(loadingSrc);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mLoading.setIndeterminateDrawable(drawable);
        }
        if(buttonPaddingTop > 0 && buttonPaddingBottom > 0){
            mClickButon.setPadding(0, DeviceUtil.px2dip(mContext, buttonPaddingTop), 0, DeviceUtil.px2dip(mContext, buttonPaddingBottom));
        }
        a.recycle();
    }

    public void switchEnable(boolean enable) {
        mEnable = enable;
        mClickButon.setEnabled(enable);
        setEnabled(enable);
        if(enable){
            mClickButon.setBackgroundResource(mEnableBackground);
        }else{
            mClickButon.setBackgroundResource(mDisableBackground);
        }
    }

    public void showLoading(){
        mLoading.setVisibility(VISIBLE);
        mClickButon.setText("");
        switchEnable(false);
    }

    public void hideLoading(){
        mLoading.setVisibility(GONE);
        mClickButon.setText(mButtonText);
        switchEnable(true);
    }


}
