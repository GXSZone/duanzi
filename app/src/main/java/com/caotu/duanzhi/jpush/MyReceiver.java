//package com.caotu.duanzhi.jpush;
//
//import android.app.Activity;
//import android.app.ActivityManager;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.app.NotificationCompat;
//import android.text.TextUtils;
//
//import com.android.volley.VolleyError;
//import com.caotu.toutu.R;
//import com.caotu.toutu.activity.MainActivity;
//import com.caotu.toutu.activity.MomentsDetailsActivity;
//import com.caotu.toutu.activity.VisitOtherActivity;
//import com.caotu.toutu.activity.WebActivity;
//import com.caotu.toutu.app.App;
//import com.caotu.toutu.bean.JpushBean;
//import com.caotu.toutu.httprequest.HTTPAPI;
//import com.caotu.toutu.httprequest.VolleyJsonObjectInterface;
//import com.caotu.toutu.httprequest.VolleyRequest;
//import com.caotu.toutu.utils.Logger;
//import com.caotu.toutu.utils.RequestUtils;
//import com.caotu.toutu.utils.ToastUtil;
//import com.google.gson.Gson;
//import com.maning.updatelibrary.NotificationUtil;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
//import cn.jpush.android.api.JPushInterface;
//
///**
// * 自定义接收器
// * <p>
// * 如果不定义这个 Receiver，则：
// * 1) 默认用户会打开主界面
// */
//public class MyReceiver extends BroadcastReceiver {
//    private static final String TAG = "JPUSH";
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
////        try {
////            notifyId++;
////            Bundle bundle = intent.getExtras();
////            Logger.i(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
////
////            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
////                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
////                Logger.i(TAG, "JPush 用户注册成功" + regId);
////                //send the Registration Id to your server...
////
////            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
////
////                String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
////                Logger.i(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + extra);
////                processCustomMessage(context, bundle);
////
////            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
////                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
////                Logger.i(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
////
////            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
////                Logger.i(TAG, "[MyReceiver] 用户点击打开了通知");
////                //打开自定义的Activity
////                if (!App.getInstance().getAppIsForeground()) {
////                    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
////                    am.moveTaskToFront(App.getInstance().getRunningActivity().getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
//////                        Intent i = new Intent(context, MainActivity.class);
//////                    i.putExtras(bundle);
//////                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//////                    context.startActivity(i);
////                }
////
////                String msgId = bundle.getString(JPushInterface.EXTRA_MSG_ID);
////                JPushInterface.reportNotificationOpened(context, msgId);
//////                notifyInterface(msgId);
////
////            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
////                Logger.i(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
////                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
////
////            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
////                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
////                Logger.i(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
////            } else {
////                Logger.i(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
////            }
////        } catch (Exception e) {
////
////        }
//
//    }
//
//    private void notifyInterface(String pushId) {
////        Map<String, String> map = new HashMap<>();
////        map.put("pushid", pushId);
////        String body = RequestUtils.getRequestBody(map);
////        VolleyRequest.RequestPostJsonObjectApp(App.getInstance(), HTTPAPI.PUSH_OPEN, body, null, new VolleyJsonObjectInterface() {
////            @Override
////            public void onSuccess(JSONObject response) {
////                String code = response.optString("code");
////                if ("1000".equals(code)) {
////                    Logger.i(TAG, "msgId  send interFace success");
////                } else {
////                    Logger.i(TAG, response.toString());
////                }
////            }
////
////            @Override
////            public void onError(VolleyError error) {
////                Logger.i(TAG, error.getMessage());
////            }
////        });
//    }
//
//    // 打印所有的 intent extra 数据
//    private static String printBundle(Bundle bundle) {
//        StringBuilder sb = new StringBuilder();
//        for (String key : bundle.keySet()) {
//            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
//                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
//            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
//                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
//            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
//                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
//                    Logger.i(TAG, "This message has no Extra data");
//                    continue;
//                }
//                try {
//                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
//                    Iterator<String> it = json.keys();
//
//                    while (it.hasNext()) {
//                        String myKey = it.next();
//                        sb.append("\nkey:" + key + ", value: [" +
//                                myKey + " - " + json.optString(myKey) + "]");
//                    }
//                } catch (JSONException e) {
//                    Logger.e(TAG, "Get message extra JSON error!");
//                }
//
//            } else {
//                sb.append("\nkey:" + key + ", value:" + bundle.get(key));
//            }
//        }
//        return sb.toString();
//    }
//
//    //和下载的通知Id保持一致
//    private static int notifyId = 10086;
//
//    private void processCustomMessage(Context context, Bundle bundle) {
//
//        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
//        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
///**
// *  * 2) 接收不到自定义消息: type:  1.审核消息   2.关注消息  3.banner   4.H5页面   5.评论消息   6.点赞/消息  7 专栏详情
// * url:    内容Id     关注人ID   bannerId     url连接    后面都是Id
// */
//        JpushBean jpushBean = new Gson().fromJson(extras, JpushBean.class);
//        String type = jpushBean.getType();
//        String url = jpushBean.getUrl();
//        String pushid = jpushBean.getPushid();
//        if (!TextUtils.isEmpty(pushid)) {
//            //接口请求
//            notifyInterface(pushid);
//        }
//        if (TextUtils.isEmpty(url)) {
//            return;
//        }
//        Activity runningActivity = App.getInstance().getRunningActivity();
//        Intent openIntent = new Intent();
//        // TODO: 2018/9/7 他娘这个跳转写的,后期必须要用路由解决这个狗屎的问题
//        switch (type) {
//            case "1":
//                //内容审核和举报消息
//                openIntent.setClass(runningActivity, MomentsDetailsActivity.class);
//
//                Bundle bundle2 = new Bundle();
//                bundle2.putString("contentId", url);
//                openIntent.putExtras(bundle2);
//                break;
//            case "5"://评论消息
//            case "6"://点赞消息    跳转到页面
//            case "2": //关注消息
//                openIntent.setClass(runningActivity, MainActivity.class);
//                openIntent.putExtra(MainActivity.KEY_TAB_SELECTED, 2);
//                break;
//            case "3":
//                //todo banner 目前跳转发现页
//                openIntent.setClass(runningActivity, MainActivity.class);
//                openIntent.putExtra(MainActivity.KEY_TAB_SELECTED, 1);
//                break;
//            case "4":
//                //H5
//                openIntent.setClass(runningActivity, WebActivity.class);
//                openIntent.putExtra(WebActivity.KEY_TITLE, title);
//                openIntent.putExtra(WebActivity.KEY_URL, url);
//                break;
//
//            case "7":
//                //专栏详情
//                openIntent.setClass(runningActivity, VisitOtherActivity.class);
////                openIntent.putExtra(VisitOtherActivity.KEY_TYPE,"theme");
////                openIntent.putExtra(VisitOtherActivity.KEY_ID,url);
//                Bundle bundle3 = new Bundle();
//                bundle3.putString("type", "theme");
//                bundle3.putString("id", url);
//                openIntent.putExtras(bundle3);
//                break;
//            default:
//                openIntent.setClass(runningActivity, MainActivity.class);
//                break;
//        }
//        //获取通知栏开关状态,关闭状态就不推了
//        boolean notificationEnable = NotificationUtil.notificationEnable(context);
//        if (notificationEnable) {
//            NotificationManager mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                //只在Android O之上需要渠道
//                NotificationChannel notificationChannel = new NotificationChannel(String.valueOf(notifyId),
//                        "toutu", NotificationManager.IMPORTANCE_DEFAULT);
//                //如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道，
//                //通知才能正常弹出
//                notificationChannel.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.happy),
//                        Notification.AUDIO_ATTRIBUTES_DEFAULT);
//                notificationChannel.setVibrationPattern(new long[]{1000});
//                mNotifyManager.createNotificationChannel(notificationChannel);
//            }
//
//            // TODO: 2018/9/15 后面需要改用广播的形式,监听自己创建的通知栏的点击事件
////            PendingIntent pendingIntent =PendingIntent.getBroadcast(context,notifyId,openIntent,0);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, notifyId, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            Notification notification = new NotificationCompat.Builder(context, String.valueOf(notifyId))
//                    .setContentTitle(title)
//                    .setContentText(message)
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setAutoCancel(true)
//                    .setContentIntent(pendingIntent)
//                    .setVibrate(new long[]{1000})
//                    .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.happy))
//                    .build();
//
//            mNotifyManager.notify(notifyId, notification);
//        } else {
//            ToastUtil.showShort("请打开通知栏开关以便接收最新推送");
//        }
//    }
//}
