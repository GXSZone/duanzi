package com.caotu.duanzhi.module.search;

import android.text.TextUtils;
import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.RedundantBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseVideoFragment;
import com.caotu.duanzhi.view.widget.StateView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * 后面当做搜索的分栏fragment,除了综合的分栏不一样
 */
public class SearchContentFragment extends BaseVideoFragment implements
        BaseQuickAdapter.OnItemClickListener, SearchDate {
    String searchWord;
    private String searchid;


    @Override
    protected void getNetWorkDate(int load_more) {
        if (TextUtils.isEmpty(searchWord)) return;
        HashMap<String, String> hashMapParams = CommonHttpRequest.getInstance().getHashMapParams();
        hashMapParams.put("pageno", searchid);
        hashMapParams.put("pagesize", pageSize);
        hashMapParams.put("querystr", searchWord);

        OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.SEARCH_CONTENT)
                .upJson(new JSONObject(hashMapParams))
                .execute(new JsonCallback<BaseResponseBean<RedundantBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<RedundantBean>> response) {
                        searchid = response.body().getData().searchid;
                        List<MomentsDataBean> newBean = DataTransformUtils.getContentNewBean(response.body().getData().getContentList());
                        setDate(load_more, newBean);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<RedundantBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });

    }

    @Override
    protected void initView(View inflate) {
        super.initView(inflate);
        //注意这里把loading 状态当初始化布局
        mStatesView.setViewForState(R.layout.layout_search_init, StateView.STATE_LOADING, true);
    }

    @Override
    public void setDate(String trim) {
        searchWord = trim;
        getNetWorkDate(DateState.init_state);
    }


    @Override
    protected int getLayoutRes() {
        return R.layout.layout_no_refresh;
    }

    @Override
    public int getEmptyImage() {
        return R.mipmap.no_tiezi;
    }

    @Override
    public String getEmptyText() {
        return "找了又找，还是没找到相关内容";
    }
}
