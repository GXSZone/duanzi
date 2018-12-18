package com.caotu.duanzhi.module.home.fragment;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.RedundantBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseVideoFragment;
import com.caotu.duanzhi.module.home.MainHomeNewFragment;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import cn.jzvd.Jzvd;


public class RecommendFragment extends BaseVideoFragment implements IHomeRefresh {


    private String pageno = "";

    @Override
    public int getPageSize() {
        return 1;
    }

    public boolean getHasReport() {
        return true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            Jzvd.releaseAllVideos();
        }
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        HashMap<String, String> hashMapParams = CommonHttpRequest.getInstance().getHashMapParams();
        hashMapParams.put("uuid", DevicesUtils.getDeviceId(MyApplication.getInstance()));
        hashMapParams.put("pageno", pageno);
        OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.MAIN_RECOMMEND_CONTENT)
                .upJson(new JSONObject(hashMapParams))
                .execute(new JsonCallback<BaseResponseBean<RedundantBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<RedundantBean>> response) {
                        //	回执页码
                        pageno = response.body().getData().pageno;
                        List<MomentsDataBean> rows = response.body().getData().getContentList();
                        if (DateState.refresh_state == load_more && (rows == null || rows.size() == 0)) {
                            pageno = "";
                            getNetWorkDate(load_more);
                            return;
                        }
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

//    public void changeItem(EventBusObject eventBusObject) {
//        Activity lastSecondActivity = MyApplication.getInstance().getLastSecondActivity();
//        if (lastSecondActivity instanceof MainActivity && isVisibleToUser) {
//            MomentsDataBean changeBean = (MomentsDataBean) eventBusObject.getObj();
//            if (momentsNewAdapter != null) {
//                //更改list数据
//                MomentsDataBean momentsDataBean = momentsNewAdapter.getData().get(skipIndex);
//                momentsDataBean.setGoodstatus(changeBean.getGoodstatus());
//                momentsDataBean.setContentgood(changeBean.getContentgood());
//                momentsDataBean.setContentbad(changeBean.getContentbad());
//                momentsDataBean.setIsfollow(changeBean.getIsfollow());
//                momentsDataBean.setContentcomment(changeBean.getContentcomment());
//                momentsDataBean.setIscollection(changeBean.getIscollection());
//                momentsNewAdapter.notifyItemChanged(skipIndex, momentsDataBean);
//            }
//        }
//    }
}
