package com.caotu.duanzhi.module.home.adapter;

import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * 内容展示列表,话题详情下的话题标签都不展示
 */

public class TextAdapter extends BaseContentAdapter {

    public TextAdapter() {
        super(R.layout.item_just_text);
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
