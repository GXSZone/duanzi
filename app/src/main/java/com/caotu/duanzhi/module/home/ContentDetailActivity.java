package com.caotu.duanzhi.module.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.UmengHelper;
import com.caotu.duanzhi.UmengStatisticsKeyIds;
import com.caotu.duanzhi.module.TextWatcherAdapter;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.module.publish.PublishPresenter;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.SoftKeyBoardListener;
import com.caotu.duanzhi.utils.ToastUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.dialog.PictureDialog;
import com.luck.picture.lib.entity.LocalMedia;
import com.ruffian.library.widget.REditText;
import com.ruffian.library.widget.RTextView;
import com.sunfusheng.GlideImageView;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdMgr;

/**
 * 内容详情页面
 */
public class ContentDetailActivity extends BaseActivity implements View.OnClickListener, IVewPublishComment {

    /**
     * 期待你的神评论
     */
    public REditText mEtSendContent;
    private ImageView mIvDetailPhoto;
    private ImageView mIvDetailVideo;
    /**
     * 发布
     */
    private RTextView mTvClickSend;
    private RelativeLayout mKeyboardShowRl;
    public PublishPresenter presenter;

    private RecyclerView recyclerView;
    private ContentDetailFragment detailFragment;
    protected MomentsDataBean bean;
    private String contentId;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_content_detail;
    }

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        mEtSendContent = findViewById(R.id.et_send_content);
        mIvDetailPhoto = findViewById(R.id.iv_detail_photo);
        mIvDetailPhoto.setOnClickListener(this);
        mIvDetailVideo = findViewById(R.id.iv_detail_video);
        mIvDetailVideo.setOnClickListener(this);

        findViewById(R.id.iv_detail_photo1).setOnClickListener(this);
        findViewById(R.id.iv_detail_video1).setOnClickListener(this);

        mTvClickSend = findViewById(R.id.tv_click_send);
        mTvClickSend.setOnClickListener(this);

        mEtSendContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0 && !mTvClickSend.isEnabled()) {
                    mTvClickSend.setEnabled(true);
                } else if (s.toString().trim().length() == 0
                        && (selectList == null || selectList.size() == 0)) {
                    mTvClickSend.setEnabled(false);
                }
            }
        });
        mKeyboardShowRl = findViewById(R.id.keyboard_show_rl);
        recyclerView = findViewById(R.id.publish_rv);
        initFragment();

        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        setKeyBoardListener();
        getPresenter();
    }

    protected void getPresenter() {
        presenter = new CommentReplyPresenter(this, bean);
    }

    public void setPresenter(MomentsDataBean date) {
        if (presenter != null && presenter instanceof CommentReplyPresenter) {
            ((CommentReplyPresenter) presenter).setByOnlyIdDate(date);
        }
    }

    public void initFragment() {
        int videoProgress = getIntent().getIntExtra(HelperForStartActivity.KEY_VIDEO_PROGRESS, 0);
        contentId = getIntent().getStringExtra("contentId");
        bean = getIntent().getParcelableExtra(HelperForStartActivity.KEY_CONTENT);
        boolean isToComment = getIntent().getBooleanExtra(HelperForStartActivity.KEY_TO_COMMENT, false);
        detailFragment = new ContentDetailFragment();
        detailFragment.setDate(bean, isToComment, videoProgress);
        turnToFragment(null, detailFragment, R.id.fl_fragment_content);
    }

    /**
     * 用于推送下来打开详情使用
     *
     * @return
     */
    public String getContentId() {
        return contentId;
    }

    private void setKeyBoardListener() {
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                mIvDetailPhoto.setVisibility(View.GONE);
                mIvDetailVideo.setVisibility(View.GONE);
                mKeyboardShowRl.setVisibility(View.VISIBLE);
                mEtSendContent.setMaxLines(4);
            }

            @Override
            public void keyBoardHide() {
                mIvDetailPhoto.setVisibility(View.VISIBLE);
                mIvDetailVideo.setVisibility(View.VISIBLE);
                mKeyboardShowRl.setVisibility(View.GONE);
                mEtSendContent.setMaxLines(1);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_detail_photo:
            case R.id.iv_detail_photo1:
                if (selectList.size() != 0 && publishType != -1 && publishType == 2) {
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setMessage("若你要添加图片，已选视频将从发表界面中清除了？")
                            .setPositiveButton(android.R.string.ok, (dialog13, which) -> {
                                dialog13.dismiss();
                                selectList.clear();
                                recyclerView.setVisibility(View.GONE);
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
            case R.id.iv_detail_video:
            case R.id.iv_detail_video1:
                if (selectList.size() != 0 && publishType != -1 && publishType == 1) {
                    AlertDialog dialog = new AlertDialog.Builder(this).setMessage("若你要添加视频，已选图片将从发表界面中清除了？")
                            .setPositiveButton(android.R.string.ok, (dialog12, which) -> {
                                dialog12.dismiss();
                                selectList.clear();
                                recyclerView.setVisibility(View.GONE);
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
            case R.id.tv_click_send:
                UmengHelper.event(UmengStatisticsKeyIds.comment_publish);
                //为了防止已经在发布内容视频,再在评论里发布视频处理不过来
                Activity lastSecondActivity = MyApplication.getInstance().getLastSecondActivity();
                if (lastSecondActivity instanceof MainActivity) {
                    boolean publishing = ((MainActivity) lastSecondActivity).isPublishing();
                    if (publishing) {
                        ToastUtil.showShort("正在发布内容中,请稍后再试");
                        return;
                    }
                }

                if (LoginHelp.isLoginAndSkipLogin()) {
                    presenter.publishBtClick();
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                break;
        }
    }

    //目前有:纯图片,纯视频,纯文字,视频加文字,图片加文字
    //       1     2     3       4        5
    private int publishType = -1;

    public void getPicture() {
        UmengHelper.event(UmengStatisticsKeyIds.reply_image);
        if (presenter == null) return;
        presenter.getPicture();
    }

    private void getVideo() {
        UmengHelper.event(UmengStatisticsKeyIds.reply_video);
        if (presenter == null) return;
        presenter.getVideo();
    }

    private List<LocalMedia> selectList = new ArrayList<>();
    ContentItemAdapter adapter;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.REQUEST_VIDEO:
                    publishType = 2;
                    selectList = PictureSelector.obtainMultipleResult(data);
                    presenter.setMediaList(selectList);
                    presenter.setIsVideo(true);
                    showRV();
                    break;
                case PictureConfig.REQUEST_PICTURE:
                    publishType = 1;
                    selectList = PictureSelector.obtainMultipleResult(data);
                    presenter.setMediaList(selectList);
                    presenter.setIsVideo(false);
                    showRV();
                    break;
            }
        }
    }

    private void showRV() {
        mTvClickSend.setEnabled(true);
        if (recyclerView != null && recyclerView.getVisibility() != View.VISIBLE) {
            recyclerView.setVisibility(View.VISIBLE);
        }

        if (adapter == null) {
            adapter = new ContentItemAdapter();
            adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    adapter.remove(position);
                    if (adapter.getData().size() == 0) {
                        recyclerView.setVisibility(View.GONE);
                    }
                    presenter.setMediaList(adapter.getData());
                    adapter.getData();
                    if (adapter.getData().size() == 0) {
                        if (TextUtils.isEmpty(mEtSendContent.getText().toString().trim())) {
                            mTvClickSend.setEnabled(false);
                        }
                    }
                }
            });
            adapter.setOnItemClickListener((adapter, view, position) -> {
                LocalMedia localMedia = selectList.get(position);
                boolean isVideo = PictureMimeType.isVideo(localMedia.getPictureType());
                if (isVideo) {
                    PictureSelector.create(ContentDetailActivity.this)
                            .externalPictureVideo(localMedia.getPath());
                } else {
                    if (DevicesUtils.isOppo()) {
                        PictureSelector.create(MyApplication.getInstance().getRunningActivity())
                                .themeStyle(R.style.picture_default_style).openExternalPreview(position, selectList);
                    } else {
                        PictureSelector.create(MyApplication.getInstance().getRunningActivity())
                                .themeStyle(R.style.picture_QQ_style).openExternalPreview(position, selectList);
                    }
                }
            });
            recyclerView.setAdapter(adapter);
        }
        adapter.setNewData(selectList);
        mEtSendContent.postDelayed(() -> showKeyboard(mEtSendContent), 200);

    }

    @Override
    protected void onDestroy() {
        if (presenter != null) {
            presenter.destory();
        }
        super.onDestroy();
    }

    @Override
    public EditText getEditView() {
        return mEtSendContent;
    }

    @Override
    public View getPublishView() {
        return mTvClickSend;
    }

    ProgressDialog dialog;

    @Override
    public void startPublish() {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setMax(100);
            dialog.setCancelable(false);
            dialog.setMessage("预备发射中...");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }
        if (mp4Dialog != null && mp4Dialog.isShowing()) {
            mp4Dialog.dismiss();
        }
        mTvClickSend.setEnabled(false);
        dialog.show();
        closeSoftKeyboard();
    }

    @Override
    public void publishError() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        mTvClickSend.setEnabled(false);
        presenter.clearSelectList();
        selectList.clear();
        recyclerView.setVisibility(View.GONE);
        ToastUtil.showShort("发布失败");
        closeSoftKeyboard();
    }

    @Override
    public void endPublish(CommendItemBean.RowsBean bean) {
        UmengHelper.event(UmengStatisticsKeyIds.comment_success);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        ToastUtil.showShort("发射成功");
        mTvClickSend.setEnabled(false);
        presenter.clearSelectList();
        selectList.clear();
        recyclerView.setVisibility(View.GONE);
        callbackFragment(bean);
        closeSoftKeyboard();
    }

    @Override
    public void publishCantTalk(String msg) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
