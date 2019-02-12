package com.caotu.duanzhi.module.home;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.module.base.MyFragmentAdapter;
import com.caotu.duanzhi.module.home.fragment.IHomeRefresh;
import com.caotu.duanzhi.module.home.fragment.PhotoFragment;
import com.caotu.duanzhi.module.home.fragment.RecommendFragment;
import com.caotu.duanzhi.module.home.fragment.TextFragment;
import com.caotu.duanzhi.module.home.fragment.VideoFragment;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.view.widget.ColorFlipPagerTitleView;
import com.luck.picture.lib.widget.PreviewViewPager;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainHomeNewFragment extends BaseFragment {

    private static final String[] CHANNELS = new String[]{"推荐", "视频", "图片", "段子"};
    private List<String> mDataList = Arrays.asList(CHANNELS);

    private PreviewViewPager mViewPager;
    private List<Fragment> fragments = new ArrayList<>();
    private RecommendFragment recommendFragment;
    private TextView refresh_tip;
    private ImageView refreshBt;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_home_main;
    }

    @Override
    protected void initDate() {

    }

    public int getViewpagerCurrentIndex() {
        if (mViewPager != null) {
            return mViewPager.getCurrentItem();
        }
        return 0;
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
        refresh_tip = inflate.findViewById(R.id.tv_refresh_tip);
        refreshBt = inflate.findViewById(R.id.iv_refresh);
        refreshBt.setOnClickListener(v -> {
            refreshBt.animate().rotationBy(360 * 3).setDuration(700)
                    .setInterpolator(new AccelerateDecelerateInterpolator());
            refreshDate();
        });
        initMagicIndicator(inflate);
        mViewPager.setAdapter(new MyFragmentAdapter(getChildFragmentManager(), fragments));
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

    private void initMagicIndicator(View inflate) {
        MagicIndicator magicIndicator =  inflate.findViewById(R.id.magic_indicator6);
        CommonNavigator commonNavigator7 = new CommonNavigator(getContext());
        commonNavigator7.setAdjustMode(true);
        commonNavigator7.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorFlipPagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setNormalColor(DevicesUtils.getColor(R.color.color_cccccc));
                simplePagerTitleView.setSelectedColor(DevicesUtils.getColor(R.color.color_333333));
                simplePagerTitleView.setTextSize(18);
                simplePagerTitleView.getPaint().setFakeBoldText(true);
                simplePagerTitleView.setOnClickListener(v -> mViewPager.setCurrentItem(index));
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(UIUtil.dip2px(context, 3));
                indicator.setLineWidth(UIUtil.dip2px(context, 20));
                indicator.setRoundRadius(UIUtil.dip2px(context, 3));
                indicator.setStartInterpolator(new AccelerateInterpolator());
//                indicator.setYOffset(DevicesUtils.dp2px(8));
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(Color.parseColor("#FF698F"));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator7);
        ViewPagerHelper.bind(magicIndicator, mViewPager);
    }

    /**
     * 首页发布给第一个推荐页面
     *
     * @param dataBean
     */
    public void addPublishDate(MomentsDataBean dataBean) {
        mViewPager.setCurrentItem(0, false);
        if (recommendFragment != null) {
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

    /**
     * 滑动的详情调用再回调给
     *
     * @param callBack
     */
    public void loadMore(ILoadMore callBack) {
        //刷新当前页面,向上转型用接口
        // TODO: 2018/12/24 #9006 java.lang.IndexOutOfBoundsException  Index: 0, Size: 0 有点奇妙
        if (fragments == null || fragments.size() == 0) {
            callBack.loadMoreDate(null);
            return;
        }
        if (fragments.get(index) instanceof IHomeRefresh) {
            IHomeRefresh refresh = (IHomeRefresh) fragments.get(index);
            refresh.loadMore(callBack);
        }
    }

    private TranslateAnimation animationOut;

    public void showRefreshTip(int size) {
        if (refresh_tip != null) {
            refresh_tip.setText(String.format("发现了%d条新内容", size));
            refresh_tip.setVisibility(View.VISIBLE);
            handler.removeMessages(111);
            handler.sendEmptyMessageDelayed(111, 1000);
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 111) {
                goneRefreshTip();
            }
        }
    };

    public void goneRefreshTip() {
        if (animationOut == null) {
            animationOut = new TranslateAnimation(0, 0,
                    0, -refresh_tip.getMeasuredHeight());
            animationOut.setDuration(300);
            animationOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    refresh_tip.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        if (refresh_tip != null) {
            refresh_tip.startAnimation(animationOut);
//            refresh_tip.animate().translationY()
        }
    }

    public void getLoadMoreDate(ILoadMore callBack) {
        loadMore(callBack);
    }
}
