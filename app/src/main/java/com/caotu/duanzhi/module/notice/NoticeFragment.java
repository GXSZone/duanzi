package com.caotu.duanzhi.module.notice;

import android.app.Activity;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.Http.bean.NoticeBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.UmengHelper;
import com.caotu.duanzhi.UmengStatisticsKeyIds;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.LazyLoadFragment;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NetWorkUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.MyListMoreView;
import com.caotu.duanzhi.view.dialog.NoticeReadTipDialog;
import com.caotu.duanzhi.view.widget.StateView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.RTextView;

import java.util.List;

/**
 * 继承该懒加载fragment,每次可见都会请求数据,因为有个头布局置顶,不能继承basestatefragment
 */
public class NoticeFragment extends LazyLoadFragment implements
        BaseQuickAdapter.RequestLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.OnItemClickListener,
        BaseQuickAdapter.OnItemChildClickListener,
        View.OnClickListener {

    //不传此参数查询全部类型 2_评论 3_关注 4_通知 5_点赞折叠
//    private int seletedIndex = 1;
    private SwipeRefreshLayout mSwipeLayout;
    private StateView mStatesView;
    private NoticeAdapter adapter;

    private RTextView mRedOne;
    private RTextView mRedTwo;
    private RTextView mRedThree;
    private int goodCount;
    private int followCount;
    private int commentCount;
    private int noteCount;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_notice;
    }

    @Override
    public void fetchData() {
        if (!NetWorkUtils.isNetworkConnected(MyApplication.getInstance())) {
            mStatesView.setCurrentState(StateView.STATE_ERROR);
            return;
        }
        requestNotice();
        getNetWorkDate(DateState.init_state);
    }

    @Override
    protected void initView(View inflate) {
        TextView mText = inflate.findViewById(R.id.notice_title);
        inflate.findViewById(R.id.iv_notice_read).setOnClickListener(this);
        mText.post(() -> {
            Shader shader_horizontal = new LinearGradient(0, 0,
                    mText.getWidth(), 0,
                    DevicesUtils.getColor(R.color.color_FF8787),
                    DevicesUtils.getColor(R.color.color_FF698F),
                    Shader.TileMode.CLAMP);
            mText.getPaint().setShader(shader_horizontal);
        });
        mStatesView = inflate.findViewById(R.id.states_view);
        RecyclerView mRvContent = inflate.findViewById(R.id.rv_content);
        mSwipeLayout = inflate.findViewById(R.id.swipe_layout);
        mRvContent.setLayoutManager(new LinearLayoutManager(getContext()));
        //条目布局
        adapter = new NoticeAdapter();
        adapter.setEmptyView(R.layout.layout_empty_default_view, mRvContent);
        adapter.bindToRecyclerView(mRvContent);
//        mRvContent.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemChildClickListener(this);
        adapter.setOnLoadMoreListener(this, mRvContent);
        mSwipeLayout.setOnRefreshListener(this);
        adapter.setLoadMoreView(new MyListMoreView());
//        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        mRvContent.setBackgroundColor(DevicesUtils.getColor(R.color.color_f5f6f8));
        initHeaderView(mRvContent);
    }


    private void initHeaderView(RecyclerView mRvContent) {
        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.layout_header_notice, mRvContent, false);
        adapter.setHeaderView(inflate);
        adapter.setHeaderAndEmpty(true);
        TextView likeAndCollection = inflate.findViewById(R.id.tv_like_and_collection);
        TextView newFocus = inflate.findViewById(R.id.tv_new_focus);
        TextView atComment = inflate.findViewById(R.id.tv_at_comment);

        mRedOne = inflate.findViewById(R.id.red_one);
        mRedTwo = inflate.findViewById(R.id.red_two);
        mRedThree = inflate.findViewById(R.id.red_three);

        likeAndCollection.setOnClickListener(this);
        newFocus.setOnClickListener(this);
        atComment.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        switch (v.getId()) {
            case R.id.tv_like_and_collection:
                HelperForStartActivity.openFromNotice(HelperForStartActivity.KEY_NOTICE_LIKE);
                if (runningActivity instanceof MainActivity &&
                        mRedOne.getVisibility() == View.VISIBLE) {
                    ((MainActivity) runningActivity).changeBottomRed(goodCount);
                    mRedOne.setVisibility(View.INVISIBLE);
                }
                CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.msg_like);
                UmengHelper.event(UmengStatisticsKeyIds.notice_like);
                break;
            case R.id.tv_new_focus:
                HelperForStartActivity.openFromNotice(HelperForStartActivity.KEY_NOTICE_FOLLOW);
                if (runningActivity instanceof MainActivity &&
                        mRedTwo.getVisibility() == View.VISIBLE) {
                    ((MainActivity) runningActivity).changeBottomRed(followCount);
                    mRedTwo.setVisibility(View.INVISIBLE);
                }
                CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.msg_follow);
                UmengHelper.event(UmengStatisticsKeyIds.notice_follow);
                break;
            case R.id.tv_at_comment:
                HelperForStartActivity.openFromNotice(HelperForStartActivity.KEY_NOTICE_COMMENT);
                if (runningActivity instanceof MainActivity &&
                        mRedThree.getVisibility() == View.VISIBLE) {
                    ((MainActivity) runningActivity).changeBottomRed(commentCount);
                    mRedThree.setVisibility(View.INVISIBLE);
                }
                CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.msg_comment);
                UmengHelper.event(UmengStatisticsKeyIds.notice_comment);
                break;
            case R.id.iv_notice_read:
                boolean hasShow = MySpUtils.getBoolean(MySpUtils.SP_READ_DIALOG, false);
                if (!hasShow) {
                    NoticeReadTipDialog dialog = new NoticeReadTipDialog(getActivity(),
                            this::setNoticeRead);
                    dialog.show();
                    MySpUtils.putBoolean(MySpUtils.SP_READ_DIALOG, true);
                } else {
                    if (goodCount + noteCount + commentCount + followCount > 0) {
                        setNoticeRead();
                    } else {
                        ToastUtil.showShort("暂无新消息通知哦～");
                    }
                }

            default:
                break;
        }
    }

    private void setNoticeRead() {
        OkGo.<BaseResponseBean<String>>post(HttpApi.MSG_ALL_READ)
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        mRedOne.setVisibility(View.INVISIBLE);
                        mRedTwo.setVisibility(View.INVISIBLE);
                        mRedThree.setVisibility(View.INVISIBLE);
                        if (getActivity() != null && getActivity() instanceof MainActivity) {
                            //该数字是为了方便,只要能减成负数就行
                            ((MainActivity) getActivity()).changeBottomRed(10000);
                        }
                        ToastUtil.showShort("全部设置为已读");
                        getNetWorkDate(DateState.refresh_state);
                    }
                });
    }


    /**
     * 不传此参数查询全部类型 2_评论 3_关注 4_通知 5_点赞折叠
     */
    protected void getNetWorkDate(@DateState int type) {
        OkGo.<BaseResponseBean<MessageDataBean>>post(HttpApi.NOTICE_LIST)
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
        if (type == DateState.init_state) {
            mStatesView.setCurrentState(StateView.STATE_CONTENT);
        }
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
            }
        }
        mSwipeLayout.setRefreshing(false);

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        HelperForStartActivity.openFromNotice(HelperForStartActivity.KEY_NOTICE_OFFICIAL);
        MessageDataBean.RowsBean content = (MessageDataBean.RowsBean) adapter.getData().get(position);
        if (TextUtils.equals("0", content.readflag)) {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getNetWorkDate(DateState.refresh_state);
                }
            }, 800);
        }
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).changeBottomRed(noteCount);
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


    private void requestNotice() {
        CommonHttpRequest.getInstance().requestNoticeCount(new JsonCallback<BaseResponseBean<NoticeBean>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<NoticeBean>> response) {
                NoticeBean bean = response.body().getData();
                try {
                    goodCount = Integer.parseInt(bean.good);
                    followCount = Integer.parseInt(bean.follow);
                    commentCount = Integer.parseInt(bean.comment);
                    noteCount = Integer.parseInt(bean.note);
                    mRedOne.setVisibility(goodCount > 0 ? View.VISIBLE : View.INVISIBLE);
                    mRedOne.setText(goodCount > 99 ? "99+" : bean.good);

                    mRedTwo.setVisibility(followCount > 0 ? View.VISIBLE : View.INVISIBLE);
                    mRedTwo.setText(followCount > 99 ? "99+" : bean.follow);

                    mRedThree.setVisibility(commentCount > 0 ? View.VISIBLE : View.INVISIBLE);
                    mRedThree.setText(commentCount > 99 ? "99+" : bean.comment);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Response<BaseResponseBean<NoticeBean>> response) {
//                super.onError(response);
            }
        });
    }
}