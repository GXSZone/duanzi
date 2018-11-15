package com.caotu.duanzhi.module.other;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.sunfusheng.GlideImageView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * @author mac
 * @日期: 2018/11/8
 * @describe TODO
 */
public class TopicDetailFragment extends BaseStateFragment<MomentsDataBean> {
    public String topicId;
    private GlideImageView mIvUserAvatar;
    private TextView mTvTopicTitle;
    private ImageView mIvSelectorIsFollow;

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new MomentsNewAdapter();
    }

    @Override
    protected void initViewListener() {
        // TODO: 2018/11/5 初始化头布局
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.layout_topic_detail_header, mRvContent, false);
        initHeaderView(headerView);
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
                    }
                });
    }

    private void bindHeader(TopicInfoBean data) {
        mIvUserAvatar.load(data.getTagimg(), 0, 3);
        mTvTopicTitle.setText(String.format("#%s#", data.getTagname()));
        //1关注 0未关注
        if ("0".equals(data.getIsfollow())) {
            mIvSelectorIsFollow.setSelected(false);
        } else {
            mIvSelectorIsFollow.setEnabled(false);
        }
    }

    public void setDate(String id) {
        topicId = id;
    }


    public void initHeaderView(View view) {
        mIvUserAvatar = (GlideImageView) view.findViewById(R.id.iv_user_avatar);
        mTvTopicTitle = (TextView) view.findViewById(R.id.tv_topic_title);
        mIvSelectorIsFollow = (ImageView) view.findViewById(R.id.iv_selector_is_follow);
    }
}
