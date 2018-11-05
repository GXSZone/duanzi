package com.caotu.duanzhi.module.publish;

import android.support.annotation.Nullable;

import com.caotu.duanzhi.Http.bean.ThemeBean;
import com.caotu.duanzhi.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class TopicAdapter extends BaseQuickAdapter<ThemeBean, BaseViewHolder> {

    public TopicAdapter(int layoutResId, @Nullable List<ThemeBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ThemeBean item) {
        helper.setText(R.id.tv_topic_title, item.getThemeName());
    }
}
