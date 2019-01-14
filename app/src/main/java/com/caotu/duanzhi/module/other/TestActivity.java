package com.caotu.duanzhi.module.other;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.PathConfig;
import com.caotu.duanzhi.utils.GlideUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Response;
import com.sunfusheng.GlideImageView;

import java.io.File;
import java.util.List;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    String text = "Java 是一种跨平台的、解释型语言，Java 源代码编译成中间”字节码”存储于 class 文件中。Java 字节码中包括了很多源代码信息，如变量名、方法名，很容易被反编译成 Java 源代码。所以需要对java代码进行混淆。混淆就是对发布出去的程序进行重新组织和处理，混淆器将代码中的所有变量、函数、类的名称变为简短的英文字母代号，反编译后将难以阅读。同时混淆的时候会遍历代码以发现没有被调用的代码，从而将其在打包成apk时剔除，最终一定程度上降低了apk的大小，比如编译后 jar 文件体积大约能减少25% ";
    private Button bt_change;
    private GlideImageView gif_image;
    private List<LocalMedia> selectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        initView();
    }

    private void initView() {
        bt_change = (Button) findViewById(R.id.bt_change);
        bt_change.setOnClickListener(this);
        gif_image = findViewById(R.id.gif_image);
        gif_image.load("https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=4155505288,1957469709&fm=173&app=25&f=JPEG?w=600&h=450&s=D7B23DC508D63AC86A1DCD350300C0C3");
//        gif_image.setOnClickListener(this);
//
//        gif_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PictureSelector.create(TestActivity.this).themeStyle(R.style.picture_QQ_style).openExternalPreview(0, selectList);
//            }
//        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_change:
//                OkGo.<Bitmap>get("https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/i1hiewrbvbjqvxno2c7kd52qop7jqvxno2b.png")
//                        .execute(new BitmapCallback() {
//                            @Override
//                            public void onSuccess(Response<Bitmap> response) {
//                                Bitmap body = response.body();
//                                String saveImage = VideoAndFileUtils.saveImage(body);
//                                ToastUtil.showShort(saveImage);
//                            }
//                        });

                OkGo.<File>get("https://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/i1hiewrbvbjqvxno2c7kd52qop7jqvxno2b.png")
                        .execute(new FileCallback(PathConfig.LOCALFILE, System.currentTimeMillis() + ".png") {
                            @Override
                            public void onSuccess(Response<File> response) {
                                File body = response.body();
                            }

                            @Override
                            public void onError(Response<File> response) {
                                super.onError(response);
                            }
                        });
//                gif_image.load(null);
//                PictureSelector.create(this)
//                        .openGallery(PictureMimeType.ofImage())//图片，视频，音频，全部
//                        .theme(R.style.picture_QQ_style)
//                        .maxSelectNum(9)
//                        .minSelectNum(1)
//                        .selectionMode(PictureConfig.MULTIPLE)//单选或多选
//                        .previewImage(true)//是否可预览图片 true or false
//                        // .compressGrade(Luban.THIRD_GEAR)
//                        .isCamera(true)
//                        .compress(true)
//                        .imageSpanCount(3)
//                        //.compressMode(PictureConfig.LUBAN_COMPRESS_MODE)
//                        .glideOverride(160, 160)
//                        .previewEggs(true)
//                        .isGif(true)//gif支持
//                        .forResult(PictureConfig.REQUEST_PICTURE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.REQUEST_PICTURE:
                    selectList = PictureSelector.obtainMultipleResult(data);
                    GlideUtils.loadImage(selectList.get(0).getPath(), gif_image);
                    break;

            }
        }
    }
}
