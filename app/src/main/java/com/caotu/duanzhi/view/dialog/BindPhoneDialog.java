package com.caotu.duanzhi.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.login.BindPhoneAndForgetPwdActivity;


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
                Activity activity = MyApplication.getInstance().getRunningActivity();
                Intent intent = new Intent(activity,
                        BindPhoneAndForgetPwdActivity.class);
                intent.putExtra(BindPhoneAndForgetPwdActivity.KEY_TYPE,
                        BindPhoneAndForgetPwdActivity.BIND_TYPE);
                activity.startActivity(intent);
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
