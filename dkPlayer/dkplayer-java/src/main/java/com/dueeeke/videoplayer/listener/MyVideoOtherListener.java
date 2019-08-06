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

    void clickTopic(); //留给后面视频播放完成有相关话题的按钮点击,目前没用到,只在全屏当做更多更多举报按钮的回调

    void mute();
}
