package com.caotu.duanzhi.module.detail_scroll;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.caotu.adlib.AdHelper;
import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.EventBusObject;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.advertisement.IADView;
import com.caotu.duanzhi.config.EventBusCode;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.detail.DetailCommentAdapter;
import com.caotu.duanzhi.module.detail.IVewPublishComment;
import com.caotu.duanzhi.module.holder.DetailHeaderViewHolder;
import com.caotu.duanzhi.module.holder.IHolder;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.module.publish.IViewDetail;
import com.caotu.duanzhi.other.HandleBackInterface;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.DevicesUtils;
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
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 内容详情页面,包括头部和底部的控件处理.发布等事件统一处理
 * <p>
 * 键盘切换 https://github.com/Jacksgong/JKeyboardPanelSwitch
 */
public class BaseContentDetailFragment extends BaseStateFragment<CommendItemBean.RowsBean>
        implements BaseQuickAdapter.OnItemChildClickListener,
        BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemLongClickListener,
        HandleBackInterface,IVewPublishComment, IViewDetail, View.OnClickListener {
    public MomentsDataBean content;
    public String contentId;

    //这里负责定义
    public AvatarWithNameLayout avatarWithNameLayout;
    public DetailPresenter presenter;
    protected TextView mUserIsFollow, bottomLikeView;
    private MomentsDataBean ugc;
    private View bottomCollection;

    public void setDate(MomentsDataBean bean) {
        content = bean;
        if (bean != null) {
            contentId = bean.getContentid();
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_not_video_detail_layout;
    }

    public DetailPresenter getPresenter() {
        if (presenter == null) {
            presenter = new DetailPresenter(this, content);
        }
        return presenter;
    }

    @Override
    protected void initView(View inflate) {
        super.initView(inflate);
        View backIv = inflate.findViewById(R.id.iv_back);
        backIv.setOnClickListener(this);
        ImageView moreView = inflate.findViewById(R.id.iv_more_bt);
        if (content == null || MySpUtils.isMe(content.getContentuid())) {
            moreView.setVisibility(View.INVISIBLE);
        } else {
            moreView.setVisibility(View.VISIBLE);
            moreView.setColorFilter(DevicesUtils.getColor(R.color.color_FF698F));
        }
        moreView.setOnClickListener(this);
        bottomLikeView = inflate.findViewById(R.id.bottom_tv_like);
        bottomLikeView.setOnClickListener(this);
        bottomCollection = inflate.findViewById(R.id.bottom_iv_collection);
        bottomCollection.setOnClickListener(this);
        if (content != null) {
            bottomCollection.setSelected(LikeAndUnlikeUtil.isLiked(content.getIscollection()));
        }
        inflate.findViewById(R.id.bottom_iv_share).setOnClickListener(this);
        ReplyTextView replyTextView = inflate.findViewById(R.id.tv_send_content);
        replyTextView.setListener(this::showPopFg);

    }


    @Override
    protected void initViewListener() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        getPresenter();
        initHeader();
    }

    public void initHeader() {
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.layout_content_detail_header, mRvContent, false);
        if (viewHolder == null) {
            viewHolder = new DetailHeaderViewHolder(headerView);
            viewHolder.bindFragment(this);
        }
        //设置头布局
        adapter.setHeaderView(headerView);
        //因为功能相同,所以就统一都由头holder处理得了,分离代码
        avatarWithNameLayout = headerView.findViewById(R.id.group_user_avatar);
        mUserIsFollow = headerView.findViewById(R.id.iv_is_follow);
        viewHolder.bindSameView(avatarWithNameLayout, mUserIsFollow, bottomLikeView);
        if (content == null) return;
        viewHolder.bindDate(content);
    }

    public IHolder<MomentsDataBean> viewHolder;


    @Override
    protected BaseQuickAdapter getAdapter() {
        if (adapter == null) {
            adapter = new DetailCommentAdapter();
            adapter.setOnItemChildClickListener(this);
            adapter.setOnItemClickListener(this);
            adapter.setOnItemLongClickListener(this);
            adapter.setHeaderAndEmpty(true);
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


    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void getEventBus(EventBusObject eventBusObject) {
        if (EventBusCode.COMMENT_CHANGE == eventBusObject.getCode()) {
            if (getActivity() == null || !TextUtils.equals(getActivity().getLocalClassName(), eventBusObject.getTag()))
                return;
            CommendItemBean.RowsBean bean = (CommendItemBean.RowsBean) eventBusObject.getObj();
            if (adapter != null) {
                int position = 1; //因为详情有头布局
                List<CommendItemBean.RowsBean> beanList = adapter.getData();
                for (int i = 0; i < beanList.size(); i++) {
                    String commentid = beanList.get(i).commentid;
                    if (TextUtils.equals(bean.commentid, commentid)) {
                        position += i;
                        CommendItemBean.RowsBean rowsBean = beanList.get(i);
                        rowsBean.goodstatus = bean.goodstatus;
                        rowsBean.commentgood = bean.commentgood;
                        break;
                    }
                }
                adapter.notifyItemChanged(position);
            }
        }
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
                        //为了跳转使用
                        ugc = response.body().getData().getUgc();
                        getPresenter().dealDateList(bestlist, rows, ugc, load_more);

                    }

                    @Override
                    public void onError(Response<BaseResponseBean<CommendItemBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        OkGo.getInstance().cancelTag(this);
    }

    /**
     * @param shareBean
     * @param shareUrl
     * @param itemBean
     * @param momentsDataBean 区分ugc的分享
     */
    public void showShareDialog(WebShareBean shareBean, String shareUrl, CommendItemBean.RowsBean itemBean, MomentsDataBean momentsDataBean) {
        ShareDialog dialog = ShareDialog.newInstance(shareBean);
        dialog.setListener(new ShareDialog.ShareMediaCallBack() {
            @Override
            public void callback(WebShareBean bean) {
                //该对象已经含有平台参数
                WebShareBean shareBeanByDetail;
                if (momentsDataBean != null) {
                    shareBeanByDetail = ShareHelper.getInstance().getShareBeanByDetail(bean, momentsDataBean, shareUrl);
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
                if (content == null) return;
                content.setIscollection(isCollection ? "1" : "0");
                bottomCollection.setSelected(isCollection);
                ToastUtil.showShort(isCollection ? "收藏成功" : "取消收藏成功");
            }
        });
        dialog.show(getChildFragmentManager(), "share");
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
            if (bean.isUgc && ugc != null) {
                boolean isVideo = LikeAndUnlikeUtil.isVideoType(ugc.getContenttype());
                String videoUrl = isVideo ? VideoAndFileUtils.getVideoUrl(ugc.getContenturllist()) : "";
                WebShareBean webBean = ShareHelper.getInstance().createWebBean(isVideo,
                        false, null, videoUrl, ugc.getContentid());
                showShareDialog(webBean, CommonHttpRequest.url, null, ugc);
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
                showShareDialog(webBean, CommonHttpRequest.cmt_url, bean, null);
            }
            // TODO: 2019-07-31 这里注意下,之前是UGC打开内容bean的评论详情页面,现在都长一样了就直接打开内容详情得了
            //这个只是自己臆想,可能还要改回去,因为这样可以去掉ugc那个鬼东西,可以删ugc的特殊处理代码,
            // 区别就是内容下面的列表可以跳转到评论详情,评论点击只能回复评论,没有进一步的跳转了
        } else if (view.getId() == R.id.child_reply_layout) {
            if (bean.isUgc && ugc != null) {
                HelperForStartActivity.openContentDetail(ugc);
            } else {
                HelperForStartActivity.openCommentDetail(bean);
            }
        } else if (view.getId() == R.id.group_user_avatar) {
            HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, bean.userid);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        CommendItemBean.RowsBean bean = (CommendItemBean.RowsBean) adapter.getData().get(position);
        // TODO: 2018/11/15 如果是ugc跳转虽然是评论详情,但是接口请求还是内容详情接口
        if (bean.isUgc && ugc != null) {
            HelperForStartActivity.openContentDetail(ugc);
        } else {
            HelperForStartActivity.openCommentDetail(bean);
        }
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
                } else {
                    CommonHttpRequest.getInstance().deleteComment(bean.commentid, null);
                }
                adapter.remove(position);
                //通知列表更新条目
                viewHolder.commentMinus();
            }

            @Override
            public void report() {
                if (bean.isUgc) {
                    showReportDialog(bean.contentid, 0);
                } else {
                    showReportDialog(bean.commentid, 1);
                }
            }
        }, MySpUtils.isMe(bean.userid), isHastitle ?
                ParserUtils.htmlToJustAtText(bean.commenttext) : null);
        dialog.show(getChildFragmentManager(), "dialog");
        //false 没有整栋效果,true 会有震动反馈的效果
        return true;
    }


    /**
     * @param id
     * @param type 0 代表内容举报,1 是评论举报
     */
    private void showReportDialog(String id, int type) {
        ReportDialog dialog = new ReportDialog(getContext());
        dialog.setIdAndType(id, type);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.iv_more_bt:
                if (LoginHelp.isLoginAndSkipLogin()) {
                    showReportDialog(contentId, 0);
                }
                break;
            case R.id.bottom_iv_share:
                if (content == null) return;
                String copyText = null;
                if ("1".equals(content.getIsshowtitle()) && !TextUtils.isEmpty(content.getContenttitle())) {
                    copyText = content.getContenttitle();
                }
                WebShareBean webBean = ShareHelper.getInstance().createWebBean(viewHolder.isVideo()
                        , content == null ? "0" : content.getIscollection(), viewHolder.getVideoUrl(),
                        content.getContentid(), copyText);
                showShareDialog(webBean, CommonHttpRequest.url, null, content);
                break;
            case R.id.bottom_iv_collection:
                if (content == null) return;
                boolean isCollection = !bottomCollection.isSelected();
                if (isCollection) {
                    UmengHelper.event(UmengStatisticsKeyIds.collection);
                }
                if (LoginHelp.isLoginAndSkipLogin() && !TextUtils.isEmpty(contentId)) {
                    CommonHttpRequest.getInstance().collectionContent(contentId, isCollection, new JsonCallback<BaseResponseBean<String>>() {
                        @Override
                        public void onSuccess(Response<BaseResponseBean<String>> response) {
                            content.setIscollection(isCollection ? "1" : "0");
                            ToastUtil.showShort(isCollection ? "收藏成功" : "取消收藏成功");
                            bottomCollection.setSelected(isCollection);
                        }
                    });
                }
                break;
            case R.id.iv_back:
                if (getActivity() != null) {
                    getActivity().finish();
                }
                break;
        }
    }

    ProgressDialog upLoadDialog;

    @Override
    public void publishError() {
        if (upLoadDialog != null) {
            upLoadDialog.dismiss();
        }
        ToastUtil.showShort("发布失败");
        detailPop.dismiss();
    }

    @Override
    public void endPublish(CommendItemBean.RowsBean bean) {
        UmengHelper.event(UmengStatisticsKeyIds.comment_success);
        if (upLoadDialog != null) {
            upLoadDialog.dismiss();
        }
        ToastUtil.showShort("发射成功");
        publishComment(bean);
        detailPop.dismissByClearDate();
    }

    @Override
    public void publishCantTalk(String msg) {
        if (upLoadDialog != null) {
            upLoadDialog.dismiss();
        }
        ToastUtil.showShort(msg);
        detailPop.dismiss();
    }

    @Override
    public void uploadProgress(int progress) {
        if (upLoadDialog != null) {
            upLoadDialog.setProgress(progress);
        }
    }


    @Override
    public void startPublish() {
        if (getActivity() == null) return;
        if (upLoadDialog == null) {
            upLoadDialog = new ProgressDialog(getContext());
            upLoadDialog.setMax(100);
            upLoadDialog.setCancelable(false);
            upLoadDialog.setMessage("预备发射中...");
            upLoadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }
        upLoadDialog.show();
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

    public void showPopFg(boolean isShowListStr) {
        Activity activity = MyApplication.getInstance().getRunningActivity();
        if (detailPop == null) {
            detailPop = new ReplyDialog(activity, BaseContentDetailFragment.this);
        }
        detailPop.setParams(isShowListStr, null);
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


    /**********************************广告区***************************************/

    /**
     * 集合处理完毕后的回调
     *
     * @param listDate
     * @param load_more
     */
    @Override
    public void setListDate(List<CommendItemBean.RowsBean> listDate, int load_more) {
        for (CommendItemBean.RowsBean rowsBean : listDate) {
            if (TextUtils.equals("6", rowsBean.commenttype) && getActivity() instanceof IADView) {
                View commentAdView = ((IADView) getActivity()).getCommentAdView();
                isCommentAdSuccess = (commentAdView != null);
                rowsBean.adView = commentAdView;
            }
        }
        setDate(load_more, listDate);
    }

    //该标志位只能是拿到接口数据后处理的时候拿广告位才能知道是否成功
    boolean isCommentAdSuccess;
    boolean isHeaderAdSuccess;

    /**
     * 该逻辑就是判断当前fragment 是不是viewpager 中第一个,第一个的话延迟拿广告,省的回调了
     * 如果不是第一个那基本广告肯定是拿到了,就不用延迟了  其实弊端就是网络卡的时候
     */
    @Override
    public void onResume() {
        super.onResume();
        if (isHeaderAdSuccess || getActivity() == null) return;
        boolean isNeedDelay = false;
        if (getActivity() instanceof ContentNewDetailActivity
                && ((ContentNewDetailActivity) getActivity()).getIndex() == 0) {
            isNeedDelay = true;
        }
        bottomLikeView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    View adView = ((IADView) getActivity()).getAdView();
                    dealHeaderAd(adView);
                }
            }
        }, isNeedDelay ? 800 : 10);
    }

    public void dealHeaderAd(View adView) {
        if (isHeaderAdSuccess || adapter == null || adView == null) return;
        FrameLayout adGroup = adapter.getHeaderLayout().findViewById(R.id.header_ad);
        AdHelper.getInstance().showAD(adView, adGroup);
        isHeaderAdSuccess = true;
    }

    /**
     * 初始化广告获取时候的回调方法,因为是异步不确保广告能直接拿到
     *
     * @param adView
     */
    public void refreshCommentListAd(View adView) {
        if (adapter == null || isCommentAdSuccess) return;
        List<CommendItemBean.RowsBean> rowsBeans = adapter.getData();
        if (!AppUtil.listHasDate(rowsBeans)) return;
        for (CommendItemBean.RowsBean rowsBean : rowsBeans) {
            if (TextUtils.equals("6", rowsBean.commenttype)) {
                rowsBean.adView = adView;
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }


    public void removeAd() {
        FrameLayout adGroup = adapter.getHeaderLayout().findViewById(R.id.header_ad);
        if (adGroup == null) return;
        adGroup.removeAllViews();
        adGroup.setVisibility(View.GONE);
    }
}
