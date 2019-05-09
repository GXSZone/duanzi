package com.caotu.duanzhi.module.home.fragment;

import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.bean.EventBusObject;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusCode;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.detail.ILoadMore;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NetWorkUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.ActionDialog;
import com.caotu.duanzhi.view.dialog.BaseDialogFragment;
import com.caotu.duanzhi.view.dialog.BaseIOSDialog;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.caotu.duanzhi.view.widget.StateView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseNoVideoFragment extends BaseStateFragment<MomentsDataBean> implements BaseQuickAdapter.OnItemChildClickListener,
        BaseQuickAdapter.OnItemClickListener, IHomeRefresh {

    public String deviceId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        deviceId = DevicesUtils.getDeviceId(MyApplication.getInstance());
    }

    /**
     * 父类设置数据前统一转换对象
     *
     * @param load_more
     * @param newDate
     */
    @Override
    protected void setDate(int load_more, List<MomentsDataBean> newDate) {
        newDate = DataTransformUtils.getContentNewBean(newDate);
        super.setDate(load_more, newDate);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
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
        // TODO: 2018/12/4 特殊之处,不管是刷新还是加载更多都是+1;
        position++;
        netWorkState = DateState.refresh_state;
        getNetWorkDate(DateState.refresh_state);
    }

    @Override
    protected void initViewListener() {
        adapter.setOnItemChildClickListener(this);
        adapter.setOnItemClickListener(this);
    }

    /**
     * 可见的时候才注册,不可见直接取消注册
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            EventBus.getDefault().register(this);
        } else {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onDestroyView() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroyView();
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        MomentsDataBean bean = (MomentsDataBean) adapter.getData().get(position);
        switch (view.getId()) {
            //更多的操作的弹窗
            case R.id.item_iv_more_bt:
                if (MySpUtils.isMe(bean.getContentuid())) {
                    BaseIOSDialog dialog = new BaseIOSDialog(getContext(), new BaseIOSDialog.SimpleClickAdapter() {
                        @Override
                        public void okAction() {
                            CommonHttpRequest.getInstance().deletePost(bean.getContentid());
                            adapter.remove(position);
                        }
                    });
                    dialog.setTitleText("是否删除该帖子").show();

                } else {
                    ActionDialog dialog = new ActionDialog();
                    dialog.setContentIdAndCallBack(bean.getContentid(), new BaseDialogFragment.DialogListener() {
                        @Override
                        public void deleteItem() {
                            adapter.remove(position);
                        }

                        @Override
                        public void report() {

                        }
                    }, true);
                    dialog.show(getChildFragmentManager(), "ActionDialog");
                }
                break;
            //分享的弹窗
            case R.id.base_moment_share_iv:
                boolean videoType = LikeAndUnlikeUtil.isVideoType(bean.getContenttype());
                String copyText = null;
                if ("1".equals(bean.getIsshowtitle()) && !TextUtils.isEmpty(bean.getContenttitle())) {
                    copyText = bean.getContenttitle();
                }
                WebShareBean webBean = ShareHelper.getInstance().createWebBean(videoType, bean.getIscollection()
                        , VideoAndFileUtils.getVideoUrl(bean.getContenturllist()), bean.getContentid(), copyText);
                ShareDialog shareDialog = ShareDialog.newInstance(webBean);
                shareDialog.setListener(new ShareDialog.ShareMediaCallBack() {
                    @Override
                    public void callback(WebShareBean webBean) {
                        //该对象已经含有平台参数
                        String cover = VideoAndFileUtils.getCover(bean.getContenturllist());
                        WebShareBean shareBeanByDetail = ShareHelper.getInstance().getShareBeanByDetail(webBean, bean, cover, CommonHttpRequest.url);
                        ShareHelper.getInstance().shareWeb(shareBeanByDetail);
                    }

                    @Override
                    public void colloection(boolean isCollection) {
                        // TODO: 2018/11/16 可能还需要回调给列表
                        bean.setIscollection(isCollection ? "1" : "0");
                        ToastUtil.showShort(isCollection ? "收藏成功" : "取消收藏成功");
                    }
                });
                shareDialog.show(getChildFragmentManager(), getTag());
                break;
            case R.id.base_moment_avatar_iv:
            case R.id.base_moment_name_tv:
                HelperForStartActivity.openOther(HelperForStartActivity.type_other_user,
                        bean.getContentuid());
                break;
            case R.id.txt_content:
                onItemClick(adapter, view, position);
            default:
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusObject eventBusObject) {
        if (!isVisibleToUser) return;
        if (EventBusCode.DETAIL_PAGE_POSITION == eventBusObject.getCode()) {
            if (getActivity() != null && !TextUtils.equals(getActivity().getLocalClassName(), eventBusObject.getTag()))
                return;
            int position = (int) eventBusObject.getObj();
            smoothMoveToPosition(position);

        } else if (EventBusCode.DETAIL_CHANGE == eventBusObject.getCode()) {
            //点赞,踩的同步操作
            if (getActivity() == null) return;
            if (!getActivity().getLocalClassName().equals(eventBusObject.getTag())) return;
            MomentsDataBean refreshBean = (MomentsDataBean) eventBusObject.getObj();
            if (refreshBean == null || adapter == null) return;
            String msg = eventBusObject.getMsg();
            if (!TextUtils.isEmpty(msg)) {
                try {
                    int index = Integer.parseInt(msg);
                    adapter.getData().set(index, refreshBean);
                    adapter.notifyItemChanged(index, refreshBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        //图片和段子分栏下面没有web类型.直接忽略
        ArrayList<MomentsDataBean> list = (ArrayList<MomentsDataBean>) adapter.getData();
        HelperForStartActivity.openContentDetail(list, position, false, 0);
    }

    /**
     * 因为推荐内容不一定是满20条的,所以全局放开
     *
     * @return
     */
    @Override
    public int getPageSize() {
        return 1;
    }

    Runnable runnable = () -> getNetWorkDate(DateState.refresh_state);

    /**
     * 用于给首页的刷新按钮刷新调用
     */
    @Override
    public void refreshDate() {
        if (mRvContent != null) {
            smoothMoveToPosition(0);
            mRvContent.removeCallbacks(runnable);
            mRvContent.postDelayed(runnable, 300);
        }
    }

    public ILoadMore dateCallBack;

    @Override
    public void loadMore(ILoadMore iLoadMore) {
        dateCallBack = iLoadMore;
        getNetWorkDate(DateState.load_more);
    }

    @Override
    public boolean isNeedLazyLoadDate() {
        return true;
    }

    @Override
    public String getEmptyText() {
        return "暂无更新,去发现看看吧";
    }

}
