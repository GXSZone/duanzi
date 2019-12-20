package com.caotu.duanzhi.module.mine.fragment;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.RedundantBean;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseVideoFragment;
import com.caotu.duanzhi.module.mine.BaseBigTitleActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * @author mac
 * @日期: 2018/11/2
 * @describe TODO
 */
public class MyLikeFragment extends BaseVideoFragment {

    @Override
    protected void getNetWorkDate(int load_more) {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("pageno", "" + position);
        map.put("pagesize", "10");
        OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.USER_MY_LIKE)
                .upJson(new JSONObject(map))
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
    protected void initViewListener() {
        super.initViewListener();
        if (getActivity() instanceof BaseBigTitleActivity) {
            ((BaseBigTitleActivity) getActivity()).alphaTitleView(mRvContent, adapter);
        }
    }

    @Override
    public String getEmptyText() {
        return "还没有点赞过，快去刷段子呗";
    }
}
