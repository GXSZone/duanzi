package com.caotu.duanzhi.config;

public interface BaseConfig {
    boolean isDebug = true;//是否是Debug模式  控制log打印

    String baseApi = "http://192.168.1.114:8860/NHDZSEVER"; //测试接口
//    String baseApi = "https://api.itoutu.com:8899/NHDZSEVER"; //正式接口
//    String baseApi = "http://192.168.1.111:8091/NHDZSEVER/";// 本地服务器
    //"http://101.69.230.98:8807/CTKJSEVER/

    //社区公约url
    String community_convention = "https://active.diqyj.cn/apph5page_nhdz/pact.html";
    //腾讯云配置
    long keyDuration = 600; //SecretKey 的有效时间，单位秒
    String COS_APPID = "1256675270";
    String COS_SID = "AKIDhCSSCgutb3FBrHwLyMTLxINCl59xuqvl";
    String COS_SKEY = "nMglbCYfXAYhcIjutgFbjdKn24tVt31u";
    String COS_BUCKET_AREA = "ap-shanghai";
    String COS_BUCKET_NAME = "ctkj-1256675270";

    String buglyId = "81c966dfe6";//配置buglyid

    String[] REPORTITEMS = new String[]{"广告", "低俗色情", "攻击歧视", "涉政", "血腥暴力", "赌博", "其他"};

    String MOMENTS_TYPE_VIDEO_TRANSVERSE = "1";//横视频
    String MOMENTS_TYPE_VIDEO_PORTRAIT = "2";//竖视频
    String MOMENTS_TYPE_IMAGE = "3";//图片
    String MOMENTS_TYPE_TEXT = "4"; //文字
    String MOMENTS_TYPE_WEB = "5"; //web类型
}
