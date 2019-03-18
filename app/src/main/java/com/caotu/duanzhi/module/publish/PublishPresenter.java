package com.caotu.duanzhi.module.publish;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.PublishResponseBean;
import com.caotu.duanzhi.Http.tecentupload.UploadServiceTask;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusCode;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.config.HttpCode;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ThreadExecutor;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.BindPhoneDialog;
import com.lansosdk.VideoFunctions;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.lansosdk.videoeditor.MediaInfo;
import com.lansosdk.videoeditor.VideoEditor;
import com.lansosdk.videoeditor.onVideoEditorProgressListener;
import com.luck.picture.lib.PictureSelectionModel;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mac
 * @日期: 2018/11/5
 * @describe 由于有多处用到发布内容的.抽取共用逻辑
 */
public class PublishPresenter {
    public IVewPublish IView;
    //选择的媒体数据集
    public List<LocalMedia> selectList;
    //上传给接口的视频和图片的链接地址
    public List<String> uploadTxFiles = new ArrayList<>();
    //选择的话题
    private String topicId;
    //视频时长
    public String videoDuration;
    //发表的内容
    public String content;
    //视频和宽高的数据
    public String mWidthAndHeight = "";
    //发表内容的类型  内容类型: 1横 2竖 3图片 4文字
    public String publishType = ""; //设置默认值为文字类型

    public static final String fileTypeImage = ".jpg";
    public static final String fileTypeVideo = ".mp4";
    public static final String fileTypeGif = ".gif";
    private String topicName;
    //该字段用来判断视频封面是否是自己生成的图片,而不是直接从系统那边拿的
    public String videoCover;

    public PublishPresenter(IVewPublish context) {
        IView = context;
    }

    public void destory() {
        IView = null;
    }

    public void clearSelectList() {
        if (selectList != null) {
            selectList.clear();
        }
        if (uploadTxFiles != null) {
            uploadTxFiles.clear();
        }
        topicId = null;
        videoDuration = null;
        content = null;
        mWidthAndHeight = "";
        publishType = "";
        topicName = null;
        videoCover = null;
    }

