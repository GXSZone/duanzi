package com.caotu.duanzhi.module.other;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.luck.picture.lib.photoview.OnViewTapListener;
import com.luck.picture.lib.photoview.PhotoView;
import com.luck.picture.lib.widget.longimage.ImageSource;
import com.luck.picture.lib.widget.longimage.ImageViewState;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;
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
//    public ProgressBar mProgressBar;

    public SimpleFragmentAdapter(Context context, List<String> images) {
        super();
        this.images = images;
//        mProgressBar = progressBar;
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
        final View contentView = LayoutInflater.from(container.getContext())
                .inflate(R.layout.picture_image_watcher, container, false);
        // 常规图控件
        final PhotoView imageView = contentView.findViewById(R.id.preview_image);
        // 长图控件
        final SubsamplingScaleImageView longImg = contentView.findViewById(R.id.longImg);

        String url = images.get(position);
        boolean isGif = url.endsWith(".gif") || url.endsWith(".GIF");
        //获取图片真正的宽高
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(container.getContext())
                .asBitmap()
                .apply(options)
                .load(url)
                .into(new SimpleTarget<Bitmap>(480, 800) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        int width = resource.getWidth();
                        int height = resource.getHeight();
                        int h = width * 3;
                        boolean isLongImg = height > h;
                        dealImageView(isLongImg,contentView, imageView, longImg, url, isGif);
                    }
                });



        imageView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                MyApplication.getInstance().getRunningActivity().finish();
            }
        });
        longImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getInstance().getRunningActivity().finish();
            }
        });

        (container).addView(contentView, 0);
        return contentView;
    }

    private void dealImageView(boolean isLongImg, View contentView, PhotoView imageView, SubsamplingScaleImageView longImg, String url, boolean isGif) {
        imageView.setVisibility(isLongImg && !isGif ? View.GONE : View.VISIBLE);
        longImg.setVisibility(isLongImg && !isGif ? View.VISIBLE : View.GONE);
        // 压缩过的gif就不是gif了
        if (isGif) {
            RequestOptions gifOptions = new RequestOptions()
                    .override(480, 800)
                    .priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.NONE);
            Glide.with(contentView.getContext())
                    .asGif()
                    .load(url)
                    .apply(gifOptions)
                    .into(imageView);
        } else {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(contentView.getContext())
                    .asBitmap()
                    .load(url)
                    .apply(options)
                    .into(new SimpleTarget<Bitmap>(480, 800) {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            if (isLongImg) {
                                displayLongPic(resource, longImg);
                            } else {
                                imageView.setImageBitmap(resource);
                            }
                        }
                    });
        }
    }

    /**
     * 加载长图
     *
     * @param bmp
     * @param longImg
     */
    private void displayLongPic(Bitmap bmp, SubsamplingScaleImageView longImg) {
        longImg.setQuickScaleEnabled(true);
        longImg.setZoomEnabled(true);
        longImg.setPanEnabled(true);
        longImg.setDoubleTapZoomDuration(100);
        longImg.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
        longImg.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        longImg.setImage(ImageSource.cachedBitmap(bmp), new ImageViewState(0, new PointF(0, 0), 0));
    }
}
