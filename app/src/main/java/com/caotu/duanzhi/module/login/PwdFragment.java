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

/**
 * 这个注册登录这块的接口是真的多,跟屎一样.自已意会吧
 */
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

        } else if (v.getId() == R.id.tv_verification_code) {
            //先判断手机号正确性
            boolean checkPhonePass = checkFirstEdit(getPhoneEdt());
            if (checkPhonePass) {
                if (mType == BindPhoneAndForgetPwdActivity.FORGET_PWD) {
                    requestHasRegist();
                } else {
                    checkPhone();
                }
            } else {
                ToastUtil.showShort(R.string.phone_number_not_right);
            }
        } else {
            super.onClick(v);
        }
    }


    @Override
    protected void doBtClick(View v) {
        requestVerifty();
    }


    private void settingPwd() {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        try {
            map.put("phone", getPhoneEdt());
            map.put("psd", AESUtils.encode(getPasswordEdt()));
            map.put("sms", getCodeText());
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.<BaseResponseBean<String>>post(HttpApi.SETTING_PWD)
                .upString(AESUtils.getRequestBodyAES(map))
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        loginAgain(getPhoneEdt(), getPasswordEdt());
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<String>> response) {
                        ToastUtil.showShort("设置密码失败");
                        super.onError(response);
                    }
                });

    }


    /**
     * 更改密码
     */
    protected void changePsw(String sessionid) {
        Map<String, String> map = new HashMap<>();
        try {
            map.put("changeid", getPhoneEdt());
            map.put("changepsd", AESUtils.getMd5Value(getPasswordEdt()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.<String>post(HttpApi.CHANGE_PASSWORD)
                .tag(this)
                .headers("sessionid", sessionid)
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
        LoginHelp.login(map, () -> {
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
        });
    }

    //  //验证手机号是否注册
    protected void requestHasRegist() {
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
//                            ToastUtil.showShort(R.string.phone_not_register);
                            initTimer();
                            requestGetVeriftyCode();
                        } else {
                            ToastUtil.showShort(R.string.phone_not_register);
                        }
                    }
                });

    }

    private void checkPhone() {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        try {
            map.put("userphone", AESUtils.encode(getPhoneEdt()));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        OkGo.<BaseResponseBean<String>>post(HttpApi.CHECK_PHONE)
                .tag(this)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        // TODO: 2019-06-17 成功的回调都是1000
                        initTimer();
                        requestGetVeriftyCode();
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<String>> response) {
                        String code = response.getException().getMessage();
                        if (HttpCode.not_self_phone.equals(code)) {
                            ToastUtil.showShort("该手机号非本账号绑定手机号，请修改～");
                        } else if (HttpCode.has_bind.equals(code)) {
                            ToastUtil.showShort("该手机号已绑定其他账号，请换一个手机号");
                        }
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
            map.put("checktype", mType == BindPhoneAndForgetPwdActivity.FORGET_PWD ? "CPSD" : "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.<BaseResponseBean<String>>post(HttpApi.DO_SMS_VERIFY)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
//                        //保存sessionid用于下个修改密码接口请求用
                        String sessionid = response.body().getData();
                        switch (mType) {
                            case BindPhoneAndForgetPwdActivity.FORGET_PWD:
                                changePsw(sessionid);
                                break;
                            case BindPhoneAndForgetPwdActivity.SETTING_PWD:
                                settingPwd();
                                break;
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<String>> response) {
                        ToastUtil.showShort(R.string.do_sms_verify_overtime);
                        super.onError(response);
                    }
                });
    }
}
