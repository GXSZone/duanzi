package com.caotu.duanzhi.view.refresh_header;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.caotu.duanzhi.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

import static com.scwang.smartrefresh.layout.constant.RefreshState.None;

public class NewRefreshHeader extends LinearLayout implements RefreshHeader {
    private LottieAnimationView mImage;


    public NewRefreshHeader(Context context) {
        this(context, null, 0);
    }

    public NewRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = View.inflate(context, R.layout.new_refresh_header, this);
        mImage = view.findViewById(R.id.animation_view);
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout layout, int height, int extendHeight) {
//        startAnim();
    }

    /**
     * 状态改变时调用。在这里切换第三阶段的动画卖萌小人
     *
     * @param refreshLayout
     * @param oldState
     * @param newState
     */
    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        if (newState == None) {
            stopAnim();
        }
//        switch (newState) {
//            case PullUpToLoad:
//            case None:
//            case PullDownToRefresh: //下拉刷新开始。正在下拉还没松手时调用
//                stopAnim();
//                break;
//            case Refreshing: //正在刷新。只调用一次
//                startAnim();
//                break;
//            case ReleaseToRefresh:
//
//                break;
//        }
//        Log.i("weige", "onStateChanged: "+newState.toString());
    }

    /**
     * 动画结束后调用
     */
    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        stopAnim();
        return 0;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {

    }

    public void startAnim() {
        if (mImage == null) return;
        if (mImage.isAnimating()) return;
        mImage.playAnimation();
    }

    public void stopAnim() {
        if (mImage == null) return;
        if (mImage.isAnimating()) {
            mImage.cancelAnimation();
        }
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
//        Log.i("onMoving", "percent: " + percent + "----height:" + height + "-----offset:" + offset);
        if (percent > 0.7 && percent < 0.9) {
            startAnim();
        }
        if (percent < 0.3) {
            stopAnim();
        }
    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }
}
