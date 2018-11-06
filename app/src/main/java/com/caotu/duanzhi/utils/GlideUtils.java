package com.caotu.duanzhi.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;

/**
 * @author mac
 * @日期: 2018/11/6
 * @describe 圆角和圆图像都用自定义RImageview,不然glide不能裁剪本地默认图片的圆角
 */
public class GlideUtils {

    /*
     *加载图片(默认)
     */
    public static void loadImage(String url, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.color.white) //占位图
                .error(R.color.white)       //错误图
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(MyApplication.getInstance()).load(url).apply(options).into(imageView);
    }

    /**
     * 有默认图的
     * @param url
     * @param placeholder
     * @param imageView
     */
    public static void loadImage(String url, int placeholder,ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(placeholder) //占位图
                .error(placeholder)       //错误图
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(MyApplication.getInstance()).load(url).apply(options).into(imageView);
    }

}
