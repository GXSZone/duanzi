package com.caotu.duanzhi.utils;

import android.app.Application;

import com.hjq.toast.ToastUtils;

/**
 * 谷歌一个toast 都不让人省心
 * java.lang.IllegalStateException
 * View android.widget.TextView has already been added to the window manager.
 * android.view.WindowManagerGlobal.addView(WindowManagerGlobal.java:325)
 */
public class ToastUtil {
    public static void initToast(Application application) {
        ToastUtils.init(application);
//        ToastUtils.init(application, new BaseToastStyle(application) {
//            @Override
//            public int getCornerRadius() {
//                return dp2px(10);
//            }
//
//            @Override
//            public int getBackgroundColor() {
//                return DevicesUtils.getColor(R.color.color_one_pressed);
//            }
//
//            @Override
//            public int getTextColor() {
//                return DevicesUtils.getColor(R.color.white);
//            }
//
//            @Override
//            public float getTextSize() {
//                return sp2px(14);
//            }
//
//            @Override
//            public int getPaddingStart() {
//                return dp2px(24);
//            }
//
//            @Override
//            public int getPaddingTop() {
//                return dp2px(16);
//            }
//        });
    }

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public static void showShort(CharSequence message) {
        try {
            ToastUtils.show(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 短时间显示Toast
     *
     * @param resId 资源ID:getResources().getString(R.string.xxxxxx);
     */
    public static void showShort(int resId) {
        try {
            ToastUtils.show(resId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
