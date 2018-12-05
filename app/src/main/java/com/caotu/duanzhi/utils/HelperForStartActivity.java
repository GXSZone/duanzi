package com.caotu.duanzhi.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.module.home.CommentDetailActivity;
import com.caotu.duanzhi.module.home.ContentDetailActivity;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.home.UgcDetailActivity;
import com.caotu.duanzhi.module.login.BindPhoneAndForgetPwdActivity;
import com.caotu.duanzhi.module.mine.BaseBigTitleActivity;
import com.caotu.duanzhi.module.mine.FocusActivity;
import com.caotu.duanzhi.module.mine.HelpAndFeedbackActivity;
import com.caotu.duanzhi.module.mine.SettingActivity;
import com.caotu.duanzhi.module.mine.ShareCardToFriendActivity;
import com.caotu.duanzhi.module.other.OtherActivity;
import com.caotu.duanzhi.module.other.PictureWatcherActivity;
import com.caotu.duanzhi.module.publish.PublishActivity;
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
    //    public static final String key_is_mine = "mine";

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
    private static void dealRequestContent(String contentid) {
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

    public static void openPublish() {
        Intent intent = new Intent(getCurrentActivty(), PublishActivity.class);
        getCurrentActivty().startActivity(intent);
    }

    /**
     * 查看图片详情
     *
     * @param positon
     * @param list
     */
    public static void openImageWatcher(int positon, ArrayList<ImageData> list, String contentID) {
        ArrayList<String> list1 = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (ImageData imageData : list) {
                list1.add(imageData.url);
            }
        }
        dealRequestContent(contentID);
//        CommonHttpRequest.getInstance().requestPlayCount(contentID);
        Intent intent = new Intent(getCurrentActivty(), PictureWatcherActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("tlist", list1);
        bundle.putInt("position", positon);
        intent.putExtra("list", bundle);
        intent.putExtra("contentId", contentID);
        getCurrentActivty().startActivity(intent);
    }

    public static void openCommentDetail(CommendItemBean.RowsBean rowsBean) {
        Intent intent = new Intent(getCurrentActivty(), CommentDetailActivity.class);
        intent.putExtra(KEY_DETAIL_COMMENT, rowsBean);
        getCurrentActivty().startActivity(intent);
    }

    /**
     * 针对有点赞取消点赞的回调,直接用bean对象过于沉重
     *
     * @param rowsBean
     * @param callBack
     */
    public static void openCommentDetail(CommendItemBean.RowsBean rowsBean, ILikeAndUnlike callBack) {
        CommentDetailActivity.openCommentDetail(rowsBean, callBack);
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


    /**
     * 目前先不用,以后跳转详情点赞和踩数据更新机制用
     */
    public interface ILikeAndUnlike {
        void like();

        void unlike();
        //评论的回调,现在先不放开
//        void comment();
    }

}
