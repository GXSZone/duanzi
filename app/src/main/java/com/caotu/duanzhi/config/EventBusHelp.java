package com.caotu.duanzhi.config;


import com.caotu.duanzhi.Http.bean.EventBusObject;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;

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
        EventBusObject eventBusObject = new EventBusObject(EventBusCode.PUBLISH, bean, publishString, null);
        EventBus.getDefault().post(eventBusObject);
    }

    public static void sendPublishEvent(String publishString, String message) {
        EventBusObject eventBusObject = new EventBusObject(EventBusCode.PUBLISH, null, publishString, message);
        EventBus.getDefault().post(eventBusObject);
    }

    public static void sendVideoIsAutoPlay(boolean isTrue) {
        EventBusObject object = new EventBusObject(EventBusCode.VIDEO_PLAY, isTrue, null, null);
        EventBus.getDefault().post(object);
    }

    public static void sendLikeAndUnlike(MomentsDataBean bean) {
        EventBusObject object = new EventBusObject(EventBusCode.DETAIL_CHANGE, bean, null, null);
        EventBus.getDefault().post(object);
    }

    public static void sendNightMode(boolean isNight) {
        EventBusObject object = new EventBusObject(EventBusCode.EYE_MODE, isNight, null, null);
        EventBus.getDefault().post(object);
    }
}
