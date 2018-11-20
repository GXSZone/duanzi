package com.caotu.duanzhi.module.other;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.luck.picture.lib.widget.PreviewViewPager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.model.Response;

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
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int i) {
                position = i;
                String text = i + 1 + " / " + images.size();
                tvPosition.setText(text);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        String text = position + 1 + " / " + images.size();
        tvPosition.setText(text);
        viewPager.setAdapter(new SimpleFragmentAdapter(this, images));
        viewPager.setCurrentItem(position, false);

        findViewById(R.id.iv_detail_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownloadImage();
            }
        });
    }

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
