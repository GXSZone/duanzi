package com.caotu.duanzhi.module.notice;

import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.LazyLoadFragment;
import com.caotu.duanzhi.module.mine.NoticeDetailActivity;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.NetWorkUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.SpaceBottomMoreView;
import com.caotu.duanzhi.view.widget.StateView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.RTextView;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 继承该懒加载fragment,每次可见都会请求数据
 */
public class NoticeFragment extends LazyLoadFragment implements
        BaseQuickAdapter.RequestLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener,
        BaseQuickAdapter.OnItemClickListener,
        BaseQuickAdapter.OnItemChildClickListener,
        View.OnClickListener {

    private int position = 1;
    //不传此参数查询全部类型 2_评论 3_关注 4_通知 5_点赞折叠
//    private int seletedIndex = 1;
    private SwipeRefreshLayout mSwipeLayout;
    private StateView mStatesView;
    private NoticeAdapter adapter;

    private RTextView mRedOne;
    private RTextView mRedTwo;
    private RTextView mRedThree;

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
        adapter = new NoticeAdapter(null);
        adapter.setEmptyView(R.layout.layout_empty_default_view, mRvContent);
        adapter.bindToRecyclerView(mRvContent);
//        mRvContent.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemChildClickListener(this);
        adapter.setOnLoadMoreListener(this, mRvContent);
        mSwipeLayout.setOnRefreshListener(this);
        adapter.setLoadMoreView(new SpaceBottomMoreView());
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

        mRedOne = (RTextView) inflate.findViewById(R.id.red_one);
        mRedTwo = (RTextView) inflate.findViewById(R.id.red_two);
        mRedThree = (RTextView) inflate.findViewById(R.id.red_three);

        likeAndCollection.setOnClickListener(this);
        newFocus.setOnClickListener(this);
        atComment.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_like_and_collection:
                HelperForStartActivity.openFromNotice(HelperForStartActivity.KEY_NOTICE_LIKE);
                break;
            case R.id.tv_new_focus:
                HelperForStartActivity.openFromNotice(HelperForStartActivity.KEY_NOTICE_FOLLOW);
                break;
            case R.id.tv_at_comment:
                HelperForStartActivity.openFromNotice(HelperForStartActivity.KEY_NOTICE_COMMENT);
                break;
            case R.id.iv_notice_read:
                // TODO: 2019/3/5 清空未读消息

            default:
                break;
        }
    }


    /**
     * 不传此参数查询全部类型 2_评论 3_关注 4_通知 5_点赞折叠
     */
    protected void getNetWorkDate(@DateState int type) {
        if (type == DateState.refresh_state || type == DateState.init_state) {
            position = 1;
        }
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("pageno", "" + position);
        map.put("pagesize", "20");


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
        position++;
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
                ToastUtil.showShort("该帖子已删除");
                return;
            }
            // TODO: 2018/12/12 剩下类型为2,5评论和点赞的跳转
            //通知作用对象：1_作品 2_评论
            if (TextUtils.equals("2", content.noteobject)) {
                CommendItemBean.RowsBean comment = content.comment;
                if (comment == null || TextUtils.isEmpty(comment.commentid)) {
                    ToastUtil.showShort("该帖子已删除");
                    return;
                }
                comment.setShowContentFrom(true);
                HelperForStartActivity.openCommentDetail(comment);
            } else {
                if (content.content == null || TextUtils.isEmpty(content.content.getContentid())) {
                    ToastUtil.showShort("该帖子已删除");
                    return;
                }
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
}