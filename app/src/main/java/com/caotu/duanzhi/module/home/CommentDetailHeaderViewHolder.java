package com.caotu.duanzhi.module.home;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.ShareUrlBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
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
    public View rootView;
    public RImageView mBaseMomentAvatarIv;
    public TextView mBaseMomentNameTv;
    public ImageView mIvIsFollow;
    public TextView mTvContentText;
    public LinearLayout mBaseMomentImgsLl;

    public TextView mBaseMomentComment, mBaseMomentLike;
    public ImageView mBaseMomentShareIv;
    public NineImageView nineImageView;
    public MyVideoPlayerStandard videoView;

    public CommentDetailHeaderViewHolder(View rootView) {
        this.rootView = rootView;
        this.mBaseMomentAvatarIv = (RImageView) rootView.findViewById(R.id.base_moment_avatar_iv);
        this.mBaseMomentNameTv = (TextView) rootView.findViewById(R.id.base_moment_name_tv);
        this.mIvIsFollow = (ImageView) rootView.findViewById(R.id.iv_is_follow);
        this.mTvContentText = (TextView) rootView.findViewById(R.id.tv_content_text);
        this.mBaseMomentImgsLl = (LinearLayout) rootView.findViewById(R.id.base_moment_imgs_ll);
        this.mBaseMomentLike = rootView.findViewById(R.id.base_moment_like);
        this.mBaseMomentComment = rootView.findViewById(R.id.base_moment_comment);
        this.mBaseMomentShareIv = (ImageView) rootView.findViewById(R.id.base_moment_share_iv);
        this.nineImageView = rootView.findViewById(R.id.detail_image_type);
        this.videoView = rootView.findViewById(R.id.detail_video_type);
    }

    public void bindDate(CommendItemBean.RowsBean data) {
        GlideUtils.loadImage(data.userheadphoto, mBaseMomentAvatarIv);
        List<CommentUrlBean> commentUrlBean = VideoAndFileUtils.getCommentUrlBean(data.commenturl);
        if (commentUrlBean == null || commentUrlBean.size() <= 0) {
            ToastUtil.showShort("详情bean对象有误");
            return;
        }
        boolean videoType = LikeAndUnlikeUtil.isVideoType(commentUrlBean.get(0).type);
        if (videoType) {
            videoView.setVisibility(View.VISIBLE);
            nineImageView.setVisibility(View.GONE);
            dealVideo(commentUrlBean, data);
        } else {
            videoView.setVisibility(View.GONE);
            nineImageView.setVisibility(View.VISIBLE);
            dealNineLayout(commentUrlBean);
        }
        mBaseMomentNameTv.setText(data.username);
        //1关注 0未关注  已经关注状态的不能取消关注
        if ("0".equals(data.getIsfollow())) {
            mIvIsFollow.setSelected(false);
        } else {
            mIvIsFollow.setEnabled(false);
        }
        mIvIsFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

//        setContentText(mTvContentText, data., data.getContenttitle(),
//                TextUtils.equals("1", data.getIsshowtitle()), data.getTagshowid());
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

                            @Override
                            public void needLogin() {
                                LoginHelp.goLogin();
                            }
                        });
            }
        });
    }

    private void dealNineLayout(List<CommentUrlBean> commentUrlBean) {
        // TODO: 2018/11/15 评论详情里的单图和视频展示信息不知

        ArrayList<ImageData> imgList = new ArrayList<>();
        for (int i = 0; i < commentUrlBean.size(); i++) {
            ImageData data = new ImageData(commentUrlBean.get(i).info);
            imgList.add(data);
        }
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
                CommonHttpRequest.getInstance().getShareUrl(data.contentid, new JsonCallback<BaseResponseBean<ShareUrlBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<ShareUrlBean>> response) {
                        String url = response.body().getData().getUrl();
                        WebShareBean bean = ShareHelper.getInstance().changeCommentBean(data, urlBean.cover, share_media, url);
                        ShareHelper.getInstance().shareWeb(bean);
                    }
                });
            }

            @Override
            public void playStart() {
                CommonHttpRequest.getInstance().requestPlayCount(data.contentid);
            }
        });
        videoView.setVideoUrl(urlBean.info, "", false);
        videoView.autoPlay();
    }


    /**
     * 处理显示内容
     *
     * @param contentView
     * @param tagshow
     * @param contenttext
     * @param ishowTag
     * @param tagshowid
     */
    private void setContentText(TextView contentView, String tagshow, String contenttext,
                                boolean ishowTag, String tagshowid) {
        if (ishowTag && !TextUtils.isEmpty(tagshow)) {
            String source = "#" + tagshow + "#" + contenttext;
            SpannableString ss = new SpannableString(source);

            ss.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // TODO: 2018/11/8 话题详情
                    HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic, tagshowid);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }
            }, 0, tagshow.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(DevicesUtils.getColor(R.color.color_FF698F)),
                    0, tagshow.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            contentView.setText(ss);
            contentView.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            contentView.setText(contenttext);
        }
    }

    public ShareCallBack callBack;

    public void setCallBack(ShareCallBack callBack) {
        this.callBack = callBack;
    }

    public interface ShareCallBack {
        void share(CommendItemBean.RowsBean bean);
    }

}
