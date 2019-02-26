package com.caotu.duanzhi.other;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.caotu.duanzhi.MyApplication;
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
 * 加水印和加片尾服务
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
//                Log.i(TAG, "水印加载完成: " + waterFilePath);
        ToastUtil.showShort("保存成功:" + "DCIM/duanzi");
        //通知系统相册更新
        if (TextUtils.isEmpty(waterFilePath)) return;
        File file1 = new File(waterFilePath);
        //获取ContentResolve对象，来操作插入视频
        ContentResolver localContentResolver = MyApplication.getInstance().getContentResolver();
        //ContentValues：用于储存一些基本类型的键值对
        ContentValues localContentValues = getVideoContentValues(file1, System.currentTimeMillis());
        //insert语句负责插入一条新的纪录，如果插入成功则会返回这条记录的id，如果插入失败会返回-1。
        Uri localUri = localContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localContentValues);

        MyApplication.getInstance().getRunningActivity().
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        localUri));

        MyApplication.getInstance().getRunningActivity()
                .sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(new File(waterFilePath))));
        isDownLoad = false;
    }

    /**
     * 视频存在本地
     *
     * @param paramFile
     * @param paramLong
     * @return
     */
    public static ContentValues getVideoContentValues(File paramFile, long paramLong) {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("title", paramFile.getName());
        localContentValues.put("_display_name", paramFile.getName());
        localContentValues.put("mime_type", "video/3gp");
        localContentValues.put("datetaken", Long.valueOf(paramLong));
        localContentValues.put("date_modified", Long.valueOf(paramLong));
        localContentValues.put("date_added", Long.valueOf(paramLong));
        localContentValues.put("_data", paramFile.getAbsolutePath());
        localContentValues.put("_size", Long.valueOf(paramFile.length()));
        return localContentValues;
    }

    private void initWaterMark() {
        /**
         * 创建VideoEditor对象, 并设置进度监听
         */
        if (mEditor == null) {
            mEditor = new VideoEditor();
            mEditor.setOnProgessListener(new onVideoEditorProgressListener() {

                @Override
                public void onProgress(VideoEditor v, int percent) {
//                Log.i(TAG, "加水印进度 onProgress: " + String.valueOf(percent) + "%");
                    if (percent == 100) {
                        //删除原先的
                        LanSongFileUtil.deleteDir(downLoadVideoFile);
                        LanSongFileUtil.deleteDir(new File(LanSongFileUtil.TMP_DIR));
                    }
                }
            });
        }

    }

    //静态变量才能保证不随fragment的销毁而制空
    public static String downLoadVideoUrl;
    public  boolean isDownLoad = false;

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "服务开启" + Thread.currentThread().getName());
        String url = intent.getStringExtra(KEY_URL);
        if (TextUtils.isEmpty(url)) return;

        if (isDownLoad) {
//            Log.i("bianliang", "对象url: " + bean.VideoUrl + "之前url:" + downLoadVideoUrl);
            if (TextUtils.equals(downLoadVideoUrl, url)) {
                ToastUtil.showShort("在下载哦，请耐心等待一下～");
            } else if (!TextUtils.equals(downLoadVideoUrl, url)) {
                ToastUtil.showShort("已有正在下载的视频哦～");
            }
            return;
        }
        String end = url.substring(url.lastIndexOf("."), url.length());
        String fileName = "duanzi-" + System.currentTimeMillis() + end;
        initWaterMark();
        downLoadVideoUrl = url;
        isDownLoad = true;
        ToastUtil.showShort("正在下载中");
        OkGo.<File>get(url)
                .execute(new FileCallback(PathConfig.VIDEO_PATH, fileName) {
                    @Override
                    public void onSuccess(Response<File> response) {
                        downLoadVideoFile = response.body();
                        Log.i(TAG, "下载完成开始加水印");
                        addWaterMark();
                    }

                    @Override
                    public void onError(Response<File> response) {
                        ToastUtil.showShort("下载失败");
                        isDownLoad = false;
                        super.onError(response);
                    }
                });
    }
}
