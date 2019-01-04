package com.caotu.duanzhi.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;

import com.caotu.duanzhi.R;


/**
 * Created by zhushijun on 2018/6/23
 */

public class PublishSaveDialog extends Dialog implements View.OnClickListener {

    private OnClickListener onClickListener;

    public PublishSaveDialog(Context context, OnClickListener onClickListener) {
        super(context, R.style.customDialog);
        this.onClickListener = onClickListener;
        setContentView(R.layout.layout_publish_save_dialog);
        findViewById(R.id.cancel_action).setOnClickListener(this);
        findViewById(R.id.ok_action).setOnClickListener(this);
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


    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }
}
