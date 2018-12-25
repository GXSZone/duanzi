package com.caotu.duanzhi.jpush;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.home.ContentDetailActivity;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.utils.NotificationUtil;
import com.caotu.duanzhi.utils.ToastUtil;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JPUSH";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            notifyId++;
            Bundle bundle = intent.getExtras();
//            Log.i(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.i(TAG, "JPush 用户注册成功" + regId);
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {

                String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
                Log.i(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + extra);
                processCustomMessage(context, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Log.i(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Log.i(TAG, "[MyReceiver] 用户点击打开了通知");
                //打开自定义的Activity
                if (!MyApplication.getInstance().getAppIsForeground()) {
                    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                    am.moveTaskToFront(MyApplication.getInstance().getRunningActivity().getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
                }

                String msgId = bundle.getString(JPushInterface.EXTRA_MSG_ID);
                JPushInterface.reportNotificationOpened(context, msgId);
//                notifyInterface(msgId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void notifyInterface(String pushId) {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("pushid", pushId);
        OkGo.<String>post(HttpApi.PUSH_OPEN)
                .upJson(new JSONObject(map))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                    }
                });
    }

    //和下载的通知Id保持一致
    private static int notifyId = 12306;

    private void processCustomMessage(Context context, Bundle bundle) {

        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
/**
 *  * 2) 接收不到自定义消息: type:  1.审核消息   2.关注消息  3.banner   4.H5页面   5.评论消息   6.点赞/消息  7 专栏详情
 * url:    内容Id     关注人ID   bannerId     url连接    后面都是Id
 */
        JpushBean jpushBean = new Gson().fromJson(extras, JpushBean.class);
        String type = jpushBean.getType();
        String url = jpushBean.getUrl();
        String pushid = jpushBean.getPushid();
        if (!TextUtils.isEmpty(pushid)) {
            //接口请求
            notifyInterface(pushid);
        }
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        Intent openIntent = new Intent();
        // TODO: 2018/9/7 他娘这个跳转写的,后期必须要用路由解决这个狗屎的问题
        switch (type) {
            case "1":
                //内容审核和举报消息
                openIntent.setClass(runningActivity, ContentDetailActivity.class);
                openIntent.putExtra("contentId", url);
                break;
            case "4":
                //H5
                openIntent.setClass(runningActivity, WebActivity.class);
                openIntent.putExtra(WebActivity.KEY_TITLE, title);
                openIntent.putExtra(WebActivity.KEY_URL, url);
                break;
            default:
                openIntent.setClass(runningActivity, MainActivity.class);
                break;
        }
        //获取通知栏开关状态,关闭状态就不推了
        boolean notificationEnable = NotificationUtil.notificationEnable(context);
        if (notificationEnable) {
            NotificationManager mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                //只在Android O之上需要渠道
                NotificationChannel notificationChannel = new NotificationChannel(String.valueOf(notifyId),
                        "duanzi", NotificationManager.IMPORTANCE_DEFAULT);
                //如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道，
                //通知才能正常弹出
                notificationChannel.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.happy),
                        Notification.AUDIO_ATTRIBUTES_DEFAULT);
                notificationChannel.setVibrationPattern(new long[]{1000});
                mNotifyManager.createNotificationChannel(notificationChannel);
            }

            // TODO: 2018/9/15 后面需要改用广播的形式,监听自己创建的通知栏的点击事件
//            PendingIntent pendingIntent =PendingIntent.getBroadcast(context,notifyId,openIntent,0);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, notifyId, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new NotificationCompat.Builder(context, String.valueOf(notifyId))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{1000})
                    .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.happy))
                    .build();

            mNotifyManager.notify(notifyId, notification);
        } else {
            ToastUtil.showShort("请打开通知栏开关以便接收最新推送");
        }
    }
}
