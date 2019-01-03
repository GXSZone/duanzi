package com.caotu.duanzhi.other;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.PathConfig;
import com.caotu.duanzhi.utils.ToastUtil;
import com.lansosdk.VideoFunctions;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.lansosdk.videoeditor.VideoEditor;
import com.lansosdk.videoeditor.onVideoEditorProgressListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Response;

import java.io.File;

/**
 * 真是有问题,明明在子线程但是会导致UI主线程卡顿
 */
public class WaterMarkServices extends IntentService {

    public static final String KEY_URL = "URL";
    private static final String TAG = "service_download";

    public WaterMarkServices() {
        super("WaterMarkServices");
    }

    File downLoadVideoFile;
    private VideoEditor mEditor;

    private void addWaterMark() {
        if (downLoadVideoFile == null) {
            ToastUtil.showShort("下载失败");
            return;
        }
        String waterFilePath = VideoFunctions.demoAddPicture(MyApplication.getInstance(), mEditor, downLoadVideoFile.getAbsolutePath());
        //删除原先的
        LanSongFileUtil.deleteDir(downLoadVideoFile);
        Log.i(TAG, "水印加载完成: " + waterFilePath);
        //通知系统相册更新
        MyApplication.getInstance().getRunningActivity().
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse(waterFilePath)));
    }

    private void initWaterMark() {
        /**
         * 创建VideoEditor对象, 并设置进度监听
         */
        mEditor = new VideoEditor();
        mEditor.setOnProgessListener(new onVideoEditorProgressListener() {

            @Override
            public void onProgress(VideoEditor v, int percent) {
                Log.i(TAG, "加水印进度 onProgress: " + String.valueOf(percent) + "%");
                if (percent == 100) {
                    ToastUtil.showShort(R.string.video_save_success);
                }
            }
        });

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "服务开启" + Thread.currentThread().getName());
        initWaterMark();
        String url = intent.getStringExtra(KEY_URL);
        if (TextUtils.isEmpty(url)) return;
        String end = url.substring(url.lastIndexOf("."),
                url.length());
        String fileName = "duanzi-" + System.currentTimeMillis() + end;

        OkGo.<File>get(url)
                .execute(new FileCallback(PathConfig.VIDEO_PATH, fileName) {
                    @Override
                    public void onSuccess(Response<File> response) {
                        downLoadVideoFile = response.body();
                        Log.i(TAG, "下载完成开始加水印");
                        addWaterMark();
                    }
                });
    }
}
