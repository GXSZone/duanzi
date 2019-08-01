package com.caotu.duanzhi.module.detail;

import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.publish.PublishPresenter;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.SoftKeyBoardListener;

/**
 * @author mac
 * @日期: 2018/11/15
 * @describe 评论详情是内容详情的子类, 处理不同的内容, 发布内容处理和其他都可以共用详情的逻辑
 */

public class CommentDetailActivity extends ContentDetailActivity {
    private CommentDetailFragment detailFragment;

    private CommendItemBean.RowsBean bean;

    @Override
    protected void initView() {
        super.initView();
        TextView title = findViewById(R.id.detail_title);
        title.setText("评论详情");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomCollection != null) {
            bottomCollection.setVisibility(View.GONE);
        }
    }

    public void setKeyBoardListener() {
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                bottomLikeView.setVisibility(View.GONE);
                bottomShareView.setVisibility(View.GONE);
                mKeyboardShowRl.setVisibility(View.VISIBLE);
                mEtSendContent.setMaxLines(4);
            }

            @Override
            public void keyBoardHide() {
                bottomLikeView.setVisibility(View.VISIBLE);
                bottomShareView.setVisibility(View.VISIBLE);
                mKeyboardShowRl.setVisibility(View.GONE);
                mEtSendContent.setMaxLines(1);
            }
        });
    }

    protected PublishPresenter getPresenter() {
        if (presenter == null) {
            presenter = new SecondCommentReplyPresenter(this, bean);
        }
        return presenter;
    }

    /**
     * 具体显示都在fragment里
     */
    @Override
    public void getIntentDate() {
        bean = getIntent().getParcelableExtra(HelperForStartActivity.KEY_DETAIL_COMMENT);
        detailFragment = new CommentDetailFragment();
        detailFragment.setDate(bean);
        turnToFragment(detailFragment, R.id.fl_fragment_content);
        getPresenter();
    }

    public void setReplyUser(String commentid, String userId, String username) {
        if (presenter instanceof SecondCommentReplyPresenter) {
            ((SecondCommentReplyPresenter) presenter).setUserInfo(commentid, userId);
        }
        mEtSendContent.setHint("回复@" + username + ":");
        mEtSendContent.postDelayed(() -> showKeyboard(mEtSendContent), 150);
    }

    @Override
    protected void callbackFragment(CommendItemBean.RowsBean bean) {
        mEtSendContent.setText("");
        if (detailFragment != null) {
            detailFragment.publishComment(bean);
        }
    }

    @Override
    public void bottomShareBt() {
        detailFragment.share();
    }
}
