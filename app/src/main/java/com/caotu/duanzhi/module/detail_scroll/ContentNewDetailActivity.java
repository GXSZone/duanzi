package com.caotu.duanzhi.module.detail_scroll;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.caotu.adlib.ADInfoWarp;
import com.caotu.adlib.AdHelper;
import com.caotu.adlib.CommentDateCallBack;
import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.advertisement.IADView;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.detail.ILoadMore;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.ToastUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 内容详情页面,只有个viewpager,处理fragment的绑定,其他都在fragment处理
 * 用viewpager2 实现的滑动事件处理不够好,特别是这个这个复杂的详情页,用viewpager 事件处理的更好一些
 */

public class ContentNewDetailActivity extends BaseActivity implements ILoadMore, IADView {
    //用Viewpager2 实现
    //    private ViewPager2 viewpager;
    //    private FragmentStateAdapter adapter;
    //用Viewpager 实现
    private ViewPager viewpager;
    public FragmentStatePagerAdapter adapter;
    private LinkedList<Pair<BaseContentDetailFragment, Integer>> fragmentAndIndex;
    int mPosition;

    @Override
    protected int getLayoutView() {
        fullScreen(this);
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
//        viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                if (position == fragmentAndIndex.size() - 2) {
//                    getLoadMoreDate();
//                }
//                UmengHelper.event(UmengStatisticsKeyIds.left_right);
//                CommonHttpRequest.getInstance().requestPlayCount(fragmentAndIndex.get(position).first.contentId);
//            }
//        });
        viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

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
//        adapter = new FragmentStateAdapter(this) {
//            @NonNull
//            @Override
//            public Fragment createFragment(int position) {
//                return fragmentAndIndex.get(position).first;
//            }
//
//            @Override
//            public int getItemCount() {
//                return fragmentAndIndex.size();
//            }
//        };
        adapter = new FragmentStatePagerAdapter(getSupportFragmentManager(),
                //该变量可以控制viewpager里的fragment可见走resume
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

            @Override
            public int getCount() {
                return fragmentAndIndex == null ? 0 : fragmentAndIndex.size();
            }

            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragmentAndIndex.get(position).first;
            }

            /**
             * 复写该方法是为了解决 FragmentStatePagerAdapter fragment太多的话抛异常会
             * android.os.TransactionTooLargeException
             * 原因:FragmentStatePagerAdapter的saveState保存了过多的历史Fragment实例的状态数据
             *
             * @return
             */
            @Override
            public Parcelable saveState() {
                Bundle bundle = (Bundle) super.saveState();
                if (bundle != null) {
                    bundle.putParcelableArray("states", null); // Never maintain any states from the base class, just null it out
                }
                return bundle;
            }
        };
        viewpager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
//        if (!isInit && adapter != null) {
//            adapter.notifyItemRangeChanged(getIndex() + 1, 20);
//        }
        if (!isInit && adapter != null) {
            adapter.notifyDataSetChanged();
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addFragment(beanList, false);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Integer second = fragmentAndIndex.get(getIndex()).second;
        EventBusHelp.sendPagerPosition(second); //为了返回列表的时候定位到当前条目
    }

    /**
     * 累计获取了多少条广告
     */
    int count = 0;
    ADInfoWarp headerWarp;
    List<View> adHeaderViewList;

    int commentCount = 0;
    ADInfoWarp commentWarp;
    List<View> adCommentViewList;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        headerWarp = AdHelper.getInstance().initDetailHeaderAd(this, new CommentDateCallBack() {
            @Override
            public void commentAd(View adView) {
                if (adHeaderViewList == null) {
                    adHeaderViewList = new ArrayList<>();
                }
                adHeaderViewList.add(adView);

//                    int index = getIndex();
//                    if (index == 0 && fragmentAndIndex != null) {
//                        fragmentAndIndex.get(index).first.dealHeaderAd(adView);
//                    }

            }

            @Override
            public void remove() {
                //移除广告
                int index = getIndex();
                if (fragmentAndIndex != null) {
                    fragmentAndIndex.get(index).first.removeAd();
                }
            }
        });

        commentWarp = AdHelper.getInstance().initCommentItemAd(this, new CommentDateCallBack() {
            @Override
            public void commentAd(View adView) {
                if (adCommentViewList == null) {
                    adCommentViewList = new ArrayList<>();
                }
                adCommentViewList.add(adView);
//                int index = getIndex();
//                if (index == 0 && fragmentAndIndex != null) {
//                    fragmentAndIndex.get(index).first.refreshCommentListAd(adView);
//                }
            }

            @Override
            public void remove() {

            }
        });
    }


    @Override
    protected void onDestroy() {
        if (headerWarp != null) {
            count = 0;
            headerWarp.destory();
            headerWarp = null;
        }
        if (commentWarp != null) {
            commentWarp.destory();
            commentWarp = null;
            commentCount = 0;
        }
        if (AppUtil.listHasDate(adHeaderViewList)) {
            adHeaderViewList.clear();
            adHeaderViewList = null;
        }
        if (AppUtil.listHasDate(adCommentViewList)) {
            adCommentViewList.clear();
            adCommentViewList = null;
        }
        super.onDestroy();
    }

    /**
     * 给子fragment调用获取广告
     */

    @Override
    public View getAdView() {
        if (adHeaderViewList == null || count > adHeaderViewList.size() - 1) return null;
        View headerAdView = AdHelper.getInstance().getDetailAd(headerWarp,
                adHeaderViewList.get(count));
        count++;
        return headerAdView;
    }

    @Override
    public View getCommentAdView() {
        if (adCommentViewList == null || commentCount > adCommentViewList.size() - 1) return null;
        View commentAdView = AdHelper.getInstance().getDetailAd(commentWarp,
                adCommentViewList.get(commentCount));
        commentCount++;
        return commentAdView;
    }
}
