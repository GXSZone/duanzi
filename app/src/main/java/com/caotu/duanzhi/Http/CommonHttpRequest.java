package com.caotu.duanzhi.Http;

import android.app.Activity;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.GoHotBean;
import com.caotu.duanzhi.Http.bean.InterestUserBean;
import com.caotu.duanzhi.Http.bean.NoticeBean;
import com.caotu.duanzhi.Http.bean.ShareUrlBean;
import com.caotu.duanzhi.Http.bean.UrlCheckBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.detail_scroll.ContentNewDetailActivity;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;

import org.json.JSONObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.List;
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
                .headers("LOC", isSure ? "" : getRecommendType())
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
                .headers("OPERATE", !islike ? "GOOD" : "BAD")
                .headers("VALUE", contentId)
                .headers("LOC", islike ? "" : getRecommendType())
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
                .headers("OPERATE", focus_or_cancle ? "FOLLOW" : "")
                .headers("LOC", focus_or_cancle ? getRecommendType() : "")
                .upJson(new JSONObject(params))
                .execute(callback);
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
    }

    /**
     * 分享统计
     * SHARE(分享内容),CSHARE(评论分享)
     * 一开始做区分,现在又不要区分开了,迷之操作的后台统计
     *
     * @param momentsId
     */
    public void requestShare(String momentsId, int type) {
        HashMap<String, String> hashMapParams = getHashMapParams();
        hashMapParams.put("contentid", momentsId);
        OkGo.<String>post(HttpApi.GET_COUNT_SHARE)
//                .headers("OPERATE", type == 1 ? "CSHARE" : "SHARE")
                .headers("OPERATE", "SHARE")
                .headers("VALUE", momentsId)
                .headers("LOC", getRecommendType())
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
                .headers("OPERATE", isCollect ? "COLLECT" : "UNCOLLECT")
                .headers("VALUE", contentId)
                .headers("LOC", getRecommendType())
                .execute(callback);
    }


    public void requestDownLoad(String momentsId, String type) {
        String page = getOriginType();
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

    public @TabType
    String getOriginType() {
        String page = "";
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        if (runningActivity instanceof MainActivity &&
                ((MainActivity) runningActivity).getCurrentTab() == 0) {
            int homeFragmentTab = ((MainActivity) runningActivity).getHomeFragment();
            switch (homeFragmentTab) {
                case 1:
                    page = TabType.video;
                    break;
                case 2:
                    page = TabType.photo;
                    break;
                case 3:
                    page = TabType.text;
                    break;
                default:
                    page = TabType.recommend;
                    break;
            }

        }
        return page;
    }

    /**
     * 针对首页推荐列表做埋点处理
     *
     * @return
     */
    public String getRecommendType() {
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        if (runningActivity instanceof MainActivity &&
                ((MainActivity) runningActivity).getCurrentTab() == 0) {
            int homeFragmentTab = ((MainActivity) runningActivity).getHomeFragment();
            if (homeFragmentTab == 0) {
                return TabType.recommend;
            }
        }

        Activity lastSecondActivity = MyApplication.getInstance().getLastSecondActivity();
        if (runningActivity instanceof ContentNewDetailActivity
                && lastSecondActivity instanceof MainActivity
                && ((MainActivity) lastSecondActivity).getCurrentTab() == 0) {
            int homeFragmentTab = ((MainActivity) lastSecondActivity).getHomeFragment();
            if (homeFragmentTab == 0) {
                return TabType.recommend;
            }
        }
        return "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.METHOD})
    public @interface TabType {
        String recommend = "PUSH";
        String video = "VIE";
        String photo = "PIC";
        String text = "WORD";
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
                .headers("LOC", getRecommendType())
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
     * 删除评论,需要回调和不需要回调两种
     *
     * @param commentId
     */
    public void deleteComment(String commentId, JsonCallback<BaseResponseBean<String>> callback) {
        HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
        params.put("cmtid", commentId);
        PostRequest<BaseResponseBean<String>> postRequest = OkGo.<BaseResponseBean<String>>post(HttpApi.COMMENT_DELETE)
                .upJson(new JSONObject(params));
        if (callback == null) {
            postRequest.execute(new JsonCallback<BaseResponseBean<String>>() {
                @Override
                public void onSuccess(Response<BaseResponseBean<String>> response) {

                }
            });
        } else {
            postRequest.execute(callback);
        }
    }

    public void requestReport(String contentId, String reportType, int type, String text) {
        Map<String, String> map = getHashMapParams();
        map.put("cid", contentId);//举报作品id
        map.put("desc", reportType);//举报描述
        map.put("reporttype", String.valueOf(type));//举报类型 1_作品 2_评论 3_举报人
        map.put("text", text);
        OkGo.<BaseResponseBean<String>>post(HttpApi.DO_INFORM)
                .upJson(new JSONObject(map))
                .headers("OPERATE", "REPORT")
                .headers("VALUE", contentId)
                .headers("LOC", getRecommendType())
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
    public static boolean canGoHot = true;
    public static String url;
    public static String cmt_url;
    public static String[] sensitiveWord; //敏感词
    public static String nhsqrz_url;
    //评论热词数组
    public static List<String> hotComments;

    /**
     * 该接口改为获取用户相关的配置接口
     */
    public void getShareUrl() {
        OkGo.<BaseResponseBean<ShareUrlBean>>post(HttpApi.GET_SHARE_URL)
                .execute(new JsonCallback<BaseResponseBean<ShareUrlBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<ShareUrlBean>> response) {
                        ShareUrlBean data = response.body().getData();
                        if (data == null) return;
                        hotComments = data.hotCommentList;
                        url = data.url;
                        cmt_url = data.cmt_url;
                        canGoHot = LikeAndUnlikeUtil.isLiked(data.gohot);
                        nhsqrz_url = data.nhsqrz_url;
                        //设置青少年模式数据
                        boolean isOpen = TextUtils.equals("1", data.youngmod);
                        teenagerIsOpen = isOpen;
                        teenagerPsd = data.youngpsd;
                        // TODO: 2019-09-03 就是网络不好的时候,有延迟,切换账号重新获取
                        EventBusHelp.sendTeenagerEvent(isOpen);
                        //帮助 ，用户协议，社区公约
                        if (!TextUtils.isEmpty(data.sisword)) {
                            sensitiveWord = data.sisword.split(",");
                        }
                        getWebUrls(data);
                    }
                });
    }

    public void getWebUrls(ShareUrlBean data) {
        if (TextUtils.isEmpty(data.daily_url)) return;
        String[] url = data.daily_url.split(",");
        for (int i = 0; i < url.length; i++) {
            if (TextUtils.isEmpty(url[i])) {
                break;
            }
            if (i == 0) {
                BaseConfig.KEY_FEEDBACK = url[i];
            } else if (i == 1) {
                BaseConfig.KEY_USER_AGREEMENT = url[i];
            } else if (i == 2) {
                BaseConfig.COMMUNITY_CONVENTION = url[i];
            }
        }
    }

    /**
     * 绑定青少年模式数据,来自用户数据bean
     */
    public void setTeenagerDateByUerInfo(boolean isOpen, String psd) {
        teenagerIsOpen = isOpen;
        teenagerPsd = psd;
    }

    /**
     * 上热门接口请求
     *
     * @param contentid
     */
    public void goHot(String contentid) {
        HashMap<String, String> hashMapParams = getHashMapParams();
        hashMapParams.put("hotid", contentid);
        OkGo.<BaseResponseBean<GoHotBean>>post(HttpApi.GO_HOT)
                .headers("OPERATE", "GOHOT")
                //推荐 PUSH  图片 PIC  视频 VIE   段子 WORD
                .headers("LOC", getRecommendType())
                .headers("VALUE", contentid)
                .upJson(new JSONObject(hashMapParams))
                .execute(new JsonCallback<BaseResponseBean<GoHotBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<GoHotBean>> response) {
                        GoHotBean data = response.body().getData();
                        if (data == null
                                || TextUtils.isEmpty(data.times)
                                || TextUtils.equals("0", data.times)) {
                            ToastUtil.showShort("今天的推荐次数用完了，咱明天继续！");
                            canGoHot = false;
                        } else if (!TextUtils.isEmpty(data.times)) {
                            ToastUtil.showShort("推荐成功，今日还剩" + data.times + "次推荐机会！");
                            canGoHot = true;
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<GoHotBean>> response) {
                        canGoHot = false;
                        ToastUtil.showShort(response.getException().getMessage());
                        super.onError(response);
                    }
                });
    }

    List<InterestUserBean.UserBeanIn> list;

    public List<InterestUserBean.UserBeanIn> getList() {
        return list;
    }

    /**
     * 获取感兴趣的用户存在本地,可能还要区分未登录状态
     */
    public void getInterestingUsers(JsonCallback<BaseResponseBean<InterestUserBean>> callback) {
        HashMap<String, String> params = getHashMapParams();
        params.put("Yes", "yes");
        PostRequest<BaseResponseBean<InterestUserBean>> request = OkGo.<BaseResponseBean<InterestUserBean>>post(HttpApi.INTEREST_USER)
                .upJson(new JSONObject(params));
        if (callback == null) {
            request.execute(new JsonCallback<BaseResponseBean<InterestUserBean>>() {
                @Override
                public void onSuccess(Response<BaseResponseBean<InterestUserBean>> response) {
                    list = response.body().getData().userlist;
                }
            });
        } else {
            request.execute(callback);
        }
    }

    /**
     * 设置接口
     *
     * @param isCheck
     */
    public void changeSwitchBySetting(boolean isCheck) {
        HashMap<String, String> params = getHashMapParams();
        if (isCheck) {
            params.put("collectionswitch", "1");
        } else {
            params.put("collectionswitch", "0");
        }
        OkGo.<String>post(HttpApi.SET_USER_BASE_INFO)
                .upJson(new JSONObject(params))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                    }
                });
    }

    public <T> void httpPostRequest(String url, Map requestBody, JsonCallback<BaseResponseBean<T>> callback) {
        PostRequest<BaseResponseBean<T>> post = OkGo.post(url);
        if (requestBody != null) {
            post.upJson(new JSONObject(requestBody));
        }
        post.execute(callback);
    }
}
