package com.caotu.duanzhi.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

public class ImageMarkUtil {
    //水印的边距
    public static final int margin = DevicesUtils.dp2px(5);


    /**
     * 给图片加水印
     *
     * @param src       原图
     * @param watermark 水印
     * @return 加水印的原图
     */
    public static Bitmap WaterMask(Bitmap src, Bitmap watermark) {
        int w = src.getWidth();
        int h = src.getHeight();

        //获取原始水印图片的宽、高
        int w2 = watermark.getWidth();
        int h2 = watermark.getHeight();
        if (w < 300 || h < 300) {
            //不对水印做处理,水印多大就多大
            return src;
        } else {
            //根据bitmap缩放水印图片,图片宽度的五分之一
            float w1 = (w * 1.0f) / 5;
            float h1 = (w1 * h2 * 1.0f) / w2;

            //计算缩放的比例
            float scalewidth = ((float) w1) / w2;
            float scaleheight = ((float) h1) / h2;

            Matrix matrix = new Matrix();
            matrix.postScale(scalewidth, scaleheight);

            watermark = Bitmap.createBitmap(watermark, 0, 0, w2, h2, matrix, true);
            //获取新的水印图片的宽、高
            w2 = watermark.getWidth();
            h2 = watermark.getHeight();
        }

        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(result);
        //在canvas上绘制原图和新的水印图
        cv.drawBitmap(src, 0, 0, null);
        //水印图绘制在画布的右下角
        cv.drawBitmap(watermark, w - w2 - margin, h - h2 - margin, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();

        return result;
    }

}
