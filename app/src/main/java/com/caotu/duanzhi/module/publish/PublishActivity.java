package com.caotu.duanzhi.module.publish;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.caotu.duanzhi.Http.bean.TopicItemBean;
import com.caotu.duanzhi.Http.bean.UserBean;
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
import com.caotu.duanzhi.utils.ParserUtils;
import com.caotu.duanzhi.view.dialog.BaseIOSDialog;
import com.caotu.duanzhi.view.widget.EditTextLib.SpXEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.ruffian.library.widget.RTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PublishActivity extends BaseActivity implements View.OnClickListener, IVewPublish {
    private SpXEditText editText;
    private TextView editLength;
    private View mBtPublish;
    private RTextView mTvSelectedTopic;
    public static final int SELECTOR_TOPIC = 229;
    public static final String KEY_SELECTED_TOPIC = "SELECTED_TOPIC";
    private PublishImageShowAdapter adapter;
    private PublishPresenter presenter;
    private TopicItemBean topicBean;
    private RecyclerView imageLayout;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_publish_new;
    }

    @Override
    protected void initView() {
        presenter = new PublishPresenter(this);
        editText = findViewById(R.id.et_publish_text);
        editLength = findViewById(R.id.tv_text_length);
        mBtPublish = findViewById(R.id.bt_publish);
        mBtPublish.setOnClickListener(this);
        mTvSelectedTopic = findViewById(R.id.tv_publish_topic);
        mTvSelectedTopic.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_get_photo).setOnClickListener(this);
        findViewById(R.id.iv_get_video).setOnClickListener(this);
        findViewById(R.id.iv_at_user).setOnClickListener(this);
        initRv();
        addEditTextListener();
        initDate();
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
            ParserUtils.htmlBindEditText(text, editText);
            int length = Objects.requireNonNull(editText.getText()).length();
            editText.setSelection(length);
        }
        TopicItemBean intentTopicBean = getIntent().getParcelableExtra("topicBean");
        if (intentTopicBean == null) {
            String topic = MySpUtils.getString(MySpUtils.SP_PUBLISH_TOPIC);
            if (!TextUtils.isEmpty(topic) && topic.contains(",")) {
                String[] split = topic.split(",");
                topicBean = new TopicItemBean();
                topicBean.tagalias = split[0];
                topicBean.tagid = split[1];
                mTvSelectedTopic.setText("# ".concat(topicBean.tagalias));
                presenter.setTopicId(topicBean.tagid);
            }
        } else {
            mTvSelectedTopic.setText("# ".concat(intentTopicBean.tagalias));
            presenter.setTopicId(intentTopicBean.tagid);
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
                    editLength.setText(String.format(Locale.CHINA, "%d/500", str.length()));
                } else {
                    editLength.setText("500/500");
                }
            }
        });
        editText.requestFocus();
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
            case R.id.tv_publish_topic:
                HelperForStartActivity.openSearchFromTopic(SELECTOR_TOPIC);
                UmengHelper.event(UmengStatisticsKeyIds.publish_topic);
                break;
            case R.id.iv_get_photo:
                if (selectList.size() != 0 && publishType == 2) {
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
                if (selectList.size() != 0 && publishType == 1) {
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
            case R.id.iv_at_user:
                UmengHelper.event(UmengStatisticsKeyIds.publish_at);
                HelperForStartActivity.openSearch();
                break;
        }
    }


    private List<LocalMedia> selectList = new ArrayList<>();

    //目前有:纯图片,纯视频,纯文字,视频加文字,图片加文字
    //       1     2     3       4        5
    private int publishType = -1;


    public void getPicture() {
        if (presenter == null) return;
        presenter.getPicture();
    }


    private void getVideo() {

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
                    topicBean = bean;
                    UmengHelper.topicEvent(topicBean.tagid);
                    mTvSelectedTopic.setText("# ".concat(topicBean.tagalias));
                    presenter.setTopicId(topicBean.tagid);
                    break;
                //@ 用户选择
                case HelperForStartActivity.at_user_requestCode:
                    UserBean extra = data.getParcelableExtra(HelperForStartActivity.KEY_AT_USER);
                    editText.addSpan(extra);
                    break;
            }
        }
    }

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
        EventBusHelp.sendPublishEvent(EventBusCode.pb_start, null);
        finish();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode && (selectList.size() > 0 ||
                !TextUtils.isEmpty(editText.getText()))) {
            showSaveTipDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showSaveTipDialog() {
        BaseIOSDialog dialog = new BaseIOSDialog(this, new BaseIOSDialog.OnClickListener() {
            @Override
            public void okAction() {
                saveDateAndFinish();
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

    /**
     * 这个有@ 的文本保存就麻烦了
     */
    public void saveDateAndFinish() {
        if (selectList != null && selectList.size() > 0) {
            String data = new Gson().toJson(selectList);
            MySpUtils.putString(MySpUtils.SP_PUBLISH_MEDIA, data);
        }
        if (topicBean != null) {
            //除了显示名字,还得有id
            MySpUtils.putString(MySpUtils.SP_PUBLISH_TOPIC, topicBean.tagalias + "," + topicBean.tagid);
        }
        if (!TextUtils.isEmpty(editText.getText())) {
            String content = ParserUtils.convertHtml(editText.getText().toString(), editText.getAtListBean());
            MySpUtils.putString(MySpUtils.SP_PUBLISH_TEXT, content);
        }
        MySpUtils.putInt(MySpUtils.SP_PUBLISH_TYPE, publishType);
        finish();
    }
}
