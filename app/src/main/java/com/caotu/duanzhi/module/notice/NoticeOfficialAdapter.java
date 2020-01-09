package com.caotu.duanzhi.module.notice;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.ParserUtils;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.other.CustomMovementMethod;
import com.caotu.duanzhi.view.widget.AvatarWithNameLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.sunfusheng.GlideImageView;

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
                if (!TextUtils.isEmpty(entity.contentid)) {
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
    protected void convert(@NonNull BaseViewHolder helper, MessageDataBean.RowsBean item) {

        AvatarWithNameLayout nameLayout = helper.getView(R.id.group_user_avatar);
        nameLayout.setUserText(item.friendname, item.timeText);
        nameLayout.load(item.friendphoto, item.guajianurl, item.authPic);
        helper.addOnClickListener(R.id.group_user_avatar);

        TextView noticeText = helper.getView(R.id.notice_text);
        noticeText.setText(ParserUtils.htmlToSpanText(item.notetext, true));
        noticeText.setMovementMethod(CustomMovementMethod.getInstance());

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
            content.setText("该内容已被删除");
            return;
        }
        if (item.content == null) return;
        String cover = VideoAndFileUtils.getCover(item.content.getContenturllist());
        if (TextUtils.isEmpty(cover)) {
            image.setVisibility(View.GONE);
        } else {
            image.setVisibility(View.VISIBLE);
            image.load(cover, R.mipmap.shenlue_logo, 4);
        }

        String contenttitle = item.content.getContenttitle();
        if (!TextUtils.isEmpty(contenttitle)) {
            if (!"1".equals(item.content.getIsshowtitle())) {
                content.setVisibility(View.INVISIBLE);
            } else {
                content.setVisibility(View.VISIBLE);
                content.setText(ParserUtils.htmlToJustAtText(contenttitle));
            }
        } else {
            content.setText(String.format("%s的作品", item.content.getUsername()));
        }
    }
}
