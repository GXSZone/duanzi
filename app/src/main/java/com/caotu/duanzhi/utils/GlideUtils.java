package com.caotu.duanzhi.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;

import java.io.IOException;


/**
 * @author mac
 * @日期: 2018/11/6
 * @describe 圆角和圆图像都用自定义RImageview, 不然glide不能裁剪本地默认图片的圆角
 */
public class GlideUtils {

    /*
     *加载图片(默认)
     */
    public static void loadImage(String url, ImageView imageView) {
        url = MyApplication.buildFileUrl(url);
        Glide.with(MyApplication.getInstance()).load(url).into(imageView);
    }


    public static void loadImage(@DrawableRes int url, ImageView imageView) {
        if (imageView == null || imageView.getContext() == null) return;
        Glide.with(MyApplication.getInstance()).load(url).into(imageView);
    }

    /**
     * 有默认图的
     *
     * @param url
     * @param placeholder
     * @param imageView
     */
    public static void loadImage(String url, int placeholder, ImageView imageView) {
        url = MyApplication.buildFileUrl(url);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(placeholder) //占位图
                .error(placeholder)       //错误图
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(MyApplication.getInstance()).load(url).apply(options).into(imageView);
    }

    static RequestOptions headerNormal = new RequestOptions()
            .centerCrop()
            .placeholder(R.mipmap.touxiang_moren) //占位图
            .error(R.mipmap.touxiang_moren);

    /*
     *需要缓存的图片处理
     */
    @SuppressLint("CheckResult")
    public static void loadImage(String url, ImageView imageView, boolean isSmall) {
        url = MyApplication.buildFileUrl(url);
        Glide.with(MyApplication.getInstance()).load(url)
                .apply(headerNormal).into(imageView);
    }

    /**
     * 获取图片类型
     * 系统自带就有这个api来获取文件后缀
     * @param path
     * @return
     */
    public static String getImageTypeWithMime(String path) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(path, options);
//        String type = options.outMimeType;
//        // ”image/png”、”image/jpeg”、”image/gif”
//        if (TextUtils.isEmpty(type)) {
//            type = "";
//        } else {
//            type = type.substring(6);
//        }
        return MimeTypeMap.getFileExtensionFromUrl(path);
    }


    public static void clearMemory() {
        Glide.get(MyApplication.getInstance()).clearMemory();
    }

    public static void trimMemory(int level) {
        Glide.get(MyApplication.getInstance()).trimMemory(level);
    }

    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    public static Bitmap resizeImage(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBM;
    }


    public static int getOrientation(String imagePath) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(imagePath);
            int orientation =
                    exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int[] getWidthHeight(String imagePath) {
        if (imagePath.isEmpty()) {
            return new int[]{0, 0};
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            Bitmap originBitmap = BitmapFactory.decodeFile(imagePath, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 使用第一种方式获取原始图片的宽高
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

        // 使用第二种方式获取原始图片的宽高
        if (srcHeight == -1 || srcWidth == -1) {
            try {
                ExifInterface exifInterface = new ExifInterface(imagePath);
                srcHeight =
                        exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.ORIENTATION_NORMAL);
                srcWidth =
                        exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.ORIENTATION_NORMAL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 使用第三种方式获取原始图片的宽高
        if (srcWidth <= 0 || srcHeight <= 0) {
            Bitmap bitmap2 = BitmapFactory.decodeFile(imagePath);
            if (bitmap2 != null) {
                srcWidth = bitmap2.getWidth();
                srcHeight = bitmap2.getHeight();
                try {
                    if (!bitmap2.isRecycled()) {
                        bitmap2.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        int orient = getOrientation(imagePath);
        if (orient == 90 || orient == 270) {
            return new int[]{srcHeight, srcWidth};
        }
        return new int[]{srcWidth, srcHeight};
    }

    public static boolean isLongImage(String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        float w = wh[0];
        float h = wh[1];
        float imageRatio = (h / w);
//        float phoneRatio = getPhoneRatio();
//        boolean isLongImage = (w > 0 && h > 0) && (h > w) && (imageRatio >= phoneRatio);
//        boolean isLongImage = imageRatio >= 3.0f;
        return imageRatio >= 3.0f;
    }


    public static boolean isWideImage(String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        float w = wh[0];
        float h = wh[1];
        float imageRatio = (w / h);
        boolean isWideImage = (w > 0 && h > 0) && (w > h) && (imageRatio >= 2);
        return isWideImage;
    }

    public static boolean isSmallImage(String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        boolean isSmallImage = wh[0] < DevicesUtils.getSrecchWidth();
        return isSmallImage;
    }

    public static float getLongImageMinScale(String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        float imageWid = wh[0];
        float phoneWid = DevicesUtils.getSrecchWidth();
        return phoneWid / imageWid;
    }

    public static float getLongImageMaxScale(String imagePath) {
        return getLongImageMinScale(imagePath) * 2;
    }

    public static float getWideImageDoubleScale(String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        float imageHei = wh[1];
        float phoneHei = DevicesUtils.getScreenHeight();
        return phoneHei / imageHei;
    }

    public static float getSmallImageMinScale(String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        float imageWid = wh[0];
        float phoneWid = DevicesUtils.getSrecchWidth();
        return phoneWid / imageWid;
    }

    public static float getSmallImageMaxScale(String imagePath) {
        return getSmallImageMinScale(imagePath) * 2;
    }

    public static Bitmap getImageBitmap(String srcPath, int degree) {
        boolean isOOM = false;
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        float be = 1;
        newOpts.inSampleSize = (int) be;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        newOpts.inDither = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        try {
            bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        } catch (OutOfMemoryError e) {
            isOOM = true;
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            isOOM = true;
            Runtime.getRuntime().gc();
        }
        if (isOOM) {
            try {
                bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
            } catch (Exception e) {
                newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
                bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
            }
        }
        if (bitmap != null) {
            if (degree == 90) {
                degree += 180;
            }
            bitmap = rotateBitmapByDegree(bitmap, degree);
            int ttHeight = (1080 * bitmap.getHeight() / bitmap.getWidth());
            if (bitmap.getWidth() >= 1080) {
                bitmap = zoomBitmap(bitmap, 1080, ttHeight);
            }
        }
        return bitmap;
    }

    private static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }


    public static boolean isGifImageWithMime(String path) {
        return "gif".equalsIgnoreCase(getImageTypeWithMime(path));
    }

}
