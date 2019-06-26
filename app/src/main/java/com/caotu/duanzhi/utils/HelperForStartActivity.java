package com.caotu.duanzhi.utils;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.TopicItemBean;
import com.caotu.duanzhi.Http.bean.UrlCheckBean;
import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.FullScreenActivity;
import com.caotu.duanzhi.module.detail.CommentDetailActivity;
import com.caotu.duanzhi.module.detail.ContentDetailActivity;
import com.caotu.duanzhi.module.detail.UgcDetailActivity;
import com.caotu.duanzhi.module.detail_scroll.BigDateList;
import com.caotu.duanzhi.module.detail_scroll.ContentNewDetailActivity;
import com.caotu.duanzhi.module.download.VideoFileReadyServices;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.login.BindPhoneAndForgetPwdActivity;
import com.caotu.duanzhi.module.mine.BaseBigTitleActivity;
import com.caotu.duanzhi.module.mine.FocusActivity;
import com.caotu.duanzhi.module.mine.HelpAndFeedbackActivity;
import com.caotu.duanzhi.module.mine.MedalDetailActivity;
import com.caotu.duanzhi.module.mine.NoticeSettingActivity;
import com.caotu.duanzhi.module.mine.SettingActivity;
import com.caotu.duanzhi.module.mine.ShareCardToFriendActivity;
import com.caotu.duanzhi.module.notice.NoticeHeaderActivity;
import com.caotu.duanzhi.module.other.OtherActivity;
import com.caotu.duanzhi.module.other.UserDetailActivity;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.module.other.imagewatcher.ImageInfo;
import com.caotu.duanzhi.module.other.imagewatcher.PictureWatcherActivity;
import com.caotu.duanzhi.module.publish.PublishActivity;
import com.caotu.duanzhi.module.search.SearchActivity;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.lzy.okgo.model.Response;
import com.sunfusheng.widget.ImageData;

import java.util.ArrayList;


/**
 * @author mac
 * @日期: 2018/11/1
 * @describe 用于管理activity跳转传值管理
 */
public class HelperForStartActivity {

    public static final String key_other_type = "other_type";
    public static final String type_other_praise = "praise"; //多人点赞列表------得自己传多人bean对象
    public static final String type_other_user = "user";  //原来的other就是指用户
    public static final String type_other_topic = "topic"; //原来的theme就是指话题现在
    public static final String key_user_id = "userId";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_TO_COMMENT = "toComment";
    public static final String KEY_DETAIL_COMMENT = "detail_comment";
    public static final String KEY_VIDEO_PROGRESS = "video_progress";
    //    public static final String KEY_SCROLL_DETAIL = "scroll_detail";
    public static final String KEY_FROM_POSITION = "position";
    public static final String KEY_MEDAL_ID = "medal_id";
    //通知头布局跳转
    public static final String KEY_NOTICE_LIKE = "5";
    public static final String KEY_NOTICE_FOLLOW = "3";
    public static final String KEY_NOTICE_COMMENT = "2";
    public static final String KEY_NOTICE_OFFICIAL = "4";

    public static Activity getCurrentActivty() {
        return MyApplication.getInstance().getRunningActivity();
    }

    /**
     * 打开话题详情和他人主页,还有其他给你点赞的页面
     *
     * @param type
     * @param id
     */
    public static void openOther(String type, String id) {
        AppCompatActivity currentActivty = (AppCompatActivity) getCurrentActivty();
        // TODO: 2019-05-31 他人主页单独分开,不跟原先的混在一起了
        if (type_other_user.equals(type)) {
            UserDetailActivity.start(currentActivty, id);
            return;
        }
        // TODO: 2019/1/15 添加点击话题次数统计
        if (TextUtils.equals(type, type_other_topic)
                && currentActivty instanceof MainActivity
                && ((MainActivity) currentActivty).getCurrentTab() == 0) {
            CommonHttpRequest.getInstance().discoverStatistics("HOME" + id);
            UmengHelper.homeTpicEvent(id);
        }
        Intent intent = new Intent(currentActivty, OtherActivity.class);
        intent.putExtra(key_other_type, type);
        intent.putExtra(key_user_id, id);
        currentActivty.startActivity(intent);
    }

