package com.caotu.duanzhi.module.holder;

import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.download.VideoDownloadHelper;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.other.VideoListenerAdapter;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.NineRvHelper;
import com.dueeeke.videoplayer.controller.StandardVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;

import java.util.List;

/**
 * 后期可以考虑和内容详情的头布局分开
 */
public class CommentVideoHeaderHolder extends CommentDetailHeaderViewHolder {
    public CommentVideoHeaderHolder(View parentView) {
        super(parentView);
        View topicView = parentView.findViewById(R.id.tv_topic);
        topicView.setVisibility(View.GONE);
        View adView = parentView.findViewById(R.id.header_ad);
        adView.setVisibility(View.GONE);
    }

    /**
     * 视频类型的holder绑定数据逻辑跟图文的不一样,内容和评论又是一样的
     * @param data
     */
    @Override
    public void bindDate(CommendItemBean.RowsBean data) {
        headerBean = data;
        bindUserInfo(data);
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
