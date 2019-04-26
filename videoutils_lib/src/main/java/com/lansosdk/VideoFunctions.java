package com.lansosdk;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import com.lansosdk.videoeditor.MediaInfo;
import com.lansosdk.videoeditor.VideoEditor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 注意: 此代码仅作为视频处理的演示使用, 不属于sdk的一部分.
 * 所有的操作需要在application初始化好
 */
public class VideoFunctions {

    public final static String TAG = "VideoFunctions";

    /**
     * 获取视频宽高
     *
     * @param path
     * @return
     */
    public static String[] getWidthAndHeight(String path) {
        String[] arr = new String[3];
        MediaInfo info = new MediaInfo(path);
        if (info.prepare()) {
            // TODO: 2018/11/7 判断是否是竖视频
            boolean portVideo = info.isPortVideo();
            arr[0] = info.getWidth() + "";
            arr[1] = info.getHeight() + "";
            arr[2] = portVideo ? "yes" : "no";
        } else {
            Map<String, String> mediaInfo = getMediaInfo(path);
            arr[0] = mediaInfo.get(WIDTH);
            arr[1] = mediaInfo.get(HEIGHT);
            arr[2] = arr[1].compareToIgnoreCase(arr[0]) > 0 ? "yes" : "no";
        }
        //安全起见多一层判断
        if (TextUtils.isEmpty(arr[0])) {
            Map<String, String> mediaInfo = getMediaInfo(path);
            arr[0] = mediaInfo.get(WIDTH);
            arr[1] = mediaInfo.get(HEIGHT);
            arr[2] = arr[1].compareToIgnoreCase(arr[0]) > 0 ? "yes" : "no";
        }
        //这他妈要还是空就不科学了
        if (TextUtils.isEmpty(arr[0])) {
            arr[0] = "960";
            arr[1] = "480";
            arr[2] = "no";
        }
        return arr;
    }

    public static final String DURATION = "duration";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String ROTATION = "rotation";

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

    /**
     * 演示视频压缩, 硬件实现
     * <p>
     * 视频压缩转码:\n调整视频的码率,让视频文件大小变小一些,方便传输.注意:如调整的小太多,则会导致画面下降.这里演示把码率)降低为70%\n
     */
    public static String videoCompressAddPicture(Context ctx, VideoEditor editor, String srcVideo) {
        MediaInfo info = new MediaInfo(srcVideo);
        if (info.prepare()) {

            int compressWidth = 480;
            int compressHeight = 960;
            int videoWidth = info.getWidth();
            int videoHeight = info.getHeight();

            float widthMultiple;
            float heightMultiple;
            if (videoWidth > videoHeight) {
                //横视频
                heightMultiple = (compressWidth * 1.0f) / videoHeight;
                widthMultiple = (compressHeight * 1.0f) / videoWidth;
            } else {
                widthMultiple = (compressWidth * 1.0f) / videoWidth;
                heightMultiple = (compressHeight * 1.0f) / videoHeight;
            }
            return editor.executeVideoAndPictureCompress(ctx, srcVideo, (widthMultiple < heightMultiple ? heightMultiple : widthMultiple));
        } else {
            return null;
        }
    }

    /**
     * 视频画面缩放[软件缩放],
     * <p>
     * 视频缩放:缩小视频的宽度和高度\n 这里演示把宽度和高度都缩小一半.\n 注意:这里是采用软缩放的形式来做,流程是:硬解码-->软件缩放-->硬编码
     */
    public static String VideoScale(VideoEditor editor, String srcVideo) {
        MediaInfo info = new MediaInfo(srcVideo);
        if (info.prepare()) {

            int compressWidth = 480;
            int compressHeight = 960;
            int videoWidth = info.getWidth();
            int videoHeight = info.getHeight();
            int width = 0;
            int height = 0;
            double widthMultiple;
            double heightMultiple;
            if (videoWidth > videoHeight) {
                //横视频
                if (videoWidth <= compressHeight || videoHeight <= compressWidth) {
                    widthMultiple = 1.0;
                    heightMultiple = 1.0;
                } else {
                    heightMultiple = videoHeight / (compressWidth * 1.0);
                    widthMultiple = videoWidth / (compressHeight * 1.0);
                }
            } else {
                if (videoWidth <= compressWidth || videoHeight <= compressHeight) {
                    widthMultiple = 1.0;
                    heightMultiple = 1.0;
                } else {
                    widthMultiple = videoWidth / (compressWidth * 1.0);
                    heightMultiple = videoHeight / (compressHeight * 1.0);
                }
            }
            //压缩的倍率
            if (widthMultiple != 1.0 && heightMultiple != 1.0) {
                width = VideoEditor.make16Closest((int) (videoWidth / (widthMultiple > heightMultiple ? heightMultiple : widthMultiple)));
                height = VideoEditor.make16Closest((int) (videoHeight / (widthMultiple > heightMultiple ? heightMultiple : widthMultiple)));
            } else {
                width = 0;
                height = 0;
            }
            return editor.executeScaleVideoFrame(srcVideo, width, height);
        } else {
            return null;
        }

    }

