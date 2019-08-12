package com.caotu.duanzhi.module.holder;

import android.app.Activity;
import android.graphics.drawable.Drawable;
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
import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.AuthBean;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.module.base.BaseSwipeActivity;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NineLayoutHelper;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.BaseIOSDialog;
import com.dueeeke.videoplayer.ProgressManagerImpl;
import com.dueeeke.videoplayer.listener.OnVideoViewStateChangeListener;
import com.dueeeke.videoplayer.player.BaseIjkVideoView;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.playerui.StandardVideoController;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.RImageView;
import com.sunfusheng.GlideImageView;
import com.sunfusheng.transformation.BlurTransformation;
import com.sunfusheng.widget.ImageData;
import com.sunfusheng.widget.NineImageView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;

/**
 * 做个详情头布局的统一
 *
 * @param <T>
 */
public abstract class BaseHeaderHolder<T> implements IHolder<T>, View.OnClickListener {
    public View rootView;
    protected RImageView mBaseMomentAvatarIv;

    protected ImageView mUserAuth, userAvatar;
    protected TextView mBaseMomentNameTv, mTvContentText, mBaseMomentLike, mBaseMomentComment,
            mUserName, mUserIsFollow, bottomLikeView, mIvIsFollow;

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

        mUserAuth = rootView.findViewById(R.id.user_auth);
        guanjian = rootView.findViewById(R.id.iv_user_headgear);
        if (mBaseMomentAvatarIv != null) {
            mBaseMomentAvatarIv.setOnClickListener(this);
        }
        if (mBaseMomentNameTv != null) {
            mBaseMomentNameTv.setOnClickListener(this);
        }
        if (mIvIsFollow != null) {
            mIvIsFollow.setOnClickListener(this);
        }

