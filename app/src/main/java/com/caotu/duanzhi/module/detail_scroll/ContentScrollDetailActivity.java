package com.caotu.duanzhi.module.detail_scroll;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.module.base.BaseSwipeActivity;
import com.caotu.duanzhi.module.detail.CommentReplyPresenter;
import com.caotu.duanzhi.module.detail.ContentDetailFragment;
import com.caotu.duanzhi.module.detail.ContentItemAdapter;
import com.caotu.duanzhi.module.detail.IHolder;
import com.caotu.duanzhi.module.detail.ILoadMore;
import com.caotu.duanzhi.module.detail.IVewPublishComment;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.module.publish.PublishPresenter;
import com.caotu.duanzhi.other.TextWatcherAdapter;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.SoftKeyBoardListener;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.TipDialog;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.dialog.PictureDialog;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.widget.PreviewViewPager;
import com.ruffian.library.widget.REditText;
import com.ruffian.library.widget.RTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 内容详情页面
 * <p>
 * 这个还不好加,会导致侧滑返回
 */

public class ContentScrollDetailActivity extends BaseSwipeActivity implements View.OnClickListener,
        IVewPublishComment, ILoadMore {

    //需要阻尼效果可以使用FlexibleViewPager
    private PreviewViewPager viewPager;
    public REditText mEtSendContent;
    private View bottomLikeView, bottomCollection, bottomShareView, keyboardView;
    //    private ImageView mIvDetailVideo;
    private RTextView mTvClickSend;
    private RelativeLayout mKeyboardShowRl;
    public PublishPresenter presenter;
    private RecyclerView recyclerView;
    private ArrayList<BaseFragment> fragments;
    private List<MomentsDataBean> dateList;
    int index = 0;

    private LinearLayout ll_bottom;
    private BaseFragmentAdapter fragmentAdapter;
    private int mPosition;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_scroll_detail;
    }

    @Override
    protected void initView() {
        keyboardView = findViewById(R.id.view_by_keyboard);
        ll_bottom = findViewById(R.id.ll_bottom_publish);
        mEtSendContent = findViewById(R.id.et_send_content);

        bottomLikeView = findViewById(R.id.bottom_tv_like);
        bottomLikeView.setOnClickListener(this);
        bottomCollection = findViewById(R.id.bottom_iv_collection);
        bottomCollection.setOnClickListener(this);
        bottomShareView = findViewById(R.id.bottom_iv_share);
        bottomShareView.setOnClickListener(this);

        findViewById(R.id.iv_detail_photo1).setOnClickListener(this);
        findViewById(R.id.iv_detail_video1).setOnClickListener(this);


        mTvClickSend = findViewById(R.id.tv_click_send);
        mTvClickSend.setOnClickListener(this);
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
        mKeyboardShowRl = findViewById(R.id.keyboard_show_rl);
        recyclerView = findViewById(R.id.publish_rv);

        viewPager = findViewById(R.id.detail_scroll_viewpager);
        initViewpager();


        setKeyBoardListener();
        //引导左右滑动
        if (!MySpUtils.getBoolean(MySpUtils.SP_SLIDE_GUIDE, false)) {
            MyApplication.getInstance().getHandler().postDelayed(() -> {
                if (ContentScrollDetailActivity.this.isDestroyed() ||
                        ContentScrollDetailActivity.this.isFinishing()) {
                    return;
                }
                TipDialog dialog = new TipDialog(ContentScrollDetailActivity.this, false);
                dialog.show();
                MySpUtils.putBoolean(MySpUtils.SP_SLIDE_GUIDE, true);
            }, 500);
        }
    }

    private void setKeyBoardListener() {
        keyboardView.setOnClickListener(v -> closeSoftKeyboard());
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
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
                bottomLikeView.setVisibility(View.VISIBLE);
                bottomCollection.setVisibility(View.VISIBLE);
                bottomShareView.setVisibility(View.VISIBLE);
                mKeyboardShowRl.setVisibility(View.GONE);
                keyboardView.setVisibility(View.GONE);
                mEtSendContent.setMaxLines(1);
            }
        });
    }

    private void initViewpager() {
        int videoProgress = getIntent().getIntExtra(HelperForStartActivity.KEY_VIDEO_PROGRESS, 0);
//        dateList = getIntent().getParcelableArrayListExtra(HelperForStartActivity.KEY_SCROLL_DETAIL);
        dateList = BigDateList.getInstance().getBeans();
        if (dateList == null || dateList.size() == 0) {
            finish();
            return;
        }
        mPosition = getIntent().getIntExtra(HelperForStartActivity.KEY_FROM_POSITION, 0);
        boolean isComment = getIntent().getBooleanExtra(HelperForStartActivity.KEY_TO_COMMENT, false);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                index = position;
                //重新设置发布的对象
                if (fragments != null) {
                    setPresenter(dateList.get(position), false);
                }
                if (selectList != null && selectList.size() > 0) {
                    //清空选择内容
                    mTvClickSend.setEnabled(false);
                    presenter.clearSelectList();
                    selectList.clear();
                    recyclerView.setVisibility(View.GONE);
                    closeSoftKeyboard();
                }

                if (TextUtils.equals("5", dateList.get(position).getContenttype())) {
                    ll_bottom.setVisibility(View.GONE);
//                    shareIcon.setVisibility(View.VISIBLE);
                } else {
//                    shareIcon.setVisibility(View.INVISIBLE);
                    ll_bottom.setVisibility(View.VISIBLE);
                }
                getLoadMoreDate(position);

                if (fragments.get(position) instanceof ScrollDetailFragment) {
                    IHolder viewHolder = ((ScrollDetailFragment) fragments.get(position)).viewHolder;
                    if (viewHolder != null) {
                        viewHolder.autoPlayVideo();
                    }
                }
            }
        });
        if (dateList != null && dateList.size() > 0) {
            fragments = new ArrayList<>();
            for (int i = 0; i < dateList.size(); i++) {
                MomentsDataBean dataBean = dateList.get(i);
                if (TextUtils.equals("5", dataBean.getContenttype())) {
                    WebFragment fragment = new WebFragment();
                    CommentUrlBean webList = VideoAndFileUtils.getWebList(dataBean.getContenturllist());
                    fragment.setDate(webList.info, dataBean.getContenttitle());
                    fragments.add(fragment);
                    continue;
                }
                ScrollDetailFragment detailFragment = new ScrollDetailFragment();
                // TODO: 2019/1/21 滑到评论还没加,也就多传个字段
                if (i == 0) {
                    detailFragment.setDate(dataBean, false, videoProgress);
                } else {
                    detailFragment.setDate(dataBean, false, 0);
                }

                fragments.add(detailFragment);
            }
        }
