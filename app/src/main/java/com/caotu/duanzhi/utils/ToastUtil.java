package com.caotu.duanzhi.utils;

import com.hjq.toast.ToastUtils;

/**
 * 谷歌一个toast 都不让人省心
 * java.lang.IllegalStateException
 * View android.widget.TextView has already been added to the window manager.
 * android.view.WindowManagerGlobal.addView(WindowManagerGlobal.java:325)
 */
public class ToastUtil {

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
