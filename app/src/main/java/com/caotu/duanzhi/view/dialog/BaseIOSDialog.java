package com.caotu.duanzhi.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.other.CustomMovementMethod;
import com.caotu.duanzhi.other.SimpeClickSpan;


/**
 * 统一弹窗IOS样式
 */

public class BaseIOSDialog extends Dialog implements View.OnClickListener {

    private OnClickListener onClickListener;
    private String mCancelText;
    private String mOKText;
    private String mTitleText;
    boolean mIsTip;  //暂时用来区别授权提示框,因为需呀处理点击事件

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

    public BaseIOSDialog setTitleByTipText(boolean isTip) {
        mIsTip = isTip;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_base_dialog);
        TextView cancelBt = findViewById(R.id.cancel_action);
        cancelBt.setOnClickListener(this);
        TextView okBt = findViewById(R.id.ok_action);
        okBt.setOnClickListener(this);
        TextView titleView = findViewById(R.id.title_text);
        if (!TextUtils.isEmpty(mOKText)) {
            okBt.setText(mOKText);
        }
        if (!TextUtils.isEmpty(mCancelText)) {
            cancelBt.setText(mCancelText);
        }
        if (TextUtils.isEmpty(mTitleText)) return;
        if (mIsTip) {
            SpannableString ss = new SpannableString(BaseConfig.permission_title);
            int start = BaseConfig.permission_title.indexOf("《");
            int end = BaseConfig.permission_title.indexOf("》");
            ss.setSpan(new SimpeClickSpan() {
                @Override
                public void onSpanClick(View widget) {
                    WebActivity.openWeb("用户协议", BaseConfig.KEY_USER_AGREEMENT, false);
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleView.setText(ss);
            titleView.setMovementMethod(CustomMovementMethod.getInstance());
        } else {
            titleView.setText(mTitleText);
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
