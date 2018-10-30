package com.caotu.duanzhi.module.publish;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.ruffian.library.widget.RTextView;

import java.util.ArrayList;
import java.util.List;

public class PublishActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mBtPublish;
    private ImageView mIvBack;
    private RTextView mTvSelectedTopic;
    private RecyclerView mFragmentPublishImagesShowLl;
    public static final int SELECTOR_TOPIC = 229;
    public static final String KEY_SELECTED_TOPIC = "SELECTED_TOPIC";


    @Override
    protected void initView() {
        mBtPublish = (ImageView) findViewById(R.id.bt_publish);
        mBtPublish.setOnClickListener(this);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(this);
        mTvSelectedTopic = (RTextView) findViewById(R.id.tv_selected_topic);
        mTvSelectedTopic.setOnClickListener(this);
        mFragmentPublishImagesShowLl = (RecyclerView) findViewById(R.id.fragment_publish_images_show_ll);
        findViewById(R.id.iv_get_photo).setOnClickListener(this);
        findViewById(R.id.iv_get_video).setOnClickListener(this);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_publish;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bt_publish:
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_selected_topic:
                Intent intent = new Intent(this, SelectTopicActivity.class);
                startActivityForResult(intent, SELECTOR_TOPIC);
                break;
            case R.id.iv_get_photo:
                getPicture();
                break;
            case R.id.iv_get_video:
                getVideo();
                break;

        }
    }

    private List<LocalMedia> selectList = new ArrayList<>();
    private List<LocalMedia> videoList = new ArrayList<>();
    private List<LocalMedia> imagUrls = new ArrayList<>();
    private boolean photoType = true;

    public void getPicture() {
        if (!photoType) {
            selectList.clear();
        }
        PictureSelector.create(this)
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


    private void getVideo() {
        if (photoType) {
            videoList.clear();
        }
        PictureSelector.create(this)
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
                .recordVideoSecond(4 * 60 + 59)//录制最大时间 后面判断不能超过5分钟 是否要改成4分59秒
                .openClickSound(true)//声音
//                .selectionMedia(videoList)
                .forResult(PictureConfig.REQUEST_VIDEO);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.REQUEST_VIDEO:
//                    isFunction = false;
                    photoType = false;
//                    isVideo = true;
//                    imagUrls.clear();
                    selectList = PictureSelector.obtainMultipleResult(data);
//                    videoTime = 0;
//                    setImages(TableShowImageConfig.VIDEO);
                    break;
                    //获取选择的话题
                case SELECTOR_TOPIC:

                    break;
                case PictureConfig.REQUEST_PICTURE:

                    break;
                default:
                    photoType = true;
//                    isVideo = false;
//                    imagUrls.clear();
                    selectList = PictureSelector.obtainMultipleResult(data);
//                    videoList.clear();
//                    videoList.addAll(selectList);
//                    videoTime = 0;
//                    setImages(TableShowImageConfig.STYPE_1);
                    break;

            }
        }
    }
}
