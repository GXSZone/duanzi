package com.caotu.duanzhi;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.mine.BaseBigTitleActivity;
import com.caotu.duanzhi.other.BuglyAdapter;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.view.NewRefreshHeader;
import com.from.view.swipeback.SwipeBackHelper;
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
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.CosXmlSimpleService;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;
import weige.umenglib.UmengLibHelper;

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
            return new NewRefreshHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            //指定为经典Footer，默认是 BallPulseFooter
            return new ClassicsFooter(context).setDrawableSize(20);
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        initLansoVideo();
        initGlobeActivity();
        initBugly();
        initUmeng();
        initJpush();
        initCosXmlService();
        initHttp();
        //https://github.com/getActivity/ToastUtils --------->可以自定义toast
        ToastUtils.init(this);
        SwipeBackHelper.init(this);
    }

    private static MyApplication sInstance;
    /**
     * 全局handler
     */
    private Handler handler;

    private HashMap<String, Long> map;
    public static boolean redNotice = false;
    private CosXmlSimpleService service;

    public void setMap(HashMap<String, Long> map) {
        if (map == null) {
            this.map = new HashMap<>(2 << 5);
        } else {
            this.map = map;
        }
    }

    public HashMap<String, Long> getMap() {
        if (map == null) {
            map = new HashMap<>(2 << 5);
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

    public static MyApplication getInstance() {
        return sInstance;
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
     * 只有图片类型才有这玩意,视频封面没有,这锤子操作也是服气,注释代码看看就好,用不上
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
        UmengLibHelper.getInstance().init(this, BaseConfig.isDebug);
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

        QCloudCredentialProvider provider = new ShortTimeCredentialProvider(BaseConfig.COS_SID,
                BaseConfig.COS_SKEY, 300);

        //创建 CosXmlService 对象，实现对象存储服务各项操作.
        service = new CosXmlSimpleService(this, serviceConfig, provider);
    }

    public CosXmlSimpleService getCosXmlService() {
        return service;
    }

    /*=======================================自定义Activity栈   START==========================================*/
    public static final LinkedList<Activity> activities = new LinkedList<>();
    private int rebusActivists = 0;


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
                activities.addLast(activity);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                activities.remove(activity);
            }

            /** Unused implementation **/
            @Override
            public void onActivityStarted(Activity activity) {
                rebusActivists++;
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                rebusActivists--;
                if (rebusActivists == 0) {
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
     * java.util.NoSuchElementException ,如果为空的话是直接报错的,getLast 这个api
     *
     * @return
     */
    public Activity getRunningActivity() {
        Activity last = null;
        try {
            last = activities.getLast();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return last;
    }

    /*=======================================自定义Activity栈 END==========================================*/

    /**
     * 初始化Bugly
     */
    private void initBugly() {

//        // 设置开发设备，默认为false，上传补丁如果下发范围指定为“开发设备”，需要调用此接口来标识开发设备
        Bugly.setIsDevelopmentDevice(this, BaseConfig.isDebug);

        // 多渠道需求塞入
        try {
            String channel = UmengLibHelper.getInstance().getChannel();
            Bugly.setAppChannel(this, channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId  调试时，将第三个参数改为true
        Beta.upgradeDialogLayoutId = R.layout.bugly_dialog;
        Beta.strUpgradeDialogCancelBtn = "";
        Beta.enableHotfix = false;
        // 指定升级弹窗只能在主页弹出
        Beta.canShowUpgradeActs.add(MainActivity.class);
        Beta.upgradeDialogLifecycleListener = new BuglyAdapter() {
            @Override
            public void onCreate(Context context, View view, UpgradeInfo upgradeInfo) {
                try {
                    ViewGroup parent = (ViewGroup) ((Activity) context).getWindow().getDecorView();
                    ViewGroup parent2 = (ViewGroup) parent.getChildAt(0);
                    ViewGroup childAt = (ViewGroup) parent2.getChildAt(1);
                    View childAt1 = childAt.getChildAt(1);
                    childAt1.setVisibility(View.GONE);
                    TextView beta_upgrade_feature = view.findViewWithTag("beta_upgrade_feature");
                    if (beta_upgrade_feature != null) {
                        beta_upgrade_feature.setMovementMethod(ScrollingMovementMethod.getInstance());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Bugly.init(this, BaseConfig.buglyId, BaseConfig.isDebug);
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
                .connectTimeout(30, TimeUnit.SECONDS) //全局的连接超时时间
                .readTimeout(30, TimeUnit.SECONDS) //全局的读取超时时间
                .writeTimeout(30, TimeUnit.SECONDS); //全局的写入超时时间
        if (!BaseConfig.isDebug) {
            builder.proxy(Proxy.NO_PROXY);//代理不生效,防抓包
        }
        //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        //好处是全局参数统一,特定请求可以特别定制参数

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        //以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
        OkGo.getInstance().init(this)
                .setOkHttpClient(builder.build())
//                    .setCacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
//                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(0)
                .addCommonHeaders(headers);          //设置全局公共头

    }

    /**
     * 控制图片内存问题
     */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        GlideUtils.trimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        GlideUtils.clearMemory();
        System.gc();
    }

    /**
     * 这个bug 之前修改过,注释了就又来了,不能删了
     * #9105 java.util.concurrent.TimeoutException
     * android.content.res.AssetManager.finalize() timed out after 120 seconds
     * <p>
     * android.content.res.AssetManager.destroy(Native Method)
     */
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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        fix();
    }
}
