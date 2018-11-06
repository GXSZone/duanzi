package com.caotu.duanzhi.module.publish;

import android.widget.ImageView;

import com.caotu.duanzhi.Http.bean.TopicItemBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.GlideUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class TopicAdapter extends BaseQuickAdapter<TopicItemBean, BaseViewHolder> {

    public TopicAdapter() {
        super(R.layout.topic_item_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, TopicItemBean item) {
        helper.setText(R.id.tv_topic_title,item.getTagalias());
        ImageView topicImage = helper.getView(R.id.iv_topic_image);
        GlideUtils.loadImage(item.getTagimg(),topicImage);
    }
}
