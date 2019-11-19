package com.caotu.duanzhi.utils;

import android.app.Application;
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

    private AppUtil() {
        throw new AssertionError("No instances.");
    }

    private static Application sApplication;

//    /**
//     * 可以完全解耦全局上下文的context,但是需要注意加固后的问题
//     */
//    static {
//        try {
//            Class<?> activityThread = Class.forName("android.app.ActivityThread");
//            Method m_currentActivityThread = activityThread.getDeclaredMethod("currentActivityThread");
//            Field f_mInitialApplication = activityThread.getDeclaredField("mInitialApplication");
//            f_mInitialApplication.setAccessible(true);
//            Object current = m_currentActivityThread.invoke(null);
//            Object app = f_mInitialApplication.get(current);
//            sApplication = (Application) app;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 获取全局 Application
//     */
//    public static Application getApplication() {
//        return sApplication;
//    }
//
//    /**
//     * 设置全局 Application
//     */
//    public static void setupApplication(@NonNull Application application) {
//        sApplication = application;
//    }
}
