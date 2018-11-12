package com.caotu.duanzhi.module.login;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpCode;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NetWorkUtils;
import com.caotu.duanzhi.utils.ToastUtil;
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
        if (!MySpUtils.getBoolean(MySpUtils.SP_ISLOGIN, false)) {
            Intent intent = new Intent();
            intent.setClass(activity, LoginAndRegisterActivity.class);
            activity.startActivityForResult(intent, LoginAndRegisterActivity.LOGIN_REQUEST_CODE);
            return false;
        }
        return true;
    }

    /**
     * 用于登陆成功后的处理
     * @param responseBean
     * @return
     */
    public static boolean isSuccess(Response<BaseResponseBean<String>> responseBean) {
        String userId = responseBean.body().getData();
        if (!TextUtils.isEmpty(userId) &&
                HttpCode.success_code.equals(responseBean.body().getCode())) {
            //这个得在我的页面请求才可以
//            MySpUtils.putString(MySpUtils.SP_MY_ID, userId);
            MySpUtils.putBoolean(MySpUtils.SP_HAS_BIND_PHONE, true);
            MySpUtils.putBoolean(MySpUtils.SP_ISLOGIN, true);
            ToastUtil.showShort(R.string.login_success);
            JPushManager.getInstance().loginSuccessAndSetJpushAlias();
            return true;
        } else {
            return false;
        }
    }
}
