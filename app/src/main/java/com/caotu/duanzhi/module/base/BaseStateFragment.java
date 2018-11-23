package com.caotu.duanzhi.module.base;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
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

//    @Retention(RetentionPolicy.SOURCE)
//    @IntRange(from = 100, to = 102)
//    public @interface DateState {
//        int init_state = 100;
//        int refresh_state = 101;
//        int load_more = 102;
//    }

//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     */
//    public static BaseFragment newInstance(String param1, String param2) {
//        BaseFragment fragment = new BaseFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }


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
//        else {
//            mStatesView.setCurrentState(StateView.STATE_CONTENT);
//        }
        if (mSwipeLayout != null) {
            mSwipeLayout.setRefreshing(true);
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
        mStatesView.setCurrentState(StateView.STATE_LOADING);
//        mRvContent.setLayoutManager(new LinearLayoutManager(inflate.getContext()));
        //条目布局
        adapter = getAdapter();
        adapter.setHasStableIds(true);
        adapter.bindToRecyclerView(mRvContent);
        adapter.setEmptyView(initEmptyView());
        adapter.closeLoadAnimation();
//        mRvContent.setAdapter(adapter);
        adapter.setOnLoadMoreListener(this, mRvContent);

        mSwipeLayout.setOnRefreshListener(this);
        initViewListener();
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
        return emptyView;
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
            mSwipeLayout.setRefreshing(false);
        }
    }

    protected void enableLoadMore(boolean isCan) {
        if (adapter != null) {
            adapter.setEnableLoadMore(isCan);
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

}
