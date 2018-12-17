package com.example.bilyli.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author bily.li
 * 根据不同的状态显示不同的编辑效果
 */
public class TintEditLayout extends LinearLayout {
    /**标题控件*/
    protected TextView mTitleTv;
    /**编辑内容控件*/
    protected EditText mContentEdit;
    /**错误显示控件*/
    protected TextView mErrorTv;
    /**编辑框背景*/
    protected View mEditBackgroundLine;

    private LinearLayout.LayoutParams mContentParams;
    private LinearLayout.LayoutParams mErrorParams;
    private LinearLayout.LayoutParams mlineParams;
    private int mEditColor;
    private int mErrorColor;
    private int mLineColor;
    private int mDefColor;
    private int mTitleColor;
    private boolean titleVisiable;
    private String mDefErrorText;

    /**默认显示状态*/
    public final static int DEFULAT_TYPE = 0;
    /**编辑显示状态*/
    public final static int EDIT_TYPE = 1;
    /**错误显示状态*/
    public final static int ERROR_TYPE = 2;

    /**编辑状态*/
    private int mEidtType = DEFULAT_TYPE;

    private final static String TEXT = "text";
    private final static String TEXTMULTILINE = "textMultiLine";
    private final static String TEXTPASSWORD = "textPassword";
    private final static String NUMBER = "number";
    private final static String PHONE = "phone";
    private final static String TEXTEMAILADDRESS = "textEmailAddress";


    private Context mContext;
    private TintOnFocusChangeListener tintOnFocusChangeListener;
    private TintTextWatcher tintTextWatcher;

    public TintEditLayout(Context context) {
        super(context);
        init(context);
    }

