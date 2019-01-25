package com.caotu.duanzhi.view.dialog;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.PathConfig;
import com.caotu.duanzhi.module.login.LoginAndRegisterActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.lansosdk.VideoFunctions;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.lansosdk.videoeditor.VideoEditor;
import com.lansosdk.videoeditor.onVideoEditorProgressListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Response;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;

/**
 * @author mac
 * @日期: 2018/11/2
 * @describe 分享弹窗
 * 分享弹窗逻辑是:先在外部判断好是否显示收藏和视频下载按钮的显示,弹窗内部只处理赋值分享的平台,真正唤起三方分享在sharehelp里实现
 */
public class ShareDialog extends BaseDialogFragment implements View.OnClickListener {

    /**
     * 保存至相册
     */
    private TextView mShareDownloadVideo;
    //分享内容的对象
    private WebShareBean bean;
    //静态变量才能保证不随fragment的销毁而制空
    public static String downLoadVideoUrl;
    public static boolean isDownLoad = false;

    public static ShareDialog newInstance(WebShareBean bean) {
        final ShareDialog fragment = new ShareDialog();
        final Bundle args = new Bundle();
        args.putParcelable("bean", bean);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bean = getArguments().getParcelable("bean");
        }
    }

    @Override
    public int getLayout() {
        return R.layout.layout_share_dialog;
    }

    @Override
    protected void initView(View inflate) {
        inflate.findViewById(R.id.share_weixin).setOnClickListener(this);
        inflate.findViewById(R.id.share_friend).setOnClickListener(this);
        inflate.findViewById(R.id.share_qq).setOnClickListener(this);
        inflate.findViewById(R.id.share_qq_space).setOnClickListener(this);
        inflate.findViewById(R.id.share_weibo).setOnClickListener(this);
        inflate.findViewById(R.id.tv_click_cancel).setOnClickListener(this);

        TextView mShareCollection = (TextView) inflate.findViewById(R.id.share_collection);
        mShareCollection.setOnClickListener(this);
        mShareDownloadVideo = (TextView) inflate.findViewById(R.id.share_download_video);
        mShareDownloadVideo.setOnClickListener(this);

        //设置背景透明，才能显示出layout中诸如圆角的布局，否则会有白色底（框）
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.TransparentBottomSheetStyle);
        //只有内容列表和内容详情的分享视频才有下载
        mShareDownloadVideo.setVisibility(bean == null || !bean.isVideo
                || TextUtils.isEmpty(bean.VideoUrl)
                ? View.GONE : View.VISIBLE);

        if (bean != null && bean.webType == 1) {
            mShareDownloadVideo.setVisibility(View.VISIBLE);
            mShareDownloadVideo.setText("保存图片");
        }
        //只有内容列表才有这个展示
        mShareCollection.setVisibility(bean == null || !bean.isNeedShowCollection
                || TextUtils.isEmpty(bean.contentId)
                ? View.GONE : View.VISIBLE);
        mShareCollection.setText(bean.hasColloection ? "取消收藏" : "收藏");
        if (mShareDownloadVideo.getVisibility() == View.GONE && mShareCollection.getVisibility() == View.GONE) {
            inflate.findViewById(R.id.space).setLayoutParams(new LinearLayout.LayoutParams(0, 1, 3));
        } else if (mShareDownloadVideo.getVisibility() == View.GONE || mShareCollection.getVisibility() == View.GONE) {
            inflate.findViewById(R.id.space).setLayoutParams(new LinearLayout.LayoutParams(0, 1, 2));
        }
    }

    @Override
    public void onClick(View v) {
        Activity activity = MyApplication.getInstance().getRunningActivity();
        switch (v.getId()) {
            case R.id.share_weixin:
                bean.medial = SHARE_MEDIA.WEIXIN;
                if (listener != null) {
                    listener.callback(bean);
                }
                break;
            case R.id.share_friend:
                bean.medial = SHARE_MEDIA.WEIXIN_CIRCLE;
                if (listener != null) {
                    listener.callback(bean);
                }
                break;
            case R.id.share_qq:
                bean.medial = SHARE_MEDIA.QQ;
                if (listener != null) {
                    listener.callback(bean);
                }
                break;
            case R.id.share_qq_space:
                bean.medial = SHARE_MEDIA.QZONE;
                if (listener != null) {
                    listener.callback(bean);
                }
                break;
            case R.id.share_weibo:
                bean.medial = SHARE_MEDIA.SINA;
                if (listener != null) {
                    listener.callback(bean);
                }
                break;
            case R.id.share_collection:
                if (!MySpUtils.getBoolean(MySpUtils.SP_ISLOGIN, false)) {
                    Intent intent = new Intent();
                    intent.setClass(activity, LoginAndRegisterActivity.class);
                    activity.startActivityForResult(intent, LoginAndRegisterActivity.LOGIN_REQUEST_CODE);
                    return;
                }
                if (!TextUtils.isEmpty(bean.contentId)) {
                    CommonHttpRequest.getInstance().collectionContent(bean.contentId, !bean.hasColloection, new JsonCallback<BaseResponseBean<String>>() {
                        @Override
                        public void onSuccess(Response<BaseResponseBean<String>> response) {
                            if (listener != null) {
                                listener.colloection(!bean.hasColloection);
                            }
                        }
                    });
                }
                break;
            case R.id.share_download_video:
                if (bean == null) return;
                if (bean.webType == 1 && !TextUtils.isEmpty(bean.url)) {
                    startDownloadImage(activity);
                } else {
                    downLoadVideo(activity);
                }
                break;
//            case R.id.tv_click_cancel:
//                break;
        }
        dismiss();
    }


    private void startDownloadImage(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // 拒绝权限
                ToastUtil.showShort("您拒绝了存储权限，下载失败！");
            } else {
                //申请权限
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1);
            }
        } else {
            // 下载当前图片
            mShareDownloadVideo.setEnabled(false);
            downloadPicture(bean.url);
        }
    }

    /**
     * 下载图片
     *
     * @param url
     */
    public void downloadPicture(String url) {
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

    private void downLoadVideo(Activity activity) {
        //过滤多次下载点击
        if (isDownLoad) {
            if (TextUtils.equals(downLoadVideoUrl, bean.VideoUrl)) {
                ToastUtil.showShort("在下载哦，请耐心等待一下～");
            } else if (!TextUtils.equals(downLoadVideoUrl, bean.VideoUrl)) {
                ToastUtil.showShort("已有正在下载的视频哦～");
            }
            dismiss();
            return;
        }
        //处理视频下载一块
        VideoAndFileUtils.checkNetwork(activity, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Activity.RESULT_OK == which) {
                    if (TextUtils.isEmpty(bean.VideoUrl)) return;
                    isDownLoad = true;
                    downLoadVideoUrl = bean.VideoUrl;
                    // TODO: 2018/11/28 这块是开启服务的形式
//                            Intent intent = new Intent(MyApplication.getInstance().getRunningActivity(), WaterMarkServices.class);
//                            intent.putExtra(WaterMarkServices.KEY_URL, bean.VideoUrl);
//                            MyApplication.getInstance().getRunningActivity().startService(intent);
                    mShareDownloadVideo.setEnabled(false);
                    CommonHttpRequest.getInstance().requestDownLoad(bean.contentId);
                    ToastUtil.showShort("正在下载中");
                    String end = bean.VideoUrl.substring(bean.VideoUrl.lastIndexOf("."),
                            bean.VideoUrl.length());
                    String fileName = "duanzi-" + System.currentTimeMillis() + end;
                    // TODO: 2018/11/28 用service 处理
                    OkGo.<File>get(bean.VideoUrl)
                            .execute(new FileCallback(PathConfig.VIDEO_PATH, fileName) {
                                @Override
                                public void onSuccess(Response<File> response) {
                                    downLoadVideoFile = response.body();
//                                            Log.i(TAG, "下载完成开始加水印");
                                    addWaterMark();
                                    mShareDownloadVideo.setEnabled(true);
                                }

                                @Override
                                public void onError(Response<File> response) {
                                    ToastUtil.showShort("下载失败");
                                    isDownLoad = false;
                                    mShareDownloadVideo.setEnabled(true);
                                    super.onError(response);
                                }
                            });
                }
            }
        });
    }

    File downLoadVideoFile;

    private void addWaterMark() {
        if (downLoadVideoFile == null) {
            ToastUtil.showShort("下载失败");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                //每次都是新的回调,这样回调才不会乱
                VideoEditor mEditor = new VideoEditor();
                mEditor.setOnProgessListener(new onVideoEditorProgressListener() {
                    @Override
                    public void onProgress(VideoEditor v, int percent) {
//                        Log.i(TAG, "加水印进度 onProgress: " + String.valueOf(percent) + "%");
                        if (percent == 100) {
                            //删除原先的
                            LanSongFileUtil.deleteDir(downLoadVideoFile);
                            LanSongFileUtil.deleteDir(new File(LanSongFileUtil.TMP_DIR));

                        }
                    }
                });

                String waterFilePath = VideoFunctions.demoAddPicture(MyApplication.getInstance(), mEditor, downLoadVideoFile.getAbsolutePath());
//                Log.i(TAG, "水印加载完成: " + waterFilePath);
                ToastUtil.showShort("保存成功:" + "DCIM/duanzi");
                isDownLoad = false;
                //删除原先的
                //通知系统相册更新
                if (TextUtils.isEmpty(waterFilePath)) return;
                File file1 = new File(waterFilePath);
                //获取ContentResolve对象，来操作插入视频
                ContentResolver localContentResolver = MyApplication.getInstance().getContentResolver();
                //ContentValues：用于储存一些基本类型的键值对
                ContentValues localContentValues = getVideoContentValues(file1, System.currentTimeMillis());
                //insert语句负责插入一条新的纪录，如果插入成功则会返回这条记录的id，如果插入失败会返回-1。
                Uri localUri = localContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localContentValues);

                Activity runningActivity = MyApplication.getInstance().getRunningActivity();
                if (runningActivity == null) return;
                runningActivity.
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                localUri));

                runningActivity
                        .sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                Uri.fromFile(new File(waterFilePath))));
            }
        }).start();

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

    public ShareMediaCallBack listener;

    /**
     * 必须调用回调方法
     *
     * @param listener
     */
    public void setListener(ShareMediaCallBack listener) {
        this.listener = listener;
    }

    public interface ShareMediaCallBack {
        void callback(WebShareBean bean);

        void colloection(boolean isCollection);
    }

    public abstract static class SimperMediaCallBack implements ShareMediaCallBack {

        @Override
        public void colloection(boolean isCollection) {

        }
    }

}
