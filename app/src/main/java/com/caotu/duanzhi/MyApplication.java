package com.caotu.duanzhi;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.utils.JinRiUIDensity;
import com.caotu.duanzhi.utils.LocalCredentialProvider;
import com.caotu.duanzhi.utils.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.vise.log.ViseLog;
import com.vise.log.inner.ConsoleTree;
import com.vise.log.inner.LogcatTree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class MyApplication extends Application {

    private static MyApplication sInstance;
    private Handler handler;//全局handler

    public static final int MAXREQUESTTIME = 5000;

    public final static long keyDuration = 600; //SecretKey 的有效时间，单位秒
    private final static String COS_APPID = "1256675270";
    private final static String COS_SID = "AKIDhCSSCgutb3FBrHwLyMTLxINCl59xuqvl";
    private final static String COS_SKEY = "nMglbCYfXAYhcIjutgFbjdKn24tVt31u";
    private final static String COS_BUCKET_AREA = "ap-shanghai";
    public final static String COS_BUCKET_NAME = "ctkj-1256675270";

    private static final String buglyId = "81c966dfe6";//配置buglyid
    private CosXmlService cosXmlService;

    @Override
    public void onCreate() {
        super.onCreate();
        //确保以下内容只初始化一次
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        sInstance = this;
        handler = new Handler(Looper.getMainLooper());
        //记住，这个值需要自己根据UI图计算的哦
        JinRiUIDensity.setDensity(this, 375);//375为UI提供设计图的宽度
        initLog();
        initGlobeActivity();
        initBugly();

        initCosXmlService();

    }

    /**
     * 腾讯云存储初始化
     */
    private void initCosXmlService() {
        //创建 CosXmlServiceConfig 对象，根据需要修改默认的配置参数
        CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion(COS_APPID, COS_BUCKET_AREA)
                .setDebuggable(true)
                .builder();

        //创建获取签名类(请参考下面的生成签名示例，或者参考 sdk中提供的ShortTimeCredentialProvider类）
        LocalCredentialProvider localCredentialProvider = new LocalCredentialProvider(COS_SID, COS_SKEY, keyDuration);

        //创建 CosXmlService 对象，实现对象存储服务各项操作.
        Context context = getApplicationContext(); //应用的上下文

        cosXmlService = new CosXmlService(context, serviceConfig, localCredentialProvider);
    }

    /*=======================================自定义Activity栈   START==========================================*/
    private static final LinkedList<Activity> activities = new LinkedList<>();
    private int resumActivitys = 0;

    /**
     * 通过监听activity的变化,自己管理一个Activity栈
     */
    private void initGlobeActivity() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Logger.i("activity", activity.toString());
                activities.addLast(activity);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Logger.i("activity", activity.toString());
                activities.remove(activity);
            }

            /** Unused implementation **/
            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                resumActivitys++;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                resumActivitys--;
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }
        });
    }


    /**
     * 获取栈顶activity,全局可以使用,省去传context的麻烦和空指针的问题
     *
     * @return
     */
    public Activity getRunningActivity() {
        if (activities == null || activities.isEmpty()) {
            return null;
        }
        return activities.getLast();
    }

    /**
     * 判断app是否在前台
     *
     * @return
     */
    public boolean getAppIsForeground() {
        return resumActivitys > 0;
    }

    /**
     * 获取第二个activity
     *
     * @return
     */
    public Activity getLastSecondActivity() {
        if (activities.size() >= 2) {
            return activities.get(activities.size() - 2);
        } else {
            return null;
        }
    }
    /*=======================================自定义Activity栈 END==========================================*/

    /**
     * 初始化Bugly
     */
    private void initBugly() {
        // 获取当前包名
        String packageName = getPackageName();
        // 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        strategy.setBuglyLogUpload(processName == null || processName.equals(packageName));
        // 初始化Bugly
        CrashReport.initCrashReport(this, buglyId, BaseConfig.isDebug, strategy);
    }

    /**
     * 获取applicition对象
     *
     * @return
     */
    public static MyApplication getInstance() {
        return sInstance;
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 全局一个handler用来处理子线程和主线程问题
     *
     * @return
     */
    public Handler getHandler() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        return handler;
    }

    /**
     * 初始LOG
     */
    private void initLog() {
        if (BaseConfig.isDebug) {
            ViseLog.getLogConfig()
                    .configAllowLog(true)//是否输出日志
                    .configShowBorders(true)//是否排版显示
                    .configTagPrefix("DuanZi")//设置标签前缀
                    .configFormatTag("%d{HH:mm:ss:SSS} %t %c{-5}")//个性化设置标签，默认显示包名
                    .configLevel(Log.VERBOSE);//设置日志最小输出级别，默认Log.VERBOSE
            ViseLog.plant(new LogcatTree());//添加打印日志信息到Logcat的树
            ViseLog.plant(new ConsoleTree());
        }
    }
}
