package com.caotu.duanzhi.module;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.home.fragment.CallBackTextClick;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NineLayoutHelper;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.NineRvHelper;
import com.caotu.duanzhi.view.widget.MyExpandTextView;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.sunfusheng.GlideImageView;
import com.sunfusheng.widget.ImageCell;
import com.sunfusheng.widget.ImageData;
import com.sunfusheng.widget.NineImageView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.List;

/**
 * 内容展示列表,话题详情下的话题标签都不展示
 * link{https://github.com/razerdp/FriendCircle}
 */

public class MomentsNewAdapter extends BaseQuickAdapter<MomentsDataBean, BaseViewHolder> {
    //1横 2竖 3图片 4文字  5web
    public static final int ITEM_VIDEO_TYPE = 1;
    public static final int ITEM_IMAGE_TYPE = 2;
    public static final int ITEM_WEB_TYPE = 3;
    public static final int ITEM_ONLY_ONE_IMAGE = 4;

    public MomentsNewAdapter() {
        super(R.layout.item_base_content);

        setMultiTypeDelegate(new MultiTypeDelegate<MomentsDataBean>() {
            @Override
            protected int getItemType(MomentsDataBean entity) {
                //根据你的实体类来判断布局类型
                String contenttype = entity.getContenttype();
                int type;
                if (TextUtils.isEmpty(contenttype)) {
                    type = ITEM_ONLY_ONE_IMAGE;
                    return type;
                }
                switch (contenttype) {
                    //使用视频布局
                    case "1"://横视频
                    case "2"://竖视频
                        type = ITEM_VIDEO_TYPE;
                        break;
                    case "5":
                        type = ITEM_WEB_TYPE;
                        break;
                    //默认也就是纯文本显示
                    default:
                        ArrayList<ImageData> imgList = VideoAndFileUtils.getImgList(entity.getContenturllist(),
                                entity.getContenttext());
                        if (imgList != null && imgList.size() == 1) {
                            type = ITEM_ONLY_ONE_IMAGE;
                        } else {
                            type = ITEM_IMAGE_TYPE;
                        }

                        break;
                }
                return type;
            }
        });
        //Step.2
        getMultiTypeDelegate()
                .registerItemType(ITEM_VIDEO_TYPE, R.layout.item_video_content)
                .registerItemType(ITEM_IMAGE_TYPE, R.layout.item_base_content)
                .registerItemType(ITEM_WEB_TYPE, R.layout.item_web_type)
                .registerItemType(ITEM_ONLY_ONE_IMAGE, R.layout.item_one_image_content);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads != null && !payloads.isEmpty()) {
            if (holder.getItemViewType() != ITEM_WEB_TYPE) {
                MomentsDataBean o = (MomentsDataBean) payloads.get(0);
                NineRvHelper.dealLikeAndUnlike(holder, o);
            }
//            Log.i("eventRefresh", "onBindViewHolder: ");
        } else {
            onBindViewHolder(holder, position);
        }
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
        GlideImageView guanjian = helper.getView(R.id.iv_user_headgear);
        guanjian.load(item.getGuajianurl());

        ImageView avatar = helper.getView(R.id.base_moment_avatar_iv);
        ImageView auth = helper.getView(R.id.user_auth);
        TextView userName = helper.getView(R.id.base_moment_name_tv);
        NineRvHelper.bindItemHeader(avatar, auth, userName, item);

        MyExpandTextView contentView = helper.getView(R.id.layout_expand_text_view);
        //判断是否显示话题 1可见，0不可见
        String tagshow = item.getTagshow();
        // TODO: 2018/12/14 该position已经是修正过减去头布局的position
        dealContentText(item, contentView, tagshow, getPositon(helper));


        //Step.3
        switch (helper.getItemViewType()) {
            case ITEM_WEB_TYPE:
                //web类型没有底部点赞等一些操作
                CommentUrlBean webList = VideoAndFileUtils.getWebList(item.getContenturllist());
                GlideImageView imageView = helper.getView(R.id.web_image);
                imageView.load(webList.cover, R.mipmap.shenlue_logo);
                helper.addOnClickListener(R.id.web_image);
                break;
            case ITEM_VIDEO_TYPE:
                //处理视频
                checkHasBestMap(helper, item);
                NineRvHelper.dealLikeAndUnlike(helper, item);
                dealVideo(helper, item);
                break;
            case ITEM_IMAGE_TYPE:
                //处理九宫格
                checkHasBestMap(helper, item);
                NineRvHelper.dealLikeAndUnlike(helper, item);
                dealNineLayout(item, helper);
                break;
            case ITEM_ONLY_ONE_IMAGE:
                //处理九宫格
                checkHasBestMap(helper, item);
                NineRvHelper.dealLikeAndUnlike(helper, item);
//                String type = item.getContenttype();
                ArrayList<ImageData> imgList = VideoAndFileUtils.getImgList(item.getContenturllist(), item.getContenttext());

                ImageCell oneImage = helper.getView(R.id.only_one_image);
                oneImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (imgList == null) return;
                        HelperForStartActivity.openImageWatcher(0, imgList, item.getContentid());
                    }
                });
                if (imgList == null || imgList.size() == 0) {
                    oneImage.setVisibility(View.GONE);
                } else {
                    oneImage.setVisibility(View.VISIBLE);
                    int max = DevicesUtils.getSrecchWidth() - DevicesUtils.dp2px(40);
                    int min = max / 2;

                    int width = imgList.get(0).realWidth;
                    int height = imgList.get(0).realHeight;

                    if (width > 0 && height > 0) {
                        float whRatio = width * 1f / height;
                        if (width > height) {
                            width = Math.max(min, Math.min(width, max));
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
                    oneImage.setData(imgList.get(0));
                }
                break;
        }

//        showShareIconTipDialog(helper);
    }

