package com.caotu.duanzhi.other;

import com.caotu.duanzhi.MyApplication;
import com.umeng.analytics.MobclickAgent;

public final class UmengHelper {
    public static void event(String key) {
        MobclickAgent.onEvent(MyApplication.getInstance(), key);
    }

    public static void topicEvent(String id) {
        MobclickAgent.onEvent(MyApplication.getInstance(), "release_".concat(id));
    }

    public static void homeTpicEvent(String id) {
        MobclickAgent.onEvent(MyApplication.getInstance(), "home_".concat(id));
    }

    public static void discoverTpicEvent(String id) {
        MobclickAgent.onEvent(MyApplication.getInstance(), "discover_".concat(id));
    }
}
