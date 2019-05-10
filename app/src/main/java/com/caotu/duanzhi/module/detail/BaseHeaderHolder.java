package com.caotu.duanzhi.module.detail;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.NineLayoutHelper;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.dueeeke.videoplayer.listener.OnVideoViewStateChangeListener;
import com.dueeeke.videoplayer.player.BaseIjkVideoView;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.playerui.StandardVideoController;
import com.ruffian.library.widget.RImageView;
import com.sunfusheng.GlideImageView;
import com.sunfusheng.transformation.BlurTransformation;
import com.sunfusheng.widget.ImageData;
import com.sunfusheng.widget.NineImageView;
import com.youngfeng.snake.Snake;

import java.util.ArrayList;

/**
 * 做个详情头布局的统一
 *
 * @param <T>
 */
public abstract class BaseHeaderHolder<T> implements IHolder<T> {
    public View rootView;
    protected RImageView mBaseMomentAvatarIv;
    protected TextView mBaseMomentNameTv;
    protected ImageView mIvIsFollow, mUserAuth;
    protected TextView mTvContentText;

    protected TextView mBaseMomentLike, mBaseMomentUnlike, mBaseMomentComment;
    protected ImageView mBaseMomentShareIv;
    protected NineImageView nineImageView;
    protected IjkVideoView videoView;
    protected GlideImageView guanjian;

    protected boolean isVideo;
    //分享需要的icon使用记录
    protected String cover;
    //视频的下载URL
    protected String videoUrl;
    //横视频是1,默认则为竖视频
    protected boolean landscape;
    protected T headerBean;
    protected BaseFragment mFragment;

    public BaseHeaderHolder(View parentView) {
        rootView = parentView;
        mBaseMomentAvatarIv = rootView.findViewById(R.id.base_moment_avatar_iv);
        mBaseMomentNameTv = rootView.findViewById(R.id.base_moment_name_tv);
        mIvIsFollow = rootView.findViewById(R.id.iv_is_follow);
        mTvContentText = rootView.findViewById(R.id.tv_content_text);
        mBaseMomentLike = rootView.findViewById(R.id.base_moment_like);
        mBaseMomentUnlike = rootView.findViewById(R.id.base_moment_unlike);
        mBaseMomentComment = rootView.findViewById(R.id.base_moment_comment);
        mBaseMomentShareIv = rootView.findViewById(R.id.base_moment_share_iv);
        nineImageView = rootView.findViewById(R.id.detail_image_type);
        videoView = rootView.findViewById(R.id.detail_video_type);
        mUserAuth = rootView.findViewById(R.id.user_auth);
        guanjian = rootView.findViewById(R.id.iv_user_headgear);
    }

    @Override
    public void bindFragment(BaseFragment fragment) {
        mFragment = fragment;
    }

    @Override
    public boolean isLandscape() {
        return landscape;
    }

    @Override
    public String getVideoUrl() {
        return videoUrl;
    }

    @Override
    public String getCover() {
        return cover;
    }

    @Override
    public boolean isVideo() {
        return isVideo;
    }


    @Override
    public IjkVideoView getVideoView() {
        return videoView;
    }


    @Override
    public void autoPlayVideo() {
        if (videoView != null) {
            videoView.start();
        }
    }

    @Override
    public void bindDate(T dataBean) {
        headerBean = dataBean;
    }

    public ShareCallBack<T> callBack;

    @Override
    public void setCallBack(IHolder.ShareCallBack<T> callBack) {
        this.callBack = callBack;
    }

    public void dealNineLayout(ArrayList<ImageData> imgList, String contentId) {
        if (imgList == null || imgList.size() == 0) return;
        isVideo = false;
        //区分是单图还是多图
        cover = imgList.get(0).url;
        nineImageView.loadGif(false)
                .enableRoundCorner(false)
                .setData(imgList, NineLayoutHelper.getInstance().getLayoutHelper(imgList));
        nineImageView.setClickable(true);
        nineImageView.setFocusable(true);
        nineImageView.setOnItemClickListener(position ->
                HelperForStartActivity.openImageWatcher(position, imgList, contentId));
    }

    /**
     * 子类再复写调用super 后加自动播放逻辑,由于holder在的fragment不一样,所以需要分开处理
     *
     * @param videoPath
     * @param videoCover
     * @param contentId
     * @param isLandscapeVideo
     * @param time
     * @param playCount
     */
    private StandardVideoController controller;

    @Override
    public StandardVideoController getVideoControll() {
        return controller;
    }

    public void dealVideo(String videoPath, String videoCover, String contentId,
                          boolean isLandscapeVideo, String time, String playCount) {
        isVideo = true;
        landscape = isLandscapeVideo;
        cover = videoCover;
        videoUrl = videoPath;
        videoView.setUrl(videoUrl); //设置视频地址
        controller = new StandardVideoController(videoView.getContext());
        GlideUtils.loadImage(cover, controller.getThumb());
        Glide.with(MyApplication.getInstance())
                .asBitmap()
                .load(cover)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(
                        MyApplication.getInstance())))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        BitmapDrawable drawable = new BitmapDrawable(resource);
                        videoView.setBackgroundForVideo(drawable);
                    }
                });
        VideoAndFileUtils.setVideoWH(videoView, isLandscapeVideo);
        videoView.setVideoController(controller); //设置控制器，如需定制可继承BaseVideoController
        videoView.addToVideoViewManager();
        videoView.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {
                Log.i("videoState", "onPlayerStateChanged: " + playerState);
                Activity runningActivity = MyApplication.getInstance().getRunningActivity();
                try {
                    Snake.enableDragToClose(runningActivity,
                            BaseIjkVideoView.PLAYER_FULL_SCREEN != playerState);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPlayStateChanged(int playState) {

            }
        });
        controller.setVideoInfo(time, playCount);
        doOtherByChild(controller, contentId);
    }

    public void doOtherByChild(StandardVideoController controller, String contentId) {

    }
}
