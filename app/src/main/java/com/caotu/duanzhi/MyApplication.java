package com.caotu.duanzhi;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.mine.BaseBigTitleActivity;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.LocalCredentialProvider;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.view.CustomRefreshHeader;
import com.danikula.videocache.HttpProxyCacheServer;
import com.hjq.toast.ToastUtils;
import com.lansosdk.videoeditor.LanSoEditor;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

public class MyApplication extends Application {
    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.colorAccent, R.color.color_replay_text);//全局设置主题颜色
                return new CustomRefreshHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }
    private static MyApplication sInstance;
    private Handler handler;//全局handler
    private CosXmlService cosXmlService;
    private HashMap<String, Long> map;
    public static boolean redNotice = false;
//    public static int APPFlag = -1;

    public void setMap(HashMap<String, Long> map) {
        if (map == null) {
            this.map = new HashMap<>();
        } else {
            this.map = map;
        }
    }

    public HashMap<String, Long> getMap() {
        if (map == null) {
            this.map = new HashMap<>();
        }
        return map;
    }

    public void putHistory(String contentId) {
        if (map != null && !TextUtils.isEmpty(contentId)) {
            // TODO: 2019/1/15 个人相关页面不记录浏览记录
            if (getRunningActivity() instanceof BaseBigTitleActivity) return;
            map.put(contentId, System.currentTimeMillis());
        }
    }

    /**
     * 获取applicition对象
     *
     * @return
     */
    public static MyApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
//        Stetho.initializeWithDefaults(this);
        //记住，这个值需要自己根据UI图计算的哦
//        JinRiUIDensity.setDensity(this, 375);//375为UI提供设计图的宽度
        initLansoVideo();
        initGlobeActivity();
        initBugly();
        initUmeng();
        initJpush();
        initCosXmlService();
        initHttp();
        //获取分享url
        CommonHttpRequest.getInstance().getShareUrl();
        //https://github.com/getActivity/ToastUtils --------->可以自定义toast
        ToastUtils.init(this);
        //添加emoji表情支持
