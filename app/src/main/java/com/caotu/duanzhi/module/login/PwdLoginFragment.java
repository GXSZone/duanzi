package com.caotu.duanzhi.module.login;


import android.view.View;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.AESUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;

import java.util.HashMap;
import java.util.Map;


public class PwdLoginFragment extends BaseLoginFragment {

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        rootView.findViewById(R.id.tv_verification_login).setOnClickListener(this);
        rootView.findViewById(R.id.bt_forget_password).setOnClickListener(this);
        rootView.findViewById(R.id.tv_user_agreement).setOnClickListener(this);
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    protected boolean getSecondEditStrategy(String content) {
        return passWordStrategy(content);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_forget_password) {
            UmengHelper.event(UmengStatisticsKeyIds.forget_password);
            HelperForStartActivity.openBindPhoneOrPsw(BindPhoneAndForgetPwdActivity.FORGET_PWD);
        } else if (v.getId() == R.id.tv_verification_login) {
            UmengHelper.event(UmengStatisticsKeyIds.code_viewpager);
            if (getActivity() != null && getActivity() instanceof LoginAndRegisterActivity) {
                ((LoginAndRegisterActivity) getActivity()).
                        getViewPager().setCurrentItem(0, true);
            }
        } else {
            super.onClick(v);
        }
    }

    @Override
    protected void doBtClick(View v) {
        UmengHelper.event(UmengStatisticsKeyIds.login_psw);
        Map<String, String> map = new HashMap<>();
        map.put("loginid", "");
        map.put("loginphone", getPhoneEdt());
        map.put("loginpwd", AESUtils.getMd5Value(getPasswordEdt()));
        map.put("logintype", "PH");
        LoginHelp.login(map, () -> {
            if (getActivity() != null) {
                getActivity().setResult(LoginAndRegisterActivity.LOGIN_RESULT_CODE);
                getActivity().finish();
            }
        });
    }
}