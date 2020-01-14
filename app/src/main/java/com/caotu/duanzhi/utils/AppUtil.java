package com.caotu.duanzhi.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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


//    /**
//     * 可以完全解耦全局上下文的context,但是需要注意加固后的问题
//     */
//    static {
//        try {
//            Class<?> activityThread = Class.forName("android.app.ActivityThread");
//            Method method = activityThread.getDeclaredMethod("currentApplication");
//            //obj参数设置为null，前提是该方法是静态方法
//            Object current = method.invoke(null);
//            sApplication = (Application) current;
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

    public static String getChannel(Context context) {
        String value = "";
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo != null) {
                value = applicationInfo.metaData.getString("UMENG_CHANNEL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
