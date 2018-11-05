package com.caotu.duanzhi.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.caotu.duanzhi.MyApplication;

public class MySpUtils {
    public static final String SP_NAME = "duanzi_config";
    //该字段在头图有用到,现在用不到,字段保留,在注册接口有返回该字段
    public static final String SP_ISFIRSTENTRY = "isFirstEntry";//是否第一次进入APP
    public static final String SP_READ_DIALOG = "READ_DIALOG";
    public static final String SP_REGISTRATION_ID = "REGISTRATION_ID";
    public static final String KEY_HAS_SHOWED_NOTIFY_DIALOG = "HAS_SHOWED_NOTIFY_DIALOG";
    public static final String SPLASH_SHOWED = "SPLASH_SHOWED";

    public static final String SP_ISLOGIN = "isLogin";//是否登录状态
    public static final String SP_ISFIRSTLOGINENTRY = "isFirstLoginEntry";//是否第一次登录状态

    public static final String SP_MY_ID = "myId";//用户的ID
    public static final String SP_MY_NAME = "myName";//用户的昵称
    public static final String SP_MY_AVATAR = "myAvatar";//用户的头像
    public static final String SP_MY_AVATAR_HANGER = "myAvatarHANGER";//用户的头像挂件
    public static final String SP_MY_SEX = "mySex";//用户的性别
    public static final String SP_MY_BIRTHDAY = "myBirthday";//用户的生日
    public static final String SP_MY_SIGN = "mySign";//用户的签名
    public static final String SP_SEARCH_HISTORY = "SearchHistory";//历史搜索
    public static final String SP_TOKEN = "token";//当前登陆唯一标识value
    public static final String SP_HAS_BIND_PHONE = "Bind_phone";

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
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, values);
        editor.apply();
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
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(key, values);
        editor.apply();
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
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, values);
        editor.apply();
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
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * 删除整个数据
     */
    public static void clear() {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * 清除登录状态
     */
    public static void clearLogingType() {
        MySpUtils.deleteKey(SP_MY_ID);
        MySpUtils.deleteKey(SP_TOKEN);
        MySpUtils.deleteKey(SP_ISLOGIN);
//        App.removeAllIsParise();
    }

    /**
     * 根据用户Id判断是否是自己
     * @param userId
     * @return
     */
    public static boolean isMe(String userId) {
        return userId.equals(MySpUtils.getString(MySpUtils.SP_MY_ID));
    }
}
