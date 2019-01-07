package com.caotu.duanzhi.module.home.fragment;


import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.RedundantBean;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.home.MainHomeNewFragment;
import com.caotu.duanzhi.module.home.adapter.TextAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class TextFragment extends BaseNoVideoFragment {

    private TextAdapter textAdapter;

    @Override
    protected BaseQuickAdapter getAdapter() {
        textAdapter = new TextAdapter();
        textAdapter.setTextClick(this);
        return textAdapter;
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
        params.put("pageno", position + "");
        params.put("pagesize", "20");
        params.put("querytype", "word");
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
                        errorLoad();
                        super.onError(response);
                    }
                });
    }

//    @Override
//    protected void changeItem(MomentsDataBean changeBean) {
//        if (!isVisibleToUser) return;
//        MomentsDataBean momentsDataBean = textAdapter.getData().get(skipIndex);
//        momentsDataBean.setGoodstatus(changeBean.getGoodstatus());
//        momentsDataBean.setContentgood(changeBean.getContentgood());
//        momentsDataBean.setContentbad(changeBean.getContentbad());
//        momentsDataBean.setIsfollow(changeBean.getIsfollow());
//        momentsDataBean.setContentcomment(changeBean.getContentcomment());
//        momentsDataBean.setIscollection(changeBean.getIscollection());
//        textAdapter.notifyItemChanged(skipIndex, momentsDataBean);
//    }
}
