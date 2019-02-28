package com.caotu.duanzhi.module.other;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.PathConfig;
import com.caotu.duanzhi.utils.ToastUtil;
import com.lansosdk.videoeditor.VideoEditor;
import com.lansosdk.videoeditor.onVideoEditorProgressListener;

public class TestActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
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
}
