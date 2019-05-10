package com.dueeeke.videoplayer.listener;

public interface MyVideoOtherListener {
    byte weixin = 10;
    byte qyq = 11;
    byte qq = 12;
    byte qqzone = 13;

    void share(byte type);

    void timeToShowWxIcon();

    void download();
}
