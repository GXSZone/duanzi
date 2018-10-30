package com.caotu.duanzhi.module.login;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.caotu.duanzhi.Http.bean.LoginResponseBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NetWorkUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.google.gson.Gson;
import com.lzy.okgo.model.Response;


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
//        if (!MySpUtils.getBoolean(MySpUtils.SP_ISLOGIN, false)) {
        Intent intent = new Intent();
        intent.setClass(activity, LoginAndRegisterActivity.class);
        activity.startActivityForResult(intent, LoginAndRegisterActivity.LOGIN_REQUEST_CODE);
        return false;
//        }

    }

    public static LoginResponseBean LoginSuccessSaveCookie(Response<String> response) {
        LoginResponseBean responseBean = new Gson().fromJson(response.body(), LoginResponseBean.class);
        if (responseBean != null) {
            String sign = responseBean.getSign();
            if (!TextUtils.isEmpty(sign)) {
                MySpUtils.putString(MySpUtils.SP_MY_ID, sign);
            }
            String token = responseBean.getToken();
            if (!TextUtils.isEmpty(token)) {
                MySpUtils.putString(MySpUtils.SP_TOKEN, token);
            }
        }
        return responseBean;
    }
}
