package com.caotu.duanzhi.module.notice;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.ParserUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ruffian.library.widget.RImageView;
import com.sunfusheng.GlideImageView;

public class NoticeAdapter extends BaseQuickAdapter<MessageDataBean.RowsBean, BaseViewHolder> {


    public NoticeAdapter() {
        super(R.layout.item_notice_home);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageDataBean.RowsBean item) {
        RImageView imageView = helper.getView(R.id.iv_notice_user);
        GlideUtils.loadImage(item.friendphoto, imageView, false);
        helper.addOnClickListener(R.id.iv_notice_user);
        GlideImageView imageView1 = helper.getView(R.id.iv_user_auth);
        if (TextUtils.isEmpty(item.authPic)) {
            imageView1.setVisibility(View.GONE);
        } else {
            imageView1.setVisibility(View.VISIBLE);
            imageView1.load(item.authPic);
        }

        helper.setText(R.id.notice_time, item.timeText);


        TextView view = helper.getView(R.id.tv_item_user);
        view.setText(item.friendname);
        view.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗

        helper.setText(R.id.notice_text, ParserUtils.htmlToJustAtText(item.notetext));
        helper.setGone(R.id.red_point_tip, TextUtils.equals("0", item.readflag));
    }
}
