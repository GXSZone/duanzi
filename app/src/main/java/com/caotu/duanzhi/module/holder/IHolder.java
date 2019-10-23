package com.caotu.duanzhi.module.holder;

import android.widget.TextView;

import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.view.widget.AvatarWithNameLayout;

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

    void bindSameView(AvatarWithNameLayout layout,TextView mUserIsFollow, TextView bottomLikeView);

    boolean isVideo();

    void autoPlayVideo();

    void bindDate(T dataBean);

    String getVideoUrl();

    String getCover();     //分享需要的icon使用记录

    void commentPlus();

    void commentMinus();

}
