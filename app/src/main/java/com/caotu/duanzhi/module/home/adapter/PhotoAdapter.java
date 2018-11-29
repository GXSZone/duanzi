package com.caotu.duanzhi.module.home.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NineLayoutHelper;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.FastClickListener;
import com.caotu.duanzhi.view.NineRvHelper;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;
import com.sunfusheng.widget.ImageData;
import com.sunfusheng.widget.NineImageView;

import java.util.ArrayList;

/**
 * 内容展示列表,话题详情下的话题标签都不展示
 */

public class PhotoAdapter extends BaseQuickAdapter<MomentsDataBean, BaseViewHolder> {

    public PhotoAdapter() {
        super(R.layout.item_base_content);
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

        GlideUtils.loadImage(item.getUserheadphoto(), helper.getView(R.id.base_moment_avatar_iv));

        helper.setText(R.id.base_moment_name_tv, item.getUsername());

        TextView contentView = helper.getView(R.id.expand_text_view);
        //判断是否显示话题 1可见，0不可见
        String tagshow = item.getTagshow();
        setContentText(contentView, tagshow, item.getContenttitle(),
                "1".equals(item.getIsshowtitle()), item.getTagshowid());

        MomentsDataBean.BestmapBean bestmap = item.getBestmap();
        if (bestmap != null && bestmap.getCommentid() != null) {
            helper.setGone(R.id.rl_best_parent, true);
            dealBest(helper, bestmap, item.getContentid());
        } else {
            helper.setGone(R.id.rl_best_parent, false);
        }
        //处理九宫格
        dealNineLayout(item, helper);
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


    private void dealNineLayout(MomentsDataBean item, BaseViewHolder helper) {
        //神评区的显示隐藏在上面判断

        String contenturllist = item.getContenturllist();
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
                        item.getContentid());
            }
        });
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


        String commenturl = bestmap.getCommenturl();
        if (TextUtils.isEmpty(commenturl) || "[]".equals(commenturl)) return;

        ArrayList<ImageData> commentShowList = VideoAndFileUtils.getDetailCommentShowList(commenturl);
        if (commentShowList == null || commentShowList.size() == 0) return;

        RecyclerView recyclerView = helper.getView(R.id.deal_with_rv);
        NineRvHelper.ShowNineImage(recyclerView, commentShowList, contentid);

    }

    /**
     * 处理显示内容
     *
     * @param contentView
     * @param tagshow
     * @param contenttext
     * @param ishowTag
     * @param tagshowid
     */
    public void setContentText(TextView contentView, String tagshow, String contenttext,
                               boolean ishowTag, String tagshowid) {
        Log.i("qlwadapter", "content: " + contenttext + "-----------ishowtag:" + ishowTag + " ---------------tag:" + tagshow);
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
            contentView.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            if (ishowTag) {
                contentView.setVisibility(View.VISIBLE);
                contentView.setText(contenttext);
            } else {

                contentView.setText("  fasd  ");
                contentView.setVisibility(View.INVISIBLE);
            }
        }
/*
另外一种解决不会显示...的问题,自定义textview
 * 注意：spannableString 設置Spannable 的對象到spannableString中時，要用Spannable.SPAN_EXCLUSIVE_EXCLUSIVE的flag值，不然可能會會出現後面的銜接字符串不會顯示

        @Override
        protected void onDraw(Canvas canvas) {
            CharSequence charSequence = getText() ;
            int lastCharDown = getLayout().getLineVisibleEnd(0) ;
            if (charSequence.length() > lastCharDown){
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder() ;
                spannableStringBuilder.append(charSequence.subSequence(0,lastCharDown-4)).append("...") ;
                setText(spannableStringBuilder);
            }
            super.onDraw(canvas);
        }
 */
        ViewTreeObserver viewTreeObserver = contentView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver viewTreeObserver = contentView.getViewTreeObserver();
                viewTreeObserver.removeOnGlobalLayoutListener(this);

                if (contentView.getLineCount() > 6) {
                    int endOfLastLine = contentView.getLayout().getLineEnd(5);
                    String newVal = contentView.getText().subSequence(0, endOfLastLine - 3) + "...";
                    contentView.setText(newVal);
                }
            }
        });
    }
}
