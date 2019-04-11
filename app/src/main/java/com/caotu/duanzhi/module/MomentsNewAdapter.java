package com.caotu.duanzhi.module;

import android.text.TextUtils;

import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.home.adapter.BaseContentAdapter;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.sunfusheng.GlideImageView;

/**
 * 内容展示列表,话题详情下的话题标签都不展示
 * link{https://github.com/razerdp/FriendCircle}
 */

public class MomentsNewAdapter extends BaseContentAdapter {
    //1横 2竖 3图片 4文字  5web

    public MomentsNewAdapter() {
        super(R.layout.item_base_content);

        setMultiTypeDelegate(new MultiTypeDelegate<MomentsDataBean>() {
            @Override
            protected int getItemType(MomentsDataBean entity) {
                //根据你的实体类来判断布局类型
                String contenttype = entity.getContenttype();
                int type;
                if (TextUtils.isEmpty(contenttype)) {
                    type = ITEM_ONLY_ONE_IMAGE;
                    return type;
                }
                switch (contenttype) {
                    //使用视频布局
                    case "1"://横视频
                    case "2"://竖视频
                        type = ITEM_VIDEO_TYPE;
                        break;
                    case "5":
                        type = ITEM_WEB_TYPE;
                        break;
                    //默认也就是纯文本显示
                    default:
                        if (entity.imgList != null && entity.imgList.size() == 1) {
                            type = ITEM_ONLY_ONE_IMAGE;
                        } else {
                            type = ITEM_IMAGE_TYPE;
                        }

                        break;
                }
                return type;
            }
        });
        //Step.2
        getMultiTypeDelegate()
                .registerItemType(ITEM_VIDEO_TYPE, R.layout.item_video_content)
                .registerItemType(ITEM_IMAGE_TYPE, R.layout.item_base_content)
                .registerItemType(ITEM_WEB_TYPE, R.layout.item_web_type)
                .registerItemType(ITEM_ONLY_ONE_IMAGE, R.layout.item_one_image_content);
    }

    @Override
    public void otherViewBind(BaseViewHolder helper, MomentsDataBean item) {
        //Step.3
        switch (helper.getItemViewType()) {
            case ITEM_WEB_TYPE:
                //web类型没有底部点赞等一些操作
                CommentUrlBean webList = VideoAndFileUtils.getWebList(item.getContenturllist());
                GlideImageView imageView = helper.getView(R.id.web_image);
                imageView.load(webList.cover, R.mipmap.shenlue_logo);
                break;
            case ITEM_VIDEO_TYPE:
                //处理视频
                dealVideo(helper, item);
                break;
            case ITEM_IMAGE_TYPE:
            case ITEM_ONLY_ONE_IMAGE:
                dealImages(helper, item);
                break;
        }
    }

//    private void showShareIconTipDialog(BaseViewHolder helper) {
//        if (!MySpUtils.getBoolean(MySpUtils.SP_DOWNLOAD_GUIDE, false) &&
//                helper.getLayoutPosition() == 0) {
//            View TagView = helper.getView(R.id.base_moment_share_iv);
//            if (TagView == null) return;
//            TagView.postDelayed(() -> {
//                GuideHelper guideHelper = new GuideHelper(MyApplication.getInstance().getRunningActivity());
//
//                GuideHelper.TipData tipData1 = new GuideHelper.TipData(R.mipmap.guide_downhere,
//                        Gravity.LEFT | Gravity.TOP, TagView);
//                tipData1.setLocation(DevicesUtils.dp2px(50), DevicesUtils.dp2px(50));
//                guideHelper.addPage(tipData1);
//                guideHelper.show(false);
//            }, 500);
//            MySpUtils.putBoolean(MySpUtils.SP_DOWNLOAD_GUIDE, true);
//        }
//    }
}
