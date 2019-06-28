package com.caotu.duanzhi.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewNoBugLinearLayoutManager extends LinearLayoutManager {
    public RecyclerViewNoBugLinearLayoutManager(Context context) {
        super(context);
    }

    public RecyclerViewNoBugLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public RecyclerViewNoBugLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            //try catch一下
            super.onLayoutChildren(recycler, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * 为了解决该问题
     *  java.lang.IllegalArgumentException
     * Called attach on a child which is not detached:
     * ViewHolder{a0c6591 position=1 id=-1, oldPos=-1, pLpos:-1} android.support.v7.widget.RecyclerView{d5f9cf6 VFED..... ......ID 0,0-1080,1688 #7f0901f9 app:id/rv_content},
     * adapter:com.caotu.duanzhi.module.detail.DetailCommentAdapter@fccf8f7, layout:com.caotu.duanzhi.view.RecyclerViewNoBugLinearLayoutManager@42b7364,
     * context:com.caotu.duanzhi.module.detail_scroll.ContentScrollDetailActivity@5858f2b
     *
     * com.scwang.smartrefresh.layout.SmartRefreshLayout.onLayout(SmartRefreshLayout.java:684)
     * @param dx
     * @param dy
     * @param state
     * @param layoutPrefetchRegistry
     */
    @Override
    public void collectAdjacentPrefetchPositions(int dx, int dy, RecyclerView.State state, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        try {
            super.collectAdjacentPrefetchPositions(dx, dy, state, layoutPrefetchRegistry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
