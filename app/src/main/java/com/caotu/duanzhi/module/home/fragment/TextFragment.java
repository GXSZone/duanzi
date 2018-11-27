package com.caotu.duanzhi.module.home.fragment;


import android.util.Log;

import com.caotu.duanzhi.module.home.adapter.TextAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;

public class TextFragment extends BaseNoVideoFragment{

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new TextAdapter();
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        Log.i("lazyLog", "TextFragment ");
    }
}
