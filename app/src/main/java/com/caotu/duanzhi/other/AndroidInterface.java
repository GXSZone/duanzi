package com.caotu.duanzhi.other;

import android.webkit.JavascriptInterface;

import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.utils.AESUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.google.gson.Gson;

import org.json.JSONObject;

public class AndroidInterface {

    @JavascriptInterface
    public String callappforkey() {
        JSONObject jsonObject = new JSONObject();
        try {
            String myId = MySpUtils.getMyId();
            jsonObject.put("userid", AESUtils.encode(myId));
            jsonObject.put("key", WebActivity.H5_KEY);
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
        WebShareBean webShareBean = new Gson().fromJson(shareContent, WebShareBean.class);

//        WebShareBean webBean = ShareHelper.getInstance().createWebBean(false, false, null
//                , null, null);
//        ShareDialog shareDialog = ShareDialog.newInstance(webBean);
//        shareDialog.setListener(new ShareDialog.ShareMediaCallBack() {
//            @Override
//            public void callback(WebShareBean bean) {
//                if (bean != null) {
//                    bean.url = shareUrl;
//                    bean.title = webTitle.getText().toString();
//                }
//                ShareHelper.getInstance().shareFromWebView(bean);
//            }
//
//            @Override
//            public void colloection(boolean isCollection) {
//
//            }
//        });
//        shareDialog.show(getSupportFragmentManager(), "share");
    }

    @JavascriptInterface
    public void weblogin() {
        LoginHelp.goLogin();
    }
}
