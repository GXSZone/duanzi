package com.caotu.duanzhi.module.login;

import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;

public class BindPhoneAndForgetPwdActivity extends BaseActivity {

    public static final int BIND_TYPE = 537;
    public static final int FORGET_PWD = 536;
    public static final int SETTING_PWD = 538;
    public static final String KEY_TYPE = "TYPE";

    @Override
    protected void initView() {
        TextView mTvTitleType = findViewById(R.id.tv_title_type);
        int type = getIntent().getIntExtra(KEY_TYPE, BIND_TYPE);
        String titleText = "绑定手机";
        switch (type) {
            case FORGET_PWD:
                titleText = "找回密码";
                break;
            case SETTING_PWD:
                titleText = "设置密码";
                break;
        }
        mTvTitleType.setText(titleText);
        PwdFragment fragment = new PwdFragment();
        fragment.setType( type);
        turnToFragment(null, fragment, R.id.fl_fragment_content);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_bind_phone_and_forget_pwd;
    }
}
