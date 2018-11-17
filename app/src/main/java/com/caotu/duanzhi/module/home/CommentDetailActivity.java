package com.caotu.duanzhi.module.home;

import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.HelperForStartActivity;

/**
 * @author mac
 * @日期: 2018/11/15
 * @describe 评论详情是内容详情的子类,处理不同的内容,发布内容处理和其他都可以共用详情的逻辑
 */
public class CommentDetailActivity extends ContentDetailActivity {
    private CommentDetailFragment detailFragment;

    @Override
    protected void initView() {
        super.initView();
        TextView title = findViewById(R.id.detail_title);
        title.setText("评论详情");
    }

    /**
     * 具体显示都在fragment里
     */
    @Override
    public void initFragment() {
        CommendItemBean.RowsBean bean = getIntent().getParcelableExtra(HelperForStartActivity.KEY_DETAIL_COMMENT);
        detailFragment = new CommentDetailFragment();
        detailFragment.setDate(bean);
        turnToFragment(null, detailFragment, R.id.fl_fragment_content);
    }

    public void setReplyUser(String userName,String userId) {

    }

    @Override
    protected void callbackFragment(CommendItemBean.RowsBean bean) {
        if (detailFragment != null) {
            detailFragment.publishComment(bean);
        }
    }
}
