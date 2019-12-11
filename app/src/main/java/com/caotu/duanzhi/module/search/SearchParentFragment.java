package com.caotu.duanzhi.module.search;

import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.module.base.MyFragmentAdapter;
import com.caotu.duanzhi.module.other.IndicatorHelper;
import com.caotu.duanzhi.other.TextWatcherAdapter;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.view.widget.SlipViewPager;
import com.google.android.material.internal.FlowLayout;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;

public class SearchParentFragment extends BaseFragment implements SearchDate {
    private SlipViewPager mViewPager;
    private List<Fragment> fragments = new ArrayList<>(4);
    private MagicIndicator magicIndicator;
    private ViewGroup historyGroup;
    private FlowLayout searchHistoryContent;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_search_parent;
    }

    @Override
    protected void initDate() {
        if (!fragments.isEmpty()) fragments.clear();

        fragments.add(new SearchAllFragment());
        fragments.add(new SearchContentFragment());
        fragments.add(new SearchUserFragment());
        fragments.add(new SearchTopicFragment());

        //指示器的初始化
        IndicatorHelper.initIndicator(getContext(), mViewPager, magicIndicator, IndicatorHelper.SEARCH_TYPE);
        mViewPager.setAdapter(new MyFragmentAdapter(getChildFragmentManager(), fragments));
        //扩大viewpager的容量
        mViewPager.setOffscreenPageLimit(3);
    }

    @Override
    protected void initView(View inflate) {
        mViewPager = inflate.findViewById(R.id.viewpager);
        magicIndicator = inflate.findViewById(R.id.search_magic_indicator);

        View searchDelete = inflate.findViewById(R.id.search_delete);
        historyGroup = (ViewGroup) searchDelete.getParent();
        searchDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySpUtils.deleteKey(MySpUtils.SP_SEARCH_HISTORY);
                changeHistoryView(false);
            }
        });
        searchHistoryContent = inflate.findViewById(R.id.search_history_content);
        List<String> searchList = MySpUtils.getSearchList();
        if (AppUtil.listHasDate(searchList)) {
            changeHistoryView(true);
            initHistory(searchList);
        } else {
            changeHistoryView(false);
        }
        //监听搜索文本内容
        if (!(getActivity() instanceof SearchActivity)) return;
        EditText etSearch = ((SearchActivity) getActivity()).getEtSearch();
        etSearch.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s) && historyGroup.getVisibility() != View.VISIBLE) {
                    changeHistoryView(true);
                    List<String> searchList = MySpUtils.getSearchList();
                    initHistory(searchList);
                }
            }
        });
    }

    private void initHistory(List<String> searchList) {
        searchHistoryContent.removeAllViews();
        for (String s : searchList) {
            TextView view = (TextView) LayoutInflater.from(getContext())
                    .inflate(R.layout.item_search_history, searchHistoryContent, false);
            view.setText(s);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() instanceof SearchActivity) {
                        EditText etSearch = ((SearchActivity) getActivity()).getEtSearch();
                        etSearch.setText(s);
                        etSearch.setSelection(s.length());
                    }
                    changeHistoryView(false);
                    setDate(s);
                }
            });
            searchHistoryContent.addView(view);
        }
    }

    /**
     * 供综合头布局更多调用
     *
     * @param index
     */
    public void changeItem(int index) {
        mViewPager.setCurrentItem(index);
    }

    @Override
    public void setDate(String trim) {
        if (!AppUtil.listHasDate(fragments)) return;
        changeHistoryView(false);
        for (Fragment fragment : fragments) {
            if (fragment instanceof SearchDate) {
                ((SearchDate) fragment).setDate(trim);
            }
        }
        MySpUtils.putBeanToSp(trim);
    }

    public void changeHistoryView(boolean isShow) {
        if (historyGroup == null) return;
        historyGroup.setVisibility(isShow ? View.VISIBLE : View.GONE);
        mViewPager.setSlipping(!isShow);
    }
}
