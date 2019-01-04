package com.caotu.duanzhi.utils;

import android.app.Activity;
import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

/**
 * @author mac
 * @日期: 2018/11/6
 * @describe 键盘监听
 */
public class SoftKeyBoardListener {
    public interface OnSoftKeyBoardChangeListener {
        void keyBoardShow(int height);

        void keyBoardHide();
    }

    private View rootView;//activity的根视图
    private int screenBottom;//纪录根视图的显示高度
    private OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener;
    private boolean isShow = false;//软键盘是否显示
    private ViewTreeObserver.OnGlobalLayoutListener listener;

    private SoftKeyBoardListener(Activity activity) {
        int state = WindowManager.LayoutParams.SOFT_INPUT_MASK_STATE & activity.getWindow().getAttributes().softInputMode;
        switch (state) {
            case WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE:
            case WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE:
                isShow = true;
                break;
        }
        //获取activity的根视图
        rootView = activity.getWindow().getDecorView();
        screenBottom = activity.getWindowManager().getDefaultDisplay().getHeight();
        //监听视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变
        listener = () -> {
            //获取当前根视图在屏幕上显示的大小
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            if (!isShow && screenBottom > r.bottom) {
                isShow = true;
                if (onSoftKeyBoardChangeListener != null) {
                    onSoftKeyBoardChangeListener.keyBoardShow(screenBottom - r.bottom);
                }
                return;
            }

            if (isShow && r.bottom >= screenBottom) {
                isShow = false;
                if (onSoftKeyBoardChangeListener != null) {
                    onSoftKeyBoardChangeListener.keyBoardHide();
                }
                return;
            }

        };
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(listener);
        addLifeObServer(activity);
    }

    private void setOnSoftKeyBoardChangeListener(OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener) {

        this.onSoftKeyBoardChangeListener = onSoftKeyBoardChangeListener;

    }

    public static void setListener(Activity activity, OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener) {
        SoftKeyBoardListener softKeyBoardListener = new SoftKeyBoardListener(activity);
        softKeyBoardListener.setOnSoftKeyBoardChangeListener(onSoftKeyBoardChangeListener);
    }

    private void addLifeObServer(Activity activity) {
        if (activity instanceof LifecycleOwner) {
            LifecycleOwner owner = (LifecycleOwner) activity;
            owner.getLifecycle().addObserver(new GenericLifecycleObserver() {
                @Override
                public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        if (rootView != null)
                            rootView.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
                    }
                }
            });
        }
    }

}
