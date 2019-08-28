package com.caotu.duanzhi.module.search;

import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.Http.bean.UserFocusBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.utils.MySpUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ 用户的选择页面,这个包括一开始展示的关注用户,还有搜索后的结果用户
 */
public class AtUserFragment extends BaseStateFragment<UserBean> implements
        BaseQuickAdapter.OnItemClickListener {

    private List<UserBean> atUserList;

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new AtUserAdapter();
    }

    @Override
    protected void initViewListener() {
        mSwipeLayout.setEnableRefresh(false);
        adapter.setOnItemClickListener(this);
        // TODO: 2019-08-28 这里的数据还得加上我本地记录的用户
        atUserList = MySpUtils.getAtUserList();
    }


    @Override
    protected void getNetWorkDate(int load_more) {
        if (!LoginHelp.isLogin()) {
            dealList(null, load_more);
            return;
        }
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("pageno", "" + position);
        map.put("pagesize", "20");
        map.put("followtype", "2");
        map.put("userid", MySpUtils.getMyId());

        OkGo.<BaseResponseBean<UserFocusBean>>post(HttpApi.USER_MY_FOCUS)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<UserFocusBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<UserFocusBean>> response) {
                        List<UserFocusBean.RowsBean> rows = response.body().getData().getRows();
                        dealList(rows, load_more);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<UserFocusBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }

    private void dealList(List<UserFocusBean.RowsBean> rows, int load_more) {
        List<UserBean> list = DataTransformUtils.changeFocusUserToAtUser(rows);

        if (load_more == DateState.init_state || load_more == DateState.refresh_state) {
            if (list == null) {
                list = new ArrayList<>();
            }
            if (atUserList != null && !atUserList.isEmpty()) {
                for (int i = 0; i < atUserList.size(); i++) {
                    UserBean userBean = atUserList.get(i);
                    if (i == 0) {
                        userBean.isHeader = true;
                    }
                    userBean.isFocus = false;
                }
                list.addAll(0, atUserList);
            }
        }
        setDate(load_more, list);
    }

    @Override
    public int getEmptyImage() {
        return R.mipmap.no_tiezi;
    }

    @Override
    public String getEmptyText() {
        return "你还没有@任何人哦～";
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        UserBean content = (UserBean) adapter.getData().get(position);
        if (getActivity() instanceof SearchActivity) {
            ((SearchActivity) getActivity()).backSetResult(content);
        }
    }
}
