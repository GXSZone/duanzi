package com.caotu.duanzhi.module.home.adapter;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.home.fragment.CallBackTextClick;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NineLayoutHelper;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.NineRvHelper;
import com.caotu.duanzhi.view.widget.MyExpandTextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.sunfusheng.widget.ImageCell;
import com.sunfusheng.widget.ImageData;
import com.sunfusheng.widget.NineImageView;

import java.util.ArrayList;

/**
 * 内容展示列表,话题详情下的话题标签都不展示
 */

public class PhotoAdapter extends BaseQuickAdapter<MomentsDataBean, BaseViewHolder> {
    public static final int ITEM_ONLY_ONE_IMAGE = 1;
    public static final int ITEM_IMAGE_TYPE = 2;

    public PhotoAdapter() {
        super(R.layout.item_base_content);
        setMultiTypeDelegate(new MultiTypeDelegate<MomentsDataBean>() {
            @Override
            protected int getItemType(MomentsDataBean momentsDataBean) {
                int type;
                ArrayList<ImageData> imgList = VideoAndFileUtils.getImgList(momentsDataBean.getContenturllist(),
                        momentsDataBean.getContenttext());
                if (imgList != null && imgList.size() == 1) {
                    type = ITEM_ONLY_ONE_IMAGE;
                } else {
                    type = ITEM_IMAGE_TYPE;
                }
                return type;
            }
        });

        //Step.2
        getMultiTypeDelegate()
                .registerItemType(ITEM_IMAGE_TYPE, R.layout.item_base_content)
                .registerItemType(ITEM_ONLY_ONE_IMAGE, R.layout.item_one_image_content);
    }

    /**
     * 文本的点击事件回调给fragment统一处理
     */
    public CallBackTextClick textClick;

    public void setTextClick(CallBackTextClick textClick) {
        this.textClick = textClick;
    }

    @Override
    protected void convert(BaseViewHolder helper, MomentsDataBean item) {
        /*--------------------------点击事件,为了bean对象的获取-------------------------------*/
//        helper.addOnClickListener(R.id.base_moment_avatar_iv);
        helper.addOnClickListener(R.id.item_iv_more_bt);
        ImageView moreAction = helper.getView(R.id.item_iv_more_bt);
        moreAction.setImageResource(getMoreImage(item.getContentuid()));
        helper.addOnClickListener(R.id.base_moment_share_iv)
                .addOnClickListener(R.id.base_moment_comment);

        NineRvHelper.dealLikeAndUnlike(helper, item);

        ImageView avatar = helper.getView(R.id.base_moment_avatar_iv);
        ImageView auth = helper.getView(R.id.user_auth);
        TextView userName = helper.getView(R.id.base_moment_name_tv);
        NineRvHelper.bindItemHeader(avatar, auth, userName, item);

        MyExpandTextView contentView = helper.getView(R.id.layout_expand_text_view);
        //判断是否显示话题 1可见，0不可见
        String tagshow = item.getTagshow();
        NineRvHelper.setContentText(contentView, tagshow, item.getContenttitle(),
                "1".equals(item.getIsshowtitle()), item.getTagshowid(), item);
        contentView.setTextListener(new MyExpandTextView.ClickTextListener() {
            @Override
            public void clickText(View textView) {
                if (textClick != null) {
                    textClick.textClick(item, getPositon(helper));
                }
            }
        });
        MomentsDataBean.BestmapBean bestmap = item.getBestmap();
        if (bestmap != null && bestmap.getCommentid() != null) {
            helper.setGone(R.id.rl_best_parent, true);
            NineRvHelper.dealBest(helper, bestmap, item.getBestauth(),item.getContentid());
        } else {
            helper.setGone(R.id.rl_best_parent, false);
        }

        //处理九宫格
        dealNineLayout(item, helper);
    }

    private int getPositon(BaseViewHolder helper) {
        if (helper.getLayoutPosition() >= getHeaderLayoutCount()) {
            return helper.getLayoutPosition() - getHeaderLayoutCount();
        }
        return 0;
    }

    /**
     * 针对我的帖子的特殊之处抽离出来
     *
     * @return
     */
    public int getMoreImage(String userId) {
        if (MySpUtils.isMe(userId)) {
            return R.mipmap.my_tiezi_delete;
        }
        return R.mipmap.home_more;
    }


    private void dealNineLayout(MomentsDataBean item, BaseViewHolder helper) {
        //神评区的显示隐藏在上面判断

        String contenturllist = item.getContenturllist();
        ArrayList<ImageData> imgList = VideoAndFileUtils.getImgList(contenturllist, item.getContenttext());
        if (imgList == null || imgList.size() == 0) {
            ImageCell oneImage = helper.getView(R.id.only_one_image);
            if (oneImage != null) {
                oneImage.setVisibility(View.GONE);
            }
            NineImageView multiImageView = helper.getView(R.id.base_moment_imgs_ll);
            if (multiImageView != null) {
                multiImageView.setVisibility(View.GONE);
            }
            return;
        }
        Log.i("photoType", "dealNineLayout: " + imgList.size());
        //区分是单图还是多图
        if (imgList.size() == 1) {
            ImageCell oneImage = helper.getView(R.id.only_one_image);
            oneImage.setVisibility(View.VISIBLE);
            oneImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HelperForStartActivity.openImageWatcher(0, imgList, item.getContentid());
                }
            });
            int max = DevicesUtils.getSrecchWidth() - DevicesUtils.dp2px(40);
            int min = max / 3;
            int width = imgList.get(0).realWidth;
            int height = imgList.get(0).realHeight;

            if (width > 0 && height > 0) {
                float whRatio = width * 1f / height;
                if (width > height) {
                    width = Math.max(min, Math.min(width, max));
                    height = Math.max(min, (int) (width / whRatio));
                } else {
                    height = Math.max(min, Math.min(height, max));
                    width = Math.max(min, (int) (height * whRatio));
                }
            } else {
                width = min;
                height = min;
            }
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) oneImage.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;
            oneImage.setLayoutParams(layoutParams);
            oneImage.setData(imgList.get(0));
        } else {
            NineImageView multiImageView = helper.getView(R.id.base_moment_imgs_ll);
            multiImageView.setVisibility(View.VISIBLE);
            multiImageView.loadGif(false)
                    .enableRoundCorner(false)
                    .setData(imgList, NineLayoutHelper.getInstance().getLayoutHelper(imgList));

            multiImageView.setOnItemClickListener(new NineImageView.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    HelperForStartActivity.openImageWatcher(position, imgList,
                            item.getContentid());
                }
            });
        }

    }
}
