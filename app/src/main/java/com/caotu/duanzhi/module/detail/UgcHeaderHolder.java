package com.caotu.duanzhi.module.detail;

import android.text.TextUtils;
import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.module.download.VideoDownloadHelper;
import com.caotu.duanzhi.other.ShareHelper;
import com.dueeeke.videoplayer.listener.VideoListenerAdapter;
import com.dueeeke.videoplayer.playerui.StandardVideoController;

/**
 * @author mac
 * @日期: 2018/11/20
 * @describe 头布局的踩需要隐藏, 内容的bean对象, 评论详情头布局的样式
 */
public class UgcHeaderHolder extends DetailHeaderViewHolder {


    public UgcHeaderHolder(View parentView) {
        super(parentView);
    }

    @Override
    public boolean getIsNeedSync() {
        return false;
    }

    @Override
    public void dealTextContent(MomentsDataBean data) {
        //	1可见，0不可见
        mTvContentText.setText("1".equals(data.getIsshowtitle()) ? data.getContenttitle() : "");
        mTvContentText.setVisibility(TextUtils.isEmpty(mTvContentText.getText().toString())
                ? View.GONE : View.VISIBLE);
    }

    @Override
    public void justBindCountAndState(MomentsDataBean data) {

    }

    @Override
    public void doOtherByChild(StandardVideoController controller, String contentId) {
        controller.setMyVideoOtherListener(new VideoListenerAdapter() {
            @Override
            public void share(byte type) {
                WebShareBean bean = ShareHelper.getInstance().changeContentBean(headerBean,
                        ShareHelper.translationShareType(type), cover, CommonHttpRequest.url);
                ShareHelper.getInstance().shareWeb(bean);
            }

            @Override
            public void download() {
                VideoDownloadHelper.getInstance().startDownLoad(true, contentId, videoUrl);
            }
        });
        autoPlayVideo();
    }
}
