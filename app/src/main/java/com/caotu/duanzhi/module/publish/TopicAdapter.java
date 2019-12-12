package com.caotu.duanzhi.module.publish;

import android.widget.ImageView;

import com.caotu.duanzhi.Http.bean.TopicItemBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class TopicAdapter extends BaseQuickAdapter<TopicItemBean, BaseViewHolder> {

    public TopicAdapter() {
        super(R.layout.item_search_topic);
    }

    @Override
    protected void convert(BaseViewHolder helper, TopicItemBean item) {
        helper.setText(R.id.tv_topic_title, item.tagalias);
        helper.setText(R.id.tv_user_follow, "选我");
        ImageView topicImage = helper.getView(R.id.iv_topic_image);
        GlideUtils.loadImage(item.tagimg, R.mipmap.shenlue_logo, topicImage);
        helper.setText(R.id.topic_user_num,
                Int2TextUtils.toText(item.activecount).concat("段友参与讨论"));
    }
}
