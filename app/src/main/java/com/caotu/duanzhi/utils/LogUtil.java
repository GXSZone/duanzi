package com.caotu.duanzhi.utils;

import com.caotu.duanzhi.config.BaseConfig;
import com.orhanobut.logger.Logger;


/**
 * 三方ViseLog封装工具类
 */

public class LogUtil {

    public static boolean showLog = BaseConfig.isDebug;
    public static final String Tag = "lwqiu";

    /**
     * 打印任意对象
     */
    public static void logObject(Object object) {
        if (showLog) {
            Logger.d(object);
        }
    }

    /**
     * 用于打印json
     *
     * @param json
     */
    public static void logJson(String json) {
        if (showLog) {
            Logger.json(json);
        }
    }

    public static void logString(String msg) {
        if (showLog) {
            Logger.i(Tag, msg);
        }
    }

    public static void logXml(String xml) {
        if (showLog) {
            Logger.xml(xml);
        }
    }

}
