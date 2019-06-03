package com.caotu.duanzhi.module.discover;

import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.DiscoverListBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.home.ITabRefresh;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class TopicListFragment extends BaseStateFragment<DiscoverListBean.RowsBean>
        implements BaseQuickAdapter.OnItemClickListener, ITabRefresh {

    @Override
    protected BaseQuickAdapter getAdapter() {
        DiscoverItemAdapter discoverItemAdapter = new DiscoverItemAdapter();
        discoverItemAdapter.setOnItemClickListener(this);
        return discoverItemAdapter;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.layout_no_refresh;
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        getListDate(load_more);
    }

    private void getListDate(int load_more) {
        HashMap<String, String> hashMapParams = CommonHttpRequest.getInstance().getHashMapParams();
        hashMapParams.put("pageno", position + "");
        hashMapParams.put("pagesize", "12");
        OkGo.<BaseResponseBean<DiscoverListBean>>post(HttpApi.DISCOVER_LIST)
                .upJson(new JSONObject(hashMapParams))
                .execute(new JsonCallback<BaseResponseBean<DiscoverListBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<DiscoverListBean>> response) {
                        List<DiscoverListBean.RowsBean> rows = response.body().getData().getRows();
                        setDate(load_more, rows);
                    }
                });
    }

    @Override
    public int getPageSize() {
        return 12;
    }

    @Override
    public boolean isNeedLazyLoadDate() {
        return true;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        DiscoverListBean.RowsBean bean = (DiscoverListBean.RowsBean) adapter.getData().get(position);
        HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic, bean.tagid);
    }

    @Override
    public void refreshDateByTab() {
        if (mSwipeLayout != null) {
            mSwipeLayout.autoRefresh();
        }
    }
}
