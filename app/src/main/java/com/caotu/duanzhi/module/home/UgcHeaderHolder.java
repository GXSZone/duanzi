package com.caotu.duanzhi.module.home;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.other.VideoDownloadHelper;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NineLayoutHelper;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.FastClickListener;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.RImageView;
import com.sunfusheng.widget.NineImageView;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * @author mac
 * @日期: 2018/11/20
 * @describe 头布局的踩需要隐藏, 内容的bean对象, 评论详情头布局的样式
 */
public class UgcHeaderHolder implements IHolder {
    public RImageView mBaseMomentAvatarIv;
    public TextView mBaseMomentNameTv;
    public ImageView mIvIsFollow;
    public TextView mTvContentText;
    public TextView mBaseMomentComment, mBaseMomentLike;
    public ImageView mBaseMomentShareIv;
    public NineImageView nineImageView;
    public MyVideoPlayerStandard videoView;
    public UgcContentFragment fragment;
    public View parentView;

    public UgcHeaderHolder(UgcContentFragment ugcContentFragment, View rootView) {
        fragment = ugcContentFragment;
        parentView = rootView;
        this.mBaseMomentAvatarIv = rootView.findViewById(R.id.base_moment_avatar_iv);
        this.mBaseMomentNameTv = rootView.findViewById(R.id.base_moment_name_tv);
        this.mIvIsFollow = rootView.findViewById(R.id.iv_is_follow);
        this.mTvContentText = rootView.findViewById(R.id.tv_content_text);
        this.mBaseMomentLike = rootView.findViewById(R.id.base_moment_like);
        this.mBaseMomentComment = rootView.findViewById(R.id.base_moment_comment);
        this.mBaseMomentShareIv = rootView.findViewById(R.id.base_moment_share_iv);
        this.nineImageView = rootView.findViewById(R.id.detail_image_type);
        this.videoView = rootView.findViewById(R.id.detail_video_type);
    }

    public MyVideoPlayerStandard getVideoView() {
        return videoView;
    }

    private boolean isVideo;
    //分享需要的icon使用记录
    private String cover;
    //视频的下载URL
    private String videoUrl;
    //横视频是1,默认则为竖视频
    private boolean landscape;

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

    /**
     * 评论成功数值加一
     */
    @Override
    public void commentPlus() {
        int contentcomment = headerBean.getContentcomment();
        contentcomment++;
        mBaseMomentComment.setText(Int2TextUtils.toText(contentcomment, "w"));
        headerBean.setContentcomment(contentcomment);
    }

    @Override
    public void commentMinus() {
        int contentcomment = headerBean.getContentcomment();
        contentcomment--;
        mBaseMomentComment.setText(Int2TextUtils.toText(contentcomment, "w"));
        headerBean.setContentcomment(contentcomment);
    }

    @Override
    public int headerViewHeight() {
        return parentView == null ? 0 : parentView.getMeasuredHeight();
    }


    MomentsDataBean headerBean;

    @Override
    public void bindDate(MomentsDataBean data) {
        headerBean = data;
        GlideUtils.loadImage(data.getUserheadphoto(), mBaseMomentAvatarIv);
        mBaseMomentNameTv.setText(data.getUsername());
        mBaseMomentAvatarIv.setOnClickListener(v -> HelperForStartActivity.
                openOther(HelperForStartActivity.type_other_user, data.getContentuid()));
        mBaseMomentNameTv.setOnClickListener(v -> HelperForStartActivity.
                openOther(HelperForStartActivity.type_other_user, data.getContentuid()));
        String contenttype = data.getContenttype();
        isVideo = LikeAndUnlikeUtil.isVideoType(contenttype);
        if (isVideo) {
            videoView.setVisibility(View.VISIBLE);
            nineImageView.setVisibility(View.GONE);
            dealVideo(data);
        } else {
            videoView.setVisibility(View.GONE);
            nineImageView.setVisibility(View.VISIBLE);
            dealNineLayout(data);
        }
        if (MySpUtils.isMe(data.getContentuid())) {
            mIvIsFollow.setVisibility(View.GONE);
        } else {
            mIvIsFollow.setVisibility(View.VISIBLE);
        }
        //1关注 0未关注  已经关注状态的不能取消关注
        String isfollow = data.getIsfollow();
        if (LikeAndUnlikeUtil.isLiked(isfollow)) {
            mIvIsFollow.setEnabled(false);
        }
        mIvIsFollow.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestFocus(data.getContentuid(),
                        "2", true, new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                ToastUtil.showShort("关注成功");
                                mIvIsFollow.setEnabled(false);
                            }

                            @Override
                            public void onError(Response<BaseResponseBean<String>> response) {
                                ToastUtil.showShort("关注失败,请稍后重试");
                                super.onError(response);
                            }
                        });
            }
        });
        //	1可见，0不可见
        mTvContentText.setText("1".equals(data.getIsshowtitle()) ? data.getContenttitle() : "");
        mTvContentText.setVisibility(TextUtils.isEmpty(mTvContentText.getText().toString())
                ? View.GONE : View.VISIBLE);
        mBaseMomentShareIv.setOnClickListener(v -> {
            if (callBack != null) {
                callBack.share(data);
            }
        });

        /*-------------------------------点赞和踩的处理---------------------------------*/
        mBaseMomentLike.setText(Int2TextUtils.toText(data.getContentgood(), "w"));

        mBaseMomentComment.setText(Int2TextUtils.toText(data.getContentcomment(), "w"));
