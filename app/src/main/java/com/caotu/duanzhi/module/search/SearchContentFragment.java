package com.caotu.duanzhi.module.search;

import android.text.TextUtils;
import android.view.View;

import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
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

/**
 * 后面当做搜索的分栏fragment,除了综合的分栏不一样
 */
public class SearchContentFragment extends BaseVideoFragment implements
        BaseQuickAdapter.OnItemClickListener, SearchDate {
    String searchWord;
    private String searchid;

    @Override
    public int getPageSize() {
        return 10;
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        HashMap<String, String> params = new HashMap<>();
        params.put("pageno", searchid);
        params.put("pagesize", pageSize);
        params.put("querystr", searchWord);

        OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.SEARCH_CONTENT)
                .upJson(new JSONObject(params))
                .execute(new JsonCallback<BaseResponseBean<RedundantBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<RedundantBean>> response) {
                        searchid = response.body().getData().searchid;
                        setDate(load_more, response.body().getData().getContentList());
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
        mStatesView.setCurrentState(StateView.STATE_EMPTY);
    }

    @Override
    public void setDate(String trim) {
        if (TextUtils.equals(searchWord, trim)) return;
        if (TextUtils.isEmpty(trim)) return;
        searchWord = trim;
        searchid = null;
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