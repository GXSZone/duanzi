package com.dueeeke.videoplayer.playerui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.dueeeke.videoplayer.controller.GestureVideoController;
import com.dueeeke.videoplayer.listener.MyVideoOtherListener;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.util.L;
import com.dueeeke.videoplayer.util.PlayerUtils;

/**
 * 直播/点播控制器
 * Created by Devlin_n on 2017/4/7.
 */

public class StandardVideoController extends GestureVideoController implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    protected TextView mTotalTime, mCurrTime;
    protected ImageView mFullScreenButton;
    protected LinearLayout mBottomContainer;
    protected SeekBar mVideoProgress;
    protected ImageView mBackButton;
    protected ImageView mLockButton;
    private boolean mIsLive;
    private boolean mIsDragging;

    private ProgressBar mBottomProgress;
    private ImageView mPlayButton;
    private ImageView mStartPlayButton;
    private ProgressBar mLoadingProgress;
    private ImageView mThumb;
    private View mCompleteContainer;
    //    private TextView mSysTime;//系统当前时间
//    private ImageView mBatteryLevel;//电量
    private Animation mShowAnim = AnimationUtils.loadAnimation(getContext(), R.anim.dkplayer_anim_alpha_in);
    private Animation mHideAnim = AnimationUtils.loadAnimation(getContext(), R.anim.dkplayer_anim_alpha_out);
    //    private BatteryReceiver mBatteryReceiver;
    private TextView videoTime, playCount;
    private ImageView mMute;
    public View moreIv;
    /**
     * 全屏的时候是否是横屏
     */
    boolean isLand = false;

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
        mControllerView.findViewById(R.id.replay_text).setOnClickListener(this);
        mControllerView.findViewById(R.id.download_text).setOnClickListener(this);
//        contentTopic = mControllerView.findViewById(R.id.iv_content_topic);
//        contentTopic.setOnClickListener(this);
        mControllerView.findViewById(R.id.share_platform_weixin).setOnClickListener(this);
        mControllerView.findViewById(R.id.share_platform_qq).setOnClickListener(this);
        mControllerView.findViewById(R.id.share_platform_qyq).setOnClickListener(this);
        mControllerView.findViewById(R.id.share_platform_qqzone).setOnClickListener(this);

