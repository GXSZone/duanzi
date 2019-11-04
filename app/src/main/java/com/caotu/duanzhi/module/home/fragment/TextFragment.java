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
import com.caotu.duanzhi.utils.ToastUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class TextFragment extends BaseNoVideoFragment {

    private List<MomentsDataBean> contentList;

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new TextAdapter();
    }

    private String pageno = "";

    @Override
    protected void getNetWorkDate(int load_more) {
        HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
        params.put("pageno", pageno);
//        params.put("pagesize", "20");
        params.put("querytype", "word");
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
        OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.HOME_TYPE)
                .upJson(new JSONObject(params))
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
}
