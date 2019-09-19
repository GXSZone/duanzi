package com.caotu.duanzhi.module.notice;

import android.text.TextUtils;

import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DateUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.ParserUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ruffian.library.widget.RImageView;

import java.util.Date;

public class NoticeAdapter extends BaseQuickAdapter<MessageDataBean.RowsBean, BaseViewHolder> {


    public NoticeAdapter() {
        super(R.layout.item_notice_home);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageDataBean.RowsBean item) {
        RImageView imageView = helper.getView(R.id.iv_notice_user);
        GlideUtils.loadImage(item.friendphoto, imageView,false);
        helper.addOnClickListener(R.id.iv_notice_user);

        String timeText = "";
        try {
            Date start = DateUtils.getDate(item.createtime, DateUtils.YMDHMS);
            timeText = DateUtils.showTimeText(start);
        } catch (Exception e) {
            e.printStackTrace();
        }

        helper.setText(R.id.notice_time, timeText);
        helper.setText(R.id.tv_item_user, item.friendname);
        helper.setText(R.id.notice_text, ParserUtils.htmlToJustAtText(item.notetext));

        helper.setGone(R.id.red_point_tip, TextUtils.equals("0", item.readflag));
    }
}
