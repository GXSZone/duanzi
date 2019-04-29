package com.caotu.duanzhi.other;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.tecentupload.UploadServiceTask;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 真是有问题,明明在子线程但是会导致UI主线程卡顿
 */
public class ChangeUserPhotoServices extends IntentService {

    public ChangeUserPhotoServices() {
        super("WaterMarkServices");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) return;
        String photo = intent.getStringExtra("photo");
        if (TextUtils.isEmpty(photo)) return;
        OkGo.<Bitmap>get(photo)
                .execute(new BitmapCallback() {
                    @Override
                    public void onSuccess(Response<Bitmap> response) {
                        String image = VideoAndFileUtils.saveImage(response.body());
                        uploadImage(image);
                    }
                });

    }

    private void uploadImage(String photo) {
        UploadServiceTask.upLoadFile(".jpg", photo, new UploadServiceTask.OnUpLoadListener() {
            @Override
            public void onUpLoad(long progress, long max) {

            }

            @Override
            public void onLoadSuccess(String url) {
                String realUrl = "https://" + url;
                requestChangeUserInfo(realUrl,photo);
            }

            @Override
            public void onLoadError(String exception) {

            }
        });
    }

    /**
     * 修改用户信息
     *
     * @param httpPhoto
     * @param localUrl
     */
    private void requestChangeUserInfo(String httpPhoto, String localUrl) {
        Map<String, String> map = new HashMap<>();
        map.put("userheadphoto", httpPhoto);
        OkGo.<BaseResponseBean<String>>post(HttpApi.SET_USER_BASE_INFO)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
//                        String data = response.body().getData();
                        LanSongFileUtil.deleteFile(localUrl);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<String>> response) {
                        // TODO: 2018/12/7 这里没有调用super 特意,父类的提示不需要
                    }
                });

    }

}
