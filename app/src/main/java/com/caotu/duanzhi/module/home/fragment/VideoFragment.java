package com.caotu.duanzhi.module.home.fragment;

import android.util.Log;

import com.caotu.duanzhi.module.base.BaseVideoFragment;
import com.caotu.duanzhi.module.home.adapter.VideoAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;

/**
 * 这里需要代码优化,既需要BaseVideoFragment 的特性,也需要LazyLoadJustInitFragment 懒加载的特性
 */
public class VideoFragment extends BaseVideoFragment implements IHomeRefresh {

    @Override
    protected void getNetWorkDate(int load_more) {
        Log.i("lazyLog", "VideoFragment ");
    }

    @Override
    public boolean isNeedLazyLoadDate() {
        return true;
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new VideoAdapter();
    }
}
