package com.caotu.duanzhi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.caotu.duanzhi.Http.MyHttpLog;
import com.caotu.duanzhi.advertisement.ADConfig;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.other.BuglyAdapter;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.lansosdk.videoeditor.LanSoEditor;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.model.HttpHeaders;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.CosXmlSimpleService;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;
import com.yunxia.adsdk.tpadmobsdk.common.AdcdnMobSDK;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import weige.umenglib.UmengLibHelper;

/**
 * activity的监听以及全局的一些初始化操作都放到这里,其他没有影响,改动太多的继续放在原先application里
 */
public class ApplicationContextProvider extends ContentProvider {
    @SuppressLint("StaticFieldLeak")
    public static Context mContext;

    @Override
    public boolean onCreate() {
        mContext = getContext();
        if (mContext instanceof Application) {
            ((Application) mContext).registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    ContextProvider.get().addActivity(activity);

                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    ContextProvider.get().remove(activity);

                }

                /** Unused implementation **/
                @Override
                public void onActivityStarted(Activity activity) {
                    ContextProvider.get().addCount(activity);
//
                }

                @Override
                public void onActivityResumed(Activity activity) {

                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {
                    ContextProvider.get().removeCount(activity);
                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }
            });
        }
        initLansoVideo();
//        initGlobeActivity();
        initBugly();
        initUmeng();
        initJpush();
        initCosXmlService();
        initHttp();
        //https://github.com/getActivity/ToastUtils --------->可以自定义toast
        ToastUtil.initToast((Application) mContext);
        AdcdnMobSDK.instance().initSdk(mContext, ADConfig.ADAPPID_NEW);
//        SmartSwipeBack.activitySlidingBack((Application) mContext, null);
        return false;
    }

    /**
     * 7.2  对应平台没有安装的时候跳转转到应用商店下载
     * 在初始化sdk的时候添加如下代码即可：
     * Config.isJumptoAppStore = true
     * 其中qq 微信会跳转到下载界面进行下载，其他应用会跳到应用商店进行下载
     */
    private void initUmeng() {
        UmengLibHelper.getInstance().init((Application) getContext(), BaseConfig.isDebug);
    }

    private void initJpush() {
        JPushManager.getInstance().initJPush(getContext(), BaseConfig.isDebug);
    }

    private void initLansoVideo() {
        //加载so库,并初始化.
        try {
            LanSoEditor.initSDK(getContext(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化Bugly
     */
    private void initBugly() {

//        // 设置开发设备，默认为false，上传补丁如果下发范围指定为“开发设备”，需要调用此接口来标识开发设备
        Bugly.setIsDevelopmentDevice(getContext(), BaseConfig.isDebug);

        // 多渠道需求塞入
        try {
            String channel = UmengLibHelper.getInstance().getChannel();
            Bugly.setAppChannel(getContext(), channel);
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
        Bugly.init(getContext(), BaseConfig.buglyId, BaseConfig.isDebug);
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
        service = new CosXmlSimpleService(mContext, serviceConfig, provider);
    }

    public static CosXmlSimpleService service;


    private void initHttp() {

        HttpHeaders headers = new HttpHeaders();
        // TODO: 2018/10/31 整体body加密用upstring形式,其他情况用upjson ,切记切记,详细看登录页的请求
        //区别两个APP,用于推荐系统,与接口协商
        headers.put("VER", DevicesUtils.getVerName());
        headers.put("DEV", DevicesUtils.getDeviceName());
        headers.put("APP", BaseConfig.APP_NAME);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.cookieJar(new CookieJarImpl(new SPCookieStore(mContext)))
                .connectTimeout(30, TimeUnit.SECONDS) //全局的连接超时时间
                .readTimeout(30, TimeUnit.SECONDS) //全局的读取超时时间
                .writeTimeout(30, TimeUnit.SECONDS); //全局的写入超时时间
        if (BaseConfig.isDebug) {
            builder.addInterceptor(new MyHttpLog());
        } else {
            builder.proxy(Proxy.NO_PROXY);//代理不生效,防抓包
        }
        //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        //好处是全局参数统一,特定请求可以特别定制参数

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        //以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
        OkGo.getInstance().init((Application) getContext())
                .setOkHttpClient(builder.build())
//                    .setCacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
//                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(0)
                .addCommonHeaders(headers);          //设置全局公共头

    }


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


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
