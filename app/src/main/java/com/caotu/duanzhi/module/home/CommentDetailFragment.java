package com.caotu.duanzhi.module.home;

import android.view.LayoutInflater;
import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.ShareUrlBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.other.HandleBackInterface;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import cn.jzvd.Jzvd;

public class CommentDetailFragment extends BaseStateFragment<CommendItemBean.RowsBean> implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener, HandleBackInterface {
    public CommendItemBean.RowsBean comment;
    public String shareUrl;
    //评论ID
    private String commentId;
    //内容ID
    private String contentId;
    private CommentReplayAdapter commentAdapter;

    @Override
    protected BaseQuickAdapter getAdapter() {
        if (commentAdapter == null) {
            commentAdapter = new CommentReplayAdapter();
            commentAdapter.setOnItemChildClickListener(this);
            commentAdapter.setOnItemClickListener(this);
        }
        return commentAdapter;
    }

    @Override
    public int getEmptyImage() {
        return R.mipmap.no_pinlun;
    }

    @Override
    public String getEmptyText() {
        return "下个神评就是你，快去评论吧";
    }

    @Override
    protected void initViewListener() {
        // TODO: 2018/11/5 初始化头布局
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.layout_comment_detail_header, mRvContent, false);
        initHeaderView(headerView);
        //设置头布局
        adapter.setHeaderView(headerView);
        adapter.setHeaderAndEmpty(true);
        // TODO: 2018/11/15 评论详情的分享url待定
        CommonHttpRequest.getInstance().getShareUrl(contentId, new JsonCallback<BaseResponseBean<ShareUrlBean>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<ShareUrlBean>> response) {
                shareUrl = response.body().getData().getUrl();
            }
        });
        bindHeader(comment);
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        HashMap<String, String> hashMapParams = CommonHttpRequest.getInstance().getHashMapParams();
        hashMapParams.put("cmttype", "2");//1_一级评论列表 2_子评论列表
        hashMapParams.put("pageno", "" + position);
        hashMapParams.put("pagesize", pageSize);
        hashMapParams.put("pid", commentId);//cmttype为1时：作品id ; cmttype为2时，评论id
        OkGo.<BaseResponseBean<CommendItemBean>>post(HttpApi.COMMENT_VISIT)
                .upJson(new JSONObject(hashMapParams))
                .execute(new JsonCallback<BaseResponseBean<CommendItemBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<CommendItemBean>> response) {
                        //神评列表
                        List<CommendItemBean.RowsBean> bestlist = response.body().getData().getBestlist();
                        //普通评论列表
                        List<CommendItemBean.RowsBean> rows = response.body().getData().getRows();
                        // TODO: 2018/11/16 评论详情页面没有ugc,所以直接就不取了
                        if (DateState.init_state == load_more || DateState.refresh_state == load_more) {
                            dealHasHeaderComment(bestlist, rows);
                        }
                        dealList(bestlist, rows, load_more);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<CommendItemBean>> response) {
                        super.onError(response);
                    }
                });
    }

    private void dealList(List<CommendItemBean.RowsBean> bestlist, List<CommendItemBean.RowsBean> rows, int load_more) {
        if (bestlist != null && rows != null) {
            rows.addAll(0, bestlist);
        }
        setDate(load_more, rows);
    }

    private void dealHasHeaderComment(List<CommendItemBean.RowsBean> bestlist, List<CommendItemBean.RowsBean> rows) {
        int size1 = bestlist == null ? 0 : bestlist.size();
        int size2 = rows == null ? 0 : rows.size();
        viewHolder.HasComment(size1 + size2 > 0);
    }


    private void bindHeader(CommendItemBean.RowsBean data) {
        if (data == null) {
            return;
        }
        viewHolder.bindDate(data);
        commentAdapter.setParentName(data.username);
    }

    public void setDate(CommendItemBean.RowsBean bean) {
        comment = bean;
        contentId = bean.contentid;
        commentId = bean.commentid;
    }

    CommentDetailHeaderViewHolder viewHolder;

    public void initHeaderView(View view) {
        if (viewHolder == null) {
            viewHolder = new CommentDetailHeaderViewHolder(view, this);
            //评论详情页面头布局分享回调
            viewHolder.setCallBack(new CommentDetailHeaderViewHolder.ShareCallBack() {
                @Override
                public void share(CommendItemBean.RowsBean bean) {
                    WebShareBean webBean = ShareHelper.getInstance().createWebBean(viewHolder.isVideo(), false
                            , viewHolder.getVideoUrl(), bean.contentid);
                    showShareDailog(webBean);
                }
            });
        }
    }

    public void showShareDailog(WebShareBean shareBean) {
        ShareDialog dialog = ShareDialog.newInstance(shareBean);
        dialog.setListener(new ShareDialog.ShareMediaCallBack() {
            @Override
            public void callback(WebShareBean bean) {
                //该对象已经含有平台参数
                String cover = viewHolder.getCover();
                WebShareBean shareBeanByDetail = ShareHelper.getInstance().getShareBeanByDetail(bean, comment, cover, shareUrl);
                ShareHelper.getInstance().shareWeb(shareBeanByDetail);
            }

            @Override
            public void colloection(boolean isCollection) {
                // TODO: 2018/11/16 评论没有收藏
            }

        });
        dialog.show(getChildFragmentManager(), getTag());
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        // TODO: 2018/11/16 以后如果加评论分享则只需要判断是否是ugc内容即可
        CommendItemBean.RowsBean bean = (CommendItemBean.RowsBean) adapter.getData().get(position);
        if (view.getId() == R.id.base_moment_share_iv) {
            WebShareBean webBean = ShareHelper.getInstance().createWebBean(false, false,
                    null, bean.contentid);
            showShareDailog(webBean);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        CommendItemBean.RowsBean bean = (CommendItemBean.RowsBean) adapter.getData().get(position);
        CommentDetailActivity commentDetailActivity = (CommentDetailActivity) getActivity();
        //双重安全
        if (commentDetailActivity == null) {
            commentDetailActivity = (CommentDetailActivity) MyApplication.getInstance().getRunningActivity();
        }
        commentDetailActivity.setReplyUser(bean.commentid, bean.userid,bean.username);
    }

    @Override
    public boolean onBackPressed() {
        return Jzvd.backPress();
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void publishComment(CommendItemBean.RowsBean bean) {
        if (viewHolder != null) {
            viewHolder.commentPlus();
        }
        if (commentAdapter.getData().size() == 0) {
            viewHolder.HasComment(true);
        }
        commentAdapter.addData(0, bean);
    }
}
