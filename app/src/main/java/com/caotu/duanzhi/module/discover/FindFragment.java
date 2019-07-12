package com.caotu.duanzhi.module.discover;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.DiscoverBannerBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.module.base.MyFragmentAdapter;
import com.caotu.duanzhi.module.home.ILoginEvent;
import com.caotu.duanzhi.module.home.ITabRefresh;
import com.caotu.duanzhi.module.mine.fragment.FocusTopicFragment;
import com.caotu.duanzhi.module.other.IndicatorHelper;
import com.caotu.duanzhi.other.AndroidInterface;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sunfusheng.GlideImageView;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * 新版的发现页UI,目前先用原先的,后期用
 */
public class FindFragment extends BaseFragment implements ITabRefresh, OnRefreshListener, ILoginEvent {

    private MZBannerView<DiscoverBannerBean.BannerListBean> bannerView;
    private ViewPager viewPager;
    private FocusTopicFragment topicFragment;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_discover_new_layout;
    }

    @Override
    protected void initDate() {
        getBannerDate();
    }

    ArrayList<Fragment> fragments;
    public SmartRefreshLayout mSwipeLayout;

    @Override
    protected void initView(View inflate) {
        View searchView = inflate.findViewById(R.id.tv_go_search);
        if (searchView != null) {
            searchView.setOnClickListener(HelperForStartActivity::openSearch);
        }
        mSwipeLayout = inflate.findViewById(R.id.swipe_layout);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setEnableLoadMore(false);
        bannerView = inflate.findViewById(R.id.mz_banner);
        MagicIndicator indicator = inflate.findViewById(R.id.magic_indicator);
        viewPager = inflate.findViewById(R.id.viewpager);
        IndicatorHelper.initIndicator(getContext(), viewPager, indicator, IndicatorHelper.FINDS);
        initFragments();
        viewPager.setAdapter(new MyFragmentAdapter(getChildFragmentManager(), fragments));
    }

    private void initFragments() {
        fragments = new ArrayList<>();
        topicFragment = new FocusTopicFragment();
        topicFragment.setDate(MySpUtils.getMyId(), true);
        TopicListFragment topicListFragment = new TopicListFragment();
        fragments.add(topicFragment);
        fragments.add(topicListFragment);
    }


    private boolean bannerSuccess = false;


    private void getBannerDate() {
        OkGo.<BaseResponseBean<DiscoverBannerBean>>post(HttpApi.DISCOVER_BANNER)
                .execute(new JsonCallback<BaseResponseBean<DiscoverBannerBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<DiscoverBannerBean>> response) {
                        List<DiscoverBannerBean.BannerListBean> bannerList = response.body().getData().getBannerList();
                        bindBanner(bannerList);
                        bannerSuccess = true;
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<DiscoverBannerBean>> response) {
                        bannerSuccess = false;
                        super.onError(response);
                    }
                });
    }


    private void bindBanner(List<DiscoverBannerBean.BannerListBean> bannerList) {
        if (bannerView != null && bannerList != null && bannerList.size() > 0) {
            bannerView.setBannerPageClickListener((view, i) -> {
                DiscoverBannerBean.BannerListBean bannerListBean = bannerList.get(i);
                skipByBanner(bannerListBean);
            });
            // 设置数据
            bannerView.setPages(bannerList, (MZHolderCreator<BannerViewHolder>) () -> new BannerViewHolder(bannerView));
            bannerView.start();
        }
    }

    private void skipByBanner(DiscoverBannerBean.BannerListBean bean) {
        //展示页类型 1_wap页 2_主题合集 3_主题 4_内容
        switch (bean.bannertype) {
            case "1":
                WebShareBean shareBean = new WebShareBean();
                shareBean.icon = bean.bannersharepic;
                HelperForStartActivity.checkUrlForSkipWeb(bean.bannertext, bean.bannerurl, AndroidInterface.type_banner, shareBean);
                //统计用
                CommonHttpRequest.getInstance().splashCount("BANNER" + bean.bannerid);
                break;
            case "3":
                HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic, bean.bannerurl);
                break;
            case "4":
                HelperForStartActivity.openContentDetail(bean.bannerurl);
                break;
            default:
                // TODO: 2018/12/4 跳转H5页面固定
//                WebActivity.openWeb();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (bannerView != null) {
            bannerView.pause();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (bannerView == null) return;
        if (!isVisibleToUser) {
            bannerView.pause();
        } else {
            bannerView.start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bannerView != null) {
            bannerView.start();
        }
    }

    @Override
    public boolean isNeedLazyLoadDate() {
        return true;
    }

    @Override
    public void refreshDateByTab() {
        if (mSwipeLayout != null) {
            mSwipeLayout.autoRefresh();
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (!bannerSuccess) {
            getBannerDate();
        }
        if (fragments != null && fragments.size() == 2) {
            Fragment fragment = fragments.get(viewPager.getCurrentItem());
            if (fragment instanceof ITabRefresh) {
                ((ITabRefresh) fragment).refreshDateByTab();
            }
        }
        if (mSwipeLayout != null && mSwipeLayout.getState() == RefreshState.Refreshing) {
            mSwipeLayout.finishRefresh(1000);
        }
    }

    @Override
    public void login() {
        if (topicFragment != null) {
            topicFragment.login();
        }
    }

    @Override
    public void loginOut() {
        if (topicFragment != null) {
            topicFragment.loginOut();
        }
    }

    public static class BannerViewHolder implements MZViewHolder<DiscoverBannerBean.BannerListBean> {
        private GlideImageView mImageView;
        private ViewGroup viewGroup;

        public BannerViewHolder(ViewGroup bannerView) {
            viewGroup = bannerView;
        }

        @Override
        public View createView(Context context) {
            // 返回页面布局
            View rootView = LayoutInflater.from(context).inflate(R.layout.item_banner_layout, viewGroup, false);
            mImageView = rootView.findViewById(R.id.image_banner);
            return rootView;
        }

        @Override
        public void onBind(Context context, int position, DiscoverBannerBean.BannerListBean data) {
            // 数据绑定
            String url = MyApplication.buildFileUrl(data.bannerpic);
//                data.bannerpic = data.bannerpic.replace("https", "http");
            mImageView.load(url, R.mipmap.shenlue_logo, 5);
        }
    }
}
