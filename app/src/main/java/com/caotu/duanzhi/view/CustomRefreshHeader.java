package com.caotu.duanzhi.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

public class CustomRefreshHeader extends LinearLayout implements RefreshHeader {
    private ImageView mImage;
    private AnimationDrawable refreshingAnim;
    private TextView headerText;
    public static String REFRESH_HEADER_PULLING = "下拉可以刷新";//"下拉可以刷新";
    public static String REFRESH_HEADER_REFRESHING = "一支穿云箭，段子来相见";//"正在刷新...";
    public static String REFRESH_HEADER_RELEASE = "别拉了，我开始冲刺了";//"释放立即刷新";

    public CustomRefreshHeader(Context context) {
        this(context, null, 0);
    }

    public CustomRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = View.inflate(context, R.layout.widget_custom_refresh_header, this);
        mImage = view.findViewById(R.id.iv_refresh_header);
        headerText = view.findViewById(R.id.pull_text);
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
        switch (newState) {
            case PullDownToRefresh: //下拉刷新开始。正在下拉还没松手时调用
                //每次重新下拉时，将图片资源重置为小人的大脑袋
                mImage.setImageResource(R.drawable.refresh_00);
                if (headerText != null) {
                    headerText.setText(REFRESH_HEADER_PULLING);
                }
                break;
            case Refreshing: //正在刷新。只调用一次
                //状态切换为正在刷新状态时，设置图片资源为小人卖萌的动画并开始执行
                if (headerText != null) {
                    headerText.setText(REFRESH_HEADER_REFRESHING);
                }
                if (refreshingAnim != null) {
                    mImage.setImageDrawable(refreshingAnim);
                } else {
                    mImage.setImageResource(R.drawable.anim_publishing);
                    refreshingAnim = (AnimationDrawable) mImage.getDrawable();
                }
                refreshingAnim.start();
                break;
            case ReleaseToRefresh:
                if (refreshingAnim != null && refreshingAnim.isRunning()) {
                    refreshingAnim.stop();
                }
                if (headerText != null) {
                    headerText.setText(REFRESH_HEADER_RELEASE);
                }
                break;
        }
    }

    /**
     * 动画结束后调用
     */
    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        if (refreshingAnim != null && refreshingAnim.isRunning()) {
            refreshingAnim.stop();
        }
        return 0;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {

    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
//        Log.i("onMoving", "percent: " + percent + "----height:" + height + "-----offset:" + offset);
        if (percent > 1.0f) return;
        mImage.setScaleX(percent);
        mImage.setScaleY(percent);
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
