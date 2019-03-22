package com.caotu.duanzhi.module.other;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.PathConfig;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.ThreadExecutor;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.lansosdk.videoeditor.MediaInfo;
import com.lansosdk.videoeditor.VideoEditor;
import com.lansosdk.videoeditor.onVideoEditorProgressListener;

import java.io.File;

import cn.jzvd.Jzvd;

/**
 * 指纹识别 代码参考:https://guolin.blog.csdn.net/article/details/81450114
 */
public class TestActivity extends AppCompatActivity {


    private String VIDEOPATH;
    private TextView mVideoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        initView();
    }


    private VideoEditor mEditor;

    public void changeImage(View view) {
        if (mEditor == null) {
            mEditor = new VideoEditor();
            mEditor.setOnProgessListener(new onVideoEditorProgressListener() {

                @Override
                public void onProgress(VideoEditor v, int percent) {
                    Log.i("fileService", "onProgress: " + percent);
                }
            });
        }
        long time1 = System.currentTimeMillis();
        String video1 = PathConfig.LOCALFILE + "123456.mp4";
        MediaInfo info = new MediaInfo(video1);
        if (info.prepare()) {
            Log.i("fileService", "原视频信息:" + info.toString());
        }
        if (info.vRotateAngle != 0) {
            ThreadExecutor.getInstance().executor(new Runnable() {
                @Override
                public void run() {
                    String srcPath = mEditor.executeRotateAngle(video1, 0);
                    if (!TextUtils.isEmpty(srcPath) && new File(srcPath).exists()) {
                        MediaInfo info2 = new MediaInfo(srcPath);
                        if (info2.prepare()) {
                            Log.i("fileService", "旋转后视频信息:" + info2.toString());
                        }
                        //这里本地写死测试竖视频
                        String video2 = PathConfig.getAbsoluteVideoByWaterPath(1);
                        String videoSrc = mEditor.executeConcatMP4(new String[]{srcPath, video2});
                        Log.i("fileService", "拼接后视频路径:" + videoSrc);
                        MediaInfo info3 = new MediaInfo(videoSrc);
                        if (info3.prepare()) {
                            Log.i("fileService", "拼接后视频信息:" + info3.toString());
                        }

                    } else {
                        Log.i("fileService", "旋转视频出错");
                    }
                }
            });
        } else {
            Log.i("fileService", "原视频不需要旋转");
        }


//

//        long time2 = System.currentTimeMillis();
//        Log.i("sudu", "time: " + (time2 - time1));
//        ToastUtil.showShort(videoSrc);

    }

    /**
     * 图片上色
     */
    public void setDrawableColor(TextView textView, int color) {
        Drawable[] drawables = textView.getCompoundDrawables();
        for (int i = 0, size = drawables.length; i < size; i++) {
            if (null != drawables[i]) {
                drawables[i].setColorFilter(new PorterDuffColorFilter(color,
                        PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public void change(View view) {
        String absoluteVideoByWaterPath = PathConfig.getAbsoluteVideoByWaterPath(0);
        String absoluteVideoByWaterPath1 = PathConfig.getAbsoluteVideoByWaterPath(1);
        File file = new File(absoluteVideoByWaterPath);
        File file1 = new File(absoluteVideoByWaterPath1);
        ToastUtil.showShort("file1Water:" + file.exists() + "------file2water:" + file1.exists());
        /**
         * 从相册中选择视频
         */

//        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(i, 66);


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 66 && resultCode == RESULT_OK && null != data) {
            Uri selectedVideo = data.getData();
            String[] filePathColumn = {MediaStore.Video.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedVideo,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            VIDEOPATH = cursor.getString(columnIndex);
            cursor.close();
            mVideoPath.setText(VIDEOPATH);
        }
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
    }

    private void initView() {
        String url = "http://ctkj-1256675270.cos.ap-shanghai.myqcloud.com/CA2A4E6D-8553-46E8-8FB7-79712C455BAB.mp4";
        mVideoPath = findViewById(R.id.video_path);
        MyVideoPlayerStandard video = findViewById(R.id.video);
        video.setThumbImage(url);
        video.setVideoUrl(url, "", false);
        video.postDelayed(new Runnable() {
            @Override
            public void run() {
                video.startVideo();
            }
        }, 2000);
    }

    public void anim(View view) {
        if (mVideoPath != null) {
            LikeAndUnlikeUtil.showNoticeTip(mVideoPath);
        }
    }

    @Override
    protected void onDestroy() {
        Jzvd.releaseAllVideos();
        super.onDestroy();
    }
}
