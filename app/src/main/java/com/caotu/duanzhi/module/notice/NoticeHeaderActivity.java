package com.caotu.duanzhi.module.notice;

import android.os.Bundle;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.base.SlideCloseHelper;

/**
 * 通知头布局的跳转过来页面
 */
public class NoticeHeaderActivity extends BaseActivity {

    @Override
    protected int getLayoutView() {
        return R.layout.activity_notice_header;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SlideCloseHelper.getInstance().initSlideBackClose(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        TextView titleText = findViewById(R.id.tv_other_user_name);

    }


}
