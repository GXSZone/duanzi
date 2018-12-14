package com.caotu.duanzhi.module.home.fragment;


import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.RedundantBean;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.home.MainHomeNewFragment;
import com.caotu.duanzhi.module.home.adapter.PhotoAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class PhotoFragment extends BaseNoVideoFragment {


    private PhotoAdapter photoAdapter;

    @Override
    protected BaseQuickAdapter getAdapter() {
        photoAdapter = new PhotoAdapter();
        photoAdapter.setTextClick(this);
        return photoAdapter;
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
        params.put("pageno", position + "");
        params.put("pagesize", "20");
        params.put("querytype", "pic");
        params.put("uuid", deviceId);
        OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.HOME_TYPE)
                .upJson(new JSONObject(params))
                .execute(new JsonCallback<BaseResponseBean<RedundantBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<RedundantBean>> response) {
                        List<MomentsDataBean> contentList = response.body().getData().getContentList();
                        if (DateState.refresh_state == load_more && (contentList == null || contentList.size() == 0)) {
                            position = 1;
                            getNetWorkDate(load_more);
                            return;
                        }
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
                });
    }

    @Override
    protected void changeItem(MomentsDataBean changeBean) {
        if (!isVisibleToUser) return;
        //更改list数据
        MomentsDataBean momentsDataBean = photoAdapter.getData().get(skipIndex);
        momentsDataBean.setGoodstatus(changeBean.getGoodstatus());
        momentsDataBean.setContentgood(changeBean.getContentgood());
        momentsDataBean.setContentbad(changeBean.getContentbad());
        momentsDataBean.setIsfollow(changeBean.getIsfollow());
        momentsDataBean.setContentcomment(changeBean.getContentcomment());
        momentsDataBean.setIscollection(changeBean.getIscollection());
        photoAdapter.notifyItemChanged(skipIndex, momentsDataBean);
    }
}
