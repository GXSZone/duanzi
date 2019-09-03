package com.caotu.duanzhi.Http;

import android.app.Activity;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.NoticeBean;
import com.caotu.duanzhi.Http.bean.ShareUrlBean;
import com.caotu.duanzhi.Http.bean.UrlCheckBean;
import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.home.MainActivity;
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
     * 点赞和踩内容的接口请求,之前的埋点操作都移到点击事件去了,设置tag
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
            url = isSure ? HttpApi.CANCEL_PARISE : HttpApi.PARISE;
        } else {
            url = isSure ? HttpApi.CANCEL_UNPARISE : HttpApi.UNPARISE;
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
//        if (focus_or_cancle) {
//            if (TextUtils.equals("2", type)) {
//                UmengHelper.event(UmengStatisticsKeyIds.follow_user);
//            } else {
//                UmengHelper.event(UmengStatisticsKeyIds.follow_topic);
//            }
//        }

        OkGo.<BaseResponseBean<T>>post(focus_or_cancle ? HttpApi.FOCUS_FOCUS : HttpApi.FOCUS_UNFOCUS)
                .upJson(new JSONObject(params))
                .execute(callback);
    }

    public static String url;
    public static String cmt_url;

    public void getShareUrl() {
//        url = "https://v3.toutushare.com/share_connotation/shareindex5.html";
//        cmt_url = "https://v3.toutushare.com/share_connotation/sharecomment5.html";
        OkGo.<BaseResponseBean<ShareUrlBean>>post(HttpApi.GET_SHARE_URL)
                .headers("APP", BaseConfig.APP_NAME)
                .execute(new JsonCallback<BaseResponseBean<ShareUrlBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<ShareUrlBean>> response) {
                        url = response.body().getData().getUrl();
                        cmt_url = response.body().getData().getCmt_url();
                    }
                });
    }

    public void splashCount(String value) {
        HashMap<String, String> params = getHashMapParams();
        params.put("pagestr", value);
        OkGo.<String>post(HttpApi.COUNTNUMBER)
                .upJson(new JSONObject(params))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        //不关注结果
                    }
                });
    }

    /**
     * 发现页的话题统计多个字段,单独区分开
     *
     * @param id
     */
    public void discoverStatistics(String id) {
        HashMap<String, String> params = getHashMapParams();
        params.put("pagestr", id);
        params.put("ctype", "HT");
        OkGo.<String>post(HttpApi.COUNTNUMBER)
                .upJson(new JSONObject(params))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        //不关注结果
                    }
                });
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface AppType {
        String home_all = "ALL";
        String home_video = "VIDEO";
        String home_pic = "PIC";
        String home_word = "WORD";

        String discover_find = "FIND";
        String discover_search = "SEARCH";

        String msg_like = "GOOD";
        String msg_follow = "FOLLOW";
        String msg_comment = "COMMENT";

        String mine_me = "ME";
        String mine_follow = "MFOLLOW";
        String mine_fan = "FANS";
        String mine_content = "CONTENT";
        String mine_comment = " MCOMMENT";
        String mine_history = "MHISTORY";
        String mine_recomment = "MPUSH";
        String mine_help = "MHELP";
        String mine_set = "MSET";
        String mine_collect = "MCOLLECT";

        String push_like = "PGOOD";
        String push_follow = "PFOLLOW";
        String push_comment = "PCOMMENT";

        String download_video = "OVIDEO";
        String download_pic = "OPIC";

    }

    public void statisticsApp(@AppType String page) {
        HashMap<String, String> params = getHashMapParams();
        params.put("pagestr", page);
        params.put("ctype", "OP");
        OkGo.<String>post(HttpApi.COUNTNUMBER)
                .upJson(new JSONObject(params))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        //不关注结果
                    }
                });
        /*
        .headers("OPERATE", "DOWNLOAD")
                //推荐PUSH  图片PIC  视频VIE   段子WORD
                .headers("LOC", "PUSH")
                .headers("VALUE", momentsId)
         */
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
        if (TextUtils.isEmpty(momentsId)) return;
        MyApplication.getInstance().putHistory(momentsId);
        HashMap<String, String> hashMapParams = getHashMapParams();
        hashMapParams.put("contentid", momentsId);
        OkGo.<String>post(HttpApi.PLAY_COUNT)
                .headers("OPERATE", "PLAY")
                //推荐PUSH  图片PIC  视频VIE   段子WORD
                .headers("LOC", "")
                .headers("VALUE", momentsId)
                .upJson(new JSONObject(hashMapParams))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
