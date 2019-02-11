package com.caotu.duanzhi.module.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.EventBusObject;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusCode;
import com.caotu.duanzhi.module.MomentsNewAdapter;
import com.caotu.duanzhi.module.home.ILoadMore;
import com.caotu.duanzhi.module.home.fragment.CallBackTextClick;
import com.caotu.duanzhi.module.home.fragment.IHomeRefresh;
import com.caotu.duanzhi.other.AndroidInterface;
import com.caotu.duanzhi.other.HandleBackInterface;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NetWorkUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.ActionDialog;
import com.caotu.duanzhi.view.dialog.BaseDialogFragment;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import cn.jzvd.JZMediaManager;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdMgr;
import cn.jzvd.JzvdStd;

/**
 * @author mac
 * @日期: 2018/11/12
 * @describe 关于视频播放的逻辑都放在这里处理
 */

public abstract class BaseVideoFragment extends BaseStateFragment<MomentsDataBean> implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener,
        HandleBackInterface, CallBackTextClick, IHomeRefresh {
    private LinearLayoutManager layoutManager;
    private boolean canAutoPlay;

    @Override
    protected BaseQuickAdapter getAdapter() {
        MomentsNewAdapter momentsNewAdapter = new MomentsNewAdapter();
        momentsNewAdapter.setTextClick(this);
        return momentsNewAdapter;
    }

    public int getPageSize() {
        return 5;
    }

    public ILoadMore dateCallBack;

    @Override
    public void loadMore(ILoadMore iLoadMore) {
        dateCallBack = iLoadMore;
        getNetWorkDate(DateState.load_more);
    }

    // TODO: 2018/12/18 只有首页推荐才有这两个回调,需要重写逻辑
    @Override
    public void refreshDate() {

    }

    /**
     * 因为io读写也是费时的,所以这里可以采取eventbus传开关的状态过来,直接记录状态的方式更佳
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusObject eventBusObject) {
        if (EventBusCode.VIDEO_PLAY == eventBusObject.getCode()) {
            canAutoPlay = NetWorkUtils.canAutoPlay();
        } else if (EventBusCode.DETAIL_PAGE_POSITION == eventBusObject.getCode()) {
            recycleviewScroll(eventBusObject);
        }
    }

    /**
     * 在viewpager里面
     *
     * @param eventBusObject
     */
    public void recycleviewScroll(EventBusObject eventBusObject) {
        if (getActivity() != null && !TextUtils.equals(getActivity().getLocalClassName(), eventBusObject.getTag()))
            return;
        int position = (int) eventBusObject.getObj();
        if (adapter != null) {
            position = position + adapter.getHeaderLayoutCount();
        }
        smoothMoveToPosition(position);
    }

//    public void changeItem(EventBusObject eventBusObject) {
//        MomentsDataBean changeBean = (MomentsDataBean) eventBusObject.getObj();
//        if (momentsNewAdapter != null) {
//            //更改list数据
//            int headerLayoutCount = momentsNewAdapter.getHeaderLayoutCount();
//            MomentsDataBean momentsDataBean = momentsNewAdapter.getData().get(skipIndex);
//            momentsDataBean.setGoodstatus(changeBean.getGoodstatus());
//            momentsDataBean.setContentgood(changeBean.getContentgood());
//            momentsDataBean.setContentbad(changeBean.getContentbad());
//            momentsDataBean.setIsfollow(changeBean.getIsfollow());
//            momentsDataBean.setContentcomment(changeBean.getContentcomment());
//            momentsDataBean.setIscollection(changeBean.getIscollection());
//            momentsNewAdapter.notifyItemChanged(skipIndex + headerLayoutCount, momentsDataBean);
//        }
//    }


    @Override
    protected void initViewListener() {
        //初始化的时候也要赋值一次
        canAutoPlay = NetWorkUtils.canAutoPlay();
        EventBus.getDefault().register(this);
        adapter.setOnItemChildClickListener(this);
        adapter.setOnItemClickListener(this);
        layoutManager = (LinearLayoutManager) mRvContent.getLayoutManager();
        mRvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!isResum) return;
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(MyApplication.getInstance()).resumeRequests();
                    onScrollPlayVideo(recyclerView, layoutManager.findFirstVisibleItemPosition(), layoutManager.findLastVisibleItemPosition());
                } else {
                    Glide.with(MyApplication.getInstance()).pauseRequests();
                }
            }
        });

