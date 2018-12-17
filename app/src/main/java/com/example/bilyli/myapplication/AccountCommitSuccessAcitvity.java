package com.example.bilyli.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

/**
 * @author bily.li
 * 激活账户提交成功页面
 */
public class AccountCommitSuccessAcitvity extends Activity implements View.OnClickListener{

    private boolean mNeedReviewed;
    public static final String NEED_REVIEWED = "need_reviewed";

    /**
     * 跳转到AccountCommitSuccessAcitvity
     * @param context
     * @param neadReviewed 是否需要审核
     */
    public static void jump(Context context, boolean neadReviewed){
        if(context == null){
            return;
        }
        Intent intent = new Intent(context, AccountCommitSuccessAcitvity.class);
        intent.putExtra(NEED_REVIEWED, neadReviewed);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_commit_success);

        mNeedReviewed = getIntent().getBooleanExtra(NEED_REVIEWED, false);
        initView();
    }

    private void initView() {
        findViewById(R.id.close_btn).setOnClickListener(this);
        TextView toRealAccount = findViewById(R.id.to_real_account);
        TextView depositAgain = findViewById(R.id.deposit_again);
        TextView toCustomService = findViewById(R.id.to_custom_service);
        TextView commitSuccessTip = findViewById(R.id.commit_success_tip);

        toRealAccount.setOnClickListener(this);
        depositAgain.setOnClickListener(this);
        toCustomService.setOnClickListener(this);

        if (mNeedReviewed){
            commitSuccessTip.setText(getString(R.string.commit_success_tip));
            toRealAccount.setVisibility(View.GONE);
            toCustomService.setVisibility(View.VISIBLE);
            depositAgain.setText(getString(R.string.common_dialog_sure));
        }else{
            toCustomService.setVisibility(View.GONE);
            toRealAccount.setVisibility(View.VISIBLE);
            commitSuccessTip.setText(getString(R.string.commit_success_tip2));
            depositAgain.setText(getString(R.string.deposit_again));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_btn:
                finish();
                break;
            case R.id.deposit_again:
                if (mNeedReviewed){
                    //待审核
                    finish();
                }else{
                    // TODO 无需审核 继续存款
                }
                break;
            case R.id.to_real_account:
                break;
            case R.id.to_custom_service:
                break;
                default:
        }
    }
}
