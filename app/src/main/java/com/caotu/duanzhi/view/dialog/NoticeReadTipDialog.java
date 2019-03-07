package com.caotu.duanzhi.view.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.caotu.duanzhi.R;


public class NoticeReadTipDialog extends Dialog implements View.OnClickListener {


    private ButtomClick mListener;

    public NoticeReadTipDialog(Context context, ButtomClick buttomClick) {
        super(context);
        mListener = buttomClick;
        setContentView(R.layout.layout_notice_read_dialog);
        findViewById(R.id.positive_but).setOnClickListener(this);
        findViewById(R.id.cancel_but).setOnClickListener(this);
        findViewById(R.id.negative_but).setOnClickListener(this);
    }

    public interface ButtomClick {
        void ok();

        void cancle();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.negative_but:
                if (mListener != null) {
                    mListener.cancle();
                }
                break;
            case R.id.positive_but:
                if (mListener != null) {
                    mListener.ok();
                }
                break;
        }
        dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除白色背景
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}
