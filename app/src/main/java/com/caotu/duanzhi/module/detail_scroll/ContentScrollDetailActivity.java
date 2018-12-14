package com.caotu.duanzhi.module.detail_scroll;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.TextWatcherAdapter;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.module.home.CommentReplyPresenter;
import com.caotu.duanzhi.module.home.ContentDetailFragment;
import com.caotu.duanzhi.module.home.ILoadMore;
import com.caotu.duanzhi.module.home.IVewPublishComment;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.module.publish.PublishPresenter;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.SoftKeyBoardListener;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.caotu.duanzhi.view.dialog.TipDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.dialog.PictureDialog;
import com.luck.picture.lib.entity.LocalMedia;
import com.ruffian.library.widget.REditText;
import com.ruffian.library.widget.RTextView;
import com.sunfusheng.GlideImageView;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdMgr;

/**
 * 内容详情页面
 */
public class ContentScrollDetailActivity extends BaseActivity implements View.OnClickListener, IVewPublishComment, ILoadMore {

    private ViewPager viewPager;
    public REditText mEtSendContent;
    private ImageView mIvDetailPhoto;
    private ImageView mIvDetailVideo;
    private RTextView mTvClickSend;
    private RelativeLayout mKeyboardShowRl;
    public PublishPresenter presenter;
    PictureDialog dialog;
    private RecyclerView recyclerView;
    private ArrayList<BaseFragment> fragments;
    private ArrayList<MomentsDataBean> dateList;
    int index = 0;
    private ImageView shareIcon;
    private LinearLayout ll_bottom;
    private TextView title;
    private BaseFragmentAdapter fragmentAdapter;

    public TextView getTitleView() {
        return title;
    }


    @Override
    protected int getLayoutView() {
        return R.layout.activity_scroll_detail;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }

    @Override
    protected void initView() {
        ll_bottom = findViewById(R.id.ll_bottom_publish);
        title = findViewById(R.id.detail_title);
        mEtSendContent = (REditText) findViewById(R.id.et_send_content);
        mIvDetailPhoto = (ImageView) findViewById(R.id.iv_detail_photo);
        mIvDetailPhoto.setOnClickListener(this);
        mIvDetailVideo = (ImageView) findViewById(R.id.iv_detail_video);
        mIvDetailVideo.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_detail_photo1).setOnClickListener(this);
        findViewById(R.id.iv_detail_video1).setOnClickListener(this);
        shareIcon = findViewById(R.id.web_share);
        shareIcon.setVisibility(View.INVISIBLE);
        shareIcon.setOnClickListener(this);

        mTvClickSend = (RTextView) findViewById(R.id.tv_click_send);
        mTvClickSend.setOnClickListener(this);
        mEtSendContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mTvClickSend.setEnabled(true);
                }
            }
        });
        mKeyboardShowRl = (RelativeLayout) findViewById(R.id.keyboard_show_rl);
        recyclerView = findViewById(R.id.publish_rv);

        viewPager = findViewById(R.id.detail_scroll_viewpager);
        initViewpager();

        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        setKeyBoardListener();
        //引导左右滑动
        if (!MySpUtils.getBoolean(MySpUtils.SP_SLIDE_GUIDE, false)) {
            MyApplication.getInstance().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    TipDialog dialog = new TipDialog(ContentScrollDetailActivity.this, false);
                    dialog.show();
                    MySpUtils.putBoolean(MySpUtils.SP_SLIDE_GUIDE, true);
                }
            }, 500);
        }
    }

    private void initViewpager() {
        int videoProgress = getIntent().getIntExtra(HelperForStartActivity.KEY_VIDEO_PROGRESS, 0);
        dateList = getIntent().getParcelableArrayListExtra(HelperForStartActivity.KEY_SCROLL_DETAIL);
        int position = getIntent().getIntExtra(HelperForStartActivity.KEY_FROM_POSITION, 0);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                index = position;
                //重新设置发布的对象
                if (fragments != null) {
                    setPresenter(dateList.get(position));
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
                    shareIcon.setVisibility(View.VISIBLE);
                } else {
                    shareIcon.setVisibility(View.INVISIBLE);
                    ll_bottom.setVisibility(View.VISIBLE);
                }

                // TODO: 2018/12/14 如果是最后一页加载更多
                Activity secondActivity = MyApplication.getInstance().getLastSecondActivity();
                if (secondActivity instanceof MainActivity) {
                    ((MainActivity) secondActivity).getLoadMoreDate(ContentScrollDetailActivity.this);
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
                    fragment.setDate(webList.info);
                    fragments.add(fragment);
                    continue;
                }
                ContentDetailFragment detailFragment = new ContentDetailFragment();
                detailFragment.setDate(dataBean, false, videoProgress);
                fragments.add(detailFragment);
            }
        }
        index = position;
        fragmentAdapter = new BaseFragmentAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setCurrentItem(index);
        getPresenter(dateList.get(index));
        // TODO: 2018/12/13 不得以的解决办法 
