package com.caotu.duanzhi.module.home;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.SlideCloseHelper;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;

/**
 * @author mac
 * @日期: 2018/11/15
 * @describe 评论详情是内容详情的子类, 处理不同的内容, 发布内容处理和其他都可以共用详情的逻辑
 */
public class CommentDetailActivity extends ContentDetailActivity {
    private CommentDetailFragment detailFragment;

    private CommendItemBean.RowsBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SlideCloseHelper.getInstance().initSlideBackClose(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        TextView title = findViewById(R.id.detail_title);
        title.setText("评论详情");
        /*
        为了处理加了侧滑返回后,底部的输入框被底部导航栏遮挡的问题
         */
        LinearLayout bottomView = findViewById(R.id.ll_bottom_publish);
        bottomView.post(new Runnable() {
            @Override
            public void run() {
                int navigationBarHeight = DevicesUtils.getNavigationBarHeight(bottomView.getContext());
                try {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bottomView.getLayoutParams();
                    layoutParams.bottomMargin += navigationBarHeight;
                    bottomView.setLayoutParams(layoutParams);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
        mEtSendContent.postDelayed(() -> showKeyboard(mEtSendContent), 150);
    }

    @Override
    protected void callbackFragment(CommendItemBean.RowsBean bean) {
        mEtSendContent.setText("");
        if (detailFragment != null) {
            detailFragment.publishComment(bean);
        }
    }
}
