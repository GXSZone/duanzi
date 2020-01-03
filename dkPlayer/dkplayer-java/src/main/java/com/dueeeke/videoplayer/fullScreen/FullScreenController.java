package com.dueeeke.videoplayer.fullScreen;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.R;
import com.dueeeke.videoplayer.player.DKVideoView;
import com.dueeeke.videoplayer.controller.StandardVideoController;
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
    public void videoNormalClick(int i) {
        if (i == R.id.back) {
            Activity activity = PlayerUtils.scanForActivity(getContext());
            if (activity != null) {
                activity.finish();
            }
        } else {
            super.videoNormalClick(i);
        }
    }

    @Override
    public void setPlayerState(int playerState) {
        super.setPlayerState(playerState);
        if (playerState == DKVideoView.PLAYER_FULL_SCREEN) {
            mFullScreenButton.setVisibility(GONE);
            mBottomContainer.setPadding(0, 0, PlayerUtils.dp2px(getContext(), 10), 0);
        }
    }

    @Override
    protected void hideAllViews() {
        super.hideAllViews();
        if (isMySelf) return;
        if (mCurrentPlayState == DKVideoView.STATE_PLAYBACK_COMPLETED) {
            moreIv.setVisibility(VISIBLE);
        } else {
            moreIv.setVisibility(GONE);
        }
    }

    @Override
    protected void showAllViews() {
        super.showAllViews();
        if (isMySelf || moreIv == null) return;
        moreIv.setVisibility(VISIBLE);
    }
}
