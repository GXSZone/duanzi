package com.caotu.duanzhi.module.detail;

import android.view.LayoutInflater;
import android.view.View;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.holder.CommentVideoHeaderHolder;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.view.widget.TitleView;
import com.dueeeke.videoplayer.player.DKVideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;


public class CommentVideoFragment extends CommentNewFragment {
    private DKVideoView videoView;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_video_detail_layout;
    }

    public void initOtherView(View inflate) {
        TitleView titleView = inflate.findViewById(R.id.title_view);
        titleView.setRightViewShow(bean == null || MySpUtils.isMe(bean.userid));
        titleView.setClickListener(() -> showReportDialog(bean.commentid));
    }

    @Override
    protected void initView(View inflate) {
        super.initView(inflate);
        videoView = rootView.findViewById(R.id.video_detail);
        avatarWithNameLayout = rootView.findViewById(R.id.group_user_avatar);
        mUserIsFollow = rootView.findViewById(R.id.tv_user_follow);
    }

    @Override
    public boolean onBackPressed() {
        return VideoViewManager.instance().onBackPressed();
    }

    protected void initHeader() {
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.layout_comment_detail_video_header, mRvContent, false);
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
