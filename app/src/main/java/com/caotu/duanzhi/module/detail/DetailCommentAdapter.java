package com.caotu.duanzhi.module.detail;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.caotu.adlib.AdHelper;
import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.AuthBean;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DateUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.ParserUtils;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.FastClickListener;
import com.caotu.duanzhi.view.NineRvHelper;
import com.caotu.duanzhi.view.fixTextClick.CustomMovementMethod;
import com.caotu.duanzhi.view.fixTextClick.SimpeClickSpan;
import com.caotu.duanzhi.view.widget.AvatarWithNameLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.lzy.okgo.model.Response;
import com.sunfusheng.widget.ImageCell;
import com.sunfusheng.widget.ImageData;
import com.sunfusheng.widget.NineImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 详情里的评论列表
 */

public class DetailCommentAdapter extends BaseQuickAdapter<CommendItemBean.RowsBean, BaseViewHolder> {


    public DetailCommentAdapter() {
        super(R.layout.item_datail_comment_layout);
        setMultiTypeDelegate(new MultiTypeDelegate<CommendItemBean.RowsBean>() {
            @Override
            protected int getItemType(CommendItemBean.RowsBean entity) {
                if (TextUtils.equals("6", entity.commenttype)) {
                    return 111;
                } else {
                    return 222;
                }
            }
        });
        //Step.2
        getMultiTypeDelegate()
                .registerItemType(111, R.layout.comment_ad_type_content)
                .registerItemType(222, R.layout.item_datail_comment_layout);
    }

