package com.caotu.duanzhi.config;

public interface BaseConfig {
    boolean isDebug = true;//是否是Debug模式  控制log打印

//     String baseApi = "http://192.168.1.111:8091/CTKJSEVER/";// 测试服务器
     String baseApi = "http://192.168.1.114:8807/CTKJSEVER/";// 本地服务器
    // String baseApi = "http://101.69.230.98:8807/CTKJSEVER/";// 测试服务器
//    String baseApi = "https://api.itoutu.com:8898/CTKJSEVER/";// 正式服务器

    //腾讯云配置
    long keyDuration = 600; //SecretKey 的有效时间，单位秒
    String COS_APPID = "1256675270";
    String COS_SID = "AKIDhCSSCgutb3FBrHwLyMTLxINCl59xuqvl";
    String COS_SKEY = "nMglbCYfXAYhcIjutgFbjdKn24tVt31u";
    String COS_BUCKET_AREA = "ap-shanghai";
    String COS_BUCKET_NAME = "ctkj-1256675270";

    String buglyId = "81c966dfe6";//配置buglyid
}
