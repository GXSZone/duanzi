package com.lansosdk;

import android.content.Context;

import com.lansosdk.videoeditor.MediaInfo;
import com.lansosdk.videoeditor.VideoEditor;

import java.io.IOException;

/**
 * 注意: 此代码仅作为视频处理的演示使用, 不属于sdk的一部分.
 */
public class VideoFunctions {

    public final static String TAG = "VideoFunctions";

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
            int width = 0;
            int height = 0;
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


}
