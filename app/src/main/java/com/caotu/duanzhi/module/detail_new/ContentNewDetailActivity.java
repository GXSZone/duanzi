package com.caotu.duanzhi.module.detail_new;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.module.base.BaseSwipeActivity;
import com.caotu.duanzhi.module.detail.IHolder;
import com.caotu.duanzhi.module.detail.ILoadMore;
import com.caotu.duanzhi.module.detail_scroll.BaseFragmentAdapter;
import com.caotu.duanzhi.module.detail_scroll.BigDateList;
import com.caotu.duanzhi.module.detail_scroll.DetailGetLoadMoreDate;
import com.caotu.duanzhi.module.detail_scroll.ScrollDetailFragment;
import com.caotu.duanzhi.module.detail_scroll.WebFragment;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 内容详情页面,只有个viewpager,处理fragment的绑定,其他都在fragment处理
 */

public class ContentNewDetailActivity extends BaseSwipeActivity implements ILoadMore {

    private ViewPager viewpager;
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
        if (baseFragment instanceof ContentScrollDetailFragment) {
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
        viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                getLoadMoreDate(position);
                if (fragments.get(position) instanceof ScrollDetailFragment) {
                    IHolder viewHolder = ((ScrollDetailFragment) fragments.get(position)).viewHolder;
                    if (viewHolder != null) {
                        viewHolder.autoPlayVideo();
                    }
                }
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
                ContentScrollDetailFragment detailFragment = new ContentScrollDetailFragment();
                detailFragment.setDate(dataBean);
                fragments.add(detailFragment);
            }
        }
        fragmentAdapter = new BaseFragmentAdapter(getSupportFragmentManager(), fragments);
        viewpager.setAdapter(fragmentAdapter);
    }


    private void getLoadMoreDate(int position) {
        if (position == fragments.size() - 1) {
            // TODO: 2018/12/14 如果是最后一页加载更多
            Activity secondActivity = MyApplication.getInstance().getLastSecondActivity();
            if (secondActivity instanceof DetailGetLoadMoreDate) {
                ((DetailGetLoadMoreDate) secondActivity).getLoadMoreDate(this);
            }
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
                ContentScrollDetailFragment detailFragment = new ContentScrollDetailFragment();
                detailFragment.setDate(dataBean);
                fragments.add(detailFragment);
            }
            if (fragmentAdapter != null) {
                fragmentAdapter.changeFragment(fragments);
            }
        }
    }
}
