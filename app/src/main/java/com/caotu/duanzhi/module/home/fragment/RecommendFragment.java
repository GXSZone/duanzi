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
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.constant.RefreshState;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


public class RecommendFragment extends BaseVideoFragment implements IHomeRefresh {

    private String pageno = "";
    private String registrationID;

    @Override
    public int getPageSize() {
        return 3;
    }

    public boolean getHasReport() {
        return true;
    }

    //13065ffa4e76dd4bee6(极光获取)       21e875ee-512f-3ff1-a37a-d2397b9766cc(自己获取)
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        registrationID = DevicesUtils.getDeviceId(MyApplication.getInstance());
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        HashMap<String, String> hashMapParams = CommonHttpRequest.getInstance().getHashMapParams();
        hashMapParams.put("uuid", registrationID);
        hashMapParams.put("pageno", pageno);
        hashMapParams.put("ishowadv", DateState.init_state == load_more ? "N" : "Y");
        int size = adapter == null ? 0 : adapter.getData().size();
        final StringBuilder contentIdList = new StringBuilder();
        for (int i = size - 1; i >= 0; i--) {
            if (i == size - 13) break;
            String contentid = adapter.getData().get(i).getContentid();
            contentIdList.append(contentid).append(",");
        }
        hashMapParams.put("contentidlist", contentIdList.toString());

        OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.MAIN_RECOMMEND_CONTENT)
                .upJson(new JSONObject(hashMapParams))
                .execute(new JsonCallback<BaseResponseBean<RedundantBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<RedundantBean>> response) {
                        //	回执页码
                        pageno = response.body().getData().pageno;
                        List<MomentsDataBean> rows = response.body().getData().getContentList();
                        // TODO: 2019-10-30 通过调用这个方法数据集已经处理过,但是不是一个全集合
                        setDate(load_more, rows);

                        if (getParentFragment() instanceof MainHomeNewFragment
                                && (DateState.refresh_state == load_more || DateState.init_state == load_more)
                                //该条件是为了不是当前页就不展示了
                                && dateCallBack == null) {
                            int size = rows == null ? 0 : rows.size();
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

    public void addPublishDate(MomentsDataBean dataBean) {
        if (adapter != null) {
            adapter.addData(0, dataBean);
        }
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
