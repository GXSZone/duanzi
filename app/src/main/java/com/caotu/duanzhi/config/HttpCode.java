package com.caotu.duanzhi.config;

/**
 * 网络请求的code常量定义
 */
public interface HttpCode {
    String success_code = "1000";   //请求成功
    String in_the_review = "3007"; //"该帖子已被删除"
    String login_failure = "1024"; //错误信息：登陆失效
    String login_error = "2001";  //无效登陆
    String no_bind_phone = "2003";  //未绑定手机
    String user_has_exsit = "3001";  //用户已存在
    String user_name = "3002";  //用户昵称碰到敏感词啦，改一下呗
    String user_sign = "3005"; //签名碰到敏感词啦，改一下呗
    String cannot_change_user_name = "1101"; //昵称一个月只能修改一次哦
    String has_regist_phone = "YES"; //用户已经绑定手机
    String cant_talk = "3838";//用户被禁言
    String not_self_phone = "2005"; //2005不是自己手机
    String has_bind = "2006";// 2006手机号被绑定过
}
