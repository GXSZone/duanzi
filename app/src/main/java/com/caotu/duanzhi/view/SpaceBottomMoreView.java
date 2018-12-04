package com.caotu.duanzhi.view;

import com.caotu.duanzhi.R;
import com.chad.library.adapter.base.loadmore.LoadMoreView;

/**
 * 为了底部不被遮挡,增加底部空间
 */
public class SpaceBottomMoreView extends LoadMoreView {
    @Override
    public int getLayoutId() {
        return R.layout.space_load_more;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }
}
