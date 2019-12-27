package com.caotu.duanzhi.module.detail_scroll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.caotu.duanzhi.utils.DevicesUtils;

/**
 * 嵌套滑动参考:https://www.jianshu.com/p/3682dde60dbf
 * 还差点慢慢滑动抖动,滑动的时候又去改变rv 约束的头布局高度导致滑动监听错乱引起
 */
public class HeaderHeightChangeViewGroup extends ConstraintLayout implements NestedScrollingParent2 {

    private NestedScrollingParentHelper parentHelper;
    private View mChildView;

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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        miniHeight = DevicesUtils.dp2px(200);
        mChildView = getChildAt(0);
        mChildView.post(() -> viewHeight = mChildView.getMeasuredHeight());
        parentHelper = new NestedScrollingParentHelper(this);
    }

    /**
     * 即将开始嵌套滑动，此时嵌套滑动尚未开始，由子控件的 startNestedScroll 方法调用
     *
     * @param child  嵌套滑动对应的父类的子类(因为嵌套滑动对于的父控件不一定是一级就能找到的，可能挑了两级父控件的父控件，child的辈分>=target)
     * @param target 具体嵌套滑动的那个子类
     * @param axes   嵌套滑动支持的滚动方向
     * @param type   嵌套滑动的类型，有两种ViewCompat.TYPE_NON_TOUCH fling效果,ViewCompat.TYPE_TOUCH 手势滑动
     * @return true 表示此父类开始接受嵌套滑动，只有true时候，才会执行下面的 onNestedScrollAccepted 等操作
     */
    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return target instanceof RecyclerView && ((axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0);
    }

    /**
     * 当onStartNestedScroll返回为true时，也就是父控件接受嵌套滑动时，该方法才会调用
     *
     * @param child
     * @param target
     * @param axes
     * @param type
     */
    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        parentHelper.onNestedScrollAccepted(child, target, axes, type);
    }

    /**
     * 在子控件开始滑动之前，会先调用父控件的此方法，由父控件先消耗一部分滑动距离，并且将消耗的距离存在consumed中，传递给子控件
     * 在嵌套滑动的子View未滑动之前
     * ，判断父view是否优先与子view处理(也就是父view可以先消耗，然后给子view消耗）
     *
     * @param target   具体嵌套滑动的那个子类
     * @param dx       水平方向嵌套滑动的子View想要变化的距离
     * @param dy       垂直方向嵌套滑动的子View想要变化的距离 dy<0向下滑动 dy>0 向上滑动
     * @param consumed 这个参数要我们在实现这个函数的时候指定，回头告诉子View当前父View消耗的距离
     *                 consumed[0] 水平消耗的距离，consumed[1] 垂直消耗的距离 好让子view做出相应的调整
     * @param type     滑动类型，ViewCompat.TYPE_NON_TOUCH fling效果,ViewCompat.TYPE_TOUCH 手势滑动
     */
    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
//        Log.i(TAG, "onNestedPreScroll: " + dy);
        //如果子view欲向上滑动，则先交给父view滑动  这里不管手势滚动还是fling都处理
        boolean hideTop = dy > 0 && mChildView.getLayoutParams().height > miniHeight;
        //如果子view欲向下滑动，必须要子view不能向下滑动后，才能交给父view滑动
        boolean showTop = dy < 0 && !target.canScrollVertically(-1);
        if (showTop || hideTop) {
            ViewGroup.LayoutParams params = mChildView.getLayoutParams();
            params.height -= dy;
            if (params.height < miniHeight) {
                params.height = miniHeight;
            }
            if (params.height > viewHeight) {
                params.height = viewHeight;
            }
            mChildView.setLayoutParams(params);
            consumed[1] = dy;
        }
    }

    /**
     * 在 onNestedPreScroll 中，父控件消耗一部分距离之后，剩余的再次给子控件，
     * 子控件消耗之后，如果还有剩余，则把剩余的再次还给父控件
     *
     * @param target       具体嵌套滑动的那个子类
     * @param dxConsumed   水平方向嵌套滑动的子控件滑动的距离(消耗的距离)
     * @param dyConsumed   垂直方向嵌套滑动的子控件滑动的距离(消耗的距离)
     * @param dxUnconsumed 水平方向嵌套滑动的子控件未滑动的距离(未消耗的距离)
     * @param dyUnconsumed 垂直方向嵌套滑动的子控件未滑动的距离(未消耗的距离)
     * @param type         滑动类型，ViewCompat.TYPE_NON_TOUCH fling效果,ViewCompat.TYPE_TOUCH 手势滑动
     */

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
//        Log.i(TAG, "子控件消耗的距离: " + dyConsumed + "    子控件未消耗的距离" + dyUnconsumed);
    }

    /**
     * 停止滑动
     *
     * @param target
     * @param type   滑动类型，ViewCompat.TYPE_NON_TOUCH fling效果,ViewCompat.TYPE_TOUCH 手势滑动
     */
    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        parentHelper.onStopNestedScroll(target, type);
    }

    @Override
    public int getNestedScrollAxes() {
        return parentHelper.getNestedScrollAxes();
    }


    /**
     * 为了让子控件也处理fling，我们需要在onNestedPreFling方法中返回false。
     * 因为在嵌套滑动机制中，如果该方法返回true,那么子控件就没有机会处理fling了
     *
     * @param target
     * @param velocityX
     * @param velocityY
     * @return
     */
    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return false;
    }
}
