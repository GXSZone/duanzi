package com.caotu.duanzhi.module.detail;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.other.HandleBackInterface;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.BaseDialogFragment;
import com.caotu.duanzhi.view.dialog.CommentActionDialog;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.dueeeke.videoplayer.playerui.StandardVideoController;
import com.dueeeke.videoplayer.smallwindow.FloatController;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * ugc内容详情和真正的内容详情公用一个页面
 */
public class ContentDetailFragment extends BaseStateFragment<CommendItemBean.RowsBean> implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener, HandleBackInterface, BaseQuickAdapter.OnItemLongClickListener, TextViewLongClick {
    public MomentsDataBean content;
    public String contentId;


    public void setDate(MomentsDataBean bean) {
        content = bean;
        if (bean != null) {
            contentId = bean.getContentid();
        }
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        if (adapter == null) {
            adapter = new DetailCommentAdapter(this);
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

    protected LinearLayoutManager layoutManager;
    private FloatController mFloatController;
    protected int firstVisibleItem = -1;

    @Override
    protected void initViewListener() {
        initHeader();
        adapter.disableLoadMoreIfNotFullPage();
        layoutManager = (LinearLayoutManager) mRvContent.getLayoutManager();
        mFloatController = new FloatController(mRvContent.getContext());
        mRvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!viewHolder.isVideo()) return;
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                IjkVideoView mIjkVideoView = viewHolder.getVideoView();
                if (mIjkVideoView == null) return;
                //第一条可见条目不是1则说明划出屏幕
                if (firstVisibleItem == 1) {
                    int[] videoSize = new int[2];
                    videoSize[0] = DevicesUtils.getSrecchWidth() / 2;
                    videoSize[1] = videoSize[0] * 9 / 16;
                    if (viewHolder != null && !viewHolder.isLandscape()) {
                        videoSize[0] = DevicesUtils.getSrecchWidth() / 3;
                        videoSize[1] = videoSize[0] * 4 / 3;
                    }
                    mIjkVideoView.setTinyScreenSize(videoSize);
                    mIjkVideoView.startTinyScreen();
                    mFloatController.setPlayState(mIjkVideoView.getCurrentPlayState());
                    mFloatController.setPlayerState(mIjkVideoView.getCurrentPlayerState());
                    mIjkVideoView.setVideoController(mFloatController);
                } else if (firstVisibleItem == 0) {
                    mIjkVideoView.stopTinyScreen();
                    StandardVideoController videoControll = viewHolder.getVideoController();
                    videoControll.setPlayState(mIjkVideoView.getCurrentPlayState());
                    videoControll.setPlayerState(mIjkVideoView.getCurrentPlayerState());
                    mIjkVideoView.setVideoController(videoControll);
                }
            }
        });
    }

    protected void initHeader() {
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.layout_content_detail_header, mRvContent, false);
        initHeaderView(headerView);
        //设置头布局
        adapter.setHeaderView(headerView);
        adapter.setHeaderAndEmpty(true);
        bindHeader(content);
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        HashMap<String, String> hashMapParams = CommonHttpRequest.getInstance().getHashMapParams();
        hashMapParams.put("cmttype", "1");//1_一级评论列表 2_子评论列表
        hashMapParams.put("pageno", String.valueOf(position));
        hashMapParams.put("pagesize", pageSize);
        hashMapParams.put("pid", contentId);//cmttype为1时：作品id ; cmttype为2时，评论id

        OkGo.<BaseResponseBean<CommendItemBean>>post(HttpApi.COMMENT_VISIT)
                .upJson(new JSONObject(hashMapParams))
                .execute(new JsonCallback<BaseResponseBean<CommendItemBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<CommendItemBean>> response) {
                        //神评列表
                        List<CommendItemBean.RowsBean> bestlist = response.body().getData().getBestlist();
                        //普通评论列表
                        List<CommendItemBean.RowsBean> rows = response.body().getData().getRows();
                        MomentsDataBean ugc = response.body().getData().getUgc();
                        if (ugc != null) {
                            ugcBean = DataTransformUtils.getContentNewBean(ugc);
                        }
                        dealList(bestlist, rows, ugc, load_more);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<CommendItemBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }

    public int bestSize = 0;


    public MomentsDataBean ugcBean;

    protected void dealList(List<CommendItemBean.RowsBean> bestlist, List<CommendItemBean.RowsBean> rows, MomentsDataBean ugc, int load_more) {
        ArrayList<CommendItemBean.RowsBean> beanArrayList = new ArrayList<>(20);
        CommendItemBean.RowsBean ugcBean = null;
        if (ugc != null) {
            ugcBean = DataTransformUtils.changeUgcBean(ugc);
        }
        //注意顺序就好
        if (AppUtil.listHasDate(bestlist)) {
            bestlist.get(0).isBest = true;
            beanArrayList.addAll(bestlist);
        }
        if (ugcBean != null) {
            beanArrayList.add(ugcBean);
        }
        if (AppUtil.listHasDate(rows)) {
            beanArrayList.addAll(rows);
        }

        //这是为了查看原帖的时候没有该对象
        if (content == null) {
            setDate(load_more, beanArrayList);
            return;
        }
        // TODO: 2019/4/15 可能还需要限定前置跳转页面,多加个判断
        if (DateState.init_state == load_more && !TextUtils.isEmpty(content.fromCommentId)) {
            int position = -1;
            try {
                for (int i = 0; i < beanArrayList.size(); i++) {
                    if (TextUtils.equals(beanArrayList.get(i).commentid, content.fromCommentId)) {
                        position = i;
                        break;
                    }
                }
                if (position != -1) {
                    CommendItemBean.RowsBean remove = beanArrayList.remove(position);
                    beanArrayList.add(0, remove);
                    setDate(load_more, beanArrayList);
                } else {
                    // TODO: 2019-04-24 需要请求接口获取置顶
                    HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
                    params.put("cmtid", content.fromCommentId);
                    OkGo.<BaseResponseBean<CommendItemBean.RowsBean>>post(HttpApi.COMMENT_DEATIL)
                            .upJson(new JSONObject(params))
                            .execute(new JsonCallback<BaseResponseBean<CommendItemBean.RowsBean>>() {
                                @Override
                                public void onSuccess(Response<BaseResponseBean<CommendItemBean.RowsBean>> response) {
                                    CommendItemBean.RowsBean data = response.body().getData();
                                    if (data == null) return;
                                    beanArrayList.add(0, data);
                                    setDate(load_more, beanArrayList);
                                }

                                @Override
                                public void onError(Response<BaseResponseBean<CommendItemBean.RowsBean>> response) {
                                    setDate(load_more, beanArrayList);
                                }
                            });
                }
            } catch (Exception e) {
                setDate(load_more, beanArrayList);
                e.printStackTrace();
            }
        } else {
            setDate(load_more, beanArrayList);
        }
    }

    public void bindHeader(MomentsDataBean data) {
        if (data == null) {
            Activity runningActivity = MyApplication.getInstance().getRunningActivity();
            if (runningActivity instanceof ContentDetailActivity) {
                contentId = ((ContentDetailActivity) runningActivity).getContentId();
            }
            getDetailDate(false);
        } else {
            viewHolder.bindDate(data);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OkGo.getInstance().cancelTag(this);
    }

    private void getDetailDate(boolean isSkip) {
        if (TextUtils.isEmpty(contentId)) return;
        //用于通知跳转
        HashMap<String, String> hashMapParams = new HashMap<>();
        hashMapParams.put("contentid", contentId);
        OkGo.<BaseResponseBean<MomentsDataBean>>post(HttpApi.WORKSHOW_DETAILS)
                .upJson(new JSONObject(hashMapParams))
                .tag(this)
                .execute(new JsonCallback<BaseResponseBean<MomentsDataBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<MomentsDataBean>> response) {
                        MomentsDataBean data = DataTransformUtils.getContentNewBean(response.body().getData());
                        if (data == null) return;
                        changeHeaderUi(data, isSkip);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<MomentsDataBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }

    public void changeHeaderUi(MomentsDataBean data, boolean isSkip) {
        content = data;
        if (viewHolder != null) {
            if (isSkip) {
                viewHolder.justBindCountAndState(data);
            } else {
                viewHolder.bindDate(data);
            }
        }
        if (getActivity() != null && getActivity() instanceof ContentDetailActivity) {
            ((ContentDetailActivity) getActivity()).setPresenter(data);
        }
    }

    @Override
    public void onReStart() {
        getDetailDate(true);
    }


    public IHolder<MomentsDataBean> viewHolder;

    public IHolder initHeaderView(View view) {
        if (viewHolder == null) {
            viewHolder = new DetailHeaderViewHolder(view);
            viewHolder.bindFragment(this);
        }
        if (getActivity() instanceof ContentDetailActivity) {
            viewHolder.bindSameView(null, null, null,
                    ((ContentDetailActivity) getActivity()).getBottomLikeView());
        }
        return viewHolder;
    }

    /**
     * @param shareBean
     * @param shareUrl
     * @param itemBean
     * @param momentsDataBean 区分ugc的分享
     */
    public void showShareDailog(WebShareBean shareBean, String shareUrl, CommendItemBean.RowsBean itemBean, MomentsDataBean momentsDataBean) {
        ShareDialog dialog = ShareDialog.newInstance(shareBean);
        dialog.setListener(new ShareDialog.ShareMediaCallBack() {
            @Override
            public void callback(WebShareBean bean) {
                //该对象已经含有平台参数
                WebShareBean shareBeanByDetail;
                if (momentsDataBean != null) {
                    String cover = VideoAndFileUtils.getCover(momentsDataBean.getContenturllist());
                    shareBeanByDetail = ShareHelper.getInstance().getShareBeanByDetail(bean, momentsDataBean, cover, shareUrl);
                } else {
                    String cover2 = "";
                    List<CommentUrlBean> commentUrlBean = VideoAndFileUtils.getCommentUrlBean(itemBean.commenturl);
                    if (commentUrlBean != null && commentUrlBean.size() > 0) {
                        cover2 = commentUrlBean.get(0).cover;
                    }
                    shareBeanByDetail = ShareHelper.getInstance().getShareBeanByDetail(bean, itemBean.commentid, cover2, shareUrl);
                }

                ShareHelper.getInstance().shareWeb(shareBeanByDetail);
            }

            @Override
            public void colloection(boolean isCollection) {
                // TODO: 2018/11/16 可能还需要回调给列表
                content.setIscollection(isCollection ? "1" : "0");
                ToastUtil.showShort(isCollection ? "收藏成功" : "取消收藏成功");
            }
        });
        dialog.show(getChildFragmentManager(), getTag());
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

            // TODO: 2018/11/20 分享也得区分开
            if (bean.isUgc && ugcBean != null) {
                boolean isVideo = LikeAndUnlikeUtil.isVideoType(ugcBean.getContenttype());
                String videoUrl = isVideo ? VideoAndFileUtils.getVideoUrl(ugcBean.getContenturllist()) : "";
                WebShareBean webBean = ShareHelper.getInstance().createWebBean(isVideo,
                        false, null, videoUrl, ugcBean.getContentid());
                showShareDailog(webBean, CommonHttpRequest.url, null, ugcBean);
            } else {
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
                showShareDailog(webBean, CommonHttpRequest.cmt_url, bean, null);
            }

        } else if (view.getId() == R.id.child_reply_layout) {
            if (bean.isUgc && ugcBean != null) {
                HelperForStartActivity.openUgcDetail(ugcBean);
            } else {
                HelperForStartActivity.openCommentDetail(bean);
            }
        } else if (view.getId() == R.id.expand_text_view) {
            if (bean.isUgc && ugcBean != null) {
                HelperForStartActivity.openUgcDetail(ugcBean);
            } else {
                HelperForStartActivity.openCommentDetail(bean);
            }
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        CommendItemBean.RowsBean bean = (CommendItemBean.RowsBean) adapter.getData().get(position);
        // TODO: 2018/11/15 如果是ugc跳转虽然是评论详情,但是接口请求还是内容详情接口
        if (bean.isUgc && ugcBean != null) {
            HelperForStartActivity.openUgcDetail(ugcBean);
        } else {
            HelperForStartActivity.openCommentDetail(bean);
        }
    }


    @Override
    public boolean onBackPressed() {
        return VideoViewManager.instance().onBackPressed();
    }

    public void publishComment(CommendItemBean.RowsBean bean) {
        if (viewHolder != null) {
            viewHolder.commentPlus();
        }
        if (adapter == null) return;
        //只有神评,都有,没有神评,没有评论
        if (adapter.getData().size() == 0) {
            adapter.addData(bean);
            adapter.loadMoreEnd();

        } else {
            adapter.addData(0, bean);
            MyApplication.getInstance().getHandler().postDelayed(() -> smoothMoveToPosition(1), 500);
        }
    }

    /**
     * 判断集合是否有数据
     *
     * @param collection
     * @return
     */
    public boolean listHasDate(Collection collection) {
        return collection != null && collection.size() > 0;
    }


    @Override
    public void textLongClick(BaseQuickAdapter adapter, View view, int position) {
        onItemLongClick(adapter, view, position);
    }

    @Override
    public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
        CommendItemBean.RowsBean bean = (CommendItemBean.RowsBean) adapter.getData().get(position);
        boolean isHastitle = true;
        if (bean.isUgc && !bean.isShowTitle) {
            isHastitle = false;
        }
        CommentActionDialog dialog = new CommentActionDialog();
        dialog.setContentIdAndCallBack(bean.commentid, new BaseDialogFragment.DialogListener() {
            @Override
            public void deleteItem() {
                if (bean.isUgc) {
                    CommonHttpRequest.getInstance().deletePost(bean.contentid);
                    adapter.remove(position);
                    //通知列表更新条目
                    viewHolder.commentMinus();
                } else {
                    CommonHttpRequest.getInstance().deleteComment(bean.commentid, new JsonCallback<BaseResponseBean<String>>() {
                        @Override
                        public void onSuccess(Response<BaseResponseBean<String>> response) {
                            // TODO: 2018/12/12 显示头得重新设置,还得考虑有热门评论的情况
                            if (position == 0) {
                                if (adapter.getData().size() > 1) {
                                    CommendItemBean.RowsBean rowsBean = (CommendItemBean.RowsBean) adapter.getData().get(position + 1);
                                    rowsBean.showHeadr = true;
                                }
                                adapter.getData().remove(position);
                                adapter.notifyDataSetChanged();
                            } else {
                                //考虑有热门评论的情况,刚好删除的是最新评论的第一条,另外还得考虑当前总的数据长度大于最新评论
                                if (bestSize > 0 && position == bestSize && adapter.getData().size() > bestSize) {
                                    CommendItemBean.RowsBean rowsBean = (CommendItemBean.RowsBean) adapter.getData().get(position + 1);
                                    rowsBean.showHeadr = true;
                                    adapter.getData().remove(position);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    adapter.remove(position);
                                }
                            }
                            //通知列表更新条目
                            viewHolder.commentMinus();
                        }
                    });
                }

            }

            @Override
            public void report() {
                showReportDialog(bean);
            }
        }, MySpUtils.isMe(bean.userid), isHastitle ? bean.commenttext : null);

        dialog.show(getChildFragmentManager(), "dialog");
        return true;
    }

    String reportType;

    private void showReportDialog(CommendItemBean.RowsBean bean) {
        new AlertDialog.Builder(MyApplication.getInstance().getRunningActivity())
                .setSingleChoiceItems(BaseConfig.REPORTITEMS, -1, (dialog, which) -> reportType = BaseConfig.REPORTITEMS[which])
                .setTitle("举报")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    if (TextUtils.isEmpty(reportType)) {
                        ToastUtil.showShort("请选择举报类型");
                    } else {
                        String id = bean.commentid;
                        int type = 1;
                        if (bean.isUgc) {
                            id = bean.contentid;
                            type = 0;
                        }
                        CommonHttpRequest.getInstance().requestReport(id, reportType, type);
                        dialog.dismiss();
                        reportType = null;
                    }
                }).show();
    }
}
