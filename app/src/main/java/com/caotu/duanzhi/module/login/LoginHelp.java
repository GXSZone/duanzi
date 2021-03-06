package com.caotu.duanzhi.module.login;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.RegistBean;
import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.utils.AESUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NetWorkUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.store.CookieStore;
import com.lzy.okgo.model.Response;

import java.util.Map;

import okhttp3.HttpUrl;


public class LoginHelp {
    /**
     * 统一处理要点击登录的操作,判断sp的值
     */
    public static boolean isLoginAndSkipLogin() {
        if (!isLogin()) {
            goLogin();
            return false;
        }
        return true;
    }

    public static boolean isLogin() {
        return MySpUtils.getBoolean(MySpUtils.SP_ISLOGIN, false);
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
        OkGo.<BaseResponseBean<String>>post(HttpApi.DO_LOGIN)
                .upString(stringBody)
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        String data = response.body().getData();
                        // TODO: 2018/11/16 date不为空则是登录成功
                        if (!TextUtils.isEmpty(data)) {
                            try {
                                loginSuccess(callback, false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            ToastUtil.showShort("登录失败,请检查账号或者密码");
                        }
                    }
                });
    }

    public static void loginSuccess(LoginCllBack callback, boolean isSettingPwd) {
        MySpUtils.putBoolean(MySpUtils.SP_HAS_BIND_PHONE, true);
        MySpUtils.putBoolean(MySpUtils.SP_ISLOGIN, true);
        JPushManager.getInstance().loginSuccessAndSetJpushAlias();
        getUserInfo(callback, isSettingPwd);
    }

    /**
     * 验证码登录和注册接口
     *
     * @param map
     * @param callback
     */
    public static void loginAndRegistByCode(Map<String, String> map, LoginCllBack callback) {
        OkGo.<BaseResponseBean<RegistBean>>post(HttpApi.VERIFY_LOGIN_AND_REGIST)
                .upString(AESUtils.getRequestBodyAES(map))
                .execute(new JsonCallback<BaseResponseBean<RegistBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<RegistBean>> response) {
                        loginSuccess(callback, false);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<RegistBean>> response) {
                        ToastUtil.showShort("登录失败,请检查账号或者验证码");
                        super.onError(response);
                    }
                });
    }

    public static void getUserInfo(LoginCllBack callback, boolean isSettingPwd) {
        OkGo.<BaseResponseBean<UserBaseInfoBean>>post(HttpApi.GET_USER_BASE_INFO)
                .upJson("{}")
                .execute(new JsonCallback<BaseResponseBean<UserBaseInfoBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<UserBaseInfoBean>> response) {
                        UserBaseInfoBean data = response.body().getData();
                        if (data != null && data.getUserInfo() != null) {
                            UserBaseInfoBean.UserInfoBean userInfo = data.getUserInfo();
                            MySpUtils.putString(MySpUtils.SP_MY_ID, userInfo.getUserid());
                            MySpUtils.putString(MySpUtils.SP_MY_AVATAR, userInfo.getUserheadphoto());
                            MySpUtils.putString(MySpUtils.SP_MY_NAME, userInfo.getUsername());
                            MySpUtils.putString(MySpUtils.SP_MY_NUM, userInfo.getUno());
                            MySpUtils.putBoolean(MySpUtils.SP_COLLECTION_SHOW,
                                    LikeAndUnlikeUtil.isLiked(userInfo.collectionswitch));
                        }
                        if (isSettingPwd) {
                            ToastUtil.showShort("设置密码成功");
                        } else {
                            ToastUtil.showShort("登录成功！");
                        }
                        EventBusHelp.sendLoginEvent();
                        if (callback != null) {
                            callback.loginSuccess();
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
