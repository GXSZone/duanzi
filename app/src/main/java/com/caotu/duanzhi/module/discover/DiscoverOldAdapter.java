package com.caotu.duanzhi.module.discover;

import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.caotu.duanzhi.Http.bean.DiscoverListBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class DiscoverOldAdapter extends BaseQuickAdapter<DiscoverListBean.RowsBean, BaseViewHolder> {
    public DiscoverOldAdapter() {
        super(R.layout.item_discover_old_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, DiscoverListBean.RowsBean item) {
        ImageView imageView = helper.getView(R.id.iv_topic_image);
        try {
            // TODO: 2019/2/22 防止傻逼手机类型转换异常
            LinearLayout linearLayout = (LinearLayout) imageView.getParent();
            int position = helper.getLayoutPosition();
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) linearLayout.getLayoutParams();
            if (position % 3 == 0) {
                layoutParams.rightMargin = DevicesUtils.dp2px(15);
                layoutParams.leftMargin = DevicesUtils.dp2px(5);
            } else if (position % 3 == 1) {
                layoutParams.leftMargin = DevicesUtils.dp2px(15);
                layoutParams.rightMargin = DevicesUtils.dp2px(5);
            } else {
                layoutParams.leftMargin = DevicesUtils.dp2px(5);
                layoutParams.rightMargin = DevicesUtils.dp2px(5);
            }
            linearLayout.setLayoutParams(layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GlideUtils.loadImage(item.tagimg, R.mipmap.shenlue_logo, imageView);
        helper.setText(R.id.tv_topic_name, item.tagalias);
    }
}
