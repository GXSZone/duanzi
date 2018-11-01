package com.caotu.duanzhi.module.mine;

import android.support.annotation.Nullable;

import com.caotu.duanzhi.Http.bean.TopicItemBean;
import com.caotu.duanzhi.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class FocusAdapter extends BaseQuickAdapter<TopicItemBean, BaseViewHolder> {

    public FocusAdapter(@Nullable List<TopicItemBean> data) {
        super(R.layout.focus_item_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TopicItemBean item) {
        helper.setText(R.id.tv_topic_title, item.getTitle());
    }
}
