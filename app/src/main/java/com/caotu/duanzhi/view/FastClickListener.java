package com.caotu.duanzhi.view;

import android.view.View;

import com.caotu.duanzhi.module.login.LoginHelp;

/**
 * @author mac
 * @日期: 2018/11/19
 * @describe 过滤快速点击事件
 */
public abstract class FastClickListener implements View.OnClickListener {
    private long mLastClickTime;
    private long timeInterval = 500L;

    public FastClickListener() {

    }

    public FastClickListener(long interval) {
        this.timeInterval = interval;
    }

    @Override
    public void onClick(View v) {
        long nowTime = System.currentTimeMillis();
        if (nowTime - mLastClickTime > timeInterval) {
            // 单次点击事件,判断是否登录也在内部做判断
            if (LoginHelp.isLoginAndSkipLogin()) {
                onSingleClick();
            }
            mLastClickTime = nowTime;
        } else {
            // 快速点击事件
            onFastClick();
        }
    }

    protected abstract void onSingleClick();

    protected void onFastClick() {
//        ToastUtil.showShort("您的操作太频繁,请稍后再试");
    }
}
