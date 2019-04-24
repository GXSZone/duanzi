package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdMgr;

public class PagerEnabledSlidingPaneLayout extends SlidingPaneLayout {
    //是否禁止侧滑
    private boolean prohibitSideslip = false;

    public PagerEnabledSlidingPaneLayout(Context context) {
        super(context, null);
    }

    public PagerEnabledSlidingPaneLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public PagerEnabledSlidingPaneLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean getProhibitSideslip() {
        return prohibitSideslip;
    }

    //在需要禁止或允许侧滑的地方调用该方法
    public void setProhibitSideslip(boolean prohibitSideslip) {
        this.prohibitSideslip = prohibitSideslip;
    }

    //该方法可以拦截SlidingPaneLayout的触屏事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_MOVE:
                if (JzvdMgr.getCurrentJzvd() != null &&
                        JzvdMgr.getCurrentJzvd().currentScreen == Jzvd.SCREEN_WINDOW_FULLSCREEN)
                    return false;

//                if (prohibitSideslip) {
//                    return false;
//                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_MOVE:
                if (JzvdMgr.getCurrentJzvd() != null &&
                        JzvdMgr.getCurrentJzvd().currentScreen == Jzvd.SCREEN_WINDOW_FULLSCREEN)
                    return false;
//                if (prohibitSideslip) {
//                    return false;
//                }
        }
        return super.onTouchEvent(ev);
    }

}
