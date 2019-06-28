package com.caotu.duanzhi.module.mine.fragment;

import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.ThemeBean;
import com.caotu.duanzhi.Http.bean.UserFocusBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.home.ITabRefresh;
import com.caotu.duanzhi.module.mine.adapter.FocusTopicAdapter;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.view.MyListMoreView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author mac
 * @日期: 2018/11/5
 * @describe TODO
 */
public class FocusTopicFragment extends BaseStateFragment<ThemeBean> implements
        BaseQuickAdapter.OnItemClickListener, ITabRefresh {

    String mUserId;
    boolean isMe;

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new FocusTopicAdapter(null);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.layout_no_refresh;
    }

    @Override
    protected void initViewListener() {
        adapter.setOnItemClickListener(this);
        adapter.setLoadMoreView(new MyListMoreView());
    }

    /**
     * 设置数据,关键参数:用户id,和是否是本人(UI相关)
     */
    public void setDate(String userId, boolean isMine) {
        mUserId = userId;
        isMe = isMine;
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("pageno", "" + position);
        map.put("pagesize", "20");
        //关注用户和话题的区别
        map.put("followtype", "1");
        map.put("userid", mUserId);

        OkGo.<BaseResponseBean<UserFocusBean>>post(HttpApi.USER_MY_FOCUS)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<UserFocusBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<UserFocusBean>> response) {
                        List<UserFocusBean.RowsBean> rows = response.body().getData().getRows();
                        List<ThemeBean> beans = DataTransformUtils.getMyFocusDataBean(rows, isMe, true);
                        setDate(load_more, beans);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<UserFocusBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }

    @Override
    public int getEmptyImage() {
        return R.mipmap.no_guanzhu;
    }

    @Override
    public String getEmptyText() {
        return "还没有关注任何话题哦";
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        // TODO: 2018/11/5 话题详情
        ThemeBean content = (ThemeBean) adapter.getData().get(position);
        HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic, content.getUserId());
    }

    @Override
    public void refreshDateByTab() {
        position = 1;
        getNetWorkDate(DateState.refresh_state);
    }

    public void login() {

    }

    public void loginOut() {

    }
}