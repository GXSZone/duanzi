package com.caotu.duanzhi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.caotu.duanzhi.Http.bean.TopicItemBean;
import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.MyApplication;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class MySpUtils {
    public static final String SP_NAME = "duanzi_config";
    //该字段在头图有用到,现在用不到,字段保留,在注册接口有返回该字段
    public static final String SP_ISFIRSTENTRY = "isFirstEntry";//是否第一次进入APP
    public static final String SP_READ_DIALOG = "READ_DIALOG";
    public static final String KEY_HAS_SHOWED_NOTIFY_DIALOG = "HAS_SHOWED_NOTIFY_DIALOG";//这个是首页通知提示弹窗
    public static final String KEY_SETTING_NOTIFY_DIALOG = "NOTIFY_DIALOG";//这个是通知设置的弹窗
    public static final String SPLASH_SHOWED = "SPLASH_SHOWED";

    public static final String SP_ISLOGIN = "isLogin";//是否登录状态

    public static final String SP_MY_ID = "myId";//用户的ID
    public static final String SP_MY_NAME = "myName";//用户的昵称
    public static final String SP_MY_AVATAR = "myAvatar";//用户的头像
    public static final String SP_MY_NUM = "user_number";//用户段友号
    public static final String SP_MY_LOCATION = "location";//用户的性别
    public static final String SP_MY_SIGN = "mySign";//用户的签名
    public static final String SP_TOKEN = "token";//当前登陆唯一标识value
    public static final String SP_HAS_BIND_PHONE = "Bind_phone";
    public static final String SP_WIFI_PLAY = "wifi_play";
    public static final String SP_TRAFFIC_PLAY = "traffic_play";
    public static final String SP_EYE_MODE = "eye_mode";
    public static final String SP_VIDEO_AUTO_REPLAY = "auto_replay";
    public static final String SP_COLLECTION_SHOW = "show_collection";

    public static final String SP_PUSH_SOUND = "push_sound";
    //发布的内容保存
    public static final String SP_PUBLISH_TEXT = "publish_text";
    public static final String SP_PUBLISH_MEDIA = "publish_media";
    public static final String SP_PUBLISH_TOPIC = "publish_topic";
    public static final String SP_PUBLISH_TYPE = "publish_type";

    public static final String sp_db_save = "db_save";
    public static final String SP_LOOK_HISTORY = "look_history";
    public static final String SP_SELECTE_TOPICS = "topic_history"; //选择过的话题记录
    public static final String SP_SELECTE_USER = "at_user_history"; //选择过@ 用户记录
    public static final String sp_test_http = "test_http";
    public static final String sp_test_name = "test_name";

    /***************************1.5.0 版本新加字段**********************************/

    public static final String SP_ENTER_RED = "enter_setting3"; //每次改一下key string就行,定义的名字不用变
    public static final String SP_AUTO_REPLAY_TIP = "replay_tip";
    public static final String SP_TEXT_SIZE = "text_size";
    public static final String SP_PERMISSION_SHOW = "Permission_show";//是否谈过授权提示框

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

    public static void putFloat(String key, float values) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putFloat(key, values).apply();
    }

    public static float getFloat(String key) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getFloat(key, 16);
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
        MySpUtils.deleteKey(SP_COLLECTION_SHOW);
    }

    public static void clearPublishContent() {
        MySpUtils.deleteKey(MySpUtils.SP_PUBLISH_MEDIA);
        MySpUtils.deleteKey(MySpUtils.SP_PUBLISH_TEXT);
        MySpUtils.deleteKey(MySpUtils.SP_PUBLISH_TOPIC);
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

    public static boolean getPushSoundIsOpen() {
        return getBoolean(SP_PUSH_SOUND, true);
    }

    public static void setPushSound(boolean isChecked) {
        putBoolean(SP_PUSH_SOUND, isChecked);
    }

//    public static boolean putHashMapData(HashMap<String, Long> map) {
//        boolean result;
//        SharedPreferences.Editor editor = MyApplication.getInstance().
//                getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit();
//        try {
//            Gson gson = new Gson();
//            String json = gson.toJson(map);
//            editor.putString(SP_LOOK_HISTORY, json);
//            result = true;
//        } catch (Exception e) {
//            result = false;
//            e.printStackTrace();
//        }
//        editor.apply();
//        return result;
//    }

    public static HashMap<String, Long> getHashMapData() {
        String json = MyApplication.getInstance().
                getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString(SP_LOOK_HISTORY, "");
        if (TextUtils.isEmpty(json)){
            return null;
        }
        return new Gson().fromJson(json, new TypeToken<HashMap<String, Long>>() {
        }.getType());
    }

    public static boolean getReplayTip() {
        return getBoolean(SP_AUTO_REPLAY_TIP, false);
    }

    public static void setReplayTip() {
        putBoolean(SP_AUTO_REPLAY_TIP, true);
    }


    public static boolean getReplaySwitch() {
        return getBoolean(SP_VIDEO_AUTO_REPLAY, false);
    }

    public static void setReplaySwitch(boolean isChecked) {
        putBoolean(SP_VIDEO_AUTO_REPLAY, isChecked);
    }

    /**
     * 获取话题记录
     *
     * @return
     */
    public static List<TopicItemBean> getTopicList() {
        String string = getString(SP_SELECTE_TOPICS);
        List<TopicItemBean> list = null;
        try {
            list = new Gson().fromJson(string, new TypeToken<List<TopicItemBean>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 保存选择过的话题记录
     *
     * @param bean
     */
    public static void putTopicToSp(TopicItemBean bean) {
        if (bean == null) return;
        List<TopicItemBean> topicList = getTopicList();
        if (topicList == null) {
            topicList = new ArrayList<>();
        }
        for (int i = 0; i < topicList.size(); i++) {
            //去重,一样的话不保存
            if (bean.tagid.equals(topicList.get(i).tagid))
                return;
        }
        if (topicList.size() >= 3) {
            topicList.remove(2);
            topicList.add(0, bean);
        } else {
            topicList.add(0, bean);
        }
        String json = new Gson().toJson(topicList);
        putString(SP_SELECTE_TOPICS, json);
    }

    /**
     * 获取@用户记录
     *
     * @return
     */
    public static List<UserBean> getAtUserList() {
        String string = getString(SP_SELECTE_USER);
        List<UserBean> list = null;
        try {
            list = new Gson().fromJson(string, new TypeToken<List<UserBean>>() {
            }.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 保存选择过的@用户记录
     *
     * @param bean
     */
    public static void putAtUserToSp(UserBean bean) {
        if (bean == null) return;
        List<UserBean> topicList = getAtUserList();
        if (topicList == null) {
            topicList = new ArrayList<>();
        }
        for (int i = 0; i < topicList.size(); i++) {
            //去重,一样的话不保存
            if (bean.userid.equals(topicList.get(i).userid))
                return;
        }
        if (topicList.size() >= 3) {
            topicList.remove(2);
            topicList.add(0, bean);
        } else {
            topicList.add(0, bean);
        }
        String json = new Gson().toJson(topicList);
        putString(SP_SELECTE_USER, json);
    }
}
