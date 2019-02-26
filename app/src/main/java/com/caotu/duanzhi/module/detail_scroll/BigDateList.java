package com.caotu.duanzhi.module.detail_scroll;

import com.caotu.duanzhi.Http.bean.MomentsDataBean;

import java.util.ArrayList;

public class BigDateList {
    private static final BigDateList ourInstance = new BigDateList();
    ArrayList<MomentsDataBean> beans;

    public static BigDateList getInstance() {
        return ourInstance;
    }

    private BigDateList() {
    }

    public void setBeans(ArrayList<MomentsDataBean> beanList) {
        beans = beanList;
    }

    public ArrayList<MomentsDataBean> getBeans() {
        return beans;
    }

    public void clearBeans() {
        if (beans != null) {
            beans.clear();
            beans = null;
        }
    }
}
