package com.caotu.duanzhi.module.detail;

import android.view.View;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.sunfusheng.GlideImageView;

import java.util.List;

/**
 * 发布内容的列表
 */
public class ContentItemAdapter extends BaseQuickAdapter<LocalMedia, BaseViewHolder> {
    public ContentItemAdapter() {
        super(R.layout.item_publish_detail);
    }

    @Override
    protected void convert(BaseViewHolder helper, LocalMedia item) {
        helper.addOnClickListener(R.id.item_publish_normal_delete_iv);
        GlideImageView imageView = helper.getView(R.id.item_publish_normal_giv);
        String url;
        //判断是否是视频
        boolean isVideo = PictureMimeType.isVideo(item.getPictureType());
        if (isVideo) {
            url = item.getPath();
            helper.setGone(R.id.item_publish_normal_play_iv, true);
        } else {
            url = item.getCompressPath();
            helper.setGone(R.id.item_publish_normal_play_iv, false);
        }
        imageView.load(url, R.drawable.image_placeholder);

        //统一处理条目的点击事件
        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isVideo = PictureMimeType.isVideo(item.getPictureType());
                int position = helper.getAdapterPosition();
                List<LocalMedia> data = getData();
                if (isVideo) {
                    PictureSelector.create(MyApplication.getInstance().getRunningActivity())
                            .externalPictureVideo(item.getPath());
                } else {
                    if (DevicesUtils.isOppo()) {
                        PictureSelector.create(MyApplication.getInstance().getRunningActivity())
                                .themeStyle(R.style.picture_default_style).openExternalPreview(position, data);
                    } else {
                        PictureSelector.create(MyApplication.getInstance().getRunningActivity())
                                .themeStyle(R.style.picture_QQ_style).openExternalPreview(position, data);
                    }
                }
            }
        });
    }
}