    /**
     * 发布视频和图片的接口请求
     */
    public void requestPublish() {
        if (isVideo && uploadTxFiles.size() < 2) {
            return;
        }
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("contenttag", topicId);//标签id
        //单张图和视频的时候传
        map.put("contenttext", mWidthAndHeight);//宽，高
        map.put("contenttitle", content);//标题
        String replaceUrl = "";
        if (uploadTxFiles != null && !uploadTxFiles.isEmpty()) {
            String contentUrl = new JSONArray(uploadTxFiles).toString();
            replaceUrl = contentUrl.replace("\\", "");
            map.put("contenturllist", replaceUrl);//内容连接
        }
        map.put("contentype", publishType);//内容类型 1横 2竖 3图片 4文字
        map.put("showtime", videoDuration);

        OkGo.<BaseResponseBean<PublishResponseBean>>post(HttpApi.WORKSHOW_PUBLISH)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<PublishResponseBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<PublishResponseBean>> response) {
                        if (HttpCode.cant_talk.equals(response.body().getCode())) {
//                            ToastUtil.showShort(response.body().getMessage());
                            EventBusHelp.sendPublishEvent(EventBusCode.pb_cant_talk, response.body().getMessage());
                            return;
                        }
                        PublishResponseBean data = response.body().getData();
                        //直接用id查接口获取列表bean对象,省的自己封装
                        getMomentDateBeanById(data.getContentid());

                        //包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统sd卡权限
                        PictureFileUtils.deleteCacheDirFile(MyApplication.getInstance());
                        LanSongFileUtil.deleteDir(new File(LanSongFileUtil.TMP_DIR));
                        if (!TextUtils.isEmpty(videoCover)) {
                            LanSongFileUtil.deleteFile(videoCover);
                        }
                        clearSelectList();
                        //之前保存的发布内容也得清空
                        MySpUtils.clearPublishContent();
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<PublishResponseBean>> response) {
                        ToastUtil.showShort("发布失败！");
                        if (!TextUtils.isEmpty(videoCover)) {
                            LanSongFileUtil.deleteFile(videoCover);
                        }
                        EventBusHelp.sendPublishEvent(EventBusCode.pb_error, null);
                        clearSelectList();
                        MySpUtils.clearPublishContent();
                        super.onError(response);
                    }
                });
    }

    private void getMomentDateBeanById(String contentid) {
        //用于通知跳转
        HashMap<String, String> hashMapParams = new HashMap<>();
        hashMapParams.put("contentid", contentid);
        OkGo.<BaseResponseBean<MomentsDataBean>>post(HttpApi.WORKSHOW_DETAILS)
                .upJson(new JSONObject(hashMapParams))
                .execute(new JsonCallback<BaseResponseBean<MomentsDataBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<MomentsDataBean>> response) {
                        // TODO: 2018/11/7 还需要封装成首页列表展示的bean对象
                        MomentsDataBean data = response.body().getData();
                        EventBusHelp.sendPublishEvent(EventBusCode.pb_success, data);
                    }
                });

    }

    public void getPicture() {
        PictureSelectionModel model = PictureSelector.create(getCurrentActivty())
                .openGallery(PictureMimeType.ofImage());//图片，视频，音频，全部
        if (DevicesUtils.isOppo()) {
            model.theme(R.style.picture_default_style);
        } else {
            model.theme(R.style.picture_QQ_style);
        }
        model
                .maxSelectNum(9)
                .minSelectNum(1)
                .selectionMode(PictureConfig.MULTIPLE)//单选或多选
                .previewImage(true)//是否可预览图片 true or false
                // .compressGrade(Luban.THIRD_GEAR)
                .isCamera(true)
                .compress(true)
                .imageSpanCount(3)
                //.compressMode(PictureConfig.LUBAN_COMPRESS_MODE)
                .glideOverride(160, 160)
                .previewEggs(true)
                .isGif(true)//gif支持
                .selectionMedia(selectList)
                .forResult(PictureConfig.REQUEST_PICTURE);
    }


    public void getVideo() {
        PictureSelectionModel model = PictureSelector.create(getCurrentActivty())
                .openGallery(PictureMimeType.ofVideo());
        if (DevicesUtils.isOppo()) {
            model.theme(R.style.picture_default_style);
        } else {
            model.theme(R.style.picture_QQ_style);
        }
        model//图片，视频，音频，全部
                .maxSelectNum(1)
                .minSelectNum(1)
                .imageSpanCount(3)
                .selectionMode(PictureConfig.SINGLE)//单选或多选
                .previewVideo(true)
                //.compressGrade(Luban.THIRD_GEAR)
                .isCamera(true)
                .compress(true)
                //.compressMode(PictureConfig.LUBAN_COMPRESS_MODE)
                .glideOverride(160, 160)
                .isGif(true)//gif支持
                .videoQuality(0)
//                .videoMinSecond(1)
                .videoMaxSecond(60 * 60)
                .recordVideoSecond(4 * 60 + 59)//录制最大时间 后面判断不能超过5分钟 是否要改成4分59秒
//                .selectionMedia(videoList)
                .forResult(PictureConfig.REQUEST_VIDEO);
    }

    public void publishBtClick() {
        //  第一层是绑定手机
        if (!MySpUtils.getBoolean(MySpUtils.SP_HAS_BIND_PHONE, false)) {
            new BindPhoneDialog(getCurrentActivty()).show();
            return;
        }
        //  校验敏感词
        String editContent = IView.getEditView().getText().toString().trim();
//        if (!TextUtils.isEmpty(editContent)) {
//            checkPublishWord(editContent);
//            return;
//        }

        if (TextUtils.isEmpty(editContent) && (selectList == null || selectList.size() == 0)) {
            ToastUtil.showShort("请先选择发表内容");
            return;
        }
        uploadFile();
    }

    /**
     * 校验敏感词
     */