        rootView.findViewById(R.id.tv_share_weixin).setOnClickListener(this);
        rootView.findViewById(R.id.tv_share_qq).setOnClickListener(this);
    }

    @Override
    public void bindSameView(TextView mUserName, ImageView userAvatar, TextView mUserIsFollow, TextView bottomLikeView) {
        this.mUserName = mUserName;
        this.userAvatar = userAvatar;
        this.mUserIsFollow = mUserIsFollow;
        this.bottomLikeView = bottomLikeView;
        if (this.mUserName != null) {
            this.mUserName.setOnClickListener(this);
        }
        if (this.userAvatar != null) {
            this.userAvatar.setOnClickListener(this);
        }

        if (this.mUserIsFollow != null) {
            this.mUserIsFollow.setOnClickListener(this);
        }
    }

    public void setComment(int count) {
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

    protected StandardVideoController controller;

    @Override
    public StandardVideoController getVideoController() {
        return controller;
    }

    @Override
    public IjkVideoView getVideoView() {
        return videoView;
    }


    @Override
    public void autoPlayVideo() {
        if (videoView != null) {
            // TODO: 2019-06-17 这行代码很关键 ,不然进入详情直接播放会有点小问题
            videoView.postDelayed(() -> videoView.start(), 80);
        }
    }

    @Override
    public void bindDate(T dataBean) {
        headerBean = dataBean;
        bindUserInfo(dataBean);
        dealType(dataBean);
        dealFollow(dataBean);
        dealLikeAndUnlike(dataBean);
    }

    public void bindUserInfo(T dataBean) {
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
    }

    protected void dealLikeAndUnlike(T dataBean) {
        if (dataBean instanceof CommendItemBean.RowsBean) {
            CommendItemBean.RowsBean bean = (CommendItemBean.RowsBean) dataBean;
            mBaseMomentLike.setSelected(LikeAndUnlikeUtil.isLiked(bean.goodstatus));
            mBaseMomentLike.setText(Int2TextUtils.toText(bean.commentgood, "w"));
            if (bottomLikeView != null) {
                bottomLikeView.setSelected(LikeAndUnlikeUtil.isLiked(bean.goodstatus));
                bottomLikeView.setText(Int2TextUtils.toText(bean.commentgood, "w"));
            }
            if (guanjian != null && !TextUtils.isEmpty(bean.getGuajianurl())) {
                guanjian.load(bean.getGuajianurl());
            }
        } else {
            MomentsDataBean data = (MomentsDataBean) dataBean;
            mBaseMomentLike.setText(Int2TextUtils.toText(data.getContentgood(), "w"));
            mBaseMomentLike.setSelected(LikeAndUnlikeUtil.isLiked(data.getGoodstatus()));
            if (bottomLikeView != null) {
                bottomLikeView.setText(Int2TextUtils.toText(data.getContentgood(), "w"));
                bottomLikeView.setSelected(LikeAndUnlikeUtil.isLiked(data.getGoodstatus()));
            }
            if (guanjian != null && !TextUtils.isEmpty(data.getGuajianurl())) {
                guanjian.load(data.getGuajianurl());
            }
        }
        mBaseMomentLike.setOnClickListener(this);
        if (bottomLikeView != null) {
            bottomLikeView.setOnClickListener(this);
        }

    }

    protected void dealFollow(T dataBean) {
        String userId = dataBean instanceof CommendItemBean.RowsBean
                ? ((CommendItemBean.RowsBean) dataBean).userid : ((MomentsDataBean) dataBean).getContentuid();

        String isFollow = dataBean instanceof CommendItemBean.RowsBean
                ? ((CommendItemBean.RowsBean) dataBean).getIsfollow() : ((MomentsDataBean) dataBean).getIsfollow();
        if (MySpUtils.isMe(userId)) {
            mIvIsFollow.setVisibility(View.GONE);
            if (mUserIsFollow != null) {
                mUserIsFollow.setVisibility(View.GONE);
            }
        } else {
            mIvIsFollow.setVisibility(View.VISIBLE);
            if (mUserIsFollow != null) {
                mUserIsFollow.setVisibility(View.VISIBLE);
            }
        }
        //1关注 0未关注  已经关注状态的不能取消关注
        if (LikeAndUnlikeUtil.isLiked(isFollow)) {
            mIvIsFollow.setEnabled(false);
            mIvIsFollow.setText("已关注");
            if (mUserIsFollow != null) {
                mUserIsFollow.setText("已关注");
                mUserIsFollow.setEnabled(false);
            }
        }
    }

    /**
     * 现在视频格式单独分出去了,只有图片和纯文字两种
     *
     * @param dataBean
     */
    protected abstract void dealType(T dataBean);


    public void dealNineLayout(ArrayList<ImageData> imgList, String contentId, String tagshowid) {
        if (imgList == null || imgList.size() == 0) return;
        isVideo = false;
        //区分是单图还是多图
        cover = imgList.get(0).url;
        nineImageView.loadGif(false)
                .enableRoundCorner(false)
                .setData(imgList, NineLayoutHelper.getInstance().getContentLayoutHelper(imgList));
        nineImageView.setClickable(true);
        nineImageView.setFocusable(true);
        nineImageView.setOnItemClickListener(position ->
                HelperForStartActivity.openImageWatcher(position, imgList, contentId, tagshowid));
    }


    public void dealVideo(String videoPath, String videoCover, String contentId,
                          boolean isLandscapeVideo, String time, String playCount, boolean isMyself) {
        isVideo = true;
        landscape = isLandscapeVideo;
        cover = videoCover;
        videoUrl = MyApplication.buildFileUrl(videoPath);
        videoView.setUrl(videoUrl); //设置视频地址
        controller = new StandardVideoController(videoView.getContext()) {
            @Override
            public void replayAction() {
                if (!MySpUtils.getReplayTip()) {
                    showTipDialog();
                } else {
                    doSuper();
                }
            }

            public void doSuper() {
                UmengHelper.event(UmengStatisticsKeyIds.replay);
                super.replayAction();
            }

            public void showTipDialog() {
                Activity activity = MyApplication.getInstance().getRunningActivity();
                BaseIOSDialog dialog = new BaseIOSDialog(activity, new BaseIOSDialog.OnClickListener() {
                    @Override
                    public void okAction() {
                        doSuper();
                    }

                    @Override
                    public void cancelAction() {
                        videoView.setLooping(true);
                        doSuper();
                        MySpUtils.setReplaySwitch(true);
                    }
                });
                dialog.setCancelText("自动重播")
                        .setOkText("手动重播")
                        .setTitleText("亲爱的段友，视频播完后你的喜好？")
                        .show();
                MySpUtils.setReplayTip();
            }
        };
        try {
            Glide.with(controller.getThumb()).load(cover).into(controller.getThumb());
            Glide.with(videoView)
                    .load(cover)
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(
                            MyApplication.getInstance())))
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            videoView.setBackgroundForVideo(resource);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        controller.setIsMySelf(isMyself);
        // TODO: 2019-05-31 这里就不再写自动重播的弹窗逻辑了,没意思,硬要的话拷贝 BaseContentAdapter 代码
        boolean videoMode = MySpUtils.getBoolean(MySpUtils.SP_VIDEO_AUTO_REPLAY, false);
        videoView.setLooping(videoMode);

        VideoAndFileUtils.setVideoWHDetailHeader(videoView, isLandscapeVideo);
        videoView.setVideoController(controller); //设置控制器，如需定制可继承BaseVideoController
        videoView.addToVideoViewManager();
        //保存播放进度
        videoView.setProgressManager(new ProgressManagerImpl());
        videoView.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {
                Activity runningActivity = MyApplication.getInstance().getRunningActivity();
                if (runningActivity instanceof BaseSwipeActivity) {
                    ((BaseSwipeActivity) runningActivity)
                            .setCanSwipe(BaseIjkVideoView.PLAYER_FULL_SCREEN != playerState);
                }
                if (playerState == BaseIjkVideoView.PLAYER_FULL_SCREEN) {
                    UmengHelper.event(UmengStatisticsKeyIds.fullscreen);
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
        if (v == mBaseMomentAvatarIv || v == mBaseMomentNameTv || v == mUserName || v == userAvatar) {
            String userId = (headerBean instanceof MomentsDataBean) ?
                    ((MomentsDataBean) headerBean).getContentuid()
                    : ((CommendItemBean.RowsBean) headerBean).userid;
            HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, userId);
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
        } else if (v.getId() == R.id.tv_share_qq) {
            share(SHARE_MEDIA.QQ);
        } else if (v.getId() == R.id.tv_share_weixin) {
            share(SHARE_MEDIA.WEIXIN);
        } else if (v == mIvIsFollow || v == mUserIsFollow) {
            UmengHelper.event(UmengStatisticsKeyIds.follow_user);
            if (!LoginHelp.isLogin()) {
                LoginHelp.goLogin();
                return;
            }
            followHttpRequest();
        } else if (v == bottomLikeView || v == mBaseMomentLike) {
            dealLikeBt(headerBean, v);
        }
    }

    protected abstract void dealLikeBt(T headerBean, View likeView);

    public void followHttpRequest() {
        String userId = headerBean instanceof CommendItemBean.RowsBean
                ? ((CommendItemBean.RowsBean) headerBean).userid : ((MomentsDataBean) headerBean).getContentuid();
        CommonHttpRequest.getInstance().requestFocus(userId,
                "2", true, new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        ToastUtil.showShort("关注成功");
                        if (mIvIsFollow != null) {
                            mIvIsFollow.setEnabled(false);
                            mIvIsFollow.setText("已关注");
                        }
                        if (mUserIsFollow != null) {
                            mUserIsFollow.setText("已关注");
                            mUserIsFollow.setEnabled(false);
                        }

                        if (headerBean instanceof CommendItemBean.RowsBean) {
                            ((CommendItemBean.RowsBean) headerBean).setIsfollow("1");
                        } else if (headerBean instanceof MomentsDataBean) {
                            ((MomentsDataBean) headerBean).setIsfollow("1");
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<String>> response) {
                        ToastUtil.showShort("关注失败,请稍后重试");
                        super.onError(response);
                    }
                });
    }

    /**
     * 分享直接父类处理了省事
     *
     * @param shareMedia
     */
    protected void share(SHARE_MEDIA shareMedia) {
        if (headerBean instanceof CommendItemBean.RowsBean) {
            CommendItemBean.RowsBean dataBean = (CommendItemBean.RowsBean) headerBean;
            WebShareBean bean = ShareHelper.getInstance().changeCommentBean(dataBean, cover,
                    shareMedia, CommonHttpRequest.cmt_url);
            ShareHelper.getInstance().shareWeb(bean);
        } else {
            MomentsDataBean dataBean = (MomentsDataBean) headerBean;
            WebShareBean bean = ShareHelper.getInstance().changeContentBean(dataBean,
                    shareMedia, cover, CommonHttpRequest.url);
            ShareHelper.getInstance().shareWeb(bean);
        }
    }
}