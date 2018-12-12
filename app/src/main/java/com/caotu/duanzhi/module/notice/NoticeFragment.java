package com.caotu.duanzhi.module.notice;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.EventBusObject;
import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusCode;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.module.mine.NoticeDetailActivity;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.NetWorkUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.SpaceBottomMoreView;
import com.caotu.duanzhi.view.widget.MyListPopupWindow;
import com.caotu.duanzhi.view.widget.StateView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NoticeFragment extends BaseFragment implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {
    private TextView mText;
    private int position = 1;
    static final List<String> list = new ArrayList<>();
    //不传此参数查询全部类型 2_评论 3_关注 4_通知 5_点赞折叠
    private int seletedIndex = 1;
    private RecyclerView mRvContent;
    private SwipeRefreshLayout mSwipeLayout;
    private StateView mStatesView;
    private NoticeAdapter adapter;

    private ImageView icArrow;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        list.add("全部通知");
        list.add("评论");
        list.add("关注");
        list.add("通知");
        list.add("点赞");
    }

    @Override
    public boolean isNeedLazyLoadDate() {
        return true;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_notice;
    }

    @Override
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
    protected void initView(View inflate) {
        mText = inflate.findViewById(R.id.notice_title);
        icArrow = inflate.findViewById(R.id.iv_arrow_anim);
        mText.post(() -> {
            Shader shader_horizontal = new LinearGradient(0, 0,
                    mText.getWidth(), 0,
                    DevicesUtils.getColor(R.color.color_FF8787),
                    DevicesUtils.getColor(R.color.color_FF698F),
                    Shader.TileMode.CLAMP);
            mText.getPaint().setShader(shader_horizontal);
        });
        mStatesView = inflate.findViewById(R.id.states_view);
        mRvContent = inflate.findViewById(R.id.rv_content);
        mSwipeLayout = inflate.findViewById(R.id.swipe_layout);
        mRvContent.setLayoutManager(new LinearLayoutManager(getContext()));
        //条目布局
        adapter = new NoticeAdapter(null);
        adapter.setEmptyView(R.layout.layout_empty_default_view, mRvContent);
        mRvContent.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemChildClickListener(this);
        adapter.setOnLoadMoreListener(this, mRvContent);
        mSwipeLayout.setOnRefreshListener(this);
        mText.setOnClickListener(v -> showPop());
        adapter.setLoadMoreView(new SpaceBottomMoreView());
    }

    private void showPop() {
        MyListPopupWindow listPopwindow = new MyListPopupWindow(getActivity(), mText.getText().toString(), list, new MyListPopupWindow.ItemChangeTextListener() {
            @Override
            public void itemSelected(View v, int position, String selected) {
                mText.setText(selected);
                seletedIndex = position + 1;
                getNetWorkDate(DateState.refresh_state);
            }
        });
        listPopwindow.showAsDropDown(mText);
        listPopwindow.setOnDismissListener(() -> icArrow.animate().rotation(0));
        icArrow.animate().rotation(180);
    }

    //不传此参数查询全部类型 2_评论 3_关注 4_通知 5_点赞折叠
    protected void getNetWorkDate(@DateState int type) {
        if (type == DateState.refresh_state || type == DateState.init_state) {
            mSwipeLayout.setRefreshing(true);
            position = 1;
        }
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("pageno", "" + position);
        map.put("pagesize", "20");
        if (seletedIndex != 1) {
            map.put("notetype", seletedIndex + "");
        }

        OkGo.<BaseResponseBean<MessageDataBean>>post(HttpApi.NOTICE_OF_ME)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<MessageDataBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<MessageDataBean>> response) {
                        MessageDataBean data = response.body().getData();
                        List<MessageDataBean.RowsBean> rows = data.rows;
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
        if (TextUtils.equals("3", content.notetype)) {
            //该类型是关注
            return;
        }
        //2评论3关注4通知5点赞折叠
        if (TextUtils.equals("4", content.notetype)) {
            //跳转通知详情
            NoticeDetailActivity.openNoticeDetail(content.friendid, content.friendname, content.friendphoto, content.notetext, content.createtime);
        } else {
            if ("1".equals(content.contentstatus)) {
                ToastUtil.showShort("该资源已被删除");
                return;
            }
            // TODO: 2018/12/12 剩下类型为2,5评论和点赞的跳转
            //通知作用对象：1_作品 2_评论
            if (TextUtils.equals("2", content.noteobject)) {
                CommendItemBean.RowsBean comment = content.comment;
                comment.setShowContentFrom(true);
                HelperForStartActivity.openCommentDetail(comment);
            } else {
                HelperForStartActivity.openContentDetail(content.content, false);
            }
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
            HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, content.friendid);
        } else if (view.getId() == R.id.fl_more_users) {
            String noteid = content.noteid;
            HelperForStartActivity.openOther(HelperForStartActivity.type_other_praise, noteid);
        }
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 登陆后刷新页面
     *
     * @param eventBusObject
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusObject eventBusObject) {
        if (eventBusObject.getCode() == EventBusCode.LOGIN) {
            if (isViewInitiated) {
                getNetWorkDate(DateState.refresh_state);
            }
        }
    }
}