package com.caotu.duanzhi.module.home;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.publish.publishView;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.SoftKeyBoardListener;
import com.caotu.duanzhi.utils.ToastUtil;
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

/**
 * 内容详情页面
 */
public class ContentDetailActivity extends BaseActivity implements View.OnClickListener, publishView {

    private ImageView mIvBack;
    /**
     * 期待你的神评论
     */
    private REditText mEtSendContent;
    private ImageView mIvDetailPhoto;
    private ImageView mIvDetailVideo;
    private ImageView mIvDetailPhoto1;
    private ImageView mIvDetailVideo1;
    /**
     * 发布
     */
    private RTextView mTvClickSend;
    private RelativeLayout mKeyboardShowRl;
    private CommentReplyPresenter presenter;
    PictureDialog dialog;
    private RecyclerView recyclerView;
    private ContentDetailFragment detailFragment;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_content_detail;
    }

    @Override
    protected void initView() {
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(this);
        mEtSendContent = (REditText) findViewById(R.id.et_send_content);
        mIvDetailPhoto = (ImageView) findViewById(R.id.iv_detail_photo);
        mIvDetailPhoto.setOnClickListener(this);
        mIvDetailVideo = (ImageView) findViewById(R.id.iv_detail_video);
        mIvDetailVideo.setOnClickListener(this);
        mIvDetailPhoto1 = (ImageView) findViewById(R.id.iv_detail_photo1);
        mIvDetailPhoto1.setOnClickListener(this);
        mIvDetailVideo1 = (ImageView) findViewById(R.id.iv_detail_video1);
        mIvDetailVideo1.setOnClickListener(this);
        mTvClickSend = (RTextView) findViewById(R.id.tv_click_send);
        mTvClickSend.setOnClickListener(this);
        mKeyboardShowRl = (RelativeLayout) findViewById(R.id.keyboard_show_rl);
        recyclerView = findViewById(R.id.publish_rv);
        initFragment();

        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        setKeyBoardListener();
        presenter = new CommentReplyPresenter(this);
    }

    public void initFragment() {
        MomentsDataBean bean = getIntent().getParcelableExtra(HelperForStartActivity.KEY_CONTENT);
        boolean isToComment = getIntent().getBooleanExtra(HelperForStartActivity.KEY_TO_COMMENT, false);
        detailFragment = new ContentDetailFragment();
        detailFragment.setDate(bean, isToComment);
        turnToFragment(null, detailFragment, R.id.fl_fragment_content);
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
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_detail_photo:
            case R.id.iv_detail_photo1:
                presenter.getPicture();
                break;
            case R.id.iv_detail_video:
            case R.id.iv_detail_video1:
                presenter.getVideo();
                break;

            case R.id.tv_click_send:
                break;
        }
    }

    private List<LocalMedia> selectList = new ArrayList<>();

    //目前有:纯图片,纯视频,纯文字,视频加文字,图片加文字
    //       1     2     3       4        5
    private int publishType = -1;
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
                    showRV();
//                    adapter.setImagUrls(selectList, true);
                    break;
                case PictureConfig.REQUEST_PICTURE:
                    publishType = 1;
                    selectList = PictureSelector.obtainMultipleResult(data);
                    presenter.setMediaList(selectList);
                    showRV();
//                    adapter.setImagUrls(selectList, false);
                    break;
            }
        }
    }

    private void showRV() {
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
                }
            });
            adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    LocalMedia localMedia = selectList.get(position);
                    boolean isVideo = PictureMimeType.isVideo(localMedia.getPictureType());
                    if (isVideo) {
                        PictureSelector.create(ContentDetailActivity.this)
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

    }

    @Override
    protected void onDestroy() {
        if (presenter != null) {
            presenter.destory();
        }
        super.onDestroy();
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
        }
        dialog.show();
    }

    @Override
    public void publishError() {
        ToastUtil.showShort("发布失败");
    }

    @Override
    public void endPublish() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
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
}
