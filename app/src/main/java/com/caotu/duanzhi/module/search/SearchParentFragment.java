package com.caotu.duanzhi.module.search;

import android.view.View;

import androidx.fragment.app.Fragment;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.module.base.MyFragmentAdapter;
import com.caotu.duanzhi.module.home.fragment.PhotoFragment;
import com.caotu.duanzhi.module.home.fragment.TextFragment;
import com.caotu.duanzhi.module.home.fragment.VideoFragment;
import com.caotu.duanzhi.module.other.IndicatorHelper;
import com.caotu.duanzhi.view.widget.SlipViewPager;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;

public class SearchParentFragment extends BaseFragment {
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


        fragments.add(new VideoFragment());
        fragments.add(new VideoFragment());
        fragments.add(new PhotoFragment());
        fragments.add(new TextFragment());

        //指示器的初始化
        IndicatorHelper.initFocusIndicator(getContext(), mViewPager, magicIndicator, IndicatorHelper.SEARCH_TYPE);
        mViewPager.setAdapter(new MyFragmentAdapter(getChildFragmentManager(), fragments));
        //扩大viewpager的容量
        mViewPager.setOffscreenPageLimit(3);
    }

    @Override
    protected void initView(View inflate) {
        mViewPager = inflate.findViewById(R.id.viewpager);
        magicIndicator = rootView.findViewById(R.id.magic_indicator6);
    }
}
