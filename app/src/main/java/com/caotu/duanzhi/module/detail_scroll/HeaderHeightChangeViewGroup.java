package com.caotu.duanzhi.module.detail_scroll;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.caotu.duanzhi.utils.DevicesUtils;

public class HeaderHeightChangeViewGroup extends ConstraintLayout {

    private int touchSlop;

    public HeaderHeightChangeViewGroup(Context context) {
        super(context);
    }

    public HeaderHeightChangeViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderHeightChangeViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    int viewHeight;
    View mChildView;
    int miniHeight = DevicesUtils.dp2px(200);

    /**
     * 为了获取初始化高度
     *
     * @param view
     */
    public void bindChildView(View view) {
        mChildView = view;
        viewHeight = view.getMeasuredHeight();
        //最小滑动距离
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    float downY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mChildView == null) {
            return super.onInterceptTouchEvent(ev);
        }
        boolean interceptd = false;

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                interceptd = false;
                break;

            case MotionEvent.ACTION_MOVE:
                //计算移动距离 判定是否滑动
                float dy = ev.getY() - downY;
                if (Math.abs(dy) > touchSlop) {
                    interceptd = true;
                    boolean isDownTouch = dy - downY > 0;//向下滑
                    if (isDownTouch) {
                        if (mChildView.getLayoutParams().height >= viewHeight) {
                            interceptd = false;
                        }
                    } else {
                        if (mChildView.getLayoutParams().height <= miniHeight) {
                            interceptd = false;
                        }
                    }
                } else {
                    interceptd = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                interceptd = false;
                break;
        }
        return interceptd;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final float y = ev.getRawY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("fff", "move: ");
                boolean isdownTouch = y - downY > 0;//向下滑
                if (isdownTouch) {
                    if (mChildView.getLayoutParams().height >= viewHeight) {
                        return false;
                    }
                } else {
                    if (mChildView.getLayoutParams().height <= miniHeight) {
                        return false;
                    }
                }
                final int yDiff = (int) Math.abs(y - downY);

                if (isdownTouch) {
                    ViewGroup.LayoutParams params = mChildView.getLayoutParams();
                    params.height += yDiff;
                    if (params.height >= viewHeight) {
                        params.height = viewHeight;
                    }
                    mChildView.setLayoutParams(params);
                } else {
                    // 判断是否是移动
                    ViewGroup.LayoutParams params = mChildView.getLayoutParams();
                    params.height -= yDiff;
                    if (params.height <= miniHeight) {
                        params.height = miniHeight;
                    }
                    mChildView.setLayoutParams(params);
                }
                return true;
            case MotionEvent.ACTION_UP:
                downY = 0;
                break;
        }
        return super.onTouchEvent(ev);
    }
}
