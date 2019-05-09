package com.caotu.duanzhi.module.detail;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.NineLayoutHelper;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.playerui.StandardVideoController;
import com.ruffian.library.widget.RImageView;
import com.sunfusheng.GlideImageView;
import com.sunfusheng.widget.ImageData;
import com.sunfusheng.widget.NineImageView;

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


    public void dealVideo(String videoPath, String videoCover, String contentId,
                          boolean isLandscapeVideo, String time, String playCount) {

        isVideo = true;
        landscape = isLandscapeVideo;
        cover = videoCover;
        videoUrl = videoPath;
        videoView.setUrl(videoUrl); //设置视频地址
        StandardVideoController controller = new StandardVideoController(videoView.getContext());
        GlideUtils.loadImage(cover, controller.getThumb());
        VideoAndFileUtils.setVideoWH(videoView, isLandscapeVideo);
        videoView.setVideoController(controller); //设置控制器，如需定制可继承BaseVideoController
        controller.setVideoInfo(time, playCount);
    }
}
