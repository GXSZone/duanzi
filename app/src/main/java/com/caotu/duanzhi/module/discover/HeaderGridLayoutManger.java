package com.caotu.duanzhi.module.discover;

import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;

public class HeaderGridLayoutManger extends GridLayoutManager.SpanSizeLookup {

    private final BaseQuickAdapter adapter;
//    private final GridLayoutManager layoutManager;

    public HeaderGridLayoutManger(BaseQuickAdapter adapter) {
        this.adapter = adapter;
//        this.layoutManager = layoutManager;
    }

    @Override
    public int getSpanSize(int position) {
        int itemCount = adapter.getItemCount();
        int count = 1;
        if (position == 0) {
            count = 3;
        } else if (position == itemCount - 1) {
            count = 3;
        }
        return count;
    }
}
