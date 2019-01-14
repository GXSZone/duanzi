package com.caotu.duanzhi.other;

import android.app.Activity;
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
    public static final String type_other = "other";  //其他

    @JavascriptInterface
    public String callappforkey() {
        JSONObject jsonObject = new JSONObject();
        try {
            String myId = MySpUtils.getMyId();
            jsonObject.put("userid", AESUtils.encode(myId));
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
        // TODO: 2019/1/14 修正分享弹窗类型,省的H5那边改
        if (webShareBean.webType == 1) {
            webShareBean.webType = 2;
        }
        ShareDialog shareDialog = ShareDialog.newInstance(webShareBean);
        shareDialog.setListener(new ShareDialog.SimperMediaCallBack() {
            @Override
            public void callback(WebShareBean bean) {
                if (bean.webType == 0) {
                    ShareHelper.getInstance().shareFromWebView(bean);
                } else if (bean.webType == 1) {
                    ShareHelper.getInstance().shareWebPicture(bean, bean.url);
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
}
