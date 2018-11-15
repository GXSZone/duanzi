package com.caotu.duanzhi.module.login;


import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.AESUtils;

import java.util.HashMap;
import java.util.Map;


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
        Intent intent = new Intent(MyApplication.getInstance().getRunningActivity(),
                BindPhoneAndForgetPwdActivity.class);
        intent.putExtra(BindPhoneAndForgetPwdActivity.KEY_TYPE,
                BindPhoneAndForgetPwdActivity.FORGET_PWD);
        MyApplication.getInstance().getRunningActivity().startActivity(intent);
    }

    @Override
    protected void doBtClick(View v) {
        Map<String, String> map = new HashMap<>();
        map.put("loginid", "");
        map.put("loginphone", phoneEdt.getText().toString().trim());
        map.put("loginpwd", AESUtils.getMd5Value(passwordEdt.getText().toString().trim()));
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
}