//        mRvContent.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
//            @Override
//            public void onChildViewAttachedToWindow(View view) {
//
//            }
//
//            @Override
//            public void onChildViewDetachedFromWindow(View view) {
//                //不可见的情况下自动播放逻辑都不走
//                if (!isResum) return;
//                Jzvd jzvd = view.findViewById(R.id.base_moment_video);
//                if (jzvd != null && jzvd.jzDataSource != null &&
//                        jzvd.jzDataSource.containsTheUrl(JZMediaManager.getCurrentUrl())) {
//                    Jzvd currentJzvd = JzvdMgr.getCurrentJzvd();
//                    if (currentJzvd != null && currentJzvd.currentScreen != Jzvd.SCREEN_WINDOW_FULLSCREEN) {
//                        Jzvd.releaseAllVideos();
//                    }
//                }
//            }
//        });

    }


    public void onScrollPlayVideo(RecyclerView recyclerView, int firstVisiblePosition, int lastVisiblePosition) {
        if (!canAutoPlay) return;
        // TODO: 2018/12/13 这个是为了修复bug java.lang.NullPointerException: Attempt to invoke virtual method 'int android.view.View.getVisibility()' on a null object reference
        if (getActivity() != null && getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            return;
        }
        if (!isResum) return;
        for (int i = 0; i <= lastVisiblePosition - firstVisiblePosition; i++) {
            View child = recyclerView.getChildAt(i);
            View view = child.findViewById(R.id.base_moment_video);
            if (view != null && view instanceof JzvdStd) {
                JzvdStd player = (JzvdStd) view;
                if (getViewVisiblePercent(player) == 1f) {
                    if (JZMediaManager.instance().positionInList != i + firstVisiblePosition) {
                        if (!player.isCurrentPlay()) {
                            //自动播放不能调用点击播放,因为要统计手动点击播放按钮才算
                            player.startVideo();
//                            player.startButton.performClick();
                        }
                    }
                    break;
                }
            }
        }
    }

    public static float getViewVisiblePercent(View view) {
        if (view == null) {
            return 0f;
        }
        float height = view.getHeight();
        Rect rect = new Rect();
        if (!view.getLocalVisibleRect(rect)) {
            return 0f;
        }
        float visibleHeight = rect.bottom - rect.top;
        return visibleHeight / height;
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        MomentsDataBean bean = (MomentsDataBean) adapter.getData().get(position);
        switch (view.getId()) {
            //更多的操作的弹窗
            case R.id.item_iv_more_bt:
                if (MySpUtils.isMe(bean.getContentuid())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("是否删除该帖子");
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            CommonHttpRequest.getInstance().deletePost(bean.getContentid());
                            adapter.remove(position);
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();


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
                    }, getHasReport());
                    dialog.show(getChildFragmentManager(), "ActionDialog");
                }
                break;
            //分享的弹窗
            case R.id.base_moment_share_iv:
                boolean videoType = LikeAndUnlikeUtil.isVideoType(bean.getContenttype());
                WebShareBean webBean = ShareHelper.getInstance().createWebBean(videoType, true, bean.getIscollection()
                        , VideoAndFileUtils.getVideoUrl(bean.getContenturllist()), bean.getContentid());
                showShareDialog(CommonHttpRequest.url, webBean, bean, position);
                break;
            case R.id.base_moment_comment:
                ArrayList<MomentsDataBean> list = (ArrayList<MomentsDataBean>) adapter.getData();
                dealVideoSeekTo(list, bean, position);
                break;
            case R.id.web_image:
                CommentUrlBean webList = VideoAndFileUtils.getWebList(bean.getContenturllist());
                MyApplication.getInstance().putHistory(bean.getContentid());
                HelperForStartActivity.checkUrlForSkipWeb("详情", webList.info, AndroidInterface.type_recommend);
            default:
                break;
        }
    }

    public void showShareDialog(String shareUrl, WebShareBean webBean, MomentsDataBean bean, int position) {
        ShareDialog shareDialog = ShareDialog.newInstance(webBean);
        shareDialog.setListener(new ShareDialog.ShareMediaCallBack() {
            @Override
            public void callback(WebShareBean webBean) {
                //该对象已经含有平台参数
                String cover = VideoAndFileUtils.getCover(bean.getContenturllist());
                WebShareBean shareBeanByDetail = ShareHelper.getInstance().getShareBeanByDetail(webBean, bean, cover, shareUrl);
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
    }

    public boolean getHasReport() {
        return false;
    }

//    public int skipIndex;

    @Override
    public void textClick(MomentsDataBean item, int positon) {
        ArrayList<MomentsDataBean> list = (ArrayList<MomentsDataBean>) adapter.getData();
        if (TextUtils.equals("5", item.getContenttype())) {
            CommentUrlBean webList = VideoAndFileUtils.getWebList(item.getContenturllist());
            MyApplication.getInstance().putHistory(item.getContentid());
            HelperForStartActivity.checkUrlForSkipWeb("详情", webList.info, AndroidInterface.type_recommend);
        } else {
            dealVideoSeekTo(list, item, positon);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        // TODO: 2018/11/13 web 类型没有详情,直接跳web页面
        MomentsDataBean bean = (MomentsDataBean) adapter.getData().get(position);
        if (TextUtils.equals("5", bean.getContenttype())) {
            CommentUrlBean webList = VideoAndFileUtils.getWebList(bean.getContenturllist());
            MyApplication.getInstance().putHistory(bean.getContentid());
            HelperForStartActivity.checkUrlForSkipWeb("详情", webList.info, AndroidInterface.type_recommend);
        } else {
            ArrayList<MomentsDataBean> list = (ArrayList<MomentsDataBean>) adapter.getData();
            dealVideoSeekTo(list, bean, position);
        }
    }

    public void dealVideoSeekTo(ArrayList<MomentsDataBean> list, MomentsDataBean bean, int position) {
        boolean videoType = LikeAndUnlikeUtil.isVideoType(bean.getContenttype());
        if (videoType) {
            Jzvd currentJzvd = JzvdMgr.getCurrentJzvd();
            if (currentJzvd != null && currentJzvd instanceof MyVideoPlayerStandard) {
                int progress = ((MyVideoPlayerStandard) currentJzvd).getmProgress();
                HelperForStartActivity.openContentDetail(list, position, false, progress);
            } else {
                HelperForStartActivity.openContentDetail(list, position, false, 0);
            }
        } else {
            HelperForStartActivity.openContentDetail(list, position, false, 0);
        }
    }


    @Override
    public boolean onBackPressed() {
        return Jzvd.backPress();
    }


    @Override
    public void onDestroyView() {
        mRvContent.clearOnScrollListeners();
        mRvContent.clearOnChildAttachStateChangeListeners();
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public String getEmptyText() {
        return "暂无更新,去发现看看吧";
    }
}
