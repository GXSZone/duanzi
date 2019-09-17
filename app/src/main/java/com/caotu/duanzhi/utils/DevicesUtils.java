package com.caotu.duanzhi.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.caotu.duanzhi.MyApplication;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
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
     * 通过改变View透明度来给view增加点击效果，可以不用再写selector
     *
     * @param view
     */
    public static void setAlphaSelector(View view) {
        view.setAlpha(1f);
        view.setOnTouchListener(new View.OnTouchListener() {
            float lastPosX = -1;
            float lastPosY = -1;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float posX = motionEvent.getX();
                float posY = motionEvent.getY();
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    lastPosX = posX;
                    lastPosY = posY;
                    if (view.isClickable()) {
                        view.setAlpha(0.5f);
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    if (lastPosY == posY && lastPosX == posX) {
                        view.setAlpha(0.5f);
                    } else {
                        view.setAlpha(1f);
                    }
                } else {
                    view.setAlpha(1f);
                }
                return false;
            }
        });
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


    protected static final String PREFS_FILE = "device_id.xml";
    protected static final String PREFS_DEVICE_ID = "device_id";
    protected static volatile UUID uuid;

    /**
     * 这个方法获取设备唯一识别码靠谱,串联的值比较多
     * 在设备首次启动时，系统会随机生成一个64位的数字，并把这个数字以16进制字符串的形式保存下来，
     * 这个16进制的字符串就是ANDROID_ID，当设备被wipe后该值会被重置。
     * Android ID是一个不错的选择，64位的随机数重复率不高，而且不需要申请权限，
     * 但也有些小问题，比如有个很常见的Bug会导致设备产生相同的Android ID: 9774d56d682e549c，
     * 另外Android ID的生成不依赖硬件，刷机或者升级系统（这个没验证过）都会改变Android ID
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
                if (!"9774d56d682e549c".equals(androidId)) {
                    uuid = UUID.nameUUIDFromBytes(androidId
                            .getBytes(StandardCharsets.UTF_8));
                } else {

                    uuid = UUID.randomUUID();
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
        if (context == null) return "";
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
        try {
            Class c = Class.forName("android.os.SystemProperties");
            Method m = c.getDeclaredMethod("get", String.class);
            m.setAccessible(true);
            sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
        } catch (Throwable e) {
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
        return "oppo".equalsIgnoreCase(manufacturer) && Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }

    public static boolean canPlayMessageSound(Context context) {
        if (!MySpUtils.getPushSoundIsOpen()) {
            return false;
        }
        if (!NotificationUtil.notificationEnable(context)) {
            return false;
        }
        AudioManager audioManager = (AudioManager) MyApplication.getInstance().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null) return false;
        int ringerMode = audioManager.getRingerMode();
        return AudioManager.RINGER_MODE_NORMAL == ringerMode;
    }

}
