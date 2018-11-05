package com.caotu.duanzhi.module.mine;

import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.mine.adapter.NoticeAdapter;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.NetWorkUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.widget.MyListPopupWindow;
import com.caotu.duanzhi.view.widget.StateView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyNoticeActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {
    private RecyclerView mRvContent;
    private SwipeRefreshLayout mSwipeLayout;
    private StateView mStatesView;
    private NoticeAdapter adapter;

    private TextView mText;
    private int position;
    static final List<String> list = new ArrayList<>();

    static {
        list.add("全部通知");
        list.add("评论");
        list.add("点赞");
        list.add("关注");
        list.add("通知");
    }

    private MyListPopupWindow listPopwindow;


    @Override
    protected void initView() {
        mText = findViewById(R.id.notice_title);
        mText.post(() -> {
            Shader shader_horizontal = new LinearGradient(0, 0,
                    mText.getWidth(), 0,
                    DevicesUtils.getColor(R.color.color_FF8787),
                    DevicesUtils.getColor(R.color.color_FF698F),
                    Shader.TileMode.CLAMP);
            mText.getPaint().setShader(shader_horizontal);
        });
        mStatesView = findViewById(R.id.states_view);
        mRvContent = findViewById(R.id.rv_content);
        mSwipeLayout = findViewById(R.id.swipe_layout);
        mRvContent.setLayoutManager(new LinearLayoutManager(this));
        //条目布局
        adapter = new NoticeAdapter(null);
        adapter.setEmptyView(R.layout.layout_empty_default_view, mRvContent);
        mRvContent.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemChildClickListener(this);
        adapter.setOnLoadMoreListener(this, mRvContent);
        mSwipeLayout.setOnRefreshListener(this);
        mText.setOnClickListener(v -> showPop());
        initDate();
    }

    private void showPop() {
        listPopwindow = new MyListPopupWindow(this, mText.getText().toString(), list, new MyListPopupWindow.ItemChangeTextListener() {
            @Override
            public void itemSelected(View v, int position, String selected) {
                mText.setText(selected);
            }
        });
        listPopwindow.setAnchorView(mText);
        listPopwindow.setVerticalOffset(10);
        listPopwindow.show();
    }


    @Override
    protected int getLayoutView() {
        return R.layout.activity_notice;
    }

    protected void initDate() {
        if (!NetWorkUtils.isNetworkConnected(MyApplication.getInstance())) {
            mStatesView.setCurrentState(StateView.STATE_ERROR);
            return;
        } else {
            mStatesView.setCurrentState(StateView.STATE_CONTENT);
        }
        getNetWorkDate(DateState.init_state);
    }


    @Override
    public void onRefresh() {
        if (!NetWorkUtils.isNetworkConnected(MyApplication.getInstance())) {
            mStatesView.setCurrentState(StateView.STATE_ERROR);
            return;
        }
        if (mStatesView.getCurrentState() != StateView.STATE_CONTENT) {
            mStatesView.setCurrentState(StateView.STATE_CONTENT);
        }
        if (adapter != null) {
            adapter.setEnableLoadMore(true);
        }
        getNetWorkDate(DateState.refresh_state);

    }

    @Override
    public void onLoadMoreRequested() {
        if (!NetWorkUtils.isNetworkConnected(MyApplication.getInstance())) {
            if (adapter != null) {
                adapter.loadMoreFail();
                return;
            }
        }
        getNetWorkDate(DateState.load_more);
    }

    protected void getNetWorkDate(@DateState int type) {
        if (type == DateState.refresh_state || type == DateState.init_state) {
            mSwipeLayout.setRefreshing(true);
            position = 1;
        }
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("pageno", "" + position);
        map.put("pagesize", "20");
        map.put("notetype", "1");
        OkGo.<BaseResponseBean<MessageDataBean>>post(HttpApi.NOTICE_OF_ME)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<MessageDataBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<MessageDataBean>> response) {
                        MessageDataBean data = response.body().getData();
                        List<MessageDataBean.RowsBean> rows = data.getRows();
                        doneDate(type, rows);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<MessageDataBean>> response) {
                        adapter.loadMoreFail();
                        mSwipeLayout.setRefreshing(false);
                        super.onError(response);
                    }
                });
    }

    private void doneDate(int type, List<MessageDataBean.RowsBean> rows) {
        if (type == DateState.refresh_state || type == DateState.init_state) {
            adapter.setNewData(rows);
            if (rows != null && rows.size() < 20) {
                adapter.loadMoreEnd();
            }
        } else {
            adapter.addData(rows);
            if (rows != null && rows.size() < 20) {
                adapter.loadMoreEnd();
            } else {
                adapter.loadMoreComplete();
                position++;
            }
        }
        mSwipeLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        MessageDataBean.RowsBean content = (MessageDataBean.RowsBean) adapter.getData().get(position);
        if (content != null) {
            String contentid = content.getContentid();
            String tagshow = content.getContent().getTagshow();
            String tagshowid = content.getContent().getTagshowid();
            if (contentid == null || tagshow == null || tagshowid == null) {
                ToastUtil.showShort("该资源已被删除");
                return;
            }
            HelperForStartActivity.openContentDetail(contentid);
        } else {
            ToastUtil.showShort("该资源已被删除");
        }

    }

    /**
     * item 里的子控件点击事件
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        MessageDataBean.RowsBean content = (MessageDataBean.RowsBean) adapter.getData().get(position);
        if (view.getId() == R.id.iv_notice_user) {
            HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, content.getFriendid());
        } else if (view.getId() == R.id.fl_more_users) {
            // TODO: 2018/11/2 可能是多个users
            HelperForStartActivity.openOther(HelperForStartActivity.type_other_praise, content.getFriendid());
        }
    }
}
