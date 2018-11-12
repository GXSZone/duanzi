package com.caotu.duanzhi.module.home;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.publish.PublishPresenter;
import com.caotu.duanzhi.module.publish.publishView;
import com.caotu.duanzhi.utils.SoftKeyBoardListener;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.ruffian.library.widget.REditText;
import com.ruffian.library.widget.RTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 内容详情页面
 */
public class ContentDetailActivity extends BaseActivity implements View.OnClickListener, publishView {

    private ImageView mIvBack;
    private FrameLayout mFlFragmentContent;
    /**
     * 期待你的神评论
     */
    private REditText mEtSendContent;
    private ImageView mIvDetailPhoto;
    private ImageView mIvDetailVideo;
    private ImageView mIvDetailPhoto1;
    private ImageView mIvDetailVideo1;
    /**
     * 发布
     */
    private RTextView mTvClickSend;
    private RelativeLayout mKeyboardShowRl;
    private PublishPresenter presenter;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_content_detail;
    }

    @Override
    protected void initView() {
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(this);
        mFlFragmentContent = (FrameLayout) findViewById(R.id.fl_fragment_content);
        mEtSendContent = (REditText) findViewById(R.id.et_send_content);
        mIvDetailPhoto = (ImageView) findViewById(R.id.iv_detail_photo);
        mIvDetailPhoto.setOnClickListener(this);
        mIvDetailVideo = (ImageView) findViewById(R.id.iv_detail_video);
        mIvDetailVideo.setOnClickListener(this);
        mIvDetailPhoto1 = (ImageView) findViewById(R.id.iv_detail_photo1);
        mIvDetailPhoto1.setOnClickListener(this);
        mIvDetailVideo1 = (ImageView) findViewById(R.id.iv_detail_video1);
        mIvDetailVideo1.setOnClickListener(this);
        mTvClickSend = (RTextView) findViewById(R.id.tv_click_send);
        mTvClickSend.setOnClickListener(this);
        mKeyboardShowRl = (RelativeLayout) findViewById(R.id.keyboard_show_rl);
        setKeyBoardListener();
        presenter = new PublishPresenter(this);
    }


    private void setKeyBoardListener() {
        SoftKeyBoardListener.setListener(getWindow().getDecorView(), new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                mIvDetailPhoto.setVisibility(View.GONE);
                mIvDetailVideo.setVisibility(View.GONE);
                mKeyboardShowRl.setVisibility(View.VISIBLE);
            }

            @Override
            public void keyBoardHide(int height) {
                mIvDetailPhoto.setVisibility(View.VISIBLE);
                mIvDetailVideo.setVisibility(View.VISIBLE);
                mKeyboardShowRl.setVisibility(View.GONE);
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
                presenter.getPicture();
                break;
            case R.id.iv_detail_video:
            case R.id.iv_detail_video1:
                presenter.getVideo();
                break;

            case R.id.tv_click_send:
                break;
        }
    }

    private List<LocalMedia> selectList = new ArrayList<>();

    //目前有:纯图片,纯视频,纯文字,视频加文字,图片加文字
    //       1     2     3       4        5
    private int publishType = -1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.REQUEST_VIDEO:
                    publishType = 2;
                    selectList = PictureSelector.obtainMultipleResult(data);
                    presenter.setMediaList(selectList);
//                    adapter.setImagUrls(selectList, true);
                    break;
                case PictureConfig.REQUEST_PICTURE:
                    publishType = 1;
                    selectList = PictureSelector.obtainMultipleResult(data);
                    presenter.setMediaList(selectList);
//                    adapter.setImagUrls(selectList, false);
                    break;
            }
        }
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

    @Override
    public void startPublish() {

    }
}
