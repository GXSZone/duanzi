package com.caotu.duanzhi.module.detail_scroll;

import com.caotu.duanzhi.module.detail.ILoadMore;

/**
 * 接口隔离,不需要直接依赖具体对象
 */
public interface DetailGetLoadMoreDate {
    void getLoadMoreDate(ILoadMore callback);
}
