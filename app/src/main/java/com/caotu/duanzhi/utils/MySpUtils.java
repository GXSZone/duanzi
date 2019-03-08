package com.caotu.duanzhi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.caotu.duanzhi.MyApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

public final class MySpUtils {
    public static final String SP_NAME = "duanzi_config";
    //该字段在头图有用到,现在用不到,字段保留,在注册接口有返回该字段
    public static final String SP_ISFIRSTENTRY = "isFirstEntry";//是否第一次进入APP
    public static final String SP_READ_DIALOG = "READ_DIALOG";
    public static final String SP_REGISTRATION_ID = "REGISTRATION_ID";
    public static final String KEY_HAS_SHOWED_NOTIFY_DIALOG = "HAS_SHOWED_NOTIFY_DIALOG";
    public static final String SPLASH_SHOWED = "SPLASH_SHOWED";

    public static final String SP_ISLOGIN = "isLogin";//是否登录状态

    public static final String SP_MY_ID = "myId";//用户的ID
    public static final String SP_MY_NAME = "myName";//用户的昵称
    public static final String SP_MY_AVATAR = "myAvatar";//用户的头像
    public static final String SP_MY_NUM = "user_number";//用户段友号
    public static final String SP_MY_SEX = "mySex";//用户的性别
    public static final String SP_MY_BIRTHDAY = "myBirthday";//用户的生日
    public static final String SP_MY_SIGN = "mySign";//用户的签名
    public static final String SP_SEARCH_HISTORY = "SearchHistory";//历史搜索
    public static final String SP_TOKEN = "token";//当前登陆唯一标识value
    public static final String SP_HAS_BIND_PHONE = "Bind_phone";
    public static final String SP_WIFI_PLAY = "wifi_play";
    public static final String SP_TRAFFIC_PLAY = "traffic_play";
    public static final String SP_EYE_MODE = "eye_mode";

    public static final String SP_DOWNLOAD_GUIDE = "download_guide";
    public static final String SP_SLIDE_GUIDE = "slide_guide";
    public static final String SP_ENTER_SETTING = "enter_setting";
    //发布的内容保存
    public static final String SP_PUBLISH_TEXT = "publish_text";
    public static final String SP_PUBLISH_MEDIA = "publish_media";
    public static final String SP_PUBLISH_TIPIC = "publish_topic";
    public static final String SP_PUBLISH_TYPE = "publish_type";

    public static final String SP_LOOK_HISTORY = "look_history";
    public static final String SP_ENTER_HISTORY = "enter_history";
    //通知设置页面的开关
    public static final String SP_SWITCH_COMMENT = "switch_comment";
//    public static final String SP_SWITCH_INTERACTIVE = "switch_interactive";
    public static final String SP_SWITCH_REPLY = "switch_reply";
    public static final String SP_SWITCH_LIKE = "switch_like";
    public static final String SP_SWITCH_FOLLOW = "switch_follow";
    public static final String SP_SWITCH_TIME = "switch_time";

    /**
     * 存储string
     *
     * @param key
     * @param values
     */
    public static void putString(String key, String values) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, values);
        editor.apply();
    }

    /**
     * 读取string
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    /**
     * 存储long值
     *
     * @param key
     * @param values
     */
    public static void putLong(String key, long values) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putLong(key, values).apply();
    }

    /**
     * 读取long值
     *
     * @param key
     * @return
     */
    public static long getLong(String key) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getLong(key, 1L);
    }


    /**
     * 存储int
     *
     * @param key
     * @param values
     */
    public static void putInt(String key, int values) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putInt(key, values).apply();
    }

    /**
     * 读取int
     *
     * @param key
     * @param defValues
     * @return
     */
    public static int getInt(String key, int defValues) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defValues);
    }

    /**
     * 存储boolean
     *
     * @param key
     * @param values
     */
    public static void putBoolean(String key, boolean values) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, values).apply();
    }

    /**
     * 读取boolean
     *
     * @param key
     * @param defValues
     * @return
     */
    public static boolean getBoolean(String key, boolean defValues) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValues);
    }

    /**
     * 删除一条数据
     *
     * @param key
     */
    public static void deleteKey(String key) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().remove(key).apply();
    }

    /**
     * 删除整个数据
     */
    public static void clear() {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }

    /**
     * 清除登录状态
     */
    public static void clearLogingType() {
        MySpUtils.deleteKey(SP_MY_ID);
        MySpUtils.deleteKey(SP_TOKEN);
        MySpUtils.deleteKey(SP_ISLOGIN);
        MySpUtils.deleteKey(SP_HAS_BIND_PHONE);
        MySpUtils.deleteKey(SP_MY_AVATAR);
        MySpUtils.deleteKey(SP_MY_NAME);
        MySpUtils.deleteKey(SP_MY_SIGN);
        MySpUtils.deleteKey(SP_MY_NUM);
    }

    public static void clearPublishContent() {
        MySpUtils.deleteKey(MySpUtils.SP_PUBLISH_MEDIA);
        MySpUtils.deleteKey(MySpUtils.SP_PUBLISH_TEXT);
        MySpUtils.deleteKey(MySpUtils.SP_PUBLISH_TIPIC);
        MySpUtils.deleteKey(MySpUtils.SP_PUBLISH_TYPE);
    }

    /**
     * 根据用户Id判断是否是自己
     *
     * @param userId
     * @return
     */
    public static boolean isMe(String userId) {
        return TextUtils.equals(userId, MySpUtils.getString(MySpUtils.SP_MY_ID));
    }

    public static String getMyId() {
        return getString(SP_MY_ID);
    }

    public static String getMyName() {
        return getString(SP_MY_NAME);
    }


    public static boolean putHashMapData(HashMap<String, Long> map) {
        boolean result;
        SharedPreferences.Editor editor = MyApplication.getInstance().
                getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit();
        try {
            Gson gson = new Gson();
            String json = gson.toJson(map);
            editor.putString(SP_LOOK_HISTORY, json);
            result = true;
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        editor.apply();
        return result;
    }

    public static HashMap<String, Long> getHashMapData() {
        String json = MyApplication.getInstance().
                getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SP_LOOK_HISTORY, "");
        return new Gson().fromJson(json, new TypeToken<HashMap<String, Long>>() {
        }.getType());
    }
}
