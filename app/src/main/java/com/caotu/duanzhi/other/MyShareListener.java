package com.caotu.duanzhi.other;

import android.text.TextUtils;

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
    int type;

    public MyShareListener(String contentId, int contentOrComment) {
        this.contentId = contentId;
        type = contentOrComment;
    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {
        //该判断除了严谨之外也是为了单图分享没有contentId,分享回调有问题
        if (!TextUtils.isEmpty(contentId)) {
            CommonHttpRequest.getInstance().requestShare(contentId, type);
        }
    }

    @Override
    public void onResult(SHARE_MEDIA share_media) {
//        ToastUtil.showShort("成功了");
    }

    @Override
    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
        ToastUtil.showShort("失败" + throwable.getMessage());
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media) {

    }
}
