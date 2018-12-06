package com.caotu.duanzhi.module.home;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.HelperForStartActivity;

/**
 * @author mac
 * @日期: 2018/11/15
 * @describe 评论详情是内容详情的子类, 处理不同的内容, 发布内容处理和其他都可以共用详情的逻辑
 */
public class CommentDetailActivity extends ContentDetailActivity {
    private CommentDetailFragment detailFragment;

    private CommendItemBean.RowsBean bean;

    public static HelperForStartActivity.ILikeAndUnlike listener;

    /**
     * 用于解决回调问题
     *
     * @param rowsBean
     * @param callBack
     */
    public static void openCommentDetail(CommendItemBean.RowsBean rowsBean, HelperForStartActivity.ILikeAndUnlike callBack) {
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        Intent intent = new Intent(runningActivity, CommentDetailActivity.class);
        intent.putExtra(HelperForStartActivity.KEY_DETAIL_COMMENT, rowsBean);
        listener = callBack;
        runningActivity.startActivity(intent);
    }

    @Override
    protected void initView() {
        super.initView();
        TextView title = findViewById(R.id.detail_title);
        title.setText("评论详情");
    }

    protected void getPresenter() {
        presenter = new SecondCommentReplyPresenter(this, bean);
    }

    /**
     * 具体显示都在fragment里
     */
    @Override
    public void initFragment() {
        bean = getIntent().getParcelableExtra(HelperForStartActivity.KEY_DETAIL_COMMENT);
        detailFragment = new CommentDetailFragment();
        detailFragment.setDate(bean);
        turnToFragment(null, detailFragment, R.id.fl_fragment_content);
    }

    public void setReplyUser(String commentid, String userId, String username) {
        if (presenter instanceof SecondCommentReplyPresenter) {
            ((SecondCommentReplyPresenter) presenter).setUserInfo(commentid, userId);
        }
        mEtSendContent.setHint("回复@" + username + ":");
        MyApplication.getInstance().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showKeyboard(mEtSendContent);
            }
        }, 150);
    }

    @Override
    protected void callbackFragment(CommendItemBean.RowsBean bean) {
        mEtSendContent.setText("");
        if (detailFragment != null) {
            detailFragment.publishComment(bean);
        }
    }
}
