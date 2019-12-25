package com.caotu.duanzhi.module.detail_scroll;

import android.view.LayoutInflater;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.holder.VideoHeaderHolder;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.dueeeke.videoplayer.player.DKVideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;

import org.greenrobot.eventbus.EventBus;

/**
 * 内容详情页面,底部的发布也得融合进来
 */
public class VideoDetailFragment extends BaseContentDetailFragment {

    private DKVideoView videoView;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_video_detail_layout;
    }

    @Override
    protected void initViewListener() {
        videoView = rootView.findViewById(R.id.video_detail);
        avatarWithNameLayout = rootView.findViewById(R.id.group_user_avatar);
        mUserIsFollow = rootView.findViewById(R.id.tv_user_follow);
        initHeader();
        adapter.disableLoadMoreIfNotFullPage();
        HeaderHeightChangeViewGroup rootViewViewById = rootView.findViewById(R.id.view_group_by_video);
        rootViewViewById.bindChildView(mRvContent);
    }

    public void initStatusBar(View backView, View moreView) {
        int statusBarHeight = DevicesUtils.getStatusBarHeight(getContext());
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) backView.getLayoutParams();
        params.topMargin = statusBarHeight;
        backView.setLayoutParams(params);
        ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) moreView.getLayoutParams();
        params2.topMargin = statusBarHeight;
        moreView.setLayoutParams(params2);
    }


    @Override
    public void onResume() {
        super.onResume();
        playVideo();
    }

    @Override
    public void onPause() {
        super.onPause();
        VideoViewManager.instance().stopPlayback();
    }

    public void playVideo() {
        if (viewHolder == null) return;
        viewHolder.autoPlayVideo();
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
        viewHolder.bindSameView(avatarWithNameLayout, mUserIsFollow, bottomLikeView);
        if (content == null) return;
        viewHolder.bindDate(content);
    }
}
