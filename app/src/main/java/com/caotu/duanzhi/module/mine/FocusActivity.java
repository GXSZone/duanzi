package com.caotu.duanzhi.module.mine;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.base.MyFragmentAdapter;
import com.caotu.duanzhi.module.mine.fragment.FocusTopicFragment;
import com.caotu.duanzhi.module.mine.fragment.FocusUserFragment;
import com.caotu.duanzhi.module.other.IndicatorHelper;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;

public class FocusActivity extends BaseActivity {

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        MagicIndicator indicator = findViewById(R.id.viewpager_indicator);
        ViewPager mViewPagerFocus = findViewById(R.id.view_pager_focus);
        IndicatorHelper.initFocusIndicator(this, mViewPagerFocus, indicator, IndicatorHelper.FOCUS);
        mViewPagerFocus.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), getFragmentList()));

    }

    private List<Fragment> getFragmentList() {
        String userId = getIntent().getStringExtra(HelperForStartActivity.key_user_id);
        boolean isMine = MySpUtils.isMe(userId);
        List<Fragment> fragments = new ArrayList<>();
        FocusTopicFragment topicFragment = new FocusTopicFragment();
        topicFragment.setDate(userId, isMine);
        fragments.add(topicFragment);

        FocusUserFragment userFragment = new FocusUserFragment();
        userFragment.setDate(userId, isMine);
        fragments.add(userFragment);
        return fragments;
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_focus;
    }
}
