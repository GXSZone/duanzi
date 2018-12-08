package com.caotu.duanzhi.module.home.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.ShareUrlBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.home.fragment.CallBackTextClick;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.NineRvHelper;
import com.caotu.duanzhi.view.widget.MyExpandTextView;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;
import com.sunfusheng.widget.ImageData;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.List;

/**
 * 内容展示列表,话题详情下的话题标签都不展示
 */

public class VideoAdapter extends BaseQuickAdapter<MomentsDataBean, BaseViewHolder> {

    public VideoAdapter() {
        super(R.layout.item_video_content);
    }

    /**
     * 文本的点击事件回调给fragment统一处理
     */
    public CallBackTextClick textClick;

    public void setTextClick(CallBackTextClick textClick) {
        this.textClick = textClick;
    }

    @Override
    protected void convert(BaseViewHolder helper, MomentsDataBean item) {
        /*--------------------------点击事件,为了bean对象的获取-------------------------------*/
//        helper.addOnClickListener(R.id.base_moment_avatar_iv);
        helper.addOnClickListener(R.id.item_iv_more_bt);
        ImageView moreAction = helper.getView(R.id.item_iv_more_bt);
        moreAction.setImageResource(getMoreImage(item.getContentuid()));
        helper.addOnClickListener(R.id.base_moment_share_iv)
                .addOnClickListener(R.id.base_moment_comment);

        /*-------------------------------点赞和踩的处理---------------------------------*/
        NineRvHelper.dealLikeAndUnlike(helper, item);

        ImageView avatar = helper.getView(R.id.base_moment_avatar_iv);
        ImageView auth = helper.getView(R.id.user_auth);
        TextView userName = helper.getView(R.id.base_moment_name_tv);
        NineRvHelper.bindItemHeader(avatar, auth, userName, item);

        MyExpandTextView contentView = helper.getView(R.id.layout_expand_text_view);
        //判断是否显示话题 1可见，0不可见
        String tagshow = item.getTagshow();
        NineRvHelper.setContentText(contentView, tagshow, item.getContenttitle(),
                "1".equals(item.getIsshowtitle()), item.getTagshowid(), item);
        contentView.setTextListener(new MyExpandTextView.ClickTextListener() {
            @Override
            public void clickText(View textView) {
                if (textClick != null) {
                    textClick.textClick(item, getPositon(helper));
                }
            }
        });

        MomentsDataBean.BestmapBean bestmap = item.getBestmap();
        if (bestmap != null && bestmap.getCommentid() != null) {
            helper.setGone(R.id.rl_best_parent, true);
            NineRvHelper.dealBest(helper, bestmap, item.getContentid());
        } else {
            helper.setGone(R.id.rl_best_parent, false);
        }
        dealVideo(helper, item);

    }

    private int getPositon(BaseViewHolder helper) {
        if (helper.getLayoutPosition() >= getHeaderLayoutCount()) {
            return helper.getLayoutPosition() - getHeaderLayoutCount();
        }
        return 0;
    }

    /**
     * 针对我的帖子的特殊之处抽离出来
     *
     * @return
     */
    public int getMoreImage(String userId) {
        if (MySpUtils.isMe(userId)) {
            return R.mipmap.my_tiezi_delete;
        }
        return R.mipmap.home_more;
    }


    private void dealVideo(BaseViewHolder helper, MomentsDataBean item) {
        MyVideoPlayerStandard videoPlayerView = helper.getView(R.id.base_moment_video);
        List<ImageData> imgList = VideoAndFileUtils.getImgList(item.getContenturllist(),
                item.getContenttext());
        if (imgList == null || imgList.size() < 2) {
            ToastUtil.showShort("内容集合解析出问题了:" + item.getContenturllist() + "---------" + item.getContenttype());
            return;
        }
        videoPlayerView.setThumbImage(imgList.get(0).url);

        boolean landscape = "1".equals(item.getContenttype());
        VideoAndFileUtils.setVideoWH(videoPlayerView, landscape);
        videoPlayerView.setOrientation(landscape);
        try {
            int playCount = Integer.parseInt(item.getPlaycount());
            videoPlayerView.setPlayCount(playCount);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        videoPlayerView.setVideoTime(item.getShowtime());

        videoPlayerView.setOnShareBtListener(new MyVideoPlayerStandard.CompleteShareListener() {
            @Override
            public void share(SHARE_MEDIA share_media) {
                doShareFromVideo(item, share_media, imgList.get(0).url);
            }

            @Override
            public void playStart() {
                CommonHttpRequest.getInstance().requestPlayCount(item.getContentid());
                //同步播放次数
                try {
                    int playCount = Integer.parseInt(item.getPlaycount());
                    playCount++;
                    item.setPlaycount(playCount + "");
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
        videoPlayerView.setVideoUrl(imgList.get(1).url, "", true);
//        //如果是第一条直接播放
//        if (helper.getAdapterPosition() == 0) {
//            videoPlayerView.startButton.performClick();
//        }
    }


    /**
     * 处理视频播放完后的分享
     *
     * @param item
     * @param share_media
     */
    private void doShareFromVideo(MomentsDataBean item, SHARE_MEDIA share_media, String cover) {
        String contentid = item.getContentid();
        CommonHttpRequest.getInstance().getShareUrl(contentid, new JsonCallback<BaseResponseBean<ShareUrlBean>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<ShareUrlBean>> response) {
                String url = response.body().getData().getUrl();
                WebShareBean bean = ShareHelper.getInstance().changeContentBean(item, share_media, cover, url);
                ShareHelper.getInstance().shareWeb(bean);
            }

            @Override
            public void onError(Response<BaseResponseBean<ShareUrlBean>> response) {
                ToastUtil.showShort("获取分享链接失败");
                super.onError(response);
            }
        });
    }
}
