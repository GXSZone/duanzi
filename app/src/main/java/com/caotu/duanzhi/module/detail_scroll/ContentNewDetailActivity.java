package com.caotu.duanzhi.module.detail_scroll;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.advertisement.ADConfig;
import com.caotu.duanzhi.advertisement.ADUtils;
import com.caotu.duanzhi.advertisement.IADView;
import com.caotu.duanzhi.advertisement.NativeAdListener;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.detail.ILoadMore;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.ToastUtil;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;

import java.util.LinkedList;
import java.util.List;

/**
 * 内容详情页面,只有个viewpager,处理fragment的绑定,其他都在fragment处理
 */

public class ContentNewDetailActivity extends BaseActivity implements ILoadMore, IADView {

    private ViewPager2 viewpager;
    private LinkedList<Pair<BaseContentDetailFragment, Integer>> fragmentAndIndex;
    int mPosition;
    private FragmentStateAdapter adapter;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_new_detail;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            fragmentAndIndex.get(getIndex()).first.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getIndex() {
        return viewpager == null ? 0 : viewpager.getCurrentItem();
    }

    /**
     * eventBus 事件传递用
     *
     * @return
     */
    public int getPosition() {
        return fragmentAndIndex == null ? 0 : fragmentAndIndex.get(getIndex()).second;
    }

    @Override
    protected void initView() {
        viewpager = findViewById(R.id.viewpager_fragment_content);
        List<MomentsDataBean> dateList = BigDateList.getInstance().getBeans();
        if (!AppUtil.listHasDate(dateList)) {
            finish();
            return;
        }
        mPosition = getIntent().getIntExtra(HelperForStartActivity.KEY_FROM_POSITION, 0);
        bindViewPager(dateList);
    }

    private void bindViewPager(List<MomentsDataBean> dateList) {
        viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == fragmentAndIndex.size() - 2) {
                    getLoadMoreDate();
                }
                UmengHelper.event(UmengStatisticsKeyIds.left_right);
                CommonHttpRequest.getInstance().requestPlayCount(fragmentAndIndex.get(position).first.contentId);
            }
        });
        if (AppUtil.listHasDate(dateList)) {
            fragmentAndIndex = new LinkedList<>();
            addFragment(dateList, true);
        }
        adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragmentAndIndex.get(position).first;
            }

            @Override
            public int getItemCount() {
                return fragmentAndIndex.size();
            }
        };
//        viewpager.setOffscreenPageLimit(1);
        viewpager.setAdapter(adapter);
    }

    /**
     * 因为这个集合是从列表完整搬移过来的,本质就是同一个集合数据,所以这里不需要记录
     * 初始化的话是整个集合都传,加载更多的话只是获取接口拿到的数据集,所以要分开
     *
     * @param dateList
     * @param isInit
     */
    private void addFragment(List<MomentsDataBean> dateList, boolean isInit) {
        if (!isInit && !AppUtil.listHasDate(dateList)) {
            ToastUtil.showShort("没有更多内容啦～");
            return;
        }
        //这个不能直接拿i 计数
        int index = isInit ? mPosition : fragmentAndIndex.getLast().second + 1;
        for (int i = isInit ? mPosition : 0; i < dateList.size(); i++) {
            MomentsDataBean dataBean = dateList.get(i);
            String contenttype = dataBean.getContenttype();
            //广告类型和感兴趣用户类型在这边不展示,但是index是个问题
            if (AppUtil.isAdType(contenttype) || AppUtil.isUserType(contenttype)
                    || TextUtils.equals("5", contenttype)) {
                index++;
                continue;
            }
//            if (TextUtils.equals("5", contenttype)) {
//                WebFragment fragment = new WebFragment();
//                CommentUrlBean webList = VideoAndFileUtils.getWebList(dataBean.getContenturllist());
//                fragment.setDate(webList.info, dataBean.getContenttitle());
//                fragments.add(fragment);
//                continue;
//            }
            BaseContentDetailFragment fragment;
            if (TextUtils.equals(contenttype, "1") || TextUtils.equals(contenttype, "2")) {
                fragment = new VideoDetailFragment();
            } else {
                fragment = new BaseContentDetailFragment();
            }
            fragment.setDate(dataBean);
            Pair<BaseContentDetailFragment, Integer> pair = new Pair<>(fragment, index);
            fragmentAndIndex.add(pair);
            index++;
        }
        if (!isInit && adapter != null) {
            adapter.notifyItemRangeChanged(getIndex() + 1, 20);
        }
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
        addFragment(beanList, false);
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
     * 广告是异步的,现在不用回调的解决办法,直接加延迟展示,回调的话两个fragment不好处理
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
                            adList = getAdList();
                        }
                    });
        }
        if (ADConfig.AdOpenConfig.commentAdIsOpen) {
            nativeCommentAd = ADUtils.getNativeAd(this, ADConfig.comment_id, 6,
                    new NativeAdListener(3) {
                        @Override
                        public void onADLoaded(List<NativeExpressADView> list) {
                            super.onADLoaded(list);
                            adCommentList = getAdList();
                        }
                    });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Integer second = fragmentAndIndex.get(getIndex()).second;
        EventBusHelp.sendPagerPosition(second); //为了返回列表的时候定位到当前条目
    }

    @Override
    protected void onDestroy() {
        ADUtils.destroyAd(null, adList);
        ADUtils.destroyAd(null, adCommentList);
        super.onDestroy();
    }

    /**
     * 给子fragment调用获取广告
     */

    @Override
    public NativeExpressADView getAdView() {
        if (!ADConfig.AdOpenConfig.contentAdIsOpen
                || nativeAd == null || adList == null) return null;
        //防止越界
        if (adList.size() - 1 < count) {
            return null;
        }
        NativeExpressADView adView = adList.get(count);
        adView.render();
        if (count >= adList.size() - 2) {  //>= 可以防止广告加载失败还有机会再去加载一次
            nativeAd.loadAD(6);
        }
        count++;
        return adView;
    }

    @Override
    public NativeExpressADView getCommentAdView() {
        if (!ADConfig.AdOpenConfig.commentAdIsOpen
                || nativeCommentAd == null || adCommentList == null) return null;

        //防止越界
        if (adCommentList.size() - 1 < commentCount) {
            return null;
        }
        NativeExpressADView adView = adCommentList.get(commentCount);
        adView.render();
        if (commentCount >= adCommentList.size() - 2) {  //>= 可以防止广告加载失败还有机会再去加载一次
            nativeCommentAd.loadAD(6);
        }
        commentCount++;
        return adView;
    }
}
