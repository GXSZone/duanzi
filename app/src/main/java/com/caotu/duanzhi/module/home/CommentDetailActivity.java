package com.caotu.duanzhi.module.home;

import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.HelperForStartActivity;

/**
 * @author mac
 * @日期: 2018/11/15
 * @describe 评论详情
 */
public class CommentDetailActivity extends ContentDetailActivity {
    private CommentDetailFragment detailFragment;

    @Override
    protected void initView() {
        super.initView();
        TextView title = findViewById(R.id.detail_title);
        title.setText("评论详情");
    }

    @Override
    public void initFragment() {
        CommendItemBean.RowsBean bean = getIntent().getParcelableExtra(HelperForStartActivity.KEY_DETAIL_COMMENT);
        boolean isToComment = getIntent().getBooleanExtra(HelperForStartActivity.KEY_TO_COMMENT, false);
        detailFragment = new CommentDetailFragment();
        detailFragment.setDate(bean);
        turnToFragment(null, detailFragment, R.id.fl_fragment_content);
    }
}
