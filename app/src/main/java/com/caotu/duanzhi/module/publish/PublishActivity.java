package com.caotu.duanzhi.module.publish;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.ThemeBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.RTextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PublishActivity extends BaseActivity implements View.OnClickListener {
    private EditText editText;
    private ImageView mBtPublish;
    private ImageView mIvBack;
    private RTextView mTvSelectedTopic;
    private RecyclerView imageLayout;
    public static final int SELECTOR_TOPIC = 229;
    public static final String KEY_SELECTED_TOPIC = "SELECTED_TOPIC";

    /*  获取视频时长
          long duration = image.getDuration();
                contentHolder.tv_duration.setText(DateUtils.timeParse(duration));
     */
    @Override
    protected void initView() {
        editText = findViewById(R.id.et_publish_text);
        mBtPublish = (ImageView) findViewById(R.id.bt_publish);
        mBtPublish.setOnClickListener(this);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(this);
        mTvSelectedTopic = (RTextView) findViewById(R.id.tv_selected_topic);
        mTvSelectedTopic.setOnClickListener(this);
        imageLayout = (RecyclerView) findViewById(R.id.fragment_publish_images_show_ll);
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
                // TODO: 2018/11/5 第一层是绑定手机
                if (!MySpUtils.getBoolean(MySpUtils.SP_HAS_BIND_PHONE, false)) {
//                    if (bindPhoneDialog == null) {
//                        bindPhoneDialog = new BindPhoneDialog(App.getInstance().getRunningActivity());
//                    }
//                    bindPhoneDialog.show();
                    return;
                }
                // TODO: 2018/11/5 校验敏感词
                String editContent = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(editContent)) {
                    checkPublishWord(editContent);
                    return;
                }


                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_selected_topic:
                Intent intent = new Intent(this, SelectTopicActivity.class);
                startActivityForResult(intent, SELECTOR_TOPIC);
                break;
            case R.id.iv_get_photo:
                if (selectList.size() != 0 && publishType != -1 && publishType != 1) {
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setMessage("若你要添加图片，已选视频将从发表界面中清除了？")
                            .setPositiveButton(android.R.string.ok, (dialog13, which) -> {
                                dialog13.dismiss();
                                getPicture();
                            })
                            .setNegativeButton(android.R.string.cancel, (dialog14, which) -> dialog14.dismiss()).create();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(DevicesUtils.getColor(R.color.color_FF8787));
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                    dialog.show();
                } else {
                    getPicture();
                }
                break;
            case R.id.iv_get_video:
                if (selectList.size() != 0 && publishType != -1 && publishType != 2) {
                    AlertDialog dialog = new AlertDialog.Builder(this).setMessage("若你要添加视频，已选图片将从发表界面中清除了？")
                            .setPositiveButton(android.R.string.ok, (dialog12, which) -> {
                                dialog12.dismiss();
                                getVideo();
                            })
                            .setNegativeButton(android.R.string.cancel, (dialog1, which) -> dialog1.dismiss())
                            .create();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(DevicesUtils.getColor(R.color.color_FF8787));
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                    dialog.show();

                } else {
                    getVideo();
                }
                break;

        }
    }


    private List<LocalMedia> selectList = new ArrayList<>();
    //目前有:纯图片,纯视频,纯文字,视频加文字,图片加文字
    //       1     2     3       4        5
    private int publishType = -1;

    public void getPicture() {
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
        selectList.clear();
    }


    private void getVideo() {
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
                .videoMinSecond(5)
                .videoMaxSecond(5 * 60)
//                .videoQuality(0) 默认是高质量1
                .recordVideoSecond(4 * 60 + 59)//录制最大时间 后面判断不能超过5分钟 是否要改成4分59秒
                .openClickSound(true)//声音
//                .selectionMedia(videoList)
                .forResult(PictureConfig.REQUEST_VIDEO);
        selectList.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.REQUEST_VIDEO:
                    publishType = 1;
                    selectList = PictureSelector.obtainMultipleResult(data);
                    break;
                case PictureConfig.REQUEST_PICTURE:
                    publishType = 2;
                    selectList = PictureSelector.obtainMultipleResult(data);
                    break;
                //获取选择的话题
                case SELECTOR_TOPIC:
                    ThemeBean date = data.getParcelableExtra(KEY_SELECTED_TOPIC);
                    mTvSelectedTopic.setText(date.getThemeName());
                    break;
                default:
                    publishType = 3;
                    selectList = PictureSelector.obtainMultipleResult(data);
                    break;

            }
        }
    }

    /**
     * 校验敏感词
     *
     * @param editContent
     */

    private void checkPublishWord(String editContent) {
        if (!mBtPublish.isEnabled()) {
            ToastUtil.showShort("正在发布,请勿重复点击");
            return;
        }
        mBtPublish.setEnabled(false);
        HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
        params.put("checkword", editContent);
        OkGo.<String>post(HttpApi.WORKSHOW_VERIFY)
                .upJson(new JSONObject(params))
                .execute(new JsonCallback<String>() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();

                    }

                    @Override
                    public void onError(Response<String> response) {
                        mBtPublish.setEnabled(true);
                        ToastUtil.showShort("碰到敏感词啦，改一下呗");
                        super.onError(response);
                    }
                });
    }
}
