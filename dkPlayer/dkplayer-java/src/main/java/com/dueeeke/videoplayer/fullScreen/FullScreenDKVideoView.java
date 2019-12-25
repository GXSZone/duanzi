package com.dueeeke.videoplayer.fullScreen;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.player.DKVideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;

/**
 * 锁定全屏播放器
 * Created by xinyu on 2017/12/25.
 */

public class FullScreenDKVideoView extends DKVideoView {

    public FullScreenDKVideoView(@NonNull Context context) {
        super(context);
    }

    public FullScreenDKVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 直接开始全屏播放
     */
    public void startFullScreenDirectly() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null) return;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        startFullScreen();
    }

    @Override
    protected void startPlay() {
        startFullScreenDirectly();
        super.startPlay();
    }


    @Override
    public boolean onBackPressed() {
        return false;
    }
}
