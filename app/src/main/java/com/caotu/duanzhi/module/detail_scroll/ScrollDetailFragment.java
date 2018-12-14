package com.caotu.duanzhi.module.detail_scroll;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.module.home.ContentDetailFragment;
import com.caotu.duanzhi.view.widget.StateView;

import cn.jzvd.Jzvd;

public class ScrollDetailFragment extends ContentDetailFragment {
    //只处理懒加载问题
    @Override
    public boolean isNeedLazyLoadDate() {
        return true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            Jzvd.releaseAllVideos();
        } else {
            // TODO: 2018/12/14 自动播放
            if (viewHolder != null) {
                MyApplication.getInstance().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewHolder.autoPlayVideo();
                    }
                },200);
            }
        }
    }

    @Override
    protected void initHeader() {
        super.initHeader();
        mStatesView.setCurrentState(StateView.STATE_CONTENT);
    }
}
