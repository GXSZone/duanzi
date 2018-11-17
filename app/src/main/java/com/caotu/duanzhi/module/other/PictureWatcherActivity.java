package com.caotu.duanzhi.module.other;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.luck.picture.lib.widget.PreviewViewPager;

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
//        ProgressBar progressBar = (ProgressBar) findViewById(R.id.picture_viewer_progressbar);
//        viewPager.setTransitionName(DevicesUtils.getString(R.string.transitional_image));
        tvPosition = findViewById(R.id.tv_picture_position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int i) {
//                position = i;
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
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_image_watcher;
    }
}
