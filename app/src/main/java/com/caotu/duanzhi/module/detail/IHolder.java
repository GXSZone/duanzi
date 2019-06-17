package com.caotu.duanzhi.module.detail;

import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.module.base.BaseFragment;
import com.dueeeke.videoplayer.player.IjkVideoView;

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

    void bindSameView(TextView mUserName, ImageView userAvatar, TextView mUserIsFollow, TextView bottomLikeView);

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

}
