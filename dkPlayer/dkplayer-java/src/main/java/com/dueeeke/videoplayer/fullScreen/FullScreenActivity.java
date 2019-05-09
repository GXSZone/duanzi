package com.dueeeke.videoplayer.fullScreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 全屏播放
 * Created by Devlin_n on 2017/4/21.
 */

public class FullScreenActivity extends AppCompatActivity {

    private FullScreenIjkVideoView ijkVideoView;
    private static final String KEY_VIDEO_URL = "VIDEO_URL";
    private static final String KEY_SHAREBEAN = "ShareBean";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ijkVideoView = new FullScreenIjkVideoView(this);
        setContentView(ijkVideoView);
        String videoUrl = getIntent().getStringExtra(KEY_VIDEO_URL);
        ijkVideoView.setUrl(videoUrl);
        FullScreenController controller = new FullScreenController(this);
        ijkVideoView.setVideoController(controller);

        ijkVideoView.start();
    }

    // TODO: 2019-05-09 starter 可以一键生成
    public static void start(Context context, String url, WebShareBean shareBean) {
        Intent starter = new Intent(context, FullScreenActivity.class);
        starter.putExtra(KEY_VIDEO_URL, url);
        starter.putExtra(KEY_SHAREBEAN, shareBean);
        context.startActivity(starter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ijkVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ijkVideoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ijkVideoView.release();
    }

    @Override
    public void onBackPressed() {
        if (!ijkVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
