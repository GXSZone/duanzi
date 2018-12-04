package com.caotu.duanzhi.module.discover;

import android.widget.ImageView;

import com.caotu.duanzhi.Http.bean.DiscoverListBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.GlideUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class DiscoverItemAdapter extends BaseQuickAdapter<DiscoverListBean.RowsBean, BaseViewHolder> {
    public DiscoverItemAdapter() {
        super(R.layout.item_discover_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, DiscoverListBean.RowsBean item) {
        ImageView imageView = helper.getView(R.id.iv_topic_image);
        GlideUtils.loadImage(item.tagimg, R.mipmap.image_default, imageView);
        helper.setText(R.id.tv_topic_name, item.tagalias);
    }
}