    /**
     * 演示 叠加图片(水印)
     * <p>
     * 视频上增加图片:
     *
     * @param ctx
     * @param editor
     * @param srcVideo
     * @return
     */
    public static String AddPicture(Context ctx, VideoEditor editor, String srcVideo) throws IOException {
        MediaInfo info = new MediaInfo(srcVideo);
        if (info.prepare()) {
            String[] imagePath = new String[82];
            for (int i = 0; i < 82; i++) {
                String id = String.format("%05d", i);
                imagePath[i] = CopyFileFromAssets.copyAssets(ctx, "logo/", "shuiyin_" + id + ".png");
            }
            return editor.executeOverLayVideoFrame(srcVideo, imagePath, 0, 0);
        } else {
            return null;
        }
    }

    /**
     * 压缩和加水印
     *
     * @param ctx
     * @param editor
     * @param srcVideo
     * @return
     * @throws IOException
     */
    public static String videoScaleAddPicture(Context ctx, VideoEditor editor, String srcVideo) throws IOException {
        MediaInfo info = new MediaInfo(srcVideo);
        if (info.prepare()) {
            String[] imagePath = new String[82];
            for (int i = 0; i < 82; i++) {
                String id = String.format("%05d", i);
                imagePath[i] = CopyFileFromAssets.copyAssets(ctx, "logo/", "shuiyin_" + id + ".png");
            }
            int compressWidth = 480;
            int compressHeight = 960;
            int videoWidth = info.getWidth();
            int videoHeight = info.getHeight();
            int width = 0;
            int height = 0;
            double widthMultiple;
            double heightMultiple;
            if (videoWidth > videoHeight) {
                //横视频
                if (videoWidth <= compressHeight || videoHeight <= compressWidth) {
                    widthMultiple = 1.0;
                    heightMultiple = 1.0;
                } else {
                    heightMultiple = videoHeight / (compressWidth * 1.0);
                    widthMultiple = videoWidth / (compressHeight * 1.0);
                }
            } else {
                if (videoWidth <= compressWidth || videoHeight <= compressHeight) {
                    widthMultiple = 1.0;
                    heightMultiple = 1.0;
                } else {
                    widthMultiple = videoWidth / (compressWidth * 1.0);
                    heightMultiple = videoHeight / (compressHeight * 1.0);
                }
            }
            //压缩的倍率
            if (widthMultiple != 1.0 && heightMultiple != 1.0) {
                width = VideoEditor.make16Closest((int) (videoWidth / (widthMultiple > heightMultiple ? heightMultiple : widthMultiple)));
                height = VideoEditor.make16Closest((int) (videoHeight / (widthMultiple > heightMultiple ? heightMultiple : widthMultiple)));
            } else {
                width = 0;
                height = 0;
            }
            return editor.executeScaleAndOverLayVideoFrame(srcVideo, imagePath, width, height, 0, 0);
        } else {
            return null;
        }
    }

    /**
     * 演示 叠加图片(水印)
     * <p>
     * 视频上增加图片:
     */
    public static String demoAddPicture(Context ctx, VideoEditor editor, String srcVideo) {
        MediaInfo info = new MediaInfo(srcVideo);
        if (info.prepare()) {
            String imagePath = CopyFileFromAssets.copyAssets(ctx, "watermark.png");
            return editor.executeOverLayVideoFrame(srcVideo, imagePath, 20, 20);
        } else {
            return null;
        }
    }


    public static String AddVideoEndPicture(VideoEditor editor, String srcVideo, String imagePath,
                                            String path, String name, int videoType) {
        return editor.executeAddPitureXYTimeScale(srcVideo, imagePath,
                1.3f, 3.0f, path, name);
    }
}
