package com.caotu.duanzhi.module.home;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.other.HandleBackInterface;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import cn.jzvd.Jzvd;

/**
 * ugc内容详情和真正的内容详情公用一个页面
 */
public class ContentDetailFragment extends BaseStateFragment<CommendItemBean.RowsBean> implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener, HandleBackInterface {
    public MomentsDataBean content;
    public String mShareUrl;
    protected String contentId;
    protected boolean isComment;
    protected DetailCommentAdapter commentAdapter;
    protected LinearLayoutManager layoutManager;


    @Override
    protected BaseQuickAdapter getAdapter() {
        if (commentAdapter == null) {
            commentAdapter = new DetailCommentAdapter();
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
        initHeader();
        CommonHttpRequest.getInstance().getShareUrl(contentId, new JsonCallback<BaseResponseBean<ShareUrlBean>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<ShareUrlBean>> response) {
                mShareUrl = response.body().getData().getUrl();
            }
        });

        layoutManager = (LinearLayoutManager) mRvContent.getLayoutManager();
        mRvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                //第一条可见条目不是1则说明划出屏幕
                if (firstVisibleItem == 1 && viewHolder.isVideo()) {
                    MyVideoPlayerStandard videoView = viewHolder.getVideoView();
                    //这个是判断暂停状态的时候不启动悬浮窗模式
                    if (videoView.currentState == Jzvd.CURRENT_STATE_PAUSE) {
                        Jzvd.releaseAllVideos();
                    } else {
                        isTiny = true;
                        videoView.startWindowTiny(viewHolder.isLandscape());
                    }
                } else if (firstVisibleItem == 0 && viewHolder.isVideo()) {
                    if (!isTiny) return;
                    Jzvd.backPress();
                    isTiny = false;
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
        CommendItemBean.RowsBean ugcBean = null;
        if (ugc != null) {
            ugcBean = DataTransformUtils.changeUgcBean(ugc);
        }
        //这里只处理初始化和刷新,加载更多直接忽略神评和ugc
        if (DateState.load_more != load_more) {
            if (bestlist != null && bestlist.size() > 0) {
                for (int i = 0; i < bestlist.size(); i++) {
                    bestlist.get(i).isBest = true;
                    if (i == 0) {
                        bestlist.get(i).showHeadr = true;
                    }
                }
                bestSize = bestlist.size();
            }
            // TODO: 2018/11/15 ugc 内容展示到最新评论里
            if (rows != null && rows.size() > 0) {
                if (ugcBean != null && rows.size() >= 3) {
                    rows.add(2, ugcBean);
                } else if (ugc != null && rows.size() <= 2) {
                    rows.add(ugcBean);
                }
                rows.get(0).showHeadr = true;
                if (bestlist != null && bestlist.size() > 0) {
                    rows.addAll(0, bestlist);
                }
            }
        }
        setDate(load_more, rows);
    }

    public void bindHeader(MomentsDataBean data) {
        if (data == null) {
            HashMap<String, String> hashMapParams = new HashMap<>();
            hashMapParams.put("contentid", contentId);
            OkGo.<BaseResponseBean<MomentsDataBean>>post(HttpApi.DETAILID)
                    .upJson(new JSONObject(hashMapParams))
                    .execute(new JsonCallback<BaseResponseBean<MomentsDataBean>>() {
                        @Override
                        public void onSuccess(Response<BaseResponseBean<MomentsDataBean>> response) {
                            MomentsDataBean data = response.body().getData();
                            viewHolder.bindDate(data);
                        }

                        @Override
                        public void onError(Response<BaseResponseBean<MomentsDataBean>> response) {
                            errorLoad();
                            super.onError(response);
                        }
                    });
            return;
        }
        viewHolder.bindDate(data);
    }

    public void setDate(MomentsDataBean bean, boolean iscomment) {
        content = bean;
        isComment = iscomment;
        contentId = bean.getContentid();
    }

    // TODO: 2018/11/20 这里就要用到面向接口编程,viewHolder这里写死了
    protected  IHolder viewHolder;

    public void initHeaderView(View view) {
        if (viewHolder == null) {
            viewHolder = new DetailHeaderViewHolder(this, view);
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

    public void showShareDailog(WebShareBean shareBean, String shareUrl) {
        ShareDialog dialog = ShareDialog.newInstance(shareBean);
        dialog.setListener(new ShareDialog.ShareMediaCallBack() {
            @Override
            public void callback(WebShareBean bean) {
                //该对象已经含有平台参数
                String cover = viewHolder.getCover();
                WebShareBean shareBeanByDetail = ShareHelper.getInstance().getShareBeanByDetail(bean, content, cover, shareUrl);
                ShareHelper.getInstance().shareWeb(shareBeanByDetail);
            }

            @Override
            public void colloection(boolean isCollection) {
                // TODO: 2018/11/16 可能还需要回调给列表
                content.setIscollection(isCollection ? "1" : "0");
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
                // TODO: 2018/11/20 如果是UGC内容,分享的URL也要重新获取
                CommonHttpRequest.getInstance().getShareUrl(contentId, new JsonCallback<BaseResponseBean<ShareUrlBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<ShareUrlBean>> response) {
                        String shareUrl = response.body().getData().getUrl();
                        WebShareBean webBean = ShareHelper.getInstance().createWebBean(isVideo,
                                true, ugcBean.getIscollection(), videoUrl, ugcBean.getContentid());
                        showShareDailog(webBean, shareUrl);
                    }
                });

            } else {
                WebShareBean webBean = ShareHelper.getInstance().createWebBean(false, false
                        , null, null, bean.contentid);
                showShareDailog(webBean, mShareUrl);
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

        if (bestSize > 0) {
            data.get(bestSize).showHeadr = false;
            bean.showHeadr = true;
            commentAdapter.addData(bestSize, bean);
        } else {
            if (data.size() > 0) {
                data.get(0).showHeadr = false;
            }
            bean.showHeadr = true;
            commentAdapter.addData(0, bean);
        }
    }
}
