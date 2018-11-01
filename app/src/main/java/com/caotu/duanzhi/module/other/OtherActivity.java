package com.caotu.duanzhi.module.other;

import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.utils.HelperForStartActivity;

/**
 * 他人主页和话题详情共用
 * 针对有列表有头布局的封装,只需要更换adapter就可以了
 */
public class OtherActivity extends BaseActivity {

    public TextView mTvOtherUserName;

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        mTvOtherUserName = findViewById(R.id.tv_other_user_name);
        View line = findViewById(R.id.view_line);
        String extra = getIntent().getStringExtra(HelperForStartActivity.key_other_type);
        BaseStateFragment fragment = null;
        if (HelperForStartActivity.type_other_user.equals(extra)) {
            line.setVisibility(View.GONE);
            // TODO: 2018/11/1 他人详情页面
        } else {
            line.setVisibility(View.VISIBLE);
            // TODO: 2018/11/1 话题详情页面
        }
        turnToFragment(null, fragment, R.id.fl_fragment_content);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_other;
    }
}
