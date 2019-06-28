package com.caotu.duanzhi.module.detail;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

/**
 * 评论列表里文本长按事件
 */
public interface TextViewLongClick {
    void textLongClick(BaseQuickAdapter adapter, View view, int position);
}
