package com.caotu.duanzhi.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.EditText;

import com.caotu.duanzhi.MyApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

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
            registrationID = JPushInterface.getRegistrationID(MyApplication.getInstance().getRunningActivity());
            MySpUtils.putString(MySpUtils.SP_REGISTRATION_ID, registrationID);
            return registrationID;
        }
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


}
