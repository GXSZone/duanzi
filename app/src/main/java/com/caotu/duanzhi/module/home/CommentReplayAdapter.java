package com.caotu.duanzhi.module.home;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
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
import com.caotu.duanzhi.Http.bean.AuthBean;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.FastClickListener;
import com.caotu.duanzhi.view.NineRvHelper;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;
import com.sunfusheng.widget.ImageData;

import java.util.ArrayList;

/**
 * 详情里的评论列表
 */

public class CommentReplayAdapter extends BaseQuickAdapter<CommendItemBean.RowsBean, BaseViewHolder> {

    private String parentName;

    public CommentReplayAdapter() {
        super(R.layout.item_replay_comment_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, CommendItemBean.RowsBean item) {
        // TODO: 2018/11/14 分享的弹窗由fragment来实现具体内容
        helper.addOnClickListener(R.id.base_moment_share_iv);

        ImageView avatar = helper.getView(R.id.comment_item_avatar);
        GlideUtils.loadImage(item.userheadphoto, avatar, false);
        helper.setText(R.id.comment_item_name_tx, item.username);

        ImageView bestAuth = helper.getView(R.id.user_auth);
        AuthBean authBean = item.getAuth();
        if (authBean != null && !TextUtils.isEmpty(authBean.getAuthid())) {
            bestAuth.setVisibility(View.VISIBLE);
//            Log.i("authPic", "convert: " + authBean.getAuthpic());
            String cover = VideoAndFileUtils.getCover(authBean.getAuthpic());
            GlideUtils.loadImage(cover, bestAuth);
        } else {
            bestAuth.setVisibility(View.GONE);
        }
        bestAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authBean != null && !TextUtils.isEmpty(authBean.getAuthurl())) {
                    WebActivity.openWeb("用户勋章", authBean.getAuthurl(), true);
                }
            }
        });
        helper.setOnClickListener(R.id.comment_item_name_tx, v -> HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, item.userid));
        avatar.setOnClickListener(v -> HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, item.userid));

        TextView mExpandTextView = helper.getView(R.id.expand_text_view);

        String commentContent = item.commenttext;
        // TODO: 2018/10/17 如果是自己恢复自己则是跟一级评论展示一样   如果评论列表里有楼主还是显示有回复的样式
        String ruusername = item.ruusername;
        if (!TextUtils.isEmpty(ruusername) && !TextUtils.isEmpty(item.ruuserid)
                && !ruusername.equals(parentName)) {
            commentContent = "回复 " + ruusername + ":" + commentContent;
            setTextClick(mExpandTextView, commentContent, 0, 3 + ruusername.length(), new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, item.ruuserid);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setUnderlineText(false);
                }
            });
        } else {
            mExpandTextView.setText(commentContent);
        }

        TextView likeIv = helper.getView(R.id.base_moment_spl_like_iv);
        likeIv.setText(Int2TextUtils.toText(item.commentgood, "W"));
        likeIv.setSelected(LikeAndUnlikeUtil.isLiked(item.goodstatus));
        likeIv.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestCommentsLike(item.userid,
                        item.contentid, item.commentid, likeIv.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                int goodCount = item.commentgood;
                                if (likeIv.isSelected()) {
                                    goodCount--;
                                    likeIv.setSelected(false);
                                } else {
                                    goodCount++;
                                    likeIv.setSelected(true);
                                }
                                if (goodCount > 0) {
                                    likeIv.setText(Int2TextUtils.toText(goodCount, "w"));
                                    item.commentgood = goodCount;
                                }
                                //"0"_未赞未踩 "1"_已赞 "2"_已踩
                                item.goodstatus = likeIv.isSelected() ? "1" : "0";
                            }
                        });
            }
        });

        dealNinelayout(helper, item);

    }

    public void dealNinelayout(BaseViewHolder helper, CommendItemBean.RowsBean item) {
        RecyclerView recyclerView = helper.getView(R.id.detail_image);
        ArrayList<ImageData> commentShowList = VideoAndFileUtils.getDetailCommentShowList(item.commenturl);
        if (commentShowList == null || commentShowList.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            return;
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }
        NineRvHelper.ShowNineImage(recyclerView, commentShowList, item.contentid);
    }

    public void setParentName(String username) {
        this.parentName = username;
    }


    public void setTextClick(TextView tv, String content, int start, int end, ClickableSpan clickableSpan) {
        final SpannableStringBuilder style = new SpannableStringBuilder();
        //设置文字
        style.append(content);
        style.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(style);
        //设置部分文字颜色
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(DevicesUtils.getColor(R.color.color_FF698F));
        style.setSpan(foregroundColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //配置给TextView
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setText(style);
    }
}
