package com.caotu.duanzhi.module.detail;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.AuthBean;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.module.download.VideoDownloadHelper;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.FastClickListener;
import com.dueeeke.videoplayer.listener.VideoListenerAdapter;
import com.dueeeke.videoplayer.playerui.StandardVideoController;
import com.lzy.okgo.model.Response;
import com.sunfusheng.widget.ImageData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mac
 * @日期: 2018/11/15
 * @describe TODO
 */
public class CommentDetailHeaderViewHolder extends BaseHeaderHolder<CommendItemBean.RowsBean> {

    private TextView tvGoDetail;

    @Override
    public void doOtherByChild(StandardVideoController controller, String contentId) {
        controller.setMyVideoOtherListener(new VideoListenerAdapter() {
            @Override
            public void share(byte type) {
                WebShareBean bean = ShareHelper.getInstance().changeCommentBean(headerBean, cover,
                        ShareHelper.translationShareType(type), CommonHttpRequest.cmt_url);
                ShareHelper.getInstance().shareWeb(bean);
            }

            @Override
            public void download() {
                VideoDownloadHelper.getInstance().startDownLoad(true, contentId, videoUrl);
            }
        });
        autoPlayVideo();
    }


    public CommentDetailHeaderViewHolder(View parentView) {
        super(parentView);
        tvGoDetail = rootView.findViewById(R.id.tv_click_content_detail);
    }

    public void commentPlus() {
        int contentcomment = headerBean.replyCount;
        contentcomment++;
        mBaseMomentComment.setText(Int2TextUtils.toText(contentcomment, "w"));
        headerBean.replyCount = contentcomment;
    }

    public void commentMinus() {
        int contentcomment = headerBean.replyCount;
        contentcomment--;
        if (contentcomment < 0) {
            contentcomment = 0;
        }
        mBaseMomentComment.setText(Int2TextUtils.toText(contentcomment, "w"));
        headerBean.replyCount = contentcomment;
    }


    public void changeHeaderDate(CommendItemBean.RowsBean data) {
        if (data == null) return;
        headerBean = data;
        //1关注 0未关注  已经关注状态的不能取消关注
        String isfollow = data.getIsfollow();
        if (LikeAndUnlikeUtil.isLiked(isfollow)) {
            mIvIsFollow.setEnabled(false);
        }
        mBaseMomentLike.setSelected(LikeAndUnlikeUtil.isLiked(data.goodstatus));
        //评论点赞数
        mBaseMomentLike.setText(Int2TextUtils.toText(data.commentgood, "w"));
    }

    public void bindDate(CommendItemBean.RowsBean data) {
        super.bindDate(data);
        GlideUtils.loadImage(data.userheadphoto, mBaseMomentAvatarIv);
        guanjian.load(data.getGuajianurl());
        mBaseMomentAvatarIv.setOnClickListener(v -> HelperForStartActivity.
                openOther(HelperForStartActivity.type_other_user, data.userid));

        if (data.isShowContentFrom()) {
            tvGoDetail.setVisibility(View.VISIBLE);
        } else {
            tvGoDetail.setVisibility(View.GONE);
        }
        tvGoDetail.setOnClickListener(v -> HelperForStartActivity.openContentDetail(data.contentid));

        AuthBean authBean = data.getAuth();
        if (authBean != null && !TextUtils.isEmpty(authBean.getAuthid())) {
            mUserAuth.setVisibility(View.VISIBLE);
            String cover = VideoAndFileUtils.getCover(authBean.getAuthpic());
            GlideUtils.loadImage(cover, mUserAuth);
        } else {
            mUserAuth.setVisibility(View.GONE);
        }
        mUserAuth.setOnClickListener(v -> {
            if (authBean != null && !TextUtils.isEmpty(authBean.getAuthurl())) {
                WebActivity.openWeb("用户勋章", authBean.getAuthurl(), true);
            }
        });

        mBaseMomentNameTv.setText(data.username);
        mBaseMomentNameTv.setOnClickListener(v -> HelperForStartActivity.
                openOther(HelperForStartActivity.type_other_user, data.userid));
        mTvContentText.setVisibility(TextUtils.isEmpty(data.commenttext) ? View.GONE : View.VISIBLE);
        mTvContentText.setText(data.commenttext);
        mBaseMomentComment.setText(Int2TextUtils.toText(data.replyCount, "w"));
        // TODO: 2018/11/17 如果集合是空的代表是纯文字类型
        List<CommentUrlBean> commentUrlBean = VideoAndFileUtils.getCommentUrlBean(data.commenturl);
        if (commentUrlBean != null && commentUrlBean.size() > 0) {
            boolean isVideo = LikeAndUnlikeUtil.isVideoType(commentUrlBean.get(0).type);
            if (isVideo) {
                videoView.setVisibility(View.VISIBLE);
                nineImageView.setVisibility(View.GONE);
                CommentUrlBean urlBean = commentUrlBean.get(0);
                dealVideo(urlBean.info, urlBean.cover, data.contentid, "1".equals(urlBean.type), null, null);
            } else {
                videoView.setVisibility(View.GONE);
                nineImageView.setVisibility(View.VISIBLE);
                dealNineImage(commentUrlBean, data.contentid);
            }
        } else {
            nineImageView.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
        }


        if (MySpUtils.isMe(data.userid)) {
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
                CommonHttpRequest.getInstance().requestFocus(data.userid,
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

        mBaseMomentShareIv.setOnClickListener(v -> {
            if (callBack != null) {
                callBack.share(data);
            }
        });
        mBaseMomentLike.setSelected(LikeAndUnlikeUtil.isLiked(data.goodstatus));
        //评论点赞数
        mBaseMomentLike.setText(Int2TextUtils.toText(data.commentgood, "w"));
        mBaseMomentLike.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestCommentsLike(data.userid,
                        data.contentid, data.commentid, mBaseMomentLike.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                if (!mBaseMomentLike.isSelected()) {
                                    LikeAndUnlikeUtil.showLike(mBaseMomentLike, 0, 20);
                                }
                                int likeCount = data.commentgood;
                                if (mBaseMomentLike.isSelected()) {
                                    likeCount--;
                                    if (likeCount < 0) {
                                        likeCount = 0;
                                    }
                                } else {
                                    likeCount++;
                                }
                                mBaseMomentLike.setText(Int2TextUtils.toText(likeCount, "w"));
                                mBaseMomentLike.setSelected(!mBaseMomentLike.isSelected());
                                //"0"_未赞未踩 "1"_已赞 "2"_已踩
                                data.goodstatus = mBaseMomentLike.isSelected() ? "1" : "0";
                                data.commentgood = likeCount;
                                EventBusHelp.sendCommendLikeAndUnlike(data);
                            }
                        });
            }
        });
    }

    @Override
    public void justBindCountAndState(CommendItemBean.RowsBean data) {

    }

    private void dealNineImage(List<CommentUrlBean> commentUrlBean, String contentid) {
        if (commentUrlBean == null || commentUrlBean.size() == 0) return;
        cover = commentUrlBean.get(0).cover;
        ArrayList<ImageData> imgList = new ArrayList<>();
        if (commentUrlBean.size() == 1) {
            //因为单图的时候不知道宽高信息
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
                            dealNineLayout(imgList, contentid);

                        }
                    });

        } else {
            for (int i = 0; i < commentUrlBean.size(); i++) {
                ImageData data = new ImageData(commentUrlBean.get(i).info);
                imgList.add(data);
            }
            dealNineLayout(imgList, contentid);
        }
    }
}
