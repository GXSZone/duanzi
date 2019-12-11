package com.caotu.duanzhi.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.caotu.duanzhi.R;

public class RvTestDialog extends Dialog {


    public RvTestDialog(@NonNull Context context) {
        super(context);
    }

    public RvTestDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public void showKeyboard(EditText text) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(text, InputMethodManager.SHOW_FORCED);
    }

    protected void initView() {
        EditText editText = findViewById(R.id.et_send_content);
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                showKeyboard(editText);
            }
        }, 500);


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.include_detail_bottom_reply);
        initView();

    }

    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        if (window == null) return;
        window.setGravity(Gravity.BOTTOM);
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }
}
