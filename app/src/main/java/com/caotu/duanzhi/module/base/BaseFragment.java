package com.caotu.duanzhi.module.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public abstract class BaseFragment extends Fragment {
    /**
     * 是否初始化过布局
     */
    protected boolean isViewInitiated;
    /**
     * 当前界面是否可见
     */
    public boolean isVisibleToUser;
    /**
     * 是否加载过数据
     */
    protected boolean isDataInitiated;
    protected View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //todo 解决fragment的bug
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
            return rootView;
        }

        rootView = inflater.inflate(getLayoutRes(), container, false);
        initView(rootView);
        isViewInitiated = true;
        if (isNeedLazyLoadDate()) {
            prepareFetchData();
        } else {
            initDate();
        }
        return rootView;
    }

    protected abstract @LayoutRes
    int getLayoutRes();

    protected abstract void initDate();

    protected abstract void initView(View inflate);

    public boolean isResum = false;

    @Override
    public void onPause() {
        super.onPause();
        isResum = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isResum = true;
    }


    /**
     * 判断懒加载条件
     * 可见状态才请求,并且只在初始化请求,只在viewpager中生效setUserVisibleHint回调
     */
    public void prepareFetchData() {

        if (isVisibleToUser && isViewInitiated && !isDataInitiated) {
            initDate();
            isDataInitiated = true;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isNeedLazyLoadDate()) {
            prepareFetchData();
        }
    }

    @Override
    public void onDestroyView() {
        isViewInitiated = false;
        isDataInitiated = false;
        super.onDestroyView();
    }

    /**
     * 子类只要return true则是懒加载模式
     *
     * @return
     */
    public boolean isNeedLazyLoadDate() {
        return false;
    }
}