//        index = position;
        fragmentAdapter = new BaseFragmentAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(fragmentAdapter);
//        viewPager.setCurrentItem(index);
        setPresenter(dateList.get(index), true);
    }

    private void getLoadMoreDate(int position) {
        if (position == fragments.size() - 1) {
            // TODO: 2018/12/14 如果是最后一页加载更多
            Activity secondActivity = MyApplication.getInstance().getLastSecondActivity();
            if (secondActivity instanceof DetailGetLoadMoreDate) {
                ((DetailGetLoadMoreDate) secondActivity).getLoadMoreDate(this);
            }
        }
    }

    /**
     * 首页tab栏接口请求更多数据后的回调
     *
     * @param beanList
     */
    @Override
    public void loadMoreDate(List<MomentsDataBean> beanList) {
        if (beanList == null || beanList.size() == 0) {
            ToastUtil.showShort("没有更多内容啦～");
            return;
        }
        if (dateList != null) {
//            dateList.addAll(beanList);
            for (int i = 0; i < beanList.size(); i++) {
                MomentsDataBean dataBean = beanList.get(i);
                //数据集也同步
                dateList.add(dataBean);
                if (TextUtils.equals("5", dataBean.getContenttype())) {
                    WebFragment fragment = new WebFragment();
                    CommentUrlBean webList = VideoAndFileUtils.getWebList(dataBean.getContenturllist());
                    fragment.setDate(webList.info, dataBean.getContenttitle());
                    fragments.add(fragment);
                    continue;
                }
                ScrollDetailFragment detailFragment = new ScrollDetailFragment();
                detailFragment.setDate(dataBean, false, 0);
                fragments.add(detailFragment);
            }
            if (fragmentAdapter != null) {
                fragmentAdapter.changeFragment(fragments);
            }
        }
    }

    public void setPresenter(MomentsDataBean date, boolean isInit) {
        if (isInit) {
            presenter = new CommentReplyPresenter(this, date);
            return;
        }
        if ((presenter instanceof CommentReplyPresenter)) {
            ((CommentReplyPresenter) presenter).setByOnlyIdDate(date);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.iv_detail_photo1:
                if (selectList.size() != 0 && publishType != -1 && publishType == 2) {
                    AlertDialog dialog = new AlertDialog.Builder(this)
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
                    AlertDialog dialog = new AlertDialog.Builder(this).setMessage("若你要添加视频，已选图片将从发表界面中清除了？")
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

    //目前有:纯图片,纯视频,纯文字,视频加文字,图片加文字
    //       1     2     3       4        5
    private int publishType = -1;

    public void getPicture() {
        UmengHelper.event(UmengStatisticsKeyIds.reply_image);
        if (presenter == null) return;
        presenter.getPicture();
    }

    private void getVideo() {
        UmengHelper.event(UmengStatisticsKeyIds.reply_video);
        if (presenter == null) return;
        presenter.getVideo();
    }

    private List<LocalMedia> selectList = new ArrayList<>();
    ContentItemAdapter adapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    private void showRV() {
        mTvClickSend.setEnabled(true);
        if (recyclerView != null && recyclerView.getVisibility() != View.VISIBLE) {
            recyclerView.setVisibility(View.VISIBLE);
        }

        if (adapter == null) {
            adapter = new ContentItemAdapter();
            adapter.setOnItemChildClickListener((adapter, view, position) -> {
                adapter.remove(position);
                if (adapter.getData().size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                }
                presenter.setMediaList(adapter.getData());
                if (adapter.getData() == null || adapter.getData().size() == 0) {
                    if (TextUtils.isEmpty(mEtSendContent.getText().toString().trim())) {
                        mTvClickSend.setEnabled(false);
                    }
                }
            });
            recyclerView.setAdapter(adapter);
        }
        adapter.setNewData(selectList);
        mEtSendContent.postDelayed(() -> showKeyboard(mEtSendContent), 200);

    }


    @Override
    public EditText getEditView() {
        return mEtSendContent;
    }

    @Override
    public View getPublishView() {
        return mTvClickSend;
    }

    ProgressDialog dialog;

    @Override
    public void startPublish() {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setMax(100);
            dialog.setCancelable(false);
            dialog.setMessage("预备发射中...");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }
        if (mp4Dialog != null && mp4Dialog.isShowing()) {
            mp4Dialog.dismiss();
        }
        mTvClickSend.setEnabled(false);
        if (this.isDestroyed() || this.isFinishing()) return;
        dialog.show();
        closeSoftKeyboard();
    }

    @Override
    public void uploadProgress(int progress) {
        Log.i("commentProgress", "uploadProgress: " + progress);
        if (dialog != null && dialog.isShowing()) {
            dialog.setProgress(progress);
        }
    }

    @Override
    public void publishError() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        mTvClickSend.setEnabled(false);
        presenter.clearSelectList();
        selectList.clear();
        recyclerView.setVisibility(View.GONE);
        ToastUtil.showShort("发布失败");
        closeSoftKeyboard();
    }

    @Override
    public void endPublish(CommendItemBean.RowsBean bean) {
        UmengHelper.event(UmengStatisticsKeyIds.comment_success);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        ToastUtil.showShort("发射成功");
        mTvClickSend.setEnabled(false);
        presenter.clearSelectList();
        selectList.clear();
        recyclerView.setVisibility(View.GONE);
        callbackFragment(bean);
        closeSoftKeyboard();
    }

    @Override
    public void publishCantTalk(String msg) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
//        mTvClickSend.setEnabled(false);
        presenter.clearSelectList();
        selectList.clear();
        recyclerView.setVisibility(View.GONE);
        ToastUtil.showShort(msg);
        closeSoftKeyboard();
    }

    /**
     * 回调给fragment的adapter
     *
     * @param bean
     */
    protected void callbackFragment(CommendItemBean.RowsBean bean) {
        mEtSendContent.setText("");
        if (fragments != null) {
            if (fragments.get(index) instanceof ContentDetailFragment) {
                ((ContentDetailFragment) fragments.get(index)).publishComment(bean);
            }
        }
    }

    public int getPosition() {
        return index + mPosition;
    }

    @Override
    protected void onDestroy() {
        if (presenter != null) {
            presenter.destory();
        }
//        BigDateList.getInstance().clearBeans();
        EventBusHelp.sendPagerPosition(index + mPosition);
        super.onDestroy();
    }

    PictureDialog mp4Dialog;

    @Override
    public void notMp4() {
        if (mp4Dialog == null) {
            mp4Dialog = new PictureDialog(this);
            mp4Dialog.setCanceledOnTouchOutside(false);
            mp4Dialog.setCancelable(false);
        }
        mTvClickSend.setEnabled(false);
        mp4Dialog.show();
        closeSoftKeyboard();
    }
}
