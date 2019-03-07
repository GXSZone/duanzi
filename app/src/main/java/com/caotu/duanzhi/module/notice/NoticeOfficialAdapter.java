package com.caotu.duanzhi.module.notice;

import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DateUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sunfusheng.GlideImageView;

import java.util.Date;

public class NoticeOfficialAdapter extends BaseQuickAdapter<MessageDataBean.RowsBean, BaseViewHolder> {

    public NoticeOfficialAdapter() {
        super(R.layout.item_notice_official);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageDataBean.RowsBean item) {

        GlideImageView imageView = helper.getView(R.id.iv_notice_user);
        imageView.load(item.friendphoto, R.mipmap.touxiang_moren, 4);
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

        helper.setText(R.id.notice_text, item.notetext);
    }
}
