package com.caotu.duanzhi.module.detail_scroll;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.advertisement.ADConfig;
import com.caotu.duanzhi.advertisement.ADUtils;
import com.caotu.duanzhi.advertisement.IADView;
import com.caotu.duanzhi.advertisement.NativeAdListener;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.module.detail.ILoadMore;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.widget.FlexibleViewPager;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;

import java.util.ArrayList;
import java.util.List;

/**
 * 内容详情页面,只有个viewpager,处理fragment的绑定,其他都在fragment处理
 */

public class ContentNewDetailActivity extends BaseActivity implements ILoadMore, IADView {

    private FlexibleViewPager viewpager;
    private ArrayList<BaseFragment> fragments;
    int mPosition;
    private ArrayList<MomentsDataBean> dateList;
    private BaseFragmentAdapter fragmentAdapter;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_new_detail;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!AppUtil.listHasDate(fragments)) return;
        BaseFragment baseFragment = fragments.get(getIndex());
        if (baseFragment instanceof BaseContentDetailFragment) {
            baseFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public int getIndex() {
        return viewpager == null ? 0 : viewpager.getCurrentItem();
    }

    @Override
    protected void initView() {
        viewpager = findViewById(R.id.viewpager_fragment_content);
        dateList = BigDateList.getInstance().getBeans();
        if (dateList == null || dateList.size() == 0) {
            finish();
            return;
        }
        mPosition = getIntent().getIntExtra(HelperForStartActivity.KEY_FROM_POSITION, 0);
        bindViewPager();
    }

    private void bindViewPager() {
        viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
//                getLoadMoreDate(position);
                UmengHelper.event(UmengStatisticsKeyIds.left_right);
                EventBusHelp.sendPagerPosition(getIndex() + mPosition); //为了返回列表的时候定位到当前条目
            }
        });
        viewpager.setOnRefreshListener(new FlexibleViewPager.OnRefreshListener() {
            @Override
            public void onRefresh() {
                finish();
            }

            @Override
            public void onLoadMore() {
                getLoadMoreDate();
            }
        });
        if (AppUtil.listHasDate(dateList)) {
            fragments = new ArrayList<>();
            // TODO: 2019-09-12 这个起始值很关键,为了进入详情只初始化两个fragment,更优方案
            for (int i = mPosition; i < dateList.size(); i++) {
                MomentsDataBean dataBean = dateList.get(i);
                if (TextUtils.equals("5", dataBean.getContenttype())) {
                    WebFragment fragment = new WebFragment();
                    CommentUrlBean webList = VideoAndFileUtils.getWebList(dataBean.getContenturllist());
                    fragment.setDate(webList.info, dataBean.getContenttitle());
                    fragments.add(fragment);
                    continue;
                }
                BaseContentDetailFragment fragment;
                if (isVideoType(dataBean)) {
                    fragment = new VideoDetailFragment();
                } else {
                    fragment = new BaseContentDetailFragment();
                }
                fragment.setDate(dataBean);
                fragments.add(fragment);
            }
        }
        fragmentAdapter = new BaseFragmentAdapter(getSupportFragmentManager(), fragments);
        viewpager.setAdapter(fragmentAdapter);
    }

    private boolean isVideoType(MomentsDataBean dataBean) {
        String contenttype = dataBean.getContenttype();
        return TextUtils.equals(contenttype, "1") || TextUtils.equals(contenttype, "2");
    }


    private void getLoadMoreDate() {
        // TODO: 2018/12/14 如果是最后一页加载更多
        Activity secondActivity = MyApplication.getInstance().getLastSecondActivity();
        if (secondActivity instanceof DetailGetLoadMoreDate) {
            ((DetailGetLoadMoreDate) secondActivity).getLoadMoreDate(this);
        }
    }

    @Override
    public void loadMoreDate(List<MomentsDataBean> beanList) {
        if (beanList == null || beanList.size() == 0) {
            ToastUtil.showShort("没有更多内容啦～");
            return;
        }

        for (int i = 0; i < beanList.size(); i++) {
            MomentsDataBean dataBean = beanList.get(i);
            if (TextUtils.equals("5", dataBean.getContenttype())) {
                WebFragment fragment = new WebFragment();
                CommentUrlBean webList = VideoAndFileUtils.getWebList(dataBean.getContenturllist());
                fragment.setDate(webList.info, dataBean.getContenttitle());
                fragments.add(fragment);
                continue;
            }
            BaseContentDetailFragment fragment;
            if (isVideoType(dataBean)) {
                fragment = new VideoDetailFragment();
            } else {
                fragment = new BaseContentDetailFragment();
            }
            fragment.setDate(dataBean);
            fragments.add(fragment);
        }

        if (fragmentAdapter != null) {
            fragmentAdapter.changeFragment(fragments);
        }
        ToastUtil.showShort("加载内容成功");
    }


    public int getPosition() {
        return getIndex() + mPosition;
    }

    /**
     * 这个方法也是解决 下面的奔溃问题,等下个版本看
     * android.os.TransactionTooLargeException
     * data parcel size 571860 bytes
     *
     * @param oldInstanceState
     */
    @Override
    protected void onSaveInstanceState(Bundle oldInstanceState) {
        super.onSaveInstanceState(oldInstanceState);
        oldInstanceState.clear();
    }

    @Override
    public int getBarColor() {
        return DevicesUtils.getColor(R.color.shadow_color);
    }

    NativeExpressAD nativeCommentAd; //评论列表插的广告
    NativeExpressAD nativeAd;        //详情头布局的广告
    List<NativeExpressADView> adList;
    List<NativeExpressADView> adCommentList;
    /**
     * 累计获取了多少条广告
     */
    int count = 0;
    int commentCount = 0;

    /**
     * 广告是异步的,所以会导致广告还没
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ADConfig.AdOpenConfig.contentAdIsOpen) {
            nativeAd = ADUtils.getNativeAd(this, ADConfig.datail_id, 6,
                    new NativeAdListener(2) {
                        @Override
                        public void onADLoaded(List<NativeExpressADView> list) {
                            super.onADLoaded(list);
                            if (adList == null) {
                                adList = new ArrayList<>();
                            }
                            adList.addAll(getAdList());
                            if (!AppUtil.listHasDate(fragments))return;
                            BaseFragment fragment = fragments.get(getIndex());
                            if (fragment instanceof BaseContentDetailFragment) {
                                ((BaseContentDetailFragment) fragment).refreshAdView(getAdView());
                            }
                        }
                    });
        }
        if (ADConfig.AdOpenConfig.commentAdIsOpen) {
            nativeCommentAd = ADUtils.getNativeAd(this, ADConfig.comment_id, 6,
                    new NativeAdListener(3) {
                        @Override
                        public void onADLoaded(List<NativeExpressADView> list) {
                            super.onADLoaded(list);
                            if (adCommentList == null) {
                                adCommentList = new ArrayList<>();
                            }
                            adCommentList.addAll(getAdList());
                            if (!AppUtil.listHasDate(fragments))return;
                            BaseFragment fragment = fragments.get(getIndex());
                            if (fragment instanceof BaseContentDetailFragment) {
                                ((BaseContentDetailFragment) fragment).
                                        refreshCommentListAd(getCommentAdView());
                            }
                        }
                    });
        }
    }

    /**
     * 给子fragment调用获取广告
     */

    @Override
    public NativeExpressADView getAdView() {
        if (!ADConfig.AdOpenConfig.contentAdIsOpen
                || nativeAd == null || adList == null) return null;
        if (count >= adList.size() - 2) {  //>= 可以防止广告加载失败还有机会再去加载一次
            nativeAd.loadAD(6);
        }
        //防止越界
        if (adList.size() - 1 < count) {
            return null;
        }
        NativeExpressADView adView = adList.get(count);
        adView.render();
        count++;
        return adView;
    }

    @Override
    public NativeExpressADView getCommentAdView() {
        if (!ADConfig.AdOpenConfig.commentAdIsOpen
                || nativeCommentAd == null || adCommentList == null) return null;
        if (commentCount >= adCommentList.size() - 2) {  //>= 可以防止广告加载失败还有机会再去加载一次
            nativeCommentAd.loadAD(6);
        }
        //防止越界
        if (adCommentList.size() - 1 < commentCount) {
            return null;
        }
        NativeExpressADView adView = adCommentList.get(commentCount);
        adView.render();
        commentCount++;
        return adView;
    }
}
