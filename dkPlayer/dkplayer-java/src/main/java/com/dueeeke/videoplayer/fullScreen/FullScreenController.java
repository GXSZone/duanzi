package com.dueeeke.videoplayer.fullScreen;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

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

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.lock) {
            doLockUnlock();
        } else if (i == R.id.iv_play || i == R.id.iv_replay) {
            doPauseResume();
        } else if (i == R.id.back) {
            PlayerUtils.scanForActivity(getContext()).finish();
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
