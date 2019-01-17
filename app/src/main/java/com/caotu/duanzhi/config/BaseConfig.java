package com.caotu.duanzhi.config;

public interface BaseConfig {
    boolean isDebug = false;//是否是Debug模式  控制log打印

    //    String baseApi = "http://192.168.1.114:8860/NHDZSEVER"; //测试接口
    String baseApi = "https://api.itoutu.com:8899/NHDZSEVER"; //正式接口
//    String baseApi = "http://192.168.1.111:8091/NHDZSEVER/";// 本地服务器
//    String baseApi = "http://101.69.230.98:8860/NHDZSEVER";
    //http://101.69.230.98:8860/NHDZSEVER

    /*******************************内含段子***************************************/
    //社区公约url
    String COMMUNITY_CONVENTION = "https://active.diqyj.cn/apph5page_nhdz/pact.html";
    //帮助反馈页面
    String KEY_FEEDBACK = "https://active.diqyj.cn/apph5page_nhdz/help.html";
    //用户协议
    String KEY_USER_AGREEMENT = "https://active.diqyj.cn/apph5page_nhdz/userprotocol.html";
    String APP_NAME = "NH";
    //分享文本
    String SHARE_CONTENT_TEXT = "内含段子，内含的不只是段子";
    String appName = "内含段子";

    /**********************************内含段友****************************************/
//    //帮助与反馈
//    String KEY_FEEDBACK = "https://active.diqyj.cn/apph5page_nhdz/help3.html";
//    //用户协议
//    String KEY_USER_AGREEMENT = "https://active.diqyj.cn/apph5page_nhdz/userprotocol3.html";
//    //社区公约
//    String COMMUNITY_CONVENTION = "https://active.diqyj.cn/apph5page_nhdz/pact3.html";
//    String APP_NAME = "DY";
//    //分享文本
//    String SHARE_CONTENT_TEXT = "内含段友，内含的不只是段子";
//    String appName = "内含段友";


    //腾讯云配置
    long keyDuration = 600; //SecretKey 的有效时间，单位秒
    String COS_APPID = "1256675270";
    String COS_SID = "AKIDhCSSCgutb3FBrHwLyMTLxINCl59xuqvl";
    String COS_SKEY = "nMglbCYfXAYhcIjutgFbjdKn24tVt31u";
    String COS_BUCKET_AREA = "ap-shanghai";
    String COS_BUCKET_NAME = "ctkj-1256675270";

    String buglyId = "81c966dfe6";//配置buglyid

    String[] REPORTITEMS = new String[]{"广告", "低俗色情", "攻击歧视", "涉政", "血腥暴力", "赌博", "其他"};

    String TYPE_VIDEO_TRANSVERSE = "1";//横视频
    String TYPE_VIDEO_PORTRAIT = "2";//竖视频
    String TYPE_IMAGE = "3";//图片
    String TYPE_TEXT = "4"; //文字
    String TYPE_WEB = "5"; //web类型
}
