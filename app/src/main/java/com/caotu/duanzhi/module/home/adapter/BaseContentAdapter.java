package com.caotu.duanzhi.module.home.adapter;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.caotu.adlib.AdHelper;
import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.module.download.VideoDownloadHelper;
import com.caotu.duanzhi.other.CustomMovementMethod;
import com.caotu.duanzhi.other.FastClickListener;
import com.caotu.duanzhi.other.NineRvHelper;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.other.VideoListenerAdapter;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NineLayoutHelper;
import com.caotu.duanzhi.utils.ParserUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.BaseIOSDialog;
import com.caotu.duanzhi.view.widget.AvatarWithNameLayout;
import com.caotu.duanzhi.view.widget.EyeTopicTextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dueeeke.videoplayer.ProgressManagerImpl;
import com.dueeeke.videoplayer.controller.StandardVideoController;
import com.dueeeke.videoplayer.listener.OnVideoViewStateChangeListener;
import com.dueeeke.videoplayer.player.BaseVideoView;
import com.dueeeke.videoplayer.player.DKVideoView;
import com.lzy.okgo.model.Response;
import com.sunfusheng.transformation.BlurTransformation;
import com.sunfusheng.widget.ImageCell;
import com.sunfusheng.widget.NineImageView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.List;

/**
 * 父类处理item的头部布局和脚部布局,只有中间的布局不一样的
 */
public abstract class BaseContentAdapter extends BaseQuickAdapter<MomentsDataBean, BaseViewHolder> {
    public static final int ITEM_VIDEO_TYPE = 1;
    public static final int ITEM_IMAGE_TYPE = 2;
    public static final int ITEM_WEB_TYPE = 3;
    public static final int ITEM_ONLY_ONE_IMAGE = 4;
    public static final int ITEM_AD_TYPE = 5;
    public static final int ITEM_USERS_TYPE = 6;

    public BaseContentAdapter(int layoutResId) {
        super(layoutResId);
    }

    /**
     * 单条目局部刷新
     *
     * @param helper
     * @param item
     * @param payloads
     */
    @Override
    protected void convertPayloads(@NonNull BaseViewHolder helper, MomentsDataBean item, @NonNull List<Object> payloads) {
        if (helper.getItemViewType() != ITEM_WEB_TYPE) {
            dealLikeAndUnlike(helper, item);
        }
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MomentsDataBean item) {
        //广告类型条目
        if (helper.getItemViewType() == ITEM_AD_TYPE) {
            dealItemAdType(helper, item);
            return;
        }
        //感兴趣用户条目
        if (helper.getItemViewType() == ITEM_USERS_TYPE) {
            dealInterestingUsers(helper);
            return;
        }

        helper.addOnClickListener(R.id.item_iv_more_bt,
                R.id.base_moment_share_iv,
                R.id.base_moment_comment,
                R.id.txt_content,
                R.id.group_user_avatar);

        ImageView moreAction = helper.getView(R.id.item_iv_more_bt);
        moreAction.setImageResource(item.isMySelf ? R.mipmap.my_tiezi_delete : R.mipmap.home_more);

        bindItemHeader(helper, item);
        //文本处理
        dealContentText(item, helper);

        //web 类型不需要处理神评和底部点赞踩操作,神评以及底部控件的绑定
        if (helper.getItemViewType() != ITEM_WEB_TYPE) {

            dealTopic(helper, item);

            dealLikeAndUnlike(helper, item);

            iconHot(helper, item);

            MomentsDataBean.BestmapBean bestmap = item.getBestmap();
            if (bestmap != null && !TextUtils.isEmpty(bestmap.getCommentid())) {
                helper.setGone(R.id.rl_best_parent, true);
                NineRvHelper.dealBest(helper, bestmap, item.getBestauth(), item.getContentid());
            } else {
                helper.setGone(R.id.rl_best_parent, false);
            }
        } else {
            moreAction.setVisibility(View.GONE);
        }
        otherViewBind(helper, item);
    }

