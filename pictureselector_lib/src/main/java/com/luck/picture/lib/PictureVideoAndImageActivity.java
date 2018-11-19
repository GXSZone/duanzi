package com.luck.picture.lib;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.luck.picture.lib.adapter.BrowseImageAdapter;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;

/**
 * 发表页面的自定义预览图片和视频页面
 */
public class PictureVideoAndImageActivity extends Activity implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, View.OnClickListener {

    private ArrayList<LocalMedia> localMedia;
    private int position;
    private int mPositionWhenPaused = -1;

    private MediaController mMediaController;
    private ImageView videoStartView, videoLeftBack;
    private RelativeLayout videoLyout, imageLyout;
    private VideoView videoPlayer;
    private TextView videoToast;
    private ArrayList<String> imagUrl;
    private TextView imagePosition;
    private ProgressBar imageProgressbar;
    private ImageView imageLeftBack;
    private ViewPager imageViewpager;
    private BrowseImageAdapter mAdapter;
    private int showPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_video);
        initView();
        initData();
    }

    /**
     * 赋值
     */
    private void initData() {
        Intent intent = getIntent();
        localMedia = (ArrayList<LocalMedia>) intent.getSerializableExtra("local_media");
        position = intent.getIntExtra("position", 0);
        LocalMedia media = localMedia.get(position);
        String pictureType = media.getPictureType();
        int mediaType = PictureMimeType.isPictureType(pictureType);
        switch (mediaType) {
            case PictureConfig.TYPE_IMAGE:
                //预览的是图片
                videoLyout.setVisibility(View.GONE);
                imageLyout.setVisibility(View.VISIBLE);
                initImage(localMedia, position);
                break;
            case PictureConfig.TYPE_VIDEO:
                //预览的是视频
                videoLyout.setVisibility(View.VISIBLE);
                imageLyout.setVisibility(View.GONE);
                initVideo(media);
                break;
        }
    }

    /**
     * 预览图片
     *
     * @param localMedia
     * @param position
     */
    private void initImage(ArrayList<LocalMedia> localMedia, int position) {
        imagUrl = new ArrayList<>();
        for (LocalMedia media : localMedia) {
            imagUrl.add(media.getPath());
        }
        showImage(imagUrl, position);
    }

    /**
     * 跳转到图片查看详情页面,另外需要传递整个bean 对象用于展示其他内容
     *
     * @param imgs
     * @param position
     */

    private void showImage(final ArrayList<String> imgs, int position) {
        //todo 传入progressbar
        mAdapter = new BrowseImageAdapter(this, imgs, imageProgressbar);
        imageViewpager.setAdapter(mAdapter);
        mAdapter.addOnPageClickListener(new BrowseImageAdapter.OnPageClickListener() {
            @Override
            public void onClick(View view, int position) {
                finish();
            }
        });
        imageViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                showPosition = position;
                String text = position + 1 + " / " + imgs.size();
                imagePosition.setText(text);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });
        if (position != -1) {
            showPosition = position;
        }
        imageViewpager.setCurrentItem(showPosition);
    }


    /**
     * 播放视频
     *
     * @param media
     */
    private void initVideo(LocalMedia media) {
        String path = media.getPath();
        long duration = media.getDuration();
        if (duration > (5 * 60 + 1) * 1000) {
            videoToast.setText("这条视频时间过长了哟！（>5min）");

        } else if (duration != 0 && duration < 5 * 1000) {
            videoToast.setText("这条视频时间太短了哟！（<5s）");

        } else if (duration == 0 || (duration != 0 && duration >= 5 * 1000 && duration <= (5 * 60 + 1) * 1000)) {
            if (path != null) {
                videoPlayer.setVideoPath(path);
            }
        }

    }

    /**
     * 控件初始化
     */
    private void initView() {
        //视频布局
        videoLyout = findViewById(R.id.picture_video_rl);
        videoPlayer = findViewById(R.id.picture_video_player_js);
        videoToast = findViewById(R.id.picture_video_text_tv);
        videoStartView = (ImageView) findViewById(R.id.picture_video_start_iv);
        videoLeftBack = (ImageView) findViewById(R.id.picture_video_left_back);

        videoStartView.setOnClickListener(this);
        videoLeftBack.setOnClickListener(this);
        mMediaController = new MediaController(this);
        videoPlayer.setOnCompletionListener(this);
        videoPlayer.setOnPreparedListener(this);
        videoPlayer.setMediaController(mMediaController);
        //图片布局
        imageLyout = findViewById(R.id.picture_image_rl);
        imageViewpager = findViewById(R.id.picture_image_viewpager);
        imageLeftBack = findViewById(R.id.picture_image_left_back);
        imageProgressbar = findViewById(R.id.picture_image_progressbar);
        imagePosition = findViewById(R.id.picture_image_position);
        imageLeftBack.setOnClickListener(this);

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        videoStartView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    // video started
                    videoPlayer.setBackgroundColor(Color.TRANSPARENT);
                    return true;
                }
                return false;
            }
        });
    }

    public void onStart() {
        // Play Video
        videoPlayer.start();
        super.onStart();
    }

    @Override
    public void onPause() {
        // Stop video when the activity is pause.
        mPositionWhenPaused = videoPlayer.getCurrentPosition();
        videoPlayer.stopPlayback();
//        MobclickAgent.onPause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaController = null;
        videoPlayer = null;
    }

    @Override
    public void onResume() {
        // Resume video player
        if (mPositionWhenPaused >= 0) {
            videoPlayer.seekTo(mPositionWhenPaused);
            mPositionWhenPaused = -1;
        }
//        MobclickAgent.onResume(this);
        super.onResume();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (R.id.picture_video_left_back == id || R.id.picture_image_left_back == id) {
            finish();
        } else if (R.id.picture_video_start_iv == id) {
            videoPlayer.start();
            videoStartView.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new ContextWrapper(newBase) {
            @Override
            public Object getSystemService(String name) {
                if (Context.AUDIO_SERVICE.equals(name))
                    return getApplicationContext().getSystemService(name);
                return super.getSystemService(name);
            }
        });
    }

}
