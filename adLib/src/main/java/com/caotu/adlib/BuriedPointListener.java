package com.caotu.adlib;

/**
 * 初始化的时候传入监听,用来处理统一埋点问题
 */
public interface BuriedPointListener {
    int splash_type = 0;
    int item_type = 1;
    int banner_type = 2;
    int detail_type = 3;
    int comment_type = 4;


    byte show = 0;
    byte click = 1;


    /**
     * 第一个参数是判断广告位显示位置,第二个参数点击还是显示
     * 信息流埋点回调,不同广告位回调监听不一样
     *
     * @param type
     */
    void adItemBuriedPoint(int type, byte requestType);
}
