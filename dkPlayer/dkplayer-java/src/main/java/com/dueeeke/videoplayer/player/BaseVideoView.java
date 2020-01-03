package com.dueeeke.videoplayer.player;

import android.content.Context;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.controller.BaseVideoController;
import com.dueeeke.videoplayer.controller.MediaPlayerControl;
import com.dueeeke.videoplayer.listener.OnVideoViewStateChangeListener;
import com.dueeeke.videoplayer.listener.PlayerEventListener;
import com.dueeeke.videoplayer.util.L;
import com.dueeeke.videoplayer.util.PlayerUtils;

/**
 * 播放器
 * Created by Devlin_n on 2017/4/7.
 */

public abstract class BaseVideoView extends FrameLayout implements MediaPlayerControl, PlayerEventListener {

    protected AbstractPlayer mMediaPlayer;//播放器
    @Nullable
    protected BaseVideoController mVideoController;//控制器
    protected boolean mIsMute;//是否静音

    protected String mCurrentUrl;//当前播放视频的地址
    protected long mCurrentPosition;//当前正在播放视频的位置

    //播放器的各种状态
    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 5;
    public static final int STATE_BUFFERING = 6;
    public static final int STATE_BUFFERED = 7;
    protected int mCurrentPlayState = STATE_IDLE;//当前播放器的状态

    public static final int PLAYER_NORMAL = 10;        // 普通播放器
    public static final int PLAYER_FULL_SCREEN = 11;   // 全屏播放器
    public static final int PLAYER_TINY_SCREEN = 12;   // 小屏播放器
    protected int mCurrentPlayerState = PLAYER_NORMAL;


    protected boolean mIsLockFullScreen;//是否锁定屏幕
    /**
     * 设置调用{@link #start()}后在移动环境下是否继续播放，默认不继续播放
     * 注意：由于{@link #IS_PLAY_ON_MOBILE_NETWORK}是static的，设置一次之后会记住状态，不需要重复设置
     */
    public static boolean IS_PLAY_ON_MOBILE_NETWORK = false;//记录是否在移动网络下播放视频

    protected OnVideoViewStateChangeListener videoViewStateChangeListener;

    @Nullable
    protected ProgressManager mProgressManager;
    /**
     * 是否循环播放
     */
    protected boolean mIsLooping;

    /**
     * 是否使用MediaCodec进行解码（硬解码），默认不开启，使用软解
     */
    protected boolean mEnableMediaCodec = true;

    protected boolean mAddToVideoViewManager;

    public BaseVideoView(@NonNull Context context) {
        super(context);
    }

    public BaseVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 初始化播放器
     */
    protected void initPlayer() {
        // TODO: 2019-06-21 切换播放器内核
//        mMediaPlayer = new IjkPlayer(getContext());
        mMediaPlayer = new ExoMediaNewPlayer(getContext());
        mMediaPlayer.bindVideoView(this);
        mMediaPlayer.initPlayer();
        mMediaPlayer.setEnableMediaCodec(mEnableMediaCodec);
        mMediaPlayer.setLooping(mIsLooping);
    }

    /**
     * 向Controller设置播放状态，用于控制Controller的ui展示
     */
    protected void setPlayState(int playState) {
        mCurrentPlayState = playState;
        if (mVideoController != null)
            mVideoController.setPlayState(playState);
        if (videoViewStateChangeListener != null) {
            videoViewStateChangeListener.onPlayStateChanged(playState);
        }
    }

    /**
     * 向Controller设置播放器状态，包含全屏状态和非全屏状态
     */
    protected void setPlayerState(int playerState) {
        mCurrentPlayerState = playerState;
        if (mVideoController != null) {
            mVideoController.setPlayerState(playerState);
        }
        if (videoViewStateChangeListener != null) {
            videoViewStateChangeListener.onPlayerStateChanged(playerState);
        }
    }

    /**
     * 开始准备播放（直接播放）
     */
    protected void startPrepare(boolean needReset) {
        if (mMediaPlayer == null || TextUtils.isEmpty(mCurrentUrl)) return;
        if (needReset) mMediaPlayer.reset();
        mMediaPlayer.setDataSource(mCurrentUrl, null);
        mMediaPlayer.prepareAsync();
        setPlayState(STATE_PREPARING);
        setPlayerState(isFullScreen() ? PLAYER_FULL_SCREEN : isTinyScreen() ? PLAYER_TINY_SCREEN : PLAYER_NORMAL);
    }

