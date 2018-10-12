package com.caotu.duanzhi.utils;

import android.util.Log;

import com.caotu.duanzhi.config.BaseConfig;
import com.vise.log.ViseLog;

/**
 * 三方ViseLog封装工具类
 */

public class Logger {

    public static boolean showLog = BaseConfig.isDebug;
    public static final String Tag = "lwqiu";

    /**
     * 打印任意对象
     *
     * @param msg
     */
    public static void logObject(String msg) {
        ViseLog.i(msg);
    }

    /**
     * 用于打印json
     *
     * @param json
     */
    public static void logJson(String json) {
        ViseLog.json(json);
    }

    /**
     * 存储错误日志信息
     *
     * @param objTag
     * @param objMsg
     */
    public static void e(Object objTag, Object objMsg, Throwable e) {
        if (showLog) {
            Log.e(getStringTag(objTag), getStringMsg(objMsg), e);
        }
    }

    public static void e(Object objTag, Object objMsg) {
        if (showLog) {
            Log.e(getStringTag(objTag), getStringMsg(objMsg));
        }
    }

    /**
     * @param objTag Log的TAG,可以传任意对象
     * @param objMsg 需要打印的Log信息，可以传任意对象
     */
    public static void i(Object objTag, Object objMsg) {
        if (showLog) {
            Log.i(getStringTag(objTag), getStringMsg(objMsg));
        }
    }


    /**
     * 把Object类型的tag转化成String类型的tag
     *
     * @param objTag
     * @return
     */
    public static String getStringTag(Object objTag) {
        String tag;
        if (objTag == null) {
            tag = "null";
        } else if (objTag instanceof String) {
            //如果objTag是string类型的,直接强转
            tag = (String) objTag;
        } else if (objTag instanceof Class<?>) {
            //如果objTag是Class ,获取类名
            tag = ((Class<?>) objTag).getSimpleName();
        } else {
            //如果objTag是对象 ,获取类名
            tag = objTag.getClass().getSimpleName();
        }
        return tag;
    }

    /**
     * 把Object类型的tag转化成String类型的tag
     *
     * @param objMsg
     * @return
     */
    public static String getStringMsg(Object objMsg) {
        String msg;
        if (objMsg == null) {
            msg = "null";
        } else {
            //转化成字符串
            msg = objMsg.toString();
        }
        return msg;
    }
}
