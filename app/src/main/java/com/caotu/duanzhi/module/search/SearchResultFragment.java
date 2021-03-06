package com.caotu.duanzhi.module.search;

import android.text.TextUtils;
import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class SearchResultFragment extends BaseStateFragment<UserBean> implements
        BaseQuickAdapter.OnItemClickListener, SearchDate {
    String searchWord;

    @Override
    protected void initView(View inflate) {
        super.initView(inflate);
        mSwipeLayout.setEnableRefresh(false);
    }

    @Override
    public String getEmptyText() {
        return "哎呀，什么都没有找到";
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        adapter = new SearchUserAdapter();
        adapter.setOnItemClickListener(this);
        return adapter;
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        if (TextUtils.isEmpty(searchWord)) return;
        if (DateState.init_state == load_more) return;
        HashMap<String, String> hashMapParams = CommonHttpRequest.getInstance().getHashMapParams();
        hashMapParams.put("pageno", position + "");
        hashMapParams.put("pagesize", pageSize);
        hashMapParams.put("querystr", searchWord);
        OkGo.<BaseResponseBean<List<UserBaseInfoBean.UserInfoBean>>>post(HttpApi.SEARCH_USER)
                .upJson(new JSONObject(hashMapParams))
                .execute(new JsonCallback<BaseResponseBean<List<UserBaseInfoBean.UserInfoBean>>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<List<UserBaseInfoBean.UserInfoBean>>> response) {
                        List<UserBean> list = DataTransformUtils.changeSearchUser(response.body().getData());
                        setDate(load_more, list);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<List<UserBaseInfoBean.UserInfoBean>>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });

    }

    @Override
    public void setDate(String trim) {
        searchWord = trim;
        //注意索引
        position = 1;
        getNetWorkDate(DateState.refresh_state);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        UserBean content = (UserBean) adapter.getData().get(position);
        if (getActivity() instanceof SearchActivity) {
            ((SearchActivity) getActivity()).backSetResult(content);
        }
    }
}
