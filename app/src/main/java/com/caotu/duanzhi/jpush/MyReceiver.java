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
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.UrlCheckBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.home.CommentDetailActivity;
import com.caotu.duanzhi.module.home.ContentDetailActivity;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.notice.NoticeHeaderActivity;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.other.AndroidInterface;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.NotificationUtil;
import com.google.gson.Gson;
import com.luck.picture.lib.tools.VoiceUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
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
                if (canPlayMessageSound(context)) {
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

    private boolean canPlayMessageSound(Context context) {
        if (DevicesUtils.isSanxing()) {
            return false;
        }
        if (!NotificationUtil.notificationEnable(context)) {
            return false;
        }
        if (DevicesUtils.isSilent()) {
            return false;
        }
        return true;
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
            //关注消息列表
            case "2":
                openIntent.setClass(context, NoticeHeaderActivity.class);
                openIntent.putExtra(HelperForStartActivity.key_other_type,
                        HelperForStartActivity.KEY_NOTICE_FOLLOW);
                break;
            //点赞消息跳转点赞消息列表
            case "6":
                openIntent.setClass(context, NoticeHeaderActivity.class);
                openIntent.putExtra(HelperForStartActivity.key_other_type,
                        HelperForStartActivity.KEY_NOTICE_LIKE);
                break;
            //评论消息
            case "5":
                break;
            default:
                openIntent.setClass(context, MainActivity.class);
                break;
        }
        openActivity(context, type, url, openIntent);
    }

    private void openActivity(Context context, String type, String url, Intent openIntent) {
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (TextUtils.equals("4", type)) {
            CommonHttpRequest.getInstance().checkUrl(url, new JsonCallback<BaseResponseBean<UrlCheckBean>>() {
                @Override
                public void onSuccess(Response<BaseResponseBean<UrlCheckBean>> response) {
                    // TODO: 2018/12/25 保存接口给的key,H5认证使用
                    UrlCheckBean data = response.body().getData();
                    WebActivity.H5_KEY = data.getReturnkey();
                    WebActivity.WEB_FROM_TYPE = AndroidInterface.type_notice;
                    openIntent.putExtra(WebActivity.KEY_IS_SHOW_SHARE_ICON,
                            TextUtils.equals("1", data.getIsshare()));
                    goActivity(context, openIntent);
                }

                @Override
                public void onError(Response<BaseResponseBean<UrlCheckBean>> response) {
                    goActivity(context, openIntent);
                }
            });
        } else if (TextUtils.equals("5", type)) {
            try {
                JSONObject object = new JSONObject(url);
                String contentid = object.getString("contentid");
                String commentid = object.getString("commentid");
                if (!TextUtils.isEmpty(contentid)) {
                    openIntent.setClass(context, ContentDetailActivity.class);
                    openIntent.putExtra("contentId", contentid);
                    goActivity(context, openIntent);
                } else if (!TextUtils.isEmpty(commentid)) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("cmtid", commentid);
                    OkGo.<BaseResponseBean<CommendItemBean.RowsBean>>post(HttpApi.COMMENT_DEATIL)
                            .upJson(new JSONObject(params))
                            .execute(new JsonCallback<BaseResponseBean<CommendItemBean.RowsBean>>() {
                                @Override
                                public void onSuccess(Response<BaseResponseBean<CommendItemBean.RowsBean>> response) {
                                    CommendItemBean.RowsBean data = response.body().getData();
                                    openIntent.setClass(context, CommentDetailActivity.class);
                                    openIntent.putExtra(HelperForStartActivity.KEY_DETAIL_COMMENT, data);
                                    goActivity(context, openIntent);
                                }
                            });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
