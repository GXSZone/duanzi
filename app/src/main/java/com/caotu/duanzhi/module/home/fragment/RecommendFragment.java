package com.caotu.duanzhi.module.home.fragment;

import android.content.Context;
import android.text.TextUtils;

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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import cn.jzvd.Jzvd;


public class RecommendFragment extends BaseVideoFragment implements IHomeRefresh {


    private String pageno = "";
    private String registrationID;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            Jzvd.releaseAllVideos();
        }
    }

    @Override
    public int getPageSize() {
        return 3;
    }

    public boolean getHasReport() {
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        registrationID = JPushInterface.getRegistrationID(MyApplication.getInstance());
//        if (TextUtils.isEmpty(registrationID)) {
//            registrationID = DevicesUtils.getDeviceId(MyApplication.getInstance());
//        }
        registrationID = DevicesUtils.getDeviceId(MyApplication.getInstance());
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        HashMap<String, String> hashMapParams = CommonHttpRequest.getInstance().getHashMapParams();
        hashMapParams.put("uuid", registrationID);
        hashMapParams.put("pageno", pageno);

        int size = adapter == null ? 0 : adapter.getData().size();
        StringBuilder contentidlist = new StringBuilder();
        if (size > 1) {
            for (int i = size - 1; i >= 0; i--) {
                if (contentidlist.lastIndexOf(",") == 12) break;
                String contentid = adapter.getData().get(i).getContentid();
                contentidlist.append(contentid).append(",");
            }
        }
        hashMapParams.put("contentidlist", contentidlist.toString());

        OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.MAIN_RECOMMEND_CONTENT)
                .upJson(new JSONObject(hashMapParams))
                .execute(new JsonCallback<BaseResponseBean<RedundantBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<RedundantBean>> response) {
                        //	回执页码
                        pageno = response.body().getData().pageno;
                        List<MomentsDataBean> rows = response.body().getData().getContentList();
                        setDate(load_more, rows);
                        //回调给滑动详情页数据
                        if (DateState.load_more == load_more && dateCallBack != null) {
                            dateCallBack.loadMoreDate(rows);
                            dateCallBack = null;
                        }
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
                        errorLoad();
                        super.onError(response);
                    }
                });
    }

    public void addPublishDate(MomentsDataBean dataBean) {
        if (adapter != null) {
            adapter.getData().add(0, dataBean);
            adapter.notifyDataSetChanged();
        }
    }

    boolean isRefreshing = false;

    @Override
    public void refreshDate() {
        if (isRefreshing) {
            ToastUtil.showShort("您的操作太频繁");
            return;
        }
        if (mRvContent != null) {
            smoothMoveToPosition(0);
            isRefreshing = true;
            mRvContent.postDelayed(() -> {
                getNetWorkDate(DateState.refresh_state);
                Jzvd.releaseAllVideos();
                isRefreshing = false;
            }, 200);
        }
    }

    public void recycleviewScroll(EventBusObject eventBusObject) {
        // TODO: 2018/12/26 为了过滤
        if (!isVisibleToUser) return;
        if (getActivity() != null && !TextUtils.equals(getActivity().getLocalClassName(), eventBusObject.getTag()))
            return;
        int position = (int) eventBusObject.getObj();
        if (adapter != null) {
            position = position + adapter.getHeaderLayoutCount();
        }
        smoothMoveToPosition(position);
    }
}
