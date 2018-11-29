package com.caotu.duanzhi.view;

import android.content.pm.ActivityInfo;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.caotu.duanzhi.module.NineAdapter;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sunfusheng.util.MediaFileUtils;
import com.sunfusheng.widget.ImageData;

import java.util.ArrayList;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

/**
 * 评论列表的九宫格布局帮助类
 */
public class NineRvHelper {

    public static void ShowNineImage(RecyclerView recyclerView, ArrayList<ImageData> list, String contentid) {
        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 3));
        //不然滑动会有冲突
        recyclerView.setNestedScrollingEnabled(false);
        NineAdapter adapter = new NineAdapter(list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String url = list.get(position).url;
                if (MediaFileUtils.getMimeFileIsVideo(url)) {
                    Jzvd.releaseAllVideos();
                    //直接全屏
                    Jzvd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    JzvdStd.startFullscreen(recyclerView.getContext()
                            , MyVideoPlayerStandard.class, url, "");
                } else {
                    HelperForStartActivity.openImageWatcher(position, list,
                            contentid);
                }
            }
        });
    }
}
