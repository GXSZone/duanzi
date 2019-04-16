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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.UmengHelper;
import com.caotu.duanzhi.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.dialog.BaseIOSDialog;
import com.sunfusheng.transformation.BlurTransformation;
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

    private View shareLayout;
    private TextView playCountText;
    private TextView videoTime;
    private TextView tinyReplay;
    private ImageView videoBg;

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
        shareLayout = findViewById(R.id.rl_video_end);
        findViewById(R.id.share_platform_friends_tv).setOnClickListener(this);
        findViewById(R.id.share_platform_weixin_tv).setOnClickListener(this);
        findViewById(R.id.share_platform_qq_tv).setOnClickListener(this);
        findViewById(R.id.share_platform_qqzone_tv).setOnClickListener(this);
        videoBg = findViewById(R.id.video_bg);
        playCountText = findViewById(R.id.play_count);
        Jzvd.setJzUserAction(new MyUserActionStd());

        videoTime = findViewById(R.id.tv_video_time);
        tinyReplay = findViewById(R.id.tiny_replay_text);

        replayTextView.setOnClickListener(this);
        tinyReplay.setOnClickListener(this);
        //下载按钮
        findViewById(R.id.download_text).setOnClickListener(this);
    }


    /**
     * 设置封面图片
     *
     * @param imageUrl
     */
    public void setThumbImage(String imageUrl) {
        Glide.with(MyApplication.getInstance()).asBitmap().load(imageUrl).into(thumbImageView);

        Glide.with(MyApplication.getInstance())
                .asBitmap()
                .load(imageUrl)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(
                        MyApplication.getInstance())))
                .into(videoBg);
    }

    @Override
    public void showWifiDialog() {
        boolean traffic_auto_play = MySpUtils.getBoolean(MySpUtils.SP_TRAFFIC_PLAY, false);
        //移动开关没开才有流量播放的弹窗
        if (!traffic_auto_play) {
            BaseIOSDialog dialog = new BaseIOSDialog(getContext(), new BaseIOSDialog.SimpleClickAdapter() {
                @Override
                public void okAction() {
                    onEvent(JZUserActionStd.ON_CLICK_START_WIFIDIALOG);
                    startVideo();
                    WIFI_TIP_DIALOG_SHOWED = true;
                }

                @Override
                public void cancelAction() {
                    clearFloatScreen();
                }
            });
            dialog.setCancelText("停止播放")
                    .setOkText("继续播放")
                    .setTitleText("您当前正在使用移动网络，继续播放将消耗流量")
                    .show();
        }
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

    @Override
    public void onStatePreparing() {
        super.onStatePreparing();
        //自动播放的回调,开始播放
        if (mListener != null) {
            mListener.justPlay();
        }
    }

    public int mPlayCount;

    /**
     * 设置播放次数
     *
     * @param playCount
     */
    public void setPlayCount(int playCount) {
        mPlayCount = playCount;
        playCountText.setText(Int2TextUtils.toText(playCount, "W") + "播放");
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
//        Log.i("hashcode", "updateUI_startWindowTiny " + " [" + this.hashCode() + "] ");
        if (currentState == CURRENT_STATE_PLAYING) {
            startButton.setVisibility(VISIBLE);
            startButton.setImageResource(R.mipmap.play_stop);
            shareLayout.setVisibility(GONE);
        } else if (currentState == CURRENT_STATE_ERROR) {
            startButton.setVisibility(INVISIBLE);
            shareLayout.setVisibility(GONE);
            playCountText.setVisibility(GONE);
            videoTime.setVisibility(GONE);
        } else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
            startButton.setVisibility(GONE);
            shareLayout.setVisibility(VISIBLE);
            playCountText.setVisibility(GONE);
            videoTime.setVisibility(GONE);
        } else {
            startButton.setImageResource(R.mipmap.play_start);
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
        if (currentState == CURRENT_STATE_AUTO_COMPLETE
                && currentScreen != SCREEN_WINDOW_TINY) {
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

        void downLoad();

        void justPlay();
    }

    public void dealPlayCount(MomentsDataBean item, MyVideoPlayerStandard videoPlayerView) {
        CommonHttpRequest.getInstance().requestPlayCount(item.getContentid());
        //同步播放次数
        try {
            int playCount = Integer.parseInt(item.getPlaycount());
            playCount++;
            item.setPlaycount(playCount + "");
            videoPlayerView.setPlayCount(playCount);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
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
            case R.id.share_platform_qqzone_tv:
                if (mListener != null) {
                    mListener.share(SHARE_MEDIA.QZONE);
                }
                break;
            case R.id.replay_text:
                UmengHelper.event(UmengStatisticsKeyIds.replay);
                if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                    shareLayout.setVisibility(GONE);
                    stateComplete();
                } else {
                    // TODO: 2019/4/15 这里是以防万一,其实用上面的代码测试也没啥问题 ,省的自己判断
                    startButton.performClick();
                }
                break;
            case R.id.tiny_replay_text:
                UmengHelper.event(UmengStatisticsKeyIds.replay);
                tinyReplay.setVisibility(GONE);
                stateComplete();
                break;
            case R.id.download_text:
                if (mListener != null) {
                    mListener.downLoad();
                }
//                try {
//                    String currentUrl = (String) JzvdMgr.getCurrentJzvd().getCurrentUrl();
//                    Log.i("currentUrl", currentUrl);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                break;
        }
    }

    /**
     * 2018/12/10 该处为自己添加代码,参考jzvp自己的播放实现
     */
    public void stateComplete() {
        //第一步:进度重新制为0
        if (jzDataSource != null) {
            JZUtils.saveProgress(getContext(), jzDataSource.getCurrentUrl(), 0);
        }
        //第二步:让退出小屏后的上层播放器的数据保持一致
        if (JzvdMgr.getSecondFloor() != null && JzvdMgr.getFirstFloor() != null) {
            JzvdMgr.getFirstFloor().jzDataSource = jzDataSource;
        }
        //第三步:重新开始播放
        JZMediaManager.setDataSource(jzDataSource);
        JZMediaManager.instance().prepare();
        JZMediaManager.start();
    }

    @Override
    public void startWindowFullscreen() {
        UmengHelper.event(UmengStatisticsKeyIds.fullscreen);
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
//                case JZUserAction.ON_CLICK_RESUME:
//                    if (mListener != null) {
////                        Log.i("autoPlay", "手动点击播放");
//                        mListener.playStart();
//                    }
//                    break;
                    //开启悬浮窗播放模式
//                case JZUserAction.ON_ENTER_TINYSCREEN:
//                    shareLayout.setVisibility(GONE);
//                    break;
                case JZUserAction.ON_QUIT_TINYSCREEN:
                    if (JzvdMgr.getFirstFloor() != null) {
                        MyVideoPlayerStandard videoPlayerStandard = (MyVideoPlayerStandard) JzvdMgr.getFirstFloor();
                        videoPlayerStandard.playCountText.setVisibility(GONE);
                        videoPlayerStandard.videoTime.setVisibility(GONE);
                        tinyReplay.setVisibility(GONE);
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

    public void onClickTinyBack() {
        if (!playComplete) {
            JZMediaManager.pause();
            onStatePause();
        }
        playComplete = false;
    }

    public void startWindowTiny(boolean isLand) {
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
            jzvd.setId(R.id.jz_tiny_id);
            int width;
            int height;
            if (isLand) {
                width = DevicesUtils.dp2px(180);
                height = DevicesUtils.dp2px(100);
            } else {
                width = DevicesUtils.dp2px(100);
                height = DevicesUtils.dp2px(180);
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
        if (currentScreen == SCREEN_WINDOW_TINY || currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            // TODO: 2019/4/15 这个判断条件后面加
//            if (JzvdMgr.getSecondFloor() != null) {
//                super.onAutoCompletion();
//            } else {
//
//            }
            playComplete = true;
            onStateAutoComplete();
            if (currentScreen == SCREEN_WINDOW_TINY) {
                tinyReplay.setVisibility(VISIBLE);
            }
        } else {
            super.onAutoCompletion();
        }
    }

    boolean playComplete = false;

    @Override
    public void setUp(JZDataSource jzDataSource, int screen) {
        super.setUp(jzDataSource, screen);
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            playCountText.setVisibility(GONE);
            videoTime.setVisibility(GONE);
        } else if (currentScreen == SCREEN_WINDOW_TINY) {
            shareLayout.setVisibility(View.GONE);
            playCountText.setVisibility(GONE);
            videoTime.setVisibility(GONE);
        }
    }

    /**
     * 记录播放进度
     */
    int mProgress;

    public int getmProgress() {
        return mProgress;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mProgress = progress;
//        Log.i("progress", "onProgressChanged: " + progress + "seekbar:" + seekBar.getProgress());
        super.onProgressChanged(seekBar, progress, fromUser);
    }
}
