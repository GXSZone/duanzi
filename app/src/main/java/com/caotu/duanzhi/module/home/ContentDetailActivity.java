package com.caotu.duanzhi.module.home;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.ruffian.library.widget.REditText;
import com.ruffian.library.widget.RTextView;

/**
 * 内容详情页面
 */
public class ContentDetailActivity extends BaseActivity implements View.OnClickListener {

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
    private KeyBoardListener listener;

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
        listener = new KeyBoardListener();
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }

    public boolean keyBoardIsShowing = false;


    private void dealEditText() {
        //过滤多次回调
        if (keyBoardIsShowing) {
            if (mKeyboardShowRl.getVisibility() == View.VISIBLE)
                return;
            mIvDetailPhoto.setVisibility(View.GONE);
            mIvDetailVideo.setVisibility(View.GONE);
            mKeyboardShowRl.setVisibility(View.VISIBLE);
        } else {
            if (mKeyboardShowRl.getVisibility() == View.GONE)
                return;
            mIvDetailPhoto.setVisibility(View.VISIBLE);
            mIvDetailVideo.setVisibility(View.VISIBLE);
            mKeyboardShowRl.setVisibility(View.GONE);
        }
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

                break;

            case R.id.iv_detail_video:
            case R.id.iv_detail_video1:
                break;

            case R.id.tv_click_send:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (listener != null) {
            getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
        super.onDestroy();
    }

    public class KeyBoardListener implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();
            //获取当前界面可视部分
            getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            //获取屏幕的高度
            int screenHeight = getWindow().getDecorView().getRootView().getHeight();
            //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
            int heightDifference = screenHeight - r.bottom;
            Log.d("Keyboard Size", "Size: " + heightDifference);
            if (heightDifference > 250) {
                keyBoardIsShowing = true;
                dealEditText();
            } else if (heightDifference < 100) {
                keyBoardIsShowing = false;
                dealEditText();
            }
        }
    }
}