    /**
     * 开始播放
     */
    @Override
    public void start() {
        if (mMediaPlayer == null || mCurrentPlayState == STATE_IDLE) {
            startPlay();
        } else if (isInPlaybackState()) {
            startInPlaybackState();
        }
        setKeepScreenOn(true);
    }

    /**
     * 第一次播放
     */
    protected void startPlay() {
        if (mAddToVideoViewManager) {
            VideoViewManager.instance().releaseVideoPlayer();
            VideoViewManager.instance().setCurrentVideoPlayer(this);
        }
        if (checkNetwork()) return;
        if (mProgressManager != null) {
            mCurrentPosition = mProgressManager.getSavedProgress(mCurrentUrl);
        }

        initPlayer();
        startPrepare(false);
    }

    protected boolean checkNetwork() {
        if (PlayerUtils.getNetworkType(getContext()) == PlayerUtils.NETWORK_MOBILE
                && !IS_PLAY_ON_MOBILE_NETWORK) {
            if (mVideoController != null) {
                mVideoController.showStatusView();
            }
            return true;
        }
        return false;
    }

    /**
     * 播放状态下开始播放
     */
    protected void startInPlaybackState() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            setPlayState(STATE_PLAYING);
        }
    }

    /**
     * 暂停播放
     */
    @Override
    public void pause() {
        if (isPlaying()) {
            mMediaPlayer.pause();
            setPlayState(STATE_PAUSED);
            setKeepScreenOn(false);
        }
    }

    /**
     * 继续播放
     */
    public void resume() {
        if (isInPlaybackState()
                && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            setPlayState(STATE_PLAYING);
            setKeepScreenOn(true);
        }
    }

    /**
     * 停止播放
     */
    public void stopPlayback() {
        if (mProgressManager != null && isInPlaybackState())
            mProgressManager.saveProgress(mCurrentUrl, mCurrentPosition);
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            setPlayState(STATE_IDLE);
            setKeepScreenOn(false);
        }
        onPlayStopped();
    }

    /**
     * 释放播放器
     */
    public void release() {
        if (mProgressManager != null && isInPlaybackState())
            mProgressManager.saveProgress(mCurrentUrl, mCurrentPosition);
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            setPlayState(STATE_IDLE);
            setKeepScreenOn(false);
        }
        onPlayStopped();
    }

    private void onPlayStopped() {
        if (mVideoController != null) mVideoController.hideStatusView();
        mIsLockFullScreen = false;
        mCurrentPosition = 0;
    }

    /**
     * 监听播放状态变化, list的方式改用set 方式
     */
    public void addOnVideoViewStateChangeListener(@NonNull OnVideoViewStateChangeListener listener) {
        videoViewStateChangeListener = listener;
    }

    /**
     * 是否处于播放状态
     */
    protected boolean isInPlaybackState() {
        return (mMediaPlayer != null
                && mCurrentPlayState != STATE_ERROR
                && mCurrentPlayState != STATE_IDLE
                && mCurrentPlayState != STATE_PREPARING
                && mCurrentPlayState != STATE_PLAYBACK_COMPLETED);
    }

    /**
     * 获取视频总时长
     */
    @Override
    public long getDuration() {
        if (isInPlaybackState()) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 获取当前播放的位置
     */
    @Override
    public long getCurrentPosition() {
        if (isInPlaybackState()) {
            mCurrentPosition = mMediaPlayer.getCurrentPosition();
            return mCurrentPosition;
        }
        return 0;
    }

    /**
     * 调整播放进度
     */
    @Override
    public void seekTo(long pos) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(pos);
        }
    }

    /**
     * 是否处于播放状态
     */
    @Override
    public boolean isPlaying() {
        if (mMediaPlayer == null) return false;
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    /**
     * 获取当前缓冲百分比
     */
    @Override
    public int getBufferedPercentage() {
        return mMediaPlayer != null ? mMediaPlayer.getBufferedPercentage() : 0;
    }

    /**
     * 设置静音
     */
    @Override
    public void setMute(boolean isMute) {
        if (mMediaPlayer != null) {
            this.mIsMute = isMute;
            float volume = isMute ? 0.0f : 1.0f;
            mMediaPlayer.setVolume(volume, volume);
        }
    }

    /**
     * 是否处于静音状态
     */
    @Override
    public boolean isMute() {
        return mIsMute;
    }

    /**
     * 设置controller是否处于锁定状态
     */
    @Override
    public void setLock(boolean isLocked) {
        this.mIsLockFullScreen = isLocked;
    }

    /**
     * 视频播放出错回调
     */
    @Override
    public void onError() {
        setPlayState(STATE_ERROR);
    }

    /**
     * 视频播放完成回调
     */
    @Override
    public void onCompletion() {
        setPlayState(STATE_PLAYBACK_COMPLETED);
        setKeepScreenOn(false);
        mCurrentPosition = 0;
        if (mProgressManager != null) {
            //播放完成，清除进度
            mProgressManager.saveProgress(mCurrentUrl, 0);
        }
    }

    @Override
    public void onInfo(int what, int extra) {
        switch (what) {
            case AbstractPlayer.MEDIA_INFO_BUFFERING_START:
                setPlayState(STATE_BUFFERING);
                break;
            case AbstractPlayer.MEDIA_INFO_BUFFERING_END:
                setPlayState(STATE_BUFFERED);
                break;
            case AbstractPlayer.MEDIA_INFO_VIDEO_RENDERING_START: // 视频开始渲染
                setPlayState(STATE_PLAYING);
                if (getWindowVisibility() != VISIBLE) pause();
                break;
        }
    }

    /**
     * 视频缓冲完毕，准备开始播放时回调
     */
    @Override
    public void onPrepared() {
        setPlayState(STATE_PREPARED);
        if (mCurrentPosition > 0) {
            seekTo(mCurrentPosition);
        }
    }

    /**
     * 获取当前播放器的状态
     */
    public int getCurrentPlayerState() {
        return mCurrentPlayerState;
    }

    /**
     * 获取当前的播放状态
     */
    public int getCurrentPlayState() {
        return mCurrentPlayState;
    }


    /**
     * 获取缓冲速度
     */
    @Override
    public long getTcpSpeed() {
        return mMediaPlayer != null ? mMediaPlayer.getTcpSpeed() : 0;
    }

    /**
     * 设置播放速度
     */
    @Override
    public void setSpeed(float speed) {
        if (isInPlaybackState()) {
            mMediaPlayer.setSpeed(speed);
        }
    }

    /**
     * 设置视频地址
     */
    public void setUrl(String url) {
        this.mCurrentUrl = url;
    }

    /**
     * 一开始播放就seek到预先设置好的位置
     */
    public void skipPositionWhenPlay(int position) {
        this.mCurrentPosition = position;
    }

    /**
     * 设置音量 0.0f-1.0f 之间
     *
     * @param v1 左声道音量
     * @param v2 右声道音量
     */
    public void setVolume(float v1, float v2) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(v1, v2);
        }
    }

    /**
     * 设置进度管理器，用于保存播放进度
     */
    public void setProgressManager(@Nullable ProgressManager progressManager) {
        this.mProgressManager = progressManager;
    }

    /**
     * 循环播放， 默认不循环播放
     */
    public void setLooping(boolean looping) {
        mIsLooping = looping;
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(looping);
        }
    }

    /**
     * 添加到{@link VideoViewManager},如需集成到RecyclerView或ListView请开启此选项
     * 用于实现同一列表同时只播放一个视频
     */
    public void addToVideoViewManager() {
        mAddToVideoViewManager = true;
    }


    /**
     * 改变返回键逻辑，用于activity
     */
    public boolean onBackPressed() {
        return mVideoController != null && mVideoController.onBackPressed();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        //activity切到后台后可能被系统回收，故在此处进行进度保存
        if (mProgressManager != null && mCurrentPosition > 0) {
            L.d("saveProgress: " + mCurrentPosition);
            mProgressManager.saveProgress(mCurrentUrl, mCurrentPosition);
        }
        return super.onSaveInstanceState();
    }
}
