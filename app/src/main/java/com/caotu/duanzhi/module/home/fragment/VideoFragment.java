package com.caotu.duanzhi.module.home.fragment;

import android.content.Context;

import androidx.annotation.NonNull;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.EventBusObject;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.RedundantBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseVideoFragment;
import com.caotu.duanzhi.module.home.MainHomeNewFragment;
import com.caotu.duanzhi.module.home.adapter.VideoAdapter;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.NetWorkUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.widget.StateView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * 这里需要代码优化,既需要BaseVideoFragment 的特性,也需要LazyLoadJustInitFragment 懒加载的特性
 */
public class VideoFragment extends BaseVideoFragment implements IHomeRefresh {
    String deviceId;
    private List<MomentsDataBean> contentList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        deviceId = DevicesUtils.getDeviceId(MyApplication.getInstance());
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (!NetWorkUtils.isNetworkConnected(MyApplication.getInstance())) {
            mStatesView.setCurrentState(StateView.STATE_ERROR);
            return;
        }
        if (mStatesView.getCurrentState() != StateView.STATE_CONTENT) {
            mStatesView.setCurrentState(StateView.STATE_CONTENT);
        }
        if (adapter != null) {
            adapter.setEnableLoadMore(true);
        }
        // TODO: 2018/12/4 特殊之处,不管是刷新还是加载更多都是+1;
        position++;
        netWorkState = DateState.refresh_state;
        getNetWorkDate(DateState.refresh_state);
        VideoViewManager.instance().stopPlayback();
    }

    private String pageno = "";

    @Override
    protected void getNetWorkDate(int load_more) {
        HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
        params.put("pageno", pageno);
        params.put("pagesize", "20");
        params.put("querytype", "vie");
        params.put("uuid", deviceId);
        int size = contentList == null ? 0 : contentList.size();
        StringBuilder contentidlist = new StringBuilder();
        if (size > 1) {
            for (int i = 0; i < size; i++) {
                String contentid = contentList.get(i).getContentid();
                contentidlist.append(contentid).append(",");
            }
        }
        params.put("contentidlist", contentidlist.toString());
//        Log.i("videoIndex", "getNetWorkDate: " + position);
        String jsonObject = new JSONObject(params).toString();
        OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.HOME_TYPE)
                .upJson(jsonObject)
                .execute(new JsonCallback<BaseResponseBean<RedundantBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<RedundantBean>> response) {
                        //	回执页码
                        pageno = response.body().getData().pageno;
                        contentList = response.body().getData().getContentList();
                        setDate(load_more, contentList);
                        //回调给滑动详情页数据
                        if (DateState.load_more == load_more && dateCallBack != null) {
                            dateCallBack.loadMoreDate(contentList);
                            dateCallBack = null;
                        }
                        if (getParentFragment() instanceof MainHomeNewFragment
                                && (DateState.refresh_state == load_more || DateState.init_state == load_more)) {
                            int size = contentList == null ? 0 : contentList.size();
                            ((MainHomeNewFragment) getParentFragment()).showRefreshTip(size);
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<RedundantBean>> response) {
                        ToastUtil.showShort(response.message());
                        errorLoad();
                        super.onError(response);
                    }
                });

    }

    @Override
    public boolean isNeedLazyLoadDate() {
        return true;
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new VideoAdapter();
    }

    @Override
    public int getPageSize() {
        return 10;
    }

    public boolean getHasReport() {
        return true;
    }

    Runnable runnable = () -> getNetWorkDate(DateState.refresh_state);

    /**
     * 用于给首页的刷新按钮刷新调用
     */
    @Override
    public void refreshDate() {
        if (mRvContent != null) {
            VideoViewManager.instance().stopPlayback();
            smoothMoveToPosition(0,false);
            mRvContent.removeCallbacks(runnable);
            mRvContent.postDelayed(runnable, 300);
        }
    }

    public void recycleviewScroll(EventBusObject eventBusObject) {
        if (isVisibleToUser) {
            super.recycleviewScroll(eventBusObject);
        }
    }

    @Override
    public void refreshItem(EventBusObject eventBusObject) {
        if (isVisibleToUser) {
            super.refreshItem(eventBusObject);
        }
    }
}
