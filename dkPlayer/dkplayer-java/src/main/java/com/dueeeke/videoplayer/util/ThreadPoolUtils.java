package com.dueeeke.videoplayer.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author mac
 * @日期: 2018/11/6
 * @describe TODO
 */
public final class ThreadPoolUtils {

    private static ExecutorService singleThreadExecutor;

    public static void executor(Runnable runnable) {
        if (singleThreadExecutor == null) {
            singleThreadExecutor = Executors.newFixedThreadPool(2);
        }
        singleThreadExecutor.execute(runnable);
    }
}
