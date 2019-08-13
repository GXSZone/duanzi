package weige.umenglib;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.statistics.common.DeviceConfig;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.List;
import java.util.Random;

/**
 * 整个友盟的主入口类
 */
public final class UmengLibHelper {
    private static final UmengLibHelper ourInstance = new UmengLibHelper();
    private static Context sApplication;

    public static UmengLibHelper getInstance() {
        return ourInstance;
    }

    private UmengLibHelper() {

    }

    public String getChannel() {
        return AnalyticsConfig.getChannel(sApplication);
    }

    /**
     * 三方key 的配置直接在这里配置
     *
     * @param application
     * @param isDebug
     */
    public void init(Application application, boolean isDebug) {
        // 打开统计SDK调试模式
        UMConfigure.setLogEnabled(isDebug);
        sApplication = application;
        Config.isJumptoAppStore = true;
        UMConfigure.init(application, UMConfigure.DEVICE_TYPE_PHONE, "");
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        PlatformConfig.setWeixin("wx7e14cd002feb85fa", "ed9439ea1f87bfa95d67e37b025240be");
        PlatformConfig.setSinaWeibo("2683279078", "a39cb78840940f7f913aa06db0da1a21",
                //下面的地址要留意
                "https://sns.whalecloud.com/sina2/callback");
        PlatformConfig.setQQZone("1107865539", "G0CdQzTri8iyp4Cf");
        // TODO: 2019-07-02 不要学我这么干😃,看懂了自己体会就好
        int nextInt = new Random().nextInt(10);
        MobclickAgent.setCatchUncaughtExceptions(nextInt < 5);
    }


    /**
     * https://developer.umeng.com/docs/66632/detail/66849?um_channel=sdk
     */
    public static void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
    }

    public static void onDestroy(Context context) {
        UMShareAPI.get(context).release();
    }

    public static void onSaveInstanceState(Context context, Bundle outState) {
        UMShareAPI.get(context).onSaveInstanceState(outState);
    }

    /**
     * 判断客户端是否安装
     */
    public static boolean isInstall(Activity activity, @ThirdPlatform int shareMedia) {
        return UMShareAPI.get(activity).isInstall(activity,
                SharePlatformTranlate.userTranlateToUmeng(shareMedia));
    }

    public static boolean isQQClientAvailable() {
        PackageManager packageManager = sApplication.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 删除授权
     */
    public static void deleteAuth(Activity activity, SHARE_MEDIA platform, UMAuthListener listener) {
        UMShareAPI.get(sApplication).deleteOauth(activity, platform, listener);
    }

    /**
     * 授权验证
     */
    public static void authVerify(Activity activity, @ThirdPlatform int platform, UMAuthListener listener) {
        UMShareAPI.get(sApplication).doOauthVerify(activity,
                SharePlatformTranlate.userTranlateToUmeng(platform), listener);
    }

    /**
     * 返回是否授权
     */
    public static boolean isAuthorize(Activity activity, SHARE_MEDIA platform) {
        return UMShareAPI.get(sApplication).isAuthorize(activity, platform);
    }

    /**
     * 获取平台信息
     * <p/>
     * 如果未授权, 会拉取授权界面, 再返回数据
     * <p/>
     * 如果已授权, 会直接返回数据
     */
    public static void platLogin(Activity activity, @ThirdPlatform int platform,
                                 AuthCallBack listener) {
        UMShareAPI.get(activity).getPlatformInfo(activity,
                SharePlatformTranlate.userTranlateToUmeng(platform), listener);
    }

    public static void umengEvent(Context context, String key) {
        MobclickAgent.onEvent(context, key);
    }

    public static String[] getTestDeviceInfo(Context context) {
        String[] deviceInfo = new String[2];
        try {
            if (context != null) {
                deviceInfo[0] = DeviceConfig.getDeviceIdForGeneral(context);
                deviceInfo[1] = DeviceConfig.getMac(context);
            }
        } catch (Exception e) {
        }
        return deviceInfo;
    }
}
