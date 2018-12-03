package com.caotu.duanzhi.module;

import com.caotu.duanzhi.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sunfusheng.widget.ImageCell;
import com.sunfusheng.widget.ImageData;

import java.util.List;

public class NineAdapter extends BaseQuickAdapter<ImageData, BaseViewHolder> {
    private int textColor;
    private int textSize;
    private int placeholderResId;
    private int errorResId;

    public NineAdapter(List<ImageData> list) {
        super(R.layout.item_nine_image, list);
        textColor = com.sunfusheng.glideimageview.R.color.nine_image_text_color;
        textSize = 20;
        placeholderResId = com.sunfusheng.glideimageview.R.mipmap.shenlue_logo;
        errorResId = com.sunfusheng.glideimageview.R.mipmap.image_default;
    }

    @Override
    protected void convert(BaseViewHolder helper, ImageData item) {
        ImageCell imageCell = helper.getView(R.id.nine_image_child);
        imageCell.setLoadGif(false)
                .setTextColor(textColor)
                .setTextSize(textSize)
                .placeholder(placeholderResId)
                .error(errorResId)
                .setRadius(0)
                .setData(item);
    }
}
