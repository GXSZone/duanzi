package com.dueeeke.videoplayer.controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.R;
import com.dueeeke.videoplayer.listener.MyVideoOtherListener;
import com.dueeeke.videoplayer.player.DKVideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;
import com.dueeeke.videoplayer.widget.CompleteView;

/**
 * 直播/点播控制器
 * Created by Devlin_n on 2017/4/7.
 */

public class StandardVideoController extends GestureVideoController implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    protected TextView mTotalTime, mCurrTime, videoTime, playCount;
    protected LinearLayout mBottomContainer;
    protected SeekBar mVideoProgress;
    protected ImageView mPlayButton, mBackButton, mLockButton, mFullScreenButton, mStartPlayButton, mThumb, mMute;
    private boolean mIsDragging;
    private ProgressBar mBottomProgress, mLoadingProgress;
    private CompleteView mCompleteContainer;
    public View moreIv;
    /**
     * 全屏的时候是否是横屏
     */
    public boolean isLand = false;

    public StandardVideoController(@NonNull Context context) {
        this(context, null);
    }

    public StandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 这个是新布局,原先布局先不删
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.dkplayer_layout_standard_controller;
    }

    @Override
    protected void initView() {
        super.initView();
        mFullScreenButton = mControllerView.findViewById(R.id.fullscreen);
        mFullScreenButton.setOnClickListener(this);
        mBottomContainer = mControllerView.findViewById(R.id.bottom_container);
//        mTopContainer = mControllerView.findViewById(R.id.top_container);
        mVideoProgress = mControllerView.findViewById(R.id.seekBar);
        mVideoProgress.setOnSeekBarChangeListener(this);
        mTotalTime = mControllerView.findViewById(R.id.total_time);
        mCurrTime = mControllerView.findViewById(R.id.curr_time);
        mBackButton = mControllerView.findViewById(R.id.back);
        mBackButton.setOnClickListener(this);
        mLockButton = mControllerView.findViewById(R.id.lock);
        mLockButton.setOnClickListener(this);
        mThumb = mControllerView.findViewById(R.id.thumb);
        mThumb.setOnClickListener(this);
        mPlayButton = mControllerView.findViewById(R.id.iv_play);
        mPlayButton.setOnClickListener(this);

        mStartPlayButton = mControllerView.findViewById(R.id.start_play);
        mStartPlayButton.setOnClickListener(this);
        mLoadingProgress = mControllerView.findViewById(R.id.loading);
        mBottomProgress = mControllerView.findViewById(R.id.bottom_progress);
        //自定义分享布局
        mCompleteContainer = mControllerView.findViewById(R.id.complete_container);
        mCompleteContainer.bindController(this);

        mMute = mControllerView.findViewById(R.id.iv_mute);
        mMute.setOnClickListener(this);
        videoTime = mControllerView.findViewById(R.id.tv_video_time);
        playCount = mControllerView.findViewById(R.id.play_count);
        moreIv = mControllerView.findViewById(R.id.iv_more_action);
        moreIv.setOnClickListener(this);
    }

    /**
     * 为了所谓狗屎的播放次数本地更新
     */
    String playNum;
    String videoDuration;

    public void setPlayNum(String playNum) {
        this.playNum = playNum;
    }

    public void setVideoInfo(String time, String play_count) {
        try {
            playNum = play_count;
            videoDuration = time;
            videoTime.setVisibility(TextUtils.isEmpty(time) ? GONE : VISIBLE);
            playCount.setVisibility(TextUtils.isEmpty(play_count) ? GONE : VISIBLE);

            if (!TextUtils.isEmpty(time)) {
                videoTime.setText(formatSecondTime(Integer.parseInt(time)));
            }
            if (!TextUtils.isEmpty(play_count)) {
                playCount.setText(String.format("%s播放", playCountText(Integer.parseInt(play_count), "W")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String playCountText(int number, String company) {
        if (number < 10000) {
            return number + "";
        } else {
            int round = number / 10000;
            int decimal = number % 10000;
            decimal = decimal / 1000;
            return round + "." + decimal + "" + company;
        }
    }

    public MyVideoOtherListener videoListener;

    public void setMyVideoOtherListener(MyVideoOtherListener myVideoOtherListener) {
        videoListener = myVideoOtherListener;
    }

    public MyVideoOtherListener getVideoListener() {
        return videoListener;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_mute) {
            toggleMute();
        } else if (i == R.id.iv_more_action) {
            if (videoListener != null) {
                // TODO: 2019-07-30 这里只是为了省事,少写一个接口,直接用父类的,反正也不用现在
                videoListener.clickTopic();
            }
        } else {
            videoNormalClick(i);
        }
    }

    /**
     * 播放器控制的更多操作,都有外部控制
     */
    public boolean isMySelf;

    public void setIsMySelf(boolean isMySelf) {
        this.isMySelf = isMySelf;
        if (moreIv != null) {
            moreIv.setVisibility(GONE);
        }
    }

    //全局记录
    static boolean isMute = false;

    private void toggleMute() {
        isMute = !isMute;
        mMediaPlayer.setMute(isMute);
        mMute.setSelected(isMute);
        if (videoListener != null) {
            videoListener.mute();
        }
    }

    /**
     * 这样子类可以复写做其他操作
     */
    public void replayAction() {
        mMediaPlayer.replay(true);
    }

    public void videoNormalClick(int i) {
        if (i == R.id.fullscreen || i == R.id.back) {
            doStartStopFullScreen();
        } else if (i == R.id.lock) {
            doLockUnlock();
        } else if ((i == R.id.thumb && mThumb.getAlpha() == 1.0f) || i == R.id.start_play || i == R.id.iv_play) {
            doPauseResume();
        }
    }


    @Override
    public void setPlayerState(int playerState) {
        switch (playerState) {
            case DKVideoView.PLAYER_NORMAL:
                isLand = false;
                if (mIsLocked) return;
                setLayoutParams(new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                mIsGestureEnabled = false;
                mFullScreenButton.setSelected(false);
                mBackButton.setVisibility(View.GONE);
                mLockButton.setVisibility(View.GONE);

                moreIv.setVisibility(GONE); //退出全屏的回调

                break;
            case DKVideoView.PLAYER_FULL_SCREEN:
                if (mIsLocked) return;
                mIsGestureEnabled = true;
                mFullScreenButton.setSelected(true);
                mBackButton.setVisibility(View.VISIBLE);
                if (mShowing) {
                    mLockButton.setVisibility(View.VISIBLE);
                } else {
                    mLockButton.setVisibility(View.GONE);
                }

                Activity activity = PlayerUtils.scanForActivity(getContext());
                isLand = activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                if (!isMySelf && !isLand) {
                    moreIv.setVisibility(VISIBLE);
                } else {
                    moreIv.setVisibility(GONE);
                }
                break;
        }
    }

    /**
     * 重写只是为了埋点
     */
    protected void doPauseResume() {
        if (mCurrentPlayState == DKVideoView.STATE_BUFFERING) return;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
            if (videoListener != null) {
                videoListener.clickPlay();
            }
        }
    }

    @Override
    public void setPlayState(int playState) {
//        Log.i("StandardVideoController", "setPlayState: " + playState);
        super.setPlayState(playState);
        switch (playState) {
            case DKVideoView.STATE_IDLE:
                hide();
                mIsLocked = false;
                mLockButton.setSelected(false);
                mMediaPlayer.setLock(false);
                mBottomProgress.setProgress(0);
                mBottomProgress.setSecondaryProgress(0);
                mVideoProgress.setProgress(0);
                mVideoProgress.setSecondaryProgress(0);
                mBottomProgress.setVisibility(View.GONE);
                mLoadingProgress.setVisibility(View.GONE);
                mStartPlayButton.setVisibility(View.VISIBLE);
                mThumb.animate().cancel();
                mThumb.setAlpha(1.0f);
                mThumb.setVisibility(View.VISIBLE);
                if (mMute != null) {
                    mMute.setVisibility(GONE);
                }
                break;
            case DKVideoView.STATE_PLAYING:
                post(mShowProgress);
                mPlayButton.setSelected(true);
//                mLoadingProgress.setVisibility(View.GONE);
                mThumb.animate().setDuration(800).alpha(0f).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mThumb.setVisibility(GONE);
                    }
                });
                mStartPlayButton.setVisibility(View.GONE);
                if (mMute != null) {
                    if (isMute) {
                        mMute.setSelected(true);
                    }
                    mMute.setVisibility(VISIBLE);
                }
                break;
            case DKVideoView.STATE_PAUSED:
                mPlayButton.setSelected(false);
                mStartPlayButton.setVisibility(View.VISIBLE);
                break;
            case DKVideoView.STATE_PREPARING:
                mPlayButton.setSelected(mMediaPlayer.isPlaying());
                mStartPlayButton.setVisibility(View.GONE);
//                mLoadingProgress.setVisibility(View.VISIBLE);
//                mThumb.setVisibility(View.VISIBLE);
                break;
            case DKVideoView.STATE_PREPARED:
                mPlayButton.setSelected(mMediaPlayer.isPlaying());
                mStartPlayButton.setVisibility(View.GONE);
                mMediaPlayer.setMute(isMute);
                break;
            case DKVideoView.STATE_ERROR:
                mStartPlayButton.setVisibility(View.GONE);
                mLoadingProgress.setVisibility(View.GONE);
                mThumb.setVisibility(View.GONE);
                mBottomProgress.setVisibility(View.GONE);
                if (mMute != null) {
                    mMute.setVisibility(GONE);
                }
                break;
            case DKVideoView.STATE_BUFFERING:
                mStartPlayButton.setVisibility(View.GONE);
                isLoad = true;
                if (!isAnim) {
                    mThumb.animate().alpha(0f).setDuration(800).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mThumb.setVisibility(GONE);
                            isAnim = false;
                            if (isLand) {
                                mLoadingProgress.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onAnimationStart(Animator animation) {
                            isAnim = true;
                        }
                    });
                }
                break;
            case DKVideoView.STATE_BUFFERED:
                isLoad = false;
                mLoadingProgress.setVisibility(View.GONE);
                break;
            case DKVideoView.STATE_PLAYBACK_COMPLETED:
                hide();
                removeCallbacks(mShowProgress);
                mStartPlayButton.setVisibility(View.GONE);
                mThumb.setAlpha(1.0f);
                mThumb.setVisibility(View.VISIBLE);
                if (mMediaPlayer.isFullScreen()) {
                    mBackButton.setVisibility(VISIBLE);
                }
                mBottomProgress.setProgress(0);
                mBottomProgress.setSecondaryProgress(0);
                mIsLocked = false;
                mMediaPlayer.setLock(false);
                if (mMute != null) {
                    mMute.setVisibility(GONE);
                }
                break;
        }
        mCompleteContainer.onPlayStateChanged(playState);
        videoTime.setVisibility(playState == DKVideoView.STATE_IDLE
                && !TextUtils.isEmpty(videoDuration) ? VISIBLE : GONE);
        playCount.setVisibility(playState == DKVideoView.STATE_IDLE
                && !TextUtils.isEmpty(playNum) ? VISIBLE : GONE);
        if (playCount.getVisibility() == VISIBLE) {
            playCount.setText(String.format("%s播放", playCountText(Integer.parseInt(playNum), "W")));
        }
    }

    boolean isAnim = false;
    boolean isLoad = false;

    protected void doLockUnlock() {
        if (mIsLocked) {
            mIsLocked = false;
            mShowing = false;
            mIsGestureEnabled = true;
            show();
            mLockButton.setSelected(false);
            Toast.makeText(getContext(), R.string.dkplayer_unlocked, Toast.LENGTH_SHORT).show();
        } else {
            hide();
            mIsLocked = true;
            mIsGestureEnabled = false;
            mLockButton.setSelected(true);
            Toast.makeText(getContext(), R.string.dkplayer_locked, Toast.LENGTH_SHORT).show();
        }
        mMediaPlayer.setLock(mIsLocked);
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mIsDragging = true;
        removeCallbacks(mShowProgress);
        removeCallbacks(mFadeOut);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        long duration = mMediaPlayer.getDuration();
        int progress = seekBar.getProgress();
        if (progress == 1000) progress = 990;
        long newPosition = (duration * progress) / mVideoProgress.getMax();
        mMediaPlayer.seekTo((int) newPosition);
        mIsDragging = false;
        post(mShowProgress);
        show();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // TODO: 2019-05-09 这里获取进度
        long duration = mMediaPlayer.getDuration();
        long newPosition = (duration * progress) / mVideoProgress.getMax();
        if (newPosition > 50000 && videoListener != null) {
            videoListener.timeToShowWxIcon();
        }
        if (!fromUser) {
            return;
        }
        if (mCurrTime != null) {
            mCurrTime.setText(stringForTime((int) newPosition));
        }
    }

    @Override
    public void hide() {
        if (mShowing) {
            if (mMediaPlayer.isFullScreen()) {
                mLockButton.setVisibility(View.GONE);
                if (!mIsLocked) {
                    hideAllViews();
                }
            } else {
                mBottomContainer.setVisibility(View.GONE);
            }
            if (!mIsLocked) {
                mBottomProgress.setVisibility(VISIBLE);
            }
            mShowing = false;
        }
    }

    protected void hideAllViews() {
        mBackButton.setVisibility(GONE);
        mBottomContainer.setVisibility(View.GONE);
        if (!mMediaPlayer.isFullScreen()) return; //只有全屏状态下才有这个更多按钮的显示隐藏
        if (isMySelf || moreIv == null) return;
        if (mCurrentPlayState == DKVideoView.STATE_PLAYBACK_COMPLETED) {
            moreIv.setVisibility(VISIBLE);
        } else {
            moreIv.setVisibility(GONE);
        }
    }

    protected void showAllViews() {
        mBottomContainer.setVisibility(VISIBLE);
        mBackButton.setVisibility(VISIBLE);
        if (!mMediaPlayer.isFullScreen() || isLand || isMySelf || moreIv == null)
            return; //只有全屏状态下才有这个更多按钮的显示隐藏
        moreIv.setVisibility(VISIBLE);
    }

    @Override
    public void show() {
        if (!mShowing) {
            if (mMediaPlayer.isFullScreen()) {
                mLockButton.setVisibility(View.VISIBLE);
                if (!mIsLocked) {
                    showAllViews();
                }
            } else {
                mBottomContainer.setVisibility(VISIBLE);
            }
            if (!mIsLocked) {
                mBottomProgress.setVisibility(GONE);
            }
            mShowing = true;
        }
        removeCallbacks(mFadeOut);
        if (mDefaultTimeout != 0) {
            postDelayed(mFadeOut, mDefaultTimeout);
        }
    }

    @Override
    protected int setProgress() {
        if (mMediaPlayer == null || mIsDragging) {
            return 0;
        }

        int position = (int) mMediaPlayer.getCurrentPosition();
        int duration = (int) mMediaPlayer.getDuration();
        if (mVideoProgress != null) {
            if (duration > 0) {
                mVideoProgress.setEnabled(true);
                int pos = (int) (position * 1.0 / duration * mVideoProgress.getMax());
                mVideoProgress.setProgress(pos);
                mBottomProgress.setProgress(pos);
            } else {
                mVideoProgress.setEnabled(false);
            }
            int percent = mMediaPlayer.getBufferedPercentage();
            if (percent >= 95) { //解决缓冲进度不能100%问题
                mVideoProgress.setSecondaryProgress(mVideoProgress.getMax());
                mBottomProgress.setSecondaryProgress(mBottomProgress.getMax());
            } else {
                mVideoProgress.setSecondaryProgress(percent * 10);
                mBottomProgress.setSecondaryProgress(percent * 10);
            }
        }

        if (mTotalTime != null)
            mTotalTime.setText(stringForTime(duration));
        if (mCurrTime != null)
            mCurrTime.setText(stringForTime(position));

        return position;
    }

    /**
     * 全屏是横屏的时候左右滑动是进度,如果是竖屏的全屏则左右滑动是退出全屏
     *
     * @param deltaX
     */
    @Override
    protected void slideToChangePosition(float deltaX) {
        //新需求全屏去掉了进度,变成退出全屏,做侧滑返回的话还得添加动画
        if (Math.abs(deltaX) > 350 && !isLand) {
            doStartStopFullScreen();
        } else if (isLand) {
            super.slideToChangePosition(deltaX);
        }
    }

    public ImageView getThumb() {
        return mThumb;
    }

    @Override
    public boolean onBackPressed() {
        if (mIsLocked) {
            show();
            Toast.makeText(getContext(), R.string.dkplayer_lock_tip, Toast.LENGTH_SHORT).show();
            return true;
        }

        Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity == null) return super.onBackPressed();

        if (mMediaPlayer.isFullScreen()) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mMediaPlayer.stopFullScreen();
            return true;
        }
        return super.onBackPressed();
    }
}