//        BundledEmojiCompatConfig config = new BundledEmojiCompatConfig(this);
//        EmojiCompat.init(config);
    }


    private void initJpush() {
        JPushManager.getInstance().initJPush(this, BaseConfig.isDebug);
    }

    private void initLansoVideo() {
        //加载so库,并初始化.
        try {
            LanSoEditor.initSDK(getApplicationContext(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 视频缓存
     */
    private HttpProxyCacheServer proxy;

    public HttpProxyCacheServer getProxy() {
        return this.proxy == null ? (proxy = newProxy()) : proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(100 * 1024 * 1024)       // 1 Gb for cache
                //这个缓存有毒,会导致视频播放失败
//                .fileNameGenerator(new MyFileNameGenerator())
                .build();
    }

    /**
     * 文件地址转换,节省流量,首页列表的图片和视频都要替换
     *
     * @param url
     * @return
     */
    public static String buildFileUrl(String url) {
        if (url != null && url.contains("cos.ap-shanghai.myqcloud")) {
            url = url.replace("cos.ap-shanghai.myqcloud", "file.myqcloud");
        }
        return url;
    }

    /**
     * 7.2  对应平台没有安装的时候跳转转到应用商店下载
     * 在初始化sdk的时候添加如下代码即可：
     * Config.isJumptoAppStore = true
     * 其中qq 微信会跳转到下载界面进行下载，其他应用会跳到应用商店进行下载
     */
    private void initUmeng() {
        // 打开统计SDK调试模式
        UMConfigure.setLogEnabled(BaseConfig.isDebug);

        Config.isJumptoAppStore = true;
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "");
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        PlatformConfig.setWeixin("wx7e14cd002feb85fa", "ed9439ea1f87bfa95d67e37b025240be");
        PlatformConfig.setSinaWeibo("2683279078", "a39cb78840940f7f913aa06db0da1a21",
                //下面的地址要留意
                "https://sns.whalecloud.com/sina2/callback");
        PlatformConfig.setQQZone("1107865539", "G0CdQzTri8iyp4Cf");
    }

    /**
     * 腾讯云存储初始化
     */
    private void initCosXmlService() {
        //创建 CosXmlServiceConfig 对象，根据需要修改默认的配置参数
        CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion(BaseConfig.COS_APPID, BaseConfig.COS_BUCKET_AREA)
                .setDebuggable(true)
                .builder();

        //创建获取签名类(请参考下面的生成签名示例，或者参考 sdk中提供的ShortTimeCredentialProvider类）
        LocalCredentialProvider localCredentialProvider = new LocalCredentialProvider(BaseConfig.COS_SID, BaseConfig.COS_SKEY, BaseConfig.keyDuration);

        //创建 CosXmlService 对象，实现对象存储服务各项操作.
        cosXmlService = new CosXmlService(this, serviceConfig, localCredentialProvider);
    }

    public CosXmlService getCosXmlService() {
        return cosXmlService;
    }

    /*=======================================自定义Activity栈   START==========================================*/
    private static final LinkedList<Activity> activities = new LinkedList<>();
    private int resumActivitys = 0;

    /**
     * 判断app是否在前台
     *
     * @return
     */
    public boolean getAppIsForeground() {
        return resumActivitys > 0;
    }

    public Activity getLastSecondActivity() {
        if (activities.size() >= 2) {
            return activities.get(activities.size() - 2);
        } else {
            return null;
        }
    }

    public Activity getBottomActivity() {
        return activities.getFirst();
    }

    /**
     * 通过监听activity的变化,自己管理一个Activity栈
     */
    private void initGlobeActivity() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                //今日头条适配
//                JinRiUIDensity.setDefault(activity);
                activities.addLast(activity);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                activities.remove(activity);
            }

            /** Unused implementation **/
            @Override
            public void onActivityStarted(Activity activity) {
                resumActivitys++;
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                resumActivitys--;
                if (resumActivitys == 0) {
                    //计时器查询
                    if (getBottomActivity() != null && getBottomActivity() instanceof MainActivity) {
                        ((MainActivity) getBottomActivity()).stopHandler();
                    }
                    MySpUtils.putHashMapData(map);
                }
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

    public int getActivitys() {
        return activities == null ? 0 : activities.size();
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
        CrashReport.initCrashReport(this, BaseConfig.buglyId, BaseConfig.isDebug, strategy);
        // TODO: 2019/4/16 获取渠道号
        String channel = AnalyticsConfig.getChannel(this);
        Log.i("channel", "initBugly: " + channel);
        Bugly.setAppChannel(this, channel);
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
        // 调试时，将第三个参数改为true
        Bugly.init(this, BaseConfig.buglyId, BaseConfig.isDebug);
    }

    //https://bugly.qq.com/docs/user-guide/instruction-manual-android-hotfix-demo/
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);
        // 安装tinker
        Beta.installTinker();
        fix();
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


    private void initHttp() {

        HttpHeaders headers = new HttpHeaders();
        // TODO: 2018/10/31 整体body加密用upstring形式,其他情况用upjson ,切记切记,详细看登录页的请求
        //区别两个APP,用于推荐系统,与接口协商
        headers.put("VER", DevicesUtils.getVerName());
        headers.put("DEV", DevicesUtils.getDeviceName());
        //是否是推荐系统
//        headers.put("LOC","PUSH");
        headers.put("APP", BaseConfig.APP_NAME);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(HttpApi.OKGO_TAG);
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // TODO: 2019/1/3 只在测试环境打印log,减少string内存
        if (BaseConfig.isDebug) {
            builder.addInterceptor(loggingInterceptor);
        }
        builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)))
                .connectTimeout(5, TimeUnit.SECONDS) //全局的连接超时时间
                .readTimeout(5, TimeUnit.SECONDS) //全局的读取超时时间
                .writeTimeout(5, TimeUnit.SECONDS); //全局的写入超时时间
        //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        //好处是全局参数统一,特定请求可以特别定制参数

        try {
            //方法一：信任所有证书,不安全有风险
            /*
            https://github.com/jeasonlzy/okhttp-OkGo/wiki/Init#%E5%85%A8%E5%B1%80%E9%85%8D%E7%BD%AE
             */
            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
//            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(getAssets().open("geo_global_ca.cer"));
            builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
            //以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
            OkGo.getInstance().init(this)
                    .setOkHttpClient(builder.build())
//                    .setCacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
//                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                    .setRetryCount(0)
                    .addCommonHeaders(headers);          //设置全局公共头
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        GlideUtils.clearMemory();
    }

    public static void fix() {
        try {
            Class clazz = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");

            Method method = clazz.getSuperclass().getDeclaredMethod("stop");
            method.setAccessible(true);

            Field field = clazz.getDeclaredField("INSTANCE");
            field.setAccessible(true);

            method.invoke(field.get(null));

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
