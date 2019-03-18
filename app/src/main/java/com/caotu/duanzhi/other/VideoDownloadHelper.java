package com.caotu.duanzhi.other;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.PathConfig;
import com.caotu.duanzhi.utils.NetWorkUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.lansosdk.videoeditor.MediaInfo;
import com.lansosdk.videoeditor.VideoEditor;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.io.File;

public class VideoDownloadHelper {
    private static final VideoDownloadHelper ourInstance = new VideoDownloadHelper();
    //静态变量才能保证不随fragment的销毁而制空
    public static String downLoadVideoUrl;
    public static boolean isDownLoad = false;


    public static VideoDownloadHelper getInstance() {
        return ourInstance;
    }

    private VideoDownloadHelper() {
    }


    public void startDownLoad(boolean isVideo, String contentId, String url) {
        if (!checkPermission()) return;
        if (isVideo) {
            downLoadVideo(contentId, url);
        } else {
            downloadPicture(url);
        }
    }

    /**
     * 下载图片
     *
     * @param url
     */
    private void downloadPicture(String url) {
        //这里文件格式
        String name = System.currentTimeMillis() + ".png";
        OkGo.<File>get(url)
                .execute(new FileCallback(PathConfig.LOCALFILE, name) {

                    @Override
                    public void onSuccess(Response<File> response) {

                        File body = response.body();
                        ToastUtil.showShort("图片下载成功,请去相册查看");

                        MyApplication.getInstance().getRunningActivity()
                                .sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                        Uri.fromFile(body)));
                    }

                    @Override
                    public void onError(Response<File> response) {
                        ToastUtil.showShort("下载失败");

                        super.onError(response);
                    }
                });
    }

    private boolean checkPermission() {
        boolean has = false;
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        if (runningActivity == null) return false;
        if (ContextCompat.checkSelfPermission(runningActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(runningActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // 拒绝权限
                ToastUtil.showShort("您拒绝了存储权限，下载失败！");
            } else {
                //申请权限
                ActivityCompat.requestPermissions(runningActivity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1);
            }
        } else {
            has = true;
        }
        return has;
    }

    private void downLoadVideo(String contentId, String VideoUrl) {
        Activity activity = MyApplication.getInstance().getRunningActivity();
        if (activity == null) return;
        //过滤多次下载点击
        if (isDownLoad) {
            if (TextUtils.equals(downLoadVideoUrl, VideoUrl)) {
                ToastUtil.showShort("在下载哦，请耐心等待一下～");
            } else if (!TextUtils.equals(downLoadVideoUrl, VideoUrl)) {
                ToastUtil.showShort("已有正在下载的视频哦～");
            }
            return;
        }
        checkNetwork(activity, contentId, VideoUrl);

    }

    private void httpDownLoad(String contentId, String VideoUrl) {
        if (TextUtils.isEmpty(VideoUrl)) return;
        isDownLoad = true;
        downLoadVideoUrl = VideoUrl;

        // TODO: 2019/3/14 视频下载统计
        CommonHttpRequest.getInstance().requestDownLoad(contentId, CommonHttpRequest.AppType.download_video);
        int lastIndexOf = VideoUrl.lastIndexOf(".");
        String end = VideoUrl.substring(lastIndexOf);
        String fileName = "duanzi-" + System.currentTimeMillis() + end;

        String downloadUrl = VideoUrl.substring(0, lastIndexOf) + ".logo" + end;
        OkGo.<File>get(downloadUrl)
                .execute(new FileCallback(PathConfig.VIDEO_PATH, fileName) {
                    @Override
                    public void onStart(Request<File, ? extends Request> request) {
                        ToastUtil.showShort("正在下载中");
                        super.onStart(request);
                    }

                    @Override
                    public void onSuccess(Response<File> response) {
                        File body = response.body();
                        dealVideo(body);
                    }

                    @Override
                    public void onError(Response<File> response) {
                        downLoadNormalVideo(VideoUrl, fileName);
                        super.onError(response);
                    }
                });
    }

    private void downLoadNormalVideo(String videoUrl, String fileName) {

        OkGo.<File>get(videoUrl)
                .execute(new FileCallback(PathConfig.VIDEO_PATH, fileName) {

                    @Override
                    public void onSuccess(Response<File> response) {
                        File body = response.body();
                        dealVideo(body);
                    }

                    @Override
                    public void onError(Response<File> response) {
                        ToastUtil.showShort("下载失败");
                        isDownLoad = false;
                        super.onError(response);
                    }
                });
    }

    private void dealVideo(File body) {
        // TODO: 2019/3/13 需要加片头片尾      TsToMp4文件名的特殊字段,重命名就没办法了
        // 这个视频拼接基本不需要监听,速度很快
        if (body == null) return;
        String waterPath = PathConfig.getAbsoluteVideoByWaterPath(0);
        String waterPath1 = PathConfig.getAbsoluteVideoByWaterPath(1);
        if (!new File(waterPath).exists() || !new File(waterPath1).exists()) {
            Log.i("fileService", "文件不存在,视频片尾未处理完成");
            ToastUtil.showShort("保存成功: DCIM/duanzi");
            isDownLoad = false;
            return;
        }
        if (VideoFileReadyServices.isDealVideoEnd) {
            Log.i("fileService", "视频片尾还在处理中");
            ToastUtil.showShort("保存成功: DCIM/duanzi");
            isDownLoad = false;
            return;
        }
        //当宽高信息拿不到时直接返回原来视频连接
        MediaInfo info = new MediaInfo(body.getAbsolutePath());
        if (!info.prepare()) {
            Log.i("fileService", "mediaInfo未准备好,so加载异常");
            ToastUtil.showShort("保存成功: DCIM/duanzi");
            noticeSystemCamera(body);
            isDownLoad = false;
            return;
        }
        VideoEditor mEditor = new VideoEditor();
        //大于两分钟静态水印 + 片头   2分钟以内（包含2分钟）：静态水印 + 片尾
        if (info.vRotateAngle > 0) {
            // TODO: 2019/3/15 如果是有视频旋转角度的先调整,但是不会调整视频宽高,因为这里会和
            new Thread(() -> {
                Log.i("fileService", "需要先处理视频旋转角度问题");
                String srcPath = mEditor.executeRotateAngle(body.getAbsolutePath(), 0);
                if (TextUtils.isEmpty(srcPath) || !new File(srcPath).exists()) {
                    ToastUtil.showShort("保存成功: DCIM/duanzi");
                    isDownLoad = false;
                } else {
                    concatVideo(new File(srcPath), waterPath, waterPath1, info, mEditor, srcPath);
                }
                // TODO: 2019/3/18 内部本省就是在异步线程虹执行,所以需要跑回到主线程

//                body.delete();
//                    MyApplication.getInstance().getHandler().post(new Runnable() {
//                        @Override
//                        public void run() {
//
//                        }
//                    });

            }).start();
        } else {
            concatVideo(body, waterPath, waterPath1, info, mEditor, body.getAbsolutePath());
        }
    }

    private void concatVideo(File body, String waterPath, String waterPath1, MediaInfo info, VideoEditor mEditor, String srcPath) {
        String videoDealPath;
        if (info.getHeight() > info.getWidth() + 100) {
            //竖视频
            if (info.vDuration > 2 * 60) {
                videoDealPath = mEditor.executeConcatMP4(new String[]{waterPath1, srcPath});
            } else {
                videoDealPath = mEditor.executeConcatMP4(new String[]{srcPath, waterPath1});
            }
        } else {
            //横视频
            if (info.vDuration > 2 * 60) {
                videoDealPath = mEditor.executeConcatMP4(new String[]{waterPath, srcPath});
            } else {
                videoDealPath = mEditor.executeConcatMP4(new String[]{srcPath, waterPath});
            }
        }
//        MediaInfo info1 = new MediaInfo(videoDealPath);
//        if (info1.prepare()) {
//            Log.i("videoInfo", "拼接后的视频信息" + info1.toString());
//        }
        if (!TextUtils.isEmpty(videoDealPath)) {
            Log.i("fileService", "视频片尾拼接成功");
            noticeSystemCamera(new File(videoDealPath));
            body.delete();
        } else {
            Log.i("fileService", "片尾处理失败");
            noticeSystemCamera(body);
            isDownLoad = false;
            return;
        }
        isDownLoad = false;
        ToastUtil.showShort("保存成功: " + videoDealPath);
    }

    /**
     * 视频下载提醒弹窗
     *
     * @param context
     * @param contentId
     * @param videoUrl
     */
    public void checkNetwork(Activity context, String contentId, String videoUrl) {
        if (!NetWorkUtils.isNetworkConnected(context)) {
            ToastUtil.showShort(R.string.video_no_network);
            return;
        }
        if (!NetWorkUtils.isWifiConnected(context)) {
            new AlertDialog.Builder(context)
                    .setMessage("你正在使用移动数据网络，是否继续下载视频？")
                    .setPositiveButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .setNegativeButton("土豪随意", (dialog, which) -> {
                        httpDownLoad(contentId, videoUrl);
                        dialog.dismiss();
                    }).show();
        } else {
            httpDownLoad(contentId, videoUrl);
        }
    }

    private void noticeSystemCamera(File file) {
        ContentResolver localContentResolver = MyApplication.getInstance().getContentResolver();
        //ContentValues：用于储存一些基本类型的键值对
        ContentValues localContentValues = getVideoContentValues(file, System.currentTimeMillis());
        //insert语句负责插入一条新的纪录，如果插入成功则会返回这条记录的id，如果插入失败会返回-1。
        Uri localUri = localContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localContentValues);

        MyApplication.getInstance().getRunningActivity().
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        localUri));

        MyApplication.getInstance().getRunningActivity()
                .sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(file)));
    }

    /**
     * 视频存在本地
     *
     * @param paramFile
     * @param paramLong
     * @return
     */
    private static ContentValues getVideoContentValues(File paramFile, long paramLong) {
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
}
