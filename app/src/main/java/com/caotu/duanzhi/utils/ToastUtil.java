package com.caotu.duanzhi.utils;

import com.hjq.toast.ToastUtils;

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
