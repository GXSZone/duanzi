package com.caotu.duanzhi.module.login;

import android.text.TextUtils;
import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.ValidatorUtils;

import java.util.Map;

/**
 * 直接验证码登录注册
 */
public class VerificationLoginFragment extends BaseLoginFragment {

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.fragment_verification_login;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        rootView.findViewById(R.id.tv_click_pw_login).setOnClickListener(this);
        rootView.findViewById(R.id.tv_user_agreement).setOnClickListener(this);
        //自动填入本机手机号
        String nativePhoneNumber = DevicesUtils.getNativePhoneNumber(getContext());
        if (!TextUtils.isEmpty(nativePhoneNumber)) {
            phoneEdt.postDelayed(() -> {
                phoneEdt.setText(nativePhoneNumber);
                passwordEdt.requestFocus();
            }, 200);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.tv_click_pw_login) {
            UmengHelper.event(UmengStatisticsKeyIds.psw_viewpager);
            if (getActivity() != null && getActivity() instanceof LoginAndRegisterActivity) {
                ((LoginAndRegisterActivity) getActivity()).
                        getViewPager().setCurrentItem(1, true);
            }
        }
    }

    @Override
    protected boolean getSecondEditStrategy(String content) {
        //只验证验证码的长度
        return content.length() >= 4;
    }


    @Override
    protected void doBtClick(View v) {
        UmengHelper.event(UmengStatisticsKeyIds.login_code);
        Map<String, String> map;
        if (getActivity() instanceof LoginAndRegisterActivity) {
            map = ((LoginAndRegisterActivity) getActivity()).getData();
        } else {
            map = CommonHttpRequest.getInstance().getHashMapParams();
        }
        try {
            map.put("regphone", getPhoneEdt());
            map.put("sms", getPasswordEdt());
        } catch (Exception e) {
            e.printStackTrace();
        }
        LoginHelp.loginAndRegistByCode(map, () -> {
            if (getActivity() != null) {
                getActivity().setResult(LoginAndRegisterActivity.LOGIN_RESULT_CODE);
                getActivity().finish();
            }
        });
    }


    @Override
    protected boolean checkSecondEdit(String text) {
        return ValidatorUtils.isVerificationCode(text);
    }

    @Override
    protected void secondEtDontPass() {
        ToastUtil.showShort(R.string.verify_code_error);
    }
}
