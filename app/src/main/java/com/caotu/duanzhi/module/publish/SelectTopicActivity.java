package com.caotu.duanzhi.module.publish;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.SelectThemeDataBean;
import com.caotu.duanzhi.Http.bean.TopicItemBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.utils.ToastUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

public class SelectTopicActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    private RecyclerView mRvSelectorTopic;
    /**
     * 用来记录当前是在话题初始页面还是话题搜索页面
     */
    boolean isSearch = false;
    private TopicAdapter initAdapter;
    private TopicAdapter searchAdapter;
    private List<TopicItemBean> initList;
    private List<TopicItemBean> searchList;


    @Override
    protected int getLayoutView() {
        return R.layout.activity_select_topic;
    }

    @Override
    protected void initView() {

        findViewById(R.id.tv_cancel_click).setOnClickListener(v -> {
                    closeSoftKeyboard();
                    finish();
                }
        );
        mRvSelectorTopic = findViewById(R.id.rv_selector_topic);

        ((EditText) findViewById(R.id.et_search_topic)).setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                requestSearch(v.getText().toString().trim());
                closeSoftKeyboard();
                return true;
            }
            return false;
        });
        getDateForRv();
    }

    private void requestSearch(String trim) {
        if (searchAdapter == null) {
            searchAdapter = new TopicAdapter();
            searchAdapter.setOnItemClickListener(this);
        }
        mRvSelectorTopic.setAdapter(searchAdapter);
        if (TextUtils.isEmpty(trim)) {
            return;
        }
        httpSearch(trim);
        isSearch = true;
    }

    /**
     * 自己从本地集合中过滤
     *
     * @param trim
     */
    private void httpSearch(String trim) {
        if (initList == null) {
            ToastUtil.showShort("搜索异常.请稍后重试");
            return;
        }
        searchList = new ArrayList<>();
        for (TopicItemBean itemBean : initList) {
            if (itemBean.getTagalias().contains(trim)) {
                searchList.add(itemBean);
            }
        }
        searchAdapter.setNewData(searchList);
    }


    private void getDateForRv() {
        initAdapter = new TopicAdapter();
        initAdapter.setOnItemClickListener(this);
        mRvSelectorTopic.setAdapter(initAdapter);
        initAdapter.setEnableLoadMore(false);
        OkGo.<BaseResponseBean<SelectThemeDataBean>>post(HttpApi.DISCOVER_GET_TAG_TREE)
                .execute(new JsonCallback<BaseResponseBean<SelectThemeDataBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<SelectThemeDataBean>> response) {
                        List<SelectThemeDataBean.RowsBean> rows = response.body().getData().getRows();
                        List<TopicItemBean> itemBeans = DataTransformUtils.summaryTopicBean(rows);
                        initList = itemBeans;
                        initAdapter.setNewData(itemBeans);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<SelectThemeDataBean>> response) {
                        ToastUtil.showShort("话题获取失败,请稍后重试");
                        super.onError(response);
                    }
                });

    }

    @Override
    public void onBackPressed() {
        if (isSearch) {
            mRvSelectorTopic.setAdapter(initAdapter);
            isSearch = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent();
        TopicItemBean itemBean = (TopicItemBean) adapter.getData().get(position);
        intent.putExtra(PublishActivity.KEY_SELECTED_TOPIC, itemBean);
        setResult(RESULT_OK, intent);
        finish();
    }
}
