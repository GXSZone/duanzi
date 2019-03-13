package com.caotu.duanzhi.other;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.caotu.duanzhi.config.PathConfig;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.utils.ImageMarkUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.lansosdk.VideoFunctions;
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
                VideoEditor editor = new VideoEditor();
                editor.setOnProgessListener(new onVideoEditorProgressListener() {
                    @Override
                    public void onProgress(VideoEditor v, int percent) {
                        Log.i("fileService", "onProgress: " + percent);
                    }
                });
                //直接覆盖即可,不需要删除
//                LanSongFileUtil.deleteFile(PathConfig.getAbsoluteVideoByWaterPath(0));
//                LanSongFileUtil.deleteFile(PathConfig.getAbsoluteVideoByWaterPath(1));

                String videoEndHByWater = VideoFunctions.AddVideoEndPicture(editor, videoH, userImagePath,
                        PathConfig.getFilePath(), PathConfig.getVideoEndName(0),0);
                Log.i("fileService", "onHandleIntent: " + videoEndHByWater);

                String videoEndVByWater = VideoFunctions.AddVideoEndPicture(editor, videoV, userImagePath,
                        PathConfig.getFilePath(), PathConfig.getVideoEndName(1),1);
                Log.i("fileService", "onHandleIntent: " + videoEndVByWater);
            }

        }
    }
}
