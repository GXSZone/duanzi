package com.caotu.duanzhi.module.detail_scroll;

import com.caotu.duanzhi.module.home.ContentDetailFragment;
import com.caotu.duanzhi.view.widget.StateView;

import cn.jzvd.Jzvd;

public class ScrollDetailFragment extends ContentDetailFragment {
    //只处理懒加载问题
    @Override
    public boolean isNeedLazyLoadDate() {
        return true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            Jzvd.releaseAllVideos();
        }
    }

    @Override
    protected void initHeader() {
        super.initHeader();
        mStatesView.setCurrentState(StateView.STATE_CONTENT);
    }
}
