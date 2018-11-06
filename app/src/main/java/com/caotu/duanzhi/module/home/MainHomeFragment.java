package com.caotu.duanzhi.module.home;

import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.publish.TopicAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;

public class MainHomeFragment extends BaseStateFragment<MomentsDataBean> {

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new TopicAdapter();
    }


    @Override
    protected void getNetWorkDate(int load_more) {

    }
}