    protected void dealItemAdType(@NonNull BaseViewHolder helper, CommendItemBean.RowsBean item) {
        ViewGroup adContainer = helper.getView(R.id.item_comment_ad);
        ViewGroup viewGroup = (ViewGroup) adContainer.getParent();
        ViewGroup.LayoutParams params = viewGroup.getLayoutParams();
        if (item.adView == null) {
            params.height = 0;
            viewGroup.setLayoutParams(params);
            return;
        }
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        viewGroup.setLayoutParams(params);
        ImageView imageView = helper.getView(R.id.iv_item_close);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = helper.getLayoutPosition();
                position -= getHeaderLayoutCount();
                remove(position);
            }
        });
        AdHelper.getInstance().showAD(item.adView,adContainer);

    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, CommendItemBean.RowsBean item) {
        if (TextUtils.equals("6", item.commenttype)) {
            dealItemAdType(helper, item);
            return;
        }
        dealUserHeader(helper, item);

        // TODO: 2018/11/29  神评的标志显示 因为有头布局
        helper.setGone(R.id.iv_god_bg, item.isBest);
        TextView mExpandTextView = helper.getView(R.id.expand_text_view);
        dealText(item, mExpandTextView);
        helper.addOnClickListener(R.id.base_moment_share_iv, R.id.group_user_avatar);

        TextView likeIv = helper.getView(R.id.base_moment_spl_like_iv);
        likeIv.setText(Int2TextUtils.toText(item.commentgood, "w"));
        likeIv.setSelected(LikeAndUnlikeUtil.isLiked(item.goodstatus));
        likeIv.setTag(UmengStatisticsKeyIds.comment_like); //为了埋点
        likeIv.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                // TODO: 2019-06-20 麻烦,省去之前还有ugc的判断
                CommonHttpRequest.getInstance().requestCommentsLike(item.userid,
                        item.contentid, item.commentid, likeIv.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                commentLikeClick(item, likeIv);
                            }
                        });
            }
        });

        //这个是回复的显示内容
        dealReplyUI(item.childList, helper, item.replyCount, item);
        dealNinelayout(helper, item);
    }

    private void dealUserHeader(@NonNull BaseViewHolder helper, CommendItemBean.RowsBean item) {
        //头像和名字显示逻辑
        AvatarWithNameLayout nameLayout = helper.getView(R.id.group_user_avatar);
        String userName;
        AuthBean authBean;
        String userPhoto;
        String userHead;
        String authText = null;
        String authPic = null;

        authBean = item.getAuth();
        userName = item.username;
        userPhoto = item.userheadphoto;
        userHead = item.getGuajianurl();
        try {
            Date start = DateUtils.getDate(item.createtime, DateUtils.YMDHMS);
            authText = DateUtils.showTimeComment(start);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (authBean != null) {
            authPic = VideoAndFileUtils.getCover(authBean.getAuthpic());

        }

        if (!TextUtils.isEmpty(item.authname)) {
            authText = authText + "·" + item.authname;
        }
        nameLayout.setUserText(userName, authText);
        nameLayout.load(userPhoto, userHead, authPic);
    }

    public void dealText(CommendItemBean.RowsBean item, TextView mExpandTextView) {
        //changeUgcBean bean对象转换
        if (item.isUgc && item.isShowTitle) {
            mExpandTextView.setVisibility(View.GONE);
        } else {
            mExpandTextView.setVisibility(TextUtils.isEmpty(item.commenttext) ? View.GONE : View.VISIBLE);
        }

        mExpandTextView.setText(ParserUtils.htmlToSpanText(item.commenttext, true));
        mExpandTextView.setMovementMethod(CustomMovementMethod.getInstance());
    }

    public void dealNinelayout(BaseViewHolder helper, CommendItemBean.RowsBean item) {
        FrameLayout parentView = helper.getView(R.id.fl_image_video);
        NineImageView nineImageView = helper.getView(R.id.detail_image);
        ImageCell oneImage = helper.getView(R.id.only_one_image);
        ArrayList<ImageData> commentShowList = VideoAndFileUtils.getDetailCommentShowList(item.commenturl);
        if (commentShowList == null || commentShowList.size() == 0) {
            parentView.setVisibility(View.GONE);
        } else {
            parentView.setVisibility(View.VISIBLE);
            NineRvHelper.ShowNineImageByVideo(oneImage, nineImageView, commentShowList, item);
        }
    }


    /**
     * 处理评论列表的点赞数显示
     *
     * @param item
     * @param likeIv
     */
    public void commentLikeClick(CommendItemBean.RowsBean item, TextView likeIv) {
        if (!likeIv.isSelected()) {
            LikeAndUnlikeUtil.showLike(likeIv, 0, 0);
        }
        int goodCount = item.commentgood;
        if (likeIv.isSelected()) {
            goodCount--;
            likeIv.setSelected(false);
        } else {
            goodCount++;
            likeIv.setSelected(true);
        }
        //修正
        if (goodCount < 0) {
            goodCount = 0;
        }
        likeIv.setText(Int2TextUtils.toText(goodCount, "w"));
        item.commentgood = goodCount;

        //"0"_未赞未踩 "1"_已赞 "2"_已踩
        item.goodstatus = likeIv.isSelected() ? "1" : "0";
    }

    protected void dealReplyUI(List<CommendItemBean.ChildListBean> childList, BaseViewHolder helper, int replyCount, CommendItemBean.RowsBean item) {
        helper.addOnClickListener(R.id.child_reply_layout);
        TextView more = helper.getView(R.id.comment_reply_more);
        TextView first = helper.getView(R.id.comment_item_first);
        TextView second = helper.getView(R.id.comment_item_second);
        //先判断是否是ugc,过滤没有神评的情况
        if (item.isUgc && (childList == null || childList.size() == 0)) {
            more.setVisibility(replyCount >= 2 ? View.VISIBLE : View.GONE);
            more.setText(String.format(Locale.CHINA, "共有%d条回复 \uD83D\uDC49", replyCount));
            first.setVisibility(View.GONE);
            second.setVisibility(View.GONE);
            if (replyCount < 2) {
                helper.setGone(R.id.child_reply_layout, false);
            }
            return;
        }
        if (childList == null || childList.size() == 0) {
            helper.setGone(R.id.child_reply_layout, false);
            return;
        }
        helper.setGone(R.id.child_reply_layout, true);

        int size = childList.size();
        if (item.isUgc) {
            more.setVisibility(replyCount >= 2 ? View.VISIBLE : View.GONE);
            more.setText(String.format("共有%d条回复 \uD83D\uDC49", replyCount));
        } else {
            more.setVisibility(size >= 2 ? View.VISIBLE : View.GONE);
            more.setText(String.format("共有%d条回复 \uD83D\uDC49", replyCount));
        }
        if (size == 1) {
            first.setVisibility(View.VISIBLE);
            second.setVisibility(View.GONE);
            setText(first, childList.get(0), item);
        } else if (size >= 2) {
            first.setVisibility(View.VISIBLE);
            second.setVisibility(View.VISIBLE);
            setText(first, childList.get(0), item);
            setText(second, childList.get(1), item);
        }
    }

    private void setText(TextView textView, CommendItemBean.ChildListBean childListBean, CommendItemBean.RowsBean item) {
        String username = childListBean.username;
        String commenturl = childListBean.commenturl;
        //这里不需要标红,也不加点击事件
        String commenttext = ParserUtils.htmlToJustAtText(childListBean.commenttext);
        List<CommentUrlBean> commentUrlBean = VideoAndFileUtils.getCommentUrlBean(commenturl);
        String type = "";
        if (commentUrlBean != null && commentUrlBean.size() > 0) {
            if (TextUtils.equals(commentUrlBean.get(0).type, "1")
                    || TextUtils.equals(commentUrlBean.get(0).type, "2")) {
                type = "[视频]";
            } else {
                type = "[图片]";
            }
        }
        String source = username + ": " + type + commenttext;
        SpannableString ss = new SpannableString(source);
        int length = TextUtils.isEmpty(username) ? 0 : username.length();
        if (length > 0) {
            ss.setSpan(new SimpeClickSpan() {
                @Override
                public void onSpanClick(View widget) {
                    HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, childListBean.userid);
                }
            }, 0, length + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setText(ss);
        textView.setMovementMethod(CustomMovementMethod.getInstance());
    }

}
