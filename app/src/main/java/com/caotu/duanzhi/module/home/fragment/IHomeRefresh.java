package com.caotu.duanzhi.module.home.fragment;

import com.caotu.duanzhi.module.detail.ILoadMore;

public interface IHomeRefresh {
    void refreshDate();

    void loadMore(ILoadMore iLoadMore);
}
