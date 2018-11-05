package com.caotu.duanzhi.module.other;

import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.mine.fragment.FocusUserFragment;
import com.caotu.duanzhi.utils.HelperForStartActivity;

/**
 * 他人主页和话题详情和通知里多人点赞列表共用
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
        String id = getIntent().getStringExtra(HelperForStartActivity.key_user_id);

        if (HelperForStartActivity.type_other_user.equals(extra)) {
            line.setVisibility(View.GONE);
            mTvOtherUserName.setVisibility(View.VISIBLE);
            OtherUserFragment fragment = new OtherUserFragment();
            fragment.setDate(id);
            turnToFragment(null, fragment, R.id.fl_fragment_content);

        } else if (HelperForStartActivity.type_other_topic.equals(extra)) {
            line.setVisibility(View.VISIBLE);
            mTvOtherUserName.setVisibility(View.GONE);
            // TODO: 2018/11/1 话题详情页面

        } else if (HelperForStartActivity.type_other_praise.equals(extra)) {
            line.setVisibility(View.VISIBLE);
            mTvOtherUserName.setVisibility(View.VISIBLE);
            mTvOtherUserName.setText("点赞的人");
            // TODO: 2018/11/2 多人给你点赞页面

            FocusUserFragment fragment = new FocusUserFragment();
            fragment.setDate(id, true);
            turnToFragment(null, fragment, R.id.fl_fragment_content);
        }

    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_other;
    }

    /**
     * 给fragment设置标题用
     *
     * @param titleText
     */
    public void setTitleText(String titleText) {
        mTvOtherUserName.setText(titleText);
    }
}
