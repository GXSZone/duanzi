package com.caotu.duanzhi.module.detail_scroll;

import android.content.Context;
import android.util.AttributeSet;
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
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (mChildView.getLayoutParams().height > miniHeight) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mChildView.getLayoutParams().height > miniHeight) {
                    return super.onInterceptTouchEvent(ev);
                }
                final int yDiff = (int) Math.abs(y - downY);
                // 判断是否是移动
                if (yDiff > touchSlop) {
                    ViewGroup.LayoutParams params = mChildView.getLayoutParams();
                    params.height = viewHeight - yDiff;
                    mChildView.setLayoutParams(params);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }
}
