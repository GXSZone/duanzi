package com.caotu.duanzhi.utils;

import android.content.Context;

import com.caotu.duanzhi.jpush.JPushManager;

public class NotificationUtil {

    /**
     * 获取通知栏开关状态
     *
     * @return true |false
     */
    public static boolean notificationEnable(Context context) {
        return JPushManager.getInstance().noticeIsOpen(context);
    }

    /**
     * 跳转到权限设置界面
     */
    public static void open(Context context) {
        JPushManager.getInstance().goSetting(context);
    }
}
