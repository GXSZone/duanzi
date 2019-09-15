package com.sunfusheng.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sunfusheng.glideimageview.R;
import com.sunfusheng.progress.GlideApp;
import com.sunfusheng.util.Utils;

/**
 * @author sunfusheng on 2018/6/19.
 *
 * https://github.com/BzCoder/EasyGlide 可以拿来参考对glide 的封装
 */
public class ImageCell extends ImageView {

    private static final float THUMBNAIL_RATIO = 0.3f;
    private ImageData imageData;
    private int radius;

    private static Drawable gifDrawable;
    private static Drawable longDrawable;
    private boolean isGif;
    private boolean loadGif;
    private int placeholderResId;
    private int errorResId;

    private boolean hasDrawCornerIcon;
    private Drawable cornerIconDrawable;
    private int cornerIconWidth;
    private int cornerIconHeight;
    private Rect cornerIconBounds;
    private int cornerIconMargin;

    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint.FontMetricsInt fontMetrics;
    private Paint paint;

    public ImageCell(Context context) {
        this(context, null);
    }

    public ImageCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        cornerIconBounds = new Rect();
        cornerIconMargin = Utils.dp2px(getContext(), 3);
        textPaint.setTextAlign(Paint.Align.CENTER);

        paint = new Paint();
        paint.setAntiAlias(true);

    }

    public void setData(ImageData imageData) {
        this.imageData = imageData;
        if (imageData != null) {
            load(imageData.url);
        }
    }

    public ImageCell setLoadGif(boolean loadGif) {
        this.loadGif = loadGif;
        return this;
    }

    public ImageCell setRadius(int radius) {
        this.radius = radius;
        return this;
    }

    public ImageCell placeholder(@DrawableRes int resId) {
        this.placeholderResId = resId;
        return this;
    }

    public ImageCell error(@DrawableRes int resId) {
        this.errorResId = resId;
        return this;
    }

    public void load(String url) {
        if (getContext() == null) return;
        GlideApp.with(getContext().getApplicationContext()).load(url)
                .placeholder(R.mipmap.shenlue_logo)
                .error(R.mipmap.shenlue_logo)
                .fitCenter()
                .transition(new DrawableTransitionOptions().dontTransition())
                .thumbnail(THUMBNAIL_RATIO)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(new DrawableTarget(this) {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Drawable drawable = resource;
                        if (resource instanceof GifDrawable) {
                            isGif = true;
                            if (!loadGif) {
                                drawable = new BitmapDrawable(((GifDrawable) resource).getFirstFrame());
                            }
                        } else {
                            isGif = false;
                        }
                        initCornerIcon();
                        super.onResourceReady(drawable, transition);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        isGif = Utils.isGif(url);
                        initCornerIcon();
                        super.onLoadFailed(errorDrawable);
                    }
                });
    }

    private void initCornerIcon() {
        if (isGif && !loadGif) {
            cornerIconDrawable = getGifDrawable();
        } else if (isLongImage()) {
            cornerIconDrawable = getLongDrawable();
        } else {
            cornerIconDrawable = null;
        }

        if (cornerIconDrawable != null) {
            cornerIconWidth = Utils.dp2px(getContext(), cornerIconDrawable.getIntrinsicWidth());
            cornerIconHeight = Utils.dp2px(getContext(), cornerIconDrawable.getIntrinsicHeight());
            if (!hasDrawCornerIcon) {
                postInvalidate();
            }
        }
    }

    public boolean isLongImage() {
        int realWidth = imageData != null ? imageData.realWidth : 0;
        int realHeight = imageData != null ? imageData.realHeight : 0;
        if (realWidth == 0 || realHeight == 0 || realWidth >= realHeight) {
            return false;
        }
        return (realHeight * 1.0f / realWidth) >= 2.5;
    }

    public Drawable getGifDrawable() {
        if (gifDrawable == null) {
            gifDrawable = Utils.getTextDrawable(getContext().getApplicationContext(), 24, 14, 2, "GIF", 11, R.color.nine_image_text_background_color);
        }
        return gifDrawable;
    }

    public Drawable getLongDrawable() {
        if (longDrawable == null) {
            longDrawable = Utils.getTextDrawable(getContext().getApplicationContext(), 25, 14, 2, "长图", 10, R.color.nine_image_text_background_color);
        }
        return longDrawable;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (cornerIconDrawable != null) {
            cornerIconBounds.set(r - l - cornerIconMargin - cornerIconWidth,
                    b - t - cornerIconHeight - cornerIconMargin,
                    r - l - cornerIconMargin,
                    b - t - cornerIconMargin);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (cornerIconDrawable != null) {
            hasDrawCornerIcon = true;
            cornerIconDrawable.setBounds(cornerIconBounds);
            cornerIconDrawable.draw(canvas);
        }
        if (imageData == null) return;
        //这个是超过9张的时候最后一张显示+
//        if (!TextUtils.isEmpty(imageData.text)) {
//            canvas.drawColor(getResources().getColor(R.color.nine_image_text_background_color));
//            float textX = getWidth() / 2f;
//            float textY = getHeight() / 2f + (fontMetrics.bottom - fontMetrics.top) / 2f - fontMetrics.bottom;
//            canvas.drawText(imageData.text, textX, textY, textPaint);
//        }
        //自己添加代码
        if (!TextUtils.isEmpty(imageData.videoUrl)) {
            //如果是视频还得有个播放图片
            Bitmap bmp = readBitMap(getContext(), R.mipmap.preview_play);
//            BitmapFactory.decodeResource(getResources(), R.mipmap.preview_play, );
            int bmpWidth = bmp.getWidth();
            int bmpHeight = bmp.getHeight();
            int height = getMeasuredHeight();
            int width = getMeasuredWidth();
            int left = width / 2 - bmpWidth / 2;
            int top = height / 2 - bmpHeight / 2;
            canvas.drawBitmap(bmp, left, top, paint);
        }
    }

    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_4444;
        //获取资源图片
        return BitmapFactory.decodeResource(context.getResources(), resId, opt);
    }


    public ImageCell setText(String text) {
        if (imageData != null) {
            imageData.text = text;
            postInvalidate();
        }
        return this;
    }

    public ImageCell setTextColor(@ColorRes int color) {
        textPaint.setColor(getResources().getColor(color));
        return this;
    }

    public ImageCell setTextSize(int size) {
        textPaint.setTextSize(Utils.dp2px(getContext(), size));
        fontMetrics = textPaint.getFontMetricsInt();
        return this;
    }

    private class DrawableTarget extends DrawableImageViewTarget {
        DrawableTarget(ImageView view) {
            super(view);
        }

        @Override
        public void onLoadStarted(Drawable placeholder) {
            getView().setScaleType(ImageView.ScaleType.FIT_CENTER);
            super.onLoadStarted(placeholder);
        }

        @Override
        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
            getView().setScaleType(ImageView.ScaleType.CENTER_CROP);
            super.onResourceReady(resource, transition);
        }
    }
}
