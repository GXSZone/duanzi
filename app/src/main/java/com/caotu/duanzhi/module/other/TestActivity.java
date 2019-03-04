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
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.PathConfig;
import com.caotu.duanzhi.utils.ToastUtil;
import com.lansosdk.videoeditor.VideoEditor;
import com.lansosdk.videoeditor.onVideoEditorProgressListener;

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
        long time1 = System.currentTimeMillis();
        String video1 = PathConfig.LOCALFILE + "191317154973water.mp4";
        String video2 = PathConfig.LOCALFILE + "1921414513767water.mp4";
        if (mEditor == null) {
            mEditor = new VideoEditor();
            mEditor.setOnProgessListener(new onVideoEditorProgressListener() {

                @Override
                public void onProgress(VideoEditor v, int percent) {
//                    Log.i("jindu", "onProgress: " + percent);
                }
            });
        }

        String videoSrc = mEditor.executeConcatMP4(new String[]{video1, video2});
        long time2 = System.currentTimeMillis();
        Log.i("sudu", "time: " + (time2 - time1));
        ToastUtil.showShort(videoSrc);
//        ImageView viewById = findViewById(R.id.change_imageview);
//        TextView text = findViewById(R.id.textview_color);
//        if (change) {
//            viewById.setColorFilter(DevicesUtils.getColor(R.color.transparent));
//            setDrawableColor(text, Color.parseColor("#C7C7C7"));
//        } else {
//            viewById.setColorFilter(Color.parseColor("#6D5444"));
//            setDrawableColor(text, DevicesUtils.getColor(R.color.color_bottom_selector));
//        }
//
//        change = !change;
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
        /**
         * 从相册中选择视频
         */

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 66);


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
        mVideoPath = findViewById(R.id.video_path);
    }
}
