package com.caotu.duanzhi.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.MyApplication;
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
     * 为了防止bitmap在不同手机分辨率下解析出来的宽高不一致
     *
     * @param resources
     * @param id
     * @return
     */
    private static Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }

    /**
     * 给图片加水印
     *
     * @param src 原图
     * @return 加水印的原图
     */
    public static Bitmap WaterMask(Bitmap src) {
        if (src == null) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        // TODO: 2019-07-10 水印图有两套,小图用小的水印图,大图用大水印图
        Bitmap watermark = BitmapFactory.decodeResource(MyApplication.getInstance().getResources(),
                R.mipmap.shuiyin_img_normal);
        Bitmap watermarkSmall = BitmapFactory.decodeResource(MyApplication.getInstance().getResources(),
                R.mipmap.shuiyin_img_small);
        //获取原始水印图片的宽、高
        int w2 = watermark.getWidth();
        int h2 = watermark.getHeight();

        int w3 = watermarkSmall.getWidth();
        int h3 = watermarkSmall.getHeight();

        if (w <= w3 || h <= h3) { //如果图片比小图水印还好则不做处理
            return src;
        } else if (w <= w2 || h <= h2) { //如果图片比大水印图小则用小水印图
            float w1 = (w * 1.0f) / 4;
            float h1 = (w1 * h3 * 1.0f) / w3;

            //计算缩放的比例
            float scalewidth = w1 / w3;
            float scaleheight = h1 / h3;

            Matrix matrix = new Matrix();
            matrix.postScale(scalewidth, scaleheight);
            if (w3 <= 0 || h3 <= 0) {
                watermark = watermarkSmall;
            } else {
                watermark = Bitmap.createBitmap(watermarkSmall, 0, 0, w3, h3, matrix, true);
            }
            //获取新的水印图片的宽、高
            w2 = watermark.getWidth();
            h2 = watermark.getHeight();
        } else {
            //根据bitmap缩放水印图片,图片宽度的五分之一
            float w1 = (w * 1.0f) / 5;
            float h1 = (w1 * h2 * 1.0f) / w2;

            //计算缩放的比例
            float scalewidth = w1 / w2;
            float scaleheight = h1 / h2;

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
        cv.save();
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
        // TODO: 2019-04-25 水印图片大小控制
        tv.setTextSize(18);
        tv.setText(text);
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
