package com.caotu.duanzhi.other;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

public class ScrollAwareFABBehavior extends CoordinatorLayout.Behavior {
    public ScrollAwareFABBehavior() {
    }

    public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    /**
     * 向上滑动隐藏,向下滑动显示
     * @param coordinatorLayout
     * @param child
     * @param target
     * @param dxConsumed
     * @param dyConsumed
     * @param dxUnconsumed
     * @param dyUnconsumed
     * @param type
     */
    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
//        Log.i("ScrollAwareFABBehavior", "dyConsumed: "+dyConsumed+"      dyUnconsumed: "+dyUnconsumed);

        if (dyConsumed > 0 && child.getScaleX() > 0) {
            child.animate().scaleX(0).scaleY(0);
        } else if (dyConsumed < 0 && child.getScaleX() < 1) {
            child.animate().scaleX(1.0f).scaleY(1.0f);
        }
    }
}
