package com.caotu.duanzhi.module.detail;

import com.dueeeke.videoplayer.player.IjkVideoView;

/**
 * @author mac
 * @日期: 2018/11/20
 * @describe TODO
 */
public interface IHolder<T> {
    boolean isVideo();

    IjkVideoView getVideoView();

    boolean isLandscape();

    void autoPlayVideo();

    void bindDate(T dataBean);

    void justBindCountAndState(T data);

    String getVideoUrl();

    String getCover();

    void commentPlus();

    void commentMinus();

    void setCallBack(ShareCallBack<T> callBack);

    interface ShareCallBack<T> {
        void share(T bean);
    }
}
