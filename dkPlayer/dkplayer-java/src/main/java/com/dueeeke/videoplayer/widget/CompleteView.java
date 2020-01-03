package com.dueeeke.videoplayer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.R;
import com.dueeeke.videoplayer.controller.StandardVideoController;
import com.dueeeke.videoplayer.listener.MyVideoOtherListener;
import com.dueeeke.videoplayer.player.DKVideoView;

/**
 * 自动播放完成界面
 */
public class CompleteView extends FrameLayout implements View.OnClickListener {


    public CompleteView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public CompleteView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.dkplayer_layout_complete_view, this, true);
        findViewById(R.id.replay_text).setOnClickListener(this);
        findViewById(R.id.download_text).setOnClickListener(this);
        findViewById(R.id.share_platform_weixin).setOnClickListener(this);
        findViewById(R.id.share_platform_qq).setOnClickListener(this);
        findViewById(R.id.share_platform_qyq).setOnClickListener(this);
        findViewById(R.id.share_platform_qqzone).setOnClickListener(this);

    }

    StandardVideoController mController;

    public void bindController(StandardVideoController controller) {
        mController = controller;
    }


    public void onPlayStateChanged(int playState) {
        if (playState == DKVideoView.STATE_PLAYBACK_COMPLETED) {
            setVisibility(VISIBLE);
            bringToFront();
        } else {
            setVisibility(GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.replay_text) {
            mController.replayAction();
            return;
        }
        if (mController.getVideoListener() == null) return;
        if (i == R.id.download_text) {
            mController.getVideoListener().download();
        } else if (i == R.id.share_platform_weixin) {
            mController.getVideoListener().share(MyVideoOtherListener.weixin);
        } else if (i == R.id.share_platform_qq) {
            mController.getVideoListener().share(MyVideoOtherListener.qq);
        } else if (i == R.id.share_platform_qyq) {
            mController.getVideoListener().share(MyVideoOtherListener.qyq);
        } else if (i == R.id.share_platform_qqzone) {
            mController.getVideoListener().share(MyVideoOtherListener.qqzone);
        }
    }
}
