package com.caotu.duanzhi.module.login;

import android.app.Activity;
import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.utils.AESUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

public class BindPhoneAndForgetPwdFragment extends RegistNewFragment {
    public static final String KEY = "key";
    private boolean isForgetPwd = false;

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        if (getArguments() != null) {
            isForgetPwd = getArguments().getBoolean(KEY, false);
        }
        btDone.setText(isForgetPwd ? "下一步" : "完成");
        rootView.findViewById(R.id.ll_user_agreement).setVisibility(View.GONE);
    }

    @Override
    protected void doBtClick(View v) {
        if (!isForgetPwd) {
            requestBindPhone();
        } else {
            super.doBtClick(v);
        }
    }

    /**
     * 忘记密码和绑定手机是同一个页面,但是和注册对获取验证码的逻辑刚好相反
     */
    @Override
    protected void hasRegist() {
        if (isForgetPwd) {
            super.noRegist();
        } else {
            resetGetVerifyCode();
            ToastUtil.showShort(R.string.has_bind_phone);
        }
    }

    @Override
    protected void noRegist() {
        if (isForgetPwd) {
            resetGetVerifyCode();
            ToastUtil.showShort(R.string.phone_not_register);
        } else {
            super.noRegist();
        }

    }

    @Override
    protected String getCheckType() {
        return "CPSD";
    }

    @Override
    protected void lastStepnetWork() {
        Map<String, String> map = new HashMap<>();
        try {
            map.put("changeid", phoneNum);
            map.put("changepsd", AESUtils.getMd5Value(passwordEdt.getText().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String bodyAES = AESUtils.getRequestBodyAES(map);
        OkGo.<String>post(HttpApi.CHANGE_PASSWORD)
                .tag(this)
                .headers("sessionid", sessionid)
                .upJson(bodyAES)
                .execute(new JsonCallback<String>() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        MySpUtils.putBoolean(MySpUtils.SP_ISLOGIN, true);
                        loginAgain(phoneNum, passwordEdt.getText().toString().trim());
                    }

                    @Override
                    public void onError(Response<String> response) {
                        ToastUtil.showShort("修改密码失败！");
                        super.onError(response);
                    }
                });
    }

    private void loginAgain(String phoneNum, String s) {
        Map<String, String> map = new HashMap<>();
        map.put("loginid", "");
        map.put("loginphone", phoneNum);
        map.put("loginpwd", AESUtils.getMd5Value(s));
        map.put("logintype", "PH");

        OkGo.<BaseResponseBean<String>>post(HttpApi.DO_LOGIN)
                .tag(this)
                .upString(AESUtils.getRequestBodyAES(map))
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        if (LoginHelp.isSuccess(response)) {
//                            EventBusHelp.sendLoginEvent();
                            if (getActivity() != null) {
                                getActivity().setResult(LoginAndRegisterActivity.LOGIN_RESULT_CODE);
                                getActivity().finish();
                            }
                            //修改成功之后登录页面也得关闭,这么处理是为了可能调用finish后不一定及时回调到destory
                            Activity runningActivity = MyApplication.getInstance().getRunningActivity();
                            if (runningActivity instanceof LoginAndRegisterActivity) {
                                getActivity().setResult(LoginAndRegisterActivity.LOGIN_RESULT_CODE);
                                runningActivity.finish();
                            } else {
                                MyApplication.getInstance().getLastSecondActivity().finish();
                            }
                        } else {
                            ToastUtil.showShort(R.string.login_fail);
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<String>> response) {
                        ToastUtil.showShort(R.string.login_failure);
                        super.onError(response);
                    }
                });
    }

    @Override
    protected void changeHint() {
        phoneEdt.setHint("输入新的登录密码");
        passwordEdt.setHint("再次输入新的登录密码");
    }

    /**
     * 绑定手机号成功后关闭页面即可
     */
    private void requestBindPhone() {
        String pNum = phoneEdt.getText().toString();
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("phonenum", pNum);
        OkGo.<BaseResponseBean<String>>post(HttpApi.BIND_PHONE)
                .tag(this)
                .upJson(AESUtils.getRequestBodyAES(map))
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        MySpUtils.putBoolean(MySpUtils.SP_HAS_BIND_PHONE, true);
                        MySpUtils.putBoolean(MySpUtils.SP_ISLOGIN, true);
                        ToastUtil.showShort(R.string.bind_success);
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<String>> response) {
                        ToastUtil.showShort(R.string.bind_failure);
                        super.onError(response);
                    }
                });
    }
}
