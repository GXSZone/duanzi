package com.caotu.duanzhi.alihotfix;

import android.content.Context;
import android.support.annotation.Keep;
import android.util.Log;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.config.BaseConfig;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixApplication;
import com.taobao.sophix.SophixEntry;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

/**
 * Sophix入口类，专门用于初始化Sophix，不应包含任何业务逻辑。
 * 此类必须继承自SophixApplication，onCreate方法不需要实现。
 * 此类不应与项目中的其他类有任何互相调用的逻辑，必须完全做到隔离。
 * AndroidManifest中设置application为此类，而SophixEntry中设为原先Application类。
 * 注意原先Application里不需要再重复初始化Sophix，并且需要避免混淆原先Application类。
 * 如有其它自定义改造，请咨询官方后妥善处理。
 * <p>
 * https://help.aliyun.com/document_detail/69874.html?spm=a2c4g.11186623.4.3.695064aaofZwdt#1.8%20%E5%85%B6%E4%BB%96%E5%AD%A6%E4%B9%A0%E8%B5%84%E6%96%99
 * <p>
 * 补丁生效时间问题:https://help.aliyun.com/knowledge_detail/53230.html?spm=a2c4g.11186631.2.14.225c408dPdQzjS
 */
public class SophixStubApplication extends SophixApplication {
    private final String TAG = "SophixStubApplication";

    // 此处SophixEntry应指定真正的Application，并且保证RealApplicationStub类名不被混淆。
    @Keep
    @SophixEntry(MyApplication.class)
    static class RealApplicationStub {
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//         如果需要使用MultiDex，需要在此处调用。
//         MultiDex.install(this);
        initSophix();
    }

    private void initSophix() {
        String appVersion = "0.0.0";
        try {
            appVersion = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0)
                    .versionName;
        } catch (Exception e) {
        }
        final SophixManager instance = SophixManager.getInstance();
        instance.setContext(this)
                .setAppVersion(appVersion)
                .setSecretMetaData(BaseConfig.ALI_APPKEY, BaseConfig.ALI_APPSECRET, BaseConfig.ALI_RSA)
                .setEnableDebug(BaseConfig.isDebug)
//                .setEnableFullLog()
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    /**
                     * code: 补丁加载状态码, 详情查看PatchStatus类说明
                     * info: 补丁加载详细说明
                     * handlePatchVersion: 当前处理的补丁版本号, 0:无 -1:本地补丁 其它:后台补丁
                     * @param mode
                     * @param code
                     * @param info
                     * @param handlePatchVersion
                     *
                     */
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            Log.i(TAG, "sophix load patch success!");
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 如果需要在后台重启，建议此处用SharePreference保存状态。
                            Log.i(TAG, "sophix preload patch success. restart app to make effect.");
//                            MySpUtils.putBoolean(MySpUtils.HOTFIX_IS_NEED_RESTART,true);
                        }
                        String msg = new StringBuilder().append("Mode:").append(mode)
                                .append(" Code:").append(code)
                                .append(" Info:").append(info)
                                .append(" HandlePatchVersion:").append(handlePatchVersion).toString();
                        Log.d(TAG, msg);
                    }
                }).initialize();
    }
    /*
      //兼容老版本的code说明
    int CODE_LOAD_SUCCESS = 1;//加载阶段, 成功
    int CODE_ERR_INBLACKLIST = 4;//加载阶段, 失败设备不支持
    int CODE_REQ_NOUPDATE = 6;//查询阶段, 没有发布新补丁
    int CODE_REQ_NOTNEWEST = 7;//查询阶段, 补丁不是最新的
    int CODE_DOWNLOAD_SUCCESS = 9;//查询阶段, 补丁下载成功
    int CODE_DOWNLOAD_BROKEN = 10;//查询阶段, 补丁文件损坏下载失败
    int CODE_UNZIP_FAIL = 11;//查询阶段, 补丁解密失败
    int CODE_LOAD_RELAUNCH = 12;//预加载阶段, 需要重启
    int CODE_REQ_APPIDERR = 15;//查询阶段, appid异常
    int CODE_REQ_SIGNERR = 16;//查询阶段, 签名异常
    int CODE_REQ_UNAVAIABLE = 17;//查询阶段, 系统无效
    int CODE_REQ_SYSTEMERR = 22;//查询阶段, 系统异常
    int CODE_REQ_CLEARPATCH = 18;//查询阶段, 一键清除补丁
    int CODE_PATCH_INVAILD = 20;//加载阶段, 补丁格式非法
    //查询阶段的code说明
    int CODE_QUERY_UNDEFINED = 31;//未定义异常
    int CODE_QUERY_CONNECT = 32;//连接异常
    int CODE_QUERY_STREAM = 33;//流异常
    int CODE_QUERY_EMPTY = 34;//请求空异常
    int CODE_QUERY_BROKEN = 35;//请求完整性校验失败异常
    int CODE_QUERY_PARSE = 36;//请求解析异常
    int CODE_QUERY_LACK = 37;//请求缺少必要参数异常
    //预加载阶段的code说明
    int CODE_PRELOAD_SUCCESS = 100;//预加载成功
    int CODE_PRELOAD_UNDEFINED = 101;//未定义异常
    int CODE_PRELOAD_HANDLE_DEX = 102;//dex加载异常
    int CODE_PRELOAD_NOT_ZIP_FORMAT = 103;//基线dex非zip格式异常
    int CODE_PRELOAD_REMOVE_BASEDEX = 105;//基线dex处理异常
    //加载阶段的code说明 分三部分dex加载, resource加载, lib加载
    //dex加载
    int CODE_LOAD_UNDEFINED = 71;//未定义异常
    int CODE_LOAD_AES_DECRYPT = 72;//aes对称解密异常
    int CODE_LOAD_MFITEM = 73;//补丁SOPHIX.MF文件解析异常
    int CODE_LOAD_COPY_FILE = 74;//补丁拷贝异常
    int CODE_LOAD_SIGNATURE = 75;//补丁签名校验异常
    int CODE_LOAD_SOPHIX_VERSION = 76;//补丁和补丁工具版本不一致异常
    int CODE_LOAD_NOT_ZIP_FORMAT = 77;//补丁zip解析异常
    int CODE_LOAD_DELETE_OPT = 80;//删除无效odex文件异常
    int CODE_LOAD_HANDLE_DEX = 81;//加载dex异常
    // 反射调用异常
    int CODE_LOAD_FIND_CLASS = 82;
    int CODE_LOAD_FIND_CONSTRUCTOR = 83;
    int CODE_LOAD_FIND_METHOD = 84;
    int CODE_LOAD_FIND_FIELD = 85;
    int CODE_LOAD_ILLEGAL_ACCESS = 86;
    //resource加载
    public static final int CODE_LOAD_RES_ADDASSERTPATH = 123;//新增资源补丁包异常
    //lib加载
    int CODE_LOAD_LIB_UNDEFINED = 131;//未定义异常
    int CODE_LOAD_LIB_CPUABIS = 132;//获取primaryCpuAbis异常
    int CODE_LOAD_LIB_JSON = 133;//json格式异常
    int CODE_LOAD_LIB_LOST = 134;//lib库不完整异常
    int CODE_LOAD_LIB_UNZIP = 135;//解压异常
    int CODE_LOAD_LIB_INJECT = 136;//注入异常
     */

}
