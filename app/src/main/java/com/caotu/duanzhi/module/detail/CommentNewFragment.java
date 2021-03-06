package com.caotu.duanzhi.module.detail;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.holder.CommentDetailHeaderViewHolder;
import com.caotu.duanzhi.module.holder.IHolder;
import com.caotu.duanzhi.module.publish.IViewDetail;
import com.caotu.duanzhi.other.HandleBackInterface;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ParserUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.BaseDialogFragment;
import com.caotu.duanzhi.view.dialog.CommentActionDialog;
import com.caotu.duanzhi.view.dialog.ReplyDialog;
import com.caotu.duanzhi.view.dialog.ReportDialog;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.caotu.duanzhi.view.widget.AvatarWithNameLayout;
import com.caotu.duanzhi.view.widget.ReplyTextView;
import com.caotu.duanzhi.view.widget.TitleView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * 评论详情页的fragment,只是图文,视频不一样,跟内容的详情大同小异,有些地方不一样,现在比较难剥离
 */
public class CommentNewFragment extends BaseStateFragment<CommendItemBean.RowsBean>
        implements BaseQuickAdapter.OnItemChildClickListener,
        BaseQuickAdapter.OnItemClickListener,
        BaseQuickAdapter.OnItemLongClickListener,
        HandleBackInterface, IVewPublishComment, IViewDetail {

    public CommentReplyPresenter presenter;
    protected TextView bottomLikeView;
    protected CommendItemBean.RowsBean bean;
    //这里负责定义
    public AvatarWithNameLayout avatarWithNameLayout;
    public TextView mUserIsFollow;

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_not_video_detail_layout;
    }

    @Override
    public CommentReplyPresenter getPresenter() {
        if (presenter == null) {
            presenter = new CommentReplyPresenter(this, bean);
        }
        return presenter;
    }

    @Override
    public void onAttach(@Nonnull Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();
        if (bundle == null) {
            ToastUtil.showShort("未传参数");
            return;
        }
        bean = bundle.getParcelable("commentBean");
    }

    public void initOtherView(View inflate) {
        TitleView titleView = inflate.findViewById(R.id.title_view);
        titleView.setTitleText("评论详情");
        titleView.setRightGone(bean == null || MySpUtils.isMe(bean.userid));
        titleView.setClickListener(() -> showReportDialog(bean.commentid));
    }

    @Override
    protected void initView(View inflate) {
        super.initView(inflate);
        bottomLikeView = rootView.findViewById(R.id.bottom_tv_like);
        rootView.findViewById(R.id.bottom_iv_collection).setVisibility(View.GONE); //评论详情底部没有收藏
        rootView.findViewById(R.id.bottom_iv_share).setOnClickListener(v -> {
            WebShareBean webBean = ShareHelper.getInstance().createWebBean(viewHolder.isVideo(), false,
                    null, viewHolder.getVideoUrl(), bean.contentid);
            showShareDialog(webBean, bean);
        });
        ReplyTextView replyTextView = rootView.findViewById(R.id.tv_send_content);
        replyTextView.setListener(this::showPopFg);
    }

    @Override
    protected void initViewListener() {
        initOtherView(rootView);
        getPresenter();
        initHeader();
    }

    public IHolder<CommendItemBean.RowsBean> viewHolder;

    protected void initHeader() {
        //两个头布局只差了个查看原帖的区别
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.layout_comment_detail_header, mRvContent, false);
        if (viewHolder == null) {
            viewHolder = new CommentDetailHeaderViewHolder(headerView);
            viewHolder.bindFragment(this);
        }

        //设置头布局
        adapter.setHeaderView(headerView);
        adapter.setHeaderAndEmpty(true);
        avatarWithNameLayout = headerView.findViewById(R.id.group_user_avatar);
        mUserIsFollow = headerView.findViewById(R.id.iv_is_follow);
        //因为功能相同,所以就统一都由头holder处理得了,分离代码
        viewHolder.bindSameView(avatarWithNameLayout, mUserIsFollow, bottomLikeView);
        if (bean == null) return;
        viewHolder.bindDate(bean);
        //这个跟列表的回复UI相关
        if (adapter instanceof CommentReplayAdapter) {
            ((CommentReplayAdapter) adapter).setParentName(bean.username);
        }
    }


    @Override
    protected BaseQuickAdapter getAdapter() {
        if (adapter == null) {
            adapter = new CommentReplayAdapter();
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
    protected void getNetWorkDate(int load_more) {
        HashMap<String, String> hashMapParams = CommonHttpRequest.getInstance().getHashMapParams();
        hashMapParams.put("cmttype", "2");//1_一级评论列表 2_子评论列表
        hashMapParams.put("pageno", "" + position);
        hashMapParams.put("pagesize", pageSize);
        hashMapParams.put("pid", bean.commentid);//cmttype为1时：作品id ; cmttype为2时，评论id
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
                        getPresenter().dealList(rows, load_more);
                        int count = data.getCount();
                        bean.replyCount = count;
                        if (viewHolder instanceof CommentDetailHeaderViewHolder) {
                            ((CommentDetailHeaderViewHolder) viewHolder).setComment(count);
                        }
                    }
                });
    }

    @Override
    public void setListDate(List<CommendItemBean.RowsBean> listDate, int load_more) {
        setDate(load_more, listDate);
        if (load_more != DateState.load_more &&
                !AppUtil.listHasDate(listDate)) {
            bottomLikeView.postDelayed(() -> {
                //"回复@" + bean.username + ":"
                hint = "回复@" + bean.username + ":";
                showPopFg(false);
            }, 500);
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

            showShareDialog(webBean, bean);
        } else if (view.getId() == R.id.group_user_avatar) {
            HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, bean.userid);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        CommendItemBean.RowsBean rowsBean = (CommendItemBean.RowsBean) adapter.getData().get(position);
        getPresenter().setUserInfo(rowsBean.commentid, rowsBean.userid);
        hint = "回复@" + rowsBean.username + ":";
        showPopFg(false);
    }

    public void showShareDialog(WebShareBean shareBean, CommendItemBean.RowsBean itemBean) {
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
            public void collection(boolean isCollection) {
                // TODO: 2018/11/16 评论没有收藏
            }

        });
        dialog.show(getChildFragmentManager(), "share");
    }


    public void publishComment(CommendItemBean.RowsBean bean) {

        if (viewHolder != null) {
            viewHolder.commentPlus();
        }
        if (adapter == null || mRvContent == null) return;
        //只有神评,都有,没有神评,没有评论
        if (adapter.getData().size() == 0) {
            ArrayList<CommendItemBean.RowsBean> beans = new ArrayList<>();
            beans.add(bean);
            adapter.setNewData(beans);
        } else {
            adapter.addData(0, bean);
            mRvContent.postDelayed(() -> smoothMoveToPosition(1, true), 200);
        }
    }

    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
        CommendItemBean.RowsBean bean = (CommendItemBean.RowsBean) adapter.getData().get(position);
        CommentActionDialog dialog = new CommentActionDialog();
        final String commentid = bean.commentid;
        dialog.setContentIdAndCallBack(commentid, new BaseDialogFragment.DialogListener() {
            @Override
            public void deleteItem() {
                CommonHttpRequest.getInstance().deleteComment(commentid, new JsonCallback<BaseResponseBean<String>>() {
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
                showReportDialog(commentid);
            }
        }, MySpUtils.isMe(bean.userid), ParserUtils.htmlToJustAtText(bean.commenttext));

        dialog.show(getChildFragmentManager(), "dialog");
        return true;
    }


    public void showReportDialog(String id) {
        ReportDialog dialog = new ReportDialog(getContext());
        dialog.setIdAndType(id, 1);
        dialog.show();
    }

    ProgressDialog dialog;

    @Override
    public void publishError() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        ToastUtil.showShort("发布失败");
        detailPop.dismissByClearDate();
    }

    @Override
    public void endPublish(CommendItemBean.RowsBean bean) {
        UmengHelper.event(UmengStatisticsKeyIds.comment_success);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        ToastUtil.showShort("发射成功");
        publishComment(bean);
        detailPop.dismissByClearDate();
    }

    @Override
    public void publishCantTalk(String msg) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        ToastUtil.showShort(msg);
        detailPop.dismissByClearDate();
    }

    @Override
    public void uploadProgress(int progress) {
        if (dialog != null && dialog.isShowing()) {
            dialog.setProgress(progress);
        }
    }


    @Override
    public void startPublish() {
        if (getActivity() == null) return;
        if (dialog == null) {
            dialog = new ProgressDialog(getContext());
            dialog.setMax(100);
            dialog.setCancelable(false);
            dialog.setMessage("预备发射中...");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }
        dialog.show();
    }

    /***************************底部输入框弹窗**********************************/
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (detailPop != null) {
            detailPop.onActivityResult(requestCode, resultCode, data);
        }
    }


    ReplyDialog detailPop;
    CharSequence hint;

    public void showPopFg(boolean isShowListStr) {
        Activity activity = getActivity();
        if (activity == null || activity.isDestroyed() || activity.isFinishing()) return;
        if (detailPop == null) {
            detailPop = new ReplyDialog(activity, CommentNewFragment.this);
            detailPop.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (bean != null && !TextUtils.isEmpty(bean.username)) {
                        hint = "回复@" + bean.username + ":";
                        getPresenter().setUserInfo(bean.commentid, bean.userid);
                    }
                }
            });
        }
        detailPop.setParams(isShowListStr, hint);
        detailPop.show();
    }

    @Override
    public EditText getEditView() {
        return detailPop != null ? detailPop.getEditView() : null;
    }

    @Override
    public View getPublishView() {
        return detailPop != null ? detailPop.getPublishView() : null;
    }

}
