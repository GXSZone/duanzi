package com.caotu.duanzhi.module.detail;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseSwipeActivity;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.module.publish.PublishPresenter;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.other.TextWatcherAdapter;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.SoftKeyBoardListener;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.dialog.PictureDialog;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.REditText;
import com.ruffian.library.widget.RTextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 内容详情页面
 */

public class ContentDetailActivity extends BaseSwipeActivity implements View.OnClickListener, IVewPublishComment {


    public REditText mEtSendContent;
    public View bottomLikeView, bottomCollection, bottomShareView;

    private RTextView mTvClickSend;
    public RelativeLayout mKeyboardShowRl;
    public PublishPresenter presenter;

    private RecyclerView recyclerView;
    private ContentDetailFragment detailFragment;
    protected MomentsDataBean bean;
    private String contentId;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_content_detail;
    }

    public TextView getBottomLikeView() {
        return (TextView) bottomLikeView;
    }

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        mEtSendContent = findViewById(R.id.et_send_content);

        findViewById(R.id.iv_detail_photo1).setOnClickListener(this);
        findViewById(R.id.iv_detail_video1).setOnClickListener(this);

        bottomLikeView = findViewById(R.id.bottom_tv_like);
        bottomLikeView.setOnClickListener(this);
        bottomCollection = findViewById(R.id.bottom_iv_collection);
        bottomCollection.setOnClickListener(this);
        bottomShareView = findViewById(R.id.bottom_iv_share);
        bottomShareView.setOnClickListener(this);

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
        getIntentDate();
        setKeyBoardListener();
    }

    protected void getPresenter() {
        presenter = new CommentReplyPresenter(this, bean);
    }

//    public void setPresenter(MomentsDataBean date) {
//        if (presenter != null && presenter instanceof CommentReplyPresenter) {
//            ((CommentReplyPresenter) presenter).setByOnlyIdDate(date);
//        }
//    }

    public void getIntentDate() {
        contentId = getIntent().getStringExtra("contentId");
        bean = getIntent().getParcelableExtra(HelperForStartActivity.KEY_CONTENT);
        if (bean == null) {
            getDetailDate();
        } else {
            detailFragment = new ContentDetailFragment();
            detailFragment.setDate(bean);
            turnToFragment(null, detailFragment, R.id.fl_fragment_content);
            if (bean != null) {
                bottomCollection.setSelected(LikeAndUnlikeUtil.isLiked(bean.getIscollection()));
            }
            getPresenter();
        }
        if (TextUtils.isEmpty(contentId) && bean != null) {
            contentId = bean.getContentid();
        }
    }

    private void getDetailDate() {
        if (TextUtils.isEmpty(contentId)) return;
        //用于通知跳转
        HashMap<String, String> hashMapParams = new HashMap<>();
        hashMapParams.put("contentid", contentId);
        OkGo.<BaseResponseBean<MomentsDataBean>>post(HttpApi.WORKSHOW_DETAILS)
                .upJson(new JSONObject(hashMapParams))
                .tag(this)
                .execute(new JsonCallback<BaseResponseBean<MomentsDataBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<MomentsDataBean>> response) {
                        MomentsDataBean data = DataTransformUtils.getContentNewBean(response.body().getData());
                        if (data == null) {
                            ToastUtil.showShort("该内容已不存在");
                            finish();
                            return;
                        }
                        bean = data;
                        detailFragment = new ContentDetailFragment();
                        detailFragment.setDate(bean);
                        turnToFragment(null, detailFragment, R.id.fl_fragment_content);
                        if (bean != null) {
                            bottomCollection.setSelected(LikeAndUnlikeUtil.isLiked(bean.getIscollection()));
                        }
                        getPresenter();
                    }
                });
    }

    /**
     * 用于推送下来打开详情使用
     *
     * @return
     */
    public String getContentId() {
        return contentId;
    }

    public void setKeyBoardListener() {
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                bottomLikeView.setVisibility(View.GONE);
                bottomCollection.setVisibility(View.GONE);
                bottomShareView.setVisibility(View.GONE);
                mKeyboardShowRl.setVisibility(View.VISIBLE);
                mEtSendContent.setMaxLines(4);
            }

            @Override
            public void keyBoardHide() {
                bottomLikeView.setVisibility(View.VISIBLE);
                bottomCollection.setVisibility(View.VISIBLE);
                bottomShareView.setVisibility(View.VISIBLE);
                mKeyboardShowRl.setVisibility(View.GONE);
                mEtSendContent.setMaxLines(1);
            }
        });
    }

    public void bottomShareBt() {
        if (bean == null) return;
        String copyText = null;
        if ("1".equals(bean.getIsshowtitle()) && !TextUtils.isEmpty(bean.getContenttitle())) {
            copyText = bean.getContenttitle();
        }
        boolean isVideo = LikeAndUnlikeUtil.isVideoType(bean.getContenttype());
        WebShareBean webBean = ShareHelper.getInstance().createWebBean(isVideo
                , bean.getIscollection(), VideoAndFileUtils.getVideoUrl(bean.getContenturllist()),
                bean.getContentid(), copyText);
        showShareDialog(webBean, CommonHttpRequest.url, null, bean);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.bottom_iv_share:
                bottomShareBt();
                break;
            case R.id.bottom_iv_collection:
                if (bean == null) return;
                boolean isCollection = "0".equals(bean.getIscollection());
                if (isCollection) {
                    UmengHelper.event(UmengStatisticsKeyIds.collection);
                }
                if (LoginHelp.isLoginAndSkipLogin() && !TextUtils.isEmpty(contentId)) {
                    CommonHttpRequest.getInstance().collectionContent(contentId, isCollection, new JsonCallback<BaseResponseBean<String>>() {
                        @Override
                        public void onSuccess(Response<BaseResponseBean<String>> response) {
                            bean.setIscollection(isCollection ? "1" : "0");
                            ToastUtil.showShort(isCollection ? "收藏成功" : "取消收藏成功");
                            bottomCollection.setSelected(isCollection);
                        }
                    });
                }
                break;
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

    public void showShareDialog(WebShareBean shareBean, String shareUrl, CommendItemBean.RowsBean itemBean, MomentsDataBean momentsDataBean) {
        ShareDialog dialog = ShareDialog.newInstance(shareBean);
        dialog.setListener(new ShareDialog.ShareMediaCallBack() {
            @Override
            public void callback(WebShareBean bean) {
                if (momentsDataBean == null) return;
                String cover = VideoAndFileUtils.getCover(momentsDataBean.getContenturllist());
                WebShareBean shareBeanByDetail = ShareHelper.getInstance().getShareBeanByDetail(bean, momentsDataBean, cover, shareUrl);
                ShareHelper.getInstance().shareWeb(shareBeanByDetail);
            }

            @Override
            public void colloection(boolean isCollection) {
                if (bean == null) return;
                bean.setIscollection(isCollection ? "1" : "0");
                bottomCollection.setSelected(isCollection);
                ToastUtil.showShort(isCollection ? "收藏成功" : "取消收藏成功");
            }
        });
        dialog.show(getSupportFragmentManager(), "share");
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
