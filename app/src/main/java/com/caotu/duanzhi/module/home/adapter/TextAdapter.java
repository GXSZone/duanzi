package com.caotu.duanzhi.module.home.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.FastClickListener;
import com.caotu.duanzhi.view.NineRvHelper;
import com.caotu.duanzhi.view.widget.MyExpandTextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;
import com.sunfusheng.widget.ImageData;

import java.util.ArrayList;

/**
 * 内容展示列表,话题详情下的话题标签都不展示
 */

public class TextAdapter extends BaseQuickAdapter<MomentsDataBean, BaseViewHolder> {

    public TextAdapter() {
        super(R.layout.item_just_text);
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

        GlideUtils.loadImage(item.getUserheadphoto(), helper.getView(R.id.base_moment_avatar_iv), true);

        helper.setText(R.id.base_moment_name_tv, item.getUsername());

        MyExpandTextView contentView = helper.getView(R.id.layout_expand_text_view);
        //判断是否显示话题 1可见，0不可见
        String tagshow = item.getTagshow();
        NineRvHelper.setContentText(contentView, tagshow, item.getContenttitle(),
                "1".equals(item.getIsshowtitle()), item.getTagshowid(), item);

        MomentsDataBean.BestmapBean bestmap = item.getBestmap();
        if (bestmap != null && bestmap.getCommentid() != null) {
            helper.setGone(R.id.rl_best_parent, true);
            dealBest(helper, bestmap, item.getContentid());
        } else {
            helper.setGone(R.id.rl_best_parent, false);
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
}
