package com.caotu.duanzhi.view.widget.WeiboEditText;

import android.text.Editable;

import com.caotu.duanzhi.module.TextWatcherAdapter;

public abstract class AtTextWatcher extends TextWatcherAdapter {
    private int editTextStart;
    private int editTextCount;
    private int editTextBefore;
    private boolean delete;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        delete = count > after;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        editTextStart = start;
        editTextCount = count;
        editTextBefore = before;
    }

    @Override
    public void afterTextChanged(Editable editable) {

        int start = editTextStart;
        int count = delete ? editTextBefore : editTextCount;

        if (count <= 0 || editable.length() < start + count) {
            return;
        }
        CharSequence s = editable.subSequence(start, start + count);
        if (s == null) {
            return;
        }
        if (s.toString().equals("@")) {
            // 启动@联系人界面
//            ToastUtil.showShort("触发@功能");
            ByDealAt();
        }
    }

    public abstract void ByDealAt();
}
