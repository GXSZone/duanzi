package com.dueeeke.videoplayer.player;

/**
 * 视频播放器管理器，需要配合addToPlayerManager()使用
 */
public class VideoViewManager {
    private BaseVideoView mPlayer;

    private VideoViewManager() {
    }

    private static VideoViewManager sInstance;

    public static VideoViewManager instance() {
        if (sInstance == null) {
            synchronized (VideoViewManager.class) {
                if (sInstance == null) {
                    sInstance = new VideoViewManager();
                }
            }
        }
        return sInstance;
    }

    public void setCurrentVideoPlayer(BaseVideoView player) {
        mPlayer = player;
    }

    public BaseVideoView getCurrentVideoPlayer() {
        return mPlayer;
    }

    public void releaseVideoPlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    /**
     * 这个用于列表划出去释放操作
     */
    public void stopPlayback() {
        if (mPlayer != null) mPlayer.stopPlayback();
    }

    public boolean onBackPressed() {
        return mPlayer != null && mPlayer.onBackPressed();
    }
}
