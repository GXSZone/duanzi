package com.caotu.duanzhi.module.detail_scroll;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;
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

    private int scrolly;
    private int videoMiniHeight = DevicesUtils.dp2px(200);
    private int videoHeight;

    @Override
    protected void initViewListener() {
        videoView = rootView.findViewById(R.id.video_detail);
        setKeyBoardListener();
        initHeader();
        adapter.disableLoadMoreIfNotFullPage();
        //效果上来看这样更接近
        mRvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                scrolly += dy;
                Log.i("recyclerView", "onScrolled: " + scrolly);
                if (videoHeight - scrolly <= videoMiniHeight) return;
                ViewGroup.LayoutParams params = videoView.getLayoutParams();
                params.height = videoHeight - scrolly;
                videoView.setLayoutParams(params);
            }
        });

        videoView.post(() -> videoHeight = videoView.getMeasuredHeight());
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
        playVideo();
    }
}
