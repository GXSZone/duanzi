package com.dueeeke.videoplayer.fullScreen;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.R;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.playerui.StandardVideoController;
import com.dueeeke.videoplayer.util.PlayerUtils;

public class FullScreenController extends StandardVideoController {
    public FullScreenController(@NonNull Context context) {
        super(context);
    }

    public FullScreenController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FullScreenController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    @Override
//    protected void initView() {
//        super.initView();
//        contentTopic.setVisibility(GONE);
//    }

    @Override
    public void videoNormalClick(int i) {
        if (i == R.id.lock) {
            doLockUnlock();
        } else if (i == R.id.iv_play || i == R.id.start_play) {
            doPauseResume();
        } else if (i == R.id.back) {
            Activity activity = PlayerUtils.scanForActivity(getContext());
            if (activity != null) {
                activity.finish();
            }
        }
    }

    @Override
    public void setPlayerState(int playerState) {
        super.setPlayerState(playerState);
        if (playerState == IjkVideoView.PLAYER_FULL_SCREEN) {
            mFullScreenButton.setVisibility(GONE);
            mBottomContainer.setPadding(0, 0, PlayerUtils.dp2px(getContext(), 10), 0);
        }
    }
}
