package com.caotu.duanzhi.module.home.adapter;

import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.AppUtil;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;

/**
 * 内容展示列表,话题详情下的话题标签都不展示
 */

public class PhotoAdapter extends BaseContentAdapter {

    public PhotoAdapter() {
        super(R.layout.item_base_content);
        setMultiTypeDelegate(new MultiTypeDelegate<MomentsDataBean>() {
            @Override
            protected int getItemType(MomentsDataBean momentsDataBean) {
                int type;
                if (AppUtil.isAdType(momentsDataBean.getContenttype())) {
                    type = ITEM_AD_TYPE;
                } else if (momentsDataBean.imgList != null && momentsDataBean.imgList.size() == 1) {
                    type = ITEM_ONLY_ONE_IMAGE;
                } else {
                    type = ITEM_IMAGE_TYPE;
                }
                return type;
            }
        });

        //Step.2
        getMultiTypeDelegate()
                .registerItemType(ITEM_IMAGE_TYPE, R.layout.item_base_content)
                .registerItemType(ITEM_AD_TYPE, R.layout.item_ad_type_content)
                .registerItemType(ITEM_ONLY_ONE_IMAGE, R.layout.item_one_image_content);
    }


    @Override
    public void otherViewBind(BaseViewHolder helper, MomentsDataBean item) {
        dealImages(helper, item);
    }
}
