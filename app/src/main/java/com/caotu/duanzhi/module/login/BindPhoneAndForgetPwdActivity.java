package com.caotu.duanzhi.module.login;

import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;

public class BindPhoneAndForgetPwdActivity extends BaseActivity {

    public static final int BIND_TYPE = 537;
    public static final int FORGET_PWD = 536;
    public static final int SETTING_PWD = 538;
    public static final String KEY_TYPE = "TYPE";

    @Override
    protected void initView() {
        TextView mTvTitleType = findViewById(R.id.tv_title_type);
        TextView tip = findViewById(R.id.bind_phone_tip);
        int type = getIntent().getIntExtra(KEY_TYPE, BIND_TYPE);
        if (type == BIND_TYPE) {
            UmengHelper.event(UmengStatisticsKeyIds.binding_mobile);
        }
        BaseLoginFragment fragment = new BindPhoneFragment();
        String titleText = "绑定手机";
        tip.setText("不想再和你失散了，这次我们绑紧一点");
        switch (type) {
            case FORGET_PWD:
                titleText = "找回密码";
                tip.setText("请输入绑定的手机号");
                fragment = new PwdFragment();
                break;
            case SETTING_PWD:
                titleText = "设置密码";
                fragment = new PwdFragment();
                tip.setVisibility(View.GONE);
                break;
        }
        mTvTitleType.setText(titleText);
        if (fragment instanceof PwdFragment) {
            ((PwdFragment) fragment).setType(type);
        }
        turnToFragment(null, fragment, R.id.fl_fragment_content);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_bind_phone_and_forget_pwd;
    }
}
