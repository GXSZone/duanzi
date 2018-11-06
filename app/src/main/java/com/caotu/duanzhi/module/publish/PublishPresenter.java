package com.caotu.duanzhi.module.publish;

import android.app.Activity;
import android.text.TextUtils;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.utils.LogUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ThreadExecutor;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.dialog.BindPhoneDialog;
import com.lansosdk.VideoFunctions;
import com.lansosdk.videoeditor.VideoEditor;
import com.lansosdk.videoeditor.onVideoEditorProgressListener;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mac
 * @日期: 2018/11/5
 * @describe 由于有多处用到发布内容的.抽取共用逻辑
 */
public class PublishPresenter {
    private publishView IView;
    private List<LocalMedia> selectList;
    private String topicId;
    private String videoDuration;

    public PublishPresenter(publishView context) {
        IView = context;
    }

    public void destory() {
        IView = null;
    }

    /**
     * 发布视频和图片的接口请求
     */
    public void requestPublish(String content, String type, List<String> list, String videoTime) {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("contenttag", topicId);//标签id
        //单张图和视频的时候传
        map.put("contenttext", null);//宽，高
        map.put("contenttitle", content);//标题
        // TODO: 2018/11/6 list转jsonarray格式
        map.put("contenturllist", new JSONArray(list).toString());//内容连接
        map.put("contentype", type);//内容类型 1横 2竖 3图片 4文字
        map.put("showtime", videoTime);
        OkGo.<BaseResponseBean<String>>post(HttpApi.WORKSHOW_PUBLISH)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        String code = response.body().getCode();
                        ToastUtil.showShort("发布成功！");
                        //包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统sd卡权限
                        PictureFileUtils.deleteCacheDirFile(MyApplication.getInstance());

                    }

                    @Override
                    public void onError(Response<BaseResponseBean<String>> response) {
                        ToastUtil.showShort("发布失败！");
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
//                .videoQuality(0) 默认是高质量1
                .recordVideoSecond(4 * 60 + 59)//录制最大时间 后面判断不能超过5分钟 是否要改成4分59秒
                .openClickSound(true)//声音
//                .selectionMedia(videoList)
                .forResult(PictureConfig.REQUEST_VIDEO);
    }

    public void publishBtClick() {
        // TODO: 2018/11/5 第一层是绑定手机
        if (!MySpUtils.getBoolean(MySpUtils.SP_HAS_BIND_PHONE, false)) {
            new BindPhoneDialog(getCurrentActivty()).show();
            return;
        }
        // TODO: 2018/11/5 校验敏感词
        String editContent = IView.getEditView().getText().toString().trim();
        if (!TextUtils.isEmpty(editContent)) {
            checkPublishWord(editContent);
            return;
        }

        if (TextUtils.isEmpty(editContent) && (selectList == null || selectList.size() == 0)) {
            ToastUtil.showShort("请先选择发表内容");
            return;
        }
        uploadFile();
    }

    /**
     * 校验敏感词
     *
     * @param editContent
     */

    private void checkPublishWord(String editContent) {
        if (!IView.getPublishView().isEnabled()) {
            ToastUtil.showShort("正在发布,请勿重复点击");
            return;
        }
        IView.getPublishView().setEnabled(false);

        HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
        params.put("checkword", editContent);
        OkGo.<BaseResponseBean<String>>post(HttpApi.WORKSHOW_VERIFY)
                .upJson(new JSONObject(params))
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        String body = response.body().getData();
                        if (!"Y".equals(body)) {
                            IView.getEditView().setText(body);
                            uploadFile();
                        } else {
                            IView.getPublishView().setEnabled(true);
                            ToastUtil.showShort("碰到敏感词啦，改一下呗");
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<String>> response) {
                        IView.getPublishView().setEnabled(true);
                        ToastUtil.showShort("网络质量不好，到空旷宽敞的地方再试试");
                        super.onError(response);
                    }
                });
    }

    //    内容类型  1横  2竖  3图片   4纯文字
    public void uploadFile() {
        if (selectList == null || selectList.size() == 0) {
            //纯文字
            String editContent = IView.getEditView().getText().toString().trim();
            requestPublish(editContent, null, null, null);
        } else if (selectList.size() == 1 && PictureMimeType.isVideo(selectList.get(0).getPictureType())) {
            //这个是视频,除了要获取是横竖视频,还要获取视频时长,视频封面,视频压缩
            // 获取视频时长
            LocalMedia media = selectList.get(0);
            long duration = media.getDuration();
            videoDuration = String.valueOf(duration);

            ThreadExecutor.getInstance().executor(new Runnable() {
                @Override
                public void run() {
                    String filePash = startRunFunction(media.getPath());
//                    uploadFile(isVideo, finalIsTextOnly);
                }
            });


        } else {
            //图片处理
        }
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
                LogUtil.logString("onProgress: " + "=====" + percent);
                if (percent == 100) {
//                    videoFrameDialog.dismiss();
                }
            }
        });
        String dstVideo = videoUrl;
        try {
            // dstVideo = VideoFunctions.videoScaleAddPicture(App.getInstance().getRunningActivity(), editor, videoUrl);
            dstVideo = VideoFunctions.VideoScale(editor, videoUrl);
        } catch (Exception e) {
            dstVideo = videoUrl;
            e.printStackTrace();
        }
        if (dstVideo == null) {
            dstVideo = videoUrl;
        }
        return dstVideo;
    }

    private static Activity getCurrentActivty() {
        return MyApplication.getInstance().getRunningActivity();
    }

    public void setTopicId(String selectorTopicId) {
        this.topicId = selectorTopicId;
    }

    public void setMediaList(List<LocalMedia> list) {
        selectList = list;
    }
}
