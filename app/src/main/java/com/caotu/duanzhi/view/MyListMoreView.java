package com.caotu.duanzhi.view;

import com.caotu.duanzhi.R;
import com.chad.library.adapter.base.loadmore.LoadMoreView;

public class MyListMoreView extends LoadMoreView {
    @Override
    public int getLayoutId() {
        return R.layout.layout_empty_more_view;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.empty_space;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.empty_space;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.empty_space;
    }
}
