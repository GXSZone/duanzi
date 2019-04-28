package com.caotu.duanzhi.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.caotu.duanzhi.R;


/**
 * 统一弹窗IOS样式
 */

public class BaseIOSDialog extends Dialog implements View.OnClickListener {

    private OnClickListener onClickListener;
    private String mCancelText;
    private String mOKText;
    private String mTitleText;

    public BaseIOSDialog(Context context, OnClickListener onClickListener) {
        super(context, R.style.customDialog);
        this.onClickListener = onClickListener;
    }

    public BaseIOSDialog setCancelText(String cancelText) {
        mCancelText = cancelText;
        return this;
    }

    public BaseIOSDialog setOkText(String okText) {
        mOKText = okText;
        return this;
    }

    public BaseIOSDialog setTitleText(String titleText) {
        mTitleText = titleText;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_publish_save_dialog);
        TextView cancelBt = findViewById(R.id.cancel_action);
        cancelBt.setOnClickListener(this);
        TextView okBt = findViewById(R.id.ok_action);
        okBt.setOnClickListener(this);
        TextView titleText = findViewById(R.id.title_text);
        if (!TextUtils.isEmpty(mOKText)) {
            okBt.setText(mOKText);
        }
        if (!TextUtils.isEmpty(mCancelText)) {
            cancelBt.setText(mCancelText);
        }
        if (!TextUtils.isEmpty(mTitleText)) {
            titleText.setText(mTitleText);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_action:
                onClickListener.cancelAction();
                break;
            case R.id.ok_action:
                onClickListener.okAction();
                break;
        }
        dismiss();
    }

    public interface OnClickListener {
        void okAction();

        void cancelAction();
    }

    public static abstract class SimpleClickAdapter implements OnClickListener {

        @Override
        public void cancelAction() {

        }
    }


    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        if (window == null) return;
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }
}
