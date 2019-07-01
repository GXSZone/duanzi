package com.caotu.duanzhi.module.detail_scroll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.caotu.duanzhi.utils.DevicesUtils;

public class HeaderHeightChangeViewGroup extends ConstraintLayout {

    private GestureDetector listener;

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

        listener = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // TODO: 2019-07-01 distanceY>0 是向上 ,向下是负
                if (distanceY > 0 && mChildView.getLayoutParams().height <= miniHeight) {
                    return false; //
                } else if (distanceY < 0 && mChildView.getLayoutParams().height >= viewHeight) {
                    return false;
                } else {
                    // 判断是否是移动
//                    Log.i("fff", "onScroll: " + distanceY);
                    ViewGroup.LayoutParams params = mChildView.getLayoutParams();
                    params.height -= distanceY;
                    if (params.height < miniHeight) {
                        params.height = miniHeight;
                    }
                    if (params.height > viewHeight) {
                        params.height = viewHeight;
                    }
                    mChildView.setLayoutParams(params);
                    return true;
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mChildView == null) {
            return super.onInterceptTouchEvent(ev);
        }
        return listener.onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return listener.onTouchEvent(ev);
    }
}