//        mTvClickSend.setEnabled(false);
        presenter.clearSelectList();
        selectList.clear();
        recyclerView.setVisibility(View.GONE);
        ToastUtil.showShort(msg);
        closeSoftKeyboard();
    }

    @Override
    public void uploadProgress(int progress) {
        Log.i("commentProgress", "uploadProgress: " + progress);
        if (dialog != null && dialog.isShowing()) {
            dialog.setProgress(progress);
        }
    }

    /**
     * 回调给fragment的adapter
     *
     * @param bean
     */
    protected void callbackFragment(CommendItemBean.RowsBean bean) {
        mEtSendContent.setText("");
        if (detailFragment != null) {
            detailFragment.publishComment(bean);
        }
    }

    /*
     *发表内容的adapter
     */
    class ContentItemAdapter extends BaseQuickAdapter<LocalMedia, BaseViewHolder> {

        public ContentItemAdapter() {
            super(R.layout.item_publish_detail);
        }

        @Override
        protected void convert(BaseViewHolder helper, LocalMedia item) {
            helper.addOnClickListener(R.id.item_publish_normal_delete_iv);
            GlideImageView imageView = helper.getView(R.id.item_publish_normal_giv);
            String url;
            //判断是否是视频
            boolean isVideo = PictureMimeType.isVideo(item.getPictureType());
            if (isVideo) {
                url = item.getPath();
                helper.setGone(R.id.item_publish_normal_play_iv, true);
            } else {
                url = item.getCompressPath();
                helper.setGone(R.id.item_publish_normal_play_iv, false);
            }
            imageView.load(url, R.drawable.image_placeholder);
        }
    }


    /**
     * 处理返回键的问题
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //一开始想着搞成静态变量,发现有bug,还是得照着demo的获取方式才可以
            if (JzvdMgr.getCurrentJzvd() != null && JzvdMgr.getCurrentJzvd().currentScreen == Jzvd.SCREEN_WINDOW_TINY) {
                Jzvd.backPress();
                finish();
                return true;
            } else if (Jzvd.backPress()) {
                return true;
            }
            return super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    PictureDialog mp4Dialog;

    @Override
    public void notMp4() {
        if (mp4Dialog == null) {
            mp4Dialog = new PictureDialog(this);
            mp4Dialog.setCanceledOnTouchOutside(false);
            mp4Dialog.setCancelable(false);
        }
        mTvClickSend.setEnabled(false);
        mp4Dialog.show();
        closeSoftKeyboard();
    }
}
