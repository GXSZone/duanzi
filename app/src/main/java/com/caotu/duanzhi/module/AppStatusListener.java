package com.caotu.duanzhi.module;

import android.os.Bundle;

/**
 * @author dongyk
 * @email dyk@xkeshi.com
 * @date 2017/11/24
 * @discription
 */

public class AppStatusListener {

    private static AppStatusListener appStatusListener;

    public static int sBeKilled = -1;
    public static int sBeAlive = 0;
    /**
     * APP的状态，默认为杀死状态。
     * 在{@link SplashActivity#onCreate(Bundle)}中设置为存活状态，
     * 进程被杀死，则静态变量被重新初始化
     * 在{@link com.caotu.duanzhi.module.base.BaseActivity#onCreate(Bundle)}中检查是否被重置
     */
    public static int sAppstatus = sBeKilled;

    private AppStatusListener() {

    }

    /**
     * {@link SplashActivity}
     * @return
     */
    public static AppStatusListener getInstance(){
        if (appStatusListener == null){
            appStatusListener = new AppStatusListener();
        }
        return appStatusListener;
    }

    public int getAppStatus(){
        return sAppstatus;
    }

    public void setAppStatus(int appstatus) {
        sAppstatus = appstatus;
    }


}
