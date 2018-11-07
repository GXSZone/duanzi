package com.caotu.duanzhi.config;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/7/29 23:38
 */
public class EventBusCode {

    public static final int REFRESH_FOCUS_COUNT = 101;
    public static final int REFRESH_PARISE_COUNT = 102;
    public static final int REFRESH_COMMEND_COUNT = 103;//评论个数
    public static final int REFRESH_COMMEND_CHILD_COUNT = 104;//子评论个数
    public static final int REFRESH_FOLLOW = 106;//子评论个数
    public static final int REFRESH_AVATAR_HANGER = 107;//子评论个数
    //登录
    public static final int LOGIN = 1007;
    //退出登录
    public static final int LOGIN_OUT = 1008;
    //关注和取消关注
    public static final int FOCUS = 1009;
    //发布后的code
    public static final int PUBLISH = 1010;
    /**
     * 发布的三种状态
     */
    public static final String pb_success = "success";
    public static final String pb_start = "start";
    public static final String pb_error = "error";
}
