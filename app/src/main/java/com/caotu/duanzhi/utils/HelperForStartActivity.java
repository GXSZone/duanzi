package com.caotu.duanzhi.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.UrlCheckBean;
import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.detail_scroll.ContentScrollDetailActivity;
import com.caotu.duanzhi.module.home.CommentDetailActivity;
import com.caotu.duanzhi.module.home.ContentDetailActivity;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.home.UgcDetailActivity;
import com.caotu.duanzhi.module.login.BindPhoneAndForgetPwdActivity;
import com.caotu.duanzhi.module.mine.BaseBigTitleActivity;
import com.caotu.duanzhi.module.mine.FocusActivity;
import com.caotu.duanzhi.module.mine.HelpAndFeedbackActivity;
import com.caotu.duanzhi.module.mine.MedalDetailActivity;
import com.caotu.duanzhi.module.mine.SettingActivity;
import com.caotu.duanzhi.module.mine.ShareCardToFriendActivity;
import com.caotu.duanzhi.module.other.OtherActivity;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.module.other.imagewatcher.ImageInfo;
import com.caotu.duanzhi.module.other.imagewatcher.PictureWatcherActivity;
import com.caotu.duanzhi.module.publish.PublishActivity;
import com.caotu.duanzhi.module.search.SearchActivity;
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
    public static final String KEY_SCROLL_DETAIL = "scroll_detail";
    public static final String KEY_FROM_POSITION = "position";
    public static final String KEY_MEDAL_ID = "medal_id";

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
        Intent intent = new Intent(getCurrentActivty(), OtherActivity.class);
        intent.putExtra(key_other_type, type);
        intent.putExtra(key_user_id, id);
        getCurrentActivty().startActivity(intent);
    }

    /**
     * 打开详情页面,跳转详情自己传bean对象
     */
    public static void openContentDetail(MomentsDataBean bean, boolean iscomment) {
        if (bean == null) {
            return;
        }
        //0_正常 1_已删除 2_审核中
        if (TextUtils.equals(bean.getContentstatus(), "1")) {
            ToastUtil.showShort("该帖子已删除");
            return;
        }
        dealRequestContent(bean.getContentid());
        Intent intent = new Intent(getCurrentActivty(), ContentDetailActivity.class);
        intent.putExtra(KEY_TO_COMMENT, iscomment);
        intent.putExtra(KEY_CONTENT, bean);
        getCurrentActivty().startActivity(intent);
    }

    /**
     * 用于视频播放传进度过去
     *
     * @param bean
     * @param iscomment
     * @param videoProgress
     */
    public static void openContentDetail(MomentsDataBean bean, boolean iscomment, int videoProgress) {
        if (bean == null) {
            return;
        }
        //0_正常 1_已删除 2_审核中
        if (TextUtils.equals(bean.getContentstatus(), "1")) {
            ToastUtil.showShort("该帖子已删除");
            return;
        }
        dealRequestContent(bean.getContentid());
        Intent intent = new Intent(getCurrentActivty(), ContentDetailActivity.class);
        intent.putExtra(KEY_TO_COMMENT, iscomment);
        intent.putExtra(KEY_CONTENT, bean);
        intent.putExtra(KEY_VIDEO_PROGRESS, videoProgress);
        getCurrentActivty().startActivity(intent);
    }

    /**
     * 用于视频播放传进度过去
     *
     * @param iscomment
     * @param videoProgress
     */
    public static void openContentDetail(ArrayList<MomentsDataBean> beanList, int position, boolean iscomment, int videoProgress) {
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
        Intent intent = new Intent(getCurrentActivty(), ContentScrollDetailActivity.class);
        intent.putExtra(KEY_TO_COMMENT, iscomment);
        intent.putExtra(KEY_SCROLL_DETAIL, beanList);
        intent.putExtra(KEY_VIDEO_PROGRESS, videoProgress);
        intent.putExtra(KEY_FROM_POSITION, position);
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

    /**
     * 代码改动最少的解决方案
     *
     * @param contentid
     */
    public static void dealRequestContent(String contentid) {
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

    public static void openPublish(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(view,
                view.getWidth() / 2, view.getHeight() / 2, //拉伸开始的坐标
                0, 0);//拉伸开始的区域大小，这里用（0，0）表示从无到全屏
        Intent intent = new Intent(getCurrentActivty(), PublishActivity.class);
        getCurrentActivty().startActivity(intent, options.toBundle());
    }

    /**
     * 查看图片详情
     *
     * @param positon
     * @param list
     */
    public static void openImageWatcher(int positon, ArrayList<ImageData> list, String contentID) {
        ArrayList<ImageInfo> list1 = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (ImageData imageData : list) {
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setOriginUrl(imageData.url);
                list1.add(imageInfo);
            }
        }
        dealRequestContent(contentID);
        CommonHttpRequest.getInstance().requestPlayCount(contentID);
        Intent intent = new Intent(getCurrentActivty(), PictureWatcherActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("tlist", list1);
        bundle.putInt("position", positon);
        intent.putExtra("list", bundle);
        intent.putExtra("contentId", contentID);
        getCurrentActivty().startActivity(intent);
        getCurrentActivty().overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    public static void openCommentDetail(CommendItemBean.RowsBean rowsBean) {
        Intent intent = new Intent(getCurrentActivty(), CommentDetailActivity.class);
        intent.putExtra(KEY_DETAIL_COMMENT, rowsBean);
        getCurrentActivty().startActivity(intent);
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

    public static void openBindPhone() {
        Intent intent = new Intent(getCurrentActivty(),
                BindPhoneAndForgetPwdActivity.class);
        intent.putExtra(BindPhoneAndForgetPwdActivity.KEY_TYPE,
                BindPhoneAndForgetPwdActivity.BIND_TYPE);
        getCurrentActivty().startActivity(intent);
    }

    public static void openShareCard() {
        Intent intent = new Intent(getCurrentActivty(), ShareCardToFriendActivity.class);
        getCurrentActivty().startActivity(intent);
    }

    public static void openSearch(View v) {
        Intent intent = new Intent(getCurrentActivty(), SearchActivity.class);
        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(getCurrentActivty(), v, "search").toBundle();
        getCurrentActivty().startActivity(intent, bundle);
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

    public static void checkUrlForSkipWeb(String title, String url) {
        if (TextUtils.isEmpty(url)) return;
        CommonHttpRequest.getInstance().checkUrl(url, new JsonCallback<BaseResponseBean<UrlCheckBean>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<UrlCheckBean>> response) {
                // TODO: 2018/12/25 保存接口给的key,H5认证使用
                UrlCheckBean data = response.body().getData();
                WebActivity.H5_KEY = data.getReturnkey();
                WebActivity.openWeb(title, url, TextUtils.equals("1", data.getIsshare()));
            }
        });
    }

}
