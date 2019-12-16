package com.caotu.duanzhi.module.search;

import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.ParserUtils;
import com.caotu.duanzhi.view.widget.AvatarWithNameLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * 后面当做搜索的分栏fragment,除了综合的分栏不一样
 */
public class SearchUserFragment extends SearchBaseFragment<UserBean> {

    @Override
    protected BaseQuickAdapter getAdapter() {
        int color = DevicesUtils.getColor(R.color.color_FF698F);
        adapter = new SearchUserAdapter() {

            @Override
            public void bindTitleText(AvatarWithNameLayout nameLayout, String username, String num) {
                nameLayout.setUserText(ParserUtils.setMarkupText(username, searchWord, color),
                        num);
            }
        };
        adapter.setOnItemClickListener(this);
        return adapter;
    }

    @Override
    protected void httpRequest(int load_more, HashMap<String, String> hashMapParams) {
        OkGo.<BaseResponseBean<List<UserBaseInfoBean.UserInfoBean>>>post(HttpApi.SEARCH_USER)
                .upJson(new JSONObject(hashMapParams))
                .execute(new JsonCallback<BaseResponseBean<List<UserBaseInfoBean.UserInfoBean>>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<List<UserBaseInfoBean.UserInfoBean>>> response) {
                        List<UserBean> data = DataTransformUtils.changeSearchUser(response.body().getData());
                        setDate(load_more, data);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<List<UserBaseInfoBean.UserInfoBean>>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }

    @Override
    protected void clickItem(UserBean date) {
        HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, date.userid);
    }

}
