package com.caotu.duanzhi.view.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.NotificationUtil;


public class NotifyEnableDialog extends Dialog implements View.OnClickListener {


    public NotifyEnableDialog(Context context) {
        super(context);
        setContentView(R.layout.layout_notice_dialog);
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
                NotificationUtil.open(MyApplication.getInstance().getRunningActivity());
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
