package com.caotu.duanzhi.jpush;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.module.AppStatusListener;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.luck.picture.lib.tools.VoiceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

public class HuaWeiActivity extends Activity {
    /**
     * 该通知的下发通道
     **/
    private static final String KEY_WHICH_PUSH_SDK = "rom_type";
    /**
     * 通知标题
     **/
    private static final String KEY_TITLE = "n_title";
    /**
     * 通知内容
     **/
    private static final String KEY_CONTENT = "n_content";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleOpenClick();
    }


    private void handleOpenClick() {
        if (getIntent().getData() == null) return;
        String data = getIntent().getData().toString();
        if (TextUtils.isEmpty(data)) return;
        if (DevicesUtils.canPlayMessageSound(this)) {
            VoiceUtils.playVoice(MyApplication.getInstance());
        }
        try {
            JSONObject jsonObject = new JSONObject(data);
            String msgId = jsonObject.optString("msg_id"); //消息Id
            byte whichPushSDK = (byte) jsonObject.optInt(KEY_WHICH_PUSH_SDK);
//            String title = jsonObject.optString(KEY_TITLE);
//            String content = jsonObject.optString(KEY_CONTENT);
            String extras = jsonObject.optString("n_extras");  //通知附加字段
            AppStatusListener.getInstance().setAppStatus(AppStatusListener.sBeAlive);
            PushActivityHelper.getInstance().pushOpen(this, extras);
            //上报点击事件
            JPushInterface.reportNotificationOpened(this, msgId, whichPushSDK);
            finish();
        } catch (JSONException e) {
            Log.w("huaweiActivity", "parse notification error");
        }
    }
}
