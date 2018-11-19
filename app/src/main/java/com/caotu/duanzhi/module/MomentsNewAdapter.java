package com.caotu.duanzhi.module;

import android.content.pm.ActivityInfo;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
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
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.lzy.okgo.model.Response;
import com.sunfusheng.util.MediaFileUtils;
import com.sunfusheng.widget.GridLayoutHelper;
import com.sunfusheng.widget.ImageData;
import com.sunfusheng.widget.NineImageView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/6/21 14:06
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
        helper.addOnClickListener(R.id.base_moment_share_iv)
                //内容详情
                .addOnClickListener(R.id.expand_text_view)
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
        }
        likeView.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestLikeOrUnlike(item.getContentuid(),
                        item.getContentid(), true, likeView.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                int goodCount = item.getContentgood();
                                if (likeView.isSelected()) {
                                    goodCount--;
                                    likeView.setSelected(false);
                                } else {
                                    goodCount++;
                                    likeView.setSelected(true);
                                }
                                likeView.setText(Int2TextUtils.toText(goodCount, "w"));
                                item.setContentgood(goodCount);
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
                                int badCount = item.getContentbad();
                                if (unlikeView.isSelected()) {
                                    badCount--;
                                    unlikeView.setSelected(false);
                                } else {
                                    badCount++;
                                    unlikeView.setSelected(true);
                                }
                                unlikeView.setText(Int2TextUtils.toText(badCount, "w"));
                                item.setContentbad(badCount);
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

        GlideUtils.loadImage(item.getUserheadphoto(), helper.getView(R.id.base_moment_avatar_iv));

        helper.setText(R.id.base_moment_name_tv, item.getUsername());

        TextView contentView = helper.getView(R.id.expand_text_view);
        //判断是否显示话题 1可见，0不可见
        String tagshow = item.getTagshow();
        setContentText(contentView, tagshow, item.getContenttitle(),
                "1".equals(item.getIsshowtitle()), item.getTagshowid(), item);

        MomentsDataBean.BestmapBean bestmap = item.getBestmap();
        dealBest(helper, bestmap, item.getContentid());

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
        if (TextUtils.isEmpty(item.getPlaycount())) {
            int playCount = Integer.parseInt(item.getPlaycount());
            videoPlayerView.setPlayCount(playCount);
        }

        videoPlayerView.setOnShareBtListener(new MyVideoPlayerStandard.CompleteShareListener() {
            @Override
            public void share(SHARE_MEDIA share_media) {
                doShareFromVideo(item, share_media, imgList.get(0).url);
            }

            @Override
            public void playStart() {
                CommonHttpRequest.getInstance().requestPlayCount(item.getContentid());
            }
        });
        videoPlayerView.setVideoUrl(imgList.get(1).url, "", true);
        //如果是第一条直接播放
        if (helper.getAdapterPosition() == 0) {
            videoPlayerView.startButton.performClick();
        }
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
                        .enableRoundCorner(false)
                        .setData(imgList, NineLayoutHelper.getInstance().getLayoutHelper(imgList));
                multiImageView.setClickable(true);
                multiImageView.setFocusable(true);
                multiImageView.setOnItemClickListener(new NineImageView.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        HelperForStartActivity.openImageWatcher(position, imgList,
                                (ImageView) multiImageView.getChildAt(position));
                    }
                });
                break;
            //web类型没有底部点赞等一些操作
            case "5":
                helper.setGone(R.id.base_moment_imgs_ll, true);
                helper.setGone(R.id.bottom_parent, false);
                CommentUrlBean webList = VideoAndFileUtils.getWebList(contenturllist);
                List<ImageData> img = new ArrayList<>(1);
                img.add(new ImageData(webList.cover));
                NineImageView oneImage = helper.getView(R.id.base_moment_spl_imgs_ll);
                oneImage.loadGif(false)
                        .enableRoundCorner(false)
                        .setData(img, new GridLayoutHelper(1, NineLayoutHelper.getMaxImgWidth(), DevicesUtils.dp2px(140), 0));
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
        if (bestmap != null && !TextUtils.isEmpty(bestmap.getCommentid())) {
            helper.setGone(R.id.rl_best_parent, true);
            GlideUtils.loadImage(bestmap.getUserheadphoto(), helper.getView(R.id.iv_best_avatar));

            helper.setText(R.id.tv_spl_name, bestmap.getUsername());
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
            ImageView splLike = helper.getView(R.id.base_moment_spl_like_iv);
            splLike.setSelected(LikeAndUnlikeUtil.isLiked(bestmap.getGoodstatus()));
            splLike.setOnClickListener(new FastClickListener() {
                @Override
                protected void onSingleClick() {

                    CommonHttpRequest.getInstance().requestCommentsLike(bestmap.getUserid(),
                            contentid, bestmap.getCommentid(), splLike.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                                @Override
                                public void onSuccess(Response<BaseResponseBean<String>> response) {
                                    splLike.setSelected(!splLike.isSelected());
                                }
                            });
                }
            });

            // TODO: 2018/11/12 判断类型后展示,九宫格和单视频显示隐藏判断,已在框架内部做处理了imageCell控件
            NineImageView bestLayout = helper.getView(R.id.base_moment_spl_imgs_ll);
            ArrayList<ImageData> commentShowList = VideoAndFileUtils.getDetailCommentShowList(bestmap.getCommenturl());
            if (commentShowList == null || commentShowList.size() == 0) return;
            bestLayout.loadGif(false)
                    .enableRoundCorner(false)
                    .setData(commentShowList, new GridLayoutHelper(3, NineLayoutHelper.getCellWidth(),
                            NineLayoutHelper.getCellHeight(), NineLayoutHelper.getMargin()));
            bestLayout.setOnItemClickListener(new NineImageView.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    String url = commentShowList.get(position).url;
                    if (MediaFileUtils.getMimeFileIsVideo(url)) {
                        //直接全屏
                        Jzvd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                        JzvdStd.startFullscreen(bestLayout.getContext()
                                , MyVideoPlayerStandard.class, url, "");
                    } else {
                        HelperForStartActivity.openImageWatcher(position, commentShowList,
                                (ImageView) bestLayout.getChildAt(position));
                    }
                }
            });

        } else {
            helper.setGone(R.id.rl_best_parent, false);
        }
    }

    /**
     * 处理显示内容
     *
     * @param contentView
     * @param tagshow
     * @param contenttext
     * @param ishowTag
     * @param tagshowid
     * @param bean
     */
    private void setContentText(TextView contentView, String tagshow, String contenttext,
                                boolean ishowTag, String tagshowid, MomentsDataBean bean) {
        if (ishowTag && !TextUtils.isEmpty(tagshow)) {
            String source = "#" + tagshow + "#" + contenttext;
            SpannableString ss = new SpannableString(source);

            ss.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // TODO: 2018/11/8 话题详情
                    HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic, tagshowid);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }
            }, 0, tagshow.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(DevicesUtils.getColor(R.color.color_FF698F)),
                    0, tagshow.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            contentView.setText(ss);
            contentView.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            contentView.setText(contenttext);
        }
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
