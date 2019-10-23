package com.caotu.duanzhi.module.detail_scroll;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.EventBusObject;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.advertisement.IADView;
import com.caotu.duanzhi.config.EventBusCode;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.detail.ContentItemAdapter;
import com.caotu.duanzhi.module.detail.DetailCommentAdapter;
import com.caotu.duanzhi.module.detail.IVewPublishComment;
import com.caotu.duanzhi.module.detail.TextViewLongClick;
import com.caotu.duanzhi.module.holder.DetailHeaderViewHolder;
import com.caotu.duanzhi.module.holder.IHolder;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.other.HandleBackInterface;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.other.TextWatcherAdapter;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ParserUtils;
import com.caotu.duanzhi.utils.SoftKeyBoardListener;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.BaseDialogFragment;
import com.caotu.duanzhi.view.dialog.CommentActionDialog;
import com.caotu.duanzhi.view.dialog.ReportDialog;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.caotu.duanzhi.view.widget.AvatarWithNameLayout;
import com.caotu.duanzhi.view.widget.EditTextLib.SpXEditText;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.dialog.PictureDialog;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.qq.e.ads.nativ.NativeExpressADView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * 内容详情页面,包括头部和底部的控件处理.发布等事件统一处理
 */
public class BaseContentDetailFragment extends BaseStateFragment<CommendItemBean.RowsBean>
        implements BaseQuickAdapter.OnItemChildClickListener,
        BaseQuickAdapter.OnItemClickListener,
        HandleBackInterface,
        BaseQuickAdapter.OnItemLongClickListener,
        TextViewLongClick, View.OnClickListener, IVewPublishComment {
    public MomentsDataBean content;
    public String contentId;

    public SpXEditText mEtSendContent;
    //收藏和分享直接在fragment处理,头像和关注扔给holder处理
    private View bottomCollection, bottomShareView, titleBar;
    //这里负责定义
    public AvatarWithNameLayout avatarWithNameLayout;
    public TextView mUserIsFollow;

    private RelativeLayout mKeyboardShowRl;
    public DetailPresenter presenter;
    private RecyclerView recyclerView;

    protected TextView mTvClickSend, bottomLikeView, titleText;
    private MomentsDataBean ugc;

    public void setDate(MomentsDataBean bean) {
        content = bean;
        if (bean != null) {
            contentId = bean.getContentid();
        }
    }

    @Override
    public boolean getIsNeedIos() {
        return false;
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

    boolean isNeedScrollHeader = true;

    @Override
    protected void initView(View inflate) {
        inflate.findViewById(R.id.iv_back).setOnClickListener(this);
        mEtSendContent = inflate.findViewById(R.id.et_send_content);
        inflate.findViewById(R.id.iv_detail_photo1).setOnClickListener(this);
        inflate.findViewById(R.id.iv_detail_video1).setOnClickListener(this);
        inflate.findViewById(R.id.iv_detail_at).setOnClickListener(this);
        // TODO: 2019-07-30 这里要求做了特殊处理,如果是自己的帖子或者内容不做联动的标题栏处理
        View moreView = inflate.findViewById(R.id.iv_more_bt);
        if (content == null || MySpUtils.isMe(content.getContentuid())) {
            moreView.setVisibility(View.INVISIBLE);
            isNeedScrollHeader = false;
        } else {
            moreView.setVisibility(View.VISIBLE);
        }
        moreView.setOnClickListener(this);
        bottomLikeView = inflate.findViewById(R.id.bottom_tv_like);
        bottomLikeView.setOnClickListener(this);
        bottomCollection = inflate.findViewById(R.id.bottom_iv_collection);
        bottomCollection.setOnClickListener(this);
        if (content != null) {
            bottomCollection.setSelected(LikeAndUnlikeUtil.isLiked(content.getIscollection()));
        }
        bottomShareView = inflate.findViewById(R.id.bottom_iv_share);
        bottomShareView.setOnClickListener(this);
        mTvClickSend = inflate.findViewById(R.id.tv_click_send);
        mTvClickSend.setOnClickListener(this);
        mKeyboardShowRl = inflate.findViewById(R.id.keyboard_show_rl);
        recyclerView = inflate.findViewById(R.id.publish_rv);
// 这个还有一坨滑动置顶的用户操作需要处理,同CommentNewFragment
//        mIvUserAvatar = inflate.findViewById(R.id.iv_user_avatar);
//        mUserName = inflate.findViewById(R.id.tv_topic_name);
//        mUserIsFollow = inflate.findViewById(R.id.tv_user_follow);
//        userHeader = inflate.findViewById(R.id.iv_user_headgear);

        titleBar = inflate.findViewById(R.id.group_title_bar);
        titleText = inflate.findViewById(R.id.tv_title_big);
        mEtSendContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0 && !mTvClickSend.isEnabled()) {
                    mTvClickSend.setEnabled(true);
                } else if (s.toString().trim().length() == 0
                        && (selectList == null || selectList.size() == 0)) {
                    mTvClickSend.setEnabled(false);
                }
            }
        });

        //这个需要注意顺序
        super.initView(inflate);
        setKeyBoardListener();
    }

    private void setKeyBoardListener() {
        View keyboardView = rootView.findViewById(R.id.view_keyboard_hide);
        keyboardView.setOnClickListener(v -> closeSoftKeyboard(mEtSendContent));
        SoftKeyBoardListener.setListener(getActivity(), new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                keyboardView.setVisibility(View.VISIBLE);
                bottomLikeView.setVisibility(View.GONE);
                bottomCollection.setVisibility(View.GONE);
                bottomShareView.setVisibility(View.GONE);
                mKeyboardShowRl.setVisibility(View.VISIBLE);
                mEtSendContent.setMaxLines(4);
            }

            @Override
            public void keyBoardHide() {
                keyboardView.setVisibility(View.GONE);
                bottomLikeView.setVisibility(View.VISIBLE);
                bottomCollection.setVisibility(View.VISIBLE);
                bottomShareView.setVisibility(View.VISIBLE);
                mKeyboardShowRl.setVisibility(View.GONE);
                mEtSendContent.setMaxLines(1);
            }
        });
    }

    private int mScrollY = 0;
    private int headerHeight = 200;

    @Override
    protected void initViewListener() {
        initHeader();
        adapter.disableLoadMoreIfNotFullPage();
        if (!isNeedScrollHeader) return;
        mRvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                mScrollY += dy;
                float scrollY = Math.min(headerHeight, mScrollY);
                if (scrollY >= headerHeight) {
                    titleBar.setVisibility(View.VISIBLE);
                    titleText.setVisibility(View.GONE);
                } else if (scrollY <= 5) {
                    titleBar.setVisibility(View.GONE);
                    titleText.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    public IHolder<MomentsDataBean> viewHolder;

    protected void initHeader() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        getPresenter();
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.layout_content_detail_header, mRvContent, false);
        if (viewHolder == null) {
            viewHolder = new DetailHeaderViewHolder(headerView);
            viewHolder.bindFragment(this);
        }
        //设置头布局
        adapter.setHeaderView(headerView);
        adapter.setHeaderAndEmpty(true);
        //因为功能相同,所以就统一都由头holder处理得了,分离代码
        avatarWithNameLayout = headerView.findViewById(R.id.group_user_avatar);
        mUserIsFollow = headerView.findViewById(R.id.iv_is_follow);
        viewHolder.bindSameView(avatarWithNameLayout, mUserIsFollow, bottomLikeView);
        initAdView(headerView);
        if (content == null) return;
        viewHolder.bindDate(content);

    }

    View adViewParent;
    FrameLayout adGroup;

    public void initAdView(View headerView) {
        if (getActivity() instanceof IADView) {
            NativeExpressADView adView = ((IADView) getActivity()).getAdView();
            if (adView == null) return;
            adViewParent = headerView.findViewById(R.id.ll_ad_parent);
            adViewParent.setVisibility(View.VISIBLE);
            adGroup = headerView.findViewById(R.id.detail_header_ad);
            adGroup.removeAllViews();
            adGroup.addView(adView);
        }
    }

    //加载成功后就不在添加,防止获取新广告的时候自动刷新替换当前页的广告,无所谓的话可以去掉该标志位
    boolean isHeadSuccess = false;

    public void refreshAdView(NativeExpressADView view) {
        if (view == null || adViewParent == null || isHeadSuccess) return;
        adViewParent.setVisibility(View.VISIBLE);
        adGroup.removeAllViews();
        adGroup.addView(view);
        isHeadSuccess = true;
    }

    boolean commentListSuccess = false;

    public void refreshCommentListAd(NativeExpressADView view) {
        if (view == null || commentListSuccess || adapter == null) return;
        List<CommendItemBean.RowsBean> data = adapter.getData();
        if (AppUtil.listHasDate(data) && data.size() >= 5) {
            CommendItemBean.RowsBean rowsBean = new CommendItemBean.RowsBean();
            //赋值操作
            adapter.addData(4, rowsBean);
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

    /**
     * 评论可能获取不满20条,加载更多的逻辑就错了,适当放小只能,啥子接口哦写的
     *
     * @return
     */
    public int getPageSize() {
        return 10;
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

    /**
     * 集合处理完毕后的回调
     *
     * @param listDate
     * @param load_more
     */
    @Override
    public void setListDate(List<CommendItemBean.RowsBean> listDate, int load_more) {
        setDate(load_more, listDate);
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
                showShareDailog(webBean, CommonHttpRequest.url, null, ugc);
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
            // TODO: 2019-07-31 这里注意下,之前是UGC打开内容bean的评论详情页面,现在都长一样了就直接打开内容详情得了
            //这个只是自己臆想,可能还要改回去,因为这样可以去掉ugc那个鬼东西,可以删ugc的特殊处理代码,
            // 区别就是内容下面的列表可以跳转到评论详情,评论点击只能回复评论,没有进一步的跳转了
        } else if (view.getId() == R.id.child_reply_layout) {
            if (bean.isUgc && ugc != null) {
                HelperForStartActivity.openContentDetail(ugc);
            } else {
                HelperForStartActivity.openCommentDetail(bean);
            }
        } else if (view.getId() == R.id.expand_text_view) {
            if (bean.isUgc && ugc != null) {
                HelperForStartActivity.openContentDetail(ugc);
            } else {
                HelperForStartActivity.openCommentDetail(bean);
            }
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
            try {
                adapter.addData(0, bean);
                mRvContent.postDelayed(() -> smoothMoveToPosition(1, true), 200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
                } else {
                    CommonHttpRequest.getInstance().deleteComment(bean.commentid);
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

    //目前有:纯图片,纯视频,纯文字,视频加文字,图片加文字
    //       1     2     3       4        5
    private int publishType = -1;

    public void getPicture() {
        UmengHelper.event(UmengStatisticsKeyIds.reply_image);
        getPresenter().getPicture();
    }

    private void getVideo() {
        UmengHelper.event(UmengStatisticsKeyIds.reply_video);
        getPresenter().getVideo();
    }

    private List<LocalMedia> selectList = new ArrayList<>();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.iv_more_bt:
                showReportDialog(contentId, 0);
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
                showShareDailog(webBean, CommonHttpRequest.url, null, content);
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
            case R.id.iv_detail_photo1:
                if (selectList.size() != 0 && publishType == 2) {
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setMessage("若你要添加图片，已选视频将从发表界面中清除了？")
                            .setPositiveButton(android.R.string.ok, (dialog13, which) -> {
                                dialog13.dismiss();
                                selectList.clear();
                                recyclerView.setVisibility(View.GONE);
                                getPicture();
                            })
                            .setNegativeButton(android.R.string.cancel, (dialog14, which) -> dialog14.dismiss()).create();
                    dialog.show();
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(DevicesUtils.getColor(R.color.color_FF8787));
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                } else {
                    getPicture();
                }

                break;
            case R.id.iv_detail_video1:
                if (selectList.size() != 0 && publishType == 1) {
                    AlertDialog dialog = new AlertDialog.Builder(getContext()).setMessage("若你要添加视频，已选图片将从发表界面中清除了？")
                            .setPositiveButton(android.R.string.ok, (dialog12, which) -> {
                                dialog12.dismiss();
                                selectList.clear();
                                recyclerView.setVisibility(View.GONE);
                                getVideo();
                            })
                            .setNegativeButton(android.R.string.cancel, (dialog1, which) -> dialog1.dismiss())
                            .create();

                    dialog.show();
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(DevicesUtils.getColor(R.color.color_FF8787));
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

                } else {
                    getVideo();
                }

                break;
            case R.id.tv_click_send:
                UmengHelper.event(UmengStatisticsKeyIds.comment_publish);
                //为了防止已经在发布内容视频,再在评论里发布视频处理不过来
                Activity lastSecondActivity = MyApplication.getInstance().getLastSecondActivity();
                if (lastSecondActivity instanceof MainActivity) {
                    boolean publishing = ((MainActivity) lastSecondActivity).isPublishing();
                    if (publishing) {
                        ToastUtil.showShort("正在发布内容中,请稍后再试");
                        return;
                    }
                }

                if (LoginHelp.isLoginAndSkipLogin()) {
                    presenter.publishBtClick();
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                break;
            case R.id.iv_detail_at:
                UmengHelper.event(UmengStatisticsKeyIds.comments_at);
                HelperForStartActivity.openSearch();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.REQUEST_VIDEO:
                    publishType = 2;
                    selectList = PictureSelector.obtainMultipleResult(data);
                    presenter.setMediaList(selectList);
                    presenter.setIsVideo(true);
                    showRV();
                    break;
                case PictureConfig.REQUEST_PICTURE:
                    publishType = 1;
                    selectList = PictureSelector.obtainMultipleResult(data);
                    presenter.setMediaList(selectList);
                    presenter.setIsVideo(false);
                    showRV();
                    break;
                case HelperForStartActivity.at_user_requestCode:
                    UserBean extra = data.getParcelableExtra(HelperForStartActivity.KEY_AT_USER);
                    if (extra != null) {
                        mEtSendContent.addSpan(extra);
                    }
                    break;
            }
        }
    }

    private ContentItemAdapter dateAdapter;

    private void showRV() {
        mTvClickSend.setEnabled(true);
        if (recyclerView != null && recyclerView.getVisibility() != View.VISIBLE) {
            recyclerView.setVisibility(View.VISIBLE);
        }

        if (dateAdapter == null) {
            dateAdapter = new ContentItemAdapter();
            dateAdapter.setOnItemChildClickListener((adapter, view, position) -> {
                adapter.remove(position);
                if (adapter.getData().size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    if (TextUtils.isEmpty(mEtSendContent.getText().toString().trim())) {
                        mTvClickSend.setEnabled(false);
                    }
                }
                getPresenter().setMediaList(adapter.getData());
            });
            recyclerView.setAdapter(dateAdapter);
        }
        dateAdapter.setNewData(selectList);
        recyclerView.postDelayed(() -> showKeyboard(mEtSendContent), 200);
    }

    ProgressDialog dialog;

    @Override
    public void publishError() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        mTvClickSend.setEnabled(false);
        getPresenter().clearSelectList();
        selectList.clear();
        recyclerView.setVisibility(View.GONE);
        ToastUtil.showShort("发布失败");
        closeSoftKeyboard(mEtSendContent);
    }

    @Override
    public void endPublish(CommendItemBean.RowsBean bean) {
        UmengHelper.event(UmengStatisticsKeyIds.comment_success);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        ToastUtil.showShort("发射成功");
        mTvClickSend.setEnabled(false);
        getPresenter().clearSelectList();
        selectList.clear();
        recyclerView.setVisibility(View.GONE);
        publishComment(bean);
        closeSoftKeyboard(mEtSendContent);
        mEtSendContent.setText("");
    }

    @Override
    public void publishCantTalk(String msg) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        getPresenter().clearSelectList();
        selectList.clear();
        recyclerView.setVisibility(View.GONE);
        ToastUtil.showShort(msg);
        closeSoftKeyboard(mEtSendContent);
    }

    @Override
    public void uploadProgress(int progress) {
        if (dialog != null && dialog.isShowing()) {
            dialog.setProgress(progress);
        }
    }


    @Override
    public EditText getEditView() {
        return mEtSendContent;
    }

    @Override
    public View getPublishView() {
        return mTvClickSend;
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
        if (mp4Dialog != null && mp4Dialog.isShowing()) {
            mp4Dialog.dismiss();
        }
        mTvClickSend.setEnabled(false);
        dialog.show();
        closeSoftKeyboard(mEtSendContent);
    }

    PictureDialog mp4Dialog;

    @Override
    public void notMp4() {
        if (mp4Dialog == null) {
            mp4Dialog = new PictureDialog(getContext());
            mp4Dialog.setCanceledOnTouchOutside(false);
            mp4Dialog.setCancelable(false);
        }
        mTvClickSend.setEnabled(false);
        mp4Dialog.show();
        closeSoftKeyboard(mEtSendContent);
    }
}
