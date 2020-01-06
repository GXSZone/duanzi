package com.caotu.duanzhi.module.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
            return rootView;
        }
        rootView = inflater.inflate(getLayoutRes(), container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        isViewInitiated = true;
        if (isNeedLazyLoadDate()) {
            prepareFetchData();
        } else {
            initDate();
        }
    }

    protected abstract @LayoutRes
    int getLayoutRes();

    /**
     * 该方法正常用于初始化的数据绑定,就算是懒加载也只会调用一次,在页面可见的时候回调
     */
    protected abstract void initDate();

    protected abstract void initView(View inflate);


    /**
     * 判断懒加载条件
     * 可见状态才请求,并且只在初始化请求,只在viewpager中生效setUserVisibleHint回调
     */
    private void prepareFetchData() {
        if (isVisibleToUser && isViewInitiated && !isDataInitiated) {
            initDate();
            isDataInitiated = true;
        }
        if (isNeedLazyLoadDate() && isVisibleToUser && isViewInitiated) {
            fragmentInViewpagerVisibleToUser();
        }
    }

    /**
     * 该方法用于fragment在viewpager中的懒加载,每次可见都会回调
     */
    public void fragmentInViewpagerVisibleToUser() {

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
     * 子类只要return true则是懒加载模式,该懒加载只针对fragment在viewpager中才可以
     * 不在viewpager中没有懒加载效果
     *
     * @return
     */
    public boolean isNeedLazyLoadDate() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasSkip) {
            onReStart();
            hasSkip = false;
        }
    }

    /**
     * 自己参考activity的生命周期的回调,界面重新可见的时候回调
     */
    public void onReStart() {

    }

    private boolean hasSkip = false;

    /**
     * 用于是否从该页面跳转出去
     */
    @Override
    public void onPause() {
        super.onPause();
        hasSkip = true;
    }

    protected void closeSoftKeyboard(EditText editText) {
        if (getContext() == null || editText == null) return;
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    protected void showKeyboard(EditText editText) {
        if (getContext() == null || editText == null) return;
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }
}
