package com.caotu.duanzhi.module.base;

import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.NetWorkUtils;
import com.caotu.duanzhi.view.widget.StateView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * 该泛型指的是列表list 的bean对象
 *
 * @param <T>
 */
public abstract class BaseStateFragment<T> extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private RecyclerView mRvContent;
    private SwipeRefreshLayout mSwipeLayout;
    private StateView mStatesView;
    private BaseQuickAdapter<T, BaseViewHolder> adapter;

    @Retention(RetentionPolicy.SOURCE)
    @IntRange(from = 100, to = 102)
    public @interface DateState {
        int init_state = 100;
        int refresh_state = 101;
        int load_more = 102;
    }

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
        } else {
            mStatesView.setCurrentState(StateView.STATE_CONTENT);
        }
        getNetWorkDate(DateState.init_state);
    }

    @Override
    protected void initView(View inflate) {
        mStatesView = inflate.findViewById(R.id.states_view);
        mRvContent = inflate.findViewById(R.id.rv_content);
        mSwipeLayout = inflate.findViewById(R.id.swipe_layout);
        mRvContent.setLayoutManager(new LinearLayoutManager(inflate.getContext()));
        //条目布局
        adapter = getAdapter();
        adapter.setEmptyView(getEmptyView(), mRvContent);
        mRvContent.setAdapter(adapter);
        adapter.setOnLoadMoreListener(this, mRvContent);

        mSwipeLayout.setOnRefreshListener(this);
        initViewListener();
    }

    /**
     * 给子类用于初始化操作
     */
    protected void initViewListener() {

    }

    protected abstract BaseQuickAdapter getAdapter();

    private @LayoutRes
    int getEmptyView() {
        return R.layout.layout_empty_default_view;
    }

    protected abstract @LayoutRes
    int getItemLayout();

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
        getNetWorkDate(DateState.refresh_state);

    }

    @Override
    public void onLoadMoreRequested() {
        if (!NetWorkUtils.isNetworkConnected(MyApplication.getInstance())) {
            if (adapter != null) {
                adapter.loadMoreFail();
                return;
            }
        }
        getNetWorkDate(DateState.load_more);
    }

    protected abstract void getNetWorkDate(@DateState int load_more);

    /* 一下方法给子类用于网络请求之后的操作调用*/

    protected void endLoadMore() {
        if (adapter != null) {
            adapter.loadMoreEnd();
        }
    }

    protected void enableLoadMore(boolean isCan) {
        if (adapter != null) {
            adapter.setEnableLoadMore(isCan);
        }
    }

    protected void setLoadMoreDate(List<T> list) {
        if (adapter != null) {
            adapter.addData(list);
        }
    }

    protected void setNewDate(List<T> newDate) {
        if (adapter != null) {
            adapter.addData(newDate);
        }
    }

}
