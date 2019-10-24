package com.caotu.duanzhi.module.publish;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.PublishResponseBean;
import com.caotu.duanzhi.Http.tecentupload.UploadServiceTask;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.EventBusCode;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.config.HttpCode;
import com.caotu.duanzhi.module.login.BindPhoneAndForgetPwdActivity;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ParserUtils;
import com.caotu.duanzhi.utils.ThreadExecutor;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.widget.EditTextLib.SpXEditText;
import com.lansosdk.VideoFunctions;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.lansosdk.videoeditor.VideoEditor;
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
    int count = 0;
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
        count = 0;
        topicId = null;
        videoDuration = null;
        content = null;
        mWidthAndHeight = "";
        publishType = "";
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
            Log.i(BaseConfig.TAG, "requestPublish: " + replaceUrl);
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
                        uMengPublishError();
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
                        MomentsDataBean contentNewBean = DataTransformUtils.getContentNewBean(response.body().getData());
                        EventBusHelp.sendPublishEvent(EventBusCode.pb_success, contentNewBean);
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
                .videoMinSecond(3)
                .videoMaxSecond(480)
                .recordVideoSecond(7 * 60 + 59)//录制最大时间 后面判断不能超过5分钟 是否要改成4分59秒
//                .selectionMedia(videoList)
                .forResult(PictureConfig.REQUEST_VIDEO);
    }

    //    内容类型  1横  2竖  3图片   4纯文字
    public boolean isVideo = false;

    public void publishBtClick() {
        //  第一层是绑定手机
        if (!MySpUtils.getBoolean(MySpUtils.SP_HAS_BIND_PHONE, false)) {
            HelperForStartActivity.openBindPhoneOrPsw(BindPhoneAndForgetPwdActivity.BIND_TYPE);
            return;
        }
        String editContent = IView.getEditView().getText().toString().trim();
        if (TextUtils.isEmpty(editContent) && (selectList == null || selectList.size() == 0)) {
            ToastUtil.showShort("请先选择发表内容");
            return;
        }
        EditText editText = IView.getEditView();
        if (editText instanceof SpXEditText) {
            content = ParserUtils.beanToHtml(editText.getText().toString(),
                    ((SpXEditText) editText).getAtListBean());
        }
        Log.i(BaseConfig.TAG, "publishBtClick: " + content);
        if (CommonHttpRequest.sensitiveWord != null && !TextUtils.isEmpty(content)) {
            int length = CommonHttpRequest.sensitiveWord.length;
            for (int i = 0; i < length; i++) {
                if (content.contains(CommonHttpRequest.sensitiveWord[i])) {
                    ToastUtil.showShort("你的内容包含敏感词 \"" + CommonHttpRequest.sensitiveWord[i] + "\" ，请修改");
                    return;
                }
            }
        }
        if (selectList == null || selectList.size() == 0) {
            upJustText();
        } else if (selectList.size() == 1 && isVideo) {
            upVideo();
        } else {
            upImages();
        }
    }

    private void upVideo() {
        //这个是视频,除了要获取是横竖视频,还要获取视频时长,视频封面,视频压缩
        // 获取视频时长
        LocalMedia media = selectList.get(0);
        //保存的是long类型的秒值
        long duration = media.getDuration();
        if (duration < 3000) {
            ToastUtil.showShort(" 这条视频时间太短了哟~（＜3s）");
            //重新放开view的点击事件
            IView.getPublishView().setEnabled(true);
            return;
        } else if (duration > 8 * 60 * 1000) {
            ToastUtil.showShort("这条视频时间太长了哟~（＞8min)");
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
            ThreadExecutor.getInstance().executor(new Runnable() {
                @Override
                public void run() {
                    String videoPath = startRunFunction(path);
                    if (TextUtils.isEmpty(videoPath)) {
                        ToastUtil.showShort("转码失败");
                        uMengPublishError();
                        return;
                    }
                    if (IView == null) return;
                    IView.getPublishView().post(() -> startVideoUpload(media, videoPath));
                }
            });

        } else {
            startVideoUpload(media, path);
        }
    }

    private void upImages() {
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

    private void upJustText() {
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
            //系统提供的获取视频缩略图的api,   获取的图片大小太小了
//            Bitmap videoThumbnail = ThumbnailUtils.createVideoThumbnail(filePash,
//                    MediaStore.Images.Thumbnails.MINI_KIND);
            saveImage = VideoAndFileUtils.saveImage(videoThumbnail);
            if (TextUtils.isEmpty(saveImage)) {
                ToastUtil.showShort("视频封面获取失败");
                uMengPublishError();
                EventBusHelp.sendPublishEvent(EventBusCode.pb_error, null);
                return;
            }
            videoCover = saveImage;
        }
        // TODO: 宽高信息不直接用media的值,用另外一套根据视频旋转角度还定位的更准确
        String[] widthAndHeight = VideoFunctions.getWidthAndHeight(filePash);
        mWidthAndHeight = widthAndHeight[0] + "," + widthAndHeight[1];
        publishType = widthAndHeight[2];
        //以防万一视频时长没有,那就换种方式
        if (TextUtils.isEmpty(videoDuration) || TextUtils.equals("0", videoDuration)) {
            videoDuration = widthAndHeight[3];
        }

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
        EditText editText = IView.getEditView();
        if (TextUtils.isEmpty(editText.getText()) || editText.getText().length() < 5) {
            ToastUtil.showShort("亲，还可以再写一些字");
            return true;
        } else {
            return false;
        }
    }

    /**
     * 腾讯这个渣渣,多文件上传的总进度还得这么玩,upload的进度为100%不是及时回调success的(卧槽),
     * 只能在进度回调里通过== 判断,自己累加,不然总进度是错的
     *
     * @param fileType
     * @param filePash
     * @param isVideo
     */
    public void updateToTencent(String fileType, String filePash, boolean isVideo) {
        UploadServiceTask.upLoadFile(fileType, filePash, new UploadServiceTask.OnUpLoadListener() {

            @Override
            public void onUpLoad(float progress) {
                int uploadSize = selectList.size();
                if (isVideo) {
                    uploadSize = 2;
                }
                int barProgress = (int) ((100.0f / uploadSize) * (progress / 100f + count));
                if (progress == 100f) {
                    count++;
                }
                Log.i("barProgress", "onUpLoad: " + barProgress);
                EventBusHelp.sendPublishEvent(EventBusCode.pb_progress, barProgress);
                uploadProgress(barProgress); //方便需要进度的地方拿进度展示
            }

            @Override
            public void onLoadSuccess(String url) {
                if (isVideo) {
                    //为了保险起见,封面图放第一位
                    if (isImageType(url)) {
                        uploadTxFiles.add(0, url);
                    } else {
                        uploadTxFiles.add(url);
                    }

                    if (uploadTxFiles.size() == 2) {
                        requestPublish();
                    }
                } else {
                    uploadTxFiles.add(url);
                    if (uploadTxFiles.size() == selectList.size()) {
                        requestPublish();
                    }
                }
            }

            @Override
            public void onLoadError(String exception) {
                uMengPublishError();
                // TODO: 2018/11/7 视频压缩不会失败,只有上传有error回调
                EventBusHelp.sendPublishEvent(EventBusCode.pb_error, null);
                ToastUtil.showShort("上传失败:" + exception);
            }
        });

    }

    public void uMengPublishError() {
        UmengHelper.event(UmengStatisticsKeyIds.publish_error);
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

    public void setTopicId(String tagid) {
        this.topicId = tagid;
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
