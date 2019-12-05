package com.caotu.duanzhi.module.other;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.TopicInfoBean;
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
import com.caotu.duanzhi.view.widget.ExpandableTextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.RImageView;
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
    private RImageView mIvUserAvatar;
    private TextView mTvTopicTitle, mIvSelectorIsFollow;
    private boolean isFollow;
    private TextView mTopicUserNum;
    private ExpandableTextView mExpandTextHeader;
    private TextView mHotTopicText;
    private GlideImageView mTopicBg;

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
        mTvTopicTitle = inflate.findViewById(R.id.tv_topic_title);
        mTopicUserNum = inflate.findViewById(R.id.topic_user_num);
        mIvSelectorIsFollow = inflate.findViewById(R.id.iv_selector_is_follow);
        mExpandTextHeader = inflate.findViewById(R.id.expand_text_header);
        mHotTopicText = inflate.findViewById(R.id.hot_topic_text);
        mTopicBg = inflate.findViewById(R.id.topic_image_bg);
        changeFollow(isFollow);
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

        mTopicBg.load(data.getTagimg(), R.drawable.my_header_bg, new BlurTransformation(mTopicBg.getContext()));

        mTvTopicTitle.setText(String.format("#%s#", data.getTagalias()));
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
            mHotTopicText.setVisibility(View.VISIBLE);
            mHotTopicText.setText(ParserUtils.htmlToJustAtText(contenttitle));
            mHotTopicText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UmengHelper.event(UmengStatisticsKeyIds.topic_header_hot);
                    HelperForStartActivity.openContentDetail(data.hotcontent);
                }
            });
        } else {
            mHotTopicText.setVisibility(View.GONE);
        }
    }

    public void setDate(String id, boolean hasFollow) {
        topicId = id;
        isFollow = hasFollow;
    }


    public void changeFollow(boolean is_follow) {
        if (mIvSelectorIsFollow == null) return;
        mIvSelectorIsFollow.setEnabled(!is_follow);
        if (is_follow) {
            mIvSelectorIsFollow.setText("√ 已关注");
        } else {
            mIvSelectorIsFollow.setText("+  关注");
        }
    }
}
