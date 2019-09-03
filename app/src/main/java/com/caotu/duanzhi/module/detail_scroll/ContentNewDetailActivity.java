package com.caotu.duanzhi.module.detail_scroll;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.module.detail.ILoadMore;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.SoftKeyBoardListener;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.widget.FlexibleViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 内容详情页面,只有个viewpager,处理fragment的绑定,其他都在fragment处理
 */

public class ContentNewDetailActivity extends BaseActivity implements ILoadMore {

    private FlexibleViewPager viewpager;
    private ArrayList<BaseFragment> fragments;
    int mPosition;
    private ArrayList<MomentsDataBean> dateList;
    private BaseFragmentAdapter fragmentAdapter;
    private View keyBoard;

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
        keyBoard = findViewById(R.id.hide_keyboard);
        keyBoard.setOnClickListener(v -> closeSoftKeyboard());
        viewpager = findViewById(R.id.viewpager_fragment_content);
        dateList = BigDateList.getInstance().getBeans();
        if (dateList == null || dateList.size() == 0) {
            finish();
            return;
        }
        mPosition = getIntent().getIntExtra(HelperForStartActivity.KEY_FROM_POSITION, 0);
        bindViewPager();
        setKeyBoardListener();
    }

    private void bindViewPager() {
        viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
//                getLoadMoreDate(position);
                UmengHelper.event(UmengStatisticsKeyIds.click_text);
                EventBusHelp.sendPagerPosition(getIndex() + mPosition);
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
            for (int i = 0; i < dateList.size(); i++) {
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

    private void setKeyBoardListener() {
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                keyBoard.setVisibility(View.VISIBLE);
                BaseFragment baseFragment = fragments.get(getIndex());
                if (baseFragment instanceof BaseContentDetailFragment) {
                    ((BaseContentDetailFragment) baseFragment).keyBoardShow();
                }
            }

            @Override
            public void keyBoardHide() {
                keyBoard.setVisibility(View.GONE);
                BaseFragment baseFragment = fragments.get(getIndex());
                if (baseFragment instanceof BaseContentDetailFragment) {
                    ((BaseContentDetailFragment) baseFragment).keyBoardHide();
                }
            }
        });
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
        if (dateList != null) {
            for (int i = 0; i < beanList.size(); i++) {
                MomentsDataBean dataBean = beanList.get(i);
                //数据集也同步
                dateList.add(dataBean);
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
        if (fragmentAdapter != null) {
            fragmentAdapter.changeFragment(fragments);
        }
        ToastUtil.showShort("加载内容成功");
    }

//    @Override
//    protected void onDestroy() {
//        EventBusHelp.sendPagerPosition(getIndex() + mPosition);
//        super.onDestroy();
//    }

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
}
