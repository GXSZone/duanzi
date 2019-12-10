package com.caotu.duanzhi.module.search;

import android.view.View;

import androidx.fragment.app.Fragment;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.module.base.MyFragmentAdapter;
import com.caotu.duanzhi.module.other.IndicatorHelper;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.view.widget.SlipViewPager;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;

public class SearchParentFragment extends BaseFragment implements SearchDate {
    private SlipViewPager mViewPager;
    private List<Fragment> fragments = new ArrayList<>(4);
    private MagicIndicator magicIndicator;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_search_parent;
    }

    @Override
    protected void initDate() {
        if (!fragments.isEmpty()) fragments.clear();

        fragments.add(new SearchAllFragment());
        fragments.add(new SearchContentFragment());
        fragments.add(new SearchUserFragment());
        fragments.add(new SearchTopicFragment());

        //指示器的初始化
        IndicatorHelper.initIndicator(getContext(), mViewPager, magicIndicator, IndicatorHelper.SEARCH_TYPE);
        mViewPager.setAdapter(new MyFragmentAdapter(getChildFragmentManager(), fragments));
        //扩大viewpager的容量
        mViewPager.setOffscreenPageLimit(3);
    }

    @Override
    protected void initView(View inflate) {
        mViewPager = inflate.findViewById(R.id.viewpager);
        magicIndicator = inflate.findViewById(R.id.search_magic_indicator);
    }

    /**
     * 供综合头布局更多调用
     *
     * @param index
     */
    public void changeItem(int index) {
        mViewPager.setCurrentItem(index);
    }

    @Override
    public void setDate(String trim) {
        if (!AppUtil.listHasDate(fragments)) return;
        for (Fragment fragment : fragments) {
            if (fragment instanceof SearchDate) {
                ((SearchDate) fragment).setDate(trim);
            }
        }
        MySpUtils.putBeanToSp(trim);
    }
}
