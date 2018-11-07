package com.caotu.duanzhi.config;


import com.caotu.duanzhi.Http.bean.EventBusObject;

import org.greenrobot.eventbus.EventBus;

/**
 * 用于管理登录和退出的通知发送
 */
public class EventBusHelp {


    public static void sendLoginEvent() {
        EventBusObject object = new EventBusObject(EventBusCode.LOGIN, null, null, null);
        EventBus.getDefault().post(object);
    }

    public static void sendLoginOut() {
        EventBusObject object = new EventBusObject(EventBusCode.LOGIN_OUT, null, null, null);
        EventBus.getDefault().post(object);
    }

    public static void sendPublishEvent(String publishString, Object bean) {
        EventBusObject eventBusObject = new EventBusObject(EventBusCode.PUBLISH, null, null, null);
        EventBus.getDefault().post(eventBusObject);
    }
}
