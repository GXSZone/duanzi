package com.caotu.duanzhi.module.detail_scroll;

import android.view.View;

import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.module.detail.DetailHeaderViewHolder;
import com.dueeeke.videoplayer.player.IjkVideoView;

public class VideoHeaderHolder extends DetailHeaderViewHolder {
    public VideoHeaderHolder(View parentView) {
        super(parentView);
    }

    @Override
    public void bindDate(MomentsDataBean data) {
        headerBean = data;

        dealLikeAndUnlike(data);
        dealTextContent(data);
        setComment(data.getContentcomment());

        dealVideo(data.imgList.get(1).url, data.imgList.get(0).url,
                data.getContentid(), "1".equals(data.getContenttype()),
                data.getShowtime(), data.getPlaycount());
    }

    public void setVideoView(IjkVideoView view) {
        videoView = view;
    }

//    public void dealVideo(String videoPath, String videoCover, String contentId,
//                          boolean isLandscapeVideo, String time, String playCount) {
//        isVideo = true;
//        landscape = isLandscapeVideo;
//        cover = videoCover;
//        videoUrl = MyApplication.buildFileUrl(videoPath);
//        videoView.setUrl(videoUrl); //设置视频地址
//        controller = new StandardVideoController(videoView.getContext());
//        GlideUtils.loadImage(cover, controller.getThumb());
////
//
//        // TODO: 2019-05-31 这里就不再写自动重播的弹窗逻辑了,没意思,硬要的话拷贝 BaseContentAdapter 代码
//        boolean videoMode = MySpUtils.getBoolean(MySpUtils.SP_VIDEO_AUTO_REPLAY, false);
//        videoView.setLooping(videoMode);
//
//        VideoAndFileUtils.setVideoWH(videoView, isLandscapeVideo);
//        videoView.setVideoController(controller); //设置控制器，如需定制可继承BaseVideoController
//        videoView.addToVideoViewManager();
//        //保存播放进度
//        videoView.setProgressManager(new ProgressManagerImpl());
//        videoView.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
//            @Override
//            public void onPlayerStateChanged(int playerState) {
//                Activity runningActivity = MyApplication.getInstance().getRunningActivity();
//                if (runningActivity instanceof BaseSwipeActivity) {
//                    ((BaseSwipeActivity) runningActivity)
//                            .setCanSwipe(BaseIjkVideoView.PLAYER_FULL_SCREEN != playerState);
//                }
//                if (playerState == BaseIjkVideoView.PLAYER_FULL_SCREEN) {
//                    UmengHelper.event(UmengStatisticsKeyIds.fullscreen);
//                }
//            }
//
//            @Override
//            public void onPlayStateChanged(int playState) {
//
//            }
//        });
//        controller.setVideoInfo(time, playCount);
//        doOtherByChild(controller, contentId);
//    }
}
