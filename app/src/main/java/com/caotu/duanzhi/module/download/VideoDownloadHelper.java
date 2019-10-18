package com.caotu.duanzhi.module.download;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.PathConfig;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.ImageMarkUtil;
import com.caotu.duanzhi.utils.NetWorkUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.lansosdk.videoeditor.MediaInfo;
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
            downloadPicture(url, false);
        }
    }

    public void startDownLoad(boolean isVideo, String contentId, String url, boolean isImageWater) {
        if (!checkPermission()) return;
        if (isVideo) {
            downLoadVideo(contentId, url);
        } else {
            downloadPicture(url, isImageWater);
        }
    }

    /**
     * 下载图片
     *
     * @param url
     */
    private void downloadPicture(String url, boolean isNeedImageWater) {
        Glide.with(MyApplication.getInstance()).downloadOnly().load(url).into(new CustomTarget<File>() {
            @Override
            public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                String path = PathConfig.LOCALFILE;
                String name = String.valueOf(System.currentTimeMillis());

                String mimeType = GlideUtils.getImageTypeWithMime(resource.getAbsolutePath());
                name = name + "." + mimeType;
                if (isNeedImageWater) {
                    // TODO: 2019/4/3 这里处理了 decodeFile 的空指针问题,图片下载有问题,暂时这么解决
                    Bitmap decodeFile = BitmapFactory.decodeFile(resource.getAbsolutePath());
                    String saveImage = VideoAndFileUtils.saveImage(ImageMarkUtil.WaterMask(decodeFile));
                    if (!TextUtils.isEmpty(saveImage)) {
                        ToastUtil.showShort("图片下载成功,请去相册查看");

                        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
                        if (runningActivity == null) return;
                        runningActivity
                                .sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                        Uri.fromFile(new File(saveImage))));
                    } else {
                        ToastUtil.showShort("保存失败");
                    }
                } else {
                    boolean result = LanSongFileUtil.copyFile(resource, path, name);
                    if (result) {
                        ToastUtil.showShort("图片下载成功,请去相册查看");

                        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
                        if (runningActivity == null) return;
                        runningActivity
                                .sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                        Uri.fromFile(new File(path.concat(name)))));
                    } else {
                        ToastUtil.showShort("保存失败");
                    }
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                ToastUtil.showShort("开始下载...");

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                ToastUtil.showShort("保存失败");
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
        UmengHelper.event(UmengStatisticsKeyIds.my_download_video);
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
        if (body == null || !body.exists()) return;
        //当宽高信息拿不到时直接返回原来视频连接,因为不好处理视频压缩,加水印的宽高位置信息也拿不到
        MediaInfo info = new MediaInfo(body.getAbsolutePath());
        if (!info.prepare()) {
            Log.i("fileService", "mediaInfo未准备好,so加载异常");
            ToastUtil.showShort("保存成功: DCIM/duanzi");
            noticeSystemCamera(body);
            isDownLoad = false;
            return;
        }
        //如果视频是有角度的也是直接返回原视频,拼接会有问题
        if (info.vRotateAngle > 0) {
            ToastUtil.showShort("保存成功: DCIM/duanzi");
            noticeSystemCamera(body);
            isDownLoad = false;
            return;
        }
        //大于两分钟静态水印 + 片头   2分钟以内（包含2分钟）：静态水印 + 片尾
        // TODO: 2019-10-18 放到服务里搞
        HelperForStartActivity.startVideoService(body);
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

    public static void noticeSystemCamera(File file) {
        ContentResolver localContentResolver = MyApplication.getInstance().getContentResolver();
        //ContentValues：用于储存一些基本类型的键值对
        ContentValues localContentValues = getVideoContentValues(file, System.currentTimeMillis());
        //insert语句负责插入一条新的纪录，如果插入成功则会返回这条记录的id，如果插入失败会返回-1。
        Uri localUri = localContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localContentValues);

        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        if (runningActivity == null) return;
        runningActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                localUri));

        runningActivity
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
