package com.caotu.duanzhi.module.base;

import com.from.view.swipeback.ISwipeBack;

/**
 * 为了统一全屏时取消侧滑返回,动态设置侧滑返回功能
 */
public abstract class BaseSwipeActivity extends BaseActivity implements ISwipeBack {
    boolean canSwipe = true;

    @Override
    public boolean isEnableGesture() {
        return canSwipe;
    }

    @Override
    public void setCanSwipe(boolean swipe) {
        canSwipe = swipe;
    }
}
