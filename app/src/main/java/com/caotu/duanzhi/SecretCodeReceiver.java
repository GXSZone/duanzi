package com.caotu.duanzhi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static android.provider.Telephony.Sms.Intents.SECRET_CODE_ACTION;

/**
 * 骚气的暗码操作,拨号键盘直接切换环境
 * 拨号输入:  *#*#1111#*#*
 */
public class SecretCodeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && SECRET_CODE_ACTION.equals(intent.getAction())) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setClass(context, HideActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
