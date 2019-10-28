package com.caotu.duanzhi.module.base;

import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.EventBusObject;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusCode;
import com.caotu.duanzhi.module.MomentsNewAdapter;
import com.caotu.duanzhi.module.detail.ILoadMore;
import com.caotu.duanzhi.module.home.fragment.IHomeRefresh;
import com.caotu.duanzhi.other.AndroidInterface;
import com.caotu.duanzhi.other.HandleBackInterface;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.AppUtil;
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
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mac
 * @日期: 2018/11/12
 * @describe 关于视频播放的逻辑都放在这里处理
 * 秒播方案可以参考阿里的:
 * https://help.aliyun.com/document_detail/109863.html?spm=a2c4g.11186623.6.1119.20559e2ePIqMxC
 */

public abstract class BaseVideoFragment extends BaseStateFragment<MomentsDataBean> implements
        BaseQuickAdapter.OnItemChildClickListener,
        BaseQuickAdapter.OnItemClickListener,
        HandleBackInterface, IHomeRefresh {
    private LinearLayoutManager layoutManager;
    private boolean canAutoPlay;

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new MomentsNewAdapter();
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

    /**
     * 因为io读写也是费时的,所以这里可以采取eventbus传开关的状态过来,直接记录状态的方式更佳
     */
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void getEventBus(EventBusObject eventBusObject) {
        if (EventBusCode.VIDEO_PLAY == eventBusObject.getCode()) {
            canAutoPlay = NetWorkUtils.canAutoPlay();
        } else if (EventBusCode.DETAIL_PAGE_POSITION == eventBusObject.getCode()) {
            recycleviewScroll(eventBusObject);
        } else if (EventBusCode.DETAIL_CHANGE == eventBusObject.getCode()) {
            //点赞,踩的同步操作
            if (getActivity() == null) return;
            if (!getActivity().getLocalClassName().equals(eventBusObject.getTag())) return;
            refreshItem(eventBusObject);
        } else if (EventBusCode.TEXT_SIZE == eventBusObject.getCode()) {
            if (adapter != null) {
                adapter.notifyDataSetChanged(); //全局刷新方式
            }
        }
    }


    public void refreshItem(EventBusObject eventBusObject) {
//        MomentsDataBean refreshBean = (MomentsDataBean) eventBusObject.getObj();
//        if (refreshBean == null && adapter == null) return;
//        //防止多开个人主页后回调错乱的问题
//        if (this instanceof OtherUserFragment) {
//            if (!TextUtils.equals(((OtherUserFragment) this).userId, refreshBean.getContentuid())) {
//                return;
//            }
//        }
//        // TODO: 2019-09-12 这种方式虽然可行,但是效率上太慢了
//        DiffItemCallback callback = new DiffItemCallback(BigDateList.getInstance().getBeans());
//        adapter.setNewDiffData(callback);
//        // TODO: 2019/4/11 这里角标拿的还是集合的,不用有头布局的, 刷新用两个参数的可以自己控制刷新哪些控件,不然整个都刷新了,浪费性能
//        String msg = eventBusObject.getMsg();
//        if (!TextUtils.isEmpty(msg)) {
//            try {
//                int position = Integer.parseInt(msg);
//                int index = position + adapter.getHeaderLayoutCount();
//                adapter.notifyItemChanged(index, refreshBean);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * 在viewpager里面
     *
     * @param eventBusObject
     */
    public void recycleviewScroll(EventBusObject eventBusObject) {
        if (getActivity() == null || !TextUtils.equals(getActivity().getLocalClassName(), eventBusObject.getTag()))
            return;
        int position = (int) eventBusObject.getObj();
        if (adapter != null) {
            position = position + adapter.getHeaderLayoutCount();
        }
//这个api可以直接滚动置顶,但是有滚动的动画效果,但是比较准,现在实时同步就无所谓了
        ((LinearLayoutManager) mRvContent.getLayoutManager()).scrollToPositionWithOffset(position, 0);
//        smoothMoveToPosition(position, false);
    }


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
                if (!isResumed()) return;
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mShouldScroll) {
                        mShouldScroll = false;
                        int n = mToPosition - layoutManager.findFirstVisibleItemPosition();
                        if (n >= 0 && n < mRvContent.getChildCount()) {
                            //获取要置顶的项顶部距离RecyclerView顶部的距离
                            int top = mRvContent.getChildAt(n).getTop();
                            //进行第二次滚动（最后的距离）
                            mRvContent.smoothScrollBy(0, top);
                        }
                    }
                    onScrollPlayVideo(recyclerView, layoutManager.findFirstVisibleItemPosition(), layoutManager.findLastVisibleItemPosition());
                }
            }
        });
        //如果是推荐列表,不全是视频的时候,划出屏幕还会播放不然
        mRvContent.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                //不可见的情况下自动播放逻辑都不走
                if (!isResumed()) return;
                IjkVideoView ijkVideoView = view.findViewById(R.id.base_moment_video);
                if (ijkVideoView != null && !ijkVideoView.isFullScreen()) {
                    ijkVideoView.stopPlayback();
                }
            }
        });

    }


    public void onScrollPlayVideo(RecyclerView view, int firstVisiblePosition, int lastVisiblePosition) {
        if (!canAutoPlay) return;
        //循环遍历可视区域videoview,如果完全可见就开始播放
        int visibleCount = lastVisiblePosition - firstVisiblePosition;
        for (int i = 0; i <= visibleCount; i++) {
            if (view == null || view.getChildAt(i) == null) continue;
            IjkVideoView ijkVideoView = view.getChildAt(i).findViewById(R.id.base_moment_video);
            if (ijkVideoView != null) {
                Rect rect = new Rect();
                ijkVideoView.getLocalVisibleRect(rect);
                int videoHeight = ijkVideoView.getHeight();
//                Log.i("viewRect", "上: " + rect.top + " ------下:" + rect.bottom
//                        + "--------视频控件高度:" + videoHeight);
                if (rect.top < rect.bottom / 2 && rect.bottom == videoHeight) {
                    ijkVideoView.start();
                    return;
                } else if (rect.bottom < videoHeight / 2 || rect.bottom < 0) {
                    ijkVideoView.stopPlayback();
                }
            }
        }
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
                    }, getHasReport());
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
                showShareDialog(CommonHttpRequest.url, webBean, bean, position);
                break;

            case R.id.group_user_avatar:
                HelperForStartActivity.openOther(HelperForStartActivity.type_other_user,
                        bean.getContentuid());
                break;

            default:
                onItemClick(adapter, view, position);
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
                bean.setIscollection(isCollection ? "1" : "0");
                ToastUtil.showShort(isCollection ? "收藏成功" : "取消收藏成功");
            }
        });
        shareDialog.show(getChildFragmentManager(), getTag());
    }

    public boolean getHasReport() {
        return false;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        MomentsDataBean bean = (MomentsDataBean) adapter.getData().get(position);
        String contenttype = bean.getContenttype();
        if (AppUtil.isAdType(contenttype)||AppUtil.isUserType(contenttype))return;
        UmengHelper.event(UmengStatisticsKeyIds.content_view);
        if (TextUtils.equals("5", contenttype)) {
            CommentUrlBean webList = VideoAndFileUtils.getWebList(bean.getContenturllist());
            MyApplication.getInstance().putHistory(bean.getContentid());
            HelperForStartActivity.dealRequestContent(bean.getContentid());
            HelperForStartActivity.checkUrlForSkipWeb("详情", webList.info, AndroidInterface.type_recommend);
        } else {
            ArrayList<MomentsDataBean> list = (ArrayList<MomentsDataBean>) adapter.getData();
            HelperForStartActivity.openContentScrollDetail(list, position);
        }
    }

    @Override
    public boolean onBackPressed() {
        return VideoViewManager.instance().onBackPressed();
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
