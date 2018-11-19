package com.caotu.duanzhi.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.PathConfig;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunfusheng.widget.ImageData;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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

    public static int[] getImageWidthHeight(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth, options.outHeight};
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

    /**
     * 针对的接口的string字符串转成list,第二个参数是宽高的参数
     * "["挨打的","奥术大师多"]" 这种格式,真他妈恶心
     */
    public static ArrayList<ImageData> getImgList(String urlList, String wh) {
        ArrayList<ImageData> list = new ArrayList<>();
        if (TextUtils.isEmpty(urlList)) {
            return list;
        }
        try {
            JSONArray jsonArray = new JSONArray(urlList);
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                ImageData imageData = new ImageData((String) jsonArray.get(i));
                if (i == 0 && !TextUtils.isEmpty(wh)) {
                    String[] split = TextUtils.split(wh, ",");
                    if (split != null && split.length == 2
                            && Pattern.matches(ValidatorUtils.ISNUM, split[0])
                            && Pattern.matches(ValidatorUtils.ISNUM, split[1])) {
                        imageData.realWidth = Integer.parseInt(split[0]);
                        imageData.realHeight = Integer.parseInt(split[1]);
                    }
                }
                list.add(imageData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取视频的下载链接
     *
     * @param urlList
     * @return
     */
    public static String getVideoUrl(String urlList) {
        if (TextUtils.isEmpty(urlList)) return "";
        try {
            JSONArray jsonArray = new JSONArray(urlList);
            if (jsonArray.length() == 2) {
                return (String) jsonArray.get(1);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取封面
     */
    public static String getCover(String urlList) {
        if (TextUtils.isEmpty(urlList)) return "";
        String cover = "";
        try {
            JSONArray jsonArray = new JSONArray(urlList);
            cover = (String) jsonArray.get(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cover;
    }


    /**
     * web类型第一个则是展示的图片.第二个是跳转的url
     *
     * @param urlList
     * @return
     */
    public static CommentUrlBean getWebList(String urlList) {
        CommentUrlBean urlBean = new CommentUrlBean();
        LogUtil.logString(urlList);
        try {
            JSONArray jsonArray = new JSONArray(urlList);
            if (jsonArray.length() < 2) return urlBean;
            urlBean.cover = (String) jsonArray.get(0);
            urlBean.info = (String) jsonArray.get(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlBean;
    }

    /**
     * 详情里的评论列表一级评论展示的图片和视频内容
     *
     * @param url [{"cover": "资源封面URL", "type": 1横视频2竖视频3图片4GIF, "info": "资源URL"}]
     * @return
     */
    public static ArrayList<ImageData> getDetailCommentShowList(String url) {
        //为空少一步解析步骤
        if (TextUtils.equals("[]", url)||TextUtils.isEmpty(url)) return null;
        ArrayList<ImageData> list = new ArrayList<>();
        List<CommentUrlBean> listBean = new Gson().fromJson(url,
                new TypeToken<List<CommentUrlBean>>() {
                }.getType());
        if (listBean != null) {
            for (CommentUrlBean urlBean : listBean) {
                ImageData data = new ImageData(urlBean.info);
                list.add(data);
            }
        }
        return list;
    }

    /**
     * 注意这里不处理是GIF类型的图片,因为获取的时候也不取,图片框架不需要判断
     * 用于发表评论转换图片和视频使用
     *
     * @param list
     * @param type
     * @return
     */
    public static String changeListToJsonArray(List<String> list, String type) {
        //这里的type为4 的类型是GIF 和发布的4是纯文字
        if (list == null || list.size() == 0 || TextUtils.equals("4", type)) {
            //防止接口返回的时候自己解析成list奔溃,保持一致
            return null;
        }
        ArrayList<CommentUrlBean> beanArrayList = new ArrayList<>();
        // TODO: 2018/11/18 后期可能会有视频和图片混合,则该判断条件就作废了,注意
        if (LikeAndUnlikeUtil.isVideoType(type) && list.size() == 2) {
            CommentUrlBean bean = new CommentUrlBean();
            bean.type = type;
            bean.cover = list.get(0);
            bean.info = list.get(1);
            beanArrayList.add(bean);
        } else {
            for (String image : list) {
                CommentUrlBean bean = new CommentUrlBean();
                bean.type = type;
                bean.cover = image;
                bean.info = image;
                beanArrayList.add(bean);
            }
        }
        return new JSONArray(beanArrayList).toString();
    }

    /**
     * 还要获取内容类型的,详情里评论列表回复里展示用
     *
     * @param url
     * @return
     */
    public static List<CommentUrlBean> getCommentUrlBean(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        return new Gson().fromJson(url,
                new TypeToken<List<CommentUrlBean>>() {
                }.getType());
    }

    private static final double CROSS_VIDEO_HIGH = 1.77d;
    private static final double VERTICAL_VIDEO_HIGH = 0.88d;

    /**
     * 控制视频的宽高
     *
     * @param player
     * @param isCross
     */
    public static void setVideoWH(MyVideoPlayerStandard player, boolean isCross) {
        //横视频 1.77
        //竖视频 0.88
        double videoHigh = isCross ? CROSS_VIDEO_HIGH : VERTICAL_VIDEO_HIGH;
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) ((DevicesUtils.getSrecchWidth()
                        //40指的是控件和屏幕两边的间距加起来
                        - DevicesUtils.dp2px(40)) / videoHigh));
        player.setLayoutParams(layoutParams);
    }
}
