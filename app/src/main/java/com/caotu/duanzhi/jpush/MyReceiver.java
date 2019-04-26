package com.caotu.duanzhi.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.luck.picture.lib.tools.VoiceUtils;

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
                //如果是三星手机或者是开了静音模式都不放
                if (DevicesUtils.canPlayMessageSound(context)) {
                    VoiceUtils.playVoice(MyApplication.getInstance());
                }
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
        PushActivityHelper.getInstance().pushOpen(context,extras);
    }
}
