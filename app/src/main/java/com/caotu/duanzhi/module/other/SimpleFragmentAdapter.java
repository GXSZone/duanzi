package com.caotu.duanzhi.module.other;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.view.photoview.PhotoView;
import com.sunfusheng.progress.CircleProgressView;

import java.util.List;

/**
 * @author：luck
 * @data：2018/1/27 下午7:50
 * @描述:图片预览
 */

public class SimpleFragmentAdapter extends PagerAdapter {
    private List<String> images;
    public CircleProgressView progressView;
    public ProgressBar mProgressBar;

    public SimpleFragmentAdapter(Context context, List<String> images, ProgressBar progressBar) {
        super();
        this.images = images;
        mProgressBar = progressBar;
        progressView = new CircleProgressView(context);
    }

    @Override
    public int getCount() {
        if (images != null) {
            return images.size();
        }
        return 0;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        // 常规图控件
        PhotoView longImg = new PhotoView(container.getContext());
        // 设置图片显示
        longImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        longImg.enable();
        String media = images.get(position);
        if (!TextUtils.isEmpty(media)) {
            String url = MyApplication.buildFileUrl(media);
            RequestOptions options = new RequestOptions()
                    .placeholder(R.mipmap.moren)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(MyApplication.getInstance())
                    .load(url)
                    .apply(options)
                    .into(new DrawableImageViewTarget(longImg) {
                        @Override
                        public void onLoadStarted(Drawable placeholder) {
                            super.onLoadStarted(placeholder);
                            mProgressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            mProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            super.onResourceReady(resource, transition);
                            mProgressBar.setVisibility(View.GONE);
                        }

                    });

            // 然后将加载了图片的photoView添加到viewpager中，并且设置宽高
            container.addView(longImg, ViewPager.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            longImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyApplication.getInstance().getRunningActivity().finish();
                }
            });

        }
        return longImg;
    }
}