    public static void openOther(String type, String id, int friendCount) {
        // TODO: 2019/1/15 添加点击话题次数统计
        if (TextUtils.equals(type, type_other_topic) && getCurrentActivty() instanceof MainActivity) {
            CommonHttpRequest.getInstance().discoverStatistics("HOME" + id);
        }
        Intent intent = new Intent(getCurrentActivty(), OtherActivity.class);
        intent.putExtra(key_other_type, type);
        intent.putExtra(key_user_id, id);
        //点赞总人数需要外面传
        intent.putExtra("friendCount", friendCount);
        getCurrentActivty().startActivity(intent);
    }


    public static void openFromNotice(String type) {
        Intent intent = new Intent(getCurrentActivty(), NoticeHeaderActivity.class);
        intent.putExtra(key_other_type, type);
        getCurrentActivty().startActivity(intent);
    }

    public static void openFromNotice(String type, String friendId, String friendName) {
        Intent intent = new Intent(getCurrentActivty(), NoticeHeaderActivity.class);
        intent.putExtra(key_other_type, type);
        intent.putExtra("friendId", friendId);
        intent.putExtra("friendName", friendName);
        getCurrentActivty().startActivity(intent);
    }

    /**
     * 打开详情页面,跳转详情自己传bean对象,因为从消息和评论跳转过去有个置顶效果
     */
    public static void openContentDetail(MomentsDataBean bean) {
        if (bean == null) {
            return;
        }
        //0_正常 1_已删除 2_审核中
        if (TextUtils.equals(bean.getContentstatus(), "1")) {
            ToastUtil.showShort("该帖子已删除");
            return;
        }
        bean = DataTransformUtils.getContentNewBean(bean);
        dealRequestContent(bean.getContentid());
        Intent intent = new Intent(getCurrentActivty(), ContentDetailActivity.class);
        intent.putExtra(KEY_CONTENT, bean);
        getCurrentActivty().startActivity(intent);
    }

    /**
     * 外部跳详情只有id的情况
     *
     * @param id
     */
    public static void openContentDetail(String id) {
        Intent intent = new Intent(getCurrentActivty(), ContentDetailActivity.class);
        intent.putExtra("contentId", id);
        getCurrentActivty().startActivity(intent);
    }

    public static void openContentScrollDetail(ArrayList<MomentsDataBean> beanList, int position) {
        if (beanList == null) {
            return;
        }
        MomentsDataBean bean = beanList.get(position);
        //0_正常 1_已删除 2_审核中
        if (TextUtils.equals(bean.getContentstatus(), "1")) {
            ToastUtil.showShort("该帖子已删除");
            return;
        }
        dealRequestContent(bean.getContentid());
        Intent intent = new Intent(getCurrentActivty(), ContentNewDetailActivity.class);

        ArrayList<MomentsDataBean> dataBeans = new ArrayList<>(beanList.size());
        if (position != 0) {
            //用sublist api会修改原集合
            for (int i = position; i < beanList.size(); i++) {
                dataBeans.add(beanList.get(i));
            }
        } else {
            dataBeans.addAll(beanList);
        }
        BigDateList.getInstance().setBeans(dataBeans);
        intent.putExtra(KEY_FROM_POSITION, position);
        getCurrentActivty().startActivity(intent);
    }


    /**
     * 代码改动最少的解决方案,用于统计埋点
     *
     * @param contentid
     */
    public static void dealRequestContent(String contentid) {
        if (TextUtils.isEmpty(contentid)) return;
        MyApplication.getInstance().putHistory(contentid);
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        if (runningActivity instanceof MainActivity) {
            if (((MainActivity) runningActivity).getCurrentTab() != 0) return;
            int homeFragmentTab = ((MainActivity) runningActivity).getHomeFragment();
            String type;
            switch (homeFragmentTab) {
                case 1:
                    type = CommonHttpRequest.TabType.video;
                    break;
                case 2:
                    type = CommonHttpRequest.TabType.photo;
                    break;
                case 3:
                    type = CommonHttpRequest.TabType.text;
                    break;
                default:
                    type = CommonHttpRequest.TabType.recommend;
                    break;
            }
            CommonHttpRequest.getInstance().requestPlayCount(contentid, type);
        } else {
            CommonHttpRequest.getInstance().requestPlayCount(contentid);
        }

    }


