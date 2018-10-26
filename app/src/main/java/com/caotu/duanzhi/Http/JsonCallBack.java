package com.caotu.duanzhi.Http;

import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.utils.LogUtil;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.lzy.okgo.callback.AbsCallback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class JsonCallBack<T> extends AbsCallback<T> {


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

        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        Type type = params[0];
        if (!(type instanceof ParameterizedType)) {
            throw new IllegalStateException("没有填写泛型参数");
        }
        //解析的是外层basebean
        Type rawType = ((ParameterizedType) type).getRawType();
        //获取内部真实泛型数据
        Type typeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];
        ResponseBody body = response.body();
        if (body == null) {
            return null;
        }
        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(body.charStream());
        if (rawType != BaseResponseBean.class) {
            T o = gson.fromJson(jsonReader, type);
            response.close();
            return o;
        } else {
            BaseResponseBean bean = gson.fromJson(jsonReader, type);
            response.close();
            String code = bean.getCode();
            if ("1000".equals(code)) {
                return (T) bean;
            } else {
                throw new IllegalStateException(code);
            }
        }
    }

    @Override
    public void onError(com.lzy.okgo.model.Response<T> response) {
        Throwable exception = response.getException();
        if (exception != null) {
            exception.printStackTrace();
        }
        if (exception instanceof UnknownHostException) {
            LogUtil.logString("url 地址错误");
        } else if (exception instanceof SocketTimeoutException) {
            LogUtil.logString("网络请求超时");
        } else {
            super.onError(response);
        }
    }
}
