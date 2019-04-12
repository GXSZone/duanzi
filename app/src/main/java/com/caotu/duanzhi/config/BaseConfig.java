package com.caotu.duanzhi.config;

import com.caotu.duanzhi.utils.MySpUtils;

public final class BaseConfig {
    public static final boolean isDebug = true;//是否是Debug模式  控制log打印,以及推送tag和接口地址
    // TODO: 2019/3/21 打线上包记得关闭
    public static final boolean isTestMode = true; //是否是测试开发模式
    public static String baseApi;
//    String baseApi = "http://192.168.1.114:8860/NHDZSEVER"; //测试接口
//    public static String baseApi = "https://api.itoutu.com:8899/NHDZSEVER"; //正式接口
//    String baseApi = "http://192.168.1.111:8091/NHDZSEVER/";// 本地服务器
//    String baseApi = "http://101.69.230.98:8860/NHDZSEVER";
//    http://101.69.230.98:8860/NHDZSEVER

    public static String COMMUNITY_CONVENTION;
    //帮助反馈页面
    public static String KEY_FEEDBACK;
    //用户协议
    public static String KEY_USER_AGREEMENT;
    public static String APP_NAME;
    //分享文本
    public static String SHARE_CONTENT_TEXT;
    public static String appName;

    static {
        if (isTestMode) {
            int anInt = MySpUtils.getInt(MySpUtils.sp_test_http, 0);
            if (anInt == 0) {
                baseApi = "http://192.168.1.114:8860/NHDZSEVER";
            } else {
                baseApi = "https://api.itoutu.com:8899/NHDZSEVER";
            }
            int name = MySpUtils.getInt(MySpUtils.sp_test_name, 0);
            if (name == 0) {
                initConfig1();
            } else {
                initConfig2();
            }
        } else {
            baseApi = isDebug ? "http://192.168.1.114:8860/NHDZSEVER" : "https://api.itoutu.com:8899/NHDZSEVER";
            initConfig1();
        }
    }

    private static void initConfig2() {
        KEY_FEEDBACK = "https://active.diqyj.cn/apph5page_nhdz/help3.html";
        KEY_USER_AGREEMENT = "https://active.diqyj.cn/apph5page_nhdz/userprotocol3.html";
        COMMUNITY_CONVENTION = "https://active.diqyj.cn/apph5page_nhdz/pact3.html";
        APP_NAME = "DY";
        SHARE_CONTENT_TEXT = "内含段友，内含的不只是段子";
        appName = "内含段友";
    }

    private static void initConfig1() {
        COMMUNITY_CONVENTION = "https://active.diqyj.cn/apph5page_nhdz/pact.html";
        KEY_FEEDBACK = "https://active.diqyj.cn/apph5page_nhdz/help.html";
        KEY_USER_AGREEMENT = "https://active.diqyj.cn/apph5page_nhdz/userprotocol.html";
        APP_NAME = "NH";
        SHARE_CONTENT_TEXT = "内含段子，内含的不只是段子";
        appName = "内含段子";
    }

    /*******************************内含段子***************************************/
//    //社区公约url
//    public static String COMMUNITY_CONVENTION = "https://active.diqyj.cn/apph5page_nhdz/pact.html";
//    //帮助反馈页面
//    public static String KEY_FEEDBACK = "https://active.diqyj.cn/apph5page_nhdz/help.html";
//    //用户协议
//    public static String KEY_USER_AGREEMENT = "https://active.diqyj.cn/apph5page_nhdz/userprotocol.html";
//    public static String APP_NAME = "NH";
//    //分享文本
//    public static String SHARE_CONTENT_TEXT = "内含段子，内含的不只是段子";
//    public static String appName = "内含段子";

    /**********************************内含段友****************************************/
//    //帮助与反馈
//    public static   String KEY_FEEDBACK = "https://active.diqyj.cn/apph5page_nhdz/help3.html";
//    //用户协议
//    public static   String KEY_USER_AGREEMENT = "https://active.diqyj.cn/apph5page_nhdz/userprotocol3.html";
//    //社区公约
//    public static   String COMMUNITY_CONVENTION = "https://active.diqyj.cn/apph5page_nhdz/pact3.html";
//    public static   String APP_NAME = "DY";
//    //分享文本
//    public static  String SHARE_CONTENT_TEXT = "内含段友，内含的不只是段子";
//    public static  String appName = "内含段友";

    /**********************************内含段友名称,皮皮段子icon****************************************/
//    //帮助与反馈
//    String KEY_FEEDBACK = "https://active.diqyj.cn/apph5page_nhdz/help3.html";
//    //用户协议
//    String KEY_USER_AGREEMENT = "https://active.diqyj.cn/apph5page_nhdz/userprotocol3.html";
//    //社区公约
//    String COMMUNITY_CONVENTION = "https://active.diqyj.cn/apph5page_nhdz/pact3.html";
//    String APP_NAME = "DP";
//    //分享文本
//    String SHARE_CONTENT_TEXT = "内含段友，内含的不只是段子";
//    String appName = "内含段友";


    //腾讯云配置
    public static final long keyDuration = 600; //SecretKey 的有效时间，单位秒
    public static final String COS_APPID = "1256675270";
    public static final String COS_SID = "AKIDhCSSCgutb3FBrHwLyMTLxINCl59xuqvl";
    public static final String COS_SKEY = "nMglbCYfXAYhcIjutgFbjdKn24tVt31u";
    public static final String COS_BUCKET_AREA = "ap-shanghai";
    public static final String COS_BUCKET_NAME = "ctkj-1256675270";

    public static final String buglyId = "81c966dfe6";//配置buglyid

    public static final String[] REPORTITEMS = new String[]{"广告", "低俗色情", "攻击歧视", "涉政", "血腥暴力", "赌博", "其他"};

}
