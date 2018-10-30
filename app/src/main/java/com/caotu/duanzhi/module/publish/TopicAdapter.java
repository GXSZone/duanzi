package com.caotu.duanzhi.module.publish;

import android.support.annotation.Nullable;

import com.caotu.duanzhi.Http.bean.TopicItemBean;
import com.caotu.duanzhi.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class TopicAdapter extends BaseQuickAdapter<TopicItemBean, BaseViewHolder> {

    public TopicAdapter(int layoutResId, @Nullable List<TopicItemBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TopicItemBean item) {
        helper.setText(R.id.tv_topic_title, item.getTitle());
    }
}
