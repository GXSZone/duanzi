package com.caotu.duanzhi.module.mine;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.mine.fragment.FocusTopicFragment;
import com.caotu.duanzhi.module.mine.fragment.FocusUserFragment;
import com.caotu.duanzhi.module.other.BasePagerAdapter;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;

import java.util.ArrayList;
import java.util.List;

public class FocusActivity extends BaseActivity {

    static List<String> titleArr;

    static {
        titleArr = new ArrayList<>();
        titleArr.add("话题");
        titleArr.add("用户");
    }

    @Override
    protected void initView() {
        TabLayout mTabLayoutFocus = (TabLayout) findViewById(R.id.tab_layout_focus);
        ViewPager mViewPagerFocus = (ViewPager) findViewById(R.id.view_pager_focus);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mViewPagerFocus.setAdapter(new BasePagerAdapter(getSupportFragmentManager(), getFragmentList(), titleArr));
        mTabLayoutFocus.setupWithViewPager(mViewPagerFocus);

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
