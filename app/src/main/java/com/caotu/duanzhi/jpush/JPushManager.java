package com.caotu.duanzhi.jpush;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.config.HttpApi;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;

public class JPushManager {
    private static final String TAG = "JPUSH";

    public static int sequence = 1;
    /**
     * 增加
     */
    public static final int ACTION_ADD = 1;
    /**
     * 覆盖
     */
    public static final int ACTION_SET = 2;
    /**
     * 删除部分
     */
    public static final int ACTION_DELETE = 3;
    /**
     * 删除所有
     */
    public static final int ACTION_CLEAN = 4;
    /**
     * 查询
     */
    public static final int ACTION_GET = 5;

    public static final int ACTION_CHECK = 6;

    public static final int DELAY_SEND_ACTION = 1;
    private Context context;
    private TagAliasBean tagAliasBean = new TagAliasBean();

    private static JPushManager mInstance;

    private JPushManager() {
    }

    public static JPushManager getInstance() {
        if (mInstance == null) {
            synchronized (JPushManager.class) {
                if (mInstance == null) {
                    mInstance = new JPushManager();
                }
            }
        }
        return mInstance;
    }

    private void init(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
        }
    }

    private SparseArray<TagAliasBean> tagAliasActionCache = new SparseArray<TagAliasBean>();


    /**
     * 初始化极光，一般可以放到程序的启动Activity或是Application的onCreate方法中调用
     *
     * @param b 设置是否开启日志,发布时请关闭日志RedDotImageView.java
     */
    public void initJPush(Context context, boolean b) {
        JPushInterface.setDebugMode(b); // 设置开启日志,发布时请关闭日志
        JPushInterface.init(context); // 初始化 JPush
    }

    public void requestPermission(Context context) {
        JPushInterface.requestPermission(context);
    }

    /**
     * 退出极光，一般是程序退出登录时候，具体还是需要看项目的实际需求
     */
    public void stopJPush() {
        //调用了本方法后，JPush 推送服务完全被停止.所有的其他 API 调用都无效,不能通过 JPushInterface.init 恢复，需要调用resumePush恢复。
        JPushInterface.stopPush(context);
//        setAliasAndTags("", "");//通过清空别名来停止极光
    }

    public void resumePush() {
        JPushInterface.resumePush(context);
    }

    /**
     * 设置极光推送app别名
     * 用于给某特定用户推送消息。别名，可以近似地被认为，是用户帐号里的昵称 使用标签
     * 覆盖逻辑，而不是增量逻辑。即新的调用会覆盖之前的设置。
     *
     * @param alias
     */
    public void setAlias(Context conn, String alias) {
        setTagAliasBean(ACTION_SET, alias, null, true);
        setAliasAndTags(conn, tagAliasBean);
    }

    /**
     * 删除极光推送app别名
     */
    public void deleteAlias(Context conn) {
        setTagAliasBean(ACTION_DELETE, null, null, true);
        setAliasAndTags(conn, tagAliasBean);
    }

    /**
     * 获取极光推送app别名
     */
    public void getAlias(Context conn) {
        setTagAliasBean(ACTION_GET, null, null, true);
        setAliasAndTags(conn, tagAliasBean);
    }

    /**
     * 设置标签
     * 用于给某一群人推送消息。标签类似于博客里为文章打上 tag ，即为某资源分类。
     */
    public void setTags(Context conn, Set<String> Tags) {
        setTagAliasBean(ACTION_SET, null, Tags, false);
        setAliasAndTags(conn, tagAliasBean);
    }

    /**
     * 添加标签
     */
    public void addTags(Context conn, Set<String> Tags) {
        setTagAliasBean(ACTION_ADD, null, Tags, false);
        setAliasAndTags(conn, tagAliasBean);
    }

    /**
     * 删除标签
     */
    public void deleteTags(Context conn, Set<String> Tags) {
        setTagAliasBean(ACTION_DELETE, null, Tags, false);
        setAliasAndTags(conn, tagAliasBean);
    }

    /**
     * 删除所有标签
     */
    public void cleanTags(Context conn) {
        setTagAliasBean(ACTION_CLEAN, null, null, false);
        setAliasAndTags(conn, tagAliasBean);
    }

    /**
     * 获取所有标签
     */
    public void getAllTags(Context conn) {
        setTagAliasBean(ACTION_GET, null, null, false);
        setAliasAndTags(conn, tagAliasBean);
    }

    /**
     * 查询标签状态
     */
    public void checkTags(Context conn, Set<String> Tags) {
        setTagAliasBean(ACTION_CHECK, null, Tags, false);
        setAliasAndTags(conn, tagAliasBean);
    }

    public void put(int sequence, TagAliasBean tagAliasBean) {
        tagAliasActionCache.put(sequence, tagAliasBean);
    }

    private Handler delaySendHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DELAY_SEND_ACTION:
                    if (msg.obj != null && msg.obj instanceof TagAliasBean) {
                        Log.i(TAG, "on delay time");
                        sequence++;
                        TagAliasBean tagAliasBean = (TagAliasBean) msg.obj;
                        tagAliasActionCache.put(sequence, tagAliasBean);
                        if (context != null) {
                            //setAliasAndTags 里的sequence会再自增一次，保持和上面的sequence一致这里再自减一次
                            sequence--;
                            setAliasAndTags(context, tagAliasBean);
                        } else {
                            Log.i(TAG, "#unexcepted - context was null");
                        }
                    } else {
                        Log.i(TAG, "#unexcepted - msg obj was incorrect");
                    }
                    break;
            }
        }
    };


    public void setAliasAndTags(Context context, TagAliasBean tagAliasBean) {

        init(context);
        if (tagAliasBean == null) {
            Log.i(TAG, "setAliasAndTags时tagAliasBean为null");
            return;
        }
        sequence++;
        put(sequence, tagAliasBean);
        if (tagAliasBean.isAliasAction) {
            switch (tagAliasBean.action) {
                case ACTION_GET:
                    JPushInterface.getAlias(context, sequence);
                    break;
                case ACTION_DELETE:
                    JPushInterface.deleteAlias(context, sequence);
                    break;
                case ACTION_SET:
                    JPushInterface.setAlias(context, sequence, tagAliasBean.alias);
                    break;
                default:
                    Log.i(TAG, "unsupport alias action type");
                    return;
            }
        } else {
            switch (tagAliasBean.action) {
                case ACTION_ADD:
                    JPushInterface.addTags(context, sequence, tagAliasBean.tags);
                    break;
                case ACTION_SET:
                    JPushInterface.setTags(context, sequence, tagAliasBean.tags);
                    break;
                case ACTION_DELETE:
                    JPushInterface.deleteTags(context, sequence, tagAliasBean.tags);
                    break;
                case ACTION_CHECK:
                    //一次只能check一个tag
                    String tag = (String) tagAliasBean.tags.toArray()[0];
                    JPushInterface.checkTagBindState(context, sequence, tag);
                    break;
                case ACTION_GET:
                    JPushInterface.getAllTags(context, sequence);
                    break;
                case ACTION_CLEAN:
                    JPushInterface.cleanTags(context, sequence);
                    break;
                default:
                    Log.i(TAG, "unsupport tag action type");
                    return;
            }
        }
    }


    private String onAliasOperatorResultAlias;

    private void setCurrentAlias(String alias) {
        Log.i(TAG, "我后执行");
        this.onAliasOperatorResultAlias = alias;
    }

    public String getOnAliasOperatorResultAlias() {
        Log.i(TAG, "我先执行");
        return onAliasOperatorResultAlias;
    }

    /**
     * 设置 TagAliasBean
     *
     * @param action        目标操作
     * @param alias
     * @param tags
     * @param isAliasAction true 设置alias ；false设置tags
     */
    private void setTagAliasBean(int action, String alias, Set<String> tags, boolean isAliasAction) {

        tagAliasBean.action = action;
        tagAliasBean.alias = alias;
        tagAliasBean.tags = tags;
        tagAliasBean.isAliasAction = isAliasAction;

    }

    /**
     * 用于登陆后请求接口获取别名,另外还需要清除原先的别名
     */
    public void loginSuccessAndSetJpushAlias() {
        OkGo.<BaseResponseBean<String>>post(HttpApi.PUSH_TAG)
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        String alias = response.body().getData();
                        Log.i(TAG, "onSuccess: alias" + alias);
                        JPushManager.getInstance().setAlias(MyApplication.getInstance(), alias);
                    }
                });
    }

    /**
     * 用于退出登录清除tag,防止继续推送
     */
    public void loginOutClearAlias() {
        deleteAlias(MyApplication.getInstance());
    }

    /**
     * 检测通知开关是否打开
     * 跳转手机的应用通知设置页，可由用户操作开启通知开关
     *
     * @param context
     * @return
     */
    public boolean noticeIsOpen(Context context) {
        //返回结果：1表示开启，0表示关闭，-1表示检测失败
        return JPushInterface.isNotificationEnabled(context) == 1;
    }

    /**
     * 跳转到通知设置页面
     *
     * @param context
     */
    public void goSetting(Context context) {
        JPushInterface.goToAppNotificationSettings(context.getApplicationContext());
    }


    public static class TagAliasBean {
        int action;
        Set<String> tags;
        String alias;
        boolean isAliasAction;

        @Override
        public String toString() {
            return "TagAliasBean{" +
                    "action=" + action +
                    ", tags=" + tags +
                    ", alias='" + alias + '\'' +
                    ", isAliasAction=" + isAliasAction +
                    '}';
        }
    }
}
