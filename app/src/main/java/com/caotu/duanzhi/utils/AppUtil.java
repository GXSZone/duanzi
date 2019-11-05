package com.caotu.duanzhi.utils;

import android.text.TextUtils;

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

    public static boolean isAdType(String type) {
        return TextUtils.equals("6", type);
    }

    public static boolean isWebType(String type) {
        return TextUtils.equals("5", type);
    }

    public static boolean isUserType(String type) {
        return TextUtils.equals("7", type);
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
