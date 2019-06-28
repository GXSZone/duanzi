package com.caotu.duanzhi.module.detail;

import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
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
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.BaseDialogFragment;
import com.caotu.duanzhi.view.dialog.CommentActionDialog;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentDetailFragment extends BaseStateFragment<CommendItemBean.RowsBean> implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener, HandleBackInterface, BaseQuickAdapter.OnItemLongClickListener, TextViewLongClick {
    public CommendItemBean.RowsBean comment;
    //评论ID
    private String commentId;
    //内容ID


    @Override
    protected BaseQuickAdapter getAdapter() {
        if (adapter == null) {
            adapter = new CommentReplayAdapter(this);
            adapter.setOnItemChildClickListener(this);
            adapter.setOnItemClickListener(this);
            adapter.setOnItemLongClickListener(this);
        }
        return adapter;
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
        bindHeader(comment);
        adapter.disableLoadMoreIfNotFullPage();
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
                        // TODO: 2019/4/15 评论列表自动过滤神评 ,只计算普通评论
                        CommendItemBean data = response.body().getData();
                        if (data == null) return;
                        //普通评论列表
                        List<CommendItemBean.RowsBean> rows = data.getRows();
                        dealList(rows, load_more);
                        int count = data.getCount();
                        comment.replyCount = count;
                        if (viewHolder == null) return;
                        viewHolder.setComment(count);
                    }
                });
    }

    private void dealList(List<CommendItemBean.RowsBean> rows, int load_more) {
        if (rows != null && rows.size() > 0
                && DateState.init_state == load_more
                && comment != null && !TextUtils.isEmpty(comment.fromCommentId)) {
            int position = -1;

            for (int i = 0; i < rows.size(); i++) {
                if (TextUtils.equals(rows.get(i).commentid, comment.fromCommentId)) {
                    position = i;
                    break;
                }
            }
            if (position != -1) {
                CommendItemBean.RowsBean remove = rows.remove(position);
                if (remove != null) {
                    rows.add(0, remove);
                    setDate(load_more, rows);
                }
            } else {
                // TODO: 2019-04-24 需要请求接口获取置顶
                HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
                params.put("cmtid", comment.fromCommentId);
                OkGo.<BaseResponseBean<CommendItemBean.RowsBean>>post(HttpApi.COMMENT_DEATIL)
                        .upJson(new JSONObject(params))
                        .execute(new JsonCallback<BaseResponseBean<CommendItemBean.RowsBean>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<CommendItemBean.RowsBean>> response) {
                                CommendItemBean.RowsBean data = response.body().getData();
                                if (data == null) return;
                                rows.add(0, data);
                                setDate(load_more, rows);
                            }

                            @Override
                            public void onError(Response<BaseResponseBean<CommendItemBean.RowsBean>> response) {
                                setDate(load_more, rows);
                            }
                        });
            }
        } else {
            setDate(load_more, rows);
        }

    }

    @Override
    public void onReStart() {
        getDetailDate();
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
                        if (data == null) return;
                        if (viewHolder != null) {
                            viewHolder.changeHeaderDate(data);
                        }
                    }
                });

    }

    private void bindHeader(CommendItemBean.RowsBean data) {
        if (data == null) {
            return;
        }
        viewHolder.bindDate(data);
        if (adapter instanceof CommentReplayAdapter) {
            ((CommentReplayAdapter) adapter).setParentName(data.username);
        }
    }

    public void setDate(CommendItemBean.RowsBean bean) {
        if (bean == null) return;
        comment = bean;
//        contentId = bean.contentid;
        commentId = bean.commentid;
    }

    private CommentDetailHeaderViewHolder viewHolder;

    public void initHeaderView(View view) {
        if (viewHolder == null) {
            viewHolder = new CommentDetailHeaderViewHolder(view);
            viewHolder.bindFragment(this);
        }
        if (getActivity() instanceof CommentDetailActivity) {
            viewHolder.bindSameView(null, null, null,
                    ((CommentDetailActivity) getActivity()).getBottomLikeView());
        }
    }

    public void showShareDailog(WebShareBean shareBean, CommendItemBean.RowsBean itemBean) {
        ShareDialog dialog = ShareDialog.newInstance(shareBean);
        dialog.setListener(new ShareDialog.ShareMediaCallBack() {
            @Override
            public void callback(WebShareBean bean) {
                //该对象已经含有平台参数
                String cover = viewHolder.getCover();
                WebShareBean shareBeanByDetail = ShareHelper.getInstance().getShareBeanByDetail(bean, itemBean.commentid, cover, CommonHttpRequest.cmt_url);
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
                .setSingleChoiceItems(BaseConfig.REPORTITEMS, -1, (dialog, which) ->
                        reportType = BaseConfig.REPORTITEMS[which])
                .setTitle("举报")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    if (TextUtils.isEmpty(reportType)) {
                        ToastUtil.showShort("请选择举报类型");
                    } else {
                        CommonHttpRequest.getInstance().requestReport(bean.commentid, reportType, 1);
                        dialog.dismiss();
                        reportType = null;
                    }
                }).show();
    }

    @Override
    public boolean onBackPressed() {
        return VideoViewManager.instance().onBackPressed();
    }


    public void publishComment(CommendItemBean.RowsBean bean) {
        if (viewHolder != null) {
            viewHolder.commentPlus();
        }
        if (adapter.getData().size() == 0) {
            ArrayList<CommendItemBean.RowsBean> arrayList = new ArrayList<>();
            arrayList.add(bean);
            adapter.setNewData(arrayList);
        } else {
            adapter.addData(0, bean);
            MyApplication.getInstance().getHandler().postDelayed(() -> smoothMoveToPosition(1, true), 500);
        }
    }

    public void share() {
        WebShareBean webBean = ShareHelper.getInstance().createWebBean(viewHolder.isVideo(), false,
                null, viewHolder.getVideoUrl(), comment.contentid);
        showShareDailog(webBean, comment);
    }
}
