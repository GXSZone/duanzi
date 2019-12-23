package com.caotu.duanzhi.module.detail_scroll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

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
    int miniHeight;
    boolean isRvScrollTop = true;

    /**
     * 为了获取初始化高度
     * 让外部传最小高度也是为了解决不能在布局界面不能预览的问题,因为成员变量miniHeight 引用了不确定类导致
     *
     * @param view
     * @param miniHeight
     */
    public void bindChildView(RecyclerView view, int miniHeight) {
        this.miniHeight = miniHeight;
        view.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isRvScrollTop = !view.canScrollVertically(-1);//滑动到顶部
            }
        });
        View mChildView = getChildAt(0);
        mChildView.post(() -> viewHeight = mChildView.getMeasuredHeight());

        listener = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // TODO: 2019-07-01 distanceY>0 是向上 ,向下是负
                if (distanceY > 0 && mChildView.getLayoutParams().height <= miniHeight) {
                    view.scrollBy(Math.round(distanceX), Math.round(distanceY));
                    return false;
                } else if (distanceY < 0
                        && mChildView.getLayoutParams().height >= viewHeight
                        && !isRvScrollTop) {//如果rv还没滑动头也是不处理滑动事件
//                    view.scrollBy(Math.round(distanceX), Math.round(distanceY));
                    return false;
                } else {
                    if (!isRvScrollTop) {
                        view.scrollBy(Math.round(distanceX), Math.round(distanceY));
                        return true;
                    }
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
        try {
            return listener.onTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return listener.onTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
