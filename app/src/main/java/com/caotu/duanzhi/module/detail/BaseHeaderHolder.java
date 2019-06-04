package com.caotu.duanzhi.module.detail;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.caotu.duanzhi.Http.bean.AuthBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.module.base.BaseSwipeActivity;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.MySpUtils;
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

import java.util.ArrayList;

/**
 * 做个详情头布局的统一
 *
 * @param <T>
 */
public abstract class BaseHeaderHolder<T> implements IHolder<T>, View.OnClickListener {
    public View rootView;
    protected RImageView mBaseMomentAvatarIv;

    protected ImageView mIvIsFollow, mUserAuth;
    protected TextView mBaseMomentNameTv, mTvContentText, mBaseMomentLike, mBaseMomentComment;

    //    protected ImageView mBaseMomentShareIv;
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
        mBaseMomentComment = rootView.findViewById(R.id.base_moment_comment);
//        mBaseMomentShareIv = rootView.findViewById(R.id.base_moment_share_iv);
        nineImageView = rootView.findViewById(R.id.detail_image_type);
        videoView = rootView.findViewById(R.id.detail_video_type);
        mUserAuth = rootView.findViewById(R.id.user_auth);
        guanjian = rootView.findViewById(R.id.iv_user_headgear);
        mBaseMomentAvatarIv.setOnClickListener(this);
        mBaseMomentNameTv.setOnClickListener(this);
    }

    void setComment(int count) {
        if (mBaseMomentComment == null) return;
        mBaseMomentComment.setText(String.format("评论  %s", Int2TextUtils.toText(count, "w")));
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
        AuthBean authBean;
        if (dataBean instanceof MomentsDataBean) {
            authBean = ((MomentsDataBean) dataBean).getAuth();
        } else {
            authBean = ((CommendItemBean.RowsBean) dataBean).getAuth();
        }
        if (authBean != null && !TextUtils.isEmpty(authBean.getAuthid())) {
            mUserAuth.setVisibility(View.VISIBLE);
            String cover = VideoAndFileUtils.getCover(authBean.getAuthpic());
            GlideUtils.loadImage(cover, mUserAuth);
        } else {
            mUserAuth.setVisibility(View.GONE);
        }
        mUserAuth.setOnClickListener(this);
        dealType(dataBean);
        dealFollow(dataBean);
        dealLikeAndUnlike(dataBean);
    }

    protected abstract void dealLikeAndUnlike(T dataBean);

    protected abstract void dealFollow(T dataBean);

    protected abstract void dealType(T dataBean);

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


    public void dealVideo(String videoPath, String videoCover, String contentId,
                          boolean isLandscapeVideo, String time, String playCount) {
        isVideo = true;
        landscape = isLandscapeVideo;
        cover = videoCover;
        videoUrl = videoPath;
        videoView.setUrl(videoUrl); //设置视频地址
        StandardVideoController controller = new StandardVideoController(videoView.getContext());
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

        // TODO: 2019-05-31 这里就不再写自动重播的弹窗逻辑了,没意思,硬要的话拷贝 BaseContentAdapter 代码
        boolean videoMode = MySpUtils.getBoolean(MySpUtils.SP_VIDEO_AUTO_REPLAY, false);
        videoView.setLooping(videoMode);

        VideoAndFileUtils.setVideoWH(videoView, isLandscapeVideo);
        videoView.setVideoController(controller); //设置控制器，如需定制可继承BaseVideoController
        videoView.addToVideoViewManager();
        videoView.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {
                Activity runningActivity = MyApplication.getInstance().getRunningActivity();
                if (runningActivity instanceof BaseSwipeActivity) {
                    ((BaseSwipeActivity) runningActivity)
                            .setCanSwipe(BaseIjkVideoView.PLAYER_FULL_SCREEN != playerState);
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

    @Override
    public void onClick(View v) {
        if (v == mBaseMomentAvatarIv || v == mBaseMomentNameTv) {
            String userId = (headerBean instanceof MomentsDataBean) ? ((MomentsDataBean) headerBean).getContentuid()
                    : ((CommendItemBean.RowsBean) headerBean).userid;
            HelperForStartActivity.openOther(HelperForStartActivity.type_other_user,
                    userId);
        } else if (v == mUserAuth) {
            AuthBean authBean;
            if (headerBean instanceof MomentsDataBean) {
                authBean = ((MomentsDataBean) headerBean).getAuth();
            } else {
                authBean = ((CommendItemBean.RowsBean) headerBean).getAuth();
            }
            if (authBean != null && !TextUtils.isEmpty(authBean.getAuthurl())) {
                WebActivity.openWeb("用户勋章", authBean.getAuthurl(), true);
            }
        }
    }
}
