package com.caotu.duanzhi.module.other.imagewatcher;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.PathConfig;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.other.OtherActivity;
import com.caotu.duanzhi.other.AndroidInterface;
import com.caotu.duanzhi.other.MyShareListener;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.ImageMarkUtil;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.from.view.swipeback.ISwipeBack;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.luck.picture.lib.dialog.PictureDialog;
import com.luck.picture.lib.widget.PreviewViewPager;
import com.ruffian.library.widget.RImageView;
import com.sunfusheng.GlideImageView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;
import java.util.ArrayList;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.ui
 * email：893855882@qq.com
 * data：16/12/31
 */
public class PictureWatcherActivity extends BaseActivity implements ISwipeBack {
    private int position;
    private ArrayList<ImageInfo> images;
    private TextView tvPosition;
    private String contentId;
    private ImageView downImage;
    private View rootView;
    private ImageView shareIv;
    private ImagePreviewAdapter previewAdapter;
    private ViewStub viewstub;
    private String tagId;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        fullScreen(this);
    }

    @Override
    protected void initView() {
        UmengHelper.event(UmengStatisticsKeyIds.content_view);
        UmengHelper.event(UmengStatisticsKeyIds.look_picture);

        images = getIntent().getParcelableArrayListExtra("list");
        position = getIntent().getIntExtra("position", 0);
        contentId = getIntent().getStringExtra("contentId");
        tagId = getIntent().getStringExtra("tagId");
        // TODO: 2019/1/15 目前可以根据内容id来判断来自于头像
        PreviewViewPager viewPager = findViewById(R.id.viewpager_image);

        tvPosition = findViewById(R.id.tv_picture_position);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                position = i;
                String text = i + 1 + " / " + images.size();
                tvPosition.setText(text);
            }
        });
        if (images == null || images.size() == 0) {
            finish();
            return;
        }
        String text = position + 1 + " / " + images.size();
        tvPosition.setText(text);

        previewAdapter = new ImagePreviewAdapter(images);
        previewAdapter.setListener(() -> {
            // TODO: 2019-07-30 埋点
            UmengHelper.event(UmengStatisticsKeyIds.longTouch);
            showShareDialog();
        });

        viewPager.setAdapter(previewAdapter);
        viewPager.setCurrentItem(position, false);

        downImage = findViewById(R.id.iv_detail_download);
        downImage.setOnClickListener(v -> startDownloadImage());
        shareIv = findViewById(R.id.iv_detail_share);
        shareIv.setOnClickListener(v -> showShareDialog());
        rootView = findViewById(R.id.view_image_watcher);
        dealTouXiang();
    }

    private void dealTouXiang() {
        String touTao = getIntent().getStringExtra("touTao");
        String guaJian = getIntent().getStringExtra("guaJian");
        if (TextUtils.isEmpty(touTao) || TextUtils.isEmpty(guaJian)) return;
        tvPosition.setVisibility(View.GONE);
        viewstub = findViewById(R.id.view_stub_user_header);
        viewstub.setVisibility(View.VISIBLE);
        RImageView imageView = findViewById(R.id.iv_user_avatar);
        GlideUtils.loadImage(images.get(0).getOriginUrl(), imageView);
        GlideImageView guanjianImageView = findViewById(R.id.iv_user_headgear);
        guanjianImageView.load(guaJian);
        findViewById(R.id.ll_guajian_bottom).setOnClickListener(v ->
                HelperForStartActivity.checkUrlForSkipWeb("头套",
                        touTao, AndroidInterface.type_other));

        //底部分享和下载的按钮位置更改
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) downImage.getLayoutParams();
        layoutParams.bottomMargin = DevicesUtils.dp2px(95);
        downImage.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) shareIv.getLayoutParams();
        layoutParams1.bottomMargin = DevicesUtils.dp2px(95);
        shareIv.setLayoutParams(layoutParams1);

    }

    public PictureDialog loadDialog;

    private void showShareDialog() {
        WebShareBean bean = new WebShareBean();
        bean.webType = 1;
        bean.url = images.get(position).getOriginUrl();
        ShareDialog shareDialog = ShareDialog.newInstance(bean);
        shareDialog.setListener(new ShareDialog.ShareMediaCallBack() {
            @Override
            public void callback(WebShareBean bean) {
                bean.url = images.get(position).getOriginUrl();
                // TODO: 2018/11/29 第二个参数还得区分内容和评论
                ShareHelper.getInstance().shareImage(bean, new MyShareListener(contentId, 0) {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                        UmengHelper.event(UmengStatisticsKeyIds.content_share);
                        if (loadDialog == null) {
                            loadDialog = new PictureDialog(PictureWatcherActivity.this);
                        }
                        loadDialog.show();
                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        super.onResult(share_media);
                        ToastUtil.showShort("分享成功");
                        dismissDialog();
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        super.onError(share_media, throwable);
                        dismissDialog();
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        dismissDialog();
                    }
                });
            }

            @Override
            public void colloection(boolean isCollection) {

            }
        });
        shareDialog.show(getSupportFragmentManager(), "image");
    }

    public void dismissDialog() {
        if (loadDialog != null && loadDialog.isShowing() && !PictureWatcherActivity.this.isDestroyed()
                && !PictureWatcherActivity.this.isFinishing()) {
            loadDialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissDialog();
    }

    private void startDownloadImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // 拒绝权限
                ToastUtil.showShort("您拒绝了存储权限，下载失败！");
            } else {
                //申请权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1);
            }
        } else {
            // 下载当前图片
            downloadPicture(images.get(position).getOriginUrl());
        }
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_image_watcher;
    }

    public int convertPercentToBlackAlphaColor(float percent) {
        percent = Math.min(1, Math.max(0, percent));
        int intAlpha = (int) (percent * 255);
        String stringAlpha = Integer.toHexString(intAlpha).toLowerCase();
        String color = "#" + (stringAlpha.length() < 2 ? "0" : "") + stringAlpha + "000000";
        return Color.parseColor(color);
    }

    public void setAlpha(float alpha) {
        int colorId = convertPercentToBlackAlphaColor(alpha);
        rootView.setBackgroundColor(colorId);
        if (alpha >= 1) {
            tvPosition.setVisibility(View.VISIBLE);
            downImage.setVisibility(View.VISIBLE);
            shareIv.setVisibility(View.VISIBLE);
            if (viewstub != null) {
                viewstub.setVisibility(View.VISIBLE);
            }
        } else {
            tvPosition.setVisibility(View.GONE);
            downImage.setVisibility(View.GONE);
            shareIv.setVisibility(View.GONE);
            if (viewstub != null) {
                viewstub.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 下载图片
     *
     * @param url
     */
    public void downloadPicture(final String url) {
        UmengHelper.event(UmengStatisticsKeyIds.my_download_pic);
        CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.download_pic);
        SimpleTarget<File> target = new SimpleTarget<File>() {
            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                ToastUtil.showShort("开始下载...");
                downImage.setEnabled(false);
                super.onLoadStarted(placeholder);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                ToastUtil.showShort("保存失败");
                downImage.setEnabled(true);
            }

            @Override
            public void onResourceReady(@NonNull File resource,
                                        @Nullable Transition<? super File> transition) {
                String path = PathConfig.LOCALFILE;
                String name = System.currentTimeMillis() + "";

                String mimeType = GlideUtils.getImageTypeWithMime(resource.getAbsolutePath());
                name = name + "." + mimeType;
                if (isNeedAddImageWater(url)) {
                    // TODO: 2019/4/3 这里处理了 decodeFile 的空指针问题,图片下载有问题,暂时这么解决
                    Bitmap decodeFile = BitmapFactory.decodeFile(resource.getAbsolutePath());
                    String saveImage = VideoAndFileUtils.saveImage(ImageMarkUtil.WaterMask(decodeFile));
                    if (!TextUtils.isEmpty(saveImage)) {
                        ToastUtil.showShort("图片下载成功,请去相册查看");

                        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
                        if (runningActivity == null) return;
                        runningActivity
                                .sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                        Uri.fromFile(new File(saveImage))));
                    } else {
                        ToastUtil.showShort("保存失败");
                    }
                } else {
                    boolean result = LanSongFileUtil.copyFile(resource, path, name);
                    if (result) {
                        ToastUtil.showShort("图片下载成功,请去相册查看");

                        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
                        if (runningActivity == null) return;
                        runningActivity
                                .sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                        Uri.fromFile(new File(path.concat(name)))));
                    } else {
                        ToastUtil.showShort("保存失败");
                    }
                }
                downImage.setEnabled(true);
            }
        };
        Glide.with(MyApplication.getInstance()).downloadOnly().load(url).into(target);
    }

    /**
     * 图片加不加水印的判断逻辑都在这里
     *
     * @param url
     * @return
     */
    public boolean isNeedAddImageWater(String url) {
        if (TextUtils.isEmpty(contentId) || TextUtils.isEmpty(url)) {
            return false;
        }
        //7b92 壁纸话题的ID
        Activity activity = MyApplication.getInstance().getLastSecondActivity();
        if (activity instanceof OtherActivity) {
            String specialTopic = ((OtherActivity) activity).isSpecialTopic();
            if (TextUtils.equals(specialTopic, "7b92")) {
                return false;
            }
        } else if (!TextUtils.isEmpty(tagId) && tagId.contains("7b92")) {
            return false;
        }
        return !url.endsWith("gif") && !url.endsWith("GIF");
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (previewAdapter != null) {
            previewAdapter.closePage();
        }
    }

    @Override
    public boolean isEnableGesture() {
        return false;
    }

    @Override
    public void setCanSwipe(boolean canSwipe) {

    }
}
