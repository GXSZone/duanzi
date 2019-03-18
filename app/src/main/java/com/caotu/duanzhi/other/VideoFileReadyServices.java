package com.caotu.duanzhi.other;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.PathConfig;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.utils.ImageMarkUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.lansosdk.VideoFunctions;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.lansosdk.videoeditor.VideoEditor;
import com.lansosdk.videoeditor.onVideoEditorProgressListener;

import java.io.File;

/**
 * 加水印和加片尾服务
 */
public class VideoFileReadyServices extends IntentService {


    public VideoFileReadyServices() {
        super("WaterMarkServices");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // 2019/3/12 提前处理生成图片文字水印的操作,另外加文字水印的片尾也直接加好
        String videoH = ImageMarkUtil.copyAssets(this, "videoHEnd.mp4");
        String videoV = ImageMarkUtil.copyAssets(this, "videoVEnd.mp4");
        //这里需要判断那张图片是否已经存在
        if (LoginHelp.isLogin()) {
            String userImagePath = PathConfig.getUserImagePath();
            File file = new File(userImagePath);
            if (!file.exists()) {
                ImageMarkUtil.textToPicture("@" + MySpUtils.getMyName(), this);
            }
            if (intent == null) return;
            boolean isNeedGenerate = intent.getBooleanExtra("isNeedGenerate", false);
            String waterPath = PathConfig.getAbsoluteVideoByWaterPath(0);
            String waterPath1 = PathConfig.getAbsoluteVideoByWaterPath(1);
            if (!isNeedGenerate) {
                if (!new File(waterPath).exists() || !new File(waterPath1).exists()) {
                    dealVideoEnd(videoH, videoV, userImagePath, waterPath, waterPath1);
                }
            } else {
                dealVideoEnd(videoH, videoV, userImagePath, waterPath, waterPath1);
            }
        }
    }

    private void dealVideoEnd(String videoH, String videoV, String userImagePath,
                              String waterPath, String waterPath1) {
        VideoEditor editor = new VideoEditor();
        editor.setOnProgessListener(new onVideoEditorProgressListener() {
            @Override
            public void onProgress(VideoEditor v, int percent) {
                Log.i("fileService", "onProgress: " + percent);
            }
        });
        //直接覆盖即可,不需要删除,因为后面有这个判断,需要用到文件是否存在
        LanSongFileUtil.deleteFile(waterPath);
        LanSongFileUtil.deleteFile(waterPath1);
        if (!new File(waterPath).exists() || !new File(waterPath1).exists()) {
            Log.i("fileService", "删除成功");
        }
        String videoEndHByWater = VideoFunctions.AddVideoEndPicture(editor, videoH, userImagePath,
                PathConfig.getFilePath(), PathConfig.getVideoEndName(0), 0);
        Log.i("fileService", "onHandleIntent: " + videoEndHByWater);
        if (BaseConfig.isDebug) {
            ToastUtil.showShort("横视频片尾已经处理好");
        }

        String videoEndVByWater = VideoFunctions.AddVideoEndPicture(editor, videoV, userImagePath,
                PathConfig.getFilePath(), PathConfig.getVideoEndName(1), 1);
        Log.i("fileService", "onHandleIntent: " + videoEndVByWater);
        if (BaseConfig.isDebug) {
            ToastUtil.showShort("竖视频片尾已经处理好");
        }
    }
}
