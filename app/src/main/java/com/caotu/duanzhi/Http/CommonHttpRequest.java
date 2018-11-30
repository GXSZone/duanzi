package com.caotu.duanzhi.Http;

import android.support.annotation.NonNull;

import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.NoticeBean;
import com.caotu.duanzhi.Http.bean.ShareUrlBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;

import org.json.JSONObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

/**
 * 为了统一接口请求类,所有的接口请求都往这里走
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
     * 点赞和踩内容的接口请求
     *
     * @param userId
     * @param contentId
     * @param isLikeView 是点赞还是踩的View操作
     * @param isSure     是取消操作还是确认操作,外面都是传控件的状态,所以要取反
     */
    public void requestLikeOrUnlike(String userId, String contentId,
                                    boolean isLikeView, boolean isSure, JsonCallback<BaseResponseBean<String>> callback) {
        HashMap<String, String> params = getHashMapParams();
        params.put("contuid", userId);
        if (isLikeView) {
            params.put("goodid", contentId);
            params.put("goodtype", "1");
        } else {
            params.put("badid", contentId);
            params.put("badtype", "1");
        }
        String url;
        if (isLikeView) {
            if (isSure) {
                url = HttpApi.CANCEL_PARISE;
            } else {
                url = HttpApi.PARISE;
            }
        } else {
            if (isSure) {
                url = HttpApi.CANCEL_UNPARISE;
            } else {
                url = HttpApi.UNPARISE;
            }
        }
        OkGo.<BaseResponseBean<String>>post(url)
                .headers("OPERATE", isLikeView ? "GOOD" : "BAD")
                .headers("VALUE", contentId)
                .upJson(new JSONObject(params))
                .execute(callback);

    }

    /**
     * 评论的点赞请求
     *
     * @param userId    用户ID
     * @param contentId 内容ID
     * @param commentId 评论ID
     * @param islike
     * @param callback
     */
    public void requestCommentsLike(String userId, String contentId, String commentId, boolean islike, @NonNull JsonCallback<BaseResponseBean<String>> callback) {
        HashMap<String, String> params = getHashMapParams();
        params.put("contuid", userId);
        params.put("cid", contentId);//仅在点赞评论时传此参数，作品id
        params.put("goodid", commentId);//作品或评论Id
        params.put("goodtype", "2");// 1_作品 2_评论
        OkGo.<BaseResponseBean<String>>post(islike ? HttpApi.CANCEL_PARISE : HttpApi.PARISE)
                .upJson(new JSONObject(params))
                .execute(callback);
    }

    /**
     * 关注按钮的接口请求
     * focus_or_cancle 为true则是关注.false则是取消关注
     */
    public <T> void requestFocus(String userId, String type, boolean focus_or_cancle, JsonCallback<BaseResponseBean<T>> callback) {
        HashMap<String, String> params = getHashMapParams();
        params.put("followid", userId);
        params.put("followtype", type);//1_主题 2_用户

        OkGo.<BaseResponseBean<T>>post(focus_or_cancle ? HttpApi.FOCUS_FOCUS : HttpApi.FOCUS_UNFOCUS)
                .upJson(new JSONObject(params))
                .execute(callback);
    }

    /**
     * 获取分享链接
     *
     * @param contentId
     * @param jsonCallback
     */
    public void getShareUrl(String contentId, JsonCallback<BaseResponseBean<ShareUrlBean>> jsonCallback) {
//        Map<String, String> map = getHashMapParams();
//        map.put("contendid", contentId);
        String name = "NH";
        if (DevicesUtils.getString(R.string.app_name).equals("皮皮段子")) {
            name = "PP";
        }
        OkGo.<BaseResponseBean<ShareUrlBean>>post(HttpApi.GET_SHARE_URL)
                .headers("APP", name)
//                .upJson(new JSONObject(map))
                .execute(jsonCallback);
    }

    /**
     * 分享统计
     * SHARE(分享内容),CSHARE(评论分享)
     *
     * @param momentsId
     */
    public void requestShare(String momentsId, int type) {
        HashMap<String, String> hashMapParams = getHashMapParams();
        hashMapParams.put("contentid", momentsId);
        OkGo.<String>post(HttpApi.GET_COUNT_SHARE)
                .headers("OPERATE", type == 1 ? "CSHARE" : "SHARE")
                .headers("VALUE", momentsId)
                .upJson(new JSONObject(hashMapParams))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
//                        String code = response.body().optString("code");
                    }
                });
    }

    /**
     * 收藏的结果还是要的
     *
     * @param contentId
     * @param isCollect
     */
    public void collectionContent(String contentId, boolean isCollect, @NonNull JsonCallback<BaseResponseBean<String>> callback) {
        HashMap<String, String> hashMapParams = getHashMapParams();
        hashMapParams.put("contentid", contentId);
        OkGo.<BaseResponseBean<String>>post(isCollect ? HttpApi.COLLECTION_CONTENT : HttpApi.UNCOLLECTION_CONTENT)
                .upJson(new JSONObject(hashMapParams))
                .execute(callback);
    }

    /**
     * 播放时请求接口计数
     */
    public void requestPlayCount(String momentsId) {
        HashMap<String, String> hashMapParams = CommonHttpRequest.getInstance().getHashMapParams();
        hashMapParams.put("contentid", momentsId);
        OkGo.<String>post(HttpApi.PLAY_COUNT)
                .headers("OPERATE", "PLAY")
                //推荐PUSH  图片PIC  视频VIE   段子WORD
                .headers("LOC", "PUSH")
                .headers("VALUE", momentsId)
                .upJson(new JSONObject(hashMapParams))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
//                        String code = response.body().optString("code");
                    }
                });
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface TabType {
        String recommend = "PUSH";
        String video = "VIE";
        String photo = "PIC";
        String text = "WORD";
    }

    /**
     * 跳转详情次数统计
     */
    public void requestPlayCount(String momentsId, @TabType String type) {
        HashMap<String, String> hashMapParams = CommonHttpRequest.getInstance().getHashMapParams();
        hashMapParams.put("contentid", momentsId);
        OkGo.<String>post(HttpApi.PLAY_COUNT)
                .headers("OPERATE", "PLAY")
                //推荐 PUSH  图片 PIC  视频 VIE   段子 WORD
                .headers("LOC", type)
                .headers("VALUE", momentsId)
                .upJson(new JSONObject(hashMapParams))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
//                        String code = response.body().optString("code");
                    }
                });
    }

    /**
     * 请求未读消息数
     *
     * @param callback
     */
    public void requestNoticeCount(JsonCallback<BaseResponseBean<NoticeBean>> callback) {
        OkGo.<BaseResponseBean<NoticeBean>>post(HttpApi.NOTICE_UNREADED_COUNT)
                .execute(callback);
    }

    /**
     * 删除作品
     *
     * @param contentId
     */
    public void deletePost(String contentId) {
        HashMap<String, String> params = getHashMapParams();
        params.put("contentid", contentId);
        OkGo.<BaseResponseBean<String>>post(HttpApi.WORKSHOW_DELETE)
                .upJson(new JSONObject(params))
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        ToastUtil.showShort("删除作品成功");
                    }
                });
    }

    /**
     * @param contentId
     * @param reportType
     * @param type       举报类型:评论还是内容,内容是0,评论是1
     */
    public void requestReport(String contentId, String reportType, int type) {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("cid", contentId);//举报作品id
        map.put("desc", reportType);//举报描述
        map.put("reporttype", type == 1 ? "2" : "1");//举报类型 1_作品 2_评论
        OkGo.<BaseResponseBean<String>>post(HttpApi.DO_INFORM)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        ToastUtil.showShort("举报成功！");
                    }
                });
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
