package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SlipViewPager extends ViewPager {
    //是否可以滑动的标志位
    public boolean isSlipping = true;

    public void setSlipping(boolean slipping) {
        isSlipping = slipping;
    }

    public SlipViewPager(@NonNull Context context) {
        super(context);
    }

    public SlipViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isSlipping && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isSlipping && super.onTouchEvent(ev);
    }
}
