package com.caotu.duanzhi.module.holder;

import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.module.download.VideoDownloadHelper;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.other.VideoListenerAdapter;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.view.NineRvHelper;
import com.dueeeke.videoplayer.controller.StandardVideoController;
import com.dueeeke.videoplayer.player.DKVideoView;

public class VideoHeaderHolder extends DetailHeaderViewHolder {
    public VideoHeaderHolder(View parentView) {
        super(parentView);
    }


    @Override
    public void bindDate(MomentsDataBean data) {
        headerBean = data;
        bindUserInfo(data);
        dealFollow(data);
        dealLikeAndUnlike(data);
        dealOther(data);
        //后台有不正常数据
        if (data.imgList == null || data.imgList.size() < 2) return;
        dealVideo(data.imgList.get(1).url, data.imgList.get(0).url,
                data.getContentid(), "1".equals(data.getContenttype()),
                data.getShowtime(), data.getPlaycount(), MySpUtils.isMe(data.getContentuid()));
    }

    public void setVideoView(DKVideoView view) {
        videoView = view;
    }

    @Override
    public void doOtherByChild(StandardVideoController controller, String contentId) {
        controller.setMyVideoOtherListener(new VideoListenerAdapter() {
            @Override
            public void share(byte type) {
                WebShareBean bean = ShareHelper.getInstance().changeContentBean(headerBean,
                        ShareHelper.translationShareType(type), cover, CommonHttpRequest.url);
                ShareHelper.getInstance().shareWeb(bean);
            }

            @Override
            public void clickTopic() {
                NineRvHelper.showReportDialog(contentId, 0);
            }

            @Override
            public void download() {
                VideoDownloadHelper.getInstance().startDownLoad(true, contentId, videoUrl);
            }

            @Override
            public void timeToShowWxIcon() {
                showWxShareIcon(ivGoHot);
            }
        });
    }
}
