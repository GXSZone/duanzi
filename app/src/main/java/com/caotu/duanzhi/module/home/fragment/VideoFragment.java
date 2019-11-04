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
import com.caotu.duanzhi.utils.ToastUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.constant.RefreshState;

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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        deviceId = DevicesUtils.getDeviceId(MyApplication.getInstance());
    }


    private String pageno = "";

    @Override
    protected void getNetWorkDate(int load_more) {
        HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
        params.put("pageno", pageno);
        params.put("querytype", "vie");
//        params.put("pagesize", "20");
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
        return 3;
    }

    public boolean getHasReport() {
        return true;
    }


    /**
     * 用于给首页的刷新按钮刷新调用
     */
    @Override
    public void refreshDate() {
        if (mRvContent != null) {
            smoothMoveToPosition(0, false);
            if (mSwipeLayout.getState() == RefreshState.Refreshing) return;
            mSwipeLayout.autoRefresh();
        }
    }

    public void rvScroll(EventBusObject eventBusObject) {
        if (isVisibleToUser) {
            super.rvScroll(eventBusObject);
        }
    }

    @Override
    public void refreshItem(EventBusObject eventBusObject) {
        if (isVisibleToUser) {
            super.refreshItem(eventBusObject);
        }
    }
}
