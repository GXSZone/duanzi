package com.caotu.duanzhi.module.detail_scroll;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.module.base.BaseSwipeActivity;
import com.caotu.duanzhi.module.detail.ILoadMore;
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

//    @Override  这个相当有问题,会影响侧滑返回的计算,键盘弹出的可视区域变化
//    protected void onCreate(Bundle savedInstanceState) {
//        // 隐藏标题栏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        // 隐藏状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        super.onCreate(savedInstanceState);
//    }

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
        if (baseFragment instanceof BaseContentDetailFragment) {
            baseFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public int getIndex() {
        return viewpager == null ? 0 : viewpager.getCurrentItem();
    }

    @Override
    protected void initView() {
//        fullScreen(this);
//        View statusBar = findViewById(R.id.view_dynamic_status_bar);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            statusBar.setBackgroundColor(Color.BLACK);
//        } else {
//            statusBar.setBackgroundColor(DevicesUtils.getColor(R.color.color_status_bar));
//        }
//
//        ViewGroup.LayoutParams layoutParams = statusBar.getLayoutParams();
//        layoutParams.height = DevicesUtils.getStatusBarHeight(this);
//        statusBar.setLayoutParams(layoutParams);

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
//        fragmentAdapter.notifyDataSetChanged();
    }

    private boolean isVideoType(MomentsDataBean dataBean) {
        String contenttype = dataBean.getContenttype();
        return TextUtils.equals(contenttype, "1") || TextUtils.equals(contenttype, "2");
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
    }

    @Override
    protected void onDestroy() {
        EventBusHelp.sendPagerPosition(getIndex() + mPosition);
        super.onDestroy();
    }

    public int getPosition() {
        return getIndex() + mPosition;
    }
}
