package com.caotu.duanzhi.module.home;

import android.view.LayoutInflater;
import android.view.View;

import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.other.ShareHelper;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * @author mac
 * @日期: 2018/11/20
 * @describe 需要处理头布局的差异和评论列表的的点击事件等
 */
public class UgcContentFragment extends ContentDetailFragment {

    @Override
    protected BaseQuickAdapter getAdapter() {
        if (commentAdapter == null) {
            commentAdapter = new DetailCommentAdapter() {
                @Override
                protected void dealReplyUI(List<CommendItemBean.ChildListBean> childList, BaseViewHolder helper, int replyCount) {
                    helper.setGone(R.id.child_reply_layout, false);
                }
            };
            commentAdapter.setOnItemChildClickListener(this);
            commentAdapter.setOnItemClickListener(this);
        }
        return commentAdapter;
    }

    @Override
    protected void initHeader() {
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.layout_comment_detail_header, mRvContent, false);
        initHeaderView(headerView);
        //设置头布局
        adapter.setHeaderView(headerView);
        adapter.setHeaderAndEmpty(true);
        bindHeader(content);
    }

    @Override
    protected void dealList(List<CommendItemBean.RowsBean> bestlist, List<CommendItemBean.RowsBean> rows, MomentsDataBean ugc, int load_more) {
        //这里只处理初始化和刷新,加载更多直接忽略神评和ugc
        if (rows != null && bestlist != null && bestlist.size() > 0) {
            rows.addAll(bestlist);
        }
        setDate(load_more, rows);
    }


    @Override
    public void initHeaderView(View view) {
        if (viewHolder == null) {
            viewHolder = new UgcHeaderHolder(this, view);
            viewHolder.setCallBack(new IHolder.ShareCallBack() {
                @Override
                public void share(MomentsDataBean bean) {
                    WebShareBean webBean = ShareHelper.getInstance().createWebBean(viewHolder.isVideo(), true
                            , content.getIscollection(), viewHolder.getVideoUrl(), bean.getContentid());
                    showShareDailog(webBean, mShareUrl);
                }
            });
        }
    }

    /**
     * 好好的一个类因为插了UGC 内容的进来就狗屎了,明明是评论列表还有内容的东西
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        CommendItemBean.RowsBean bean = (CommendItemBean.RowsBean) adapter.getData().get(position);
        if (view.getId() == R.id.base_moment_share_iv) {
            WebShareBean webBean = ShareHelper.getInstance().createWebBean(false, false
                    , null, null, bean.contentid);
            showShareDailog(webBean, mShareUrl);
        }
    }

    /**
     * 在UGC的详情里没有点击事件
     *
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }


    public void publishComment(CommendItemBean.RowsBean bean) {
        if (viewHolder != null) {
            viewHolder.commentPlus();
        }

        if (commentAdapter.getData().size() == 0) {
//            commentAdapter.getData().add(bean);
//            commentAdapter.notifyDataSetChanged();
            commentAdapter.setEnableLoadMore(false);
//            commentAdapter.addData(bean);
        }
        commentAdapter.addData(0, bean);

    }
}
