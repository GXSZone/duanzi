package com.caotu.duanzhi.module.detail;

import android.view.LayoutInflater;
import android.view.View;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.detail_scroll.HeaderHeightChangeViewGroup;
import com.caotu.duanzhi.module.holder.CommentVideoHeaderHolder;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;


public class CommentVideoFragment extends CommentNewFragment {
    private IjkVideoView videoView;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_video_detail_layout;
    }

    @Override
    protected void initViewListener() {
        initOtherView(rootView);
        videoView = rootView.findViewById(R.id.video_detail);
        avatarWithNameLayout = rootView.findViewById(R.id.group_user_avatar);
        mUserIsFollow = rootView.findViewById(R.id.tv_user_follow);
        initHeader();
        adapter.disableLoadMoreIfNotFullPage();
        HeaderHeightChangeViewGroup rootViewViewById = rootView.findViewById(R.id.view_group_by_video);
        rootViewViewById.bindChildView(mRvContent, DevicesUtils.dp2px(200));
    }

    @Override
    public boolean onBackPressed() {
        return VideoViewManager.instance().onBackPressed();
    }

    protected void initHeader() {
        getPresenter();
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.layout_content_detail_video_header, mRvContent, false);
        if (viewHolder == null) {
            viewHolder = new CommentVideoHeaderHolder(headerView);
            ((CommentVideoHeaderHolder) viewHolder).setVideoView(videoView);
            viewHolder.bindFragment(this);
        }
        //设置头布局
        adapter.setHeaderView(headerView);
        adapter.setHeaderAndEmpty(true);
        //因为功能相同,所以就统一都由头holder处理得了,分离代码
        viewHolder.bindSameView(avatarWithNameLayout, mUserIsFollow, bottomLikeView);
        if (bean == null) return;
        viewHolder.bindDate(bean);
        if (adapter instanceof CommentReplayAdapter) {
            ((CommentReplayAdapter) adapter).setParentName(bean.username);
        }
        viewHolder.autoPlayVideo();
    }
}
