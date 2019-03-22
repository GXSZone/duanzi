package com.caotu.duanzhi.module.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.other.HandleBackInterface;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.BaseDialogFragment;
import com.caotu.duanzhi.view.dialog.CommentActionDialog;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import cn.jzvd.Jzvd;

public class CommentDetailFragment extends BaseStateFragment<CommendItemBean.RowsBean> implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener, HandleBackInterface, BaseQuickAdapter.OnItemLongClickListener, TextViewLongClick {
    public CommendItemBean.RowsBean comment;
    //评论ID
    private String commentId;
    //内容ID
    private CommentReplayAdapter commentAdapter;

    @Override
    protected BaseQuickAdapter getAdapter() {
        if (commentAdapter == null) {
            commentAdapter = new CommentReplayAdapter(this);
            commentAdapter.setOnItemChildClickListener(this);
            commentAdapter.setOnItemClickListener(this);
            commentAdapter.setOnItemLongClickListener(this);
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
    public void changeEmptyParam(View emptyView) {
        ViewGroup.LayoutParams layoutParams = emptyView.getLayoutParams();
        layoutParams.height = DevicesUtils.dp2px(250);
        emptyView.setLayoutParams(layoutParams);
    }

    @Override
    protected void initViewListener() {
        // TODO: 2018/11/5 初始化头布局
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.layout_comment_detail_header, mRvContent, false);
        initHeaderView(headerView);
        //设置头布局
        adapter.setHeaderView(headerView);
        adapter.setHeaderAndEmpty(true);
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
                        dealList(bestlist, rows, load_more);
                    }

                });
    }

    private void dealList(List<CommendItemBean.RowsBean> bestlist, List<CommendItemBean.RowsBean> rows, int load_more) {
        if (bestlist != null && rows != null) {
            rows.addAll(0, bestlist);
        }
        setDate(load_more, rows);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasSkip) {
            getDetailDate();
            hasSkip = false;
        }
    }

    private void getDetailDate() {
        if (comment == null || TextUtils.isEmpty(commentId)) return;
        HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
        params.put("cmtid", commentId);
        OkGo.<BaseResponseBean<CommendItemBean.RowsBean>>post(HttpApi.COMMENT_DEATIL)
                .upJson(new JSONObject(params))
                .execute(new JsonCallback<BaseResponseBean<CommendItemBean.RowsBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<CommendItemBean.RowsBean>> response) {
                        CommendItemBean.RowsBean data = response.body().getData();
                        if (viewHolder != null) {
                            viewHolder.changeHeaderDate(data);
                        }
                    }
                });

    }

    boolean hasSkip = false;

    /**
     * 用于是否从该页面跳转出去
     */
    @Override
    public void onPause() {
        super.onPause();
        hasSkip = true;
    }


    private void bindHeader(CommendItemBean.RowsBean data) {
        if (data == null) {
            return;
        }
        viewHolder.bindDate(data);
        commentAdapter.setParentName(data.username);
    }

    public void setDate(CommendItemBean.RowsBean bean) {
        if (bean == null) return;
        comment = bean;
//        contentId = bean.contentid;
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
                    WebShareBean webBean = ShareHelper.getInstance().createWebBean(viewHolder.isVideo(), false,
                            null, viewHolder.getVideoUrl(), bean.contentid);
                    showShareDailog(webBean, comment);
                }
            });
        }
    }

    public void showShareDailog(WebShareBean shareBean, CommendItemBean.RowsBean itemBean) {
        ShareDialog dialog = ShareDialog.newInstance(shareBean);
        dialog.setListener(new ShareDialog.ShareMediaCallBack() {
            @Override
            public void callback(WebShareBean bean) {
                //该对象已经含有平台参数
                String cover = viewHolder.getCover();
                WebShareBean shareBeanByDetail = ShareHelper.getInstance().getShareBeanByDetail(bean, itemBean, cover, CommonHttpRequest.cmt_url);
                ShareHelper.getInstance().shareWeb(shareBeanByDetail);
            }

            @Override
            public void colloection(boolean isCollection) {
                // TODO: 2018/11/16 评论没有收藏
            }

        });
        dialog.show(getChildFragmentManager(), "share");
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

        CommendItemBean.RowsBean bean = (CommendItemBean.RowsBean) adapter.getData().get(position);
        if (view.getId() == R.id.base_moment_share_iv) {
            List<CommentUrlBean> commentUrlBean = VideoAndFileUtils.getCommentUrlBean(bean.commenturl);
            boolean isVideo = false;
            String videoUrl = "";
            if (commentUrlBean != null && commentUrlBean.size() > 0) {
                isVideo = LikeAndUnlikeUtil.isVideoType(commentUrlBean.get(0).type);
                if (isVideo) {
                    videoUrl = commentUrlBean.get(0).info;
                }
            }
            WebShareBean webBean = ShareHelper.getInstance().createWebBean(isVideo, false
                    , null, videoUrl, bean.commentid);

            showShareDailog(webBean, bean);
        } else if (view.getId() == R.id.expand_text_view) {
            commentDetailReplay(bean);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        CommendItemBean.RowsBean bean = (CommendItemBean.RowsBean) adapter.getData().get(position);
        commentDetailReplay(bean);
    }

    private void commentDetailReplay(CommendItemBean.RowsBean bean) {
        CommentDetailActivity commentDetailActivity = (CommentDetailActivity) getActivity();
        //双重安全
        if (commentDetailActivity == null) {
            commentDetailActivity = (CommentDetailActivity) MyApplication.getInstance().getRunningActivity();
        }
        commentDetailActivity.setReplyUser(bean.commentid, bean.userid, bean.username);
    }

    String reportType;

    @Override
    public void textLongClick(BaseQuickAdapter adapter, View view, int position) {
        // TODO: 2018/12/17 注意adapter的position的修正
        onItemLongClick(adapter, view, position);
    }

    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
        CommendItemBean.RowsBean bean = (CommendItemBean.RowsBean) adapter.getData().get(position);
        CommentActionDialog dialog = new CommentActionDialog();
        dialog.setContentIdAndCallBack(bean.commentid, new BaseDialogFragment.DialogListener() {
            @Override
            public void deleteItem() {
                CommonHttpRequest.getInstance().deleteComment(bean.commentid, new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        adapter.remove(position);
                        //通知列表更新条目
                        viewHolder.commentMinus();
                    }
                });
            }

            @Override
            public void report() {
                showReportDialog(bean);
            }
        }, MySpUtils.isMe(bean.userid), bean.commenttext);

        dialog.show(getChildFragmentManager(), "dialog");

        return true;
    }

    private void showReportDialog(CommendItemBean.RowsBean bean) {
        new AlertDialog.Builder(MyApplication.getInstance().getRunningActivity())
                .setSingleChoiceItems(BaseConfig.REPORTITEMS, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reportType = BaseConfig.REPORTITEMS[which];
                    }
                })
                .setTitle("举报")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(reportType)) {
                            ToastUtil.showShort("请选择举报类型");
                        } else {
                            CommonHttpRequest.getInstance().requestReport(bean.commentid, reportType, 1);
                            dialog.dismiss();
                            reportType = null;
                        }
                    }
                }).show();
    }

    @Override
    public boolean onBackPressed() {
        return Jzvd.backPress();
    }


    public void publishComment(CommendItemBean.RowsBean bean) {
        if (viewHolder != null) {
            viewHolder.commentPlus();
        }
        if (commentAdapter.getData().size() == 0) {
            commentAdapter.addData(bean);
            commentAdapter.loadMoreEnd();
            commentAdapter.disableLoadMoreIfNotFullPage();
        } else {
            commentAdapter.addData(0, bean);
            MyApplication.getInstance().getHandler().postDelayed(() -> smoothMoveToPosition(1), 500);
        }
    }
}
