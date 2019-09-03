package com.caotu.duanzhi.module.notice;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DateUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.ruffian.library.widget.RImageView;
import com.sunfusheng.GlideImageView;

import java.util.Date;

/**
 * 这个类需要升级,有多条目类型
 */
public class NoticeOfficialAdapter extends BaseQuickAdapter<MessageDataBean.RowsBean, BaseViewHolder> {
    public static final int ITEM_JUST_TEXT = 1;
    public static final int ITEM_TEXT_AND_IMAGE = 2;

    public NoticeOfficialAdapter() {
        super(R.layout.item_notice_official);
        setMultiTypeDelegate(new MultiTypeDelegate<MessageDataBean.RowsBean>() {
            @Override
            protected int getItemType(MessageDataBean.RowsBean entity) {
                //根据你的实体类来判断布局类型
                if (entity.notetype.equals("6")) {
                    return ITEM_TEXT_AND_IMAGE;
                }
                return ITEM_JUST_TEXT;
            }
        });
        //Step.2
        getMultiTypeDelegate()
                .registerItemType(ITEM_JUST_TEXT, R.layout.item_notice_official)
                .registerItemType(ITEM_TEXT_AND_IMAGE, R.layout.item_notice_official_other);

    }

    @Override
    protected void convert(BaseViewHolder helper, MessageDataBean.RowsBean item) {

        RImageView imageView = helper.getView(R.id.iv_notice_user);
        GlideUtils.loadImage(item.friendphoto, imageView, false);
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
        if (helper.getItemViewType() != ITEM_TEXT_AND_IMAGE) return;
        setDataLL(helper, item);
    }

    /**
     * 下面条目的展示问题也是操蛋逻辑,这里只有跳转内容详情,因为是点上热门才会在这里展示
     *
     * @param helper
     * @param item
     */
    private void setDataLL(BaseViewHolder helper, MessageDataBean.RowsBean item) {
        TextView content = helper.getView(R.id.comment_item_second_comment_tv);
        GlideImageView image = helper.getView(R.id.iv_comment_item_second);
        if (TextUtils.equals("1", item.contentstatus)) {
            image.setVisibility(View.VISIBLE);
            image.setImageResource(R.mipmap.deletestyle2);

            content.setVisibility(View.VISIBLE);
            content.setText("该内容已被删除");
            return;
        }


    }
}
