package com.caotu.duanzhi.config;

public interface BaseConfig {
    boolean isDebug = true;//是否是Debug模式  控制log打印

    // String baseApi = "http://192.168.1.111:8091/CTKJSEVER/";// 测试服务器
    // String baseApi = "http://192.168.1.114:8807/CTKJSEVER/";// 本地服务器
    // String baseApi = "http://101.69.230.98:8807/CTKJSEVER/";// 测试服务器
    String baseApi = "https://api.itoutu.com:8898/CTKJSEVER/";// 正式服务器
}
