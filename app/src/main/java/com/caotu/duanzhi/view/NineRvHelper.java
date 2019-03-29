package com.caotu.duanzhi.view;

import android.content.pm.ActivityInfo;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NineLayoutHelper;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.widget.MyExpandTextView;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.sunfusheng.util.MediaFileUtils;
import com.sunfusheng.widget.ImageCell;
import com.sunfusheng.widget.ImageData;
import com.sunfusheng.widget.NineImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

/**
 * 评论列表的九宫格布局帮助类
 */
public class NineRvHelper {

    /**
     * 处理显示内容
     * https://github.com/binaryfork/Spanny 处理span的三方
     *
     * @param contentView
     * @param tagshow
     * @param contenttext
     * @param ishowTag
     * @param tagshowid
     */
    public static void setContentText(MyExpandTextView contentView, String tagshow, String contenttext,
                                      boolean ishowTag, String tagshowid, MomentsDataBean dataBean) {
        if (!TextUtils.isEmpty(tagshow)) {
            String source = "#" + tagshow + "#";
            if (ishowTag) {
                source = source + contenttext;
            }
            SpannableString ss = new SpannableString(source);
            ss.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // TODO: 2018/11/8 话题详情
                    MyApplication.getInstance().putHistory(dataBean.getContentid());
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
            contentView.setVisibility(View.VISIBLE);

        } else {
            if (ishowTag) {
                contentView.setVisibility(View.VISIBLE);
                contentView.setText(contenttext);
            } else {
                // TODO: 2018/12/24 间距可能需要重新调 
                contentView.setVisibility(View.GONE);
            }
        }

        if (dataBean != null) {
            contentView.clickCount(dataBean.getContentid());
        }
    }

    public static void bindItemHeader(ImageView userPhoto, ImageView userAuth, TextView userName,
                                      MomentsDataBean dataBean) {
        GlideUtils.loadImage(dataBean.getUserheadphoto(), userPhoto, true);
        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dataBean.getContentuid().equals(MySpUtils.getString(MySpUtils.SP_MY_ID))) {
                    // TODO: 2019/1/15 添加历史记录统计
                    MyApplication.getInstance().putHistory(dataBean.getContentid());
                    HelperForStartActivity.openOther(HelperForStartActivity.type_other_user,
                            dataBean.getContentuid());
                }
            }
        });
        AuthBean authBean = dataBean.getAuth();
        if (authBean != null && !TextUtils.isEmpty(authBean.getAuthid())) {
            Log.i("authPic", "convert: " + authBean.getAuthpic());
            userAuth.setVisibility(View.VISIBLE);
            String cover = VideoAndFileUtils.getCover(authBean.getAuthpic());
            GlideUtils.loadImage(cover, userAuth);
        } else {
            userAuth.setVisibility(View.GONE);
        }
        userAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authBean != null && !TextUtils.isEmpty(authBean.getAuthurl())) {
                    WebActivity.openWeb("用户勋章", authBean.getAuthurl(), true);
                }
            }
        });
        userName.setText(dataBean.getUsername());
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2018/11/8 如果是自己则不跳转
                if (!dataBean.getContentuid().equals(MySpUtils.getString(MySpUtils.SP_MY_ID))) {
                    HelperForStartActivity.openOther(HelperForStartActivity.type_other_user,
                            dataBean.getContentuid());
                }
            }
        });
    }


    /**
     * 处理神评展示
     *
     * @param helper
     * @param bestmap
     * @param bestauth
     * @param contentid
     */
    public static void dealBest(BaseViewHolder helper, MomentsDataBean.BestmapBean bestmap, AuthBean bestauth, String contentid) {
        //统一处理神评的空白区域点击跳转
        helper.setOnClickListener(R.id.rl_best_parent, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(bestmap.getCommentid())) return;
                HashMap<String, String> params = new HashMap<>();
                params.put("cmtid", bestmap.getCommentid());
                OkGo.<BaseResponseBean<CommendItemBean.RowsBean>>post(HttpApi.COMMENT_DEATIL)
                        .upJson(new JSONObject(params))
                        .execute(new JsonCallback<BaseResponseBean<CommendItemBean.RowsBean>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<CommendItemBean.RowsBean>> response) {
                                CommendItemBean.RowsBean data = response.body().getData();
                                HelperForStartActivity.openCommentDetail(data);
                            }
                        });
            }
        });

        GlideUtils.loadImage(bestmap.getUserheadphoto(), helper.getView(R.id.iv_best_avatar));
        helper.setText(R.id.tv_spl_name, bestmap.getUsername());
        helper.setGone(R.id.base_moment_spl_comment_tv, !TextUtils.isEmpty(bestmap.getCommenttext()));
        helper.setText(R.id.base_moment_spl_comment_tv, bestmap.getCommenttext());
        helper.setOnClickListener(R.id.iv_best_avatar, v -> {
            // TODO: 2018/11/8 如果是自己则不跳转
            if (!bestmap.getUserid().equals(MySpUtils.getString(MySpUtils.SP_MY_ID))) {
                MyApplication.getInstance().putHistory(contentid);
                HelperForStartActivity.openOther(HelperForStartActivity.type_other_user,
                        bestmap.getUserid());
            }
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
            ShowNineImage(false, R.id.best_one_image, R.id.detail_image, helper, commentShowList, contentid);
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

    public static void ShowNineImage(boolean needOneImageSize, int oneId, int moreId, BaseViewHolder helper, ArrayList<ImageData> list, String contentid) {
        ImageCell oneImage = helper.getView(oneId);
        NineImageView multiImageView = helper.getView(moreId);
        if (list.size() == 1) {
            oneImage.setVisibility(View.VISIBLE);
            multiImageView.setVisibility(View.GONE);
            ImageData data = list.get(0);
            oneImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = data.url;
                    if (MediaFileUtils.getMimeFileIsVideo(url)) {
                        Jzvd.releaseAllVideos();
                        //直接全屏
                        Jzvd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                        JzvdStd.startFullscreen(oneImage.getContext()
                                , MyVideoPlayerStandard.class, url, "");
                    } else {
                        HelperForStartActivity.openImageWatcher(0, list,
                                contentid);
                    }
                }
            });
            if (needOneImageSize) {
                dealOneImageSize(oneImage, data);
            }
            oneImage.setData(data);
        } else {
            oneImage.setVisibility(View.GONE);
            multiImageView.setVisibility(View.VISIBLE);
            multiImageView.post(new Runnable() {
                @Override
                public void run() {
                    multiImageView.loadGif(false)
                            .setData(list, NineLayoutHelper.getInstance().getLayoutHelper(list));
                    multiImageView.setClickable(true);
                    multiImageView.setFocusable(true);
                    multiImageView.setOnItemClickListener(position ->
                            HelperForStartActivity.openImageWatcher(position, list, contentid));
                }
            });
        }
    }


    public static void dealLikeAndUnlike(BaseViewHolder helper, MomentsDataBean item) {
        /*-------------------------------点赞和踩的处理---------------------------------*/
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
        likeView.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                if (!likeView.isSelected()) {
                    LikeAndUnlikeUtil.showLike(likeView, 0, 20);
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

        /*-------------------------------点赞和踩的处理结束---------------------------------*/
    }
}
