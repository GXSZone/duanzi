package com.caotu.duanzhi.module.search;

import android.text.TextUtils;
import android.view.View;

import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.view.widget.StateView;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.HashMap;

/**
 * 后面当做搜索的分栏fragment,除了综合的分栏不一样
 */
public abstract class SearchBaseFragment<T> extends BaseStateFragment<T> implements
        BaseQuickAdapter.OnItemClickListener, SearchDate, IEmpty {
    public String searchWord;

    @Override
    protected void initView(View inflate) {
        super.initView(inflate);
        mStatesView.setCurrentState(StateView.STATE_EMPTY);
        mRvContent.setBackgroundColor(DevicesUtils.getColor(R.color.white));
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
        if (TextUtils.equals(searchWord, trim)) return;
        searchWord = trim;
        if (TextUtils.isEmpty(searchWord)) return;
        //注意索引
        position = 1;
        getNetWorkDate(DateState.init_state);
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        if (TextUtils.isEmpty(searchWord)) return;
        if (load_more != DateState.load_more) {
            mStatesView.setCurrentState(StateView.STATE_LOADING);
        }
        HashMap<String, String> param = new HashMap<>();
        param.put("pageno", position + "");
        param.put("pagesize", pageSize);
        param.put("querystr", searchWord);
        httpRequest(load_more, param);
    }

    protected abstract void httpRequest(int load_more, HashMap<String, String> param);

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        T date = (T) adapter.getData().get(position);
        clickItem(date);
    }

    protected abstract void clickItem(T date);

    @Override
    public void changeEmpty() {
        mStatesView.setCurrentState(StateView.STATE_EMPTY);
    }

    @Override
    public void resetSearchWord() {
        searchWord = null;
    }
}
