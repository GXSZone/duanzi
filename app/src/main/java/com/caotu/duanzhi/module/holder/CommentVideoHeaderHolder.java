package com.caotu.duanzhi.module.holder;

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
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.NineRvHelper;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.controller.StandardVideoController;

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
        setHeaderText(data);
        setComment(data.replyCount);
        List<CommentUrlBean> commentUrlBean = VideoAndFileUtils.getCommentUrlBean(data.commenturl);
        if (commentUrlBean == null || commentUrlBean.size() == 0) return;
        CommentUrlBean urlBean = commentUrlBean.get(0);
        dealVideo(urlBean.info, urlBean.cover, data.contentid, "1".equals(urlBean.type),
                null, null, MySpUtils.isMe(data.userid));
    }

    public void setVideoView(IjkVideoView view) {
        videoView = view;
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
