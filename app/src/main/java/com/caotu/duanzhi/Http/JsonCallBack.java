package com.caotu.duanzhi.Http;

import android.text.TextUtils;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.config.HttpCode;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.dialog.BindPhoneDialog;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.exception.HttpException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.Response;

/**
 * okGo框架提供
 *
 * @param <T>
 */
public abstract class JsonCallback<T> extends AbsCallback<T> {

    private Type type;
    private Class<T> clazz;

    public JsonCallback() {
    }

    public JsonCallback(Type type) {
        this.type = type;
    }

    public JsonCallback(Class<T> clazz) {
        this.clazz = clazz;
    }


// TODO: 2018/10/28 对于一些接口要另外加头参数可以在该方法里加
//    @Override
//    public void onStart(Request<T, ? extends Request> request) {
//        super.onStart(request);
//        // 主要用于在所有请求之前添加公共的请求头或请求参数
//        // 例如登录授权的 token
//        // 使用的设备信息
//        // 可以随意添加,也可以什么都不传
//        // 还可以在这里对所有的参数进行加密，均在这里实现
//        request.headers("header1", "HeaderValue1")//
//                .params("params1", "ParamsValue1")//
//                .params("token", "3215sdf13ad1f65asd4f3ads1f");
//    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    @Override
    public T convertResponse(Response response) throws Throwable {

        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用
        // 重要的事情说三遍，不同的业务，这里的代码逻辑都不一样，如果你不修改，那么基本不可用

        //详细自定义的原理和文档，看这里： https://github.com/jeasonlzy/okhttp-OkGo/wiki/JsonCallback
//        String json = response.body().string();
//        JSONObject jsonObject = new JSONObject(json);
//        String code = jsonObject.getString("code");
////        String success_code = "1000";   //请求成功
////        String in_the_review = "3007"; //"操作失败，正在审核中！"
////        String login_failure = "1024"; //错误信息：登陆失效
////        String no_bind_phone = "2003";  //未绑定手机
////        String user_has_exsit = "3001";  //用户已存在
//        if (HttpCode.login_failure.equals(code)) {
//            ToastUtil.showShort("登陆失效");
//            //清除用户信息
//            MySpUtils.clearLogingType();
//            throw new IllegalStateException("1024");
//        } else if (HttpCode.in_the_review.equals(code)) {
//            ToastUtil.showShort("操作失败，正在审核中！");
//            throw new IllegalStateException("操作失败，正在审核中！");
//        } else if (HttpCode.no_bind_phone.equals(code)) {
//            BindPhone();
//            return null;
////            throw new IllegalStateException("未绑定手机");
//        }

        if (type == null) {
            if (clazz == null) {
                Type genType = getClass().getGenericSuperclass();
                type = ((ParameterizedType) genType).getActualTypeArguments()[0];
            } else {
                JsonConvert<T> convert = new JsonConvert<>(clazz);
                return convert.convertResponse(response);
            }
        }

        JsonConvert<T> convert = new JsonConvert<>(type);
        return convert.convertResponse(response);
    }

    @Override
    public void onError(com.lzy.okgo.model.Response<T> response) {
        Throwable exception = response.getException();
        if (exception != null) {
            exception.printStackTrace();
        }
        if (exception instanceof UnknownHostException || exception instanceof ConnectException) {
            ToastUtil.showShort("网络连接失败");
        } else if (exception instanceof SocketTimeoutException) {
            ToastUtil.showShort("网络请求超时");
        } else if (exception instanceof HttpException) {
            ToastUtil.showShort("服务端响应码404或者500了");
        }else if (exception instanceof NoRouteToHostException){
            ToastUtil.showShort("确认是否开启了代理");
        }
        super.onError(response);
        String message = response.getException().getMessage();
        if (HttpCode.login_failure.equals(message)) {
            // TODO: 2018/11/14 这里统一删除用户信息
            LoginHelp.loginOut();
            needLogin();
        } else if (TextUtils.equals(HttpCode.no_bind_phone, message)) {
            BindPhone();
        } else {
            super.onError(response);
        }

    }

    public void BindPhone() {
        BindPhoneDialog dialog = new BindPhoneDialog(MyApplication.getInstance().getRunningActivity());
        dialog.show();
    }

    public void needLogin(){}
}
