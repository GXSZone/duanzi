package com.caotu.duanzhi.module.base;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.module.MomentsNewAdapter;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.other.HandleBackInterface;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.NetWorkUtils;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.ActionDialog;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.chad.library.adapter.base.BaseQuickAdapter;

import cn.jzvd.Jzvd;

/**
 * @author mac
 * @日期: 2018/11/12
 * @describe 关于视频播放的逻辑都放在这里处理
 */
public abstract class BaseVideoFragment extends BaseStateFragment<MomentsDataBean> implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener, HandleBackInterface {
    private int firstVisibleItem;
    private int lastVisibleItem;
    private LinearLayoutManager layoutManager;

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new MomentsNewAdapter();
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

    /**
     * 自动播放
     */
    private void autoPlayVideo(RecyclerView recyclerView) {
        // 这里还得判断当前是否在wifi环境下
        if (!NetWorkUtils.isWifiConnected(MyApplication.getInstance())) return;
        if (firstVisibleItem == 0 && lastVisibleItem == 0 && recyclerView.getChildAt(0) != null) {

            MyVideoPlayerStandard videoView = null;
            if (recyclerView != null && recyclerView.getChildAt(0) != null) {
                videoView = recyclerView.getChildAt(0).findViewById(R.id.base_moment_video);
            }
            if (videoView != null) {
                if (videoView.currentState == Jzvd.CURRENT_STATE_NORMAL ||
                        videoView.currentState == Jzvd.CURRENT_STATE_PAUSE) {
                    videoView.startVideo();
                }
            }
        }

        for (int i = 0; i <= lastVisibleItem; i++) {
            if (recyclerView == null || recyclerView.getChildAt(i) == null) {
                return;
            }
            MyVideoPlayerStandard
                    videoView = recyclerView.getChildAt(i).findViewById(R.id.base_moment_video);
            if (videoView != null) {

                Rect rect = new Rect();
                //获取视图本身的可见坐标，把值传入到rect对象中
                videoView.getLocalVisibleRect(rect);
                //获取视频的高度
                int videoHeight = videoView.getHeight();

                if (rect.top <= 100 && rect.bottom >= videoHeight) {
                    if (videoView.currentState == Jzvd.CURRENT_STATE_NORMAL
                            || videoView.currentState == Jzvd.CURRENT_STATE_PAUSE) {
                        videoView.startVideo();
                    }
                    return;
                }

                Jzvd.releaseAllVideos();

            } else {
                Jzvd.releaseAllVideos();
            }

        }

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
                ShareDialog shareDialog = new ShareDialog();
                shareDialog.show(getChildFragmentManager(), "shareDialog");
                break;
            case R.id.expand_text_view:
                if (BaseConfig.MOMENTS_TYPE_WEB.equals(bean.getContenttype())) {
                    CommentUrlBean webList = VideoAndFileUtils.getWebList(bean.getContenturllist());
//                    ShareHelper.getInstance().
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
        Jzvd.releaseAllVideos();
    }
}
