package com.caotu.duanzhi.module;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.module.download.VideoDownloadHelper;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.view.dialog.ReportDialog;
import com.dueeeke.videoplayer.fullScreen.FullScreenController;
import com.dueeeke.videoplayer.fullScreen.FullScreenIjkVideoView;
import com.dueeeke.videoplayer.listener.VideoListenerAdapter;


/**
 * 全屏播放
 */

public class FullScreenActivity extends AppCompatActivity {

    private FullScreenIjkVideoView ijkVideoView;
    private static final String KEY_VIDEO_URL = "VIDEO_URL";
    private static final String KEY_SHAREBEAN = "ShareBean";
    private WebShareBean shareBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ijkVideoView = new FullScreenIjkVideoView(this);
        setContentView(ijkVideoView);
        String videoUrl = getIntent().getStringExtra(KEY_VIDEO_URL);
        ijkVideoView.setUrl(videoUrl);
        FullScreenController controller = new FullScreenController(this);
        ijkVideoView.setVideoController(controller);
        boolean videoMode = MySpUtils.getBoolean(MySpUtils.SP_VIDEO_AUTO_REPLAY, false);
        ijkVideoView.setLooping(videoMode);
        shareBean = getIntent().getParcelableExtra(KEY_SHAREBEAN);
        controller.setIsMySelf(shareBean.isMySelf);
        controller.setMyVideoOtherListener(new VideoListenerAdapter() {
            @Override
            public void share(byte type) {
                if (shareBean != null) {
                    shareBean.medial = ShareHelper.translationShareType(type);
                    ShareHelper.getInstance().shareWeb(shareBean);
                }
            }

            @Override
            public void download() {
                VideoDownloadHelper.getInstance().startDownLoad(true, shareBean.contentId, videoUrl);
            }

            @Override
            public void clickTopic() {
                showReportDialog();
            }

            @Override
            public void mute() {
                UmengHelper.event(UmengStatisticsKeyIds.volume);
            }
        });
        ijkVideoView.start();
    }

    //举报弹窗需要修改
    private void showReportDialog() {
        ReportDialog dialog = new ReportDialog(this);
        dialog.setIdAndType(shareBean.contentId, shareBean.contentOrComment);
        dialog.show();
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
