package com.caotu.duanzhi.config;

import android.os.Environment;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.utils.AESUtils;
import com.caotu.duanzhi.utils.MySpUtils;

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

    public static String getFilePath() {
        return MyApplication.getInstance().getFilesDir().getAbsolutePath();
    }

    /**
     * 用户名的文字水印
     *
     * @return
     */
    public static String getTextImagePath() {
        String imagePath = MyApplication.getInstance().getFilesDir().getAbsolutePath()
                + File.separator + "textImage.jpg";
        return imagePath;
    }

    public static String getUserImagePath() {
        String name = AESUtils.getMd5Value(MySpUtils.getMyName());
        String imagePath = PathConfig.getFilePath() + File.separator + name + ".jpg";
        return imagePath;
    }

    /**
     * type为0则为横视频,type=1则为竖视频
     *
     * @param type
     * @return
     */
    public static String getVideoEndName(int type) {
        if (type == 0) {
            return "videoHEndWater.mp4";
        } else {
            return "videoVEndWater.mp4";
        }
    }

    /**
     * type为0则为横视频,type=1则为竖视频
     *
     * @param type
     * @return
     */
    public static String getAbsoluteVideoByWaterPath(int type) {
        if (type == 0) {
            return getFilePath() + File.separator + "videoHEndWater.mp4";
        } else {
            return getFilePath() + File.separator + "videoVEndWater.mp4";
        }
    }


    /**
     * 图像选择图片保存位置
     */
    public static final String LOCALFILE = Environment.
            getExternalStorageDirectory().getAbsolutePath() +
            File.separator + "DCIM/duanzi" + File.separator;

    /**
     * VV朋友圈保存位置
     */
    public static final String VIDEO_PATH = Environment.
            getExternalStorageDirectory().getAbsolutePath() +
            File.separator + "DCIM/duanzi" + File.separator;

}
