package com.caotu.duanzhi.module.publish;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.ThemeBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ruffian.library.widget.REditText;

import java.util.ArrayList;
import java.util.List;

public class SelectTopicActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    /**
     * 搜我喜欢的话题
     */
    private REditText mEtSearchTopic;
    /**
     * 取消
     */
    private TextView mTvCancelClick;
    private RecyclerView mRvSelectorTopic;
    private int type = 0;
    private TopicAdapter initAdapter;
    private TopicAdapter searchAdapter;
    private List<ThemeBean> initBeans;
    private List<ThemeBean> searchBeans;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_select_topic;
    }

    @Override
    protected void initView() {
        mEtSearchTopic = (REditText) findViewById(R.id.et_search_topic);
        mTvCancelClick = (TextView) findViewById(R.id.tv_cancel_click);
        mRvSelectorTopic = (RecyclerView) findViewById(R.id.rv_selector_topic);
        mRvSelectorTopic.setLayoutManager(new LinearLayoutManager(this));
        mTvCancelClick.setOnClickListener(v -> finish());
        mEtSearchTopic.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                requestSearch(v.getText().toString().trim());
                closeSoftKeyboard();
                return true;
            }
            return false;
        });
        setKeyBoardListener();
        getDateForRv();
    }

    private void setKeyBoardListener() {
        //当键盘弹出隐藏的时候会 调用此方法。
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            //获取当前界面可视部分
            getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            //获取屏幕的高度
            int screenHeight = getWindow().getDecorView().getRootView().getHeight();
            //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
            int heightDifference = screenHeight - r.bottom;
            Log.d("Keyboard Size", "Size: " + heightDifference);
            if (heightDifference > 0) {
                type = 1;
                requestSearch(null);
                return;
            }
            if (type == 0) return;
            type = 0;
            resetToInit();
        });
    }

    public void resetToInit() {
        mRvSelectorTopic.setAdapter(initAdapter);
    }

    private void requestSearch(String trim) {
        if (searchAdapter == null) {
            searchBeans = new ArrayList<>();
            searchAdapter = new TopicAdapter(R.layout.topic_item_layout, searchBeans);
            searchAdapter.setOnItemClickListener(this);
        }
        mRvSelectorTopic.setAdapter(searchAdapter);
        if (TextUtils.isEmpty(trim)) {
            return;
        }
        // TODO: 2018/10/30 搜索话题

    }


    private void getDateForRv() {
        initBeans = new ArrayList<>();
        initAdapter = new TopicAdapter(R.layout.topic_item_layout, initBeans);
        initAdapter.setOnItemClickListener(this);
        mRvSelectorTopic.setAdapter(initAdapter);

    }

    @Override
    public void onBackPressed() {
        if (type == 1) {
            resetToInit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent();
        ThemeBean bean;
        if (type == 1 && adapter == searchAdapter) {
            bean = searchBeans.get(position);
        } else {
            bean = initBeans.get(position);
        }
        intent.putExtra(PublishActivity.KEY_SELECTED_TOPIC, bean);
        setResult(RESULT_OK, intent);
        finish();
    }
}
