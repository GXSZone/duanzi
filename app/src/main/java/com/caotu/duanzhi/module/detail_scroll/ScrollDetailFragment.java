package com.caotu.duanzhi.module.detail_scroll;

import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.module.home.ContentDetailActivity;
import com.caotu.duanzhi.module.home.ContentDetailFragment;
import com.caotu.duanzhi.view.widget.StateView;

import org.greenrobot.eventbus.EventBus;

public class ScrollDetailFragment extends ContentDetailFragment {
//    //只处理懒加载问题
//    @Override
//    public boolean isNeedLazyLoadDate() {
//        return true;
//    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            MyApplication.getInstance().putHistory(contentId);
        }
        if (isVisibleToUser) {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        } else {
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
        }
    }

    @Override
    protected void initHeader() {
        super.initHeader();
        mStatesView.setCurrentState(StateView.STATE_CONTENT);
    }

    public void changeHeaderUi(MomentsDataBean data, boolean isSkip) {
        content = data;
        if (viewHolder != null) {
            //这里多加了条件
            if (isSkip && isVisibleToUser) {
                viewHolder.justBindCountAndState(data);
            } else {
                viewHolder.bindDate(data);
            }
        }
        if (getActivity() != null && getActivity() instanceof ContentDetailActivity) {
            ((ContentDetailActivity) getActivity()).setPresenter(data);
        }
    }
}
