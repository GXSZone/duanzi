package com.caotu.duanzhi.module.base;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.NetWorkUtils;
import com.caotu.duanzhi.view.widget.StateView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * 该泛型指的是列表list 的bean对象
 *
 * @param <T>
 */
public abstract class BaseStateFragment<T> extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    public RecyclerView mRvContent;
    public SwipeRefreshLayout mSwipeLayout;
    public StateView mStatesView;
    public BaseQuickAdapter<T, BaseViewHolder> adapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.layout_base_states_view;
    }

    @Override
    protected void initDate() {
        if (!NetWorkUtils.isNetworkConnected(MyApplication.getInstance())) {
            mStatesView.setCurrentState(StateView.STATE_ERROR);
            return;
        }
        position = 1;
        netWorkState = DateState.init_state;
        getNetWorkDate(DateState.init_state);
    }

    @Override
    protected void initView(View inflate) {
        mStatesView = inflate.findViewById(R.id.states_view);
        mRvContent = inflate.findViewById(R.id.rv_content);
        mSwipeLayout = inflate.findViewById(R.id.swipe_layout);

        //条目布局
        adapter = getAdapter();
        //这里其实就是绑定adapter
        adapter.bindToRecyclerView(mRvContent);
        adapter.setEmptyView(initEmptyView());
        //这里默认初始化为loading状态
        mStatesView.setCurrentState(StateView.STATE_LOADING);
        adapter.closeLoadAnimation();
        adapter.setOnLoadMoreListener(this, mRvContent);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeColors(DevicesUtils.getColor(R.color.color_FF8787),
                DevicesUtils.getColor(R.color.color_3f4557));
//        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        initViewListener();
        adapter.disableLoadMoreIfNotFullPage(mRvContent);

    }

    /**
     * 给子类用于初始化操作,adapter加头布局也可以,这样adapter也可以复用
     */
    protected void initViewListener() {
//        adapter.setHeaderAndEmpty(true);
//        adapter.setHeaderView()
    }

    protected abstract BaseQuickAdapter getAdapter();

    private View initEmptyView() {
        View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.layout_empty_default_view, mRvContent, false);
        ImageView emptyIv = emptyView.findViewById(R.id.iv_empty_image);
        emptyIv.setImageResource(getEmptyImage());
        TextView emptyText = emptyView.findViewById(R.id.tv_empty_msg);
        emptyText.setText(getEmptyText());
        changeEmptyParam(emptyView);
        return emptyView;
    }

    /**
     * 详情的缺醒图可以使用
     *
     * @param emptyView
     */
    public void changeEmptyParam(View emptyView) {

    }

    /**
     * 子类基本都需要重写
     *
     * @return
     */
    public String getEmptyText() {
        return "空空如也，快去和段友们互动吧";
    }

    public int getEmptyImage() {
        return R.mipmap.no_shoucang;
    }


    @Override
    public void onRefresh() {
        if (!NetWorkUtils.isNetworkConnected(MyApplication.getInstance())) {
            mStatesView.setCurrentState(StateView.STATE_ERROR);
            return;
        }
        if (mStatesView.getCurrentState() != StateView.STATE_CONTENT) {
            mStatesView.setCurrentState(StateView.STATE_CONTENT);
        }
        if (adapter != null) {
            adapter.setEnableLoadMore(true);
        }
        position = 1;
        netWorkState = DateState.refresh_state;
        getNetWorkDate(DateState.refresh_state);

    }

    public int netWorkState;

    @Override
    public void onLoadMoreRequested() {
        if (!NetWorkUtils.isNetworkConnected(MyApplication.getInstance())) {
            if (adapter != null) {
                adapter.loadMoreFail();
                return;
            }
        }
        netWorkState = DateState.load_more;
        getNetWorkDate(DateState.load_more);
    }

    public int position = 1;
    public static final String pageSize = "20";

    protected abstract void getNetWorkDate(@DateState int load_more);

    /* 一下方法给子类用于网络请求之后的操作调用*/

    protected void errorLoad() {
        if (netWorkState == DateState.init_state) {
            mStatesView.setCurrentState(StateView.STATE_CONTENT);
        }
        if (adapter != null) {
            adapter.loadMoreFail();
        }
        if (mSwipeLayout != null) {
            mSwipeLayout.setRefreshing(false);
        }
    }

    /**
     * 请求完接口处理数据,而且必须得是在成功的回调里调用
     *
     * @param load_more
     * @param newDate
     */
    protected void setDate(@DateState int load_more, List<T> newDate) {
        if (adapter == null) return;
        if (load_more == DateState.init_state) {
            mStatesView.setCurrentState(StateView.STATE_CONTENT);
        }
        if (load_more == DateState.refresh_state && mStatesView.getCurrentState() != StateView.STATE_CONTENT) {
            mStatesView.setCurrentState(StateView.STATE_CONTENT);
        }
        if (load_more == DateState.refresh_state || load_more == DateState.init_state) {
            adapter.setNewData(newDate);
            if (newDate != null && newDate.size() < getPageSize()) {
                adapter.loadMoreEnd();
            }
            mSwipeLayout.setRefreshing(false);
        } else {
            adapter.addData(newDate);
            if (newDate != null && newDate.size() < getPageSize()) {
                adapter.loadMoreEnd();
            } else {
                adapter.loadMoreComplete();
            }
        }
        position++;
    }

    /**
     * 默认是二十页处理,像首页浏览列表则不是20,不需要判断页面
     *
     * @return
     */
    public int getPageSize() {
        return 20;
    }


    //目标项是否在最后一个可见项之后
    public boolean mShouldScroll;
    //记录目标项位置
    public int mToPosition;

    /**
     * 滑动到指定位置
     */
    public void smoothMoveToPosition(final int position) {
        if (mRvContent == null) return;
        // 第一个可见位置
        int firstItem = mRvContent.getChildLayoutPosition(mRvContent.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRvContent.getChildLayoutPosition(mRvContent.getChildAt(mRvContent.getChildCount() - 1));
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前，使用smoothScrollToPosition
            mRvContent.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后，最后一个可见项之前
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRvContent.getChildCount()) {
                int top = mRvContent.getChildAt(movePosition).getTop();
                // smoothScrollToPosition 不会有效果，此时调用smoothScrollBy来滑动到指定位置
                mRvContent.smoothScrollBy(0, top);
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法
            mRvContent.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }

    public int getScollYDistance() {
        if (mRvContent.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) mRvContent.getLayoutManager();
            int position = layoutManager.findFirstVisibleItemPosition();
            View firstVisiableChildView = layoutManager.findViewByPosition(position);
            int itemHeight = firstVisiableChildView.getHeight();
            return (position) * itemHeight - firstVisiableChildView.getTop();
        } else {
            return 0;
        }
    }
}
