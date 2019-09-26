package com.dueeeke.videoplayer.player;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.dueeeke.videoplayer.util.ThreadPoolUtils;

import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IjkPlayer extends AbstractPlayer {

    protected IjkMediaPlayer mMediaPlayer;
    private boolean mIsLooping;
    private boolean mIsEnableMediaCodec;
    protected Context mAppContext;
    private int mBufferedPercent;

    public IjkPlayer(Context context) {
        mAppContext = context.getApplicationContext();
    }

    @Override
    public void initPlayer() {
        mMediaPlayer = new IjkMediaPlayer();
        setOptions();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnErrorListener(onErrorListener);
        mMediaPlayer.setOnCompletionListener(onCompletionListener);
        mMediaPlayer.setOnInfoListener(onInfoListener);
        mMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
        mMediaPlayer.setOnPreparedListener(onPreparedListener);
        mMediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        mMediaPlayer.setOnNativeInvokeListener(new IjkMediaPlayer.OnNativeInvokeListener() {
            @Override
            public boolean onNativeInvoke(int i, Bundle bundle) {
                return true;
            }
        });
    }

    @Override
    public void setOptions() {
       /*
      清空DNS,有时因为在APP里面要播放多种类型的视频(如:MP4,直播,直播平台保存的视频,和其他http视频),
      有时会造成因为DNS的问题而报10000问题的.
        */
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
        /*
        某些视频在SeekTo的时候，会跳回到拖动前的位置，这是因为视频的关键帧的问题，
        通俗一点就是FFMPEG不兼容，视频压缩过于厉害，
        seek只支持关键帧，出现这个情况就是原始的视频文件中i 帧比较少
        2019-07-08 这个精准进度会影响续播功能所以没放开
         */
//        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);

        //播放前的探测Size，默认是1M, 改小一点会出画面更快
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 200);
        //设置播放前的探测时间 1,达到首屏秒开效果
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1);
    }

    @Override
    public void setDataSource(String path, Map<String, String> headers) {
        try {
            Uri uri = Uri.parse(path);
            if (ContentResolver.SCHEME_ANDROID_RESOURCE.equals(uri.getScheme())) {
                RawDataSourceProvider rawDataSourceProvider = RawDataSourceProvider.create(mAppContext, uri);
                mMediaPlayer.setDataSource(rawDataSourceProvider);
            } else {
                mMediaPlayer.setDataSource(mAppContext, uri, headers);
            }

        } catch (Exception e) {
            mPlayerEventListener.onError();
        }
    }

    @Override
    public void setDataSource(AssetFileDescriptor fd) {
        try {
            mMediaPlayer.setDataSource(fd.getFileDescriptor());
        } catch (Exception e) {
            mPlayerEventListener.onError();
        }
    }

    @Override
    public void pause() {
        try {
            mMediaPlayer.pause();
        } catch (IllegalStateException e) {
            mPlayerEventListener.onError();
        }
    }

    @Override
    public void start() {
        try {
            mMediaPlayer.start();
        } catch (IllegalStateException e) {
            mPlayerEventListener.onError();
        }
    }

    @Override
    public void stop() {
        try {
            mMediaPlayer.stop();
        } catch (IllegalStateException e) {
            mPlayerEventListener.onError();
        }
    }

    @Override
    public void prepareAsync() {
        try {
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            mPlayerEventListener.onError();
        }
    }

    @Override
    public void reset() {
        mMediaPlayer.reset();
        mMediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
        mMediaPlayer.setLooping(mIsLooping);
        setOptions();
        setEnableMediaCodec(mIsEnableMediaCodec);
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(long time) {
        try {
            mMediaPlayer.seekTo((int) time);
        } catch (IllegalStateException e) {
            mPlayerEventListener.onError();
        }
    }

    /**
     * 线上有ANR问题,用线程池还好一点
     */
    @Override
    public void release() {
        ThreadPoolUtils.executor(new Runnable() {
            @Override
            public void run() {
                try {
                    mMediaPlayer.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public long getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public int getBufferedPercentage() {
        return mBufferedPercent;
    }

    @Override
    public void setSurface(Surface surface) {
        mMediaPlayer.setSurface(surface);
    }

    @Override
    public void setDisplay(SurfaceHolder holder) {
        mMediaPlayer.setDisplay(holder);
    }

    @Override
    public void setVolume(float v1, float v2) {
        mMediaPlayer.setVolume(v1, v2);
    }

    @Override
    public void setLooping(boolean isLooping) {
        this.mIsLooping = isLooping;
        mMediaPlayer.setLooping(isLooping);
    }

    /**
     * 硬解码这东西需要看效果再调
     * @param isEnable
     */
    @Override
    public void setEnableMediaCodec(boolean isEnable) {
        mIsEnableMediaCodec = isEnable;
        int value = isEnable ? 1 : 0;
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", value);//开启硬解码
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", value);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", value);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-hevc", value);//开启hevc硬解
    }

    @Override
    public void setSpeed(float speed) {
        mMediaPlayer.setSpeed(speed);
    }

    @Override
    public long getTcpSpeed() {
        return mMediaPlayer.getTcpSpeed();
    }

    private IMediaPlayer.OnErrorListener onErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int framework_err, int impl_err) {
            mPlayerEventListener.onError();
            return true;
        }
    };

    private IMediaPlayer.OnCompletionListener onCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            mPlayerEventListener.onCompletion();
        }
    };

    private IMediaPlayer.OnInfoListener onInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
            mPlayerEventListener.onInfo(what, extra);
            return true;
        }
    };

    private IMediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
            mBufferedPercent = percent;
        }
    };


    private IMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            mPlayerEventListener.onPrepared();
        }
    };

    private IMediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
            int videoWidth = iMediaPlayer.getVideoWidth();
            int videoHeight = iMediaPlayer.getVideoHeight();
            if (videoWidth != 0 && videoHeight != 0) {
                mPlayerEventListener.onVideoSizeChanged(videoWidth, videoHeight);
            }
        }
    };
}
