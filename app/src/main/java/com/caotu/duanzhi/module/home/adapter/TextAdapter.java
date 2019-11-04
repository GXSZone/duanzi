package com.caotu.duanzhi.module.home.adapter;

import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.AppUtil;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;

/**
 * 内容展示列表,话题详情下的话题标签都不展示
 */

public class TextAdapter extends BaseContentAdapter {

    public TextAdapter() {
        super(R.layout.item_just_text);
        setMultiTypeDelegate(new MultiTypeDelegate<MomentsDataBean>() {
            @Override
            protected int getItemType(MomentsDataBean momentsDataBean) {
                int type;
                if (AppUtil.isAdType(momentsDataBean.getContenttype()) || momentsDataBean.adView != null) {
                    type = ITEM_AD_TYPE;
                } else {
                    type = ITEM_ONLY_ONE_IMAGE;
                }
                return type;
            }
        });

        //Step.2
        getMultiTypeDelegate()
                .registerItemType(ITEM_ONLY_ONE_IMAGE, R.layout.item_just_text)
                .registerItemType(ITEM_AD_TYPE, R.layout.item_ad_type_content);
    }

    /**
     * 啥都不需要处理,父类已经都处理了
     * @param helper
     * @param item
     */
    @Override
    public void otherViewBind(BaseViewHolder helper, MomentsDataBean item) {

    }
}
