package com.caotu.duanzhi.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.ViewConfiguration;
import android.widget.EditText;

import com.caotu.duanzhi.MyApplication;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/6/15 17:25
 */
public class DevicesUtils {

    /**
     * 网络判断参数
     */
    private static boolean flag = false;

    private static DisplayMetrics displayMetrics = null;

    /**
     * 可以当做判断手机机型的方法
     *
     * @return
     */
    public static boolean isNeedDelay() {
        String manufacturer = Build.MANUFACTURER;
        //这个字符串可以自己定义,例如判断华为就填写huawei,魅族就填写meizu
        if ("huawei".equalsIgnoreCase(manufacturer) || "meizu".equalsIgnoreCase(manufacturer)) {
            return true;
        }
        return false;
    }

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean judgeWhetherNet(Context context) {
        flag = NetWorkUtils.isMobileConnected(context);//判断WIFI网络是否可用
        if (!flag) {//如果不可用，继续 判断是否有网络连接
            flag = NetWorkUtils.isNetworkConnected(context);//判断是否有网络连接
        }
        return flag;
    }

    public static Drawable getDrawable(@DrawableRes int drawsource) {
        return MyApplication.getInstance().getResources().getDrawable(drawsource);
    }

    public static int getColor(@ColorRes int id) {
        return MyApplication.getInstance().getResources().getColor(id);
    }

    public static String getString(@StringRes int id) {
        return MyApplication.getInstance().getResources().getString(id);
    }


    /**
     * 获取当前屏幕宽度(px)
     *
     * @return
     */
    public static int getSrecchWidth() {
        if (displayMetrics == null) {
            displayMetrics = MyApplication.getInstance().getResources().getDisplayMetrics();
        }
        return displayMetrics.widthPixels;
    }

    /**
     * 获取当前屏幕高度(px)
     *
     * @return
     */
    public static int getScreenHeight() {
        if (displayMetrics == null) {
            displayMetrics = MyApplication.getInstance().getResources().getDisplayMetrics();
        }
        return displayMetrics.heightPixels;
    }

