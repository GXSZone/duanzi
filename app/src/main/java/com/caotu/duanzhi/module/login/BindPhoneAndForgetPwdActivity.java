package com.caotu.duanzhi.module.login;

import android.os.Bundle;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;

public class BindPhoneAndForgetPwdActivity extends BaseActivity {

    public static final int BIND_TYPE = 537;
    public static final int FORGET_PWD = 536;
    public static final String KEY_TYPE = "TYPE";

    @Override
    protected void initView() {
        TextView mTvTitleType = findViewById(R.id.tv_title_type);
        boolean isForgetPwd = (FORGET_PWD == getIntent().getIntExtra(KEY_TYPE, BIND_TYPE));
        mTvTitleType.setText(isForgetPwd ? "找回密码" : "绑定手机");
        BindPhoneAndForgetPwdFragment fragment = new BindPhoneAndForgetPwdFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(BindPhoneAndForgetPwdFragment.KEY, isForgetPwd);
        turnToFragment(bundle, fragment, R.id.fl_fragment_content);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_bind_phone_and_forget_pwd;
    }
}
