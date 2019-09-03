package com.caotu.duanzhi.module.detail;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.holder.CommentDetailHeaderViewHolder;
import com.caotu.duanzhi.module.holder.IHolder;
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
import com.caotu.duanzhi.view.dialog.ReportDialog;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.dialog.PictureDialog;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.REditText;
import com.sunfusheng.GlideImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * 评论详情页的fragment,只是图文,视频不一样,跟内容的详情大同小异,有些地方不一样,现在比较难剥离
 */
public class CommentNewFragment extends BaseStateFragment<CommendItemBean.RowsBean>
        implements BaseQuickAdapter.OnItemChildClickListener,
        BaseQuickAdapter.OnItemClickListener,
        HandleBackInterface,
        BaseQuickAdapter.OnItemLongClickListener,
        TextViewLongClick, View.OnClickListener, IVewPublishComment {

    public REditText mEtSendContent;
    private View bottomShareView, titleBar;
    private RelativeLayout mKeyboardShowRl;
    public CommentReplyPresenter presenter;
    private RecyclerView recyclerView;

    protected TextView mUserName, mTvClickSend, mUserIsFollow, bottomLikeView, titleText;
    protected ImageView mIvUserAvatar;
    protected CommendItemBean.RowsBean bean;
    protected GlideImageView userHeader;

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

    public CommentReplyPresenter getPresenter() {
        if (presenter == null) {
            presenter = new CommentReplyPresenter(this, bean);
        }
        return presenter;
    }

    boolean isNeedScrollHeader = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();
        if (bundle == null) {
            ToastUtil.showShort("未传参数");
            return;
        }
        bean = bundle.getParcelable("commentBean");
    }

    public void initOtherView(View inflate) {
        inflate.findViewById(R.id.iv_back).setOnClickListener(this);
        mEtSendContent = inflate.findViewById(R.id.et_send_content);
        inflate.findViewById(R.id.iv_detail_photo1).setOnClickListener(this);
        inflate.findViewById(R.id.iv_detail_video1).setOnClickListener(this);
        inflate.findViewById(R.id.iv_detail_at).setOnClickListener(this);
        // TODO: 2019-07-30 这里要求做了特殊处理,如果是自己的帖子或者内容不做联动的标题栏处理
        View moreView = inflate.findViewById(R.id.iv_more_bt);
        if (bean == null || MySpUtils.isMe(bean.userid)) {
            moreView.setVisibility(View.INVISIBLE);
            isNeedScrollHeader = false;
        } else {
            moreView.setVisibility(View.VISIBLE);
        }
        moreView.setOnClickListener(this);
        bottomLikeView = inflate.findViewById(R.id.bottom_tv_like);
        bottomLikeView.setOnClickListener(this);
        inflate.findViewById(R.id.bottom_iv_collection).setVisibility(View.GONE); //评论详情底部没有收藏

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
        //视频类型没有这个标题栏
        if (titleText != null) {
            titleText.setText("评论详情");
        }
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
        userHeader = inflate.findViewById(R.id.iv_user_headgear);
        setKeyBoardListener();
    }

    private void setKeyBoardListener() {
        SoftKeyBoardListener.setListener(getActivity(), new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                bottomLikeView.setVisibility(View.GONE);
                bottomShareView.setVisibility(View.GONE);
                mKeyboardShowRl.setVisibility(View.VISIBLE);
                mEtSendContent.setMaxLines(4);
            }

            @Override
            public void keyBoardHide() {
                bottomLikeView.setVisibility(View.VISIBLE);
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
        initOtherView(rootView);
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

    public IHolder<CommendItemBean.RowsBean> viewHolder;

    protected void initHeader() {
        getPresenter();
        //两个头布局只差了个查看原帖的区别
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.layout_comment_detail_header, mRvContent, false);
        if (viewHolder == null) {
            viewHolder = new CommentDetailHeaderViewHolder(headerView);
            viewHolder.bindFragment(this);
        }

        //设置头布局
        adapter.setHeaderView(headerView);
        adapter.setHeaderAndEmpty(true);
        //因为功能相同,所以就统一都由头holder处理得了,分离代码
        viewHolder.bindSameView(mUserName, mIvUserAvatar, mUserIsFollow, bottomLikeView);
        if (bean == null) return;
        viewHolder.bindDate(bean);
        //这个跟列表的回复UI相关
        if (adapter instanceof CommentReplayAdapter) {
            ((CommentReplayAdapter) adapter).setParentName(bean.username);
        }
        if (userHeader == null || bean == null || TextUtils.isEmpty(bean.getGuajianurl()))
            return;
        userHeader.load(bean.getGuajianurl());
    }


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
                        dealList(rows, load_more);
                        int count = data.getCount();
                        bean.replyCount = count;
                        if (viewHolder instanceof CommentDetailHeaderViewHolder) {
                            ((CommentDetailHeaderViewHolder) viewHolder).setComment(count);
                        }
                    }
                });
    }

    private void dealList(List<CommendItemBean.RowsBean> rows, int load_more) {
        if (rows != null && rows.size() > 0
                && DateState.init_state == load_more
                && bean != null && !TextUtils.isEmpty(bean.fromCommentId)) {
            int position = -1;

            for (int i = 0; i < rows.size(); i++) {
                if (TextUtils.equals(rows.get(i).commentid, bean.fromCommentId)) {
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
                params.put("cmtid", bean.fromCommentId);
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

    /**
     * 为了跳转后的数据同步
     */
    @Override
    public void onReStart() {
        if (bean == null) return;
        HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
        params.put("cmtid", bean.commentid);
        OkGo.<BaseResponseBean<CommendItemBean.RowsBean>>post(HttpApi.COMMENT_DEATIL)
                .upJson(new JSONObject(params))
                .execute(new JsonCallback<BaseResponseBean<CommendItemBean.RowsBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<CommendItemBean.RowsBean>> response) {
                        CommendItemBean.RowsBean data = response.body().getData();
                        if (data == null) return;
                        if (viewHolder instanceof CommentDetailHeaderViewHolder) {
                            ((CommentDetailHeaderViewHolder) viewHolder).changeHeaderDate(data);
                        }
                    }
                });
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
        } else if (view.getId() == R.id.expand_text_view) {
            commentDetailReplay(bean);
        }
    }

    private void commentDetailReplay(CommendItemBean.RowsBean bean) {
        getPresenter().setUserInfo(bean.commentid, bean.userid);
        mEtSendContent.setHint("回复@" + bean.username + ":");
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        CommendItemBean.RowsBean bean = (CommendItemBean.RowsBean) adapter.getData().get(position);
        commentDetailReplay(bean);
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
            public void colloection(boolean isCollection) {
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
    public void textLongClick(BaseQuickAdapter adapter, View view, int position) {
        onItemLongClick(adapter, view, position);
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
        }, MySpUtils.isMe(bean.userid), bean.commenttext);

        dialog.show(getChildFragmentManager(), "dialog");
        return true;
    }


    private void showReportDialog(String id) {
        ReportDialog dialog = new ReportDialog(getContext());
        dialog.setIdAndType(id, 1);
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
                showReportDialog(bean.commentid);
                break;
            case R.id.bottom_iv_share:
                WebShareBean webBean = ShareHelper.getInstance().createWebBean(viewHolder.isVideo(), false,
                        null, viewHolder.getVideoUrl(), bean.contentid);
                showShareDialog(webBean, bean);
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
                        ToastUtil.showShort(extra.userid);
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
