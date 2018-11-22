package com.caotu.duanzhi.module.mine;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.UploadServiceTask;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.TextWatcherAdapter;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.utils.ThreadExecutor;
import com.caotu.duanzhi.utils.ToastUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class SubmitFeedBackActivity extends BaseActivity {
    public static final int EXCEPTION = 1;
    public static final int OTHER = 2;
    private EditText contentEdit, connectWayEdit;
    private ImageView imageView;
    private String imgUrl;
    private TextView textWatcher;
    private int intExtra;
    private String imagePath;


    public static void start(int type) {
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        Intent intent = new Intent(runningActivity, SubmitFeedBackActivity.class);
        intent.putExtra("TYPE", type);
        runningActivity.startActivity(intent);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_help_layout;
    }

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        TextView title = findViewById(R.id.help_title);
        contentEdit = findViewById(R.id.fragment_help_content_edt);
        connectWayEdit = findViewById(R.id.fragment_help_connectway_edt);
        imageView = findViewById(R.id.fragment_help_image_iv);
        textWatcher = findViewById(R.id.text_watcher);
        imageView.setOnClickListener(view -> openIcon());

        contentEdit.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable editable) {

                int length = editable.toString().length();
                if (length >= 500) {
                    ToastUtil.showShort(R.string.help_text_max_length);
                }
                textWatcher.setText(String.format("%d/500", length));

            }
        });
        intExtra = getIntent().getIntExtra("TYPE", 1);
        if (1 == intExtra) {
            title.setText("功能异常");
        } else if (2 == intExtra) {
            title.setText("其他反馈");
        }
        findViewById(R.id.tv_request).setOnClickListener(v -> clickRight());
    }


    public void clickRight() {
        if (TextUtils.isEmpty(imagePath)) {
            ThreadExecutor.getInstance().executor(new Runnable() {
                @Override
                public void run() {
                    UploadServiceTask.upLoadFile(".jpg", imagePath, new UploadServiceTask.OnUpLoadListener() {
                        @Override
                        public void onUpLoad(long progress, long max) {

                        }

                        @Override
                        public void onLoadSuccess(String url) {
                            imgUrl = "https://" + url;
                            request();
                        }

                        @Override
                        public void onLoadError(String exception) {
                            ToastUtil.showShort("上传失败");
                        }
                    });
                }
            });

        } else {
            request();
        }
    }

    public void request() {
        String content = contentEdit.getText().toString().trim();
        if (content.length() == 0) {
            ToastUtil.showShort("请输入内容");
            return;
        }
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("contactway", connectWayEdit.getText().toString().trim());
        map.put("feedtext", content);
        map.put("feedtype", String.valueOf(intExtra));
        map.put("feedurllist", imgUrl);
        OkGo.<BaseResponseBean<String>>post(HttpApi.USER_MY_TSUKKOMI)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        ToastUtil.showShort("提交成功！");
                        finish();
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<String>> response) {
                        ToastUtil.showShort("提交失败！");
                        super.onError(response);
                    }
                });

    }


    private void openIcon() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())//图片，视频，音频，全部
                .theme(R.style.picture_QQ_style)
                .selectionMode(PictureConfig.SINGLE)
                .previewImage(true)//是否可预览图片 true or false
                .isZoomAnim(true)
                .compress(true)
                .imageSpanCount(3)
                .isCamera(true)
                //.compressMode(PictureConfig.LUBAN_COMPRESS_MODE)
//                .glideOverride(160, 160)
                .previewEggs(true)
                .forResult(PictureConfig.REQUEST_PICTURE);//结果回调onActivityResult code
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == PictureConfig.REQUEST_PICTURE || requestCode == PictureConfig.CAMERA) {
            // 图片、视频、音频选择结果回调
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            LocalMedia localMedia = selectList.get(0);
            if (localMedia.isCompressed()) {
                imagePath = localMedia.getCompressPath();
            } else {
                imagePath = localMedia.getPath();
            }

            RequestOptions options = new RequestOptions()
                    .placeholder(R.mipmap.image_default)
                    .dontAnimate();

            Glide.with(this).load(imagePath)
                    .apply(options)
                    .into(imageView);
        }
    }

}
