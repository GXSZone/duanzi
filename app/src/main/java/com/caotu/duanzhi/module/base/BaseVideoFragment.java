package com.caotu.duanzhi.module.base;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.ShareUrlBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.module.MomentsNewAdapter;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.other.HandleBackInterface;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.NetWorkUtils;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.ActionDialog;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.model.Response;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

/**
 * @author mac
 * @日期: 2018/11/12
 * @describe 关于视频播放的逻辑都放在这里处理
 */
public abstract class BaseVideoFragment extends BaseStateFragment<MomentsDataBean> implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener, HandleBackInterface {
    private int firstVisibleItem;
    private int lastVisibleItem;
    private LinearLayoutManager layoutManager;
    private MomentsNewAdapter momentsNewAdapter;
    private int visibleCount;

    @Override
    protected BaseQuickAdapter getAdapter() {
        momentsNewAdapter = new MomentsNewAdapter();
        return momentsNewAdapter;
    }

    @Override
    protected void initViewListener() {
        adapter.setOnItemChildClickListener(this);
        adapter.setOnItemClickListener(this);
        layoutManager = (LinearLayoutManager) mRvContent.getLayoutManager();
        mRvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                visibleCount = lastVisibleItem - firstVisibleItem;
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE://停止滚动
                        /**在这里执行，视频的自动播放与停止*/
                        autoPlayVideo(recyclerView);
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING://拖动
//                        autoPlayVideo(recyclerView);
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING://惯性滑动
                        MyVideoPlayerStandard.releaseAllVideos();
                        break;
                }

            }
        });

    }

    private void autoPlayVideo(RecyclerView recyclerView) {
        // 这里还得判断当前是否在wifi环境下
        if (!NetWorkUtils.isWifiConnected(MyApplication.getInstance())) return;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        for (int i = 0; i < visibleCount; i++) {
            if (layoutManager != null && layoutManager.getChildAt(i) != null &&
                    layoutManager.getChildAt(i).findViewById(R.id.base_moment_video) != null) {
                JzvdStd jzvdStd = layoutManager.getChildAt(i).findViewById(R.id.base_moment_video);
                Rect rect = new Rect();
                jzvdStd.getLocalVisibleRect(rect);
                int videoHeight = jzvdStd.getHeight();
                if (rect.top == 0 && rect.bottom == videoHeight) {
                    jzvdStd.startButton.performClick();
                    return;
                }
            }
        }
        Jzvd.releaseAllVideos();
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        MomentsDataBean bean = (MomentsDataBean) adapter.getData().get(position);
        switch (view.getId()) {
            //更多的操作的弹窗
            case R.id.item_iv_more_bt:
                ActionDialog dialog = new ActionDialog();
                dialog.setContentIdAndCallBack(bean.getContentid(), new ActionDialog.DialogListener() {
                    @Override
                    public void deleteItem() {
                        adapter.remove(position);
                    }
                });
                dialog.show(getChildFragmentManager(), "ActionDialog");
                break;
            //分享的弹窗
            case R.id.base_moment_share_iv:
                CommonHttpRequest.getInstance().getShareUrl(bean.getContentid(), new JsonCallback<BaseResponseBean<ShareUrlBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<ShareUrlBean>> response) {
                       String shareUrl = response.body().getData().getUrl();
                        boolean videoType = LikeAndUnlikeUtil.isVideoType(bean.getContenttype());
                        WebShareBean webBean = ShareHelper.getInstance().createWebBean(videoType, true
                                , VideoAndFileUtils.getVideoUrl(bean.getContenturllist()), bean.getContentid());
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
                            }
                        });
                        shareDialog.show(getChildFragmentManager(), getTag());
                    }
                });
                break;
            case R.id.expand_text_view:
                if (BaseConfig.MOMENTS_TYPE_WEB.equals(bean.getContenttype())) {
                    CommentUrlBean webList = VideoAndFileUtils.getWebList(bean.getContenturllist());
                    WebActivity.openWeb("web", webList.info, false, null);
                } else {
                    HelperForStartActivity.openContentDetail(bean, false);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        // TODO: 2018/11/13 web 类型没有详情,直接跳web页面
        MomentsDataBean bean = (MomentsDataBean) adapter.getData().get(position);
        HelperForStartActivity.openContentDetail(bean, false);
    }


    @Override
    public boolean onBackPressed() {
        return Jzvd.backPress();
    }

    @Override
    public void onPause() {
        super.onPause();
        JzvdStd.goOnPlayOnPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        //home back
        JzvdStd.goOnPlayOnResume();
    }
}
