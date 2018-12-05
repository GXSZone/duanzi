package com.caotu.duanzhi.module;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.ShareUrlBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NineLayoutHelper;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.FastClickListener;
import com.caotu.duanzhi.view.NineRvHelper;
import com.caotu.duanzhi.view.widget.MyExpandTextView;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.lzy.okgo.model.Response;
import com.sunfusheng.widget.GridLayoutHelper;
import com.sunfusheng.widget.ImageData;
import com.sunfusheng.widget.NineImageView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.List;

/**
 * 内容展示列表,话题详情下的话题标签都不展示
 */

public class MomentsNewAdapter extends BaseQuickAdapter<MomentsDataBean, BaseViewHolder> {
    //1横 2竖 3图片 4文字  5web
    public static final int ITEM_VIDEO_TYPE = 1;
    public static final int ITEM_IMAGE_TYPE = 2;

    public MomentsNewAdapter() {
        super(R.layout.item_base_content);

        setMultiTypeDelegate(new MultiTypeDelegate<MomentsDataBean>() {
            @Override
            protected int getItemType(MomentsDataBean entity) {
                //根据你的实体类来判断布局类型
                String contenttype = entity.getContenttype();
                int type;
                switch (contenttype) {
                    //使用视频布局
                    case "1"://横视频
                    case "2"://竖视频
                        type = ITEM_VIDEO_TYPE;
                        break;
                    default:
                        type = ITEM_IMAGE_TYPE;
                        break;
                }
                return type;
            }
        });
        //Step.2
        getMultiTypeDelegate()
                .registerItemType(ITEM_VIDEO_TYPE, R.layout.item_video_content)
                .registerItemType(ITEM_IMAGE_TYPE, R.layout.item_base_content);
    }