//        viewPager.setOffscreenPageLimit(Integer.MAX_VALUE);
    }

    protected void getPresenter(MomentsDataBean dataBean) {
        presenter = new CommentReplyPresenter(this, dataBean);
    }

    public void setPresenter(MomentsDataBean date) {
        if (presenter != null && presenter instanceof CommentReplyPresenter) {
            ((CommentReplyPresenter) presenter).setByOnlyIdDate(date);
        }
    }

    private void setKeyBoardListener() {
        SoftKeyBoardListener.setListener(getWindow().getDecorView(), new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                mIvDetailPhoto.setVisibility(View.GONE);
                mIvDetailVideo.setVisibility(View.GONE);
                mKeyboardShowRl.setVisibility(View.VISIBLE);
            }

            @Override
            public void keyBoardHide(int height) {
                mIvDetailPhoto.setVisibility(View.VISIBLE);
                mIvDetailVideo.setVisibility(View.VISIBLE);
                mKeyboardShowRl.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.web_share:
                WebShareBean webBean = ShareHelper.getInstance().createWebBean(false, false, null
                        , null, null);
                ShareDialog shareDialog = ShareDialog.newInstance(webBean);
                shareDialog.setListener(new ShareDialog.ShareMediaCallBack() {
                    @Override
                    public void callback(WebShareBean bean) {
                        MomentsDataBean dataBean = dateList.get(index);
                        CommentUrlBean webList = VideoAndFileUtils.getWebList(dataBean.getContenturllist());
                        if (bean != null) {
                            bean.url = webList.info;
                            bean.title = "web";
                        }
                        ShareHelper.getInstance().shareFromWebView(bean);
                    }

                    @Override
                    public void colloection(boolean isCollection) {

                    }
                });
                shareDialog.show(getSupportFragmentManager(), "share");

                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_detail_photo:
            case R.id.iv_detail_photo1:
                if (presenter == null) return;
                presenter.getPicture();
                break;
            case R.id.iv_detail_video:
            case R.id.iv_detail_video1:
                if (presenter == null) return;
                presenter.getVideo();
                break;
            case R.id.tv_click_send:
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

    private List<LocalMedia> selectList = new ArrayList<>();
    ContentItemAdapter adapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.REQUEST_VIDEO:
                    selectList = PictureSelector.obtainMultipleResult(data);
                    presenter.setMediaList(selectList);
                    presenter.setIsVideo(true);
                    showRV();
                    break;
                case PictureConfig.REQUEST_PICTURE:
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
        if (recyclerView.getVisibility() != View.VISIBLE) {
            recyclerView.setVisibility(View.VISIBLE);
        }

        if (adapter == null) {
            adapter = new ContentItemAdapter();
            adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
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
                }
            });
            adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    LocalMedia localMedia = selectList.get(position);
                    boolean isVideo = PictureMimeType.isVideo(localMedia.getPictureType());
                    if (isVideo) {
                        PictureSelector.create(MyApplication.getInstance().getRunningActivity())
                                .externalPictureVideo(localMedia.getPath());
                    } else {
                        PictureSelector.create(MyApplication.getInstance().getRunningActivity())
                                .themeStyle(R.style.picture_QQ_style).openExternalPreview(position, selectList);
                    }
                }
            });
            recyclerView.setAdapter(adapter);
        }
        adapter.setNewData(selectList);

        MyApplication.getInstance().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showKeyboard(mEtSendContent);
            }
        }, 200);

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
        if (dialog == null) {
            dialog = new PictureDialog(this);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
        }
        mTvClickSend.setEnabled(false);
        dialog.show();
        closeSoftKeyboard();
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
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
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

    /**
     * 首页tab栏接口请求更多数据后的回调
     *
     * @param beanList
     */
    @Override
    public void loadMoreDate(List<MomentsDataBean> beanList) {
        if (beanList == null || beanList.size() == 0) {
            ToastUtil.showShort("没有更多了");
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
                    fragment.setDate(webList.info);
                    fragments.add(fragment);
                    continue;
                }
                ContentDetailFragment detailFragment = new ContentDetailFragment();
                detailFragment.setDate(dataBean, false, 0);
                fragments.add(detailFragment);
            }
            if (fragmentAdapter != null) {
                fragmentAdapter.changeFragment(fragments);
            }
        }
    }

    /*
     *发表内容的adapter
     */
    class ContentItemAdapter extends BaseQuickAdapter<LocalMedia, BaseViewHolder> {

        public ContentItemAdapter() {
            super(R.layout.item_publish_detail);
        }

        @Override
        protected void convert(BaseViewHolder helper, LocalMedia item) {
            helper.addOnClickListener(R.id.item_publish_normal_delete_iv);
            GlideImageView imageView = helper.getView(R.id.item_publish_normal_giv);
            String url;
            //判断是否是视频
            boolean isVideo = PictureMimeType.isVideo(item.getPictureType());
            if (isVideo) {
                url = item.getPath();
                helper.setGone(R.id.item_publish_normal_play_iv, true);
            } else {
                url = item.getCompressPath();
                helper.setGone(R.id.item_publish_normal_play_iv, false);
            }
            imageView.load(url, R.drawable.image_placeholder);
        }
    }

    @Override
    protected void onDestroy() {
        if (presenter != null) {
            presenter.destory();
        }
        super.onDestroy();
    }

    /**
     * 处理返回键的问题
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //一开始想着搞成静态变量,发现有bug,还是得照着demo的获取方式才可以
            if (JzvdMgr.getCurrentJzvd() != null && JzvdMgr.getCurrentJzvd().currentScreen == Jzvd.SCREEN_WINDOW_TINY) {
                Jzvd.backPress();
                finish();
                return true;
            } else if (Jzvd.backPress()) {
                return true;
            }
            return super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
