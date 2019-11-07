package com.caotu.duanzhi.module.notice;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.Http.bean.NoticeBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.home.ILoginEvent;
import com.caotu.duanzhi.module.home.ITabRefresh;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.view.MyListMoreView;
import com.caotu.duanzhi.view.widget.StateView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.List;

/**
 * 通知首页,把头布局固定抽出
 */
public class NoticeNewFragment extends BaseStateFragment<MessageDataBean.RowsBean> implements
        BaseQuickAdapter.OnItemChildClickListener,
        BaseQuickAdapter.OnItemClickListener,
        ITabRefresh, ILoginEvent {

    private NoticeEmptyAndHeaderHolder holder;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_notice_layout_new;
    }

    @Override
    public boolean isNeedLazyLoadDate() {
        return true;
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new NoticeAdapter();
    }

    @Override
    protected void initViewListener() {
        //内容页面,默认是loading状态
        if (LoginHelp.isLogin()) {
            mStatesView.setCurrentState(StateView.STATE_CONTENT);
        }
        View stateView = mStatesView.getStateView(StateView.STATE_LOADING);
        holder = new NoticeEmptyAndHeaderHolder(this);
        holder.initView(rootView, stateView);
        adapter.setOnItemChildClickListener(this);
        adapter.setOnItemClickListener(this);
        adapter.setLoadMoreView(new MyListMoreView());
    }

    /**
     * 过滤初始化请求,但是加载更多的接口还是走这个回调,只处理加载更多的操作
     * 刚切换到该页面会调用一次initdate请求,双击tab和清除都是刷新
     */
    protected void getNetWorkDate(@DateState int type) {
        if (!LoginHelp.isLogin()) return;
        if (DateState.load_more == type) {
            requestMsgList(type);
        } else {
            requestNotice();
            requestMsgList(type);
        }
    }

    /**
     * 提前获取好未登录下通知
     *
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        getNotLoginBean();
    }


    private void requestMsgList(@DateState int type) {
        OkGo.<BaseResponseBean<MessageDataBean>>post(HttpApi.NOTICE_LIST)
                .execute(new JsonCallback<BaseResponseBean<MessageDataBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<MessageDataBean>> response) {
                        MessageDataBean data = response.body().getData();
                        List<MessageDataBean.RowsBean> rowsBeans = DataTransformUtils.changeMsgBean(data.rows);
                        setDate(type, rowsBeans);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<MessageDataBean>> response) {
                        mStatesView.setCurrentState(StateView.STATE_LOADING);
                    }
                });
    }

    MessageDataBean.RowsBean noticeItem;

    /**
     * 获取未登录的时候的展示条目
     */
    public void getNotLoginBean() {
        OkGo.<BaseResponseBean<MessageDataBean>>post(HttpApi.NOTICE_UNLOGIN)
                .execute(new JsonCallback<BaseResponseBean<MessageDataBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<MessageDataBean>> response) {
                        MessageDataBean data = response.body().getData();
                        List<MessageDataBean.RowsBean> rows = DataTransformUtils.changeMsgBean(data.rows);
                        if (AppUtil.listHasDate(rows)) {
                            noticeItem = rows.get(0);
                        }
                    }
                });
    }

    /**
     * 过滤从通知页面跳转出去的情况,刷新通知数目
     */
    @Override
    public void onReStart() {
        if (!LoginHelp.isLogin()) return;
        if (getActivity() instanceof MainActivity && isVisibleToUser) {
            ((MainActivity) getActivity()).requestNotice();
            getNetWorkDate(DateState.refresh_state);
        }
    }

    private void requestNotice() {
        CommonHttpRequest.getInstance().requestNoticeCount(new JsonCallback<BaseResponseBean<NoticeBean>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<NoticeBean>> response) {
                NoticeBean bean = response.body().getData();
                holder.setNoticeCountBean(bean);
            }

            @Override
            public void onError(Response<BaseResponseBean<NoticeBean>> response) {
                mStatesView.setCurrentState(StateView.STATE_LOADING);
            }
        });
    }


    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        MessageDataBean.RowsBean content = (MessageDataBean.RowsBean) adapter.getData().get(position);
        if (view.getId() == R.id.iv_notice_user) {
            HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, content.friendid);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        MessageDataBean.RowsBean content = (MessageDataBean.RowsBean) adapter.getData().get(position);
        if (TextUtils.equals(content.friendid, "001ae21c998b4e5aae8099838da9c580")) {//段子哥id
            UmengHelper.event(UmengStatisticsKeyIds.notice_duanzige);
        } else if (TextUtils.equals(content.friendid, "4e4129bf41664a11b9eda1d6f9d090e7")) { //段子妹Id
            UmengHelper.event(UmengStatisticsKeyIds.message_duanzm);
        }
        HelperForStartActivity.openFromNotice(HelperForStartActivity.KEY_NOTICE_OFFICIAL, content.friendid, content.friendname);
    }

    /**
     * 底部双击刷新操作以及一键清除小红点的操作统一回调
     */
    @Override
    public void refreshDateByTab() {
        if (mStatesView.getCurrentState() == StateView.STATE_CONTENT_LOADING) return;
        getNetWorkDate(DateState.refresh_state);
    }

    /**
     * 因为已经在登录状态下或者未登录状态下,页面切换就需要走这个回调
     */
    @Override
    public void fragmentInViewpagerVisibleToUser() {
        if (LoginHelp.isLogin()) {
            login();
        } else {
            loginOut();
            if (noticeItem != null) {
                holder.setNoticeItemDate(noticeItem);
            }
            holder.clearRedNotice();
        }
    }

    /**
     * 这两个只是登录和未登录的行为监听
     */
    @Override
    public void login() {
        mStatesView.setCurrentState(StateView.STATE_CONTENT);
        //处理切换账号的情况,目前页面可见没有再请求接口
        onReStart();
    }

    @Override
    public void loginOut() {
        mStatesView.setCurrentState(StateView.STATE_LOADING);
    }
}