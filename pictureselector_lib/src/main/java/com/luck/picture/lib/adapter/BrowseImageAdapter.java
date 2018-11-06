package com.luck.picture.lib.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.luck.picture.lib.R;
import com.luck.picture.lib.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/7/2 9:25
 */
public class BrowseImageAdapter extends PagerAdapter {
    private static final String TAG = "PhotoAdapter";
    /**
     * Fragment虽然不是上下文对象但是他有个方法getContext()对象
     * 我们使用Fragment是因为前面说到使用Glide加载图片时，Glide会根据传入的
     * Fragment或者Activity自行关联它们的生命周期，达到优化内存的效果。
     */
    private Context mContext;
    private ProgressBar progressBar;
    /**
     * 图片地址集合
     */
    private List<String> mList;
    /**
     * PhotoView集合，有多少个图片就创建多少个PhotoView。
     */
    private List<PhotoView> mPhoto = new ArrayList<>();

    /**
     * 构造方法，初始化适配器
     *
     * @param mContext
     * @param mList
     */
    public BrowseImageAdapter(Context mContext, List<String> mList, ProgressBar progressBar) {
        this.mContext = mContext;
        this.mList = mList;
        this.progressBar = progressBar;
        // initPhoto();
    }

    public interface OnPageClickListener {
        void onClick(View view, int position);
    }

    public OnPageClickListener mOnPageClickListener;

    public void addOnPageClickListener(OnPageClickListener listener) {
        mOnPageClickListener = listener;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public void notifyDataSetChanged() {
//        initPhoto();
        super.notifyDataSetChanged();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final PhotoView photoView = new PhotoView(mContext);
        // 设置图片显示
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        String imgUrl = mList.get(position);


        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(container.getContext())
                .load(imgUrl)
                .apply(options)
                .into(new DrawableImageViewTarget(photoView) {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        super.onResourceReady(resource, transition);
                        progressBar.setVisibility(View.GONE);
                    }


                });

        // 然后将加载了图片的photoView添加到viewpager中，并且设置宽高
        container.addView(photoView, ViewPager.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnPageClickListener.onClick(view, position);
            }
        });
        return photoView;

    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
