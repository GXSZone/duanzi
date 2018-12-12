package com.caotu.duanzhi.module.search;

import android.text.TextUtils;
import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.view.widget.StateView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class SearchFragment extends BaseStateFragment<UserBaseInfoBean.UserInfoBean> {
    String searchWord;

    @Override
    protected void initView(View inflate) {
        super.initView(inflate);
        //禁止下来刷新
        mSwipeLayout.setEnabled(false);
        //注意这里把loading 状态当初始化布局
        mStatesView.setViewForState(R.layout.layout_search_init, StateView.STATE_LOADING,true);
    }

    @Override
    protected void initDate() {
//        super.initDate();
    }

    @Override
    public int getEmptyImage() {
        return R.mipmap.no_tiezi;
    }

    @Override
    public String getEmptyText() {
        return "哎呀，什么都没有找到";
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new SearchUserAdapter();
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        if (TextUtils.isEmpty(searchWord)) return;
        HashMap<String, String> hashMapParams = CommonHttpRequest.getInstance().getHashMapParams();
        hashMapParams.put("pageno", position + "");
        hashMapParams.put("pagesize", pageSize);
        hashMapParams.put("querystr", searchWord);
        OkGo.<BaseResponseBean<List<UserBaseInfoBean.UserInfoBean>>>post(HttpApi.SEARCH_USER)
                .upJson(new JSONObject(hashMapParams))
                .execute(new JsonCallback<BaseResponseBean<List<UserBaseInfoBean.UserInfoBean>>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<List<UserBaseInfoBean.UserInfoBean>>> response) {
                        List<UserBaseInfoBean.UserInfoBean> data = response.body().getData();
                        setDate(load_more, data);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<List<UserBaseInfoBean.UserInfoBean>>> response) {
                        super.onError(response);
                    }
                });

    }

    public void setDate(String trim) {
        searchWord = trim;
        getNetWorkDate(DateState.init_state);
    }
}
