package com.caotu.duanzhi.module.login;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.utils.AESUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NetWorkUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.cookie.store.CookieStore;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.HttpUrl;


public class LoginHelp {
    /**
     * 统一处理要点击登录的操作,判断sp的值
     */
    public static boolean isLoginAndSkipLogin() {
        Activity activity = MyApplication.getInstance().getRunningActivity();
        if (!NetWorkUtils.isNetworkConnected(activity)) {
            ToastUtil.showShort("没有网络连接");
            return false;
        }
        if (!MySpUtils.getBoolean(MySpUtils.SP_ISLOGIN, false)) {
            Intent intent = new Intent();
            intent.setClass(activity, LoginAndRegisterActivity.class);
            activity.startActivityForResult(intent, LoginAndRegisterActivity.LOGIN_REQUEST_CODE);
            return false;
        }
        return true;
    }

    public static void goLogin() {
        Activity activity = MyApplication.getInstance().getRunningActivity();
        if (!NetWorkUtils.isNetworkConnected(activity)) {
            ToastUtil.showShort("没有网络连接");
            return;
        }
        Intent intent = new Intent();
        intent.setClass(activity, LoginAndRegisterActivity.class);
        activity.startActivityForResult(intent, LoginAndRegisterActivity.LOGIN_REQUEST_CODE);
    }

    public interface LoginCllBack {
        void loginSuccess();
    }

    public static void login(Map<String, String> map, LoginCllBack callback) {
        String stringBody = AESUtils.getRequestBodyAES(map);
        OkGo.<String>post(HttpApi.DO_LOGIN)
                .upString(stringBody)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            String sign = jsonObject.optString("sign");
                            if (TextUtils.isEmpty(sign)) {
                                ToastUtil.showShort(R.string.login_failure);
                            } else {
                                String userId = AESUtils.decode(sign);
                                MySpUtils.putString(MySpUtils.SP_MY_ID, userId);
                                MySpUtils.putBoolean(MySpUtils.SP_HAS_BIND_PHONE, true);
                                MySpUtils.putBoolean(MySpUtils.SP_ISLOGIN, true);
                                ToastUtil.showShort(R.string.login_success);
                                JPushManager.getInstance().loginSuccessAndSetJpushAlias();
                                if (callback!=null){
                                    callback.loginSuccess();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public static void loginOut() {
        MySpUtils.clearLogingType();
        JPushManager.getInstance().loginOutClearAlias();
        // TODO: 2018/11/12 清除本地cookie
        HttpUrl httpUrl = HttpUrl.parse(BaseConfig.baseApi);
        CookieStore cookieStore = OkGo.getInstance().getCookieJar().getCookieStore();
        cookieStore.removeCookie(httpUrl);
    }
}
