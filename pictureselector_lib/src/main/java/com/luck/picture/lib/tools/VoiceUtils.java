package com.luck.picture.lib.tools;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.luck.picture.lib.R;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.tool
 * email：893855882@qq.com
 * data：2017/5/25
 */

public class VoiceUtils {
    private static SoundPool soundPool;
    private static int soundID;//创建某个声音对应的音频ID
    private boolean isPlay;

    /**
     * start SoundPool
     */
    public static void playVoice(Context mContext, final boolean enableVoice) {

        if (soundPool == null) {
            soundPool = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
            soundID = soundPool.load(mContext, R.raw.music, 1);
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    play(enableVoice, soundPool);
                }
            });
        } else {
            play(enableVoice, soundPool);
        }
    }

    static MediaPlayer mMediaPlayer;

    public static void playVoice(Context mContext) {
        //直接创建，不需要设置setDataSource
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(mContext, R.raw.happy);
        }
        mMediaPlayer.start();
//        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                mp.release();
//            }
//        });
//        if (soundPool == null) {
//            soundPool = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
//            soundID = soundPool.load(mContext, R.raw.happy, 1);
//            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
//                @Override
//                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
//                    play(true, soundPool);
//                }
//            });
//        } else {
//            play(true, soundPool);
//        }
    }

    public static void play(boolean enableVoice, SoundPool soundPool) {
        if (enableVoice) {
            soundPool.play(
                    soundID,
                    0.1f,
                    0.5f,
                    0,
                    0,
                    1
            );
        }
    }

    /**
     * release SoundPool
     */
    public static void release() {
        if (soundPool != null) {
            soundPool.stop(soundID);
        }
        soundPool = null;
    }
}
