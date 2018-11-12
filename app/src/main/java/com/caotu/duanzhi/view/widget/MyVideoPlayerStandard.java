package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

/**
 * Created by Nathen
 * On 2016/04/18 16:15
 */
public class MyVideoPlayerStandard extends JzvdStd {

    private LinearLayout shareLayout;
    private TextView playCountText;

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
    }

    /**
     * 设置封面图片
     *
     * @param imageUrl
     */
    public void setThumbImage(String imageUrl) {
        Glide.with(getContext()).load(imageUrl).into(thumbImageView);
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
            playCountText.setText(Int2TextUtils.toText(playCount, "W") + "播放");
        }
    }

    /**
     * 更换播放和暂停的按钮UI,参考JzvdStd
     */
    public void updateStartImage() {
        if (currentState == CURRENT_STATE_PLAYING) {
            startButton.setVisibility(VISIBLE);
            startButton.setImageResource(R.mipmap.play_stop);
            replayTextView.setVisibility(INVISIBLE);
            shareLayout.setVisibility(GONE);
            playCountText.setVisibility(GONE);
        } else if (currentState == CURRENT_STATE_ERROR) {
            startButton.setVisibility(INVISIBLE);
            replayTextView.setVisibility(INVISIBLE);
            shareLayout.setVisibility(GONE);
            playCountText.setVisibility(GONE);
        } else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
            startButton.setVisibility(VISIBLE);
            startButton.setImageResource(R.drawable.jz_click_replay_selector);
            replayTextView.setVisibility(VISIBLE);
            shareLayout.setVisibility(VISIBLE);
            playCountText.setVisibility(VISIBLE);
        } else {
            startButton.setImageResource(R.mipmap.play_start);
            replayTextView.setVisibility(INVISIBLE);
            shareLayout.setVisibility(GONE);
            playCountText.setVisibility(VISIBLE);
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

    /**
     * 全屏和退出全屏的类似监听
     */
    @Override
    public void autoQuitFullscreen() {
        playCountText.setVisibility(VISIBLE);
        super.autoQuitFullscreen();
    }

    @Override
    public void startWindowFullscreen() {
        playCountText.setVisibility(GONE);
        super.startWindowFullscreen();
    }

    /**
     * 可以作为播放开始的监听
     */
    @Override
    public void onStatePrepared() {
        super.onStatePrepared();
        if (mListener != null) {
            //播放次数加一
            setPlayCount(mPlayCount + 1);
            mListener.playStart();
        }
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
//        if (playerUrl.contains("cos.ap-shanghai.myqcloud")) {
//            playerUrl = App.buildFileUrl(playerUrl);
//        }
        //使用缓存
        if (videoUrl.startsWith("https://") || videoUrl.startsWith("http://")) {
            videoUrl = MyApplication.getInstance().getProxy().getProxyUrl(videoUrl);
        }
        setUp(videoUrl, titleText, isListVideo ? Jzvd.SCREEN_WINDOW_LIST : Jzvd.SCREEN_WINDOW_NORMAL);
    }
}
