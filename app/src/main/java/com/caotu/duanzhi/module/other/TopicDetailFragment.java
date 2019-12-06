package com.caotu.duanzhi.module.other;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.TopicInfoBean;
import com.caotu.duanzhi.Http.bean.TopicItemBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.MomentsNewAdapter;
import com.caotu.duanzhi.module.base.BaseVideoFragment;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.ParserUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.FastClickListener;
import com.caotu.duanzhi.view.shadowLayout.ShadowLinearLayout;
import com.caotu.duanzhi.view.widget.ExpandableTextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.material.appbar.AppBarLayout;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.sunfusheng.GlideImageView;
import com.sunfusheng.transformation.BlurTransformation;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * @author mac
 * @日期: 2018/11/8
 * @describe TODO
 */
public class TopicDetailFragment extends BaseVideoFragment {
    public String topicId;
    private ImageView mIvUserAvatar, mSmallTopicImage, mIvGoPublish;
    private TextView mTvTopicTitle, mSmallTopicTitle, mIvSelectorIsFollow,
            mSmallFollow, mTopicUserNum, mHotTopicText;
    private boolean isFollow;
    private ExpandableTextView mExpandTextHeader;
    private GlideImageView mTopicBg;
    private View backIv;
    private ShadowLinearLayout hotParent;
    private View hotSpace;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_topic_detail;
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new MomentsNewAdapter() {
            @Override
            public void dealTopic(@NonNull BaseViewHolder helper, MomentsDataBean item) {
                helper.setGone(R.id.tv_topic, false);
            }
        };
    }

    @Override
    protected void initView(View inflate) {
        super.initView(inflate);
        mIvUserAvatar = inflate.findViewById(R.id.iv_user_avatar);
        mSmallTopicImage = inflate.findViewById(R.id.topic_small_img);

        mTvTopicTitle = inflate.findViewById(R.id.tv_topic_title);
        mSmallTopicTitle = inflate.findViewById(R.id.topic_small_title);

        mIvSelectorIsFollow = inflate.findViewById(R.id.iv_selector_is_follow);
        mSmallFollow = inflate.findViewById(R.id.iv_top_follow);

        mTopicUserNum = inflate.findViewById(R.id.topic_user_num);
        mExpandTextHeader = inflate.findViewById(R.id.expand_text_header);
        hotSpace = inflate.findViewById(R.id.hot_content_gone);
        hotParent = inflate.findViewById(R.id.hot_content_visible);
        mHotTopicText = inflate.findViewById(R.id.hot_topic_text);

        mTopicBg = inflate.findViewById(R.id.topic_image_bg);
        mIvGoPublish = inflate.findViewById(R.id.iv_go_publish);
        backIv = inflate.findViewById(R.id.iv_back);
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
        changeFollow(isFollow);
        setTopMargin(inflate);
    }

    private void setTopMargin(View inflate) {
        RelativeLayout layout = (RelativeLayout) mSmallTopicTitle.getParent();
        View setMb = inflate.findViewById(R.id.header_parent);
        View setTop = inflate.findViewById(R.id.fl_hot_content);

        int barHeight = DevicesUtils.getStatusBarHeight(getContext());
        layout.post(() -> {
            ViewGroup.LayoutParams params = layout.getLayoutParams();
            params.height = DevicesUtils.dp2px(44) + barHeight;
            layout.setLayoutParams(params);

            CoordinatorLayout.LayoutParams params1 = (CoordinatorLayout.LayoutParams) backIv.getLayoutParams();
            params1.topMargin = barHeight;
            backIv.setLayoutParams(params1);


            AppBarLayout.LayoutParams params2 = (AppBarLayout.LayoutParams) setTop.getLayoutParams();
            params2.topMargin = params.height;
            setTop.setLayoutParams(params2);

            AppBarLayout.LayoutParams params3 = (AppBarLayout.LayoutParams) setMb.getLayoutParams();
            params3.bottomMargin = -params.height - DevicesUtils.dp2px(20);
            setMb.setLayoutParams(params3);
        });

        AppBarLayout appBarLayout = inflate.findViewById(R.id.appbar_layout);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                Log.i("AppBarLayout", "onOffsetChanged: " + i);

                //verticalOffset  当前偏移量 appBarLayout.getTotalScrollRange() 最大高度 便宜值
                float alpha = Math.abs(i * 1.0f) / appBarLayout.getTotalScrollRange();
//               hotParent 改变圆角,左右间距
                if (hotParent.getVisibility() == View.VISIBLE) {
                    FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) hotParent.getLayoutParams();
                    params2.leftMargin = params2.rightMargin
                            = (int) (DevicesUtils.dp2px(20) * (1 - alpha));
                    hotParent.setLayoutParams(params2);
                    hotParent.setRadius((int) (DevicesUtils.dp2px(10) * (1 - alpha)));
                }

                layout.setAlpha(alpha);
            }
        });
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        //只有初始化和下拉刷新请求主题相关内容
        if (DateState.init_state == load_more || DateState.refresh_state == load_more) {
            HashMap<String, String> hashMapParams = new HashMap<>();
            hashMapParams.put("tagid", topicId);
            OkGo.<BaseResponseBean<TopicInfoBean>>post(HttpApi.THEME_DETAILS)
                    .upJson(new JSONObject(hashMapParams))
                    .execute(new JsonCallback<BaseResponseBean<TopicInfoBean>>() {
                        @Override
                        public void onSuccess(Response<BaseResponseBean<TopicInfoBean>> response) {
                            TopicInfoBean data = response.body().getData();
                            if (data == null) return;
                            bindHeader(data);
                        }
                    });
        }
        HashMap<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("querytype", "TIME");
        map.put("querytag", topicId);
        map.put("pageno", "" + position);
        map.put("pagesize", pageSize);
        OkGo.<BaseResponseBean<List<MomentsDataBean>>>post(HttpApi.THEME_CONTENT)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<List<MomentsDataBean>>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<List<MomentsDataBean>>> response) {
                        setDate(load_more, response.body().getData());
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<List<MomentsDataBean>>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }

    private void bindHeader(TopicInfoBean data) {

        GlideUtils.loadImage(data.getTagimg(), mIvUserAvatar);
        GlideUtils.loadImage(data.getTagimg(), mSmallTopicImage);
        mTopicBg.load(data.getTagimg(), R.drawable.my_header_bg, new BlurTransformation(mTopicBg.getContext()));

        mTvTopicTitle.setText(String.format("#%s#", data.getTagalias()));
        mSmallTopicTitle.setText(String.format("#%s#", data.getTagalias()));

        //1关注 0未关注
        changeFollow(LikeAndUnlikeUtil.isLiked(data.getIsfollow()));

        mIvSelectorIsFollow.setTag(UmengStatisticsKeyIds.follow_topic);
        mIvSelectorIsFollow.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestFocus(topicId, "1", true, new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        changeFollow(true);
                        ToastUtil.showShort("关注成功");
                    }
                });
            }
        });

        mSmallFollow.setTag(UmengStatisticsKeyIds.follow_topic);
        mSmallFollow.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestFocus(topicId, "1", true, new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        changeFollow(true);
                        ToastUtil.showShort("关注成功");
                    }
                });
            }
        });


        if (!TextUtils.isEmpty(data.getTaglead())) {
            mExpandTextHeader.setVisibility(View.VISIBLE);
            mExpandTextHeader.initWidth(DevicesUtils.getSrecchWidth() - DevicesUtils.dp2px(80));
            mExpandTextHeader.setOriginalText(data.getTaglead());
        } else {
            mExpandTextHeader.setVisibility(View.GONE);
        }
        mTopicUserNum.setVisibility(TextUtils.isEmpty(data.activecount) ? View.INVISIBLE : View.VISIBLE);
        mTopicUserNum.setText(Int2TextUtils.toText(data.activecount).concat("段友参与讨论"));
        if (data.hotcontent == null) return;

        String contenttitle = data.hotcontent.getContenttitle();
        if (!TextUtils.isEmpty(contenttitle)) {
            hotSpace.setVisibility(View.GONE);
            hotParent.setVisibility(View.VISIBLE);
            mHotTopicText.setText(ParserUtils.htmlToJustAtText(contenttitle));
            mHotTopicText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UmengHelper.event(UmengStatisticsKeyIds.topic_header_hot);
                    HelperForStartActivity.openContentDetail(data.hotcontent);
                }
            });
        } else {
            hotParent.setVisibility(View.GONE);
            hotSpace.setVisibility(View.VISIBLE);
        }
        mIvGoPublish.setOnClickListener(v -> {
            TopicItemBean itemBean = DataTransformUtils.changeTopic(data);
            if (itemBean == null) return;
            HelperForStartActivity.openPublishFromTopic(itemBean);
        });
    }

    public void setDate(String id, boolean hasFollow) {
        topicId = id;
        isFollow = hasFollow;
    }


    public void changeFollow(boolean is_follow) {
        if (mIvSelectorIsFollow == null || mSmallFollow == null) return;
        mIvSelectorIsFollow.setEnabled(!is_follow);
        mSmallFollow.setEnabled(!is_follow);
        if (is_follow) {
            mIvSelectorIsFollow.setText("√ 已关注");
            mSmallFollow.setText("√ 已关注");
        } else {
            mIvSelectorIsFollow.setText("+  关注");
            mSmallFollow.setText("+  关注");
        }
    }
}
