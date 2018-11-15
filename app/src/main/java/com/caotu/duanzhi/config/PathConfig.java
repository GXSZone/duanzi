package com.caotu.duanzhi.config;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2018/6/24.
 */

public class PathConfig {

    /**
     * 判断当前存储卡是否可用
     **/
    public static boolean checkSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    /**
     * 图像选择图片保存位置
     */
    public static final String LOCALFILE = Environment.
            getExternalStorageDirectory().getAbsolutePath() +
            File.separator + "DCIM/duanzi"+ File.separator;

    /**
     * VV朋友圈保存位置
     */
    public static final String VIDEO_PATH = Environment.
            getExternalStorageDirectory().getAbsolutePath() +
            File.separator + "DCIM/duanzi" + File.separator;

}
