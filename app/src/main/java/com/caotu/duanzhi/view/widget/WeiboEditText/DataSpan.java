package com.caotu.duanzhi.view.widget.WeiboEditText;

import android.text.style.ForegroundColorSpan;

import com.caotu.duanzhi.Http.bean.UserBean;

public class DataSpan extends ForegroundColorSpan {
    UserBean date;

    public DataSpan(int color) {
        super(color);
    }

    public void bindDate(UserBean bean) {
        date = bean;
    }

    public UserBean getDate() {
        return date;
    }
}
