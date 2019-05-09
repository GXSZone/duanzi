package com.caotu.duanzhi.module.detail;

import android.text.TextUtils;
import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.ToastUtil;

/**
 * @author mac
 * @日期: 2018/11/20
 * @describe ugc 内容详情.展示跟评论详情一样,但是接口请求都跟内容详情的一样
 */
public class UgcDetailActivity extends ContentDetailActivity {

    private UgcContentFragment detailFragment;

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
        bean = getIntent().getParcelableExtra(HelperForStartActivity.KEY_CONTENT);
        detailFragment = new UgcContentFragment();
        if (bean == null || TextUtils.isEmpty(bean.getContentid())) {
            ToastUtil.showShort("对象传递有误");
            return;
        }
        detailFragment.setDate(bean, false, 0);
        turnToFragment(null, detailFragment, R.id.fl_fragment_content);
    }

    protected void callbackFragment(CommendItemBean.RowsBean bean) {
        mEtSendContent.setText("");
        if (detailFragment != null) {
            detailFragment.publishComment(bean);
        }
    }
}
