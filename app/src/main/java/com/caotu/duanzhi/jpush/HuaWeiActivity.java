package com.caotu.duanzhi.jpush;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.UrlCheckBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.home.ContentDetailActivity;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.other.WebActivity;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

public class HuaWeiActivity extends BaseActivity {
    /**
     * 消息Id
     **/
    private static final String KEY_MSGID = "msg_id";
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
    /**
     * 通知附加字段
     **/
    private static final String KEY_EXTRAS = "n_extras";
    private static final String TAG = "huaweiActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleOpenClick();
    }

    @Override
    protected void initView() {
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_hua_wei;
    }


    private void handleOpenClick() {
        if (getIntent().getData() == null) return;
        String data = getIntent().getData().toString();
        if (TextUtils.isEmpty(data)) return;
        try {
            JSONObject jsonObject = new JSONObject(data);
            String msgId = jsonObject.optString(KEY_MSGID);
            byte whichPushSDK = (byte) jsonObject.optInt(KEY_WHICH_PUSH_SDK);
            String title = jsonObject.optString(KEY_TITLE);
            String content = jsonObject.optString(KEY_CONTENT);
            String extras = jsonObject.optString(KEY_EXTRAS);

            JpushBean jpushBean = new Gson().fromJson(extras, JpushBean.class);
            String type = jpushBean.getType();
            String url = jpushBean.getUrl();
            String pushid = jpushBean.getPushid();
            Intent openIntent = new Intent();
            if (!TextUtils.isEmpty(pushid)) {
                //接口请求
                CommonHttpRequest.getInstance().notifyInterface(pushid);
            }
            if (TextUtils.isEmpty(url)) {
                return;
            }
            switch (type) {
                case "1":
                    //内容审核和举报消息
                    openIntent.setClass(this, ContentDetailActivity.class);
                    openIntent.putExtra("contentId", url);
                    break;
                case "4":
                    //H5
                    openIntent.setClass(this, WebActivity.class);
                    openIntent.putExtra(WebActivity.KEY_TITLE, "web");
                    openIntent.putExtra(WebActivity.KEY_URL, url);
                    break;
                default:
                    openIntent.setClass(this, MainActivity.class);
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
//                    WebActivity.WEB_FROM_TYPE = AndroidInterface.type_notice;
                        goActivity(openIntent);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<UrlCheckBean>> response) {
                        goActivity(openIntent);
                    }
                });
            } else {
                goActivity(openIntent);
            }
            //上报点击事件
            JPushInterface.reportNotificationOpened(this, msgId, whichPushSDK);
            finish();
        } catch (JSONException e) {
            Log.w(TAG, "parse notification error");
        }
    }

    private void goActivity(Intent openIntent) {
        if (MyApplication.getInstance().getLastSecondActivity() == null) {
            //判断是否APP还还活着的逻辑
            Intent[] intents = new Intent[2];
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intents[0] = intent;
            intents[1] = openIntent;
            startActivities(intents);
        } else {
            startActivity(openIntent);
        }
    }
}
