package com.caotu.duanzhi.module.publish;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.TopicItemBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusCode;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.module.TextWatcherAdapter;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.ScreenUtils;
import com.ruffian.library.widget.RTextView;

import java.util.ArrayList;
import java.util.List;

public class PublishActivity extends BaseActivity implements View.OnClickListener, publishView {
    private EditText editText;
    private TextView editLength;
    private ImageView mBtPublish;
    private ImageView mIvBack;
    private RTextView mTvSelectedTopic;
    private RecyclerView imageLayout;
    public static final int SELECTOR_TOPIC = 229;
    public static final String KEY_SELECTED_TOPIC = "SELECTED_TOPIC";
    private PublishImageShowAdapter adapter;
    private PublishPresenter presenter;

    /*  获取视频时长
          long duration = image.getDuration();
                contentHolder.tv_duration.setText(DateUtils.timeParse(duration));
     */
    @Override
    protected void initView() {
        editText = findViewById(R.id.et_publish_text);
        editLength = findViewById(R.id.tv_text_length);
        mBtPublish = (ImageView) findViewById(R.id.bt_publish);
        mBtPublish.setOnClickListener(this);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(this);
        mTvSelectedTopic = (RTextView) findViewById(R.id.tv_selected_topic);
        mTvSelectedTopic.setOnClickListener(this);
        imageLayout = (RecyclerView) findViewById(R.id.fragment_publish_images_show_ll);
        findViewById(R.id.iv_get_photo).setOnClickListener(this);
        findViewById(R.id.iv_get_video).setOnClickListener(this);
        //初始化9宫格,已在xml配置好
        adapter = new PublishImageShowAdapter();

        imageLayout.setHasFixedSize(true);
        imageLayout.addItemDecoration(new GridSpacingItemDecoration(3,
                ScreenUtils.dip2px(this, 12), false));
        imageLayout.setLayoutManager(new GridLayoutManager(this, 3));
        // 解决调用 notifyItemChanged 闪烁问题,取消默认动画
        imageLayout.getItemAnimator().setChangeDuration(0);
        adapter.setOnClickItemListener(new PublishImageShowAdapter.OnClickItemListener() {
            @Override
            public void onClickDelete(int position) {
            }

            @Override
            public void onClickAdd() {
                getPicture();
            }
        });
        imageLayout.setAdapter(adapter);
        imageLayout.setNestedScrollingEnabled(false);
        addEditTextListener();

        presenter = new PublishPresenter(this);
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
                if (str.length() <= 300) {
                    editLength.setText(String.format("%d/300", str.length()));
                } else {
                    ToastUtil.showShort("输入文字已达到上限！");
                }
            }
        });
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_publish;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_publish:
                presenter.publishBtClick();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_selected_topic:
                Intent intent = new Intent(this, SelectTopicActivity.class);
                startActivityForResult(intent, SELECTOR_TOPIC);
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
        presenter.getPicture();
    }


    private void getVideo() {
        presenter.getVideo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.REQUEST_VIDEO:
                    publishType = 2;
                    selectList = PictureSelector.obtainMultipleResult(data);
                    presenter.setMediaList(selectList);
                    adapter.setImagUrls(selectList, true);
                    break;
                case PictureConfig.REQUEST_PICTURE:
                    publishType = 1;
                    selectList = PictureSelector.obtainMultipleResult(data);
                    presenter.setMediaList(selectList);
                    adapter.setImagUrls(selectList, false);
                    break;
                //获取选择的话题
                case SELECTOR_TOPIC:
                    TopicItemBean date = data.getParcelableExtra(KEY_SELECTED_TOPIC);
                    mTvSelectedTopic.setText(date.getTagalias());
                    presenter.setTopicId(date.getTagid());
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
        // TODO: 2018/11/7 跳转到首页展示发布的进度,封装成bean对象传给首页展示
        EventBusHelp.sendPublishEvent(EventBusCode.pb_start, null);
        finish();
//        selectList.size() == 1 && PictureMimeType.isVideo(selectList.get(0).getPictureType())
    }

//    @Override
//    public void publishError() {
//
//    }
//
//    @Override
//    public void endPublish() {
//
//    }
}
