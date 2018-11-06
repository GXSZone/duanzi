package com.caotu.duanzhi.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.login.BindPhoneAndForgetPwdActivity;
import com.caotu.duanzhi.utils.DevicesUtils;


public class BindPhoneDialog extends Dialog implements View.OnClickListener {


    public BindPhoneDialog(Context context) {
        super(context, R.style.customDialog);
        setContentView(R.layout.layout_bindphone_dialog);
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
    public void show() {
        super.show();
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DevicesUtils.getSrecchWidth(MyApplication.getInstance());
        params.height = DevicesUtils.getScreenHeight(MyApplication.getInstance());
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setAttributes(params);
//        window.setBackgroundDrawableResource(R.drawable.shape_transparent_bg);
    }


}
