package com.caotu.duanzhi.config;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/7/29 23:38
 */
public class EventBusCode {

    //登录
    public static final int LOGIN = 1007;
    //退出登录
    public static final int LOGIN_OUT = 1008;

    //发布后的code
    public static final int PUBLISH = 1010;
    //wifi状态下是否自动播放
    public static final int VIDEO_PLAY = 1011;
    //详情状态更新
    public static final int DETAIL_CHANGE = 1012;

    //详情页page滑动的页码
    public static final int DETAIL_PAGE_POSITION = 1014;

    //评论区点赞同步问题
    public static final int COMMENT_CHANGE = 1016;

    public static final int TEXT_SIZE = 1017;
    public static final int TEENAGER_MODE = 1018;

    /**
     * 发布的三种状态
     */
    public static final String pb_success = "success";
    public static final String pb_start = "start";
    public static final String pb_error = "error";
    public static final String pb_progress = "progress"; //发布进度
    public static final String pb_cant_talk = "talk";  //禁言
}
