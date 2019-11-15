package com.caotu.duanzhi.module.login;

import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.config.HttpCode;
import com.caotu.duanzhi.utils.AESUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.ValidatorUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * 验证码发送的请求操作
 */
public class BindPhoneFragment extends BaseLoginFragment {

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.fragment_bind_phone;
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
            map.put("phonenum", AESUtils.encode(getPhoneEdt()));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        OkGo.<BaseResponseBean<String>>post(HttpApi.VERIFY_HAS_REGIST)
                .tag(this)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        if (HttpCode.has_regist_phone.equals(response.body().getData())) {
                            requestVerifty();
                        } else {
                            ToastUtil.showShort("该手机已被绑定");
                        }
                    }
                });
    }

    /**
     * 绑定手机号成功后关闭页面即可
     */
    private void requestBindPhone() {

        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("phonenum", getPhoneEdt());

        OkGo.<BaseResponseBean<String>>post(HttpApi.BIND_PHONE)
                .tag(this)
                .upString(AESUtils.getRequestBodyAES(map))
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        MySpUtils.putBoolean(MySpUtils.SP_HAS_BIND_PHONE, true);
                        MySpUtils.putBoolean(MySpUtils.SP_ISLOGIN, true);
                        ToastUtil.showShort("绑定手机成功");
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<String>> response) {
                        ToastUtil.showShort("绑定手机失败");
                        super.onError(response);
                    }
                });
    }

    /**
     * 验证码正确性验证
     */
    public void requestVerifty() {

        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        try {
            map.put("checksms", AESUtils.encode(getPasswordEdt()));
            map.put("checkid", AESUtils.encode(getPhoneEdt()));
            map.put("checktype", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.<BaseResponseBean<String>>post(HttpApi.DO_SMS_VERIFY)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        requestBindPhone();
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<String>> response) {
                        ToastUtil.showShort("验证码错误或验证码超时");
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
        ToastUtil.showShort("验证码格式错误");
    }

    @Override
    public void onDestroyView() {
        OkGo.getInstance().cancelTag(this);
        super.onDestroyView();
    }
}
