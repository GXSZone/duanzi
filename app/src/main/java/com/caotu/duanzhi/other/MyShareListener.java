package com.caotu.duanzhi.other;

import android.text.TextUtils;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.utils.ToastUtil;

import weige.umenglib.ShareCallBack;

/**
 * @author mac
 * @日期: 2018/11/13
 * @describe TODO
 */
public class MyShareListener extends ShareCallBack {

    String contentId;
    //默认0,即是内容,1代表评论;
    int type;

    public MyShareListener(String contentId, int contentOrComment) {
        this.contentId = contentId;
        type = contentOrComment;
    }


    @Override
    public void shareStart() {
        if (!TextUtils.isEmpty(contentId)) {
            CommonHttpRequest.getInstance().requestShare(contentId, type);
        }
        if (type == 0) {
            UmengHelper.event(UmengStatisticsKeyIds.content_share);
        } else if (type == 1) {
            UmengHelper.event(UmengStatisticsKeyIds.comment_share);
        }
    }

    @Override
    public void shareError(String message) {
        ToastUtil.showShort(message);
    }

    @Override
    public void shareSuccess() {
        ToastUtil.showShort("分享成功");
    }
}
