package com.caotu.duanzhi.other;

import com.caotu.duanzhi.MyApplication;

import weige.umenglib.UmengLibHelper;

public final class UmengHelper {
    public static void event(String key) {
        UmengLibHelper.umengEvent(MyApplication.getInstance(), key);
    }

    public static void topicEvent(String id) {
        UmengLibHelper.umengEvent(MyApplication.getInstance(), "release_".concat(id));
    }

    public static void homeTpicEvent(String id) {
        UmengLibHelper.umengEvent(MyApplication.getInstance(), "home_".concat(id));
    }

    public static void discoverTpicEvent(String id) {
        UmengLibHelper.umengEvent(MyApplication.getInstance(), "discover_".concat(id));
    }
}
