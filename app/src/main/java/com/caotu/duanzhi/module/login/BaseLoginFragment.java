package com.caotu.duanzhi.module.login;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.other.TextWatcherAdapter;
import com.caotu.duanzhi.utils.AESUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.ValidatorUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.Map;


/**
 * Created by Administrator on 2018/6/18.
 */

public abstract class BaseLoginFragment extends Fragment implements View.OnClickListener {
    public TextView verificationCode; //计时器
    protected View rootView;
    public EditText phoneEdt, passwordEdt;
    private TextView btDone;
    private CountDownTimer timer;

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(getFragmentLayoutId(), container, false);
        initView(rootView);
        return rootView;
    }

    protected abstract @LayoutRes
    int getFragmentLayoutId();

    @CallSuper
    protected void initView(View rootView) {
        phoneEdt = rootView.findViewById(R.id.phone_et);
        passwordEdt = rootView.findViewById(R.id.password_et);
        btDone = rootView.findViewById(R.id.fragment_login_login_but);
        btDone.setOnClickListener(this);

        initEtlistener();
        verificationCode = rootView.findViewById(R.id.tv_verification_code);
        if (verificationCode != null) {
            verificationCode.setOnClickListener(this);
        }
    }


    protected void initEtlistener() {
        phoneEdt.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString();
                if (passwordEdt == null || passwordEdt.getText() == null
                        || phoneEdt == null || phoneEdt.getText() == null
                        || TextUtils.isEmpty(content)) {
                    setConfirmButton(false);
                    return;
                }
                boolean firstFinish = phoneStrategy(content);
                if (verificationCode != null) {
                    verificationCode.setEnabled(firstFinish);
                }
                boolean secondFinish = getSecondEditStrategy(getPasswordEdt());
                setConfirmButton(firstFinish && secondFinish);
            }
        });

        passwordEdt.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString();
                if (passwordEdt == null || passwordEdt.getText() == null
                        || phoneEdt == null || phoneEdt.getText() == null
                        || TextUtils.isEmpty(content)) {
                    setConfirmButton(false);
                    return;
                }
                boolean firstFinish = phoneStrategy(content);
                boolean secondFinish = getSecondEditStrategy(content);
                setConfirmButton(firstFinish && secondFinish);
            }
        });
    }

    /**
     * 子类处理第二个输入框的处理策略
     *
     * @param content
     */
    protected abstract boolean getSecondEditStrategy(String content);


    /**
     * 父类提供一些输入框的策略,子类直接调用即可
     *
     * @param content
     * @return
     */
    protected boolean passWordStrategy(String content) {
        return (content.length() <= 16 && content.length() >= 6);
    }

    protected boolean phoneStrategy(String content) {
        return (content.length() == 11);
    }

    /**
     * 默认两个输入框的条件都满足
     *
     * @param isComplete
     */
    public void setConfirmButton(boolean isComplete) {
        btDone.setEnabled(isComplete);
    }

    /**
     * 子类可以复写验证规则
     *
     * @param text
     * @return
     */
    protected boolean checkFirstEdit(String text) {
        return ValidatorUtils.isMobile(text);
    }

    public String getPhoneEdt() {
        if (phoneEdt == null) return null;
        return phoneEdt.getText().toString().trim();
    }

    public String getPasswordEdt() {
        if (passwordEdt == null) return null;
        return passwordEdt.getText().toString().trim();
    }

    protected boolean checkSecondEdit(String text) {
        return ValidatorUtils.isPassword(text);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fragment_login_login_but) {
            if (!checkFirstEdit(getPhoneEdt())) {
                firstEtDontPass();
                return;
            }
            if (passwordEdt == null || passwordEdt.getText() == null ||
                    !checkSecondEdit(getPasswordEdt())) {
                secondEtDontPass();
                return;
            }
            doBtClick(v);
        } else if (v.getId() == R.id.tv_user_agreement) {
            WebActivity.openWeb("用户协议", BaseConfig.KEY_USER_AGREEMENT, false);
        } else if (v.getId() == R.id.tv_verification_code) {
            //先判断手机号正确性
            boolean checkPhonePass = checkFirstEdit(getPhoneEdt());
            if (checkPhonePass) {
                initTimer();
                requestGetVeriftyCode();
            } else {
                ToastUtil.showShort(R.string.phone_number_not_right);
            }
        }
    }

    /**
     * 获取验证码
     */
    protected void requestGetVeriftyCode() {

        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        try {
            map.put("sms", AESUtils.encode(getPhoneEdt()));
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

    protected void resetGetVerifyCode() {
        verificationCode.setText(R.string.get_verify_code);
        verificationCode.setEnabled(true);
    }

    /**
     * 开启计时器读秒
     */
    public void initTimer() {
        if (timer == null) {
            timer = new CountDownTimer(60 * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        int remainTime = (int) (millisUntilFinished / 1000L);
                        if (verificationCode != null) {
                            verificationCode.setText(String.format("%ds", remainTime));
                        }
                    }
                }

                @Override
                public void onFinish() {
                    if (verificationCode != null) {
                        verificationCode.setText(R.string.get_verify_code);
                        verificationCode.setEnabled(true);
                    }
                }
            };
        }
        timer.start();
        verificationCode.setEnabled(false);
    }

    @Override
    public void onDestroyView() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroyView();
    }

    /**
     * 用于处理第二个输入框输入没有通过的情况
     */
    protected void secondEtDontPass() {
        ToastUtil.showShort(R.string.password_not_right);
    }

    /**
     * 用于处理第一个输入框输入没有通过的情况
     */
    protected void firstEtDontPass() {
        ToastUtil.showShort(R.string.phone_number_not_right);
    }

    protected abstract void doBtClick(View v);

}