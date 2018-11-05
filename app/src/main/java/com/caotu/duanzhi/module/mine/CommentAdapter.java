package com.caotu.duanzhi.module.mine;

import com.caotu.duanzhi.Http.bean.CommentBaseBean;
import com.caotu.duanzhi.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sunfusheng.GlideImageView;

/**
 * @author mac
 * @日期: 2018/11/5
 * @describe TODO
 */
public class CommentAdapter extends BaseQuickAdapter<CommentBaseBean.RowsBean, BaseViewHolder> {

    public CommentAdapter() {
        super(R.layout.item_comment_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, CommentBaseBean.RowsBean item) {
        GlideImageView avatar = helper.getView(R.id.comment_item_avatar);
        avatar.load(item.getContent().getUserheadphoto(), R.mipmap.touxiang_moren, 4);

        GlideImageView SecondCommentTv = helper.getView(R.id.iv_comment_item_second);
        SecondCommentTv.load(item.getContent().getUserheadphoto(), R.mipmap.touxiang_moren, 4);

        helper.setText(R.id.comment_item_name_tx, item.getUsername())
                .setText(R.id.comment_item_content_tv, item.getCommenttext())
                .setText(R.id.comment_item_second_comment_tv, item.getContent().getContenttitle());
    }
}
