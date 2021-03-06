package com.caotu.duanzhi.module.home;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.module.base.MyFragmentAdapter;
import com.caotu.duanzhi.module.detail.ILoadMore;
import com.caotu.duanzhi.module.home.fragment.IHomeRefresh;
import com.caotu.duanzhi.module.home.fragment.PhotoFragment;
import com.caotu.duanzhi.module.home.fragment.RecommendFragment;
import com.caotu.duanzhi.module.home.fragment.TextFragment;
import com.caotu.duanzhi.module.home.fragment.VideoFragment;
import com.caotu.duanzhi.module.other.IndicatorHelper;
import com.caotu.duanzhi.other.FastClickListener;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.view.widget.SlipViewPager;
import com.dueeeke.videoplayer.player.VideoViewManager;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;


/**
 * @author mac
 */
public class MainHomeNewFragment extends BaseFragment implements ITabRefresh {

    private SlipViewPager mViewPager;
    private List<Fragment> fragments = new ArrayList<>(4);
    private TextView refresh_tip;
    private ViewGroup teenagerTab;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_home_main;
    }

    @Override
    protected void initDate() {
        if (!fragments.isEmpty()) fragments.clear();
        fragments.add(new RecommendFragment());
        fragments.add(new VideoFragment());
        fragments.add(new PhotoFragment());
        fragments.add(new TextFragment());
        mViewPager.setAdapter(new MyFragmentAdapter(getChildFragmentManager(), fragments));
        mViewPager.setOffscreenPageLimit(3);
    }

    public int getViewpagerCurrentIndex() {
        if (mViewPager != null) {
            return mViewPager.getCurrentItem();
        }
        return 0;
    }

    @Override
    protected void initView(View inflate) {
        mViewPager = inflate.findViewById(R.id.viewpager);
        refresh_tip = inflate.findViewById(R.id.tv_refresh_tip);
        View refreshBt = inflate.findViewById(R.id.iv_refresh);
        refreshBt.setOnClickListener(new FastClickListener(1000L, false) {
            @Override
            protected void onSingleClick() {
                refreshBt.animate().rotationBy(360 * 2).setDuration(700)
                        .setInterpolator(new AccelerateDecelerateInterpolator());
                refreshDateByTab();
            }
        });
        //初始化设置
        teenagerTab = inflate.findViewById(R.id.home_tab_teenager_mode);
        setTeenagerMode(CommonHttpRequest.teenagerIsOpen);
        teenagerTab.setOnClickListener(v ->
                HelperForStartActivity.openTeenager(CommonHttpRequest.teenagerIsOpen,
                        CommonHttpRequest.teenagerPsd));
        inflate.findViewById(R.id.home_search).setOnClickListener(HelperForStartActivity::openSearch);
        //指示器的初始化
        MagicIndicator magicIndicator = inflate.findViewById(R.id.magic_indicator6);
        IndicatorHelper.homeIndicator(getContext(), mViewPager, magicIndicator, IndicatorHelper.CHANNELS);
        maiDian();
    }

    /**
     * 2019/3/13 统计埋点用
     */
    private void maiDian() {
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                String type;
                String home_type;
                switch (position) {
                    case 1:
                        type = CommonHttpRequest.AppType.home_video;
                        home_type = UmengStatisticsKeyIds.home_video;
                        break;
                    case 2:
                        type = CommonHttpRequest.AppType.home_pic;
                        home_type = UmengStatisticsKeyIds.home_pic;
                        break;
                    case 3:
                        type = CommonHttpRequest.AppType.home_word;
                        home_type = UmengStatisticsKeyIds.home_word;
                        break;
                    default:
                        type = CommonHttpRequest.AppType.home_all;
                        home_type = UmengStatisticsKeyIds.home_recommended;
                        break;
                }
                UmengHelper.event(home_type);
                CommonHttpRequest.getInstance().statisticsApp(type);
                VideoViewManager.instance().stopPlayback();
            }
        });
    }

    /**
     * 首页发布给第一个推荐页面
     *
     * @param dataBean
     */
    public void addPublishDate(MomentsDataBean dataBean) {
        mViewPager.setCurrentItem(0, false);
        if (AppUtil.listHasDate(fragments)) {
            ((RecommendFragment) fragments.get(0)).addPublishDate(dataBean);
        }
    }

    public TranslateAnimation animationOut;

    public void setTeenagerMode(boolean isTeenagerOpen) {
        if (teenagerTab == null) return;
        teenagerTab.setVisibility(isTeenagerOpen ? View.VISIBLE : View.GONE);
        //防止如果不在推荐页,开启后指定到推荐页
        if (isTeenagerOpen) {
            mViewPager.setCurrentItem(0);
        }
        mViewPager.setSlipping(!isTeenagerOpen);
    }

    public class MyRunnable implements Runnable {

        @Override
        public void run() {
            if (animationOut == null) {
                animationOut = new TranslateAnimation(0, 0,
                        0, -1.5f * refresh_tip.getMeasuredHeight());
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
            refresh_tip.startAnimation(animationOut);
        }
    }

    private MyRunnable runnable;

    public void showRefreshTip(int size) {
        if (refresh_tip != null) {
            refresh_tip.setText(String.format("发现了%d条新内容", size));
            refresh_tip.setVisibility(View.VISIBLE);
            if (runnable == null) {
                runnable = new MyRunnable();
            }
            refresh_tip.removeCallbacks(runnable);
            refresh_tip.postDelayed(runnable, 1000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        refresh_tip.setVisibility(View.GONE);
    }

    /**
     * 左右滑动的详情也加载更多的时候调用,同时获取数据后还需要回调给调用方的详情页
     */

    public void getLoadMoreDate(ILoadMore callBack) {
        //刷新当前页面,向上转型用接口
        // TODO: 2018/12/24 #9006 java.lang.IndexOutOfBoundsException  Index: 0, Size: 0 有点奇妙
        if (fragments == null || fragments.size() == 0) {
            callBack.loadMoreDate(null);
            return;
        }
        int index = getViewpagerCurrentIndex();
        if (fragments.get(index) instanceof IHomeRefresh) {
            IHomeRefresh refresh = (IHomeRefresh) fragments.get(index);
            refresh.loadMore(callBack);
        }
    }

    @Override
    public void refreshDateByTab() {
        int index = getViewpagerCurrentIndex();
        if (fragments.get(index) instanceof IHomeRefresh) {
            IHomeRefresh refresh = (IHomeRefresh) fragments.get(index);
            refresh.refreshDate();
        }
    }
}
