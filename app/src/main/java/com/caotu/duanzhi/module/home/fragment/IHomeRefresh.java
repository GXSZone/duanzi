package com.caotu.duanzhi.module.home.fragment;

import com.caotu.duanzhi.module.detail.ILoadMore;

/**
 * 这个接口是为了首页刷新悬浮按钮以及详情获取更多数据
 */
public interface IHomeRefresh {
    void refreshDate();

    void loadMore(ILoadMore iLoadMore);
}
