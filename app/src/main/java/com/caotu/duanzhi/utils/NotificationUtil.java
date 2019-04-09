package com.caotu.duanzhi.utils;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NotificationUtil {

    public static final int notifyId = 450;
    static final String CHANNEL_ID = "channel_id_1";
    static final String CHANNEL_NAME = "channel_name_1";
    private static NotificationManager mNotificationManager;

    /**
     * 构建一个消息
     *
     * @param context 上下文
     * @param icon    图标id
     * @param title   标题
     * @param content 内容
     */
    private static NotificationCompat.Builder builderNotification(Context context, int icon, String title, String content) {

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //只在Android O之上需要渠道
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            //如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道，
            //通知才能正常弹出
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        return builder.setSmallIcon(icon)
                .setContentTitle("通知标题")
                .setContentText("通知内容")
                //不能删除
                .setAutoCancel(false)
                //正在交互（如播放音乐）
                .setOngoing(true)
                .setContentTitle(title)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true);

//        mNotificationManager.notify(notifyId, builder.build());
    }

    public static void cancleNotification(int notiyId) {
        mNotificationManager.cancel(notiyId);
    }

    /**
     * 显示刚开始下载的通知
     *
     * @param context 上下文
     * @param icon    图标
     * @param title   标题
     * @param content 内容
     */
    public static void showNotification(Context context, int icon, String title, String content) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = builderNotification(context, icon, title, content)
                .setDefaults(Notification.DEFAULT_SOUND);
        manager.notify(notifyId, builder.build());
    }

    /**
     * 显示正在下载的通知
     *
     * @param context 上下文
     * @param icon    图标
     * @param title   标题
     * @param content 内容
     */
    public static void showProgressNotification(Context context, int icon, String title, String content,
                                                int max, int progress) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = builderNotification(context, icon, title, content)
                .setProgress(max, progress, false);
        manager.notify(notifyId, builder.build());
    }

    /**
     * 获取通知栏开关状态
     *
     * @return true |false
     */
    public static boolean notificationEnable(Context context) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        return notificationManagerCompat.areNotificationsEnabled();
    }

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    public static boolean isNotificationEnabled(Context context) {

        AppOpsManager mAppOps =
                (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);

        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        Class appOpsClass = null;

        /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());

            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW,
                    Integer.TYPE, Integer.TYPE, String.class);

            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int value = (Integer) opPostNotificationValue.get(Integer.class);

            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) ==
                    AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 跳转到权限设置界面
     */
    public static void open(Context context) {

        // vivo 点击设置图标>加速白名单>我的app
        //      点击软件管理>软件管理权限>软件>我的app>信任该软件
//        Intent appIntent = context.getPackageManager().getLaunchIntentForPackage("com.iqoo.secure");
//        if (appIntent != null) {
//            context.startActivity(appIntent);
//            return;
//        }

        // oppo 点击设置图标>应用权限管理>按应用程序管理>我的app>我信任该应用
        //      点击权限隐私>自启动管理>我的app
        Intent appIntent = context.getPackageManager().getLaunchIntentForPackage("com.oppo.safe");
        if (appIntent != null) {
            context.startActivity(appIntent);
            return;
        }

        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        try {
            // TODO: 2019/4/3 解决锤子手机 android.content.ActivityNotFoundException
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
