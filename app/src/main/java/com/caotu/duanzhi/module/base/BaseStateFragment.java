package com.caotu.duanzhi.module.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.NetWorkUtils;
import com.caotu.duanzhi.view.refresh_header.SpaceBottomMoreView;
import com.caotu.duanzhi.view.widget.StateView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

/**
 * 该泛型指的是列表list 的bean对象
 *
 * @param <T>
 */
public abstract class BaseStateFragment<T> extends BaseFragment implements BaseQuickAdapter.RequestLoadMoreListener, OnRefreshListener {
    public RecyclerView mRvContent;
    public RefreshLayout mSwipeLayout;
    public StateView mStatesView;
    public BaseQuickAdapter<T, BaseViewHolder> adapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.layout_base_states_view;
    }

    public void setInitPosition() {
        position = 1;
    }

    @Override
    protected void initDate() {
        if (!NetWorkUtils.isNetworkConnected(MyApplication.getInstance())) {
            mStatesView.setCurrentState(StateView.STATE_ERROR);
            return;
        }
        setInitPosition();
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
        adapter.setLoadMoreView(new SpaceBottomMoreView());
        //这里其实就是绑定adapter
        adapter.bindToRecyclerView(mRvContent);
        adapter.setEmptyView(initEmptyView());
        adapter.setOnLoadMoreListener(this, mRvContent);
        if (mSwipeLayout != null) {
            mSwipeLayout.setOnRefreshListener(this);
            mSwipeLayout.setEnableLoadMore(false);
            mSwipeLayout.setEnableAutoLoadMore(false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewListener();
    }

    /**
     * 给子类用于初始化操作,adapter加头布局也可以,这样adapter也可以复用
     */
    protected void initViewListener() {

    }

    protected abstract BaseQuickAdapter getAdapter();

    private View initEmptyView() {
        View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.layout_empty_has_header, mRvContent, false);
        ImageView emptyIv = emptyView.findViewById(R.id.iv_empty_image);
        emptyIv.setImageResource(getEmptyImage());
        TextView emptyText = emptyView.findViewById(R.id.tv_empty_msg);
        emptyText.setText(getEmptyText());
        return emptyView;
    }

    /**
     * 子类基本都需要重写
     *
     * @return
     */
    public String getEmptyText() {
        return "暂无更新,去发现看看吧";
    }

    public int getEmptyImage() {
        return R.mipmap.no_tiezi;
    }

    public boolean isRefreshReset() {
        return true;
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (!NetWorkUtils.isNetworkConnected(MyApplication.getInstance())) {
            mSwipeLayout.finishRefresh();
            mStatesView.setCurrentState(StateView.STATE_ERROR);
            return;
        }
        if (isRefreshReset()) {
            position = 1;
        }
        netWorkState = DateState.refresh_state;
        getNetWorkDate(DateState.refresh_state);
    }

    @Override
    public void onLoadMoreRequested() {
        if (!NetWorkUtils.isNetworkConnected(MyApplication.getInstance())) {
            adapter.loadMoreFail();
            return;
        }
        netWorkState = DateState.load_more;
        getNetWorkDate(DateState.load_more);
    }

    public int netWorkState;
    public int position = 1;
    public static final String pageSize = "20";

    protected abstract void getNetWorkDate(@DateState int load_more);

    /* 一下方法给子类用于网络请求之后的操作调用*/

    protected void errorLoad() {
        if (netWorkState == DateState.init_state) {
            mStatesView.setCurrentState(StateView.STATE_ERROR);
        }
        if (adapter != null) {
            adapter.loadMoreFail();
        }
        if (mSwipeLayout != null && mSwipeLayout.getState() == RefreshState.Refreshing) {
            mSwipeLayout.finishRefresh();
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
        if (load_more != DateState.load_more &&
                mStatesView.getCurrentState() != StateView.STATE_CONTENT) {
            mStatesView.setCurrentState(StateView.STATE_CONTENT);
        }
        if (load_more != DateState.load_more) {
            adapter.setNewData(newDate);
            if (newDate != null && newDate.size() < getPageSize()) {
                adapter.loadMoreEnd();
            }
            if (mSwipeLayout != null) {
                mSwipeLayout.finishRefresh();
            }
        } else {
            if (AppUtil.listHasDate(newDate)) {
                adapter.addData(newDate);
            }
            if (newDate == null || newDate.size() < getPageSize()) {
                adapter.loadMoreEnd();
            } else {
                adapter.loadMoreComplete();
            }
        }
        position++;
    }

    /**
     * 默认是二十页处理,像首页浏览列表则不是20,不需要判断页面
     * 判断是否还能加载更多
     *
     * @return
     */
    public int getPageSize() {
        return 5;
    }


    //目标项是否在最后一个可见项之后
    public boolean mShouldScroll;
    //记录目标项位置
    public int mToPosition;

    /**
     * 滑动到指定位置,动画版
     */
    public void smoothMoveToPosition(final int position, boolean isNeedSmooth) {
        if (mRvContent == null) return;
        // 第一个可见位置
        int firstItem = mRvContent.getChildLayoutPosition(mRvContent.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRvContent.getChildLayoutPosition(mRvContent.getChildAt(mRvContent.getChildCount() - 1));
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前，使用smoothScrollToPosition
            if (isNeedSmooth) {
                mRvContent.smoothScrollToPosition(position);
            } else {
                mRvContent.scrollToPosition(position);
            }
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后，最后一个可见项之前
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRvContent.getChildCount()) {
                int top = mRvContent.getChildAt(movePosition).getTop();
                // smoothScrollToPosition 不会有效果，此时调用smoothScrollBy来滑动到指定位置
                mRvContent.scrollBy(0, top);
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法
            if (isNeedSmooth) {
                mRvContent.smoothScrollToPosition(position);
            } else {
                mRvContent.scrollToPosition(position);
            }
            mToPosition = position;
            mShouldScroll = true;
        }
    }

    /**
     * 滑动到指定位置,无动画版
     */
    public void moveToPosition(int index) {
        LinearLayoutManager manager = (LinearLayoutManager) mRvContent.getLayoutManager();
        if (manager != null) {
            manager.scrollToPositionWithOffset(index, 0);
        }
    }
}
