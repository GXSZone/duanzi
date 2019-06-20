package com.caotu.duanzhi.module.publish;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.caotu.duanzhi.Http.bean.TopicItemBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusCode;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.login.BindPhoneAndForgetPwdActivity;
import com.caotu.duanzhi.module.login.LoginAndRegisterActivity;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.other.TextWatcherAdapter;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.view.dialog.BaseIOSDialog;
import com.caotu.duanzhi.view.widget.OneSelectedLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.ruffian.library.widget.RTextView;

import java.util.ArrayList;
import java.util.List;

public class PublishActivity extends BaseActivity implements View.OnClickListener, IVewPublish {
    private EditText editText;
    private TextView editLength;
    private View mBtPublish;
    private RTextView mTvSelectedTopic;
    public static final int SELECTOR_TOPIC = 229;
    public static final String KEY_SELECTED_TOPIC = "SELECTED_TOPIC";
    private PublishImageShowAdapter adapter;
    private PublishPresenter presenter;
    private TopicItemBean topicBean;
    private OneSelectedLayout layout;
    private RecyclerView imageLayout;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_publish_new;
    }

    @Override
    protected void initView() {
        editText = findViewById(R.id.et_publish_text);
        editLength = findViewById(R.id.tv_text_length);
        mBtPublish = findViewById(R.id.bt_publish);
        //这里添加一个按键反馈
        DevicesUtils.setAlphaSelector(mBtPublish);
        mBtPublish.setOnClickListener(this);
        ImageView mIvBack = findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(this);
        mTvSelectedTopic = findViewById(R.id.tv_selected_topic);
        mTvSelectedTopic.setOnClickListener(this);
        findViewById(R.id.iv_get_photo).setOnClickListener(this);
        findViewById(R.id.iv_get_video).setOnClickListener(this);
        initRv();
        addEditTextListener();
        presenter = new PublishPresenter(this);

        layout = findViewById(R.id.radio_selected);
        layout.setListener(bean -> {
            topicBean = bean;
            UmengHelper.userTopicEvent(topicBean.getTagid());
            presenter.setTopicId(topicBean.getTagid());
            mTvSelectedTopic.setText("#添加其他话题");
        });
        List<TopicItemBean> topicList = MySpUtils.getTopicList();
        layout.setVisibility(topicList == null ? View.GONE : View.VISIBLE);
        layout.setDates(topicList);
        initDate();
        findViewById(R.id.iv_publish_topic).setOnClickListener(this);
        editText.post(() -> editText.requestFocus());
    }

    /**
     * 保存数据的默认显示
     */
    private void initDate() {
        publishType = MySpUtils.getInt(MySpUtils.SP_PUBLISH_TYPE, -1);
        String date = MySpUtils.getString(MySpUtils.SP_PUBLISH_MEDIA);
        if (!TextUtils.isEmpty(date)) {
            selectList = new Gson().fromJson(date, new TypeToken<List<LocalMedia>>() {
            }.getType());
            presenter.setMediaList(selectList);
            presenter.setIsVideo(publishType == 2);
            adapter.setImagUrls(selectList, publishType == 2);
            imageLayout.setVisibility(View.VISIBLE);
        }

        String text = MySpUtils.getString(MySpUtils.SP_PUBLISH_TEXT);
        if (!TextUtils.isEmpty(text)) {
            editText.setText(text);
            editText.setSelection(text.length());
        }
        TopicItemBean intentTopicBean = getIntent().getParcelableExtra("topicBean");
        if (intentTopicBean == null) {
            String topic = MySpUtils.getString(MySpUtils.SP_PUBLISH_TIPIC);
            if (!TextUtils.isEmpty(topic) && topic.contains(",")) {
                String[] split = topic.split(",");
                topicBean = new TopicItemBean();
                topicBean.setTagalias(split[0]);
                topicBean.setTagid(split[1]);
                mTvSelectedTopic.setText(topicBean.getTagalias());
                presenter.setTopicId(topicBean.getTagid());
            }
        } else {

            mTvSelectedTopic.setText(intentTopicBean.getTagalias());
            presenter.setTopicId(intentTopicBean.getTagid());
        }
    }

    private void initRv() {
        imageLayout = findViewById(R.id.publish_images);
        //初始化9宫格,已在xml配置好
        adapter = new PublishImageShowAdapter();
        imageLayout.setHasFixedSize(true);
        adapter.setOnClickItemListener(new PublishImageShowAdapter.OnClickItemListener() {

            @Override
            public void onClickDelete(List<LocalMedia> imagUrls) {
                selectList = imagUrls;
                if (presenter != null) {
                    presenter.setMediaList(selectList);
                }
                if (imagUrls == null || imagUrls.isEmpty()) {
                    imageLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onClickAdd() {
                getPicture();
            }
        });
        imageLayout.setAdapter(adapter);
    }


    @Override
    protected void onDestroy() {
        if (presenter != null) {
            presenter.destory();
        }
        super.onDestroy();
    }

    private void addEditTextListener() {
        editText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString().trim();
                if (str.length() < 500) {
                    editLength.setText(String.format("%d/500", str.length()));
                } else {
                    editLength.setText("500/500");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_publish:
                if (!LoginHelp.isLogin()) {
                    UmengHelper.event(UmengStatisticsKeyIds.publish_login);
                    LoginHelp.goLogin();
                    return;
                }
                presenter.publishBtClick();
                UmengHelper.event(UmengStatisticsKeyIds.publish_bt);
                break;
            case R.id.iv_back:
                if (selectList.size() > 0 || editText.getText().toString().length() > 0) {
                    showSaveTipDialog();
                } else {
                    finish();
                }
                break;
            case R.id.iv_publish_topic:
            case R.id.tv_selected_topic:
                Intent intent = new Intent(this, SelectTopicActivity.class);
                startActivityForResult(intent, SELECTOR_TOPIC);
                UmengHelper.event(UmengStatisticsKeyIds.publish_topic);
                break;
            case R.id.iv_get_photo:
                if (selectList.size() != 0 && publishType != -1 && publishType == 2) {
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setMessage("若你要添加图片，已选视频将从发表界面中清除了？")
                            .setPositiveButton(android.R.string.ok, (dialog13, which) -> {
                                dialog13.dismiss();
                                selectList.clear();
                                getPicture();
                            })
                            .setNegativeButton(android.R.string.cancel, (dialog14, which) -> dialog14.dismiss()).create();

                    dialog.show();
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(DevicesUtils.getColor(R.color.color_FF8787));
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                } else {
                    getPicture();
                }
                break;
            case R.id.iv_get_video:
                if (selectList.size() != 0 && publishType != -1 && publishType == 1) {
                    AlertDialog dialog = new AlertDialog.Builder(this).setMessage("若你要添加视频，已选图片将从发表界面中清除了？")
                            .setPositiveButton(android.R.string.ok, (dialog12, which) -> {
                                dialog12.dismiss();
                                selectList.clear();
                                getVideo();
                            })
                            .setNegativeButton(android.R.string.cancel, (dialog1, which) -> dialog1.dismiss())
                            .create();

                    dialog.show();
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(DevicesUtils.getColor(R.color.color_FF8787));
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

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
        UmengHelper.event(UmengStatisticsKeyIds.publish_image);
        if (presenter == null) return;
        presenter.getPicture();
    }


    private void getVideo() {
        UmengHelper.event(UmengStatisticsKeyIds.publish_video);
        if (presenter == null) return;
        presenter.getVideo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginAndRegisterActivity.LOGIN_REQUEST_CODE
                && resultCode == LoginAndRegisterActivity.LOGIN_RESULT_CODE) {
            if (!MySpUtils.getBoolean(MySpUtils.SP_HAS_BIND_PHONE, false)) {
                HelperForStartActivity.openBindPhoneOrPsw(BindPhoneAndForgetPwdActivity.BIND_TYPE);
            } else {
                //登陆成功回调直接继续下一步发布操作
                mBtPublish.performClick();
            }
            return;
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.REQUEST_VIDEO:
                    publishType = 2;
                    imageLayout.setVisibility(View.VISIBLE);
                    selectList = PictureSelector.obtainMultipleResult(data);
                    presenter.setMediaList(selectList);
                    presenter.setIsVideo(true);
                    adapter.setImagUrls(selectList, true);
                    break;
                case PictureConfig.REQUEST_PICTURE:
                    imageLayout.setVisibility(View.VISIBLE);
                    publishType = 1;
                    selectList = PictureSelector.obtainMultipleResult(data);
                    presenter.setMediaList(selectList);
                    presenter.setIsVideo(false);
                    adapter.setImagUrls(selectList, false);
                    break;
                //获取选择的话题
                case SELECTOR_TOPIC:
                    TopicItemBean bean = data.getParcelableExtra(KEY_SELECTED_TOPIC);
                    if (bean == null) return;
                    layout.clearAllCheck();
                    topicBean = bean;
                    UmengHelper.topicEvent(topicBean.getTagid());
                    mTvSelectedTopic.setText(topicBean.getTagalias());
                    presenter.setTopicId(topicBean.getTagid());
                    break;
            }
        }
    }

    /**
     * 获取输入框对象
     *
     * @return
     */
    @Override
    public EditText getEditView() {
        return editText;
    }

    @Override
    public View getPublishView() {
        return mBtPublish;
    }

    /**
     * 现在发布开始就直接跳主页,在主页显示进度条
     */
    @Override
    public void startPublish() {
        MySpUtils.putTopicToSp(topicBean);
        mBtPublish.setEnabled(false);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        EventBusHelp.sendPublishEvent(EventBusCode.pb_start, null);
        finish();
    }

    ProgressDialog dialog;

    @Override
    public void notMp4() {
        mBtPublish.setEnabled(false);
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("正在转码中,请不要离开");
        }
        dialog.show();

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode && (selectList.size() > 0 ||
                editText.getText().toString().length() > 0)) {
            showSaveTipDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showSaveTipDialog() {
        BaseIOSDialog dialog = new BaseIOSDialog(this, new BaseIOSDialog.OnClickListener() {
            @Override
            public void okAction() {

                if (selectList != null && selectList.size() > 0) {
                    String data = new Gson().toJson(selectList);
                    MySpUtils.putString(MySpUtils.SP_PUBLISH_MEDIA, data);
                }
                if (editText.getText().toString().length() > 0) {
                    MySpUtils.putString(MySpUtils.SP_PUBLISH_TEXT, editText.getText().toString());
                }
                if (topicBean != null) {
                    //除了显示名字,还得有id
                    MySpUtils.putString(MySpUtils.SP_PUBLISH_TIPIC, topicBean.getTagalias() + "," + topicBean.getTagid());
                }
                MySpUtils.putInt(MySpUtils.SP_PUBLISH_TYPE, publishType);

                finish();
            }

            @Override
            public void cancelAction() {
                MySpUtils.clearPublishContent();
                finish();
            }
        });
        dialog.setCancelText("丢弃")
                .setOkText("保留")
                .setTitleText("是否保存编辑内容")
                .show();
    }
}
