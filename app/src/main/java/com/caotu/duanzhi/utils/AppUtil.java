package com.caotu.duanzhi.utils;

import java.util.Collection;

public class AppUtil {
    /**
     * 判断集合是否有数据
     *
     * @param collection
     * @return
     */
    public static boolean listHasDate(Collection collection) {
        return collection != null && collection.size() > 0;
    }

    private static long lastClickTime;
    private static final int MINI_DALAY_TIME = 1000;

    public static boolean isFastClick() {
        boolean flag = true;
        long timeMillis = System.currentTimeMillis();
        if ((timeMillis - lastClickTime) >= MINI_DALAY_TIME) {
            flag = false;
        }
        lastClickTime = timeMillis;
        return flag;
    }
}
