package com.dueeeke.videoplayer.player;

import android.text.TextUtils;

import java.util.LinkedHashMap;

/**
 * 视频播放器管理器，需要配合addToPlayerManager()使用
 */
public class VideoViewManager {
    private final static LinkedHashMap<String, Long> progressMap = new LinkedHashMap<>();
    private BaseIjkVideoView mPlayer;

    private VideoViewManager() {
    }

    public void changeProgress(String url, long progress) {
        if (TextUtils.isEmpty(url)) return;
        progressMap.put(url, progress);
    }

    public Long getProgress(String url) {
        return progressMap.get(url);
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

    public void setCurrentVideoPlayer(BaseIjkVideoView player) {
        mPlayer = player;
    }

    public BaseIjkVideoView getCurrentVideoPlayer() {
        return mPlayer;
    }

    public void releaseVideoPlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void stopPlayback() {
        if (mPlayer != null) mPlayer.stopPlayback();
    }

    public boolean onBackPressed() {
        return mPlayer != null && mPlayer.onBackPressed();
    }
}
