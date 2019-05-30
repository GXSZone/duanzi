package com.caotu.duanzhi.module.login;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.utils.AESUtils;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.ValidatorUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/6/19 18:10
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

        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        try {
            map.put("phone", AESUtils.encode(getPhoneEdt()));
            map.put("sms", AESUtils.encode(getPasswordEdt()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // TODO: 2019-05-23 该接口还需要调整 
        OkGo.<String>post(HttpApi.VERIFY_LOGIN)
                .upJson(new JSONObject(map))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.i("$$$$", "onSuccess: ");
                    }

                    @Override
                    public void onError(Response<String> response) {
                        Log.i("$$$$", "onError: ");
                        super.onError(response);
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
