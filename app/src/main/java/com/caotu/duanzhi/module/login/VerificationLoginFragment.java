package com.caotu.duanzhi.module.login;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.utils.AESUtils;
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

    private TextView verificationCode;
    private CountDownTimer timer;


    @Override
    protected int getFragmentLayoutId() {
        return R.layout.fragment_verification_login;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        verificationCode = rootView.findViewById(R.id.tv_verification_code);

        verificationCode.setOnClickListener(v -> {
            //先判断手机号正确性
            boolean checkPhonePass = checkFirstEdit(getPhoneEdt().getText().toString());
            if (checkPhonePass) {
                timerCode();
                requestGetVeriftyCode();
            } else {
                ToastUtil.showShort(R.string.phone_number_not_right);
            }

        });
        rootView.findViewById(R.id.tv_click_pw_login).setOnClickListener(v -> {
            if (getActivity() != null && getActivity() instanceof LoginAndRegisterActivity) {
                ((LoginAndRegisterActivity) getActivity()).
                        getViewPager().setCurrentItem(1, true);
            }
        });
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
        //只验证验证码的长度
        return content.length() >= 4;
    }


    @Override
    public boolean getFirstEditStrategy(EditText phoneEdt, String content) {
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


    @Override
    protected void doBtClick(View v) {
        String phoneNum = phoneEdt.getText().toString();
        String sms = passwordEdt.getText().toString();
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        try {
            map.put("phone", AESUtils.encode(phoneNum.trim()));
            map.put("sms", AESUtils.encode(sms.trim()));
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

    @Override
    public void onDestroyView() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroyView();
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

}
