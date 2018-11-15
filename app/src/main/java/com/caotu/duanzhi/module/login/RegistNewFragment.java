package com.caotu.duanzhi.module.login;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.RegistBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.config.HttpCode;
import com.caotu.duanzhi.utils.AESUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.ValidatorUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/6/19 18:10
 */
public class RegistNewFragment extends BaseLoginFragment {

    TextView verificationCode;
    CountDownTimer timer;

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.fragment_register;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        verificationCode = rootView.findViewById(R.id.tv_verification_code);

        verificationCode.setOnClickListener(v -> {
            //先判断手机号正确性
            boolean checkPhonePass = checkFirstEdit(getPhoneEdt().getText().toString());
            if (checkPhonePass) {
                //验证手机号是否注册
                requestHasRegist(getPhoneEdt().getText().toString());
            } else {
                ToastUtil.showShort(R.string.phone_number_not_right);
            }

        });
        verificationCode.setEnabled(false);
    }

    public void timerCode() {
        timer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    int remainTime = (int) (millisUntilFinished / 1000L);
                    changeText(remainTime);
                }
            }

            @Override
            public void onFinish() {
                resetGetVerifyCode();
            }
        };
        timer.start();
        verificationCode.setEnabled(false);
    }

    protected void resetGetVerifyCode() {
        verificationCode.setText(R.string.get_verify_code);
        verificationCode.setEnabled(true);
    }

    @SuppressLint("DefaultLocale")
    private void changeText(int remainTime) {
        if (verificationCode != null) {
            verificationCode.setText(String.format("%ds", remainTime));
        }
    }

    @Override
    protected boolean getSecondEditStrategy(EditText phoneEdt, String content) {
        if (goSetPassword) {
            //只验证密码长度,是否符合规则是在点击下一步再验证
            return passWordStrategy(content);
        }
        //只验证验证码的长度
        return content.length() >= 4;
    }


    @Override
    public boolean getFirstEditStrategy(EditText phoneEdt, String content) {
        if (goSetPassword) {
            return passWordStrategy(content);
        }
        //初始处理只验证长度
        return phoneStrategy(content);
    }

    /**
     * 处理获取验证码的按钮
     *
     * @param firstFinish
     * @param content
     */
    @Override
    public void firstListener(boolean firstFinish, String content) {
        verificationCode.setEnabled(firstFinish);
    }

    //记录手机号用于跳转到设置密码页面请求参数用
    String phoneNum = null;

    @Override
    protected void doBtClick(View v) {
        if (goSetPassword) {
            if (!phoneEdt.getText().toString().equals(passwordEdt.getText().toString())) {
                ToastUtil.showShort(R.string.password_not_equals);
            } else {
                lastStepnetWork();
            }
        } else {
            phoneNum = phoneEdt.getText().toString();
            requestVerifty();
        }
    }

    protected void lastStepnetWork() {
        requestRegist();
    }

    @Override
    protected boolean checkSecondEdit(String text) {
        if (goSetPassword) {
            return ValidatorUtils.isPassword(text);
        } else {
            return ValidatorUtils.isVerificationCode(text);
        }
    }

    @Override
    protected boolean checkFirstEdit(String text) {
        if (goSetPassword) {
            return ValidatorUtils.isPassword(text);
        }
        return super.checkFirstEdit(text);
    }

    @Override
    protected void secondEtDontPass() {
        if (goSetPassword) {
            super.secondEtDontPass();
        } else {
            ToastUtil.showShort(R.string.verify_code_error);
        }
    }

    @Override
    protected void firstEtDontPass() {
        if (goSetPassword) {
            super.secondEtDontPass();
        } else {
            super.firstEtDontPass();
        }
    }

    @Override
    public void onDestroyView() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroyView();
    }

    protected void requestHasRegist(String phone) {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        try {
            map.put("phonenum", AESUtils.encode(phone.trim()));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        OkGo.<String>post(HttpApi.VERIFY_HAS_REGIST)
                .tag(this)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<String>() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (HttpCode.has_regist_phone.equals(response.body())) {
                            noRegist();
                        } else {
                            hasRegist();
                        }
                    }
                });

    }

    protected void noRegist() {
        timerCode();
        requestGetVeriftyCode();
    }

    protected void hasRegist() {
        resetGetVerifyCode();
//        verificationCode.setEnabled(true);
        ToastUtil.showShort(R.string.phone_has_register);
    }

    /**
     * 获取验证码
     */
    protected void requestGetVeriftyCode() {

        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        try {
            map.put("sms", AESUtils.encode(phoneEdt.getText().toString().trim()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.<String>post(HttpApi.REQUEST_SMS_VERIFY)
                .tag(this)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<String>() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        ToastUtil.showShort("已发送验证码,请注意查收");
                    }

                    @Override
                    public void onError(Response<String> response) {
                        resetGetVerifyCode();
                        ToastUtil.showShort(R.string.get_verify_code_error);
                        super.onError(response);
                    }
                });
    }

    /**
     * 用于设置是否从注册跳转到设置密码页面(号码没有注册过)
     */
    boolean goSetPassword = false;
    String sessionid;

    protected void requestVerifty() {

        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        try {
            map.put("checksms", AESUtils.encode(getPasswordEdt().getText().toString().trim()));
            map.put("checkid", AESUtils.encode(getPhoneEdt().getText().toString().trim()));
            map.put("checktype", getCheckType());
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkGo.<String>post(HttpApi.DO_SMS_VERIFY)
                .tag(this)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<String>() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        goSetPassword = true;
                        //保存sessionid用于下个修改密码接口请求用
                        sessionid = response.body();
                        goToSetPassWord();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        goSetPassword = false;
                        ToastUtil.showShort(R.string.do_sms_verify_overtime);
                        super.onError(response);
                    }
                });
    }

    protected String getCheckType() {
        //校验类型CPSD为修改密码
        return "";
    }

    protected void requestRegist() {
        Map<String, String> data = ((LoginAndRegisterActivity) getActivity()).getData();
        if (data == null) {
            data = new HashMap<>();
        }
        data.put("regphone", phoneNum);
        //需要添加注册类型字段
        data.put("regtype", "PH");
        data.put("regpwd", AESUtils.getMd5Value(passwordEdt.getText().toString()));
        String requestBodyAES = AESUtils.getRequestBodyAES(data);
        OkGo.<BaseResponseBean<RegistBean>>post(HttpApi.DO_REGIST)
                .tag(this)
                .upJson(requestBodyAES)
                .execute(new JsonCallback<BaseResponseBean<RegistBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<RegistBean>> response) {
                        requestLogin();
                    }
                });

    }

    /**
     * 关于cookie和token由登录后的接口管理
     */
    private void requestLogin() {
        Map<String, String> map = new HashMap<>();
        map.put("loginid", "");
        map.put("loginphone", phoneNum);
        map.put("loginpwd", AESUtils.getMd5Value(passwordEdt.getText().toString()));
        map.put("logintype", "PH");
        LoginHelp.login(map, new LoginHelp.LoginCllBack() {
            @Override
            public void loginSuccess() {
                if (getActivity() != null) {
                    getActivity().setResult(LoginAndRegisterActivity.LOGIN_RESULT_CODE);
                    getActivity().finish();
                }
            }
        });
    }


    private void goToSetPassWord() {
        if (getPhoneEdt() == null || getPasswordEdt() == null) return;
        switchSetPassWord();
    }

    private void switchSetPassWord() {
        phoneEdt.setText("");
        passwordEdt.setText("");
        changeHint();
        verificationCode.setVisibility(View.GONE);
        if (timer != null) {
            timer.cancel();
        }
        phoneEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        phoneEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        passwordEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        phoneEdt.requestFocus();
        passwordEdt.clearFocus();
        btDone.setText("完成");
    }

    protected void changeHint() {
        phoneEdt.setHint(R.string.input_password);
        passwordEdt.setHint(R.string.input_password_make_sure);
    }
}
