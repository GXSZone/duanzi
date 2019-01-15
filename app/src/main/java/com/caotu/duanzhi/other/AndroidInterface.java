package com.caotu.duanzhi.other;

import android.app.Activity;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.utils.AESUtils;
import com.caotu.duanzhi.utils.LogUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.google.gson.Gson;

import org.json.JSONObject;

public class AndroidInterface {
    public static final String type_splash = "splash"; //闪屏
    public static final String type_banner = "banner"; //发现页banner
    public static final String type_recommend = "recommend"; //推荐列表
    public static final String type_notice = "notice"; //推送通知
    public static final String type_user = "user";  //个人中心页面
    public static final String type_other_user = "other_user";
    public static final String type_other = "other";  //其他

    @JavascriptInterface
    public String callappforkey() {
        JSONObject jsonObject = new JSONObject();
        try {
            String myId = MySpUtils.getMyId();
            if (!TextUtils.isEmpty(WebActivity.USER_ID)) {
                myId = WebActivity.USER_ID;
            }
            jsonObject.put("userid", TextUtils.isEmpty(myId) ? "" : AESUtils.encode(myId));
            jsonObject.put("key", WebActivity.H5_KEY);
            jsonObject.put("apptype", "Android");
            jsonObject.put("apppage", WebActivity.WEB_FROM_TYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @JavascriptInterface
    public void closeapp() {
        MyApplication.getInstance().getRunningActivity().finish();
    }

    @JavascriptInterface
    public void shareweb(String shareContent) {
        LogUtil.logString("webCall", Thread.currentThread().getName());
        WebShareBean webShareBean = new Gson().fromJson(shareContent, WebShareBean.class);

        ShareDialog shareDialog = ShareDialog.newInstance(webShareBean);
        shareDialog.setListener(new ShareDialog.SimperMediaCallBack() {
            @Override
            public void callback(WebShareBean bean) {
                if (bean.webType == 1) {
                    ShareHelper.getInstance().shareWebPicture(bean, bean.url);
                } else {
                    ShareHelper.getInstance().shareFromWebView(bean);
                }
            }
        });
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        if (runningActivity instanceof BaseActivity) {
            shareDialog.show(((BaseActivity) runningActivity).getSupportFragmentManager(), "share");
        }
    }

    @JavascriptInterface
    public void weblogin() {
        LoginHelp.goLogin();
    }

    /**
     * H5设置给app分享内容
     *
     * @param shareContent
     */
    @JavascriptInterface
    public void setShareContent(String shareContent) {
        WebShareBean webShareBean = new Gson().fromJson(shareContent, WebShareBean.class);
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        if (runningActivity != null && runningActivity instanceof WebActivity) {
            runningActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((WebActivity) runningActivity).setShareBean(webShareBean);
                }
            });
        }
    }
}
