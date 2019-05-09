package com.caotu.duanzhi.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.config.PathConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunfusheng.widget.ImageData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    /*
     * 把View变成bitmap
     * */
    public static Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = loadBitmapFromView(v);
        if (cacheBitmap == null) {
            Log.e("Folder", "failed getViewBitmap(" + v + ")", new RuntimeException());
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    /**
     * View转Bitmap()替代 view.getDrawingCache()[当view超出屏幕时获取为空]
     *
     * @param v
     * @return
     */
    public static Bitmap loadBitmapFromView(View v) {
        if (v == null) {
            return null;
        }
        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(screenshot);
        c.translate(-v.getScrollX(), -v.getScrollY());
        v.draw(c);
        return screenshot;
    }

    /*
     * 把View变成bitmap
     * */
    public static Bitmap getLongViewBitmap(ScrollView scrollView) {
        int sumHeight = 0;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            sumHeight += scrollView.getChildAt(i).getHeight();
        }
        Bitmap bmp = Bitmap.createBitmap(scrollView.getWidth(), sumHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        scrollView.draw(canvas);
        return bmp;
    }

    public static Map<String, String> getMediaInfo(String path) {
        HashMap<String, String> param = new HashMap<>();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
            mmr.setDataSource(inputStream.getFD());
        } catch (Exception e) {
            e.printStackTrace();
        }

//        mmr.setDataSource(mUri);

        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)
        String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
        String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高
        String rotation = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);//高

        param.put(DURATION, duration);
        param.put(WIDTH, width);
        param.put(HEIGHT, height);
        param.put(ROTATION, rotation);


        mmr.release();
        return param;
    }


    public static String saveImage(Bitmap bmp) {
        if (bmp == null) return "";
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
     * 针对的接口的string字符串转成list,第二个参数是宽高的参数
     * "["挨打的","奥术大师多"]" 这种格式,真他妈恶心
     */
    public static ArrayList<ImageData> getImgList(String urlList, String wh) {
        if (TextUtils.isEmpty(urlList) || TextUtils.equals("[]", urlList)) {
            return null;
        }
        ArrayList<ImageData> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(urlList);
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                String url = (String) jsonArray.get(i);

                //这个可能会有影响  列表的视频加载失败，评论区的视频，图片封面图也很慢
                url = MyApplication.buildFileUrl(url);

                ImageData imageData = new ImageData(url);
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
            if (jsonArray.length() >= 2) {
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
        if (TextUtils.isEmpty(urlList) || TextUtils.equals("[]", urlList)) return "";
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
     * 为了视频全屏分享的封面,列表没做处理,直接拿视频链接交给glide处理
     *
     * @param urlList
     * @return
     */
    public static String getCommentCover(String urlList) {
        if (TextUtils.isEmpty(urlList) || TextUtils.equals("[]", urlList)) return "";
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
        if (TextUtils.equals("[]", url) || TextUtils.isEmpty(url)) return null;
        ArrayList<ImageData> list = new ArrayList<>();
        List<CommentUrlBean> listBean = new Gson().fromJson(url,
                new TypeToken<List<CommentUrlBean>>() {
                }.getType());
        if (listBean != null) {
            for (CommentUrlBean urlBean : listBean) {
                String infoUrl = MyApplication.buildFileUrl(urlBean.info);
                ImageData data = new ImageData(infoUrl);
                if (!TextUtils.isEmpty(urlBean.size) && urlBean.size.contains(",")) {
                    try {
                        String[] split = urlBean.size.split(",");
                        data.realWidth = Integer.parseInt(split[0]);
                        data.realHeight = Integer.parseInt(split[1]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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
     * @param wh   1横视频 2竖视频 3图片 4文字
     * @return
     */
    public static String changeListToJsonArray(List<String> list, String type, String wh) {
        //这里的type为4 的类型是GIF 和发布的4是纯文字
        if (list == null || list.size() == 0 || TextUtils.equals("4", type)) {
            //防止接口返回的时候自己解析成list奔溃,保持一致
            return null;
        }
        JSONArray array = new JSONArray();
        try {
            // TODO: 2018/11/18 后期可能会有视频和图片混合,则该判断条件就作废了,注意
            if (LikeAndUnlikeUtil.isVideoType(type) && list.size() == 2) {
                JSONObject object = new JSONObject();
                object.put("type", type);
                object.put("cover", list.get(0));
                object.put("info", list.get(1));
                object.put("size", wh);
                array.put(object);
            } else {
                for (String image : list) {
                    JSONObject object = new JSONObject();
                    object.put("type", type);
                    object.put("cover", image);
                    object.put("info", image);
                    object.put("size", wh);
                    array.put(object);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array.toString();
    }

    /**
     * 还要获取内容类型的,详情里评论列表回复里展示用
     *
     * @param url
     * @return
     */
    public static List<CommentUrlBean> getCommentUrlBean(String url) {
        if (TextUtils.isEmpty(url) || TextUtils.equals("[]", url)) {
            return null;
        }
        return new Gson().fromJson(url,
                new TypeToken<List<CommentUrlBean>>() {
                }.getType());
    }

    private static final double CROSS_VIDEO_HIGH = 1.50d;
    private static final double VERTICAL_VIDEO_HIGH = 0.88d;

    /**
     * 控制视频的宽高
     *
     * @param player
     * @param isCross
     */
    public static void setVideoWH(View player, boolean isCross) {
        //横视频 1.77
        //竖视频 0.88
        double videoHigh = isCross ? CROSS_VIDEO_HIGH : VERTICAL_VIDEO_HIGH;
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) ((DevicesUtils.getSrecchWidth()
                        //40指的是控件和屏幕两边的间距加起来
                        - DevicesUtils.dp2px(40)) / videoHigh));
        player.setLayoutParams(layoutParams);
    }

    /**
     * 直接从 "["挨打的","奥术大师多"]" 转成
     * [{"cover": "资源封面URL", "type": 1横视频2竖视频3图片4GIF, "info": "资源URL"}]
     * 针对UGC的对象转换
     *
     * @param urlList
     * @param type    1横视频 2竖视频 3图片 4文字
     * @return
     */
    public static String changeStringToCommentUrl(String urlList, String type) {
        if (TextUtils.isEmpty(urlList) || TextUtils.equals("4", type)) {
            //防止接口返回的时候自己解析成list奔溃,保持一致
            return null;
        }
        JSONArray objectList = new JSONArray();
        try {
            JSONArray array = new JSONArray(urlList);
            if (LikeAndUnlikeUtil.isVideoType(type)) {
                JSONObject object = new JSONObject();
                object.put("type", type);
                object.put("cover", array.get(0));
                object.put("info", array.get(1));
                objectList.put(object);
            } else {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = new JSONObject();
                    object.put("type", type);
                    object.put("cover", array.get(i));
                    object.put("info", array.get(i));
                    objectList.put(object);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return objectList.toString();
    }
}
