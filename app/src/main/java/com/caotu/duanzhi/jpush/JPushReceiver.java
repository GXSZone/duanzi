package com.caotu.duanzhi.jpush;

import android.content.Context;
import android.util.Log;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.luck.picture.lib.tools.VoiceUtils;

import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

public class JPushReceiver extends JPushMessageReceiver {
    private static final String TAG = "PushMessageReceiver";


    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        Log.i(TAG, "[onMessage] " + customMessage.toString());
        //如果是三星手机或者是开了静音模式都不放
        if (DevicesUtils.canPlayMessageSound(context)) {
            VoiceUtils.playVoice(MyApplication.getInstance());
        }
    }

    /**
     * 该方法可以当做类似的网络状态监听,网络断开链接都会回调该方法
     *
     * @param context
     * @param b
     * //网络状态监听
     * if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction()))
     */
    @Override
    public void onConnected(Context context, boolean b) {
        super.onConnected(context, b);
        CommonHttpRequest.getInstance().getShareUrl();
        EventBusHelp.sendVideoIsAutoPlay();
    }

    /**
     * 收到通知时其他手机有这回调,华为没有,只有上面的
     *
     * @param context
     * @param message
     */
    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage message) {
        Log.i(TAG, "[onNotifyMessageArrived] " + message);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage message) {
        Log.i(TAG, "[onNotifyMessageOpened] " + message.toString());
        JPushInterface.reportNotificationOpened(context, message.msgId);
        try {
            PushActivityHelper.getInstance().pushOpen(context, message.notificationExtras);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
