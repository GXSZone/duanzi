package com.caotu.duanzhi.module.login;

import android.app.Activity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.config.HttpCode;
import com.caotu.duanzhi.other.TextWatcherAdapter;
import com.caotu.duanzhi.utils.AESUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.ValidatorUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PwdFragment extends BaseLoginFragment {
    int mType;
    private EditText codeEt;

    public void setType(int type) {
        mType = type;
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.fragment_pwd_layout;
    }

    private String getCodeText() {
        if (codeEt == null) return "";
        return codeEt.getText().toString().trim();
    }

    @Override
    protected void initEtlistener() {
        phoneEdt.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                editTextChange(phoneEdt);
            }
        });
        passwordEdt.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void afterTextChanged(Editable s) {
                editTextChange(passwordEdt);
            }
        });
        codeEt = rootView.findViewById(R.id.code_et);
        codeEt.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                editTextChange(codeEt);
            }
        });
    }

    public void editTextChange(EditText Edt) {
        if (TextUtils.isEmpty(getPhoneEdt()) || TextUtils.isEmpty(getPasswordEdt())
                || TextUtils.isEmpty(getCodeText())) {
            setConfirmButton(false);
            return;
        }
        boolean firstFinish = phoneStrategy(getPhoneEdt());
        if (verificationCode != null && Edt == phoneEdt) {
            verificationCode.setEnabled(firstFinish);
        }
        boolean secondFinish = getSecondEditStrategy(getPasswordEdt());
        boolean thirdFinish = getCodeText().length() >= 4;
        setConfirmButton(firstFinish && secondFinish && thirdFinish);
    }

    @Override
    protected boolean getSecondEditStrategy(String content) {
        return passWordStrategy(content);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fragment_login_login_but) {
            if (!checkFirstEdit(getPhoneEdt())) {
                firstEtDontPass();
                return;
            }
            if (!checkSecondEdit(getPasswordEdt())) {
                secondEtDontPass();
                return;
            }
            if (!ValidatorUtils.isVerificationCode(getCodeText())) {
                ToastUtil.showShort(R.string.verify_code_error);
                return;
            }
            doBtClick(v);

        } else {
            super.onClick(v);
        }
    }

    @Override
    protected void doBtClick(View v) {
        switch (mType) {
            case BindPhoneAndForgetPwdActivity.FORGET_PWD:
                forgetPwd();
                break;
            case BindPhoneAndForgetPwdActivity.SETTING_PWD:
                settingPwd();
                break;
        }
    }


    private void settingPwd() {

    }

    private void forgetPwd() {

    }

    /**
     * 忘记密码和绑定手机是同一个页面,但是和注册对获取验证码的逻辑刚好相反
     */

//            resetGetVerifyCode();
//            ToastUtil.showShort(R.string.has_bind_phone);
//
//
//
//            resetGetVerifyCode();
//            ToastUtil.showShort(R.string.phone_not_register);


    /**
     * 更改密码
     */
    protected void changePsw() {
        Map<String, String> map = new HashMap<>();
        try {
            map.put("changeid", getPhoneEdt());
            map.put("changepsd", AESUtils.getMd5Value(getPasswordEdt()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.<String>post(HttpApi.CHANGE_PASSWORD)
                .tag(this)
//                .headers("sessionid", sessionid)
                .upString(AESUtils.getRequestBodyAES(map))
                .execute(new JsonCallback<String>() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        MySpUtils.putBoolean(MySpUtils.SP_ISLOGIN, true);
                        loginAgain(getPhoneEdt(), getPasswordEdt());
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
        LoginHelp.login(map, new LoginHelp.LoginCllBack() {
            @Override
            public void loginSuccess() {
                if (getActivity() != null) {
                    getActivity().setResult(LoginAndRegisterActivity.LOGIN_RESULT_CODE);
                    getActivity().finish();
                }
                //修改成功之后登录页面也得关闭,这么处理是为了可能调用finish后不一定及时回调到destory
                Activity runningActivity = MyApplication.getInstance().getRunningActivity();
                if (runningActivity instanceof LoginAndRegisterActivity) {
                    runningActivity.setResult(LoginAndRegisterActivity.LOGIN_RESULT_CODE);
                    runningActivity.finish();
                } else {
                    Activity lastSecondActivity = MyApplication.getInstance().getLastSecondActivity();
                    if (lastSecondActivity != null) {
                        lastSecondActivity.finish();
                    }
                }
            }
        });
    }

    //  //验证手机号是否注册
    protected void requestHasRegist(String phone) {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        try {
            map.put("phonenum", AESUtils.encode(phone.trim()));
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
//                            noRegist();
                        } else {
//                            hasRegist();
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

    /**
     * 验证码正确性验证
     */
    public void requestVerifty() {

        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        try {
            map.put("checksms", AESUtils.encode(getPasswordEdt()));
            map.put("checkid", AESUtils.encode(getPhoneEdt()));
            map.put("checktype", getCheckType());
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.<BaseResponseBean<String>>post(HttpApi.DO_SMS_VERIFY)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
//                        //保存sessionid用于下个修改密码接口请求用
//                        sessionid = response.body().getData();
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<String>> response) {

                        ToastUtil.showShort(R.string.do_sms_verify_overtime);
                        super.onError(response);
                    }
                });
    }

    protected String getCheckType() {
        //校验类型CPSD为修改密码
        return "";
    }
}
