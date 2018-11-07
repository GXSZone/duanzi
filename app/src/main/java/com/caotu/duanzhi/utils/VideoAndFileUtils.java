package com.caotu.duanzhi.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.PathConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/7/8.
 */

public class VideoAndFileUtils {
    public static final String DURATION = "duration";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String ROTATION = "rotation";

    public static Map<String, String> getPlayTime(String mUri) {
        HashMap<String, String> param = new HashMap<>();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(mUri).getAbsolutePath());
            mmr.setDataSource(inputStream.getFD());
        } catch (Exception e) {
            e.printStackTrace();
        }

//        mmr.setDataSource(mUri);

        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)
        String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
        String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高
        String rotation = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);//高

//            Log.e("TAG", "playtime" + duration);
//            Log.e("TAG", "width" + width);
//            Log.e("TAG", "height" + height);
        param.put(DURATION, duration);
        param.put(WIDTH, width);
        param.put(HEIGHT, height);
        param.put(ROTATION, rotation);


        mmr.release();
        return param;
    }

    /**
     * long类型时间转时分秒
     */
    public static String timeParse(long duration) {
        String time = "";
        long minute = duration / 60000;
        long seconds = duration % 60000;
        long second = Math.round((float) seconds / 1000);
        if (minute < 10) {
            time += "0";
        }
        time += minute + ":";
        if (second < 10) {
            time += "0";
        }
        time += second;
        return time;
    }



    public static String saveImage(Bitmap bmp) {
        File appDir = new File(PathConfig.LOCALFILE);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return PathConfig.LOCALFILE + fileName;
    }

    public static int[] getImageWidthHeight(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth,options.outHeight};
    }

    /**
     * 保存文件，文件名为当前日期
     * 用于下载视频保存用
     */

    public static String getVideoSavePath(String url) {
        int lastDot = url.lastIndexOf('.');
        String suffix;
        if (lastDot > 0) {
            suffix = url.substring(lastDot);
        } else {
            return null;
        }
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/duanzi/" + "duanzi" +System.currentTimeMillis()+ suffix;
        // 发送广播，通知刷新图库的显示
//        App.getInstance().getRunningActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));

    }

    /**
     * 视频下载提醒弹窗
     *
     * @param context
     * @param listener
     */
    public static void checkNetwork(Activity context, final DialogInterface.OnClickListener listener) {
        if (!NetWorkUtils.isNetworkConnected(context)) {
            ToastUtil.showShort(R.string.video_no_network);
            return;
        }
        if (!NetWorkUtils.isWifiConnected(context)) {
            new AlertDialog.Builder(context)
                    .setMessage("你正在使用移动数据网络，是否继续下载视频？")
                    .setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("土豪随意", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (listener != null) {
                                listener.onClick(dialog, Activity.RESULT_OK);
                            }
                            dialog.dismiss();
                        }
                    }).show();
        } else {
            if (listener != null) {
                listener.onClick(null, Activity.RESULT_OK);
            }
        }
    }

}
