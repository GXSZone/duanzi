package com.caotu.duanzhi.module.holder;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.module.download.VideoDownloadHelper;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.other.VideoListenerAdapter;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.NineRvHelper;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.playerui.StandardVideoController;

import java.util.List;

public class CommentVideoHeaderHolder extends CommentDetailHeaderViewHolder {
    public CommentVideoHeaderHolder(View parentView) {
        super(parentView);
    }

    @Override
    public void bindDate(CommendItemBean.RowsBean data) {
        headerBean = data;
        if (userAvatar != null) {
            GlideUtils.loadImage(data.userheadphoto, userAvatar, false);
        }
        if (mUserName != null) {
            mUserName.setText(data.username);
        }
        if (data.isShowContentFrom()) {
            tvGoDetail.setVisibility(View.VISIBLE);
        } else {
            tvGoDetail.setVisibility(View.GONE);
        }
        tvGoDetail.setOnClickListener(v -> HelperForStartActivity.openContentDetail(data.contentid));
        dealFollow(data);
        dealLikeAndUnlike(data);
        mTvContentText.setVisibility(TextUtils.isEmpty(data.commenttext) ? View.GONE : View.VISIBLE);
        mTvContentText.setText(data.commenttext);
        mTvContentText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, MySpUtils.getFloat(MySpUtils.SP_TEXT_SIZE));
        setComment(data.replyCount);
        List<CommentUrlBean> commentUrlBean = VideoAndFileUtils.getCommentUrlBean(data.commenturl);
        if (commentUrlBean == null || commentUrlBean.size() == 0) return;
        CommentUrlBean urlBean = commentUrlBean.get(0);
        dealVideo(urlBean.info, urlBean.cover, data.contentid, "1".equals(urlBean.type),
                null, null,MySpUtils.isMe(data.userid));
    }

    public void setVideoView(IjkVideoView view) {
        videoView = view;
    }

    protected void dealFollow(CommendItemBean.RowsBean dataBean) {
        String userId = dataBean.userid;
        String isFollow = dataBean.getIsfollow();
        if (MySpUtils.isMe(userId)) {
            if (mUserIsFollow != null) {
                mUserIsFollow.setVisibility(View.GONE);
            }
        } else {
            if (mUserIsFollow != null) {
                mUserIsFollow.setVisibility(View.VISIBLE);
            }
        }
        //1关注 0未关注  已经关注状态的不能取消关注
        if (LikeAndUnlikeUtil.isLiked(isFollow)) {
            if (mUserIsFollow != null) {
                mUserIsFollow.setText("已关注");
                mUserIsFollow.setEnabled(false);
            }
        }
    }

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
            public void clickTopic() {
                NineRvHelper.showReportDialog(contentId, 1);
            }

            @Override
            public void download() {
                VideoDownloadHelper.getInstance().startDownLoad(true, contentId, videoUrl);
            }
        });
        autoPlayVideo();
    }
}
