package com.caotu.duanzhi.module.detail_scroll;

import com.caotu.duanzhi.Http.bean.MomentsDataBean;

import java.util.List;

public class BigDateList {
    private static final BigDateList ourInstance = new BigDateList();
    List<MomentsDataBean> beans;

    public static BigDateList getInstance() {
        return ourInstance;
    }

    private BigDateList() {
    }

    public void setBeans(List<MomentsDataBean> beanList) {
        beans = beanList;
    }

    public List<MomentsDataBean> getBeans() {
        return beans;
    }

    public void clearBeans() {
        if (beans != null) {
            beans.clear();
            beans = null;
        }
    }
}
