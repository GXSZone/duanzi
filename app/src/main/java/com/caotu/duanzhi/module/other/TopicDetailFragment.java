package com.caotu.duanzhi.module.other;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.FastClickListener;
import com.caotu.duanzhi.view.widget.ExpandableTextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.RImageView;

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
    private LinearLayout layout;
    private boolean isFollow;
    private TextView mTopicUserNum;
    private ExpandableTextView mExpandTextHeader;
    private LinearLayout mLlHotParent;
    private TextView mHotTopicText;

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new MomentsNewAdapter() {
            @Override
            public void dealTopic(@NonNull BaseViewHolder helper, MomentsDataBean item) {
                helper.setGone(R.id.tv_topic, false);
            }
        };
    }

    int mScrollY = 0;
    int headerHeight = 450;

    @Override
    protected void initViewListener() {
        super.initViewListener();
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.layout_topic_detail_header, mRvContent, false);
        initHeaderView(headerView);
        if (getActivity() != null && getActivity() instanceof OtherActivity) {
            layout = ((OtherActivity) getActivity()).getLayout();
        }
        mRvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                mScrollY += dy;
                float scrollY = Math.min(headerHeight, mScrollY);
                float percent = scrollY / headerHeight;
                percent = Math.min(1, percent);
                if (layout != null) {
                    layout.setAlpha(percent);
                }

            }
        });
        //设置头布局
        adapter.setHeaderView(headerView);
        adapter.setHeaderAndEmpty(true);
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
                        List<MomentsDataBean> data = response.body().getData();
                        setDate(load_more, data);
                        //回调给滑动详情页数据
                        if (DateState.load_more == load_more && dateCallBack != null) {
                            dateCallBack.loadMoreDate(data);
                            dateCallBack = null;
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<List<MomentsDataBean>>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }

    private void bindHeader(TopicInfoBean data) {
        if (getActivity() != null && getActivity() instanceof OtherActivity) {
            ((OtherActivity) getActivity()).bindTopic(data);
        }
        GlideUtils.loadImage(data.getTagimg(), mIvUserAvatar);

        mTvTopicTitle.setText(String.format("#%s#", data.getTagname()));
        //1关注 0未关注
        if (LikeAndUnlikeUtil.isLiked(data.getIsfollow())) {
            changeFollow();
        }
        mIvSelectorIsFollow.setTag(UmengStatisticsKeyIds.follow_topic);
        mIvSelectorIsFollow.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestFocus(topicId, "1", true, new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        changeFollow();
                        if (getActivity() != null && getActivity() instanceof OtherActivity) {
                            ((OtherActivity) getActivity()).changeFollowState();
                        }
                        ToastUtil.showShort("关注成功");
                    }
                });
            }
        });
        if (!TextUtils.isEmpty(data.getTaglead())) {
            mExpandTextHeader.initWidth(DevicesUtils.getSrecchWidth() - DevicesUtils.dp2px(80));
            mExpandTextHeader.setOriginalText(data.getTaglead());
        }

        mTopicUserNum.setText(data.activecount + "人参与");
        MomentsDataBean hotcontent = data.hotcontent;
        if (hotcontent != null) {
            mLlHotParent.setVisibility(View.VISIBLE);
            mHotTopicText.setText(hotcontent.getContenttitle());
            mLlHotParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HelperForStartActivity.openContentDetail(hotcontent);
                }
            });
        } else {
            mLlHotParent.setVisibility(View.GONE);
        }
    }

    public void setDate(String id, boolean hasFollow) {
        topicId = id;
        isFollow = hasFollow;
    }


    public void initHeaderView(View view) {
        mIvUserAvatar = view.findViewById(R.id.iv_user_avatar);
        mTvTopicTitle = view.findViewById(R.id.tv_topic_title);
        mTopicUserNum = view.findViewById(R.id.topic_user_num);
        mIvSelectorIsFollow = view.findViewById(R.id.iv_selector_is_follow);
        mIvSelectorIsFollow.setEnabled(!isFollow);
        mExpandTextHeader = view.findViewById(R.id.expand_text_header);
        mLlHotParent = view.findViewById(R.id.ll_hot_parent);
        mHotTopicText = view.findViewById(R.id.hot_topic_text);
    }

    public void changeFollow() {
        if (mIvSelectorIsFollow != null) {
            mIvSelectorIsFollow.setEnabled(false);
            mIvSelectorIsFollow.setText("√  已关注");
        }
    }
}
