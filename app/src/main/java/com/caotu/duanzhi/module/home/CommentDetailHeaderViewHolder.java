package com.caotu.duanzhi.module.home;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.NineLayoutHelper;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.RImageView;
import com.sunfusheng.widget.ImageData;
import com.sunfusheng.widget.NineImageView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mac
 * @日期: 2018/11/15
 * @describe TODO
 */
public class CommentDetailHeaderViewHolder {
    public RImageView mBaseMomentAvatarIv;
    public TextView mBaseMomentNameTv;
    public ImageView mIvIsFollow;
    public TextView mTvContentText;
    public LinearLayout llHasComment;
    public TextView mBaseMomentComment, mBaseMomentLike;
    public ImageView mBaseMomentShareIv;
    public NineImageView nineImageView;
    public MyVideoPlayerStandard videoView;
    public CommentDetailFragment fragment;

    public CommentDetailHeaderViewHolder(View rootView, CommentDetailFragment commentDetailFragment) {
        fragment = commentDetailFragment;
        this.mBaseMomentAvatarIv = (RImageView) rootView.findViewById(R.id.base_moment_avatar_iv);
        this.mBaseMomentNameTv = (TextView) rootView.findViewById(R.id.base_moment_name_tv);
        this.mIvIsFollow = (ImageView) rootView.findViewById(R.id.iv_is_follow);
        this.mTvContentText = (TextView) rootView.findViewById(R.id.tv_content_text);
        this.mBaseMomentLike = rootView.findViewById(R.id.base_moment_like);
        this.mBaseMomentComment = rootView.findViewById(R.id.base_moment_comment);
        this.mBaseMomentShareIv = (ImageView) rootView.findViewById(R.id.base_moment_share_iv);
        this.nineImageView = rootView.findViewById(R.id.detail_image_type);
        this.videoView = rootView.findViewById(R.id.detail_video_type);
        llHasComment = rootView.findViewById(R.id.ll_has_comment_replay);
    }

    /**
     * 用于设置是否有评论的头布局
     *
     * @param hasComment
     */
    public void HasComment(boolean hasComment) {
        llHasComment.setVisibility(hasComment ? View.VISIBLE : View.GONE);
    }

    private boolean isVideo;
    //分享需要的icon使用记录
    private String cover;
    //视频的下载URL
    private String videoUrl;

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getCover() {
        return cover;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void bindDate(CommendItemBean.RowsBean data) {
        GlideUtils.loadImage(data.userheadphoto, mBaseMomentAvatarIv);
        mTvContentText.setText(data.commenttext);
        mBaseMomentComment.setText(Int2TextUtils.toText(data.replyCount,"w"));
        // TODO: 2018/11/17 如果集合是空的代表是纯文字类型
        List<CommentUrlBean> commentUrlBean = VideoAndFileUtils.getCommentUrlBean(data.commenturl);
        if (commentUrlBean != null && commentUrlBean.size() > 0) {
            isVideo = LikeAndUnlikeUtil.isVideoType(commentUrlBean.get(0).type);
            if (isVideo) {
                videoView.setVisibility(View.VISIBLE);
                nineImageView.setVisibility(View.GONE);
                dealVideo(commentUrlBean, data);
            } else {
                videoView.setVisibility(View.GONE);
                nineImageView.setVisibility(View.VISIBLE);
                dealNineLayout(commentUrlBean);
            }
        } else {
            nineImageView.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
        }

        mBaseMomentNameTv.setText(data.username);
        //1关注 0未关注  已经关注状态的不能取消关注
        String isfollow = data.getIsfollow();
        if ("0".equals(isfollow) || TextUtils.isEmpty(isfollow)) {
            mIvIsFollow.setSelected(false);
        } else {
            mIvIsFollow.setEnabled(false);
        }
        mIvIsFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LoginHelp.isLoginAndSkipLogin()) return;
                CommonHttpRequest.getInstance().<String>requestFocus(data.userid,
                        "2", true, new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                ToastUtil.showShort("关注成功");
                                mIvIsFollow.setEnabled(true);
                            }

                            @Override
                            public void onError(Response<BaseResponseBean<String>> response) {
                                ToastUtil.showShort("关注失败,请稍后重试");
                                super.onError(response);
                            }

                        });
            }
        });

        mBaseMomentShareIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) {
                    callBack.share(data);
                }
            }
        });
        mBaseMomentLike.setSelected(LikeAndUnlikeUtil.isLiked(data.goodstatus));
        mBaseMomentLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHttpRequest.getInstance().requestCommentsLike(data.userid,
                        data.commentid, mBaseMomentLike.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                mBaseMomentLike.setSelected(!mBaseMomentLike.isSelected());
                            }