    public TintEditLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TintEditLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context c, AttributeSet attrs){
        init(c);
        obtainAttributeSet(c, attrs);
        setEditFocusChange();
        mContentEdit.addTextChangedListener(new MyTextWatch());
    }

    private void setEditFocusChange() {
        mContentEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    setEditType(EDIT_TYPE);
                }else{
                    setEditType(DEFULAT_TYPE);
                    if(TextUtils.isEmpty(mContentEdit.getText().toString()) && !TextUtils.isEmpty(mDefErrorText)){
                        setErrotText(mDefErrorText);
                    }
                }
                if(tintOnFocusChangeListener != null){
                    tintOnFocusChangeListener.onFocusChange(v, hasFocus);
                }
            }
        });
    }

    private void init(Context c) {
        mContext = c;
        setOrientation(LinearLayout.VERTICAL);
        mTitleTv = new TextView(c);
        mTitleTv.setVisibility(INVISIBLE);
        mContentEdit = new EditText(c);
        mContentEdit.setBackgroundColor(c.getResources().getColor(R.color.translu));
        mErrorTv = new TextView(c);
        mErrorTv.setVisibility(GONE);
        mEditBackgroundLine = new View(c);

        addView(mTitleTv);
        addView(mContentEdit);
        addView(mEditBackgroundLine);
        addView(mErrorTv);

        mContentParams = (LayoutParams) mContentEdit.getLayoutParams();
        mContentParams.width = LayoutParams.MATCH_PARENT;
        mContentParams.height = LayoutParams.WRAP_CONTENT;

        mlineParams = (LayoutParams) mEditBackgroundLine.getLayoutParams();
        mlineParams.width = LayoutParams.MATCH_PARENT;
        mlineParams.height = 2;

        mErrorParams = (LayoutParams) mErrorTv.getLayoutParams();
    }

    /**
     * 属性初始化
     * @param c
     * @param attrs
     */
    private void obtainAttributeSet(Context c, AttributeSet attrs) {
        TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.TintEditLayout);

        mEidtType = a.getInt(R.styleable.TintEditLayout_edit_type, TintEditLayout.DEFULAT_TYPE);

        mEidtType = a.getInt(R.styleable.TintEditLayout_edit_type, TintEditLayout.DEFULAT_TYPE);
        mDefColor = a.getColor(R.styleable.TintEditLayout_def_color, 0);
        mEditColor = a.getColor(R.styleable.TintEditLayout_edit_color, 0);
        mErrorColor = a.getColor(R.styleable.TintEditLayout_error_color, 0);
        mTitleColor = a.getColor(R.styleable.TintEditLayout_title_color, 0);
        mLineColor = a.getColor(R.styleable.TintEditLayout_line_color, 0);

        float editPadding = a.getDimension(R.styleable.TintEditLayout_edit_padding_top, 8);
        float errorPadding = a.getDimension(R.styleable.TintEditLayout_error_padding_top, 8);
        float lineHeight = a.getDimension(R.styleable.TintEditLayout_line_height, 2);

        titleVisiable = a.getBoolean(R.styleable.TintEditLayout_title_visiable, false);

        String textTitle = a.getString(R.styleable.TintEditLayout_title_text);
        String textContentHint = a.getString(R.styleable.TintEditLayout_text_content_hint);
        mDefErrorText = a.getString(R.styleable.TintEditLayout_def_error_text);

        mTitleTv.setText(textTitle);
        mContentEdit.setHint(textContentHint);

        mContentParams.topMargin = (int) editPadding;
        mErrorParams.topMargin = (int) errorPadding;
        mlineParams.height = (int) lineHeight;

        setViewFont(a);

        switchEditType();
        a.recycle();
    }

    private void setViewFont(TypedArray a) {
        int maxLenght = a.getInteger(R.styleable.TintEditLayout_max_length, 0);
        int maxLines = a.getInteger(R.styleable.TintEditLayout_max_lines, 0);
        float titleSize = a.getDimensionPixelSize(R.styleable.TintEditLayout_title_size, 0);
        float contentSize = a.getDimensionPixelSize(R.styleable.TintEditLayout_content_size, 0);
        float errorSize = a.getDimensionPixelSize(R.styleable.TintEditLayout_error_size, 0);
        int drawableLeft = a.getResourceId(R.styleable.TintEditLayout_drawable_left, 0);
        float drawableLeftPadding = a.getDimensionPixelSize(R.styleable.TintEditLayout_drawable_left_padding, 0);
        if(drawableLeft != 0){
            Drawable drawable= getResources().getDrawable(drawableLeft);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mContentEdit.setCompoundDrawables(drawable,null,null,null);
            mContentEdit.setCompoundDrawablePadding((int) DeviceUtil.px2dip(mContext, drawableLeftPadding));
        }
        setInputType(a);
        setErrorViewVisiable(a);

        if(mTitleColor != 0){
            mTitleTv.setTextColor(mTitleColor);
        }else{
            if(mDefColor != 0) {
                mTitleTv.setTextColor(mDefColor);
            }
        }

        if(mDefColor != 0){
            mContentEdit.setHintTextColor(mDefColor);
        }

        if(titleSize > 0){
            mTitleTv.setTextSize(DeviceUtil.px2dip(mContext, titleSize));
        }
        if(contentSize > 0){
            mContentEdit.setTextSize(DeviceUtil.px2dip(mContext, contentSize));
        }
        if(errorSize > 0){
            mErrorTv.setTextSize(DeviceUtil.px2dip(mContext, errorSize));
        }

        if(maxLenght > 0){
            mContentEdit.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLenght) });
        }
        if(maxLines > 0){
            mContentEdit.setMaxLines(maxLines);
        }
    }

    private void setErrorViewVisiable(TypedArray a) {
        boolean errorVisiable = a.getBoolean(R.styleable.TintEditLayout_error_visiable, false);
        if (errorVisiable) {
            mErrorTv.setVisibility(VISIBLE);
        } else {
            mErrorTv.setVisibility(GONE);
        }
        if (titleVisiable) {
            mTitleTv.setVisibility(VISIBLE);
        } else {
            mTitleTv.setVisibility(INVISIBLE);
        }
    }

    private void setInputType(TypedArray a) {
        String inputType = a.getString(R.styleable.TintEditLayout_edit_input_type);
        if(TEXTMULTILINE.equalsIgnoreCase(inputType)){
            mContentEdit.setInputType(EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
        }else if(TEXTPASSWORD.equalsIgnoreCase(inputType)){
            mContentEdit.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
        }else if(NUMBER.equalsIgnoreCase(inputType)){
            mContentEdit.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        }else if(PHONE.equalsIgnoreCase(inputType)){
            mContentEdit.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        }else if(TEXTEMAILADDRESS.equalsIgnoreCase(inputType)){
            mContentEdit.setInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }else{
            mContentEdit.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        }
    }

    /**
     * 切换编辑状态
     */
    private void switchEditType() {
        if(mEidtType == ERROR_TYPE){
            mEditBackgroundLine.setBackgroundColor(mErrorColor);
            mErrorTv.setTextColor(mErrorColor);
            mErrorTv.setVisibility(VISIBLE);
        }else if(mEidtType == EDIT_TYPE){
            mEditBackgroundLine.setBackgroundColor(mEditColor);
            mErrorTv.setTextColor(mEditColor);
            mErrorTv.setVisibility(GONE);
        }else{
            if(mLineColor != 0){
                mEditBackgroundLine.setBackgroundColor(mLineColor);
            }else{
                mEditBackgroundLine.setBackgroundColor(mDefColor);
            }
            mErrorTv.setTextColor(mDefColor);
            mErrorTv.setVisibility(GONE);

        }
    }

    public void setTitleText(String title){
        mTitleTv.setText(title);
    }

    public void setTitleText(int resId){
        mTitleTv.setText(resId);
    }

    public void setTitleSize(int resId){
        mTitleTv.setTextSize(resId);
    }

    public void setTitleSize(float size){
        mTitleTv.setTextSize(size);
    }

    public void setTitleColor(int color){
        mTitleColor = color;
        mTitleTv.setTextColor(color);
    }

    public void setEditHintText(String hint){
        mContentEdit.setHint(hint);
    }

    public void setEditHintText(int resId){
        mContentEdit.setHint(resId);
    }

    public void setEditHintColor(int color){
        mContentEdit.setHintTextColor(color);
    }

    public void setEditBackgrount(int resId){
        mContentEdit.setBackgroundResource(resId);
        mEditBackgroundLine.setVisibility(GONE);
    }

    public void setEditBackgrountColor(int color){
        mEditBackgroundLine.setBackgroundColor(color);
    }

    public void setEditType(int type){
        mEidtType = type;
        switchEditType();
    }

    public void setInputType(int inputType){
        mContentEdit.setInputType(inputType);
    }

    public void setErrotText(String error){
        setEditType(ERROR_TYPE);
        mErrorTv.setText(error);
    }

    private class  MyTextWatch implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(tintTextWatcher != null){
                tintTextWatcher.beforeTextChanged(charSequence, i, i1, i2);
            }
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int i1, int count) {
            if(TextUtils.isEmpty(charSequence)){
                if(titleVisiable){
                    mTitleTv.setVisibility(VISIBLE);
                }else{
                    mTitleTv.setVisibility(INVISIBLE);
                }
            }else{
                mTitleTv.setVisibility(VISIBLE);
            }
            if(tintTextWatcher != null){
                tintTextWatcher.onTextChanged(charSequence, start, i1, count);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(tintTextWatcher != null){
                tintTextWatcher.afterTextChanged(s);
            }
        }
    }

    public String getText(){
        return mContentEdit.getText().toString();
    }


    public void setTintOnFocusChangeListener(TintOnFocusChangeListener tintOnFocusChangeListener){
        this.tintOnFocusChangeListener = tintOnFocusChangeListener;
    }

    public void addTintTextWatcher(TintTextWatcher tintTextWatcher){
        this.tintTextWatcher = tintTextWatcher;
    }

    public interface TintOnFocusChangeListener{
        void onFocusChange(View v, boolean hasFocus);
    }

    public interface TintTextWatcher{
        void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) ;
        void onTextChanged(CharSequence charSequence, int start, int i1, int count);
        void afterTextChanged(Editable s) ;
    }
}