//        mSysTime = mControllerView.findViewById(R.id.sys_time);
//        mBatteryLevel = mControllerView.findViewById(R.id.iv_battery);
//        mBatteryReceiver = new BatteryReceiver(mBatteryLevel);
        mMute = mControllerView.findViewById(R.id.iv_mute);
        mMute.setOnClickListener(this);
        videoTime = mControllerView.findViewById(R.id.tv_video_time);
        playCount = mControllerView.findViewById(R.id.play_count);
        moreIv = mControllerView.findViewById(R.id.iv_more_action);
        moreIv.setOnClickListener(this);
    }

    public void setVideoInfo(String time, String play_count) {
        try {
            Log.i("videoInfo", "time:  " + time + "        play_count:   " + play_count);
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
// 电量广播
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        getContext().unregisterReceiver(mBatteryReceiver);
//    }
//
//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        getContext().registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
//    }

    public MyVideoOtherListener videoListener;

    public void setMyVideoOtherListener(MyVideoOtherListener myVideoOtherListener) {
        videoListener = myVideoOtherListener;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.replay_text) {
            replayAction();
        } else if (i == R.id.download_text) {
            if (videoListener != null) {
                videoListener.download();
            }
        } else if (i == R.id.share_platform_weixin) {
            if (videoListener != null) {
                videoListener.share(MyVideoOtherListener.weixin);
            }
        } else if (i == R.id.share_platform_qq) {
            if (videoListener != null) {
                videoListener.share(MyVideoOtherListener.qq);
            }
        } else if (i == R.id.share_platform_qyq) {
            if (videoListener != null) {
                videoListener.share(MyVideoOtherListener.qyq);
            }
        } else if (i == R.id.share_platform_qqzone) {
            if (videoListener != null) {
                videoListener.share(MyVideoOtherListener.qqzone);
            }
        } else if (i == R.id.iv_mute) {
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
        } else if (i == R.id.iv_play || i == R.id.thumb || i == R.id.start_play) {
            doPauseResume();
        }
    }


    @Override
    public void setPlayerState(int playerState) {
        switch (playerState) {
            case IjkVideoView.PLAYER_NORMAL:
                isLand = false;
                L.e("PLAYER_NORMAL");
                if (mIsLocked) return;
                setLayoutParams(new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                mIsGestureEnabled = false;
                mFullScreenButton.setSelected(false);
                mBackButton.setVisibility(View.GONE);
                mLockButton.setVisibility(View.GONE);
//                mSysTime.setVisibility(View.GONE);
//                mBatteryLevel.setVisibility(View.GONE);
//                mTopContainer.setVisibility(View.GONE);
                moreIv.setVisibility(GONE); //退出全屏的回调

                break;
            case IjkVideoView.PLAYER_FULL_SCREEN:
                L.e("PLAYER_FULL_SCREEN");

                if (mIsLocked) return;
                mIsGestureEnabled = true;
                mFullScreenButton.setSelected(true);
                mBackButton.setVisibility(View.VISIBLE);
//                mSysTime.setVisibility(View.VISIBLE);
//                mBatteryLevel.setVisibility(View.VISIBLE);
                if (mShowing) {
                    mLockButton.setVisibility(View.VISIBLE);
//                    mTopContainer.setVisibility(View.VISIBLE);
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
        if (mCurrentPlayState == IjkVideoView.STATE_BUFFERING) return;
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
        super.setPlayState(playState);
        switch (playState) {
            case IjkVideoView.STATE_IDLE:
                L.e("STATE_IDLE");
                hide();
                mIsLocked = false;
                mLockButton.setSelected(false);
                mMediaPlayer.setLock(false);
                mBottomProgress.setProgress(0);
                mBottomProgress.setSecondaryProgress(0);
                mVideoProgress.setProgress(0);
                mVideoProgress.setSecondaryProgress(0);
                mCompleteContainer.setVisibility(View.GONE);
                mBottomProgress.setVisibility(View.GONE);
                mLoadingProgress.setVisibility(View.GONE);
                mStartPlayButton.setVisibility(View.VISIBLE);

                mThumb.setVisibility(View.VISIBLE);
                if (mMute != null) {
                    mMute.setVisibility(GONE);
                }
                break;
            case IjkVideoView.STATE_PLAYING:
                L.e("STATE_PLAYING");
                post(mShowProgress);
                mPlayButton.setSelected(true);
                mLoadingProgress.setVisibility(View.GONE);
                mCompleteContainer.setVisibility(View.GONE);
                mThumb.setVisibility(View.GONE);
                mStartPlayButton.setVisibility(View.GONE);
                if (mMute != null) {
                    if (isMute) {
                        mMute.setSelected(true);
                    }
                    mMute.setVisibility(VISIBLE);
                }
                break;
            case IjkVideoView.STATE_PAUSED:
                L.e("STATE_PAUSED");
                mPlayButton.setSelected(false);
                mStartPlayButton.setVisibility(View.VISIBLE);
                break;
            case IjkVideoView.STATE_PREPARING:
                L.e("STATE_PREPARING");
                mCompleteContainer.setVisibility(View.GONE);
                mStartPlayButton.setVisibility(View.GONE);
                mLoadingProgress.setVisibility(View.VISIBLE);
//                mThumb.setVisibility(View.VISIBLE);
                break;
            case IjkVideoView.STATE_PREPARED:
                L.e("STATE_PREPARED");
                if (!mIsLive) mBottomProgress.setVisibility(View.VISIBLE);
//                mLoadingProgress.setVisibility(GONE);
                mStartPlayButton.setVisibility(View.GONE);
                // TODO: 2019-08-01 初始化就设置空指针,这个时机应该可以
                mMediaPlayer.setMute(isMute);
                break;
            case IjkVideoView.STATE_ERROR:
                L.e("STATE_ERROR");
                mStartPlayButton.setVisibility(View.GONE);
                mLoadingProgress.setVisibility(View.GONE);
                mThumb.setVisibility(View.GONE);
                mBottomProgress.setVisibility(View.GONE);
                if (mMute != null) {
                    mMute.setVisibility(GONE);
                }
//                mTopContainer.setVisibility(View.GONE);
                break;
            case IjkVideoView.STATE_BUFFERING:
                L.e("STATE_BUFFERING");
                mStartPlayButton.setVisibility(View.GONE);
                mLoadingProgress.setVisibility(View.VISIBLE);
                mThumb.setVisibility(View.GONE);
                mPlayButton.setSelected(mMediaPlayer.isPlaying());
                break;
            case IjkVideoView.STATE_BUFFERED:
                L.e("STATE_BUFFERED");
                mLoadingProgress.setVisibility(View.GONE);
                mStartPlayButton.setVisibility(View.GONE);
                mThumb.setVisibility(View.GONE);
                mPlayButton.setSelected(mMediaPlayer.isPlaying());
                break;
            case IjkVideoView.STATE_PLAYBACK_COMPLETED:
                L.e("STATE_PLAYBACK_COMPLETED");
                hide();
                removeCallbacks(mShowProgress);
                mStartPlayButton.setVisibility(View.GONE);
                mThumb.setVisibility(View.VISIBLE);
                mCompleteContainer.setVisibility(View.VISIBLE);
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
        videoTime.setVisibility(playState == IjkVideoView.STATE_IDLE ? VISIBLE : GONE);
        playCount.setVisibility(playState == IjkVideoView.STATE_IDLE ? VISIBLE : GONE);
    }

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

    /**
     * 设置是否为直播视频
     */
    public void setLive() {
        mIsLive = true;
        mBottomProgress.setVisibility(View.GONE);
        mVideoProgress.setVisibility(View.INVISIBLE);
        mTotalTime.setVisibility(View.INVISIBLE);
        mCurrTime.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mIsDragging = true;
        removeCallbacks(mShowProgress);
        removeCallbacks(mFadeOut);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
//        long duration = mMediaPlayer.getDuration();
//        long newPosition = (duration * seekBar.getProgress()) / mVideoProgress.getMax();
//        mMediaPlayer.seekTo((int) newPosition);
//        mIsDragging = false;
//        post(mShowProgress);
//        show();
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
                mBottomContainer.startAnimation(mHideAnim);
            }
            if (!mIsLive && !mIsLocked) {
                mBottomProgress.setVisibility(View.VISIBLE);
                mBottomProgress.startAnimation(mShowAnim);
            }
            mShowing = false;
        }
    }

    protected void hideAllViews() {
        mBackButton.setVisibility(GONE);

        mBottomContainer.setVisibility(View.GONE);
        mBottomContainer.startAnimation(mHideAnim);
        if (!mMediaPlayer.isFullScreen()) return; //只有全屏状态下才有这个更多按钮的显示隐藏
        if (isMySelf || moreIv == null) return;
        if (mCurrentPlayState == IjkVideoView.STATE_PLAYBACK_COMPLETED) {
            moreIv.setVisibility(VISIBLE);
        } else {
            moreIv.setVisibility(GONE);
        }
    }

    private void show(int timeout) {
        if (!mShowing) {
            if (mMediaPlayer.isFullScreen()) {
                mLockButton.setVisibility(View.VISIBLE);
                if (!mIsLocked) {
                    showAllViews();
                }
            } else {
                mBottomContainer.setVisibility(View.VISIBLE);
                mBottomContainer.startAnimation(mShowAnim);
            }
            if (!mIsLocked && !mIsLive) {
                mBottomProgress.setVisibility(View.GONE);
                mBottomProgress.startAnimation(mHideAnim);
            }
            mShowing = true;
        }
        removeCallbacks(mFadeOut);
        if (timeout != 0) {
            postDelayed(mFadeOut, timeout);
        }
    }

    protected void showAllViews() {
        mBottomContainer.setVisibility(View.VISIBLE);
        mBottomContainer.startAnimation(mShowAnim);
        mBackButton.setVisibility(VISIBLE);
        if (!mMediaPlayer.isFullScreen() || isLand || isMySelf || moreIv == null)
            return; //只有全屏状态下才有这个更多按钮的显示隐藏
        moreIv.setVisibility(VISIBLE);
    }

    @Override
    public void show() {
        show(mDefaultTimeout);
    }

    @Override
    protected int setProgress() {
        if (mMediaPlayer == null || mIsDragging) {
            return 0;
        }

        if (mIsLive) return 0;

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
