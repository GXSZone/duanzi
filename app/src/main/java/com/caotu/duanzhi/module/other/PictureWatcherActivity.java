package com.caotu.duanzhi.module.other;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.other.MyShareListener;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.luck.picture.lib.dialog.PictureDialog;
import com.luck.picture.lib.widget.PreviewViewPager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.model.Response;
import com.umeng.socialize.bean.SHARE_MEDIA;

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
    private ArrayList<String> images;
    private TextView tvPosition;

    @Override
    protected void initView() {
        Intent intent = getIntent();

        Bundle bundle = intent.getBundleExtra("list");
        if (bundle != null) {
            position = bundle.getInt("position", 0);
            images = bundle.getStringArrayList("tlist");
        }
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
        viewPager.setAdapter(new SimpleFragmentAdapter(this, images));
        viewPager.setCurrentItem(position, false);

        findViewById(R.id.iv_detail_download).setOnClickListener(v -> startDownloadImage());
        findViewById(R.id.iv_detail_share).setOnClickListener(v -> showShareDialog());
    }

    public PictureDialog loadDialog;

    private void showShareDialog() {
        WebShareBean webBean = ShareHelper.getInstance().createWebBean(false, false,
                null, null, null);
        ShareDialog shareDialog = ShareDialog.newInstance(webBean);
        shareDialog.setListener(new ShareDialog.ShareMediaCallBack() {
            @Override
            public void callback(WebShareBean bean) {
                bean.url = images.get(position);
                ShareHelper.getInstance().shareImage(bean, new MyShareListener(null, -1) {
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

    // TODO: 2018/11/28 下载图片只有静态图,gif还有问题
    private void startDownloadImage() {
        String url = images.get(position);
        OkGo.<Bitmap>get(url)
                .execute(new BitmapCallback() {
                    @Override
                    public void onSuccess(Response<Bitmap> response) {
                        VideoAndFileUtils.saveImage(response.body());
                        ToastUtil.showShort("图片下载成功,请去相册查看");
                    }
                });
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_image_watcher;
    }
}
