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
}
