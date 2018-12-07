package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.lang.reflect.Constructor;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZMediaManager;
import cn.jzvd.JZUserAction;
import cn.jzvd.JZUserActionStd;
import cn.jzvd.JZUtils;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdMgr;
import cn.jzvd.JzvdStd;

/**
 * Created by Nathen
 * On 2016/04/18 16:15
 */
public class MyVideoPlayerStandard extends JzvdStd {

    private LinearLayout shareLayout;
    private TextView playCountText;
    private TextView videoTime;
    private TextView tinyReplay;

    public MyVideoPlayerStandard(Context context) {
        super(context);
    }

    public MyVideoPlayerStandard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_my_video_player;
    }

    @Override
    public void init(Context context) {
        super.init(context);
        shareLayout = findViewById(R.id.video_share_parent);
        findViewById(R.id.share_platform_friends_tv).setOnClickListener(this);
        findViewById(R.id.share_platform_weixin_tv).setOnClickListener(this);
        findViewById(R.id.share_platform_qq_tv).setOnClickListener(this);
        findViewById(R.id.share_platform_weibo_tv).setOnClickListener(this);
        playCountText = findViewById(R.id.play_count);
        Jzvd.setJzUserAction(new MyUserActionStd());
        replayTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    Log.i("autoPlay", "重播");
                    mListener.playStart();
                }
                startButton.performClick();
            }
        });
        videoTime = findViewById(R.id.tv_video_time);
    }

    /**
     * 设置封面图片
     *
     * @param imageUrl
     */
    public void setThumbImage(String imageUrl) {
        Glide.with(getContext()).asBitmap().load(imageUrl).into(thumbImageView);
    }

    /**
     * 设置横竖屏幕
     *
     * @param portrait
     */
    public void setOrientation(boolean portrait) {
        if (portrait) {
            //横屏
            Jzvd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        } else {
            Jzvd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
        Jzvd.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    public int mPlayCount;

    /**
     * 设置播放次数
     *
     * @param playCount
     */
    public void setPlayCount(int playCount) {
        mPlayCount = playCount;
        if (playCountText != null) {
            playCountText.setVisibility(VISIBLE);
            playCountText.setText(Int2TextUtils.toText(playCount, "W") + "播放");
        }
    }

    /**
     * 接口目前设置的秒数的字符串
     *
     * @param time
     */
    public void setVideoTime(String time) {
        if (TextUtils.isEmpty(time)) return;
        if (videoTime != null) {
            try {
                videoTime.setVisibility(VISIBLE);
                long duration = Integer.parseInt(time) * 1000;
                videoTime.setText(JZUtils.stringForTime(duration));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 更换播放和暂停的按钮UI,参考JzvdStd
     */
    public void updateStartImage() {
        Log.i("hashcode", "updateUI_startWindowTiny " + " [" + this.hashCode() + "] ");
        if (currentState == CURRENT_STATE_PLAYING) {
            startButton.setVisibility(VISIBLE);
            startButton.setImageResource(R.mipmap.play_stop);
            replayTextView.setVisibility(INVISIBLE);
            shareLayout.setVisibility(GONE);

        } else if (currentState == CURRENT_STATE_ERROR) {
            startButton.setVisibility(INVISIBLE);
            replayTextView.setVisibility(INVISIBLE);
            shareLayout.setVisibility(GONE);
            playCountText.setVisibility(GONE);
            videoTime.setVisibility(GONE);
        } else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
            startButton.setVisibility(GONE);
//            startButton.setImageResource(R.drawable.jz_click_replay_selector);
            replayTextView.setVisibility(VISIBLE);
            shareLayout.setVisibility(VISIBLE);
            playCountText.setVisibility(GONE);
            videoTime.setVisibility(GONE);
        } else {
            startButton.setImageResource(R.mipmap.play_start);
            replayTextView.setVisibility(INVISIBLE);
            shareLayout.setVisibility(GONE);
            playCountText.setVisibility(VISIBLE);
            videoTime.setVisibility(VISIBLE);
        }

        //单独写一套逻辑
        if (currentState == CURRENT_STATE_NORMAL) {
            playCountText.setVisibility(VISIBLE);
            videoTime.setVisibility(VISIBLE);
        } else {
            playCountText.setVisibility(GONE);
            videoTime.setVisibility(GONE);
        }
        //分享的显示逻辑
        if (currentState == CURRENT_STATE_AUTO_COMPLETE && currentScreen != SCREEN_WINDOW_TINY) {
            shareLayout.setVisibility(VISIBLE);
        } else {
            shareLayout.setVisibility(GONE);
        }


    }

    CompleteShareListener mListener;

    public void setOnShareBtListener(CompleteShareListener listener) {
        mListener = listener;
    }

    public interface CompleteShareListener {
        //只回调分享的平台
        void share(SHARE_MEDIA share_media);

        //主要是有请求接口统计次数的请求
        void playStart();
    }

    /**
     * @param v
     */
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.share_platform_friends_tv:
                if (mListener != null) {
                    mListener.share(SHARE_MEDIA.WEIXIN_CIRCLE);
                }
                break;
            case R.id.share_platform_weixin_tv:
                if (mListener != null) {
                    mListener.share(SHARE_MEDIA.WEIXIN);
                }
                break;
            case R.id.share_platform_qq_tv:
                if (mListener != null) {
                    mListener.share(SHARE_MEDIA.QQ);
                }
                break;
            case R.id.share_platform_weibo_tv:
                if (mListener != null) {
                    mListener.share(SHARE_MEDIA.SINA);
                }
                break;
        }
    }

    @Override
    public void startWindowFullscreen() {
        playCountText.setVisibility(GONE);
        videoTime.setVisibility(GONE);
        super.startWindowFullscreen();
    }


    /**
     * 头布局自动播放的逻辑,可以加wifi自动播放开关的逻辑
     */
    public void autoPlay() {
        startButton.performClick();
    }

    /**
     * 设置播放地址和标题
     *
     * @param videoUrl
     * @param titleText
     */
    public void setVideoUrl(String videoUrl, String titleText, boolean isListVideo) {

        if (videoUrl == null) {
            ToastUtil.showShort("播放地址获取失败");
            return;
        }
        //域名替换  使用代理
        if (videoUrl.contains("cos.ap-shanghai.myqcloud")) {
            videoUrl = MyApplication.buildFileUrl(videoUrl);
        }
        //使用缓存,替代成本地地址,续播问题可能就是这个问题
        if (videoUrl.startsWith("https://") || videoUrl.startsWith("http://")) {
            videoUrl = MyApplication.getInstance().getProxy().getProxyUrl(videoUrl);
        }
        setUp(videoUrl, titleText, isListVideo ? Jzvd.SCREEN_WINDOW_LIST : Jzvd.SCREEN_WINDOW_NORMAL);
    }


    /**
     * 这只是给埋点统计用户数据用的，不能写和播放相关的逻辑，监听事件请参考MyJzvdStd，复写函数取得相应事件
     */
    class MyUserActionStd implements JZUserActionStd {

        @Override
        public void onEvent(int type, Object url, int screen, Object... objects) {
            switch (type) {
                //这个才是手动点击播放的回调,播放次数统计分开
                case JZUserAction.ON_CLICK_START_ICON:
                case JZUserAction.ON_CLICK_RESUME:
                    if (mListener != null) {
                        Log.i("autoPlay", "手动点击播放");
                        mListener.playStart();
                    }
                    break;
                //开启悬浮窗播放模式
//                case JZUserAction.ON_ENTER_TINYSCREEN:
//                    shareLayout.setVisibility(GONE);
//                    break;
                case JZUserAction.ON_QUIT_TINYSCREEN:
                    if (JzvdMgr.getFirstFloor() != null) {
                        MyVideoPlayerStandard videoPlayerStandard = (MyVideoPlayerStandard) JzvdMgr.getFirstFloor();
                        videoPlayerStandard.playCountText.setVisibility(GONE);
                        videoPlayerStandard.videoTime.setVisibility(GONE);
                        videoPlayerStandard.tinyReplay.setVisibility(GONE);
                    }
//
                    break;
//                case JZUserAction.ON_ENTER_FULLSCREEN:
//                    playCountText.setVisibility(GONE);
                default:
                    Log.i("USER_EVENT", "unknow");
                    break;
            }
        }
    }

    public void startWindowTiny(boolean isLand) {
        Log.i("hashcode", "before_startWindowTiny " + " [" + this.hashCode() + "] ");
        onEvent(JZUserAction.ON_ENTER_TINYSCREEN);
        if (currentState == CURRENT_STATE_NORMAL || currentState == CURRENT_STATE_ERROR || currentState == CURRENT_STATE_AUTO_COMPLETE)
            return;
        ViewGroup vp = (JZUtils.scanForActivity(getContext()))//.getWindow().getDecorView();
                .findViewById(Window.ID_ANDROID_CONTENT);
        View old = vp.findViewById(R.id.jz_tiny_id);
        if (old != null) {
            vp.removeView(old);
        }
        textureViewContainer.removeView(JZMediaManager.textureView);

        try {
            Constructor<MyVideoPlayerStandard> constructor = (Constructor<MyVideoPlayerStandard>) MyVideoPlayerStandard.this.getClass().getConstructor(Context.class);
            MyVideoPlayerStandard jzvd = constructor.newInstance(getContext());
            Log.i("hashcode", "startWindowTiny " + " [" + jzvd.hashCode() + "] ");
            jzvd.setId(R.id.jz_tiny_id);
            int width;
            int height;
            if (isLand) {
                width = DevicesUtils.dp2px(160);
                height = DevicesUtils.dp2px(90);
            } else {
                width = DevicesUtils.dp2px(100);
                height = DevicesUtils.dp2px(160);
            }
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
            lp.gravity = Gravity.RIGHT | Gravity.TOP;
            vp.addView(jzvd, lp);
            jzvd.setUp(jzDataSource, JzvdStd.SCREEN_WINDOW_TINY);
            jzvd.setState(currentState);
            jzvd.addTextureView();
            JzvdMgr.setSecondFloor(jzvd);
            onStateNormal();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 详情小窗口播放完保持小窗口模式,不退出
     */
    @Override
    public void onAutoCompletion() {
        if (currentScreen == SCREEN_WINDOW_TINY) {
            onStateAutoComplete();
            tinyReplay.setVisibility(VISIBLE);
//            replayTextView.setVisibility(VISIBLE);
        } else {
            super.onAutoCompletion();
        }
    }

    @Override
    public void setUp(JZDataSource jzDataSource, int screen) {
        super.setUp(jzDataSource, screen);
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            playCountText.setVisibility(GONE);
            videoTime.setVisibility(GONE);
            replayTextView.setVisibility(GONE);
        } else if (currentScreen == SCREEN_WINDOW_TINY) {
            shareLayout.setVisibility(View.GONE);
            playCountText.setVisibility(GONE);
            videoTime.setVisibility(GONE);
            replayTextView.setVisibility(GONE);
        }
    }
}
