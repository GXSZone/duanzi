package com.caotu.duanzhi.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.UrlCheckBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.module.home.ContentDetailActivity;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.other.AndroidInterface;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

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
            //网络状态监听
            if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                CommonHttpRequest.getInstance().getShareUrl();
                EventBusHelp.sendVideoIsAutoPlay();
                return;
            }
            Bundle bundle = intent.getExtras();
            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.i(TAG, "JPush 用户注册成功" + regId);
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {

                String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
                Log.i(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + extra);
//                processCustomMessage(context, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Log.i(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Log.i(TAG, "[MyReceiver] 用户点击打开了通知");
                processCustomMessage(context, bundle);
                String msgId = bundle.getString(JPushInterface.EXTRA_MSG_ID);
                Log.i(TAG, "通知 msgId----onReceive: " + msgId);
                JPushInterface.reportNotificationOpened(context, msgId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void processCustomMessage(Context context, Bundle bundle) {

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
            CommonHttpRequest.getInstance().notifyInterface(pushid);
        }
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Intent openIntent = new Intent();
        switch (type) {
            case "1":
                //内容审核和举报消息
                openIntent.setClass(context, ContentDetailActivity.class);
                openIntent.putExtra("contentId", url);
                break;
            case "4":
                //H5
                openIntent.setClass(context, WebActivity.class);
                openIntent.putExtra(WebActivity.KEY_TITLE, "web");
                openIntent.putExtra(WebActivity.KEY_URL, url);
                break;
            default:
                openIntent.setClass(context, MainActivity.class);
                break;
        }
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (TextUtils.equals("4", type)) {
            CommonHttpRequest.getInstance().checkUrl(url, new JsonCallback<BaseResponseBean<UrlCheckBean>>() {
                @Override
                public void onSuccess(Response<BaseResponseBean<UrlCheckBean>> response) {
                    // TODO: 2018/12/25 保存接口给的key,H5认证使用
                    UrlCheckBean data = response.body().getData();
                    WebActivity.H5_KEY = data.getReturnkey();
                    WebActivity.WEB_FROM_TYPE = AndroidInterface.type_notice;
                    goActivity(context, openIntent);
                }

                @Override
                public void onError(Response<BaseResponseBean<UrlCheckBean>> response) {
                    goActivity(context, openIntent);
                }
            });
        } else {
            goActivity(context, openIntent);
        }
    }

    private void goActivity(Context context, Intent openIntent) {
        //判断是否APP还还活着的逻辑
        if (MyApplication.getInstance().getRunningActivity() == null) {
            Intent[] intents = new Intent[2];
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intents[0] = intent;
            intents[1] = openIntent;
            context.startActivities(intents);
        } else {
            context.startActivity(openIntent);
        }
    }
}
