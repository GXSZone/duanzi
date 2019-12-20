package com.caotu.duanzhi.module.mine.fragment;

import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.RedundantBean;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseVideoFragment;
import com.caotu.duanzhi.module.mine.BaseBigTitleActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * @author mac
 * @日期: 2018/11/13
 * @describe 需要重写adapter
 */
public class MyPostFragment extends BaseVideoFragment {

    @Override
    protected void initViewListener() {
        super.initViewListener();
        if (getActivity() instanceof BaseBigTitleActivity) {
            ((BaseBigTitleActivity) getActivity()).alphaTitleView(mRvContent, adapter);
        }
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageno", position);
        params.put("pagesize", pageSize);
        OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.USER_WORKSHOW)
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
    public String getEmptyText() {
        return "不会发段子的土豪不是好逗比";
    }

}
