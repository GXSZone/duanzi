package com.caotu.duanzhi.module.detail;

import com.caotu.duanzhi.module.base.BaseFragment;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.playerui.StandardVideoController;

/**
 * @author mac
 * @日期: 2018/11/20
 * @describe TODO
 */
public interface IHolder<T> {
    /**
     * 该方法为了拿到fragment对象用来判断自动播放的问题
     *
     * @param fragment
     */
    void bindFragment(BaseFragment fragment);

    StandardVideoController getVideoControll();

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