//    private void showShareIconTipDialog(BaseViewHolder helper) {
//        if (!MySpUtils.getBoolean(MySpUtils.SP_DOWNLOAD_GUIDE, false) &&
//                helper.getLayoutPosition() == 0) {
//            View TagView = helper.getView(R.id.base_moment_share_iv);
//            if (TagView == null) return;
//            TagView.postDelayed(() -> {
//                GuideHelper guideHelper = new GuideHelper(MyApplication.getInstance().getRunningActivity());
//
//                GuideHelper.TipData tipData1 = new GuideHelper.TipData(R.mipmap.guide_downhere,
//                        Gravity.LEFT | Gravity.TOP, TagView);
//                tipData1.setLocation(DevicesUtils.dp2px(50), DevicesUtils.dp2px(50));
//                guideHelper.addPage(tipData1);
//                guideHelper.show(false);
//            }, 500);
//            MySpUtils.putBoolean(MySpUtils.SP_DOWNLOAD_GUIDE, true);
//        }
//    }

    private int getPositon(BaseViewHolder helper) {
        if (helper.getLayoutPosition() >= getHeaderLayoutCount()) {
            return helper.getLayoutPosition() - getHeaderLayoutCount();
        }
        return 0;
    }

    private void checkHasBestMap(BaseViewHolder helper, MomentsDataBean item) {
        GlideImageView bestGunajian = helper.getView(R.id.iv_best_user_headgear);
        if (bestGunajian != null) {
            //为了防止web类型
            bestGunajian.load(item.getBestguajian());
        }
        MomentsDataBean.BestmapBean bestmap = item.getBestmap();
        if (bestmap != null && !TextUtils.isEmpty(bestmap.getCommentid())) {
            helper.setGone(R.id.rl_best_parent, true);
            NineRvHelper.dealBest(helper, bestmap, item.getBestauth(), item.getContentid());
        } else {
            helper.setGone(R.id.rl_best_parent, false);
        }
    }


    public void dealContentText(MomentsDataBean item, MyExpandTextView contentView, String tagshow, int positon) {
        NineRvHelper.setContentText(contentView, tagshow, item.getContenttitle(),
                "1".equals(item.getIsshowtitle()), item.getTagshowid(), item);
        contentView.setTextListener(textView -> {
            if (textClick != null) {
                textClick.textClick(item, positon);
            }
        });
    }

    /**
     * 文本的点击事件回调给fragment统一处理
     */
    public CallBackTextClick textClick;

    public void setTextClick(CallBackTextClick textClick) {
        this.textClick = textClick;
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
            public void justPlay() {
                videoPlayerView.setOrientation(landscape);
                videoPlayerView.dealPlayCount(item, videoPlayerView);
            }
        });
        videoPlayerView.setVideoUrl(imgList.get(1).url, "", true);
    }

    private void dealNineLayout(MomentsDataBean item, BaseViewHolder helper) {
        //神评区的显示隐藏在上面判断

        String contenturllist = item.getContenturllist();
        NineImageView multiImageView = helper.getView(R.id.base_moment_imgs_ll);

        ArrayList<ImageData> imgList = VideoAndFileUtils.getImgList(contenturllist, item.getContenttext());
        if (imgList == null || imgList.size() == 0) {
            multiImageView.setVisibility(View.GONE);
            return;
        }
        multiImageView.setVisibility(View.VISIBLE);
        multiImageView.post(new Runnable() {
            @Override
            public void run() {
                //区分是单图还是多图
                multiImageView.loadGif(false)
                        .setData(imgList, NineLayoutHelper.getInstance().getLayoutHelper(imgList));
                multiImageView.setOnItemClickListener(position ->
                        HelperForStartActivity.openImageWatcher(position, imgList, item.getContentid()));
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
