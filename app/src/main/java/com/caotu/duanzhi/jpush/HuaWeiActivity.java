package com.caotu.duanzhi.jpush;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

public class HuaWeiActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleOpenClick();
    }

    private void handleOpenClick() {
        String data = null;
        //获取华为平台附带的jpush信息
        if (getIntent().getData() != null) {
            data = getIntent().getData().toString();
        }
        //获取fcm、小米、oppo、vivo平台附带的jpush信息
        if (TextUtils.isEmpty(data) && getIntent().getExtras() != null) {
            data = getIntent().getExtras().getString("JMessageExtra");
        }
        if (TextUtils.isEmpty(data)) return;
        try {
            JSONObject jsonObject = new JSONObject(data);
            String msgId = jsonObject.optString("msg_id"); //消息Id
            byte whichPushSDK = (byte) jsonObject.optInt("rom_type");
//            String title = jsonObject.optString(KEY_TITLE);
//            String content = jsonObject.optString(KEY_CONTENT);
            String extras = jsonObject.optString("n_extras");  //通知附加字段
            PushActivityHelper.pushOpen(this, extras);
            //上报点击事件
            JPushInterface.reportNotificationOpened(this, msgId, whichPushSDK);
            finish();
        } catch (JSONException e) {
            Log.w("huaweiActivity", "parse notification error");
        }
    }
}
