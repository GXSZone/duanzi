package com.caotu.duanzhi.module.home.fragment;

import android.content.Context;
import android.util.Log;

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
import com.caotu.duanzhi.view.widget.StateView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import cn.jzvd.Jzvd;

/**
 * 这里需要代码优化,既需要BaseVideoFragment 的特性,也需要LazyLoadJustInitFragment 懒加载的特性
 */
public class VideoFragment extends BaseVideoFragment implements IHomeRefresh {
    String deviceId;
    private VideoAdapter videoAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        deviceId = DevicesUtils.getDeviceId(MyApplication.getInstance());
    }

    @Override
    public void onRefresh() {
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
        Jzvd.releaseAllVideos();
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
        params.put("pageno", position + "");
        params.put("pagesize", "20");
        params.put("querytype", "vie");
        params.put("uuid", deviceId);
        Log.i("videoIndex", "getNetWorkDate: " + position);
        String jsonObject = new JSONObject(params).toString();
        OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.HOME_TYPE)
                .upJson(jsonObject)
                .execute(new JsonCallback<BaseResponseBean<RedundantBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<RedundantBean>> response) {
                        List<MomentsDataBean> contentList = response.body().getData().getContentList();
                        if (DateState.refresh_state == load_more && (contentList == null || contentList.size() == 0)) {
                            Log.i("videoIndex", "重新请求第一页数据");
                            position = 1;
                            getNetWorkDate(load_more);
                            return;
                        }
                        setDate(load_more, contentList);
                        if (getParentFragment() instanceof MainHomeNewFragment
                                && (DateState.refresh_state == load_more || DateState.init_state == load_more)) {
                            int size = contentList == null ? 0 : contentList.size();
                            ((MainHomeNewFragment) getParentFragment()).showRefreshTip(size);
                        }
                    }
                });

    }

    @Override
    public boolean isNeedLazyLoadDate() {
        return true;
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        videoAdapter = new VideoAdapter();
        videoAdapter.setTextClick(this);
        return videoAdapter;
    }

    @Override
    public int getPageSize() {
        return 1;
    }

    public boolean getHasReport() {
        return true;
    }

    @Override
    public void refreshDate() {
        if (mRvContent != null) {
            mRvContent.smoothScrollToPosition(0);
        }
        MyApplication.getInstance().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getNetWorkDate(DateState.refresh_state);
                Jzvd.releaseAllVideos();
            }
        }, 200);
    }

    public void changeItem(EventBusObject eventBusObject) {
        //不可见的时候说明不是他自己fragment跳转出去的
        if (!isVisibleToUser) return;
        MomentsDataBean changeBean = (MomentsDataBean) eventBusObject.getObj();
        if (videoAdapter != null) {
            //更改list数据
            MomentsDataBean momentsDataBean = videoAdapter.getData().get(skipIndex);
            momentsDataBean.setGoodstatus(changeBean.getGoodstatus());
            momentsDataBean.setContentgood(changeBean.getContentgood());
            momentsDataBean.setContentbad(changeBean.getContentbad());
            momentsDataBean.setIsfollow(changeBean.getIsfollow());
            momentsDataBean.setContentcomment(changeBean.getContentcomment());
            momentsDataBean.setIscollection(changeBean.getIscollection());
            videoAdapter.notifyItemChanged(skipIndex, momentsDataBean);
        }
    }
}
