package com.caotu.duanzhi.module.download;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.caotu.duanzhi.config.PathConfig;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.utils.ImageMarkUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.lansosdk.videoeditor.MediaInfo;
import com.lansosdk.videoeditor.VideoEditor;
import com.lansosdk.videoeditor.onVideoEditorProgressListener;

import java.io.File;

/**
 * 加水印和加片尾服务 对应的bug  具体原因是因为从Android 8.0开始禁止应用在后台运行时创建Service
 * 37202 java.lang.IllegalStateException
 * Not allowed to start service Intent { cmp=com.caotu.duanzhi/.module.download.VideoFileReadyServices (has extras) }:
 * app is in background uid UidRecord{20bb6b5 u0a152 SVC idle change:uncached procs:2 seq(0,0,0)}
 * com.caotu.duanzhi.utils.HelperForStartActivity.startVideoService(HelperForStartActivity.java:464)
 */
public class VideoFileReadyServices extends JobIntentService {

    // Service unique ID
    static final int SERVICE_JOB_ID = 50;

    // Enqueuing work in to this service.
    public static void enqueueWork(Context context, Intent work) {
        try {
            enqueueWork(context, VideoFileReadyServices.class, SERVICE_JOB_ID, work);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        String srcFilePath = intent.getStringExtra("srcFile");
        String waterPath = PathConfig.getFilePath() + File.separator + "videoHEnd.mp4";
        String waterPath1 = PathConfig.getFilePath() + File.separator + "videoVEnd.mp4";
        if (!LanSongFileUtil.fileExist(waterPath)) {
            waterPath = ImageMarkUtil.copyAssets(this, "videoHEnd.mp4");
        }
        if (!LanSongFileUtil.fileExist(waterPath1)) {
            waterPath1 = ImageMarkUtil.copyAssets(this, "videoVEnd.mp4");
        }

        //如果是登录状态则需要先处理片尾视频,再拼接视频,否则只处理片尾的尺寸分辨率问题,不需要加水印
        if (LoginHelp.isLogin()) {
            String userImagePath = PathConfig.getUserImagePath();
            File file = new File(userImagePath);
            if (!file.exists()) {
                ImageMarkUtil.textToPicture("@" + MySpUtils.getMyName(), this);
            }
            //保证水印图片存在
            if (file.exists()) {
                dealVideoEnd(srcFilePath, waterPath, waterPath1, userImagePath);
            } else {
                dealVideoEnd(srcFilePath, waterPath, waterPath1, null);
            }
        } else {
            dealVideoEnd(srcFilePath, waterPath, waterPath1, null);
        }
    }


    private void dealVideoEnd(String srcFilePath, String waterPath, String waterPath1, String userImagePath) {
        VideoEditor editor = new VideoEditor();
        editor.setOnProgessListener(new onVideoEditorProgressListener() {
            @Override
            public void onProgress(VideoEditor v, int percent) {
                Log.i("fileService", "onProgress: " + percent);
            }
        });
        MediaInfo info = new MediaInfo(srcFilePath);
        //这个是缩放到的宽高
        int width = 0;
        int height = 0;
        if (info.prepare()) {
            width = info.getWidth();
            height = info.getHeight();
            Log.i("fileService", "手机信息: " + Build.MODEL);
            Log.i("fileService", "原视频信息: " + info.toString());
        } else {
            VideoDownloadHelper.isDownLoad = false;
            ToastUtil.showShort("保存成功: " + srcFilePath);
        }


        String endWater = (height > width + 100) ? waterPath1 : waterPath;
        MediaInfo info1 = new MediaInfo(endWater);
        if (info1.prepare()) {
            Log.i("fileService", "片尾信息: " + info1.toString());
        }
        String endVideoPath;
        //如果是登录状态则需要先处理片尾视频,再拼接视频,否则只处理片尾的尺寸分辨率问题,不需要加水印
//        PathConfig.getFilePath(), PathConfig.getVideoEndName(0)
        // TODO: 2019-10-18 已经尝试使用相同的分辨率,还是有封面问题,后面尝试比特率和软硬解,现在三星默认是硬解
        if (TextUtils.isEmpty(userImagePath)) {
            endVideoPath = endWater;
//            endVideoPath = editor.executeScaleVideoFrame(endWater, width, height);
        } else {
            endVideoPath = editor.executeAddPitureAtXYTime(endWater, userImagePath, 0.7f, 1.2f);
//            endVideoPath = editor.executeCropOverlayAtTime(endWater, userImagePath, width, height);
        }
        if (TextUtils.isEmpty(endVideoPath) || !LanSongFileUtil.fileExist(endVideoPath)) {
            downloadSuccess(endVideoPath, srcFilePath);
            return;
        }
        Log.i("fileService", "是否是竖视频:" + (height > width + 100) + "   处理后完后的视频片尾地址: " + endVideoPath);
        String videoDealPath = editor.executeConcatMP4(new String[]{srcFilePath, endVideoPath});

        if (!TextUtils.isEmpty(videoDealPath)) {
            Log.i("fileService", "视频片尾拼接成功: " + videoDealPath);
            VideoDownloadHelper.noticeSystemCamera(new File(videoDealPath));
            LanSongFileUtil.deleteFile(srcFilePath);
        } else {
            Log.i("fileService", "片尾处理失败");
            VideoDownloadHelper.noticeSystemCamera(new File(srcFilePath));
        }
        downloadSuccess(endVideoPath, videoDealPath);

    }

    private void downloadSuccess(String endVideoPath, String videoDealPath) {
        LanSongFileUtil.deleteFile(endVideoPath);
        VideoDownloadHelper.isDownLoad = false;
        ToastUtil.showShort("保存成功: " + videoDealPath);
    }
/**
 * executeScaleOverlay  缩放加水印
 * [0:v]scale=%d:%d [scale];[scale][1:v] overlay=%d:%d
 *
 * executeCropOverlay   裁剪加水印
 * "[0:v]crop=%d:%d:%d:%d [crop];[crop][1:v] " +
 *                     "overlay=%d:%d"
 *
 * executeAddPitureAtXYTime 指定位置加水印:
 * "[0:v][1:v] overlay=%d:%d:enable='between(t,%f,%f)'"
 *
 * @param editor
 * @param srcVideo
 * @param imagePath
 * @param path
 * @param name
 * @return
 */

}
