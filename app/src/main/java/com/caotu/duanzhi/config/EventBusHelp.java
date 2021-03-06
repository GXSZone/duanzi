package com.caotu.duanzhi.config;


import android.app.Activity;

import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.EventBusObject;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.module.detail_scroll.ContentNewDetailActivity;

import org.greenrobot.eventbus.EventBus;

/**
 * 用于管理登录和退出的通知发送
 */
public class EventBusHelp {
    /**
     * 用于详情和列表的联动
     * 这个同步如果做的话需要用DiffItemCallback 做,因为列表加了广告类型和关注用户类型
     *
     * @param bean
     */
    public static void sendLikeAndUnlike(MomentsDataBean bean) {

        // TODO: 2019/4/12 这里统计拿position    //类名来标识当前页面响应
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        if (runningActivity instanceof ContentNewDetailActivity) {
            String position = ((ContentNewDetailActivity) runningActivity).getPosition() + "";
            Activity lastSecondActivity = MyApplication.getInstance().getLastSecondActivity();
            String className = lastSecondActivity == null ? "" : lastSecondActivity.getLocalClassName();
            EventBusObject object = new EventBusObject(EventBusCode.DETAIL_CHANGE, bean, position, className);
            EventBus.getDefault().post(object);
        }
    }

    /**
     * 评论区的点赞同步
     *
     * @param bean
     */
    public static void sendCommendLikeAndUnlike(CommendItemBean.RowsBean bean) {
        //类名来标识当前页面响应
        Activity lastSecondActivity = MyApplication.getInstance().getLastSecondActivity();
        if (lastSecondActivity == null) return;
        String className = lastSecondActivity.getLocalClassName();
        EventBusObject object = new EventBusObject(EventBusCode.COMMENT_CHANGE, bean, null, className);
        EventBus.getDefault().post(object);
    }

    public static void sendLoginEvent() {
        EventBusObject object = new EventBusObject(EventBusCode.LOGIN, null, null, null);
        EventBus.getDefault().post(object);
    }

    public static void sendLoginOut() {
        EventBusObject object = new EventBusObject(EventBusCode.LOGIN_OUT, null, null, null);
        EventBus.getDefault().post(object);
    }

    public static void sendPublishEvent(String publishString, Object bean) {
        EventBusObject eventBusObject = new EventBusObject(EventBusCode.PUBLISH, bean, publishString, null);
        EventBus.getDefault().post(eventBusObject);
    }

    public static void sendPublishEvent(String publishString, String message) {
        EventBusObject eventBusObject = new EventBusObject(EventBusCode.PUBLISH, null, publishString, message);
        EventBus.getDefault().post(eventBusObject);
    }

    public static void sendVideoIsAutoPlay() {
        EventBusObject object = new EventBusObject(EventBusCode.VIDEO_PLAY, null, null, null);
        EventBus.getDefault().post(object);
    }

    public static void sendPagerPosition(int position) {
        Activity lastSecondActivity = MyApplication.getInstance().getLastSecondActivity();
        String tag = lastSecondActivity != null ? lastSecondActivity.getLocalClassName() : "";
        EventBusObject object = new EventBusObject(EventBusCode.DETAIL_PAGE_POSITION, position, null, tag);
        EventBus.getDefault().post(object);
    }

    public static void sendChangeTextSize(float size) {
        EventBusObject object = new EventBusObject(EventBusCode.TEXT_SIZE, size, null, null);
        EventBus.getDefault().post(object);
    }

    public static void sendTeenagerEvent(boolean isOpen) {
        EventBusObject object = new EventBusObject(EventBusCode.TEENAGER_MODE, isOpen, null, null);
        EventBus.getDefault().post(object);
    }
}