    /**
     * 获取设备的状态栏高度(px)
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        try {
            //获取status_bar_height资源的ID . 可能报NullPointerException
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                //根据资源ID获取响应的尺寸值
                return context.getResources().getDimensionPixelSize(resourceId);
            }
            return -1;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                MyApplication.getInstance().getResources().getDisplayMetrics());
    }

    public static int dp2px(float dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                MyApplication.getInstance().getResources().getDisplayMetrics());
    }

    public static int sp2px(int sp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, sp,
                MyApplication.getInstance().getResources().getDisplayMetrics());
    }


    /**
     * 获取当前本地apk的版本
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本号名称
     *
     * @return
     */
    public static String getVerName() {
        String verName = "";
        try {
            verName = MyApplication.getInstance().getPackageManager().
                    getPackageInfo(MyApplication.getInstance().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    //判断系统是否设置了默认浏览器     //如果info.activityInfo.packageName为android,则没有设置,否则,有默认的程序.
    public static boolean hasPreferredApplication(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        ResolveInfo info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return !"android".equals(info.activityInfo.packageName);
    }

    protected static final String PREFS_FILE = "device_id.xml";
    protected static final String PREFS_DEVICE_ID = "device_id";
    protected static volatile UUID uuid;

    /**
     * 这个方法获取设备唯一识别码靠谱,串联的值比较多
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {

        if (uuid == null) {
            final SharedPreferences prefs = context
                    .getSharedPreferences(PREFS_FILE, 0);
            final String id = prefs.getString(PREFS_DEVICE_ID, null);
            if (id != null) {
                uuid = UUID.fromString(id);
            } else {
                final String androidId = Settings.Secure.getString(
                        context.getContentResolver(), Settings.Secure.ANDROID_ID);
                try {
                    if (!"9774d56d682e549c".equals(androidId)) {
                        uuid = UUID.nameUUIDFromBytes(androidId
                                .getBytes("utf8"));
                    } else {

                        uuid = UUID.randomUUID();
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        return uuid.toString();
    }


    public static boolean isToday(long inputJudgeDate) {
        //获取当前系统时间
        if (inputJudgeDate == 1) {
            return false;
        }
        Date nowDate = new Date(System.currentTimeMillis());
        Date recordDate = new Date(inputJudgeDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = dateFormat.format(nowDate);
        String subDate = format.substring(0, 10);
        //定义每天的24h时间范围
        String beginTime = subDate + " 00:00:00";
        String endTime = subDate + " 23:59:59";

        try {
            Date paseBeginTime = dateFormat.parse(beginTime);
            Date paseEndTime = dateFormat.parse(endTime);
            if (recordDate.after(paseBeginTime) && recordDate.before(paseEndTime)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * 获取手机号码
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getNativePhoneNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String nativePhoneNumber = "";
        try {
            nativePhoneNumber = telephonyManager.getLine1Number();
            if (nativePhoneNumber.startsWith("+86")) {
                nativePhoneNumber = nativePhoneNumber.substring(3);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nativePhoneNumber;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    public static String getDeviceName() {
        return getDeviceBrand() + ":" + getSystemModel();
    }


    /**
     * 检查有没有安装权限
     *
     * @param installPermissionCallBack
     */
    public static void checkInstallPermission(Activity activity, InstallPermissionCallBack installPermissionCallBack) {
        if (activity == null) return;
        if (hasInstallPermission()) {
            if (installPermissionCallBack != null) {
                installPermissionCallBack.onGranted();
            }
        } else {
            openInstallPermissionSetting(activity, installPermissionCallBack);
        }
    }


    /**
     * 判断有没有安装权限
     *
     * @return
     */
    public static boolean hasInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //先获取是否有安装未知来源应用的权限
            return MyApplication.getInstance().getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    /**
     * 去打开安装权限的页面
     *
     * @param activity
     * @param installPermissionCallBack
     */
    public static void openInstallPermissionSetting(Activity activity, final InstallPermissionCallBack installPermissionCallBack) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (activity == null) return;
            Uri packageURI = Uri.parse("package:" + activity.getPackageName());
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
            activity.startActivity(intent);
        } else {
            //用户授权了
            if (installPermissionCallBack != null) {
                installPermissionCallBack.onGranted();
            }
        }
    }

    /**
     * 8.0权限检查回调监听
     */
    public interface InstallPermissionCallBack {
        void onGranted();
//
//        void onDenied();
    }

    /**
     * 安装APK
     *
     * @param context
     * @param apkPath
     */
    public static void installApk(Context context, String apkPath) {
        if (context == null || TextUtils.isEmpty(apkPath)) {
            return;
        }
        File file = new File(apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);

        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            //provider authorities
            Uri apkUri = FileProvider.getUriForFile(context, "com.caotu.duanzhi.FileProvider", file);
            //Granting Temporary Permissions to a URI
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    /**
     * EditText竖直方向是否可以滚动
     *
     * @param editText 需要判断的EditText
     * @return true：可以滚动  false：不可以滚动
     */
    public static boolean canVerticalScroll(EditText editText) {
        //滚动的距离
        int scrollY = editText.getScrollY();
        //控件内容的总高度
        int scrollRange = editText.getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() - editText.getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;

        if (scrollDifference == 0) {
            return false;
        }
        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }

    /**
     * 检查是否存在虚拟按键栏
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 判断虚拟按键栏是否重写
     *
     * @return
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }

    //获取虚拟按键的高度
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     * 6.0一下oppo机子适配
     *
     * @return
     */
    public static boolean isOppo() {
        String manufacturer = Build.MANUFACTURER;
        //这个字符串可以自己定义,例如判断华为就填写huawei,魅族就填写meizu
        if ("oppo".equalsIgnoreCase(manufacturer) && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return false;
    }

    public static boolean isSanxing() {
        String manufacturer = Build.MANUFACTURER;
        if ("samsung".equalsIgnoreCase(manufacturer)) {
            return true;
        }
        return false;
    }

    public static boolean isSilent() {
        AudioManager audioManager = (AudioManager) MyApplication.getInstance().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null) return false;
        int ringerMode = audioManager.getRingerMode();
        return AudioManager.RINGER_MODE_NORMAL != ringerMode;
    }

    private static double mInch = 0;
    /**
     * 获取屏幕尺寸
     * @return
     */
    public static double getScreenInch() {
        if (mInch != 0.0d) {
            return mInch;
        }
        try {
            int realWidth = 0, realHeight = 0;
            Display display = MyApplication.getInstance().getRunningActivity().
                    getWindowManager().getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            if (android.os.Build.VERSION.SDK_INT >= 17) {
                Point size = new Point();
                display.getRealSize(size);
                realWidth = size.x;
                realHeight = size.y;
            } else if (android.os.Build.VERSION.SDK_INT < 17
                    && android.os.Build.VERSION.SDK_INT >= 14) {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                realWidth = (Integer) mGetRawW.invoke(display);
                realHeight = (Integer) mGetRawH.invoke(display);
            } else {
                realWidth = metrics.widthPixels;
                realHeight = metrics.heightPixels;
            }

            mInch =formatDouble(Math.sqrt((realWidth/metrics.xdpi) * (realWidth /metrics.xdpi) + (realHeight/metrics.ydpi) * (realHeight / metrics.ydpi)),1);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return mInch;
    }
    /**
     * Double类型保留指定位数的小数，返回double类型（四舍五入）
     * newScale 为指定的位数
     */
    private static double formatDouble(double d,int newScale) {
        BigDecimal bd = new BigDecimal(d);
        return bd.setScale(newScale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
