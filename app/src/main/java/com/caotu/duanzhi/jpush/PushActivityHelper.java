package com.caotu.duanzhi.jpush;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.UrlCheckBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.detail.DetailActivity;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.notice.NoticeHeaderActivity;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.other.AndroidInterface;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PushActivityHelper {

    /**
     * * 2) 接收不到自定义消息: type:  1.审核消息   2.关注消息  3.banner   4.H5页面   5.评论消息   6.点赞/消息  7 专栏详情
     * url:    内容Id     关注人ID   bannerId     url连接    后面都是Id
     */
    public static void pushOpen(Context context, String extras) {
        JpushBean jpushBean = new Gson().fromJson(extras, JpushBean.class);
        String type = jpushBean.getType();
        String url = jpushBean.getUrl();
        String pushid = jpushBean.getPushid();
        if (!TextUtils.isEmpty(pushid)) {
            //接口请求
            CommonHttpRequest.getInstance().notifyInterface(pushid);
        }

        Intent openIntent = new Intent();
        switch (type) {
            case "1":
                if (TextUtils.isEmpty(url)) {
                    return;
                }
                //内容审核和举报消息
                openIntent.setClass(context, DetailActivity.class);
                openIntent.putExtra("contentId", url);
                break;
            //H5
            case "4":
                if (TextUtils.isEmpty(url)) {
                    return;
                }
                openIntent.setClass(context, WebActivity.class);
                openIntent.putExtra(WebActivity.KEY_TITLE, "web");
                openIntent.putExtra(WebActivity.KEY_URL, url);
                break;
            //关注消息列表
            case "2":
                openIntent.setClass(context, NoticeHeaderActivity.class);
                openIntent.putExtra(HelperForStartActivity.key_other_type,
                        HelperForStartActivity.KEY_NOTICE_FOLLOW);
                CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.push_follow);
                UmengHelper.event(UmengStatisticsKeyIds.push_follow);
                break;
            //点赞消息跳转点赞消息列表
            case "6":
                openIntent.setClass(context, NoticeHeaderActivity.class);
                openIntent.putExtra(HelperForStartActivity.key_other_type,
                        HelperForStartActivity.KEY_NOTICE_LIKE);
                CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.push_like);
                UmengHelper.event(UmengStatisticsKeyIds.push_like);
                break;
            //评论消息
            case "5":
                UmengHelper.event(UmengStatisticsKeyIds.push_comment);
                break;
            default:
                openIntent.setClass(context, MainActivity.class);
                break;
        }
        openActivity(context, type, url, openIntent);
    }

    private static void openActivity(Context context, String type, String url, Intent openIntent) {
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
                CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.push_comment);
                if (!TextUtils.isEmpty(commentid)) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("cmtid", commentid);
                    OkGo.<BaseResponseBean<CommendItemBean.RowsBean>>post(HttpApi.COMMENT_DEATIL)
                            .upJson(new JSONObject(params))
                            .execute(new JsonCallback<BaseResponseBean<CommendItemBean.RowsBean>>() {
                                @Override
                                public void onSuccess(Response<BaseResponseBean<CommendItemBean.RowsBean>> response) {
                                    CommendItemBean.RowsBean data = response.body().getData();
                                    if (data == null) return;
                                    if (!TextUtils.isEmpty(contentid)) {
                                        data.setShowContentFrom(true);
                                    }
                                    openIntent.setClass(context, DetailActivity.class);
                                    openIntent.putExtra(HelperForStartActivity.KEY_DETAIL_COMMENT, data);
                                    goActivity(context, openIntent);
                                }
                            });


                } else if (!TextUtils.isEmpty(contentid)) {
                    openIntent.setClass(context, DetailActivity.class);
                    openIntent.putExtra("contentId", contentid);
                    goActivity(context, openIntent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            goActivity(context, openIntent);
        }
    }

    private static void goActivity(Context context, Intent openIntent) {
        //判断是否APP还还活着的逻辑
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        if (runningActivity == null) {
            Intent[] intents = new Intent[2];
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intents[0] = intent;
            intents[1] = openIntent;
            context.startActivities(intents);
        } else if (MyApplication.activities.size() == 1
                && runningActivity instanceof HuaWeiActivity) {
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

    /**
     * H5页面打开APP
     * 首页、发现、发布、消息、我、他人主页（比如段子哥、段子妹）、话题详情、内容详情
     * type: 0=首页  1=他人主页  2=内容详情  3=话题详情  4=发现 5=发布  6=消息  7=我
     * 说明一点: 调用这个方法的时候APP的首页已经是开启了的
     */
    public static void openApp(int type, String id) {
        switch (type) {
            case 1:
                if (TextUtils.isEmpty(id)) return;
                HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, id);
                break;
            case 2:
                if (TextUtils.isEmpty(id)) return;
                HelperForStartActivity.openContentDetail(id);
                break;
            case 3:
                if (TextUtils.isEmpty(id)) return;
                HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic, id);
                break;
//            case 4:
//                break;
//            case 5:
//                break;
//            case 6:
//                break;
//            case 7:
//                break;
//            default:
//                break;
        }
    }
}



