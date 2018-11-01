package com.caotu.duanzhi.config;

/**
 * 网络请求的code常量定义
 */
public interface HttpCode {
    String success_code = "1000";   //请求成功
    String in_the_review = "3007"; //"操作失败，正在审核中！"
    String login_failure = "1024"; //错误信息：登陆失效
    String no_bind_phone = "2003";  //未绑定手机
    String user_has_exsit = "3001";  //用户已存在
    String has_regist_phone = "YES";
}
