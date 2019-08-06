package com.caotu.duanzhi.module.detail;

import android.text.TextUtils;
import android.view.View;

import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.dueeeke.videoplayer.player.IjkVideoView;

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
        setComment(data.replyCount);
        List<CommentUrlBean> commentUrlBean = VideoAndFileUtils.getCommentUrlBean(data.commenturl);
        if (commentUrlBean == null || commentUrlBean.size() == 0) return;
        CommentUrlBean urlBean = commentUrlBean.get(0);
        dealVideo(urlBean.info, urlBean.cover, data.contentid, "1".equals(urlBean.type), null, null);
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
}
