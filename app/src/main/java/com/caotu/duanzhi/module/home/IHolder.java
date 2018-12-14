package com.caotu.duanzhi.module.home;

import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;

/**
 * @author mac
 * @日期: 2018/11/20
 * @describe TODO
 */
public interface IHolder {
    boolean isVideo();

    MyVideoPlayerStandard getVideoView();

    boolean isLandscape();

    void autoPlayVideo();

    void bindDate(MomentsDataBean dataBean);

    void justBindCountAndState(MomentsDataBean data);

    String getVideoUrl();

    String getCover();

    void commentPlus();

    void commentMinus();

    int headerViewHeight();

    void setCallBack(ShareCallBack callBack);

    interface ShareCallBack {
        void share(MomentsDataBean bean);
    }
}