    @Override
    protected void convert(BaseViewHolder helper, MomentsDataBean item) {
        /*--------------------------点击事件,为了bean对象的获取-------------------------------*/
//        helper.addOnClickListener(R.id.base_moment_avatar_iv);
        helper.addOnClickListener(R.id.item_iv_more_bt);
        ImageView moreAction = helper.getView(R.id.item_iv_more_bt);
        moreAction.setImageResource(getMoreImage(item.getContentuid()));
        //如果是web类型不显示右上角的更多按钮
        moreAction.setVisibility(TextUtils.equals("5", item.getContenttype()) ? View.GONE : View.VISIBLE);

        helper.addOnClickListener(R.id.base_moment_share_iv)
                //内容详情
                .addOnClickListener(R.id.base_moment_comment);
        /*-------------------------------点赞和踩的处理---------------------------------*/
        helper.setText(R.id.base_moment_like, Int2TextUtils.toText(item.getContentgood(), "w"))
                .setText(R.id.base_moment_unlike, Int2TextUtils.toText(item.getContentbad(), "w"))
                .setText(R.id.base_moment_comment, Int2TextUtils.toText(item.getContentcomment(), "w"));
//        "0"_未赞未踩 "1"_已赞 "2"_已踩
        String goodstatus = item.getGoodstatus();
        TextView likeView = helper.getView(R.id.base_moment_like);
        TextView unlikeView = helper.getView(R.id.base_moment_unlike);

        if (TextUtils.equals("1", goodstatus)) {
            likeView.setSelected(true);
        } else if (TextUtils.equals("2", goodstatus)) {
            unlikeView.setSelected(true);
        } else {
            likeView.setSelected(false);
            unlikeView.setSelected(false);
        }
        likeView.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestLikeOrUnlike(item.getContentuid(),
                        item.getContentid(), true, likeView.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                if (TextUtils.equals("2", item.getGoodstatus())) {
                                    unlikeView.setSelected(false);
                                    if (item.getContentbad() > 0) {
                                        item.setContentbad(item.getContentbad() - 1);
                                        unlikeView.setText(Int2TextUtils.toText(item.getContentbad(), "w"));
                                    }
                                }
                                int goodCount = item.getContentgood();
                                if (likeView.isSelected()) {
                                    goodCount--;
                                    likeView.setSelected(false);
                                } else {
                                    goodCount++;
                                    likeView.setSelected(true);
                                }
                                if (goodCount > 0) {
                                    likeView.setText(Int2TextUtils.toText(goodCount, "w"));
                                    item.setContentgood(goodCount);
                                }
                                //修改goodstatus状态 "0"_未赞未踩 "1"_已赞 "2"_已踩
                                item.setGoodstatus(likeView.isSelected() ? "1" : "0");

                            }
                        });
            }
        });

        unlikeView.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestLikeOrUnlike(item.getContentuid(),
                        item.getContentid(), false, unlikeView.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                if (TextUtils.equals("1", item.getGoodstatus())) {
                                    likeView.setSelected(false);
                                    if (item.getContentgood() > 0) {
                                        item.setContentgood(item.getContentgood() - 1);
                                        likeView.setText(Int2TextUtils.toText(item.getContentgood(), "w"));
                                    }
                                }
                                int badCount = item.getContentbad();
                                if (unlikeView.isSelected()) {
                                    badCount--;
                                    unlikeView.setSelected(false);
                                } else {
                                    badCount++;
                                    unlikeView.setSelected(true);
                                }
                                if (badCount > 0) {
                                    unlikeView.setText(Int2TextUtils.toText(badCount, "w"));
                                    item.setContentbad(badCount);
                                }

                                //修改goodstatus状态 "0"_未赞未踩 "1"_已赞 "2"_已踩
                                item.setGoodstatus(unlikeView.isSelected() ? "2" : "0");
                            }
                        });
            }
        });


        /*-------------------------------点赞和踩的处理结束---------------------------------*/

        helper.setOnClickListener(R.id.base_moment_avatar_iv, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2018/11/8 如果是自己则不跳转
                if (!item.getContentuid().equals(MySpUtils.getString(MySpUtils.SP_MY_ID))) {
                    HelperForStartActivity.openOther(HelperForStartActivity.type_other_user,
                            item.getContentuid());
                }
            }
        });

        GlideUtils.loadImage(item.getUserheadphoto(), helper.getView(R.id.base_moment_avatar_iv),true);

        helper.setText(R.id.base_moment_name_tv, item.getUsername());

        MyExpandTextView contentView = helper.getView(R.id.layout_expand_text_view);
        //判断是否显示话题 1可见，0不可见
        String tagshow = item.getTagshow();
        dealContentText(item, contentView, tagshow);

        MomentsDataBean.BestmapBean bestmap = item.getBestmap();
        if (bestmap != null && !TextUtils.isEmpty(bestmap.getCommentid())) {
            helper.setGone(R.id.rl_best_parent, true);
            dealBest(helper, bestmap, item.getContentid());
        } else {
            helper.setGone(R.id.rl_best_parent, false);
        }

        //Step.3
        switch (helper.getItemViewType()) {
            case ITEM_VIDEO_TYPE:
                dealVideo(helper, item);
                break;
            case ITEM_IMAGE_TYPE:
                //处理九宫格
                dealNineLayout(item, helper);
                break;
        }

    }

    public void dealContentText(MomentsDataBean item, MyExpandTextView contentView, String tagshow) {
        NineRvHelper.setContentText(contentView, tagshow, item.getContenttitle(),
                "1".equals(item.getIsshowtitle()), item.getTagshowid(), item);
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
                    videoPlayerView.setPlayCount(playCount);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
        videoPlayerView.setVideoUrl(imgList.get(1).url, "", true);
//        //如果是第一条直接播放
//        if (helper.getAdapterPosition() == 0&&) {
//            videoPlayerView.startButton.performClick();
//        }
    }

    private void dealNineLayout(MomentsDataBean item, BaseViewHolder helper) {
        //神评区的显示隐藏在上面判断
        String type = item.getContenttype();
        String contenturllist = item.getContenturllist();
        switch (type) {
            case "3":
                helper.setGone(R.id.base_moment_imgs_ll, true);
                helper.setGone(R.id.bottom_parent, true);
                ArrayList<ImageData> imgList = VideoAndFileUtils.getImgList(contenturllist, item.getContenttext());
                if (imgList == null || imgList.size() == 0) return;
                //区分是单图还是多图
                NineImageView multiImageView = helper.getView(R.id.base_moment_imgs_ll);
                multiImageView.loadGif(false)
//                        .enableRoundCorner(true)
                        .setData(imgList, NineLayoutHelper.getInstance().getLayoutHelper(imgList));
                multiImageView.setClickable(true);
                multiImageView.setFocusable(true);
                multiImageView.setOnItemClickListener(new NineImageView.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        HelperForStartActivity.openImageWatcher(position, imgList,
                                item.getContentid());
                    }
                });
                break;
            //web类型没有底部点赞等一些操作
            case "5":
                helper.setGone(R.id.base_moment_imgs_ll, true);
                helper.setVisible(R.id.bottom_parent, false);
                CommentUrlBean webList = VideoAndFileUtils.getWebList(contenturllist);
                List<ImageData> img = new ArrayList<>(1);
                img.add(new ImageData(webList.cover));
                NineImageView oneImage = helper.getView(R.id.base_moment_imgs_ll);
                oneImage.loadGif(false)
                        .enableRoundCorner(false)
                        .setData(img, new GridLayoutHelper(1, NineLayoutHelper.getMaxImgWidth(), DevicesUtils.dp2px(140), 0));
                oneImage.setOnItemClickListener(new NineImageView.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        CommentUrlBean webList = VideoAndFileUtils.getWebList(item.getContenturllist());
                        WebActivity.openWeb("web", webList.info, false, null);
                    }
                });
                break;
            //纯文字,注意分享
            case "4":
                helper.setGone(R.id.base_moment_imgs_ll, false);
                helper.setGone(R.id.bottom_parent, true);
                break;
        }

    }

    /**
     * 处理神评展示
     *
     * @param helper
     * @param bestmap
     * @param contentid
     */
    private void dealBest(BaseViewHolder helper, MomentsDataBean.BestmapBean bestmap, String contentid) {

        GlideUtils.loadImage(bestmap.getUserheadphoto(), helper.getView(R.id.iv_best_avatar));

        helper.setText(R.id.tv_spl_name, bestmap.getUsername());
        Log.i("qlwadapter", "dealBest: " + bestmap.getCommenttext());
        helper.setText(R.id.base_moment_spl_comment_tv, bestmap.getCommenttext());
        helper.setOnClickListener(R.id.iv_best_avatar, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: 2018/11/8 如果是自己则不跳转
                if (!bestmap.getUserid().equals(MySpUtils.getString(MySpUtils.SP_MY_ID))) {
                    HelperForStartActivity.openOther(HelperForStartActivity.type_other_user,
                            bestmap.getUserid());
                }
            }
        });
        //神评的点赞状态
        TextView splLike = helper.getView(R.id.base_moment_spl_like_iv);
        splLike.setSelected(LikeAndUnlikeUtil.isLiked(bestmap.getGoodstatus()));
        splLike.setText(Int2TextUtils.toText(bestmap.getCommentgood(), "W"));

        splLike.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestCommentsLike(bestmap.getUserid(),
                        contentid, bestmap.getCommentid(), splLike.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                int goodCount = 0;
                                try {
                                    goodCount = Integer.parseInt(bestmap.getCommentgood());
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                if (splLike.isSelected()) {
                                    goodCount--;
                                    splLike.setSelected(false);
                                } else {
                                    goodCount++;
                                    splLike.setSelected(true);
                                }
                                if (goodCount > 0) {
                                    //这里列表不需要改bean对象
                                    splLike.setText(Int2TextUtils.toText(goodCount, "w"));
                                    bestmap.setCommentgood(goodCount + "");
                                }
                            }
                        });
            }
        });

        RecyclerView recyclerView = helper.getView(R.id.deal_with_rv);
        String commenturl = bestmap.getCommenturl();
        if (TextUtils.isEmpty(commenturl) || "[]".equals(commenturl)) {
            recyclerView.setVisibility(View.GONE);
            return;
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }
        ArrayList<ImageData> commentShowList = VideoAndFileUtils.getDetailCommentShowList(commenturl);
        if (commentShowList == null || commentShowList.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            return;
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }
        Log.i("bestMapUrl", "dealBest: " + commentShowList.toString());

        NineRvHelper.ShowNineImage(recyclerView, commentShowList, contentid);

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
