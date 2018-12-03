package com.caotu.duanzhi.view;

import android.content.pm.ActivityInfo;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.module.NineAdapter;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.widget.MyExpandTextView;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
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
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3,
                DevicesUtils.dp2px(5), false));
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

    /**
     * 处理显示内容
     *
     * @param contentView
     * @param tagshow
     * @param contenttext
     * @param ishowTag
     * @param tagshowid
     */
    public static void setContentText(MyExpandTextView contentView, String tagshow, String contenttext,
                                      boolean ishowTag, String tagshowid, MomentsDataBean dataBean) {
        if (!TextUtils.isEmpty(tagshow)) {
            String source = "#" + tagshow + "#";
            if (ishowTag) {
                source = source + contenttext;
            }
            SpannableString ss = new SpannableString(source);
            ss.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // TODO: 2018/11/8 话题详情
                    HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic, tagshowid);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }
            }, 0, tagshow.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            ss.setSpan(new ForegroundColorSpan(DevicesUtils.getColor(R.color.color_FF698F)),
                    0, tagshow.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            contentView.setText(ss);
            contentView.setVisibility(View.VISIBLE);

        } else {
            if (ishowTag) {
                contentView.setVisibility(View.VISIBLE);
                contentView.setText(contenttext);
            } else {

                contentView.setText("  fasd  ");
                contentView.setVisibility(View.INVISIBLE);
            }
        }

        contentView.setTextListener(new MyExpandTextView.ClickTextListener() {
            @Override
            public void clickText(View textView) {
                if (BaseConfig.MOMENTS_TYPE_WEB.equals(dataBean.getContenttype())) {
                    CommentUrlBean webList = VideoAndFileUtils.getWebList(dataBean.getContenturllist());
                    WebActivity.openWeb("web", webList.info, false, null);
                } else {
                    HelperForStartActivity.openContentDetail(dataBean, false);
                }
            }
        });

    }
}
