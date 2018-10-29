package com.caotu.duanzhi.module.login;

import android.app.Activity;
import android.content.Intent;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.utils.NetWorkUtils;
import com.caotu.duanzhi.utils.ToastUtil;


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
}
