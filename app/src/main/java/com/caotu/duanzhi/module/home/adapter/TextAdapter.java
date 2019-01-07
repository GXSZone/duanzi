package com.caotu.duanzhi.module.home.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.home.fragment.CallBackTextClick;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.view.NineRvHelper;
import com.caotu.duanzhi.view.widget.MyExpandTextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sunfusheng.GlideImageView;

/**
 * 内容展示列表,话题详情下的话题标签都不展示
 */

public class TextAdapter extends BaseQuickAdapter<MomentsDataBean, BaseViewHolder> {

    public TextAdapter() {
        super(R.layout.item_just_text);
    }

    /**
     * 文本的点击事件回调给fragment统一处理
     */
    public CallBackTextClick textClick;

    public void setTextClick(CallBackTextClick textClick) {
        this.textClick = textClick;
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
        NineRvHelper.dealLikeAndUnlike(helper, item);

        GlideImageView guanjian = helper.getView(R.id.iv_user_headgear);
        guanjian.load(item.getGuajianurl());

        ImageView avatar = helper.getView(R.id.base_moment_avatar_iv);
        ImageView auth = helper.getView(R.id.user_auth);
        TextView userName = helper.getView(R.id.base_moment_name_tv);
        NineRvHelper.bindItemHeader(avatar, auth, userName, item);

        MyExpandTextView contentView = helper.getView(R.id.layout_expand_text_view);
        //判断是否显示话题 1可见，0不可见
        String tagshow = item.getTagshow();
        NineRvHelper.setContentText(contentView, tagshow, item.getContenttitle(),
                "1".equals(item.getIsshowtitle()), item.getTagshowid(), item);

        contentView.setTextListener(new MyExpandTextView.ClickTextListener() {
            @Override
            public void clickText(View textView) {
                if (textClick != null) {
                    textClick.textClick(item, getPositon(helper));
                }
            }
        });
        MomentsDataBean.BestmapBean bestmap = item.getBestmap();
        GlideImageView bestGunajian = helper.getView(R.id.iv_best_user_headgear);
        bestGunajian.load(item.getBestguajian());
        if (bestmap != null && bestmap.getCommentid() != null) {
            helper.setGone(R.id.rl_best_parent, true);
            NineRvHelper.dealBest(helper, bestmap,item.getBestauth(), item.getContentid());
        } else {
            helper.setGone(R.id.rl_best_parent, false);
        }

    }

    private int getPositon(BaseViewHolder helper) {
        if (helper.getLayoutPosition() >= getHeaderLayoutCount()) {
            return helper.getLayoutPosition() - getHeaderLayoutCount();
        }
        return 0;
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
}
