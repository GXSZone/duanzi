package com.caotu.duanzhi.module.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public abstract class BaseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(getLayoutRes(), container, false);
        initView(inflate);
        initDate();
        return inflate;
    }

    protected abstract @LayoutRes int getLayoutRes();

    protected abstract void initDate();

    protected abstract void initView(View inflate);

}