//                        String code = response.body().optString("code");
                    }
                });
    }

    public void requestDownLoad(String momentsId, String type) {
        String page = "";
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        if (runningActivity instanceof MainActivity &&
                ((MainActivity) runningActivity).getCurrentTab() == 0) {
            int homeFragmentTab = ((MainActivity) runningActivity).getHomeFragment();
            switch (homeFragmentTab) {
                case 1:
                    page = CommonHttpRequest.TabType.video;
                    break;
                case 2:
                    page = CommonHttpRequest.TabType.photo;
                    break;
                case 3:
                    page = CommonHttpRequest.TabType.text;
                    break;
                default:
                    page = CommonHttpRequest.TabType.recommend;
                    break;
            }

        }
        HashMap<String, String> params = getHashMapParams();
        params.put("pagestr", type);
        params.put("ctype", "OP");
        OkGo.<String>post(HttpApi.COUNTNUMBER)
                .headers("OPERATE", "DOWNLOAD")
                //推荐PUSH  图片PIC  视频VIE   段子WORD
                .headers("LOC", page)
                .headers("VALUE", momentsId)
                .upJson(new JSONObject(params))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        //不关注结果
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
     * 删除评论
     *
     * @param commentId
     */
    public void deleteComment(String commentId, JsonCallback<BaseResponseBean<String>> callback) {
        HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
        params.put("cmtid", commentId);
        OkGo.<BaseResponseBean<String>>post(HttpApi.COMMENT_DELETE)
                .upJson(new JSONObject(params))
                .execute(callback);
    }

    public void deleteComment(String commentId) {
        HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
        params.put("cmtid", commentId);
        OkGo.<BaseResponseBean<String>>post(HttpApi.COMMENT_DELETE)
                .upJson(new JSONObject(params))
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {

                    }
                });
    }

    /**
     * @param contentId
     * @param reportType
     * @param type       举报类型:评论还是内容,内容是0,评论是1
     */
    public void requestReport(String contentId, String reportType, int type) {
        Map<String, String> map = getHashMapParams();
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

    public void requestReport(String contentId, String reportType, int type, String text) {
        Map<String, String> map = getHashMapParams();
        map.put("cid", contentId);//举报作品id
        map.put("desc", reportType);//举报描述
        map.put("reporttype", type == 1 ? "2" : "1");//举报类型 1_作品 2_评论
        map.put("text", text);
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
     * 校验url
     *
     * @param url
     * @param callback
     */
    public void checkUrl(String url, JsonCallback<BaseResponseBean<UrlCheckBean>> callback) {
        Map<String, String> map = getHashMapParams();
        map.put("linkurl", url);
        OkGo.<BaseResponseBean<UrlCheckBean>>post(HttpApi.URL_CHECK)
                .upJson(new JSONObject(map))
                .execute(callback);
    }

    /**
     * 推送回调给接口
     *
     * @param pushId
     */
    public void notifyInterface(String pushId) {
        Map<String, String> map = getHashMapParams();
        map.put("pushid", pushId);
        OkGo.<String>post(HttpApi.PUSH_OPEN)
                .upJson(new JSONObject(map))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                    }
                });
    }

    public void noticeSetting(Map<String, String> key) {
        OkGo.<String>post(HttpApi.NOTICE_SETTING)
                .upJson(new JSONObject(key))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                    }
                });
    }

    /**
     * 青少年模式相关字段
     */
    public static boolean teenagerIsOpen;
    public static String teenagerPsd;

    /**
     * 绑定青少年模式数据,来自用户数据bean
     */
    public void setTeenagerDateByUerInfo(boolean isOpen, String psd) {
        teenagerIsOpen = isOpen;
        teenagerPsd = psd;
    }

    /**
     * 为了获取青少年模式的配置
     */
    public void getUserInfo() {
        OkGo.<BaseResponseBean<UserBaseInfoBean>>post(HttpApi.GET_USER_BASE_INFO)
                .upJson("{}")
                .execute(new JsonCallback<BaseResponseBean<UserBaseInfoBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<UserBaseInfoBean>> response) {
                        UserBaseInfoBean data = response.body().getData();
                        if (data != null && data.getUserInfo() != null) {
                            UserBaseInfoBean.UserInfoBean userInfo = data.getUserInfo();
                            //设置青少年模式数据
                            setTeenagerDateByUerInfo(
                                    TextUtils.equals("1", userInfo.youngmod),
                                    userInfo.youngpsd);
                            // TODO: 2019-09-03 这里其实还得考虑一种其他情况,就是网络不好的时候,有延迟
//                            EventBusHelp.sendTeenagerEvent(TextUtils.equals("1", userInfo.youngmod));
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<UserBaseInfoBean>> response) {
//                        super.onError(response);
                    }
                });
    }

    public static boolean canGoHot = true;

    /**
     * 上热门接口请求
     *
     * @param contentid
     */
    public void goHot(String contentid) {
        HashMap<String, String> hashMapParams = getHashMapParams();
        hashMapParams.put("contentid", contentid);
//        OkGo.<String>post(HttpApi.NOTICE_SETTING)
//                .upJson(new JSONObject(hashMapParams))
//                .execute(new StringCallback() {
//                    @Override
//                    public void onSuccess(Response<String> response) {
//                        canGoHot = true;
//                    }
//
//                    @Override
//                    public void onError(Response<String> response) {
//                        // TODO: 2019-09-02 有很多提示
//                        ToastUtil.showShort(response.message());
//                    }
//                });
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

    public <T> void httpPostRequest(String url, Map requestBody, JsonCallback<BaseResponseBean<T>> callback) {
        PostRequest<BaseResponseBean<T>> post = OkGo.post(url);
        if (requestBody != null) {
            post.upJson(new JSONObject(requestBody));
        }
        post.execute(callback);
    }
}
