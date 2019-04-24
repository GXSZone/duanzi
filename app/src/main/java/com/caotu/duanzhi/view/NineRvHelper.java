package com.caotu.duanzhi.view;

import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.AuthBean;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.UmengHelper;
import com.caotu.duanzhi.UmengStatisticsKeyIds;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NineLayoutHelper;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;
import com.sunfusheng.util.MediaFileUtils;
import com.sunfusheng.widget.ImageCell;
import com.sunfusheng.widget.ImageData;
import com.sunfusheng.widget.NineImageView;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdMgr;
import cn.jzvd.JzvdStd;
import cn.jzvd.bean.WebShareBean;

/**
 * 评论列表的九宫格布局帮助类
 */
public class NineRvHelper {

    /**
     * 处理神评展示
     *
     * @param helper
     * @param bestmap
     * @param bestauth
     * @param contentid
     */
    public static void dealBest(BaseViewHolder helper, MomentsDataBean.BestmapBean bestmap, AuthBean bestauth, String contentid) {
//        //统一处理神评的空白区域点击跳转
//        helper.setOnClickListener(R.id.rl_best_parent, v -> {
//            if (TextUtils.isEmpty(bestmap.getCommentid())) return;
//            HashMap<String, String> params = new HashMap<>();
//            params.put("cmtid", bestmap.getCommentid());
//            OkGo.<BaseResponseBean<CommendItemBean.RowsBean>>post(HttpApi.COMMENT_DEATIL)
//                    .upJson(new JSONObject(params))
//                    .execute(new JsonCallback<BaseResponseBean<CommendItemBean.RowsBean>>() {
//                        @Override
//                        public void onSuccess(Response<BaseResponseBean<CommendItemBean.RowsBean>> response) {
//                            CommendItemBean.RowsBean data = response.body().getData();
//                            HelperForStartActivity.openCommentDetail(data);
//                        }
//                    });
//        });

        GlideUtils.loadImage(bestmap.getUserheadphoto(), helper.getView(R.id.iv_best_avatar), true);
        helper.setText(R.id.tv_spl_name, bestmap.getUsername());
        helper.setGone(R.id.base_moment_spl_comment_tv, !TextUtils.isEmpty(bestmap.getCommenttext()));
        helper.setText(R.id.base_moment_spl_comment_tv, bestmap.getCommenttext());
        helper.setOnClickListener(R.id.iv_best_avatar, v -> {
            MyApplication.getInstance().putHistory(contentid);
            HelperForStartActivity.openOther(HelperForStartActivity.type_other_user,
                    bestmap.getUserid());
        });

        ImageView bestAuth = helper.getView(R.id.best_user_auth);

        if (bestauth != null && !TextUtils.isEmpty(bestauth.getAuthid())) {
            bestAuth.setVisibility(View.VISIBLE);
            String cover = VideoAndFileUtils.getCover(bestauth.getAuthpic());
            GlideUtils.loadImage(cover, bestAuth);
        } else {
            bestAuth.setVisibility(View.GONE);
        }
        bestAuth.setOnClickListener(v -> {
            if (bestauth != null && !TextUtils.isEmpty(bestauth.getAuthurl())) {
                WebActivity.openWeb("用户勋章", bestauth.getAuthurl(), true);
            }
        });

        //神评的点赞状态
        TextView splLike = helper.getView(R.id.base_moment_spl_like_iv);
        splLike.setSelected(LikeAndUnlikeUtil.isLiked(bestmap.getGoodstatus()));
        splLike.setText(Int2TextUtils.toText(bestmap.getCommentgood(), "W"));

        splLike.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                if (!splLike.isSelected()) {
                    LikeAndUnlikeUtil.showLike(splLike, 0, -10);
                }
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
                                if (goodCount < 0) {
                                    goodCount = 0;
                                }
                                //这里列表不需要改bean对象
                                splLike.setText(Int2TextUtils.toText(goodCount, "w"));
                                bestmap.setCommentgood(goodCount + "");

                            }
                        });
            }
        });

        FrameLayout splLayout = helper.getView(R.id.deal_with_rv);
        String commenturl = bestmap.getCommenturl();
        ArrayList<ImageData> commentShowList = VideoAndFileUtils.getDetailCommentShowList(commenturl);
        if (commentShowList == null || commentShowList.size() == 0) {
            splLayout.setVisibility(View.GONE);
        } else {
            splLayout.setVisibility(View.VISIBLE);
            ShowNineImage(helper.getView(R.id.best_one_image), helper.getView(R.id.detail_image),
                    commentShowList, contentid, bestmap);
        }
    }

    /**
     * 新加入方法,调整单图显示
     *
     * @param oneImage
     * @param imageData
     */
    private static void dealOneImageSize(ImageCell oneImage, ImageData imageData) {
        ViewGroup.LayoutParams layoutParams = oneImage.getLayoutParams();
        int fixedSize = DevicesUtils.dp2px(98);
        if (imageData.realHeight > 0 && imageData.realWidth > 0) {
            float whRatio = (imageData.realWidth + 0.0f) / imageData.realHeight;
            //修正宽高比
            if (whRatio < 0.5f) {
                whRatio = 0.5f;
            } else if (whRatio > 1.5f) {
                whRatio = 1.5f;
            }
            //宽大与高
            if (whRatio >= 1.0f) {
                layoutParams.height = fixedSize;
                layoutParams.width = (int) (fixedSize * whRatio);
            } else {
                layoutParams.width = fixedSize;
                layoutParams.height = (int) (fixedSize / whRatio);
            }
        } else {
            layoutParams.height = fixedSize;
            layoutParams.width = fixedSize;
        }
        oneImage.setLayoutParams(layoutParams);
    }

    public static void ShowNineImage(ImageCell oneImage, NineImageView multiImageView,
                                     ArrayList<ImageData> list, String contentid,
                                     MomentsDataBean.BestmapBean bestmap) {
        if (list.size() == 1) {
            oneImage.setVisibility(View.VISIBLE);
            multiImageView.setVisibility(View.GONE);
            WebShareBean shareBean = getVideoFullScreenShareBean(bestmap.getCommenturl(), bestmap.getCommentid());

            ImageData data = list.get(0);
            oneImage.setOnClickListener(v -> {
                String url = data.url;
                if (MediaFileUtils.getMimeFileIsVideo(url)) {
                    if (JzvdMgr.getCurrentJzvd() != null) {
                        Jzvd.releaseAllVideos();
                    }
                    //直接全屏
                    Jzvd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    JzvdStd.startFullscreen(oneImage.getContext()
                            , MyVideoPlayerStandard.class, url, shareBean);
                    UmengHelper.event(UmengStatisticsKeyIds.fullscreen);
                } else {
                    HelperForStartActivity.openImageWatcher(0, list,
                            contentid);
                }
            });
            oneImage.setData(data);
        } else {
            oneImage.setVisibility(View.GONE);
            multiImageView.setVisibility(View.VISIBLE);
            multiImageView.loadGif(false)
                    .setData(list, NineLayoutHelper.getInstance().getLayoutHelper(list));
            multiImageView.setClickable(true);
            multiImageView.setFocusable(true);
            multiImageView.setOnItemClickListener(position ->
                    HelperForStartActivity.openImageWatcher(position, list, contentid));
        }
    }

    public static WebShareBean getVideoFullScreenShareBean(String coverUrl, String commentId) {
        // TODO: 2019-04-24 拼成webshare对象
        //注意这里的导包用的是jzvd 里的bean
        WebShareBean hasBean = new WebShareBean();
        String contenttitle = MySpUtils.getMyName();
        if (!TextUtils.isEmpty(contenttitle) && contenttitle.length() > 8) {
            contenttitle = contenttitle.substring(0, 8);
        }
        contenttitle = "来自段友" + contenttitle + "的分享";

        hasBean.title = contenttitle;
        hasBean.content = BaseConfig.SHARE_CONTENT_TEXT;

        List<CommentUrlBean> commentUrlBean = VideoAndFileUtils.getCommentUrlBean(coverUrl);
        if (commentUrlBean != null && commentUrlBean.size() > 0) {
            hasBean.icon = commentUrlBean.get(0).cover;
        }
        //因为只有评论这块这么做,所以直接用评论链接
        hasBean.url = CommonHttpRequest.cmt_url;
        hasBean.contentId = commentId;
        hasBean.contentOrComment = 1;
        return hasBean;
    }


    public static void ShowNineImageByVideo(ImageCell oneImage, NineImageView multiImageView,
                                            ArrayList<ImageData> list, CommendItemBean.RowsBean item) {
        if (list.size() == 1) {
            oneImage.setVisibility(View.VISIBLE);
            multiImageView.setVisibility(View.GONE);
            ImageData data = list.get(0);
            WebShareBean shareBean = getVideoFullScreenShareBean(item.commenturl, item.commentid);
            oneImage.setOnClickListener(v -> {
                String url = data.url;
                if (MediaFileUtils.getMimeFileIsVideo(url)) {
                    if (JzvdMgr.getCurrentJzvd() != null) {
                        Jzvd.releaseAllVideos();
                    }
                    //直接全屏
                    Jzvd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    JzvdStd.startFullscreen(oneImage.getContext()
                            , MyVideoPlayerStandard.class, url, shareBean);
                    UmengHelper.event(UmengStatisticsKeyIds.fullscreen);
                } else {
                    HelperForStartActivity.openImageWatcher(0, list,
                            item.contentid);
                }
            });
            dealOneImageSize(oneImage, data);
            oneImage.setData(data);
        } else {
            oneImage.setVisibility(View.GONE);
            multiImageView.setVisibility(View.VISIBLE);
            multiImageView.loadGif(false)
                    .setData(list, NineLayoutHelper.getInstance().getLayoutHelper(list));
            multiImageView.setClickable(true);
            multiImageView.setFocusable(true);
            multiImageView.setOnItemClickListener(position ->
                    HelperForStartActivity.openImageWatcher(position, list, item.contentid));
        }
    }
}
