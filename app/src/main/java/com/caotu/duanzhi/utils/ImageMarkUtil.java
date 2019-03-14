package com.caotu.duanzhi.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.PathConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

    /**
     * 文本转成Bitmap
     *
     * @param text    文本内容
     * @param context 上下文
     * @return 图片的bitmap
     */
    private static Bitmap textToBitmap(String text, Context context) {
        TextView tv = new TextView(context);
        tv.setText(text);
        tv.setTextSize(28);
        tv.setBackgroundColor(DevicesUtils.getColor(R.color.white));
        tv.setDrawingCacheEnabled(true);
        tv.setTextColor(DevicesUtils.getColor(R.color.color_7c7c7c));
        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
        tv.buildDrawingCache();
        return tv.getDrawingCache();
//        int rate = bitmap.getHeight() / 20;
//        return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / rate, 20, false);
    }

    /**
     * 文字生成图片
     *
     * @param text    text
     * @param context context
     * @return 生成图片是否成功
     */
    public static boolean textToPicture(String text, Context context) {
        Bitmap bitmap = textToBitmap(text, context);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(PathConfig.getUserImagePath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 只有两个参数的方法
     *
     * @param mContext
     * @param assetsName
     * @return
     */
    public static String copyAssets(Context mContext, String assetsName) {
        String filePath = PathConfig.getFilePath() + File.separator + assetsName;
        if (new File(filePath).exists()) return filePath;
        try {
            InputStream is = mContext.getResources().getAssets().open(assetsName);
            FileOutputStream fos = new FileOutputStream(filePath);
            byte[] buffer = new byte[7168];
            int count = 0;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            fos.close();
            is.close();
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
