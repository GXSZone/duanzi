package com.dueeeke.videoplayer.listener;

/**
 * 该回调用于业务所需: 下载,分享,播放到50秒显示微信图标
 */
public interface MyVideoOtherListener {
    byte weixin = 10;
    byte qyq = 11;
    byte qq = 12;
    byte qqzone = 13;

    void share(byte type);

    void timeToShowWxIcon();

    void download();

    void clickTopic();
}
