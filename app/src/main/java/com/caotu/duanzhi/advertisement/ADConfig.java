package com.caotu.duanzhi.advertisement;

/**
 * 广点通文档
 * https://developers.adnet.qq.com/doc/android/union/union_native_express
 */
public interface ADConfig {
    String AD_APPID = "1109968862";

    //SplashActivity 开屏广告
    String splash_id = "7090482709154032";

    //信息流广告  推荐列表使用
    String recommend_id = "5010496091487588";

    //内容详情广告
    String detail_id = "5060494072972532";
//    String detail_id = "5010384749454046";

    //评论区广告
    String comment_id = "4070799062274550";

    //banner广告----也是原生的信息流,只是样式不一样
    String banner_id = "6030682769454098";


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
    String tab_video_show = "sysp_show";
    String tab_video_click = "sysp_click";
    String tab_pic_show = "sytp_show";
    String tab_pic_click = "sytp_click";
    String tab_text_show = "sydz_show";
    String tab_text_click = "sydz_click";

    String comment_show = "AZ_PL_show";
    String comment_click = "AZ_PL_click";

    String detail_header_show = "AZ_NRXQ_show";
    String detail_header_click = "AZ_NRXQ_click";

    String banner_show = "AZ_BANNER_show";
    String banner_click = "AZ_BANNER_click";
}
