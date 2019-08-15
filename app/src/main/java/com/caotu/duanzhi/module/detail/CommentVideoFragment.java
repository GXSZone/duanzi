package com.caotu.duanzhi.module.detail;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.caotu.duanzhi.Http.bean.AuthBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.detail_scroll.HeaderHeightChangeViewGroup;
import com.caotu.duanzhi.module.holder.CommentVideoHeaderHolder;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.sunfusheng.GlideImageView;


public class CommentVideoFragment extends CommentNewFragment {
    private IjkVideoView videoView;
    private GlideImageView userLogos;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_video_detail_layout;
    }

    @Override
    protected void initViewListener() {
        userLogos = rootView.findViewById(R.id.ll_user_logos);
        initOtherView(rootView);
        videoView = rootView.findViewById(R.id.video_detail);
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
        viewHolder.bindSameView(mUserName, mIvUserAvatar, mUserIsFollow, bottomLikeView);
        if (bean == null) return;
        viewHolder.bindDate(bean);
        if (adapter instanceof CommentReplayAdapter) {
            ((CommentReplayAdapter) adapter).setParentName(bean.username);
        }
        AuthBean auth = bean.getAuth();
        if (auth != null && !TextUtils.isEmpty(auth.getAuthid())) {
            String coverUrl = VideoAndFileUtils.getCover(auth.getAuthpic());
            userLogos.setVisibility(TextUtils.isEmpty(coverUrl) ? View.GONE : View.VISIBLE);
            userLogos.load(coverUrl);
            userLogos.setOnClickListener(v -> WebActivity.openWeb("用户勋章", auth.getAuthurl(), true));
        } else {
            userLogos.setVisibility(View.GONE);
        }

        viewHolder.autoPlayVideo();
        if (userHeader != null && bean != null && !TextUtils.isEmpty(bean.getGuajianurl())) {
            userHeader.load(bean.getGuajianurl());
        }
    }
}
