package com.caotu.adlib;

import cn.admob.admobgensdk.ad.constant.ADMobGenAdPlaforms;

/**
 * http://101.37.118.54/dokuwiki/lib/exe/fetch.php?media=admobgensdk_android_question_answer.pdf
 */
public interface ADLibConfig {


    String AdMobile_APPID = "2376864";


    //广告位序号（默认为0，用于支持单样式多广告位，无需要可以填0或者使用其他构造方法）
    int home_item = 0;
    int comment_item = 1;

    String[] PLATFORMS = {
              ADMobGenAdPlaforms.PLAFORM_ADMOB
            , ADMobGenAdPlaforms.PLAFORM_TOUTIAO  //头条
//            , ADMobGenAdPlaforms.PLAFORM_GDT    //优量汇
            , ADMobGenAdPlaforms.PLAFORM_INMOBI  //inmobi
            , ADMobGenAdPlaforms.PLAFORM_MOBVSITA   //汇量
//            , ADMobGenAdPlaforms.PLAFORM_MGTV
//            , ADMobGenAdPlaforms.PLAFORM_BAIDU
    };

    /**
     * 广告的开关状态
     */
    class AdOpenConfig {
        public static boolean splashAdIsOpen = false;
        public static boolean commentAdIsOpen = false;
        public static boolean contentAdIsOpen = false; //针对内容详情页面头广告
        public static boolean itemAdIsOpen = false;  //针对推荐列表和关注列表
        public static boolean bannerAdIsOpen = false;
    }
}
