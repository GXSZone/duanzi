package com.caotu.duanzhi.module.home.adapter;

import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * 内容展示列表,话题详情下的话题标签都不展示
 */

public class VideoAdapter extends BaseContentAdapter {

    public VideoAdapter() {
        super(R.layout.item_video_content);
    }

    @Override
    public void otherViewBind(BaseViewHolder helper, MomentsDataBean item) {
        dealVideo(helper, item);
    }
}
