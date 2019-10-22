package com.caotu.duanzhi.advertisement;

/**
 * 广点通文档
 * https://developers.adnet.qq.com/doc/android/union/union_native_express
 */
public interface ADConfig {
    String AD_APPID = "1109934540";

    //SplashActivity 开屏广告
    String splash_id = "3060785673326397";

    //信息流广告  推荐列表使用
    String recommend_id = "1000386655841049";

    //内容详情广告
    String datail_id = "9030984693833302";

    //评论区广告
    String comment_id = "7090888663639215";

    //banner广告----也是原生的信息流,只是样式不一样
    String banner_id = "1040684673762331";


    /**
     * banner 位的广告位置,第二个位置默认
     */
    int bannerInDex = 1;

    class AdOpenConfig {
        public static boolean splashAdIsOpen = false;
        public static boolean commentAdIsOpen = false;
        public static boolean contentAdIsOpen = false; //针对内容详情页面头广告
        public static boolean itemAdIsOpen = false;  //针对推荐列表和关注列表
        public static boolean bannerAdIsOpen = false;
    }

    /**
     * 广告埋点字段
     */
    String splash_show = "AZ_SP_show";
    String splash_click = "AZ_SP_click";
    String splash_skip = "AZ_SP_skip";

    String item_show = "AZ_XXL_show";
    String item_click = "AZ_XXL_click";

    String comment_show = "AZ_PL_show";
    String comment_click = "AZ_PL_click";

    String detail_header_show = "AZ_NRXQ_show";
    String detail_header_click = "AZ_NRXQ_click";

    String banner_show = "AZ_BANNER_show";
    String banner_click = "AZ_BANNER_click";
}
