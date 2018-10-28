package com.caotu.duanzhi.Http;

import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.config.HttpApi;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.request.PostRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 为了统一接口请求类,所有的接口请求都往这里走
 *
 */
public class CommonHttpRequest {
    private static final CommonHttpRequest instance = new CommonHttpRequest();
    private HashMap<String, String> params;

    private CommonHttpRequest() {
    }

    public static CommonHttpRequest getInstance() {
        return instance;
    }

    public HashMap<String, String> getHashMapParams() {
        if (params == null) {
            params = new HashMap<>(8);
        } else {
            params.clear();
        }
        return params;
    }

    /**
     * 点赞和踩的接口请求
     *
     * @param userId
     * @param contentId
     * @param islike
     */
    public <T> void requestLikeOrUnlike(String userId, String contentId, boolean islike, JsonCallback<BaseResponseBean<T>> callback) {
// TODO: 2018/10/28 请求接口前需要判断是否登录,或者接口返回登录失效
        HashMap<String, String> params = getHashMapParams();
        params.put("contuid", userId);
        params.put("badid", contentId);
        params.put("badtype", "1");

        OkGo.<BaseResponseBean<T>>post(islike ? HttpApi.PARISE : HttpApi.UNPARISE)
                .headers("OPERATE", islike ? "GOOD" : "BAD")
                .headers("VALUE", contentId)
                .upJson(new JSONObject(params))
                .execute(callback);

    }

    /**
     * 评论的点赞请求
     */
    public <T> void requestCommentsLike(String userId, String contentId, JsonCallback<BaseResponseBean<T>> callback) {
        HashMap<String, String> params = getHashMapParams();
        params.put("contuid", userId);
        params.put("badid", contentId);
        params.put("badtype", "2");
        OkGo.<BaseResponseBean<T>>post(HttpApi.PARISE)
                .upJson(new JSONObject(params))
                .execute(callback);
    }


    /**
     * 有对象要解析的
     *
     * @param url
     * @param headers
     * @param requestBody
     */
    public <T> void httpRequest(String url, HttpHeaders headers, Map requestBody, JsonCallback<BaseResponseBean<T>> callback) {
        PostRequest<BaseResponseBean<T>> post = OkGo.post(url);
        if (headers != null) {
            post.headers(headers);
        }
        if (requestBody != null) {
            post.upJson(new JSONObject(requestBody));
        }
        post.execute(callback);
    }
}
