package com.caotu.duanzhi.module.detail_scroll;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.detail.DetailActivity;
import com.caotu.duanzhi.module.holder.VideoHeaderHolder;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;

import org.greenrobot.eventbus.EventBus;

/**
 * 内容详情页面,底部的发布也得融合进来
 */
public class VideoDetailFragment extends BaseContentDetailFragment {

    private IjkVideoView videoView;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_video_detail_layout;
    }

    @Override
    protected void initViewListener() {
        videoView = rootView.findViewById(R.id.video_detail);
        initHeader();
        adapter.disableLoadMoreIfNotFullPage();
        HeaderHeightChangeViewGroup rootViewViewById = rootView.findViewById(R.id.view_group_by_video);
        rootViewViewById.bindChildView(mRvContent);
    }

    /**
     * 这里是为了可见性的回调比较早,初始化走得慢所以会有两套播放判断,一打开详情第一个播放
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            playVideo();
        } else {
            VideoViewManager.instance().stopPlayback();
        }
    }

    public void playVideo() {
        if (viewHolder == null) return;
        if (isVisibleToUser) {
            viewHolder.autoPlayVideo();
        }
    }

    @Override
    public boolean onBackPressed() {
        return VideoViewManager.instance().onBackPressed();
    }

    protected void initHeader() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        getPresenter();
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.layout_content_detail_video_header, mRvContent, false);
        if (viewHolder == null) {
            viewHolder = new VideoHeaderHolder(headerView);
            ((VideoHeaderHolder) viewHolder).setVideoView(videoView);
            viewHolder.bindFragment(this);
        }
        //设置头布局
        adapter.setHeaderView(headerView);
        adapter.setHeaderAndEmpty(true);
        //因为功能相同,所以就统一都由头holder处理得了,分离代码
        viewHolder.bindSameView(mUserName, mIvUserAvatar, mUserIsFollow, bottomLikeView);
        if (content == null) return;
        viewHolder.bindDate(content);
        if (userHeader == null || content == null || TextUtils.isEmpty(content.getGuajianurl()))
            return;
        userHeader.load(content.getGuajianurl());
        //这个在左右能够滑动的详情和单页面的时候都是公用的
        if (getActivity() instanceof DetailActivity) {
            viewHolder.autoPlayVideo();
        } else {
            playVideo();
        }
    }
}
