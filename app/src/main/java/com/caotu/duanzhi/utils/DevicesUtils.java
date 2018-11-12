package com.caotu.duanzhi.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.caotu.duanzhi.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import cn.jpush.android.api.JPushInterface;

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


    /**
     * 获取当前屏幕宽度(px)
     *
     * @param context
     * @return
     */
    public static int getSrecchWidth(Context context) {
        if (displayMetrics == null) {
            displayMetrics = context.getResources().getDisplayMetrics();
        }
        return displayMetrics.widthPixels;
    }

    /**
     * 获取当前屏幕高度(px)
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        if (displayMetrics == null) {
            displayMetrics = context.getResources().getDisplayMetrics();
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

    /**
     * 获取唯一标识
     *
     * @return
     */
    public static String getRegistrationID() {
        //先去sp取值
        String registrationID = MySpUtils.getString(MySpUtils.SP_REGISTRATION_ID);
        if (registrationID != null && registrationID.length() != 0) {
            return registrationID;
        } else {
            registrationID = JPushInterface.getRegistrationID(MyApplication.getInstance());
            MySpUtils.putString(MySpUtils.SP_REGISTRATION_ID, registrationID);
            return registrationID;
        }
    }

    /**
     * 这个方法获取设备唯一识别码靠谱,串联的值比较多
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {

        String m_szLongID = getImei(context) + getAndroidId(context)
                + getShortId() + getMac(context);
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
        byte p_md5Data[] = m.digest();
        String m_szUniqueID = new String();
        for (int i = 0; i < p_md5Data.length; i++) {
            int b = (0xFF & p_md5Data[i]);
            if (b <= 0xF) {
                m_szUniqueID += "0";
            }
            m_szUniqueID += Integer.toHexString(b);
        }

        m_szUniqueID = m_szUniqueID.toUpperCase();
        return m_szUniqueID;

    }

    public static String getShortId() {
        return "35" + Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10;
    }
    public static String getAndroidId(Context context) {
        try {
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String getMac(Context context) {
        try {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            return wm.getConnectionInfo().getMacAddress();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @SuppressLint("MissingPermission")
    public static String getImei(Context context) {

        try {
            TelephonyManager TelephonyMgr = (TelephonyManager) context.
                    getSystemService(context.TELEPHONY_SERVICE);
            return TelephonyMgr.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String sID = null;
    public static final String INSTALLATION = "INSTALLATION";

    public synchronized static String id(Context context) {
        if (sID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists())
                    writeInstallationFile(installation);
                sID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sID;
    }

    public static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    public static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
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
    public String getNativePhoneNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String nativePhoneNumber = "";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            return telephonyManager.getLine1Number();
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
     * @param activity
     * @param installPermissionCallBack
     */
    public static void checkInstallPermission(Activity activity, InstallPermissionCallBack installPermissionCallBack) {
        if (hasInstallPermission(activity)) {
            if (installPermissionCallBack != null) {
                installPermissionCallBack.onGranted();
            }
        } else {
            openInstallPermissionSetting(activity, installPermissionCallBack);
        }
    }


    /**
     * 判断有没有安装权限
     * @param context
     * @return
     */
    public static boolean hasInstallPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //先获取是否有安装未知来源应用的权限
            return context.getPackageManager().canRequestPackageInstalls();
        }
        return true;
    }

    /**
     * 去打开安装权限的页面
     * @param activity
     * @param installPermissionCallBack
     */
    public static void openInstallPermissionSetting(Activity activity, final InstallPermissionCallBack installPermissionCallBack) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
            Uri apkUri = FileProvider.getUriForFile(context, MyApplication.getInstance().getPackageName(), file);
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

}