//                            @Override
//                            public void needLogin() {
//                                LoginHelp.goLogin();
//                            }
                        });
            }
        });
    }

    private void dealNineLayout(List<CommentUrlBean> commentUrlBean) {
        ArrayList<ImageData> imgList = new ArrayList<>();
        if (commentUrlBean.size() == 1) {
            String info = commentUrlBean.get(0).info;
            Glide.with(nineImageView.getContext())
                    .asBitmap()
                    .load(info)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            ImageData data = new ImageData(info);
                            data.realWidth = resource.getWidth();
                            data.realHeight = resource.getHeight();
                            Log.i("detail", "width:" + data.realWidth + "  height:" + data.realHeight);
                            imgList.add(data);
                            showNineLayout(imgList);
                        }
                    });

        } else {
            for (int i = 0; i < commentUrlBean.size(); i++) {
                ImageData data = new ImageData(commentUrlBean.get(i).info);
                imgList.add(data);
            }
            showNineLayout(imgList);
        }
        cover = imgList.get(0).url;
    }

    private void showNineLayout(ArrayList<ImageData> imgList) {
        //区分是单图还是多图
        nineImageView.loadGif(false)
                .enableRoundCorner(false)
                .setData(imgList, NineLayoutHelper.getInstance().getLayoutHelper(imgList));
        nineImageView.setClickable(true);
        nineImageView.setFocusable(true);
        nineImageView.setOnItemClickListener(new NineImageView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                HelperForStartActivity.openImageWatcher(position, imgList,
                        (ImageView) nineImageView.getChildAt(position));
            }
        });
    }

    private void dealVideo(List<CommentUrlBean> commentUrlBean, CommendItemBean.RowsBean data) {

        CommentUrlBean urlBean = commentUrlBean.get(0);
        videoView.setThumbImage(urlBean.cover);
        cover = urlBean.cover;
        videoUrl = urlBean.info;
        //1横视频2竖视频3图片4GIF
        boolean landscape = "1".equals(urlBean.type);
        VideoAndFileUtils.setVideoWH(videoView, landscape);
        videoView.setOrientation(landscape);
//        int playCount = Integer.parseInt(data.getPlaycount());
//        videoView.setPlayCount(playCount);

        videoView.setOnShareBtListener(new MyVideoPlayerStandard.CompleteShareListener() {
            @Override
            public void share(SHARE_MEDIA share_media) {
                //视频播放完的分享直接分享

                WebShareBean bean = ShareHelper.getInstance().changeCommentBean(data, urlBean.cover, share_media, fragment.getShareUrl());
                ShareHelper.getInstance().shareWeb(bean);

            }

            @Override
            public void playStart() {
                CommonHttpRequest.getInstance().requestPlayCount(data.contentid);
            }
        });
        videoView.setVideoUrl(urlBean.info, "", false);
        videoView.autoPlay();
    }

    public ShareCallBack callBack;

    public void setCallBack(ShareCallBack callBack) {
        this.callBack = callBack;
    }

    public interface ShareCallBack {
        void share(CommendItemBean.RowsBean bean);
    }

}
