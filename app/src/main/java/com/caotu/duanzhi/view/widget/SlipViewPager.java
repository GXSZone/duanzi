package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

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
        try {
            return isSlipping && super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return isSlipping && super.onTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
