package com.caotu.duanzhi.module.home;

import android.os.Bundle;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;

/**
 * 内容详情页面
 */
public class ContentDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_detail);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_content_detail;
    }
}
