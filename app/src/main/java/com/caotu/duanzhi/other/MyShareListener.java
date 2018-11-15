package com.caotu.duanzhi.other;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.utils.ToastUtil;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * @author mac
 * @日期: 2018/11/13
 * @describe TODO
 */
public class MyShareListener implements UMShareListener {

    String contentId;

    public MyShareListener(String contentId) {
        this.contentId = contentId;
    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {

    }

    @Override
    public void onResult(SHARE_MEDIA share_media) {
        ToastUtil.showShort("成功了");
        CommonHttpRequest.getInstance().requestShare(contentId);
    }

    @Override
    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
        ToastUtil.showShort("失败" + throwable.getMessage());
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media) {

    }
}
