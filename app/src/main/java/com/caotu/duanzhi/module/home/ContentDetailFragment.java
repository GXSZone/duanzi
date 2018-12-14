package com.caotu.duanzhi.module.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.ShareUrlBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.other.HandleBackInterface;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.BaseDialogFragment;
import com.caotu.duanzhi.view.dialog.CommentActionDialog;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.sunfusheng.widget.ImageData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import cn.jzvd.Jzvd;

/**
 * ugc内容详情和真正的内容详情公用一个页面
 */
public class ContentDetailFragment extends BaseStateFragment<CommendItemBean.RowsBean> implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener, HandleBackInterface, BaseQuickAdapter.OnItemLongClickListener {
    public MomentsDataBean content;
    public String mShareUrl;
    public String mCommentUrl;
    protected String contentId;
    protected boolean isComment;
    protected DetailCommentAdapter commentAdapter;
    protected LinearLayoutManager layoutManager;
    public int mVideoProgress = 0;


    public void setDate(MomentsDataBean bean, boolean iscomment, int videoProgress) {
        content = bean;
        isComment = iscomment;
        if (bean != null) {
            contentId = bean.getContentid();
        }
        mVideoProgress = videoProgress;
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        if (commentAdapter == null) {
            commentAdapter = new DetailCommentAdapter();
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
    protected void initViewListener() {
        // TODO: 2018/11/5 初始化头布局
        initHeader();
        CommonHttpRequest.getInstance().getShareUrl(contentId, new JsonCallback<BaseResponseBean<ShareUrlBean>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<ShareUrlBean>> response) {
                mShareUrl = response.body().getData().getUrl();
                mCommentUrl = response.body().getData().getCmt_url();
            }
        });

        layoutManager = (LinearLayoutManager) mRvContent.getLayoutManager();
        mRvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!viewHolder.isVideo()) return;
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                Log.i("firstVisibleItem", "onScrolled: " + firstVisibleItem);
                //第一条可见条目不是1则说明划出屏幕
                if (firstVisibleItem == 1) {
                    MyVideoPlayerStandard videoView = viewHolder.getVideoView();
                    //这个是判断暂停状态的时候不启动悬浮窗模式
                    if (videoView.currentState == Jzvd.CURRENT_STATE_PLAYING && videoView.currentScreen != Jzvd.SCREEN_WINDOW_TINY) {
                        isTiny = true;
                        videoView.startWindowTiny(viewHolder.isLandscape());
                    }
                } else if (firstVisibleItem == 0) {
                    //过滤初始化的回调
                    if (!isTiny) return;
                    Jzvd.backPress();
                    isTiny = false;
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (mShouldScroll && RecyclerView.SCROLL_STATE_IDLE == newState) {
                    mShouldScroll = false;
                    smoothMoveToPosition(mRvContent, mToPosition);
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

    protected int firstVisibleItem = 0;
    protected boolean isTiny = false;

    @Override
    protected void getNetWorkDate(int load_more) {
        HashMap<String, String> hashMapParams = CommonHttpRequest.getInstance().getHashMapParams();
        hashMapParams.put("cmttype", "1");//1_一级评论列表 2_子评论列表
        hashMapParams.put("pageno", "" + position);
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
                            ugcBean = ugc;
                        }
                        dealList(bestlist, rows, ugc, load_more);
                    }
                });
    }

    public int bestSize = 0;

    // TODO: 2018/11/20  用于记录评论列表的详情的跳转
    public MomentsDataBean ugcBean;

    protected void dealList(List<CommendItemBean.RowsBean> bestlist, List<CommendItemBean.RowsBean> rows, MomentsDataBean ugc, int load_more) {
        ArrayList<CommendItemBean.RowsBean> beanArrayList = new ArrayList<>();
        CommendItemBean.RowsBean ugcBean = null;
        if (ugc != null) {
            ugcBean = DataTransformUtils.changeUgcBean(ugc);
        }
        //这里只处理初始化和刷新,加载更多直接忽略神评和ugc
        if (DateState.load_more != load_more) {
            if (listHasDate(bestlist)) {
                bestSize = bestlist.size();
                for (int i = 0; i < bestSize; i++) {
                    bestlist.get(i).isBest = true;
                    if (i == 0) {
                        bestlist.get(i).showHeadr = true;
                    }
                    if (i == bestSize - 1) {
                        bestlist.get(i).isShowFooterLine = false;
                    } else {
                        bestlist.get(i).isShowFooterLine = true;
                    }
                }
                beanArrayList.addAll(bestlist);
            }


            // TODO: 2018/11/15 ugc 内容展示到最新评论里
            if (listHasDate(rows)) {
                if (ugcBean != null && rows.size() >= 3) {
                    rows.add(2, ugcBean);
                } else if (ugc != null && rows.size() <= 2) {
                    rows.add(ugcBean);
                }
                rows.get(0).showHeadr = true;
                for (int i = 0; i < rows.size(); i++) {
//                    if (i != rows.size() - 1) {
                    rows.get(i).isShowFooterLine = true;
                    //目前不处理最后一条的下划线显示,逻辑太多,偷懒
//                    } else {
//                        rows.get(i).isShowFooterLine = false;
//                    }
                }
                beanArrayList.addAll(rows);
            }
        } else if (rows != null && rows.size() > 0) {
            beanArrayList.addAll(rows);
        }
        //这里的代码是为了从评论跳进来直接到评论列表
        if (beanArrayList.size() > 0 && isComment) {
            MyApplication.getInstance().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    smoothMoveToPosition(mRvContent, 1);
                }
            }, 200);
        }
        setDate(load_more, beanArrayList);
    }

    public void bindHeader(MomentsDataBean data) {
        if (data == null) {
            if (getActivity() != null) {
                contentId = ((ContentDetailActivity) getActivity()).getContentId();
            } else {
                Activity runningActivity = MyApplication.getInstance().getRunningActivity();
                if (runningActivity instanceof ContentDetailActivity) {
                    contentId = ((ContentDetailActivity) runningActivity).getContentId();
                }
            }
            getDetailDate(false);
        } else {
            viewHolder.bindDate(data);
        }
    }

    private void getDetailDate(boolean isSkip) {
        if (TextUtils.isEmpty(contentId)) return;
        //用于通知跳转
        HashMap<String, String> hashMapParams = new HashMap<>();
        hashMapParams.put("contentid", contentId);
        OkGo.<BaseResponseBean<MomentsDataBean>>post(HttpApi.DETAILID)
                .upJson(new JSONObject(hashMapParams))
                .execute(new JsonCallback<BaseResponseBean<MomentsDataBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<MomentsDataBean>> response) {
                        MomentsDataBean data = response.body().getData();
                        if (isSkip) {
                            viewHolder.justBindCountAndState(data);
                        } else {
                            viewHolder.bindDate(data);
                        }
                        if (getActivity() != null && getActivity() instanceof ContentDetailActivity) {
                            ((ContentDetailActivity) getActivity()).setPresenter(data);
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<MomentsDataBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasSkip) {
            getDetailDate(true);
            hasSkip = false;
        }
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

    // TODO: 2018/11/20 这里就要用到面向接口编程,viewHolder这里写死了
    protected IHolder viewHolder;

    public void initHeaderView(View view) {
        if (viewHolder == null) {
            viewHolder = new DetailHeaderViewHolder(this, view, mVideoProgress);
            viewHolder.setCallBack(new IHolder.ShareCallBack() {
                @Override
                public void share(MomentsDataBean bean) {
                    WebShareBean webBean = ShareHelper.getInstance().createWebBean(viewHolder.isVideo(), true
                            , content == null ? "0" : content.getIscollection(), viewHolder.getVideoUrl(), bean.getContentid());
                    showShareDailog(webBean, mShareUrl, null, content);
                }
            });
        }
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
                    ArrayList<ImageData> commentShowList = VideoAndFileUtils.getDetailCommentShowList(itemBean.commenturl);
                    if (commentShowList != null && commentShowList.size() > 0) {
                        cover2 = commentShowList.get(0).url;
                    }
                    shareBeanByDetail = ShareHelper.getInstance().getShareBeanByDetail(bean, itemBean, cover2, shareUrl);
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
//                boolean isVideo = LikeAndUnlikeUtil.isVideoType(ugcBean.getContenttype());
//                String videoUrl = isVideo ? VideoAndFileUtils.getVideoUrl(ugcBean.getContenturllist()) : "";
                WebShareBean webBean = ShareHelper.getInstance().createWebBean(false,
                        false, null, null, ugcBean.getContentid());
                showShareDailog(webBean, mShareUrl, null, ugcBean);
            } else {
                WebShareBean webBean = ShareHelper.getInstance().createWebBean(false, false
                        , null, null, bean.commentid);
                showShareDailog(webBean, mCommentUrl, bean, null);
            }

        } else if (view.getId() == R.id.child_reply_layout) {
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

    /**
     * 给头布局视频类型的播放完成使用url
     *
     * @return
     */
    public String getShareUrl() {
        return mShareUrl;
    }


    @Override
    public boolean onBackPressed() {
        return Jzvd.backPress();
    }

    public void publishComment(CommendItemBean.RowsBean bean) {
        if (viewHolder != null) {
            viewHolder.commentPlus();
        }
        // TODO: 2018/11/17 还得处理边界状态,一开始是没有评论和已经有评论
        List<CommendItemBean.RowsBean> data = commentAdapter.getData();
        if (commentAdapter == null) return;
        //只有神评,有神评有其他评论,都没有,有神评没其他评论,只有其他评论 五种情况区分
        if (bestSize > 0) {
            //总数大于神评
            bean.showHeadr = true;
            if (data.size() > bestSize) {
                data.get(bestSize).showHeadr = false;
                commentAdapter.addData(bestSize, bean);
//                commentAdapter.notifyItemRangeChanged();
                commentAdapter.notifyDataSetChanged();
            } else {
                commentAdapter.addData(bean);
            }
        } else {
            if (data.size() > 0) {
                data.get(0).showHeadr = false;
            } else {
                adapter.setEnableLoadMore(false);
            }
            bean.showHeadr = true;
            bean.isShowFooterLine = true;
            commentAdapter.addData(0, bean);
            if (commentAdapter.getData().size() > 1) {
                commentAdapter.notifyDataSetChanged();
            }
        }
        MyApplication.getInstance().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                smoothMoveToPosition(mRvContent, bestSize + 1);
            }
        }, 500);
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


    //目标项是否在最后一个可见项之后
    private boolean mShouldScroll;
    //记录目标项位置
    private int mToPosition;

    /**
     * 滑动到指定位置
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前，使用smoothScrollToPosition
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后，最后一个可见项之前
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                // smoothScrollToPosition 不会有效果，此时调用smoothScrollBy来滑动到指定位置
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
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
                    }
                }).show();
    }
}
