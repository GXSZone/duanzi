package com.caotu.duanzhi.config;


import android.app.Activity;

import com.caotu.duanzhi.Http.bean.EventBusObject;
import com.caotu.duanzhi.MyApplication;

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

    public static void sendVideoIsAutoPlay() {
        EventBusObject object = new EventBusObject(EventBusCode.VIDEO_PLAY, null, null, null);
        EventBus.getDefault().post(object);
    }

    public static void sendRefreshNotice() {
        EventBusObject object = new EventBusObject(EventBusCode.NOTICE_REFRESH, null, null, null);
        EventBus.getDefault().post(object);
    }

    public static void sendNightMode(boolean isNight) {
        EventBusObject object = new EventBusObject(EventBusCode.EYE_MODE, isNight, null, null);
        EventBus.getDefault().post(object);
    }

    public static void sendPagerPosition(int position) {
        Activity lastSecondActivity = MyApplication.getInstance().getLastSecondActivity();
        String tag = lastSecondActivity != null ? lastSecondActivity.getLocalClassName() : "";
        EventBusObject object = new EventBusObject(EventBusCode.DETAIL_PAGE_POSITION, position, null, tag);
        EventBus.getDefault().post(object);
    }
}
