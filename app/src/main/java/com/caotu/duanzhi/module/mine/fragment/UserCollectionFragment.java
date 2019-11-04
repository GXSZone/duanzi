package com.caotu.duanzhi.module.mine.fragment;

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

import java.util.Map;

/**
 * @author mac
 * @日期: 2018/11/2
 * @describe TODO
 * 和我的收藏页面完全分开,代码没差多少
 */
public class UserCollectionFragment extends BaseVideoFragment {
    public String mUserId;

    @Override
    public boolean isNeedLazyLoadDate() {
        return true;
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("pageno", "" + position);
        map.put("pagesize", pageSize);
        map.put("userid", mUserId);
        OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.COLLECTION)
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
    public int getEmptyImage() {
        return R.mipmap.no_shoucang;
    }

    @Override
    public String getEmptyText() {
        //直接用string形式可以少一步IO流从xml读写
        return "空空如也,快去首页发现好贴";
    }

    public void setDate(String userId) {
        mUserId = userId;
    }
}
