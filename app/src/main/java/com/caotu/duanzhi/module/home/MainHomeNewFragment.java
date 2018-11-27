package com.caotu.duanzhi.module.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.module.base.MyFragmentAdapter;
import com.caotu.duanzhi.module.home.fragment.IHomeRefresh;
import com.caotu.duanzhi.module.home.fragment.RecommendFragment;
import com.caotu.duanzhi.module.home.fragment.PhotoFragment;
import com.caotu.duanzhi.module.home.fragment.TextFragment;
import com.caotu.duanzhi.module.home.fragment.VideoFragment;
import com.caotu.duanzhi.view.widget.ScaleTransitionPagerTitleView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.BezierPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainHomeNewFragment extends BaseFragment {

    private static final String[] CHANNELS = new String[]{"推荐", "视频", "图片", "段子"};
    private List<String> mDataList = Arrays.asList(CHANNELS);

    private ViewPager mViewPager;
    private List<Fragment> fragments = new ArrayList<>();
    private RecommendFragment recommendFragment;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_home_main;
    }

    @Override
    protected void initDate() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recommendFragment = new RecommendFragment();
        fragments.add(recommendFragment);
        fragments.add(new VideoFragment());
        fragments.add(new PhotoFragment());
        fragments.add(new TextFragment());
    }

    @Override
    protected void initView(View inflate) {
        mViewPager = inflate.findViewById(R.id.viewpager);
        MagicIndicator magicIndicator = (MagicIndicator) inflate.findViewById(R.id.magic_indicator6);
        mViewPager.setAdapter(new MyFragmentAdapter(getChildFragmentManager(), fragments));
//        magicIndicator.setBackgroundColor(Color.WHITE);

        CommonNavigator commonNavigator = new CommonNavigator(getContext());
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setTextSize(20);
                simplePagerTitleView.setNormalColor(Color.parseColor("#BBBCCD"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#FF698F"));

                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                BezierPagerIndicator indicator = new BezierPagerIndicator(context);
                indicator.setColors(Color.parseColor("#FF698F"));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                index = position;
            }
        });
        //扩大viewpager的容量
        mViewPager.setOffscreenPageLimit(3);
    }

    int index = 0;

    /**
     * 首页发布给第一个推荐页面
     *
     * @param dataBean
     */
    public void addPublishDate(MomentsDataBean dataBean) {
        if (index != 0 && recommendFragment != null) {
            recommendFragment.addPublishDate(dataBean);
        }
    }

    /**
     * 目前该四个fragment都是实现了IHomeRefresh 接口的,所以可以向上转型
     */
    public void refreshDate() {
        //刷新当前页面,向上转型用接口
        if (fragments.get(index) instanceof IHomeRefresh) {
            IHomeRefresh refresh = (IHomeRefresh) fragments.get(index);
            refresh.refreshDate();
        }
    }
}