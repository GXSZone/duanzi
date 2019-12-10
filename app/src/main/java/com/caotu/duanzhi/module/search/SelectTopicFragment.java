package com.caotu.duanzhi.module.search;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.SelectThemeDataBean;
import com.caotu.duanzhi.Http.bean.TopicItemBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.publish.PublishActivity;
import com.caotu.duanzhi.module.publish.TopicAdapter;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.view.widget.OneSelectedLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

public class SelectTopicFragment extends BaseStateFragment<TopicItemBean> implements
        BaseQuickAdapter.OnItemClickListener, SearchDate {
    String searchWord;
    private List<TopicItemBean> itemBeans;
    private List<TopicItemBean> searchBeans;

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
        return "哎呀，什么都没有找到";
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        adapter = new TopicAdapter();
        adapter.setOnItemClickListener(this);
        return adapter;
    }

    @Override
    protected void initViewListener() {
        List<TopicItemBean> topicList = MySpUtils.getTopicList();
        if (!AppUtil.listHasDate(topicList)) return;
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.select_topic_header, mRvContent, false);
        adapter.setHeaderView(headerView);
        OneSelectedLayout layout = headerView.findViewById(R.id.radio_selected);
        layout.setDates(topicList);
        layout.setListener(bean -> backResult(bean));
    }


    @Override
    protected void getNetWorkDate(int load_more) {
        OkGo.<BaseResponseBean<SelectThemeDataBean>>post(HttpApi.DISCOVER_GET_TAG_TREE)
                .execute(new JsonCallback<BaseResponseBean<SelectThemeDataBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<SelectThemeDataBean>> response) {
                        // TODO: 2019-12-03 这里需要根据UI 添加字段
                        List<SelectThemeDataBean.RowsBean> rows = response.body().getData().getRows();
                        itemBeans = DataTransformUtils.summaryTopicBean(rows);
                        setDate(load_more, itemBeans);
                        //禁掉加载更多操作
                        adapter.setEnableLoadMore(false);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<SelectThemeDataBean>> response) {
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
        if (itemBeans == null) {
            return;
        }
        if (searchBeans == null) {
            searchBeans = new ArrayList<>();
        } else {
            searchBeans.clear();
        }

        for (TopicItemBean itemBean : itemBeans) {
            if (itemBean.tagalias.contains(trim)) {
                searchBeans.add(itemBean);
            }
        }
        setDate(DateState.refresh_state, searchBeans);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        TopicItemBean itemBean = (TopicItemBean) adapter.getData().get(position);
        backResult(itemBean);
    }

    public void backResult(TopicItemBean itemBean) {
        Intent intent = new Intent();
        intent.putExtra(PublishActivity.KEY_SELECTED_TOPIC, itemBean);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.setResult(Activity.RESULT_OK, intent);
            activity.finish();
        }
    }
}