    /**
     * 打开设置页面
     */
    public static void openSetting() {
        Intent intent = new Intent(getCurrentActivty(), SettingActivity.class);
        getCurrentActivty().startActivity(intent);
    }

    /**
     * 自己主页和他人主页都有这个跳转功能
     *
     * @param userId
     */
    public static void openFocus(String userId) {
        Intent intent = new Intent(getCurrentActivty(), FocusActivity.class);
//        intent.putExtra(key_is_mine, MySpUtils.isMe(userId));
        intent.putExtra(key_user_id, userId);
        getCurrentActivty().startActivity(intent);
    }

    /**
     * 打开粉丝页面.包括自己的和他人的
     *
     * @param userId
     */
    public static void openFans(String userId) {
        Intent intent = new Intent(getCurrentActivty(),
                BaseBigTitleActivity.class);
        intent.putExtra(BaseBigTitleActivity.KEY_TITLE, BaseBigTitleActivity.FANS_TYPE);
        intent.putExtra(key_user_id, userId);
        getCurrentActivty().startActivity(intent);
    }

    /**
     * #46506 android.os.TransactionTooLargeException
     * data parcel size 656304 bytes
     *
     * android.app.ActivityThread$StopInfo.run(ActivityThread.java:4178)
     * 动画稍有不 就崩了,还是不用了
     */
    public static void openPublish() {
        if (getCurrentActivty() instanceof MainActivity) {
            UmengHelper.event(UmengStatisticsKeyIds.publish_tab);
        }
        Intent intent = new Intent(getCurrentActivty(), PublishActivity.class);
        getCurrentActivty().startActivity(intent);
    }

    /**
     * 话题详情页跳转过去需要携带话题内容过去
     *
     * @param topicItemBean
     */
    public static void openPublishFromTopic(TopicItemBean topicItemBean) {
        UmengHelper.event(UmengStatisticsKeyIds.topic_detail_go_publish);
        Intent intent = new Intent(getCurrentActivty(), PublishActivity.class);
        intent.putExtra("topicBean", topicItemBean);
        getCurrentActivty().startActivity(intent);
    }

    /**
     * 查看图片详情
     *
     * @param positon
     * @param list
     */
    public static void openImageWatcher(int positon, ArrayList<ImageData> list, String contentID, String tagId) {
        ArrayList<ImageInfo> list1 = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (ImageData imageData : list) {
                ImageInfo imageInfo = new ImageInfo();
//                String url = imageData.url;
//                if (!TextUtils.isEmpty(url) && url.contains(".small")) {
//                    url = url.replace(".small", "");
//                }
                imageInfo.setOriginUrl(imageData.url);
                list1.add(imageInfo);
            }
        }
        dealRequestContent(contentID);
        Intent intent = new Intent(getCurrentActivty(), PictureWatcherActivity.class);
        intent.putParcelableArrayListExtra("list", list1);
        intent.putExtra("position", positon);
        intent.putExtra("contentId", contentID);
        intent.putExtra("tagId", tagId);
        getCurrentActivty().startActivity(intent);
        getCurrentActivty().overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    public static void openImageWatcher(String url, String h5Url, String guanjian) {
        ArrayList<ImageInfo> list1 = new ArrayList<>();
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setOriginUrl(url);
        list1.add(imageInfo);
        Intent intent = new Intent(getCurrentActivty(), PictureWatcherActivity.class);
        intent.putParcelableArrayListExtra("list", list1);
        intent.putExtra("touTao", h5Url);
        intent.putExtra("guaJian", guanjian);
        getCurrentActivty().startActivity(intent);
        getCurrentActivty().overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    public static void openCommentDetail(CommendItemBean.RowsBean rowsBean) {
        Activity currentActivty = getCurrentActivty();
        if (currentActivty == null) return;
        Intent intent = new Intent(currentActivty, CommentDetailActivity.class);
        intent.putExtra(KEY_DETAIL_COMMENT, rowsBean);
        currentActivty.startActivity(intent);
    }

    /**
     * 打开详情页面,跳转详情自己传bean对象
     */
    public static void openUgcDetail(MomentsDataBean bean) {
        if (bean == null) {
            return;
        }
        //0_正常 1_已删除 2_审核中
        if (TextUtils.equals(bean.getContentstatus(), "1")) {
            ToastUtil.showShort("该帖子已删除");
            return;
        }
        Intent intent = new Intent(getCurrentActivty(), UgcDetailActivity.class);
        intent.putExtra(KEY_CONTENT, bean);
        getCurrentActivty().startActivity(intent);
    }

    /**
     * 打开帮助反馈页面
     */
    public static void openFeedBack() {
        Intent intent = new Intent(getCurrentActivty(), HelpAndFeedbackActivity.class);
        getCurrentActivty().startActivity(intent);
    }

    public static void openBindPhoneOrPsw(int type) {
        Intent intent = new Intent(getCurrentActivty(), BindPhoneAndForgetPwdActivity.class);
        intent.putExtra(BindPhoneAndForgetPwdActivity.KEY_TYPE, type);
        getCurrentActivty().startActivity(intent);
    }

    public static void openShareCard() {
        Intent intent = new Intent(getCurrentActivty(), ShareCardToFriendActivity.class);
        getCurrentActivty().startActivity(intent);
    }

    public static void openSearch() {
        CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.discover_search);
        UmengHelper.event(UmengStatisticsKeyIds.search);
        Intent intent = new Intent(getCurrentActivty(), SearchActivity.class);
//        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(getCurrentActivty(), v, "search").toBundle();
        getCurrentActivty().startActivity(intent);
    }

