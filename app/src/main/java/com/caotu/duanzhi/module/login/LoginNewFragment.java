package com.caotu.duanzhi.module.login;


import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.utils.AESUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;


public class LoginNewFragment extends BaseLoginFragment {

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        rootView.findViewById(R.id.bt_forget_password).setOnClickListener(this);
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    protected boolean getSecondEditStrategy(EditText phoneEdt, String content) {
        return passWordStrategy(content);
    }

    @Override
    public boolean getFirstEditStrategy(EditText phoneEdt, String content) {
        return phoneStrategy(content);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_forget_password) {
            goForgetPwdAndBindActivity();
        }
        super.onClick(v);
    }

    private void goForgetPwdAndBindActivity() {
//        Intent intent = new Intent(MyApplication.getInstance().getRunningActivity(), BindPhoneActivity.class);
//        intent.putExtra(BindPhoneActivity.KEY_TYPE, BindPhoneActivity.FORGET_PWD);
//        MyApplication.getInstance().getRunningActivity().startActivity(intent);
    }

    @Override
    protected void doBtClick(View v) {
        Map<String, String> map = new HashMap<>();
        map.put("loginid", "");
        map.put("loginphone", phoneEdt.getText().toString().trim());
        map.put("loginpwd", AESUtils.getMd5Value(passwordEdt.getText().toString().trim()));
        map.put("logintype", "PH");

        String stringBody = AESUtils.getRequestBodyAES(map);

        OkGo.<String>post(HttpApi.DO_LOGIN)
                .upJson(stringBody)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Headers headers = response.headers();
                        if (!TextUtils.isEmpty(response.body())){
                            MySpUtils.putBoolean(MySpUtils.SP_HAS_BIND_PHONE,true);
                            MySpUtils.putBoolean(MySpUtils.SP_ISLOGIN,true);
                            ToastUtil.showShort(R.string.login_success);
                            JPushManager.getInstance().loginSuccessAndSetJpushAlias();
//                            EventBusHelp.sendLoginEvent();
                            if (getActivity() != null) {
                                getActivity().setResult(LoginAndRegisterActivity.LOGIN_RESULT_CODE);
                                getActivity().finish();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        ToastUtil.showShort(R.string.login_failure);
                        super.onError(response);
                    }
                });
    }
}