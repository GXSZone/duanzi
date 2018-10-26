package com.caotu.duanzhi.Http;

import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.utils.ToastUtil;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CommonHttpRequest {
    private static final CommonHttpRequest instance = new CommonHttpRequest();
    Gson gsonBean;

    private CommonHttpRequest() {
    }

    public CommonHttpRequest getInstance() {
        return instance;
    }

    interface HttpCallBack<T> {

        void success(T bean);

        void error(String msg);

    }


    public Gson getGsonBean() {
        if (gsonBean == null) {
            gsonBean = new Gson();
        }
        return gsonBean;
    }

    /**
     * 点赞和踩的接口请求
     *
     * @param userId
     * @param contentId
     * @param islike
     * @param callBack
     */
    public void requestLikeOrUnlike(String userId, String contentId, final boolean islike, final HttpCallBack callBack) {

        HashMap<String, String> params = new HashMap<>();
        params.put("contuid", userId);
        params.put("badid", contentId);
        params.put("badtype", "1");

        try {
            OkGo.<String>post(islike ? HttpApi.PARISE : HttpApi.UNPARISE)
                    .headers("OPERATE", "BAD")
                    .headers("VALUE", contentId)
                    .upJson(new JSONObject(params))
                    .execute(new StringCallback() {

                        @Override
                        public void onSuccess(Response<String> response) {
                            String body = response.body();
                            BaseResponseBean responseBean = getGsonBean().fromJson(body, BaseResponseBean.class);
                            String code = responseBean.getCode();
                            if ("1000".equals(code)) {
                                if (callBack != null) {
                                    callBack.success(null);
                                }
                            } else if ("3007".equals(code)) {
                                ToastUtil.showShort("操作失败，正在审核中！");
                            } else {
                                ToastUtil.showShort(islike ? "踩失败！" : "点赞失败！");
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            if (callBack != null) {
                                callBack.error(response.getException().getMessage());
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 评论的点赞请求
     */
    public void requestCommentsLike(String userId, String contentId, final HttpCallBack callBack) {
        HashMap<String, String> params = new HashMap<>();
        params.put("contuid", userId);
        params.put("badid", contentId);
        params.put("badtype", "2");
        try {
            OkGo.<String>post(HttpApi.PARISE)
                    .upJson(new JSONObject(params))
                    .execute(new StringCallback() {

                        @Override
                        public void onSuccess(Response<String> response) {
                            String body = response.body();
                            BaseResponseBean responseBean = getGsonBean().fromJson(body, BaseResponseBean.class);
                            String code = responseBean.getCode();
                            if ("1000".equals(code)) {
                                if (callBack != null) {
                                    callBack.success(null);
                                }
                            } else if ("3007".equals(code)) {
                                ToastUtil.showShort("操作失败，正在审核中！");
                            } else {
                                ToastUtil.showShort("点赞失败！");
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            if (callBack != null) {
                                callBack.error(response.message());
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 有对象要解析的
     *
     * @param url
     * @param headers
     * @param requestBody
     * @param callBack
     * @param <T>
     */
    public <T> void httpRequest(String url, HttpHeaders headers, final Map requestBody, final HttpCallBack<T> callBack) {
        PostRequest<BaseResponseBean<T>> post = OkGo.post(url);
        if (headers != null) {
            post.headers(headers);
        }
        if (requestBody != null) {
            post.upJson(new JSONObject(requestBody));
        }
        post.execute(new JsonCallBack<BaseResponseBean<T>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<T>> response) {
                if (callBack != null) {
                    BaseResponseBean<T> body = response.body();
                    callBack.success(body.getData());
                }
            }

            @Override
            public void onError(Response<BaseResponseBean<T>> response) {
                super.onError(response);
                if (callBack != null) {
                    callBack.error(response.body().getMessage());
                }
            }
        });
    }
}