    public static void openNoticeSetting() {
        Intent intent = new Intent(getCurrentActivty(), NoticeSettingActivity.class);
        getCurrentActivty().startActivity(intent);
    }

    /**
     * 打开用户勋章详情页面
     *
     * @param honorlistBean
     */
    public static void openUserMedalDetail(UserBaseInfoBean.UserInfoBean.HonorlistBean honorlistBean) {
        Intent intent = new Intent(getCurrentActivty(), MedalDetailActivity.class);
        intent.putExtra(KEY_MEDAL_ID, honorlistBean);
        getCurrentActivty().startActivity(intent);
    }

    public static void checkUrlForSkipWeb(String title, String url, String fromType) {
        if (TextUtils.isEmpty(url)) return;
        CommonHttpRequest.getInstance().checkUrl(url, new JsonCallback<BaseResponseBean<UrlCheckBean>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<UrlCheckBean>> response) {
                // TODO: 2018/12/25 保存接口给的key,H5认证使用
                UrlCheckBean data = response.body().getData();
                WebActivity.H5_KEY = data.getReturnkey();
                WebActivity.WEB_FROM_TYPE = fromType;
                WebActivity.openWeb(title, url, TextUtils.equals("1", data.getIsshare()));
            }
        });
    }

    /**
     * banner页有自己的分享逻辑
     *
     * @param title
     * @param url
     * @param fromType
     * @param bean
     */
    public static void checkUrlForSkipWeb(String title, String url, String fromType, WebShareBean bean) {
        if (TextUtils.isEmpty(url)) return;
        CommonHttpRequest.getInstance().checkUrl(url, new JsonCallback<BaseResponseBean<UrlCheckBean>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<UrlCheckBean>> response) {
                // TODO: 2018/12/25 保存接口给的key,H5认证使用
                UrlCheckBean data = response.body().getData();
                WebActivity.H5_KEY = data.getReturnkey();
                WebActivity.WEB_FROM_TYPE = fromType;
                WebActivity.openWeb(title, url, TextUtils.equals("1", data.getIsshare()), bean);
            }
        });
    }

    /**
     * 该值为false 则只需要判断文件是否存在再决定是否新生成视频片尾,true则不判断直接生成
     *
     * @param isNeedGenerate
     */
    public static void startVideoService(boolean isNeedGenerate) {
        Activity currentActivty = getCurrentActivty();
        if (currentActivty == null) return;
        Intent intent = new Intent(currentActivty, VideoFileReadyServices.class);
        intent.putExtra("isNeedGenerate", isNeedGenerate);
        VideoFileReadyServices.enqueueWork(currentActivty,intent);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            currentActivty.startForegroundService(intent);
//        } else {
//            currentActivty.startService(intent);
//        }
    }

    /**
     * 开启全屏播放
     *
     * @param url
     * @param shareBean
     */
    public static void openVideoFullScreen(String url, WebShareBean shareBean) {
        UmengHelper.event(UmengStatisticsKeyIds.fullscreen);
        FullScreenActivity.start(getCurrentActivty(), url, shareBean);
    }

}
