package com.caotu.duanzhi.module.other.imagewatcher;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.PathConfig;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.other.AndroidInterface;
import com.caotu.duanzhi.other.MyShareListener;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.FileUtil;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.dialog.ShareDialog;
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
public class PictureWatcherActivity extends BaseActivity {
    private PreviewViewPager viewPager;
    private int position;
    private ArrayList<ImageInfo> images;
    private TextView tvPosition;
    private String contentId;
    private ImageView downImage;
    private View rootView;
    private ImageView shareIv;
    private ImagePreviewAdapter previewAdapter;
    private ViewStub viewstub;

    @Override
    protected void initView() {
        //设置全屏
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        images = getIntent().getParcelableArrayListExtra("list");
        position = getIntent().getIntExtra("position", 0);
        contentId = getIntent().getStringExtra("contentId");
        // TODO: 2019/1/15 目前可以根据内容id来判断来自于头像
        viewPager = findViewById(R.id.viewpager_image);

        tvPosition = findViewById(R.id.tv_picture_position);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                position = i;
                String text = i + 1 + " / " + images.size();
                tvPosition.setText(text);
            }
        });

        String text = position + 1 + " / " + images.size();
        tvPosition.setText(text);

        previewAdapter = new ImagePreviewAdapter(images);
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
        viewstub.setOnClickListener(v -> HelperForStartActivity.checkUrlForSkipWeb("头套",
                touTao, AndroidInterface.type_other));

        //底部分享和下载的按钮位置更改
        downImage.post(() -> {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) downImage.getLayoutParams();
            layoutParams.bottomMargin = DevicesUtils.dp2px(95);
            downImage.setLayoutParams(layoutParams);
        });

        shareIv.post(() -> {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) shareIv.getLayoutParams();
            layoutParams.bottomMargin = DevicesUtils.dp2px(95);
            shareIv.setLayoutParams(layoutParams);
        });

    }

    public PictureDialog loadDialog;

    private void showShareDialog() {
        WebShareBean webBean = ShareHelper.getInstance().createWebBean(false, false,
                null, null, null);
        ShareDialog shareDialog = ShareDialog.newInstance(webBean);
        shareDialog.setListener(new ShareDialog.ShareMediaCallBack() {
            @Override
            public void callback(WebShareBean bean) {
                bean.url = images.get(position).getOriginUrl();
                // TODO: 2018/11/29 第二个参数还得区分内容和评论
                ShareHelper.getInstance().shareImage(bean, new MyShareListener(contentId, 0) {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                        if (loadDialog == null) {
                            loadDialog = new PictureDialog(PictureWatcherActivity.this);
                        }
                        loadDialog.show();
                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        super.onResult(share_media);
                        if (loadDialog != null && loadDialog.isShowing() && !PictureWatcherActivity.this.isDestroyed()
                                && !PictureWatcherActivity.this.isFinishing()) {
                            loadDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        super.onError(share_media, throwable);
                        if (loadDialog != null && loadDialog.isShowing() && !PictureWatcherActivity.this.isDestroyed()
                                && !PictureWatcherActivity.this.isFinishing()) {
                            loadDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        if (loadDialog != null && loadDialog.isShowing() && !PictureWatcherActivity.this.isDestroyed()
                                && !PictureWatcherActivity.this.isFinishing()) {
                            loadDialog.dismiss();
                        }
                    }
                });
            }

            @Override
            public void colloection(boolean isCollection) {

            }
        });
        shareDialog.show(getSupportFragmentManager(), "image");
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
            downImage.setEnabled(false);
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
        SimpleTarget<File> target = new SimpleTarget<File>() {
            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                ToastUtil.showShort("开始下载...");
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
                FileUtil.createFileByDeleteOldFile(path + name);
                boolean result = FileUtil.copyFile(resource, path, name);
                if (result) {
                    ToastUtil.showShort("图片下载成功,请去相册查看");

                    MyApplication.getInstance().getRunningActivity()
                            .sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                    Uri.fromFile(new File(path.concat(name)))));
                } else {
                    ToastUtil.showShort("保存失败");
                }
                downImage.setEnabled(true);
            }
        };
        Glide.with(MyApplication.getInstance()).downloadOnly().load(url).into(target);
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
}
