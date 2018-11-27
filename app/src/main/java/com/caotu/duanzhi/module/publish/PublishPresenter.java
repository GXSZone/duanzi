package com.caotu.duanzhi.module.publish;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.UploadServiceTask;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.PublishResponseBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusCode;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ThreadExecutor;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.BindPhoneDialog;
import com.lansosdk.VideoFunctions;
import com.lansosdk.videoeditor.VideoEditor;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author mac
 * @日期: 2018/11/5
 * @describe 由于有多处用到发布内容的.抽取共用逻辑
 */
public class PublishPresenter {
    private IVewPublish IView;
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
    public String mWidthAndHeight;
    //发表内容的类型  内容类型: 1横 2竖 3图片 4文字
    public String publishType;

    public static final String fileTypeImage = ".jpg";
    public static final String fileTypeVideo = ".mp4";
    public static final String fileTypeGif = ".gif";
    private String topicName;

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
        mWidthAndHeight = null;
        publishType = null;
        topicName = null;

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
        String contentUrl = new JSONArray(uploadTxFiles).toString();
        map.put("contenturllist", contentUrl);//内容连接
        map.put("contentype", publishType);//内容类型 1横 2竖 3图片 4文字
        map.put("showtime", videoDuration);
        OkGo.<BaseResponseBean<PublishResponseBean>>post(HttpApi.WORKSHOW_PUBLISH)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<PublishResponseBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<PublishResponseBean>> response) {
                        // TODO: 2018/11/16 这里需要创建好bean对象给首页展示
                        PublishResponseBean data = response.body().getData();

                        MomentsDataBean publishBean = new MomentsDataBean();
                        publishBean.setUserheadphoto(MySpUtils.getString(MySpUtils.SP_MY_AVATAR));
                        publishBean.setUsername(MySpUtils.getString(MySpUtils.SP_MY_NAME));
                        publishBean.setContentuid(MySpUtils.getMyId());
                        publishBean.setContentid(data.getContentid());
                        publishBean.setContenttitle(data.getContenttitle());
                        publishBean.setIsshowtitle("1");
                        publishBean.setShowtime(videoDuration);
                        publishBean.setContenturllist(contentUrl);
                        if (!TextUtils.isEmpty(topicId) && !TextUtils.isEmpty(topicName)) {
                            publishBean.setTagshowid(topicId);
                            publishBean.setTagshow(topicName);
                        }
                        publishBean.setContenttext(mWidthAndHeight);
                        publishBean.setContenttype(publishType);
                        publishBean.setPlaycount("0");

                        // TODO: 2018/11/7 还需要封装成首页列表展示的bean对象
                        EventBusHelp.sendPublishEvent(EventBusCode.pb_success, publishBean);
                        //包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统sd卡权限
                        PictureFileUtils.deleteCacheDirFile(MyApplication.getInstance());
                        MyApplication.getInstance().getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                clearSelectList();
                            }
                        },50);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<PublishResponseBean>> response) {
                        ToastUtil.showShort("发布失败！");
                        EventBusHelp.sendPublishEvent(EventBusCode.pb_error, null);
                        MyApplication.getInstance().getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                clearSelectList();
                            }
                        },200);
                        super.onError(response);
                    }
                });
    }

    public void getPicture() {
        PictureSelector.create(getCurrentActivty())
                .openGallery(PictureMimeType.ofImage())//图片，视频，音频，全部
                .theme(R.style.picture_QQ_style)
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
                .openClickSound(true)//声音
                .selectionMedia(selectList)
                .forResult(PictureConfig.REQUEST_PICTURE);
    }


    public void getVideo() {
        PictureSelector.create(getCurrentActivty())
                .openGallery(PictureMimeType.ofVideo())//图片，视频，音频，全部
                .theme(R.style.picture_QQ_style)
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
                .videoMinSecond(5)
                .videoMaxSecond(5 * 60)
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
            isVideo = false;
            //纯文字
            publishType = "4";
            if (shouldCheckLength()) return;

            if (IView != null) {
                IView.startPublish();
            }
            requestPublish();
        } else if (selectList.size() == 1 && PictureMimeType.isVideo(selectList.get(0).getPictureType())) {

            isVideo = true;
            //这个是视频,除了要获取是横竖视频,还要获取视频时长,视频封面,视频压缩
            // 获取视频时长
            LocalMedia media = selectList.get(0);
            //保存的是long类型的秒值
            long duration = media.getDuration();
            if (duration < 5000) {
                ToastUtil.showShort("该条视频时间太短了哦");
                return;
            }
            if (IView != null) {
                IView.startPublish();
            }
//            DateUtils.timeParse(duration)
            videoDuration = String.valueOf(duration / 1000);

            ThreadExecutor.getInstance().executor(new Runnable() {
                @Override
                public void run() {
                    String filePash = startRunFunction(media.getPath());  //视频压缩后的地址,上传用
                    Bitmap videoThumbnail = VideoEditor.getVideoThumbnailAndSave(filePash);
                    String saveImage = VideoAndFileUtils.saveImage(videoThumbnail);
                    // TODO: 2018/11/7 获取压缩后的视频的宽高以及是否是竖视频的判断
                    String[] widthAndHeight = VideoFunctions.getWidthAndHeight(filePash);
                    String width = widthAndHeight[0];
                    String height = widthAndHeight[1];
//                    1横 2竖 3图片 4文字
                    publishType = widthAndHeight[2].equals("yes") ? "2" : "1";
                    mWidthAndHeight = width + "," + height;
                    //第一个是视频封面,第二个是视频
                    updateToTencent(fileTypeImage, saveImage, true);
                    updateToTencent(fileTypeVideo, filePash, true);
                }
            });
        } else {
            if (IView != null) {
                IView.startPublish();
            }
            isVideo = false;
            //图片处理
            publishType = "3";
            if (selectList != null && selectList.size() == 1) {
                int[] imageWidthHeight = VideoAndFileUtils.getImageWidthHeight(selectList.get(0).getCompressPath());
                mWidthAndHeight = imageWidthHeight[0] + "," + imageWidthHeight[1];
            }

            for (int i = 0; i < selectList.size(); i++) {
                String path = selectList.get(i).getCompressPath();
                if (TextUtils.isEmpty(path)) {
                    path = selectList.get(i).getPath();
                }
                String fileType;
                if (path.endsWith(".gif") || path.endsWith(".GIF")) {
                    fileType = fileTypeGif;
                } else {
                    fileType = fileTypeImage;
                }
                updateToTencent(fileType, path, false);
            }
        }
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
//                        float result = (float) (progress * 100.0 / max);
                    }

                    @Override
                    public void onLoadSuccess(String url) {
                        String realUrl = "https://" + url;
                        uploadTxFiles.add(realUrl);
                        if (isVideo) {
                            if (uploadTxFiles.size() == 2) {
                                requestPublish();
                            }
                        } else {
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
     * 视频操作
     *
     * @return
     */
    private String startRunFunction(String videoUrl) {

        VideoEditor editor = new VideoEditor();
        // TODO: 2018/11/7 如果是视频操作,则还有视频压缩处理的进度,所以首页展示的进度,max值搞成200,这样就能使用两个进度
//        editor.setOnProgessListener(new onVideoEditorProgressListener() {
//            @Override
//            public void onProgress(VideoEditor v, int percent) {
//                LogUtil.logString("onProgress: " + "=====" + percent);
//                if (percent == 100) {
////                    videoFrameDialog.dismiss();
//                }
//            }
//        });
        String dstVideo = videoUrl;
        try {
            String dstVideo1 = VideoFunctions.VideoScale(editor, videoUrl);
            if (!TextUtils.isEmpty(dstVideo1)) {
                dstVideo = dstVideo1;
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
}
