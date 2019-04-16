package com.caotu.duanzhi;

import com.umeng.analytics.MobclickAgent;

public final class UmengHelper {
    public static void event(String key) {
        MobclickAgent.onEvent(MyApplication.getInstance(), key);
    }

    public static void topicEvent(String id) {
        MobclickAgent.onEvent(MyApplication.getInstance(), "release_".concat(id));
    }
}