//        "0"_未赞未踩 "1"_已赞 "2"_已踩

        if (TextUtils.equals("1", data.getGoodstatus())) {
            mBaseMomentLike.setSelected(true);
        }
        mBaseMomentLike.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestLikeOrUnlike(data.getContentuid(),
                        data.getContentid(), true, mBaseMomentLike.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                if (!mBaseMomentLike.isSelected()) {
                                    LikeAndUnlikeUtil.showLike(mBaseMomentLike, 20, 30);
                                }
                                int goodCount = data.getContentgood();
                                if (mBaseMomentLike.isSelected()) {
                                    goodCount--;
                                    mBaseMomentLike.setSelected(false);
                                } else {
                                    goodCount++;
                                    mBaseMomentLike.setSelected(true);
                                }
                                mBaseMomentLike.setText(Int2TextUtils.toText(goodCount, "w"));
                                data.setContentgood(goodCount);
                                //修改goodstatus状态 "0"_未赞未踩 "1"_已赞 "2"_已踩
                                data.setGoodstatus(mBaseMomentLike.isSelected() ? "1" : "0");
                            }
                        });
            }
        });

        /*-------------------------------点赞和踩的处理结束---------------------------------*/
    }

    @Override
    public void justBindCountAndState(MomentsDataBean data) {

    }

    private void dealNineLayout(MomentsDataBean data) {
        if (data.imgList == null || data.imgList.size() == 0) return;
        //区分是单图还是多图
        cover = data.imgList.get(0).url;
        nineImageView.loadGif(false)
                .enableRoundCorner(false)
                .setData(data.imgList, NineLayoutHelper.getInstance().getLayoutHelper(data.imgList));
        nineImageView.setClickable(true);
        nineImageView.setFocusable(true);
        nineImageView.setOnItemClickListener(position ->
                HelperForStartActivity.openImageWatcher(position, data.imgList, data.getContentid()));
    }

    private void dealVideo(MomentsDataBean data) {

        if (data.imgList == null || data.imgList.size() < 2) {
            ToastUtil.showShort("内容集合解析出问题了:" + data.getContenturllist());
            return;
        }
        cover = data.imgList.get(0).url;
        videoUrl = data.imgList.get(1).url;

        videoView.setThumbImage(cover);
        landscape = "1".equals(data.getContenttype());
        VideoAndFileUtils.setVideoWH(videoView, landscape);

        int playCount = Integer.parseInt(data.getPlaycount());
        videoView.setPlayCount(playCount);
        videoView.setVideoTime(data.getShowtime());
        videoView.setOnShareBtListener(new MyVideoPlayerStandard.CompleteShareListener() {
            @Override
            public void share(SHARE_MEDIA share_media) {
                WebShareBean bean = ShareHelper.getInstance().changeContentBean(data, share_media, cover, CommonHttpRequest.url);
                ShareHelper.getInstance().shareWeb(bean);
            }

            @Override
            public void downLoad() {
                VideoDownloadHelper.getInstance().startDownLoad(true, data.getContentid(), videoUrl);
            }

            @Override
            public void justPlay() {
                CommonHttpRequest.getInstance().requestPlayCount(data.getContentid());
                videoView.setOrientation(landscape);
            }

            @Override
            public void timeToShowWxIcon() {

            }
        });
        videoView.setVideoUrl(videoUrl, "", false);
//        videoView.autoPlay();
    }

    @Override
    public void autoPlayVideo() {
        if (isVideo && videoView != null && videoView.getVisibility() == View.VISIBLE) {
            videoView.startVideo();
        }
    }

    public ShareCallBack callBack;

    @Override
    public void setCallBack(IHolder.ShareCallBack callBack) {
        this.callBack = callBack;
    }
}
