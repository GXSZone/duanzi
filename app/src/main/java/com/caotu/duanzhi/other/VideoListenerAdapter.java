package com.caotu.duanzhi.other;

import com.dueeeke.videoplayer.listener.MyVideoOtherListener;

public class VideoListenerAdapter implements MyVideoOtherListener {
    @Override
    public void share(byte type) {

    }

    @Override
    public void timeToShowWxIcon() {

    }

    @Override
    public void download() {

    }

    @Override
    public void clickTopic() {

    }

    /**
     * 统一埋点处理了
     */
    @Override
    public void mute() {
        UmengHelper.event(UmengStatisticsKeyIds.volume);
    }

    @Override
    public void clickPlay() {
        UmengHelper.event(UmengStatisticsKeyIds.click_play);
    }
}
