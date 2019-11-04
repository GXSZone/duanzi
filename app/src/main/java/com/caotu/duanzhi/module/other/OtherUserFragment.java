package com.caotu.duanzhi.module.other;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.RedundantBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseVideoFragment;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * @author mac
 * @日期: 2018/11/5
 * @describe 他人主页
 */
public class OtherUserFragment extends BaseVideoFragment {
   public String userId;

    public void setDate(String myId) {
        userId = myId;
    }
    @Override
    protected int getLayoutRes() {
        return R.layout.layout_no_refresh;
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
        params.put("pageno", "" + position);
        params.put("pagesize", pageSize);
        params.put("userid", userId);
        OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.USER_WORKSHOW)
                .tag(this)
                .upJson(new JSONObject(params))
                .execute(new JsonCallback<BaseResponseBean<RedundantBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<RedundantBean>> response) {
                        setDate(load_more, response.body().getData().getRows());
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<RedundantBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });

    }


    @Override
    public int getEmptyImage() {
        return R.mipmap.no_tiezi;
    }

    @Override
    public String getEmptyText() {
        return "不会发段子的土豪不是好逗比";
    }


    @Override
    public void onDestroyView() {
        OkGo.getInstance().cancelTag(this);
        super.onDestroyView();
    }
}
