package com.caotu.duanzhi.module.detail_scroll;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.EventBusObject;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.EventBusCode;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.detail.ContentItemAdapter;
import com.caotu.duanzhi.module.detail.DetailCommentAdapter;
import com.caotu.duanzhi.module.detail.DetailHeaderViewHolder;
import com.caotu.duanzhi.module.detail.IHolder;
import com.caotu.duanzhi.module.detail.IVewPublishComment;
import com.caotu.duanzhi.module.detail.TextViewLongClick;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.other.HandleBackInterface;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.other.TextWatcherAdapter;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.SoftKeyBoardListener;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.BaseDialogFragment;
import com.caotu.duanzhi.view.dialog.CommentActionDialog;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dueeeke.videoplayer.player.BaseIjkVideoView;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.dueeeke.videoplayer.playerui.StandardVideoController;
import com.dueeeke.videoplayer.smallwindow.FloatController;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.dialog.PictureDialog;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.REditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * 内容详情页面,底部的发布也得融合进来
 */
public class ContentScrollDetailFragment extends BaseStateFragment<CommendItemBean.RowsBean>
        implements BaseQuickAdapter.OnItemChildClickListener,
        BaseQuickAdapter.OnItemClickListener,
        HandleBackInterface,
        BaseQuickAdapter.OnItemLongClickListener,
        TextViewLongClick, View.OnClickListener, IVewPublishComment {
    public MomentsDataBean content;
    public String contentId;

    public REditText mEtSendContent;
    private View bottomCollection, bottomShareView, titleBar;
    private RelativeLayout mKeyboardShowRl;
    public DetailPresenter presenter;
    private RecyclerView recyclerView;

    private TextView mUserName, mTvClickSend, mUserIsFollow, bottomLikeView, titleText;
    private ImageView mIvUserAvatar;
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

        mIvUserAvatar = inflate.findViewById(R.id.iv_user_avatar);
        mUserName = inflate.findViewById(R.id.tv_topic_name);
        mUserIsFollow = inflate.findViewById(R.id.tv_user_follow);
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
    }

    private int mScrollY = 0;
    private int headerHeight = 200;
    protected LinearLayoutManager layoutManager;
    private FloatController mFloatController;
    protected int firstVisibleItem = -1;

    @Override
    protected void initViewListener() {
        setKeyBoardListener();
        initHeader();
        layoutManager = (LinearLayoutManager) mRvContent.getLayoutManager();
        mFloatController = new FloatController(mRvContent.getContext());
        adapter.disableLoadMoreIfNotFullPage();
        mRvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isNeedScrollHeader) {
                    mScrollY += dy;
                    float scrollY = Math.min(headerHeight, mScrollY);
                    if (scrollY >= headerHeight) {
                        titleBar.setVisibility(View.VISIBLE);
                        titleText.setVisibility(View.GONE);
                    } else if (scrollY <= 5) {
                        titleBar.setVisibility(View.GONE);
                        titleText.setVisibility(View.VISIBLE);
                    }
                    Log.i("mScrollY", "onScrolled: " + mScrollY);
                }

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
        viewHolder.bindSameView(mUserName, mIvUserAvatar, mUserIsFollow, bottomLikeView);
        if (content == null) return;
        viewHolder.bindDate(content);
        playVideo(true);
    }

    /**
     * 这里是为了可见性的回调比较早,初始化走得慢所以会有两套播放判断,一打开详情第一个播放
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            playVideo(true);
        } else {
            VideoViewManager.instance().stopPlayback();
            if (viewHolder == null) return;
            IjkVideoView mIjkVideoView = viewHolder.getVideoView();
            if (viewHolder.isVideo() && mIjkVideoView != null) {
                if (mIjkVideoView.getCurrentPlayerState() == BaseIjkVideoView.PLAYER_TINY_SCREEN) {
                    mIjkVideoView.stopTinyScreen();
                }
            }
        }
    }

    public void playVideo(boolean isPlay) {
        if (viewHolder == null) return;
        if (isPlay && isVisibleToUser) {
            viewHolder.autoPlayVideo();
        }
    }

    private void setKeyBoardListener() {
        SoftKeyBoardListener.setListener(getActivity(), new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                bottomLikeView.setVisibility(View.GONE);
                bottomCollection.setVisibility(View.GONE);
                bottomShareView.setVisibility(View.GONE);
                mKeyboardShowRl.setVisibility(View.VISIBLE);
                mEtSendContent.setMaxLines(4);
            }

            @Override
            public void keyBoardHide() {
                bottomLikeView.setVisibility(View.VISIBLE);
                bottomCollection.setVisibility(View.VISIBLE);
                bottomShareView.setVisibility(View.VISIBLE);
                mKeyboardShowRl.setVisibility(View.GONE);
                mEtSendContent.setMaxLines(1);
            }
        });
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
                        ArrayList<CommendItemBean.RowsBean> rowsBeans = getPresenter().dealDateList(bestlist, rows, ugc);
                        setDate(load_more, rowsBeans);
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
     * 为了跳转后的数据同步
     */
    @Override
    public void onReStart() {
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
                        if (data == null) {
                            return;
                        }
                        content = data;
                        if (viewHolder != null) {
                            viewHolder.justBindCountAndState(data);
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<MomentsDataBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
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

        } else if (view.getId() == R.id.child_reply_layout) {
            if (bean.isUgc && ugc != null) {
                HelperForStartActivity.openUgcDetail(ugc);
            } else {
                HelperForStartActivity.openCommentDetail(bean);
            }
        } else if (view.getId() == R.id.expand_text_view) {
            if (bean.isUgc && ugc != null) {
                HelperForStartActivity.openUgcDetail(ugc);
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
            HelperForStartActivity.openUgcDetail(ugc);
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
        }, MySpUtils.isMe(bean.userid), isHastitle ? bean.commenttext : null);
        dialog.show(getChildFragmentManager(), "dialog");
        return true;
    }

    String reportType;

    /**
     * @param id
     * @param type 0 代表内容举报,1 是评论举报
     */
    private void showReportDialog(String id, int type) {
        new AlertDialog.Builder(MyApplication.getInstance().getRunningActivity())
                .setSingleChoiceItems(BaseConfig.REPORTITEMS, -1, (dialog, which) -> reportType = BaseConfig.REPORTITEMS[which])
                .setTitle("举报")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    if (TextUtils.isEmpty(reportType)) {
                        ToastUtil.showShort("请选择举报类型");
                    } else {
                        CommonHttpRequest.getInstance().requestReport(id, reportType, type);
                        dialog.dismiss();
                        reportType = null;
                    }
                }).show();
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
                boolean isCollection = "0".equals(content.getIscollection());
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
                if (selectList.size() != 0 && publishType != -1 && publishType == 2) {
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
                if (selectList.size() != 0 && publishType != -1 && publishType == 1) {
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
