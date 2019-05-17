package com.caotu.duanzhi;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.mine.BaseBigTitleActivity;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.view.CustomRefreshHeader;
import com.hjq.toast.ToastUtils;
import com.lansosdk.videoeditor.LanSoEditor;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.CosXmlSimpleService;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.youngfeng.snake.Snake;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

/**
 * 可优化点:
 * 图片插件压缩 :https://github.com/duking666/ImgCompressPlugin/blob/master/README-zh-rCN.md
 */
public class MyApplication extends Application {
    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.colorAccent, R.color.color_replay_text);//全局设置主题颜色
            return new CustomRefreshHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            //指定为经典Footer，默认是 BallPulseFooter
            return new ClassicsFooter(context).setDrawableSize(20);
        });
    }

    private static MyApplication sInstance;
    private Handler handler;//全局handler
    //    private CosXmlService cosXmlService;
    private HashMap<String, Long> map;
    public static boolean redNotice = false;
    private CosXmlSimpleService service;

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
        // 对Snake进行初始化
        Snake.init(this);
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
     * 文件地址转换,节省流量,首页列表的图片和视频都要替换
     *
     * @param url
     * @return
     */
    public static String buildFileUrl(String url) {
        if (TextUtils.isEmpty(url)) return url;
        if (url.contains("cos.ap-shanghai.myqcloud")) {
            url = url.replace("cos.ap-shanghai.myqcloud", "file.myqcloud");
        }
        if (url.startsWith("https")) {
            url = url.replace("https", "http");
        }
        //图片替换
//        if (url.endsWith(".png")) {
//            url = url.replace(".png", ".small.png");
//        }
//        if (url.endsWith(".jpg")) {
//            url = url.replace(".jpg", ".small.jpg");
//        }
//
//        if (url.endsWith(".jpeg")) {
//            url = url.replace(".jpeg", ".small.jpeg");
//        }
//
//        if (url.endsWith(".gif")) {
//            url = url.replace(".gif", ".small.gif");
//        }

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
                .setRegion(BaseConfig.COS_BUCKET_AREA)
                .setDebuggable(BaseConfig.isDebug)
                .isHttps(true)
                .builder();

        QCloudCredentialProvider Provider = new ShortTimeCredentialProvider(BaseConfig.COS_SID,
                BaseConfig.COS_SKEY, 300);

        //创建 CosXmlService 对象，实现对象存储服务各项操作.
        service = new CosXmlSimpleService(this, serviceConfig, Provider);
    }

    public CosXmlSimpleService getCosXmlService() {
        return service;
    }

    /*=======================================自定义Activity栈   START==========================================*/
    private static final LinkedList<Activity> activities = new LinkedList<>();
    private int resumActivitys = 0;

    /**
     * 判断app是否在前台
     *
     * @return
     */
    public boolean getAppIsBackground() {
        return resumActivitys <= 0;
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
//                if (activities == null || activities.isEmpty()) {
//                    Jzvd.clearSavedProgress(activity, null);
//                }
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
                    //补丁加载完成后APP 退后台重启
//                    if (MySpUtils.getBoolean(MySpUtils.HOTFIX_IS_NEED_RESTART, false)) {
//                        MySpUtils.putBoolean(MySpUtils.HOTFIX_IS_NEED_RESTART, false);
//                        SophixManager.getInstance().killProcessSafely();
//                    }
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

//        // 设置开发设备，默认为false，上传补丁如果下发范围指定为“开发设备”，需要调用此接口来标识开发设备
//        Bugly.setIsDevelopmentDevice(this, BaseConfig.isDebug);

        // 多渠道需求塞入
        try {
            String channel = AnalyticsConfig.getChannel(this);
            Bugly.setAppChannel(this, channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId  调试时，将第三个参数改为true
        Beta.upgradeDialogLayoutId = R.layout.bugly_dialog;
        Beta.initDelay = 2 * 1000;
        Beta.strUpgradeDialogCancelBtn = "";
        // 指定升级弹窗只能在主页弹出
        Beta.canShowUpgradeActs.add(MainActivity.class);
        Bugly.init(this, BaseConfig.buglyId, BaseConfig.isDebug);
    }

    //https://bugly.qq.com/docs/user-guide/instruction-manual-android-hotfix-demo/
//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        // you must install multiDex whatever tinker is installed!
//        MultiDex.install(base);
//        // 安装tinker
//        Beta.installTinker();
//        fix();
//    }

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
        headers.put("APP", BaseConfig.APP_NAME);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // TODO: 2019/1/3 只在测试环境打印log,减少string内存
        if (BaseConfig.isDebug) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(HttpApi.OKGO_TAG);
            //log打印级别，决定了log显示的详细程度
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
            //log颜色级别，决定了log在控制台显示的颜色
            loggingInterceptor.setColorLevel(Level.INFO);
            builder.addInterceptor(loggingInterceptor);
        }
        builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)))
                .connectTimeout(5, TimeUnit.SECONDS) //全局的连接超时时间
                .readTimeout(5, TimeUnit.SECONDS) //全局的读取超时时间
                .writeTimeout(5, TimeUnit.SECONDS); //全局的写入超时时间
        //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        //好处是全局参数统一,特定请求可以特别定制参数
        //方法一：信任所有证书,不安全有风险
            /*
            https://github.com/jeasonlzy/okhttp-OkGo/wiki/Init#%E5%85%A8%E5%B1%80%E9%85%8D%E7%BD%AE
             */
        //定义一个信任所有证书的TrustManager
//        X509TrustManager trustManager = new X509TrustManager() {
//            @Override
//            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//
//            }
//
//            @Override
//            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//
//            }
//
//            @Override
//            public X509Certificate[] getAcceptedIssuers() {
//                return new X509Certificate[0];
//            }
//        };
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
////            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(getAssets().open("geo_global_ca.cer"));
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
//        builder.sslSocketFactory(new SSL(trustManager), trustManager);
        //以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
        OkGo.getInstance().init(this)
                .setOkHttpClient(builder.build())
//                    .setCacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
//                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(0)
                .addCommonHeaders(headers);          //设置全局公共头

    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        GlideUtils.clearMemory();
    }

}
