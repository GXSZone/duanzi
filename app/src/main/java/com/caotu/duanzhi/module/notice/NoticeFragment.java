package com.caotu.duanzhi.module.notice;

import android.app.Activity;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.Http.bean.NoticeBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
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
public class NoticeFragment extends BaseStateFragment<MessageDataBean.RowsBean> implements
        View.OnClickListener,
        BaseQuickAdapter.OnItemChildClickListener,
        BaseQuickAdapter.OnItemClickListener {

    //不传此参数查询全部类型 2_评论 3_关注 4_通知 5_点赞折叠
//    private int seletedIndex = 1;


    private RTextView mRedOne;
    private RTextView mRedTwo;
    private RTextView mRedThree;
    private int goodCount;
    private int followCount;
    private int commentCount;
    private int noteCount;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_notice_layout;
    }

    @Override
    public boolean isNeedLazyLoadDate() {
        return true;
    }

    @Override
    protected void initViewListener() {
        //内容页面
        mStatesView.setCurrentState(StateView.STATE_CONTENT);

        TextView mText = rootView.findViewById(R.id.notice_title);
        rootView.findViewById(R.id.iv_notice_read).setOnClickListener(this);
        mText.post(() -> {
            Shader shader_horizontal = new LinearGradient(0, 0,
                    mText.getWidth(), 0,
                    DevicesUtils.getColor(R.color.color_FF8787),
                    DevicesUtils.getColor(R.color.color_FF698F),
                    Shader.TileMode.CLAMP);
            mText.getPaint().setShader(shader_horizontal);
        });
        initHeaderView(mRvContent);
        adapter.setOnItemChildClickListener(this);
        adapter.setOnItemClickListener(this);
        adapter.setLoadMoreView(new MyListMoreView());
        //取消拉下刷新
        mSwipeLayout.setEnableRefresh(false);
    }


    @Override
    protected BaseQuickAdapter getAdapter() {
        return new NoticeAdapter();
    }


    private void initHeaderView(RecyclerView mRvContent) {
        View inflate = LayoutInflater.from(mRvContent.getContext()).inflate(R.layout.layout_header_notice, mRvContent, false);
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
        if (!LoginHelp.isLoginAndSkipLogin()) return;
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        //这个判断需要再前面,防止初始化刚开始请求两次,后面可见在请求接口
        if (isVisibleToUser && isDataInitiated) {
            requestNotice();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    /**
     * 不传此参数查询全部类型 2_评论 3_关注 4_通知 5_点赞折叠
     */
    protected void getNetWorkDate(@DateState int type) {
        if (!LoginHelp.isLogin()) return;
        if (DateState.init_state == type) {
            requestNotice();
        }
        OkGo.<BaseResponseBean<MessageDataBean>>post(HttpApi.NOTICE_LIST)
                .execute(new JsonCallback<BaseResponseBean<MessageDataBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<MessageDataBean>> response) {
                        MessageDataBean data = response.body().getData();
                        List<MessageDataBean.RowsBean> rows = data.rows;
                        setDate(type, rows);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<MessageDataBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        HelperForStartActivity.openFromNotice(HelperForStartActivity.KEY_NOTICE_OFFICIAL);
        MessageDataBean.RowsBean content = (MessageDataBean.RowsBean) adapter.getData().get(position);
        if (TextUtils.equals("0", content.readflag)) {
            view.postDelayed(() -> getNetWorkDate(DateState.refresh_state), 800);
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