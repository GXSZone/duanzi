package com.caotu.duanzhi.module.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author mac
 * @日期: 2018/11/1
 * @describe 懒加载fragment
 */
public abstract class LazyLoadFragment extends Fragment {
    /**
     * 是否初始化过布局
     */
    protected boolean isViewInitiated;
    /**
     * 当前界面是否可见
     */
    protected boolean isVisibleToUser;
    /**
     * 是否加载过数据
     */
    protected boolean isDataInitiated;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(getLayoutRes(), container, false);
        initView(inflate);
        isViewInitiated = true;
        prepareFetchData();
        return inflate;
    }

    protected abstract @LayoutRes
    int getLayoutRes();


    protected abstract void initView(View inflate);


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            prepareFetchData();
        }
    }

    /**
     * 懒加载
     */
    public abstract void fetchData();

    /**
     * 判断懒加载条件
     * 可见状态才请求,并且只在初始化请求,只在viewpager中生效setUserVisibleHint回调
     */
    public void prepareFetchData() {
        //!isDataInitiated 少了该条件,即每次可见都请求,为了数据同步性
        if (isVisibleToUser && isViewInitiated) {
            fetchData();
            isDataInitiated = true;
        }
    }
}
