package com.caotu.duanzhi.module.home.adapter;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
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
import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.AuthBean;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseSwipeActivity;
import com.caotu.duanzhi.module.download.VideoDownloadHelper;
import com.caotu.duanzhi.module.other.WebActivity;
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
import com.caotu.duanzhi.view.FastClickListener;
import com.caotu.duanzhi.view.NineRvHelper;
import com.caotu.duanzhi.view.dialog.BaseIOSDialog;
import com.caotu.duanzhi.view.fixTextClick.CustomMovementMethod;
import com.caotu.duanzhi.view.fixTextClick.SimpeClickSpan;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dueeeke.videoplayer.ProgressManagerImpl;
import com.dueeeke.videoplayer.listener.OnVideoViewStateChangeListener;
import com.dueeeke.videoplayer.player.BaseIjkVideoView;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.controller.StandardVideoController;
import com.lzy.okgo.model.Response;
import com.sunfusheng.GlideImageView;
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
    protected void convert(BaseViewHolder helper, MomentsDataBean item) {
        ImageView moreAction = helper.getView(R.id.item_iv_more_bt);
        //web 类型没有这按钮
        if (moreAction != null) {
            moreAction.setImageResource(item.isMySelf ? R.mipmap.my_tiezi_delete : R.mipmap.home_more);
//            GlideUtils.loadImage(item.isMySelf ? R.mipmap.my_tiezi_delete : R.mipmap.home_more, moreAction);
        }
        // TODO: 2019/4/11 R.id.base_moment_comment 由于目前未设置跳转详情滑动评论页,所以不设置点击事件
        helper.addOnClickListener(R.id.item_iv_more_bt,
                R.id.base_moment_share_iv,
                R.id.base_moment_comment,
                R.id.txt_content);

        /*-------------------------------点赞和踩的处理---------------------------------*/

        GlideImageView guanjian = helper.getView(R.id.iv_user_headgear);
        guanjian.load(item.getGuajianurl());

        ImageView avatar = helper.getView(R.id.base_moment_avatar_iv);
        ImageView auth = helper.getView(R.id.user_auth);
        TextView userName = helper.getView(R.id.base_moment_name_tv);
        helper.addOnClickListener(R.id.base_moment_avatar_iv, R.id.base_moment_name_tv);
        bindItemHeader(avatar, auth, userName, item);

        //文本处理
        dealContentText(item, helper);

        //web 类型不需要处理神评和底部点赞踩操作
        if (helper.getItemViewType() != ITEM_WEB_TYPE) {
            dealLikeAndUnlike(helper, item);
            // TODO: 2019/4/18 这个小图标得分开不然详情里联动会有影响
            dealShareWxIcon(helper, item);

            MomentsDataBean.BestmapBean bestmap = item.getBestmap();
            GlideImageView bestGunajian = helper.getView(R.id.iv_best_user_headgear);
            bestGunajian.load(item.getBestguajian());
            if (bestmap != null && bestmap.getCommentid() != null) {
                helper.setGone(R.id.rl_best_parent, true);
                NineRvHelper.dealBest(helper, bestmap, item.getBestauth(), item.getContentid());
            } else {
                helper.setGone(R.id.rl_best_parent, false);
            }
        }
        otherViewBind(helper, item);
    }

    private void dealShareWxIcon(BaseViewHolder helper, MomentsDataBean item) {
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
     * 绑定头部布局
     *
     * @param userPhoto
     * @param userAuth
     * @param userName
     * @param dataBean
     */
    public void bindItemHeader(ImageView userPhoto, ImageView userAuth, TextView userName,
                               MomentsDataBean dataBean) {
        GlideUtils.loadImage(dataBean.getUserheadphoto(), userPhoto, false);

        AuthBean authBean = dataBean.getAuth();
        if (authBean != null && !TextUtils.isEmpty(authBean.getAuthid())) {
            userAuth.setVisibility(View.VISIBLE);
            GlideUtils.loadImage(dataBean.authPic, userAuth);
        } else {
            userAuth.setVisibility(View.GONE);
        }
        userAuth.setOnClickListener(v -> {
            if (authBean != null && !TextUtils.isEmpty(authBean.getAuthurl())) {
                WebActivity.openWeb("用户勋章", authBean.getAuthurl(), true);
            }
        });
        userName.setText(dataBean.getUsername());
    }

    public abstract void otherViewBind(BaseViewHolder helper, MomentsDataBean item);

    private void dealContentText(MomentsDataBean item, BaseViewHolder helper) {
        TextView contentView = helper.getView(R.id.txt_content);
        TextView stateView = helper.getView(R.id.txt_state);
        boolean ishowTag = "1".equals(item.getIsshowtitle());
        String contenttext = item.getContenttitle();
        String tagshow = item.getTagshow();
        if (hasTag(item, contentView, ishowTag, contenttext, tagshow)) {
            contentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, MySpUtils.getFloat(MySpUtils.SP_TEXT_SIZE));
            dealTextHasMore(item, contentView, stateView);
            return;
        }

        if (ishowTag) {
            contentView.setText(ParserUtils.htmlToSpanText(contenttext, true));
            contentView.setMovementMethod(CustomMovementMethod.getInstance());
            dealTextHasMore(item, contentView, stateView);
            contentView.setVisibility(View.VISIBLE);
        } else {
            contentView.setVisibility(View.GONE);
            stateView.setVisibility(View.GONE);
        }
        contentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, MySpUtils.getFloat(MySpUtils.SP_TEXT_SIZE));
    }

    /**
     * https://github.com/binaryfork/Spanny 处理span的三方
     */

    public boolean hasTag(MomentsDataBean item, TextView contentView, boolean ishowTag, String contenttext, String tagshow) {
        if (!TextUtils.isEmpty(tagshow)) {
            String source = "#" + item.getTagshow() + "# "; //这里留一下故意多加一个空格,只是为了美观,别无他意
            // TODO: 2019-09-03 因为这个标题得放前面
            SpannableStringBuilder builder2 = new SpannableStringBuilder();

            builder2.append(source, new SimpeClickSpan() {
                @Override
                public void onSpanClick(View widget) {
                    MyApplication.getInstance().putHistory(item.getContentid());
                    HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic, item.getTagshowid());
                }
            }, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            //这里还有个判断,是否展示标题
            if (ishowTag) {
                builder2.append(ParserUtils.htmlToSpanText(contenttext, true));
            }
            contentView.setText(builder2);
            contentView.setMovementMethod(CustomMovementMethod.getInstance());
            contentView.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }

    private void dealTextHasMore(MomentsDataBean item, TextView contentView, TextView stateView) {
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

    public void dealLikeAndUnlike(BaseViewHolder helper, MomentsDataBean item) {
        TextView likeView = helper.getView(R.id.base_moment_like);
        TextView unlikeView = helper.getView(R.id.base_moment_unlike);
        TextView commentView = helper.getView(R.id.base_moment_comment);

        if (likeView == null || unlikeView == null || commentView == null) return;
        likeView.setText(Int2TextUtils.toText(item.getContentgood(), "w"));
        unlikeView.setText(Int2TextUtils.toText(item.getContentbad(), "w"));
        commentView.setText(Int2TextUtils.toText(item.getContentcomment(), "w"));

//        "0"_未赞未踩 "1"_已赞 "2"_已踩
        String goodstatus = item.getGoodstatus();

        if (TextUtils.equals("1", goodstatus)) {
            likeView.setSelected(true);
            unlikeView.setSelected(false);
        } else if (TextUtils.equals("2", goodstatus)) {
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
                    LikeAndUnlikeUtil.showLike(likeView, 20, 30);
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
                                if (goodCount < 0) {
                                    goodCount = 0;
                                }
                                likeView.setText(Int2TextUtils.toText(goodCount, "w"));
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
                                if (badCount < 0) {
                                    badCount = 0;
                                }
                                unlikeView.setText(Int2TextUtils.toText(badCount, "w"));
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
        // TODO: 2019-08-28 这里宽高还要调整
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
        IjkVideoView videoView = helper.getView(R.id.base_moment_video);
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
        //横视频把背景干掉
        if (landscape) {
            videoView.setBackgroundForVideo(null);
        } else {
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
                Activity runningActivity = MyApplication.getInstance().getRunningActivity();
                if (runningActivity instanceof BaseSwipeActivity) {
                    ((BaseSwipeActivity) runningActivity)
                            .setCanSwipe(BaseIjkVideoView.PLAYER_FULL_SCREEN != playerState);
                }
                if (playerState == BaseIjkVideoView.PLAYER_FULL_SCREEN) {
                    UmengHelper.event(UmengStatisticsKeyIds.fullscreen);
                }
            }

            @Override
            public void onPlayStateChanged(int playState) {
                if (playState == BaseIjkVideoView.STATE_PLAYING) {
                    // TODO: 2019-06-25 统计有两套,友盟一套,自己接口一套
                    UmengHelper.event(UmengStatisticsKeyIds.content_view);
                    UmengHelper.event(UmengStatisticsKeyIds.total_play);
                    CommonHttpRequest.getInstance().requestPlayCount(item.getContentid());
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
