package com.caotu.duanzhi.other;

import android.view.View;

import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.other.UmengHelper;

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

    public FastClickListener(long interval, boolean needLogin) {
        this.timeInterval = interval;
        isNeedLogin = needLogin;
    }

    @Override
    public void onClick(View v) {
        //加个渐变效果的判断,关注话题页面会误判,所以改成0.5
        if (v.getAlpha() < 0.5f) return;
        long nowTime = System.currentTimeMillis();
        onFastClick(v);
        if (nowTime - mLastClickTime > timeInterval) {
            if (isNeedLogin && LoginHelp.isLoginAndSkipLogin()) {
                onSingleClick();
            } else if (!isNeedLogin) {
                onSingleClick();
            }
            mLastClickTime = nowTime;
        }
    }

    protected abstract void onSingleClick();

    /**
     * 该事件是只要点击就回调,可以用于埋点
     *
     * @param v
     */
    protected void onFastClick(View v) {
        Object tag = v.getTag();
        if (tag instanceof String) {
            UmengHelper.event(((String) tag));
        }
    }
}
