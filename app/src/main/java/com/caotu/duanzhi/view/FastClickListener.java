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
    private boolean isNeedLogin = true;

    public FastClickListener() {

    }

    public void setNeedLogin(boolean needLogin) {
        isNeedLogin = needLogin;
    }

    public void setTimeInterval(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    public FastClickListener(long interval) {
        this.timeInterval = interval;
    }

    @Override
    public void onClick(View v) {
        long nowTime = System.currentTimeMillis();
        if (nowTime - mLastClickTime > timeInterval) {
            if (isNeedLogin && LoginHelp.isLoginAndSkipLogin()) {
                onSingleClick();
            } else if (!isNeedLogin) {
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