    protected void dealInterestingUsers(BaseViewHolder helper) {

    }

    /**
     * 这块有点神奇,需要设置高度的形式,直接gone的方式还是会有空白一块展示在列表里
     * 直接用数据bean形式可以介绍详情和列表同步时候的广告拿取
     *
     * @param helper
     * @param item
     */

    protected void dealItemAdType(@NonNull BaseViewHolder helper, MomentsDataBean item) {
        ViewGroup adContainer = helper.getView(R.id.fl_ad_content);
        ViewGroup parent = (ViewGroup) adContainer.getParent();
        ViewGroup.LayoutParams params = parent.getLayoutParams();

        if (item.adView == null) {
            params.height = 0;
            parent.setLayoutParams(params);
            return;
        }
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        parent.setLayoutParams(params);
        ImageView imageView = helper.getView(R.id.iv_item_close);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = helper.getAdapterPosition();
                position -= getHeaderLayoutCount();
                remove(position);
                ToastUtil.showShort("减少此内容推荐");
            }
        });
        AdHelper.getInstance().showAD(item.adView, adContainer);
    }

    public void dealTopic(@NonNull BaseViewHolder helper, MomentsDataBean item) {
        //点击事件内部处理
        EyeTopicTextView tagTv = helper.getView(R.id.tv_topic);
        tagTv.setTopicText(item.getTagshowid(), item.getTagshow());
    }

    private void iconHot(BaseViewHolder helper, MomentsDataBean item) {
        ImageView shareWx = helper.getView(R.id.share_wx);
        //该控件的初始大小为0
        ViewGroup.LayoutParams params = shareWx.getLayoutParams();
        if (params != null) {
            params.width = 0;
            params.height = 0;
            shareWx.setLayoutParams(params);
        }
        // TODO: 名字暂时不变,但是功能变了
        shareWx.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                if (MySpUtils.isMe(item.getContentuid())) {
                    ToastUtil.showShort("不能推荐自己的内容上热门哦");
                    return;
                }
                UmengHelper.event(UmengStatisticsKeyIds.top_popular);
                CommonHttpRequest.getInstance().goHot(item.getContentid());
            }
        });
    }

    /**
     * item 头部布局绑定
     *
     * @param helper
     * @param dataBean
     */
    public void bindItemHeader(BaseViewHolder helper, MomentsDataBean dataBean) {
        AvatarWithNameLayout avatarLayout = helper.getView(R.id.group_user_avatar);
        avatarLayout.load(dataBean.getUserheadphoto(), dataBean.getGuajianurl(), dataBean.authPic);
        avatarLayout.setUserText(dataBean.getUsername(), dataBean.authname);
    }

    public abstract void otherViewBind(BaseViewHolder helper, MomentsDataBean item);


    private void dealContentText(MomentsDataBean item, BaseViewHolder helper) {
        TextView contentView = helper.getView(R.id.txt_content);
        TextView stateView = helper.getView(R.id.txt_state);
        String contentText = item.getContenttitle();
        if ("1".equals(item.getIsshowtitle())) {
            SpannableStringBuilder text = getText(contentText);
            contentView.setText(text);
            contentView.setMovementMethod(CustomMovementMethod.getInstance());
            dealTextHasMore(item, contentView, stateView);
            contentView.setVisibility(View.VISIBLE);
        } else {
            contentView.setVisibility(View.GONE);
            stateView.setVisibility(View.GONE);
        }
        contentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, MySpUtils.getFloat(MySpUtils.SP_TEXT_SIZE));
    }

    public SpannableStringBuilder getText(String contentText) {
        return ParserUtils.htmlToSpanText(contentText, true);
    }


    public void dealTextHasMore(MomentsDataBean item, TextView contentView, TextView stateView) {
        if (item.isShowCheckAll) {
            stateView.setVisibility(View.VISIBLE);
            if (item.isExpanded) {
                contentView.setMaxLines(Integer.MAX_VALUE);
                stateView.setText("收起");
            } else {
                contentView.setMaxLines(8);
                stateView.setText("全文");
            }
            stateView.setOnClickListener(v -> {
                item.isExpanded = !item.isExpanded;

                if (item.isExpanded) {
                    UmengHelper.event(UmengStatisticsKeyIds.content_view);
                    UmengHelper.event(UmengStatisticsKeyIds.click_text);
                    CommonHttpRequest.getInstance().requestPlayCount(item.getContentid());
                    contentView.setMaxLines(Integer.MAX_VALUE);
                    stateView.setText("收起");
                } else {
                    contentView.setMaxLines(8);
                    stateView.setText("全文");
                }
            });
        } else {
            stateView.setVisibility(View.GONE);
            contentView.setMaxLines(Integer.MAX_VALUE);
        }
    }

    /*-------------------------------点赞和踩的处理---------------------------------*/

    public void dealLikeAndUnlike(BaseViewHolder helper, MomentsDataBean item) {
        TextView likeView = helper.getView(R.id.base_moment_like);
        TextView unlikeView = helper.getView(R.id.base_moment_unlike);
        TextView commentView = helper.getView(R.id.base_moment_comment);

        if (likeView == null || unlikeView == null || commentView == null) return;
        likeView.setText(Int2TextUtils.toText(item.getContentgood(), "顶"));
        unlikeView.setText(Int2TextUtils.toText(item.getContentbad(), "踩"));
        commentView.setText(Int2TextUtils.toText(item.getContentcomment(), "评论"));

//        "0"_未赞未踩 "1"_已赞 "2"_已踩
        if (TextUtils.equals("1", item.getGoodstatus())) {
            likeView.setSelected(true);
            unlikeView.setSelected(false);
        } else if (TextUtils.equals("2", item.getGoodstatus())) {
            unlikeView.setSelected(true);
            likeView.setSelected(false);
        } else {
            likeView.setSelected(false);
            unlikeView.setSelected(false);
        }
        likeView.setTag(UmengStatisticsKeyIds.content_like);
        likeView.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                if (!likeView.isSelected()) {
                    LikeAndUnlikeUtil.showLike(likeView);
                    ImageView shareWx = helper.getView(R.id.share_wx);
                    showWxShareIcon(shareWx, item.isMySelf);
                }

                CommonHttpRequest.getInstance().requestLikeOrUnlike(item.getContentuid(),
                        item.getContentid(), true, likeView.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                if (TextUtils.equals("2", item.getGoodstatus())) {
                                    unlikeView.setSelected(false);
                                    if (item.getContentbad() > 0) {
                                        item.setContentbad(item.getContentbad() - 1);
                                        unlikeView.setText(Int2TextUtils.toText(item.getContentbad(), "踩"));
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
                                if (goodCount < 0) {
                                    goodCount = 0;
                                }
                                likeView.setText(Int2TextUtils.toText(goodCount, "顶"));
                                item.setContentgood(goodCount);

                                //修改goodstatus状态 "0"_未赞未踩 "1"_已赞 "2"_已踩
                                item.setGoodstatus(likeView.isSelected() ? "1" : "0");

                            }
                        });
            }
        });
        unlikeView.setTag(UmengStatisticsKeyIds.content_unlike);
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
                                        likeView.setText(Int2TextUtils.toText(item.getContentgood(), "顶"));
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
                                if (badCount < 0) {
                                    badCount = 0;
                                }
                                unlikeView.setText(Int2TextUtils.toText(badCount, "踩"));
                                item.setContentbad(badCount);

                                //修改goodstatus状态 "0"_未赞未踩 "1"_已赞 "2"_已踩
                                item.setGoodstatus(unlikeView.isSelected() ? "2" : "0");
                            }
                        });
            }
        });
    }

    /**
     * 这两个数据是在xml 量出来的,宽展示 80dp ,高 : 55dp
     *
     * @param shareWx
     * @param isMySelf
     */
    private void showWxShareIcon(View shareWx, boolean isMySelf) {
        // TODO: 2019-09-02 这里还需要判断,该用户是否有该资格,没资格也不展示
        if (!CommonHttpRequest.canGoHot || isMySelf) return;
        if (shareWx == null) return;
        ViewGroup.LayoutParams params = shareWx.getLayoutParams();
        if (params == null) return;
        if (params.height > 10 || params.width > 10) return;  //在方法里过滤
        int px = DevicesUtils.dp2px(40);
        ValueAnimator anim = ValueAnimator.ofInt(0, px);
        anim.setInterpolator(new OvershootInterpolator());
        anim.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            params.width = value + px;
            params.height = value;
            shareWx.setLayoutParams(params);
        });
        anim.start();
    }

    /**
     * 处理图片,包括单图和多图
     *
     * @param helper
     * @param item
     */
    public void dealImages(BaseViewHolder helper, MomentsDataBean item) {
        //神评区的显示隐藏在上面判断
        if (item.imgList == null || item.imgList.size() == 0) {
            ImageCell oneImage = helper.getView(R.id.only_one_image);
            if (oneImage != null) {
                oneImage.setVisibility(View.GONE);
            }
            NineImageView multiImageView = helper.getView(R.id.base_moment_imgs_ll);
            if (multiImageView != null) {
                multiImageView.setVisibility(View.GONE);
            }
            return;
        }
        //区分是单图还是多图
        if (item.imgList.size() == 1) {
            ImageCell oneImage = helper.getView(R.id.only_one_image);
            oneImage.setVisibility(View.VISIBLE);
            oneImage.setOnClickListener(v ->
                    HelperForStartActivity.openImageWatcher(0, item.imgList, item.getContentid(), item.getContenttag()));
            int max = DevicesUtils.getSrecchWidth() - DevicesUtils.dp2px(40);
            int min = max / 3;
            int width = item.imgList.get(0).realWidth;
            int height = item.imgList.get(0).realHeight;

            if (width > 0 && height > 0) {
                float whRatio = width * 1f / height;
                if (width > height) {
                    width = max;
                    height = Math.max(min, (int) (width / whRatio));
                } else {
                    height = Math.max(min, Math.min(height, max));
                    width = Math.max(min, (int) (height * whRatio));
                }
            } else {
                width = min;
                height = min;
            }
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) oneImage.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;
            oneImage.setLayoutParams(layoutParams);
            oneImage.setData(item.imgList.get(0));
        } else {
            NineImageView multiImageView = helper.getView(R.id.base_moment_imgs_ll);
            if (multiImageView == null) return;
            multiImageView.setVisibility(View.VISIBLE);
            multiImageView.loadGif(false)
                    .enableRoundCorner(false)
                    .setData(item.imgList, NineLayoutHelper.getInstance().getContentLayoutHelper(item.imgList));

            multiImageView.setOnItemClickListener(position ->
                    HelperForStartActivity.openImageWatcher(position, item.imgList, item.getContentid(), item.getContenttag()));

        }
    }

    /**
     * 处理视频
     *
     * @param helper
     * @param item
     */
    public void dealVideo(BaseViewHolder helper, MomentsDataBean item) {
        if (item.imgList == null || item.imgList.size() < 2) {
            return;
        }
        DKVideoView videoView = helper.getView(R.id.base_moment_video);
        String videoUrl = item.imgList.get(1).url;
        videoView.setUrl(videoUrl); //设置视频地址

        StandardVideoController controller = new StandardVideoController(videoView.getContext()) {
            @Override
            public void replayAction() {
                if (!MySpUtils.getReplayTip()) {
                    showTipDialog();
                } else {
                    doSuper();
                }
            }

            public void doSuper() {
                UmengHelper.event(UmengStatisticsKeyIds.replay);
                super.replayAction();
            }

            public void showTipDialog() {
                Activity activity = MyApplication.getInstance().getRunningActivity();
                BaseIOSDialog dialog = new BaseIOSDialog(activity, new BaseIOSDialog.OnClickListener() {
                    @Override
                    public void okAction() {
                        doSuper();
                    }

                    @Override
                    public void cancelAction() {
                        videoView.setLooping(true);
                        doSuper();
                        MySpUtils.setReplaySwitch(true);
                    }
                });
                dialog.setCancelText("自动重播")
                        .setOkText("手动重播")
                        .setTitleText("亲爱的段友，视频播完后你的喜好？")
                        .show();
                MySpUtils.setReplayTip();
            }
        };
        final String cover = item.imgList.get(0).url;

        // TODO: 2019-05-31 自动重播的关键代码,开启自动重播会导致没有播放完成的回调
        boolean videoMode = MySpUtils.getReplaySwitch();
        videoView.setLooping(videoMode);
        //保存播放进度
        videoView.setProgressManager(new ProgressManagerImpl());

        boolean landscape = "1".equals(item.getContenttype());
        VideoAndFileUtils.setVideoWH(videoView, landscape);

        GlideUtils.loadImage(cover, controller.getThumb());
        if (BaseConfig.isSupportBlur && !landscape) {
            Glide.with(videoView)
                    .load(cover)
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(
                            videoView.getContext())))
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            videoView.setBackgroundForVideo(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            videoView.setBackgroundForVideo(placeholder);
                        }
                    });
        } else {
            videoView.setBackgroundForVideo(null);
        }

        videoView.addToVideoViewManager();
        videoView.setVideoController(controller); //设置控制器，如需定制可继承BaseVideoController
        controller.setVideoInfo(item.getShowtime(), item.getPlaycount());
        controller.setIsMySelf(MySpUtils.isMe(item.getContentuid()));
        controller.setMyVideoOtherListener(new VideoListenerAdapter() {
            @Override
            public void share(byte type) {
                doShareFromVideo(item, ShareHelper.translationShareType(type), cover);
            }

            @Override
            public void timeToShowWxIcon() {
                showWxShareIcon(helper.getView(R.id.share_wx), item.isMySelf);
            }

            @Override
            public void download() {
                VideoDownloadHelper.getInstance().startDownLoad(true, item.getContentid(), videoUrl);
            }

            @Override
            public void clickTopic() {
                NineRvHelper.showReportDialog(item.getContentid(), 0);
            }
        });

        videoView.addOnVideoViewStateChangeListener(new OnVideoViewStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {
                if (playerState == BaseVideoView.PLAYER_FULL_SCREEN) {
                    UmengHelper.event(UmengStatisticsKeyIds.fullscreen);
                }
            }

            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == BaseVideoView.STATE_PLAYING) {
                    // TODO: 2019-06-25 统计有两套,友盟一套,自己接口一套
                    UmengHelper.event(UmengStatisticsKeyIds.content_view);
                    UmengHelper.event(UmengStatisticsKeyIds.total_play);
                    CommonHttpRequest.getInstance().requestPlayCount(item.getContentid());
                } else if (playState == BaseVideoView.STATE_PREPARED) {
                    int parseInt = Integer.parseInt(item.getPlaycount());
                    parseInt++;
                    String value = String.valueOf(parseInt);
                    item.setPlaycount(value);
                    controller.setPlayNum(value);
                }
            }
        });
    }

    /**
     * 处理视频播放完后的分享
     *
     * @param item
     * @param share_media
     */
    private void doShareFromVideo(MomentsDataBean item, SHARE_MEDIA share_media, String cover) {
        WebShareBean bean = ShareHelper.getInstance().changeContentBean(item, share_media, cover, CommonHttpRequest.url);
        ShareHelper.getInstance().shareWeb(bean);
    }
}
