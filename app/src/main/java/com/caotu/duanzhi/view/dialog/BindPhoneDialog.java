package com.caotu.duanzhi.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.login.BindPhoneAndForgetPwdActivity;
import com.caotu.duanzhi.utils.HelperForStartActivity;


public class BindPhoneDialog extends Dialog implements View.OnClickListener {


    public BindPhoneDialog(Context context) {
        super(context);
        setContentView(R.layout.layout_bind_phone_dialog);
        findViewById(R.id.positive_but).setOnClickListener(this);
        findViewById(R.id.cancel_but).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_but:
                dismiss();
                break;
            case R.id.positive_but:
                HelperForStartActivity.openBindPhoneOrPsw(BindPhoneAndForgetPwdActivity.BIND_TYPE);
                dismiss();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除白色背景
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}
