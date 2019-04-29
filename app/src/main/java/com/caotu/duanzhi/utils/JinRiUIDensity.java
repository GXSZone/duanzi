package com.caotu.duanzhi.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;

/**
 * 今日头条UI适配
 */
public class JinRiUIDensity {
    private static float appDensity;
    private static float appScaledDensity;
    private static DisplayMetrics appDisplayMetrics;
    /**
     * 用来参照的的width
     */
    private static float WIDTH;

    public static void setDensity(@NonNull final Application application, float width) {
        appDisplayMetrics = application.getResources().getDisplayMetrics();
        WIDTH = width;
//        registerActivityLifecycleCallbacks(application);

        if (appDensity == 0) {
            //初始化的时候赋值
            appDensity = appDisplayMetrics.density;
            appScaledDensity = appDisplayMetrics.scaledDensity;

            //添加字体变化的监听
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    //字体改变后,将appScaledDensity重新赋值
                    if (newConfig != null && newConfig.fontScale > 0) {
                        appScaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {
                }
            });
        }
    }


    public static void setDefault(Activity activity) {
        setAppOrientation(activity);
    }

    /**
     * 通过application中监听activity的创建来动态修改density
     *
     * @param activity
     */
    private static void setAppOrientation(@Nullable Activity activity) {

        float targetDensity = 0;
        try {
            targetDensity = appDisplayMetrics.widthPixels / WIDTH;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        float targetScaledDensity = targetDensity * (appScaledDensity / appDensity);
        int targetDensityDpi = (int) (160 * targetDensity);
        /**
         *
         * 最后在这里将修改过后的值赋给系统参数
         *
         * 只修改Activity的density值
         */
        DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
        activityDisplayMetrics.densityDpi = targetDensityDpi;
    }

}
