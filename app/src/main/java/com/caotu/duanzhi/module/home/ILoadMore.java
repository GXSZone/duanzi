package com.caotu.duanzhi.module.home;

import com.caotu.duanzhi.Http.bean.MomentsDataBean;

import java.util.List;

public interface ILoadMore {
    void loadMoreDate(List<MomentsDataBean> beanList);
}
