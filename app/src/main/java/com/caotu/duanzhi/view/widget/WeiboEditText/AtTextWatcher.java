package com.caotu.duanzhi.view.widget.WeiboEditText;

import android.text.Editable;

import com.caotu.duanzhi.other.TextWatcherAdapter;

public abstract class AtTextWatcher extends TextWatcherAdapter {
    private int editTextStart;
    private int editTextCount;
    private int editTextBefore;
    private boolean delete;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        delete = count > after; //新输入还是删除的判断
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
        if (s.toString().equals("@") && !delete) { //还是是输入字符增多的情况下
            // 启动@联系人界面
            ByDealAt();
        }
    }

    public abstract void ByDealAt();
}
