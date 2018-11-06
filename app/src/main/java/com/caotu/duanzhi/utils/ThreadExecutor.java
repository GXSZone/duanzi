package com.caotu.duanzhi.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author mac
 * @日期: 2018/11/6
 * @describe TODO
 */
public class ThreadExecutor {
    private static final ThreadExecutor ourInstance = new ThreadExecutor();
    private static ExecutorService singleThreadExecutor;

    public static ThreadExecutor getInstance() {
        if (singleThreadExecutor == null) {
            singleThreadExecutor = Executors.newSingleThreadExecutor();
        }
        return ourInstance;
    }

    private ThreadExecutor() {
    }

    public void executor(Runnable runnable) {
        singleThreadExecutor.execute(runnable);
    }
}
