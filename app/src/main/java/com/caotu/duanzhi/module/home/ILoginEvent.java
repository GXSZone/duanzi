package com.caotu.duanzhi.module.home;

/**
 * 为了统一只在activity中注册eventbus就够了,不用再fragment再注册一边
 */
public interface ILoginEvent {
    void login();

    void loginOut();
}