//    private void checkPublishWord(String editContent) {
//        if (!IView.getPublishView().isEnabled()) {
//            ToastUtil.showShort("正在发布,请勿重复点击");
//            return;
//        }
//        IView.getPublishView().setEnabled(false);
//
//        HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
//        params.put("checkword", editContent);
//        OkGo.<BaseResponseBean<String>>post(HttpApi.WORKSHOW_VERIFY)
//                .upJson(new JSONObject(params))
//                .execute(new JsonCallback<BaseResponseBean<String>>() {
//                    @Override
//                    public void onSuccess(Response<BaseResponseBean<String>> response) {
//                        String body = response.body().getData();
//                        if (!"Y".equals(body)) {
//                            if (IView != null) {
//                                IView.getEditView().setText(body);
//                            }
//                            uploadFile();
//                        } else {
//                            if (IView != null) {
//                                IView.getPublishView().setEnabled(true);
//                            }
//                            ToastUtil.showShort("碰到敏感词啦，改一下呗");
//                        }
//                    }
//
//                    @Override
//                    public void onError(Response<BaseResponseBean<String>> response) {
//                        if (IView != null) {
//                            IView.getPublishView().setEnabled(true);
//                        }
//                        ToastUtil.showShort("校验敏感词失败");
////                        ToastUtil.showShort("网络质量不好，到空旷宽敞的地方再试试");
//                        super.onError(response);
//                    }
//                });
//    }
    //    内容类型  1横  2竖  3图片   4纯文字
    public boolean isVideo = false;

    public void uploadFile() {
        content = IView.getEditView().getText().toString().trim();

        if (selectList == null || selectList.size() == 0) {
            //纯文字
            publishType = "4";
            if (shouldCheckLength()) {
                return;
            }
            isVideo = false;
            if (IView != null) {
                IView.startPublish();
            }
            requestPublish();
        } else if (selectList.size() == 1 && isVideo) {

            //这个是视频,除了要获取是横竖视频,还要获取视频时长,视频封面,视频压缩
            // 获取视频时长
            LocalMedia media = selectList.get(0);
            //保存的是long类型的秒值
            long duration = media.getDuration();
            if (duration < 5000) {
                ToastUtil.showShort(" 这条视频时间太短了哟~（＜5s）");
                //重新放开view的点击事件
                IView.getPublishView().setEnabled(true);
                return;
            } else if (duration > 5 * 60 * 1000) {
                ToastUtil.showShort("这条视频时间太长了哟~（＞5min)");
                //重新放开view的点击事件
                IView.getPublishView().setEnabled(true);
                return;
            }
            String path = media.getPath();
            if (!path.endsWith(".mp4") && !path.endsWith(".MP4")) {
                // TODO: 2019/2/27  先压缩转码
                if (IView != null) {
                    IView.notMp4();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("fileService", "发布先处理视频转码问题");
                        String videoPath = startRunFunction(path);
                        if (TextUtils.isEmpty(videoPath)) {
                            ToastUtil.showShort("转码失败");
                            return;
                        }

                        if (IView != null) {
                            IView.getPublishView().post(new Runnable() {
                                @Override
                                public void run() {
                                    startVideoUpload(media, videoPath);
                                }
                            });
                        }
                    }
                }).start();
            } else {
                startVideoUpload(media, path);
            }

        } else {
            if (IView != null) {
                IView.startPublish();
            }
            //图片处理
            publishType = "3";
            if (selectList != null && selectList.size() == 1) {
                LocalMedia localMedia = selectList.get(0);
                mWidthAndHeight = localMedia.getWidth() + "," + localMedia.getHeight();
            }

            for (int i = 0; i < selectList.size(); i++) {
                String sourcePath = selectList.get(i).getPath();
                String path = selectList.get(i).getCompressPath();
                String fileType;
                if (sourcePath.endsWith(".gif") || sourcePath.endsWith(".GIF")) {
                    path = sourcePath;
                    fileType = fileTypeGif;
                } else {
                    if (TextUtils.isEmpty(path)) {
                        path = selectList.get(i).getPath();
                    }
                    fileType = fileTypeImage;
                }
                updateToTencent(fileType, path, false);
            }
        }
    }

    private void startVideoUpload(LocalMedia media, String path) {
        long duration = media.getDuration();
        if (IView != null) {
            IView.startPublish();
        }
        // TODO: 2018/12/24 保险起见type为空的情况
        publishType = "1";
        videoDuration = String.valueOf(duration / 1000);

        uploadVideo(path, media);
    }


    private void uploadVideo(String filePash, LocalMedia media) {
        String saveImage;
        //框架自带已经解决视频封面.应该不需要自己再去获取视频封面
        if (!TextUtils.isEmpty(media.getVideoImagePath())) {
            saveImage = media.getVideoImagePath();
        } else {
            Bitmap videoThumbnail = VideoEditor.getVideoThumbnailAndSave(filePash);
            saveImage = VideoAndFileUtils.saveImage(videoThumbnail);
            videoCover = saveImage;
        }
        // TODO: 2018/11/7 获取压缩后的视频的宽高以及是否是竖视频的判断
        String[] widthAndHeight = new String[3];
        if (media.getWidth() > 0 && media.getHeight() > 0) {
            widthAndHeight[0] = media.getWidth() + "";
            widthAndHeight[1] = media.getHeight() + "";
            widthAndHeight[2] = media.getHeight() > media.getWidth() ? "yes" : "no";
        } else {
            widthAndHeight = VideoFunctions.getWidthAndHeight(filePash);
        }
        // TODO: 2019/3/15 用这个判断就是因为有些看着画面是竖视频,但是宽高信息是反着的情况
        MediaInfo info = new MediaInfo(media.getPath());
        if (info.prepare()) {
            if (info.isPortVideo()) {
                publishType = "2";
            }
        } else {
            //1横 2竖 3图片 4文字
            publishType = TextUtils.equals("yes", widthAndHeight[2]) ? "2" : "1";
        }
        mWidthAndHeight = widthAndHeight[0] + "," + widthAndHeight[1];
        //第一个是视频封面,第二个是视频
        updateToTencent(fileTypeImage, saveImage, true);
        //filePash.substring(filePash.lastIndexOf(".")
        updateToTencent(fileTypeVideo, filePash, true);
    }

    /**
     * 是否限制发布长度
     *
     * @return
     */
    protected boolean shouldCheckLength() {
        if (TextUtils.isEmpty(content) || content.length() < 5) {
            ToastUtil.showShort("亲，还可以再写一些字");
            return true;
        } else {
            return false;
        }
    }

    public void updateToTencent(String fileType, String filePash, boolean isVideo) {
        ThreadExecutor.getInstance().executor(new Runnable() {
            @Override
            public void run() {
                UploadServiceTask.upLoadFile(fileType, filePash, new UploadServiceTask.OnUpLoadListener() {
                    @Override
                    public void onUpLoad(long progress, long max) {
                        int uploadSize = selectList.size();
                        if (isVideo) {
                            uploadSize = 2;
                        }
                        int barProgress = (int) ((100.0f / uploadSize)
                                * (progress * 1.0f / max + uploadTxFiles.size()));
                        EventBusHelp.sendPublishEvent(EventBusCode.pb_progress, barProgress);
                        // TODO: 2019/3/18 需要的话可以传进度出去
                        uploadProgress(barProgress);
                    }

                    @Override
                    public void onLoadSuccess(String url) {
                        String realUrl = "https://" + url;

                        if (isVideo) {
                            //为了保险起见,封面图放第一位
                            if (isImageType(realUrl)) {
                                uploadTxFiles.add(0, realUrl);
                            } else {
                                uploadTxFiles.add(realUrl);
                            }

                            if (uploadTxFiles.size() == 2) {
                                requestPublish();
                            }
                        } else {
                            uploadTxFiles.add(realUrl);
                            if (uploadTxFiles.size() == selectList.size()) {
                                requestPublish();
                            }
                        }
                    }

                    @Override
                    public void onLoadError(String exception) {
                        // TODO: 2018/11/7 视频压缩不会失败,只有上传有error回调
                        EventBusHelp.sendPublishEvent(EventBusCode.pb_error, null);
                        ToastUtil.showShort("上传失败:" + exception);
                    }
                });
            }
        });

    }

    /**
     * 供子类复写接收上传进度
     *
     * @param barProgress
     */
    public void uploadProgress(int barProgress) {

    }

    /**
     * 视频操作
     *
     * @return
     */
    private String startRunFunction(String videoUrl) {

        VideoEditor editor = new VideoEditor();
        editor.setOnProgessListener(new onVideoEditorProgressListener() {
            @Override
            public void onProgress(VideoEditor v, int percent) {
                Log.i("videoYasuo", "onProgress: " + percent);
            }
        });
        String dstVideo = videoUrl;
        try {
//            VideoFunctions.VideoScale(editor, videoUrl); //这个只是缩小尺寸,不是压缩视频大小
            String videoCompress = VideoFunctions.VideoScale(editor, videoUrl);
            if (!TextUtils.isEmpty(videoCompress)) {
                dstVideo = videoCompress;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dstVideo;
    }

    private static Activity getCurrentActivty() {
        return MyApplication.getInstance().getRunningActivity();
    }

    public void setTopicId(String tagid, String topicName) {
        this.topicId = tagid;
        this.topicName = topicName;
    }

    public void setMediaList(List<LocalMedia> list) {
        selectList = list;
    }

    public void setIsVideo(boolean b) {
        isVideo = b;
    }


    public boolean isImageType(String name) {

        return name.endsWith(".PNG") || name.endsWith(".png")
                || name.endsWith(".jpeg") || name.endsWith(".JPEG")
                || name.endsWith(".gif") || name.endsWith(".GIF")
                || name.endsWith(".jpg") || name.endsWith(".JPG");
    }
}
