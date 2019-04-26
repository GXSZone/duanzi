package com.caotu.duanzhi.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;

import com.caotu.duanzhi.R;


/**
 * Created by zhushijun on 2018/6/23
 */

public class CustomHelpEditDialog extends Dialog implements View.OnClickListener {
    private OnClickListener onClickListener;


    public CustomHelpEditDialog(Context context) {
        super(context, R.style.customDialog);
        setContentView(R.layout.custom_helpedit_dialog);
        findViewById(R.id.custom_helpedit_exception_but).setOnClickListener(this);
        findViewById(R.id.custom_helpedit_other_but).setOnClickListener(this);
        findViewById(R.id.custom_helpedit_nothing_but).setOnClickListener(this);

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.custom_helpedit_exception_but:
                onClickListener.onClickHelpException();
                break;
            case R.id.custom_helpedit_other_but:
                onClickListener.onClickHelpOther();
                break;
        }
        dismiss();
    }

    public interface OnClickListener {

        void onClickHelpException();

        void onClickHelpOther();

    }


    @Override
    public void show() {
        super.show();
        Window window = getWindow();
//        WindowManager.LayoutParams params = window.getAttributes();
//        params.width = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
//        params.height = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
        window.getDecorView().setPadding(0, 0, 0, 0);
//        window.setAttributes(params);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }
}
