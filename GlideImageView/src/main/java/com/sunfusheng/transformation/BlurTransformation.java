package com.sunfusheng.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.util.Util;
import com.sunfusheng.util.BlurUtils;

import java.security.MessageDigest;

/**
 * @author sunfusheng on 2018/6/25.
 */
public class BlurTransformation extends BitmapTransformation {
    private final String ID = getClass().getName();

    private static int MAX_RADIUS = 25;

    private Context context;
    private int radius; //模糊半径0～25  半径越大越模糊,但影响性能
    private int sampling; //取样0～25  --------> 取样值越大,效率越高

    /**
     * 该参数为最佳
     * @param context
     */
    public BlurTransformation(Context context) {
        this(context, 15, 20);
    }


    public BlurTransformation(Context context, int radius, int sampling) {
        this.context = context;
        this.radius = radius > MAX_RADIUS ? MAX_RADIUS : radius;
        this.sampling = sampling;
    }


    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        int width = toTransform.getWidth();
        int height = toTransform.getHeight();
        int scaledWidth = width / sampling;
        int scaledHeight = height / sampling;

        Bitmap bitmap = pool.get(scaledWidth, scaledHeight, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        canvas.scale(1 / (float) sampling, 1 / (float) sampling);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(toTransform, 0, 0, paint);
        return BlurUtils.rsBlur(context, bitmap, radius);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlurTransformation) {
            BlurTransformation other = (BlurTransformation) obj;
            return radius == other.radius && sampling == other.sampling;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Util.hashCode(ID.hashCode(), Util.hashCode(radius, Util.hashCode(sampling)));
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((ID + radius * 10 + sampling).getBytes(CHARSET));
    }
}
