package com.caotu.duanzhi.module.other;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.FastClickListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
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
    private TextView mTvTopicTitle;
    private ImageView mIvSelectorIsFollow;
    private LinearLayout layout;

    @Override
    public int getEmptyViewLayout() {
        return R.layout.layout_empty_has_header;
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new MomentsNewAdapter() {
            @Override
            public boolean hasTag(MomentsDataBean item, TextView contentView, TextView stateView, boolean ishowTag, String contenttext, String tagshow) {
                return false;
            }
        };
    }

    int mScrollY = 0;
    int headerHeight = 326;

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
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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
                });
    }

    private void bindHeader(TopicInfoBean data) {
        if (getActivity() != null && getActivity() instanceof OtherActivity) {
            ((OtherActivity) getActivity()).bindTopic(data);
        }
        GlideUtils.loadImage(data.getTagimg(), mIvUserAvatar);
        mTvTopicTitle.setText(String.format("#%s#", data.getTagalias()));
        //1关注 0未关注
        if (LikeAndUnlikeUtil.isLiked(data.getIsfollow())) {
            mIvSelectorIsFollow.setEnabled(false);
        }
        mIvSelectorIsFollow.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestFocus(topicId, "1", true, new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        mIvSelectorIsFollow.setEnabled(false);
                        if (getActivity() != null && getActivity() instanceof OtherActivity) {
                            ((OtherActivity) getActivity()).changeFollowState();
                        }
                        ToastUtil.showShort("关注成功");
                    }
                });
            }
        });
    }

    public void setDate(String id) {
        topicId = id;
    }


    public void initHeaderView(View view) {
        mIvUserAvatar = view.findViewById(R.id.iv_user_avatar);
        mTvTopicTitle = view.findViewById(R.id.tv_topic_title);
        mIvSelectorIsFollow = view.findViewById(R.id.iv_selector_is_follow);
    }

    public void changeFollow() {
        if (mIvSelectorIsFollow != null) {
            mIvSelectorIsFollow.setEnabled(false);
        }
    }
}
