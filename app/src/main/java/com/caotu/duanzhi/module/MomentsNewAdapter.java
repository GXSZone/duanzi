package com.caotu.duanzhi.module;

import android.app.Activity;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.home.adapter.BaseContentAdapter;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.lzy.okgo.model.Response;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.sunfusheng.GlideImageView;

import java.util.List;

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
                    case "6":
                        type = ITEM_AD_TYPE;
                        break;
                    case "7":
                        type = ITEM_USERS_TYPE;
                        break;
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
                .registerItemType(ITEM_ONLY_ONE_IMAGE, R.layout.item_one_image_content)
                .registerItemType(ITEM_AD_TYPE, R.layout.item_ad_type_content)
                .registerItemType(ITEM_USERS_TYPE, R.layout.item_interested_user_layout);
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

    /**
     * 内容列表页自己实现数据绑定操作,这两个类型只在推荐列表展示
     *
     * @param helper
     */
    protected void dealInterestingUsers(BaseViewHolder helper) {
        Activity context = MyApplication.getInstance().getRunningActivity();
        Resources res = context.getResources();
        helper.setOnClickListener(R.id.tv_change_users, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHttpRequest.getInstance().getInterestingUsers();
            }
        });
        List<UserBean> usersList = CommonHttpRequest.getInstance().getUsersList();
        if (!AppUtil.listHasDate(usersList) || usersList.size() < 3) return;
        for (int i = 1; i < usersList.size() + 1; i++) {
            UserBean userBean = usersList.get(i - 1);
            int id = res.getIdentifier("iv_user_image" + i, "id", context.getPackageName());
            ImageView userPhoto = helper.getView(id);
            GlideUtils.loadImage(userBean.userheadphoto, userPhoto, false);

            int id2 = res.getIdentifier("tv_user_name" + i, "id", context.getPackageName());
            helper.setText(id2, userBean.username);

            int id3 = res.getIdentifier("tv_user_des" + i, "id", context.getPackageName());
            helper.setText(id3, userBean.groupId);

            int id4 = res.getIdentifier("iv_follow" + i, "id", context.getPackageName());
            TextView viewFollow = helper.getView(id4);
            viewFollow.setEnabled(!userBean.isFocus);
            viewFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonHttpRequest.getInstance().requestFocus(userBean.userid, "2", true,
                            new JsonCallback<BaseResponseBean<String>>() {
                                @Override
                                public void onSuccess(Response<BaseResponseBean<String>> response) {
                                    viewFollow.setText("已关注");
                                    viewFollow.setEnabled(false);
                                    ToastUtil.showShort("关注成功");
                                }
                            });
                }
            });
        }
    }

    protected void dealItemAdType(@NonNull BaseViewHolder helper) {
        //隔离麻烦的广告请求操作,直接从activity拿,而且只在首页加广告
        Activity activity = MyApplication.getInstance().getRunningActivity();
        if (!(activity instanceof MainActivity)) return;

        ImageView imageView = helper.getView(R.id.iv_item_close);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = helper.getAdapterPosition();
                position -= getHeaderLayoutCount();
                remove(position);
            }
        });

        ViewGroup adContainer = helper.getView(R.id.item_content_ad);
        NativeExpressADView adView = ((MainActivity) activity).getAdView();
        adContainer.removeAllViews();
        adContainer.addView(adView);
    }
}
