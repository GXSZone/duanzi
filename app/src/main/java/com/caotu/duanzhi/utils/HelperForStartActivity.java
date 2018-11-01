package com.caotu.duanzhi.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.module.home.ContentDetailActivity;
import com.caotu.duanzhi.module.mine.FocusActivity;
import com.caotu.duanzhi.module.mine.MyNoticeActivity;
import com.caotu.duanzhi.module.mine.SettingActivity;
import com.caotu.duanzhi.module.other.OtherActivity;

/**
 * @author mac
 * @日期: 2018/11/1
 * @describe 用于管理activity跳转传值管理
 */
public class HelperForStartActivity {

    public static final String key_other_type = "other_type";
    public static final String type_other_user = "user";  //原来的other就是指用户
    public static final String type_other_topic = "topic"; //原来的theme就是指话题现在
    public static final String key_user_id = "userId";

    public static Activity getCurrentActivty() {
        return MyApplication.getInstance().getRunningActivity();
    }

    /**
     * 打开话题详情和他人主页
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
     * 打开详情页面
     *
     * @param contentid
     * @param equals
     */
    public static void openContentDetail(String contentid, boolean equals) {
        Intent intent = new Intent(getCurrentActivty(), ContentDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("contentId", contentid);
//        bundle.putString("themeName", tagshow);
//        bundle.putString("themeId", tagshowid);
        bundle.putBoolean("isFocus", equals);
        intent.putExtras(bundle);
        getCurrentActivty().startActivity(intent);
    }

    /**
     * 打开通知页面
     */
    public static void openNotice() {
        Intent intent = new Intent(getCurrentActivty(), MyNoticeActivity.class);
        getCurrentActivty().startActivity(intent);
    }

    /**
     * 打开设置页面
     */
    public static void openSetting(){
        Intent intent = new Intent(getCurrentActivty(), SettingActivity.class);
        getCurrentActivty().startActivity(intent);
    }

    public static void openFocus() {
        Intent intent = new Intent(getCurrentActivty(), FocusActivity.class);
        getCurrentActivty().startActivity(intent);
    }
}
