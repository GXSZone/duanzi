package com.caotu.duanzhi.module.home.fragment;


import android.util.Log;

import com.caotu.duanzhi.module.home.adapter.PhotoAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;

public class PhotoFragment extends BaseNoVideoFragment {


    @Override
    protected BaseQuickAdapter getAdapter() {
        return new PhotoAdapter();
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        Log.i("lazyLog", "PhotoFragment ");
    }
}
