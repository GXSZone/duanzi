package com.caotu.duanzhi.module.detail;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.ParserUtils;
import com.caotu.duanzhi.view.fixTextClick.CustomMovementMethod;
import com.caotu.duanzhi.view.fixTextClick.SimpeClickSpan;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * 评论详情里的评论列表
 */

public class CommentReplayAdapter extends DetailCommentAdapter {
    private String parentName;

    public CommentReplayAdapter(TextViewLongClick textViewLongClick) {
        super(textViewLongClick);
    }

    @Override
    protected void dealReplyUI(List<CommendItemBean.ChildListBean> childList, BaseViewHolder helper, int replyCount, CommendItemBean.RowsBean item) {
        helper.setGone(R.id.child_reply_layout, false);
    }

    @Override
    public void dealText(CommendItemBean.RowsBean item, TextView mExpandTextView) {
        SpannableStringBuilder builder2 = new SpannableStringBuilder();
        // TODO: 2018/10/17 如果是自己恢复自己则是跟一级评论展示一样   如果评论列表里有楼主还是显示有回复的样式
        String ruusername = item.ruusername;
        if (!TextUtils.isEmpty(ruusername) && !TextUtils.isEmpty(item.ruuserid)
                && !ruusername.equals(parentName)) {
            builder2.append("回复 " + ruusername + ":", new SimpeClickSpan() {
                @Override
                public void onSpanClick(View widget) {
                    HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, item.ruuserid);
                }
            }, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        SpannableStringBuilder spanText = ParserUtils.htmlToSpanText(item.commenttext, true);
        builder2.append(spanText);
        mExpandTextView.setText(builder2);
        mExpandTextView.setMovementMethod(CustomMovementMethod.getInstance());
        mExpandTextView.setVisibility(TextUtils.isEmpty(builder2) ? View.GONE : View.VISIBLE);
    }

    public void setParentName(String username) {
        this.parentName = username;
    }
}
