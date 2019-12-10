package com.caotu.duanzhi.module.search;

import android.view.View;

import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.view.widget.StateView;
import com.chad.library.adapter.base.BaseQuickAdapter;

/**
 * 后面当做搜索的分栏fragment,除了综合的分栏不一样
 */
public abstract class SearchBaseFragment<T> extends BaseStateFragment<T> implements
        BaseQuickAdapter.OnItemClickListener, SearchDate {
    public String searchWord;

    @Override
    protected void initView(View inflate) {
        super.initView(inflate);
        //注意这里把loading 状态当初始化布局
        mStatesView.setViewForState(R.layout.layout_search_init, StateView.STATE_LOADING, true);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.layout_no_refresh;
    }

    @Override
    public int getEmptyImage() {
        return R.mipmap.no_tiezi;
    }

    @Override
    public String getEmptyText() {
        return "找了又找，还是没找到相关内容";
    }


    @Override
    public void setDate(String trim) {
        searchWord = trim;
        //注意索引
        position = 1;
        getNetWorkDate(DateState.init_state);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        T date = (T) adapter.getData().get(position);

    }
}
