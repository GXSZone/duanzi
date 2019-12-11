package com.caotu.duanzhi.module.search;

import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.TopicInfoBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.ParserUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.FastClickListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * 后面当做搜索的分栏fragment,除了综合的分栏不一样
 */
public class SearchTopicFragment extends SearchBaseFragment<TopicInfoBean> {

    @Override
    protected BaseQuickAdapter getAdapter() {
        adapter = new SearchTopicAdapter();
        adapter.setOnItemClickListener(this);
        return adapter;
    }

    @Override
    protected void httpRequest(int load_more, HashMap<String, String> hashMapParams) {
        OkGo.<BaseResponseBean<List<TopicInfoBean>>>post(HttpApi.SEARCH_TOPIC)
                .upJson(new JSONObject(hashMapParams))
                .execute(new JsonCallback<BaseResponseBean<List<TopicInfoBean>>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<List<TopicInfoBean>>> response) {
                        setDate(load_more, response.body().getData());
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<List<TopicInfoBean>>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }

    @Override
    protected void clickItem(TopicInfoBean date) {
        HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic, date.getTagid());
    }


    public class SearchTopicAdapter extends BaseQuickAdapter<TopicInfoBean, BaseViewHolder> {

        private int color;

        public SearchTopicAdapter() {
            super(R.layout.item_search_topic);
            color = DevicesUtils.getColor(R.color.color_FF698F);
        }

        @Override
        protected void convert(BaseViewHolder helper, TopicInfoBean item) {
            helper.setText(R.id.tv_topic_title,
                    ParserUtils.setMarkupText(item.getTagalias(), searchWord, color));
            ImageView topicImage = helper.getView(R.id.iv_topic_image);
            GlideUtils.loadImage(item.getTagimg(), R.mipmap.shenlue_logo, topicImage);
            helper.setText(R.id.topic_user_num,
                    Int2TextUtils.toText(item.activecount).concat("段友参与讨论"));

            boolean isFollow = LikeAndUnlikeUtil.isLiked(item.getIsfollow());

            TextView follow = helper.getView(R.id.tv_user_follow);
            follow.setEnabled(!isFollow);
            follow.setText(isFollow ? "已关注" : "+  关注");
            follow.setTag(UmengStatisticsKeyIds.follow_topic);
            follow.setOnClickListener(new FastClickListener() {
                @Override
                protected void onSingleClick() {
                    CommonHttpRequest.getInstance().requestFocus(item.getTagid(), "1", true, new JsonCallback<BaseResponseBean<String>>() {
                        @Override
                        public void onSuccess(Response<BaseResponseBean<String>> response) {
                            follow.setText("已关注");
                            follow.setEnabled(false);
                            item.setIsfollow("1");
                            ToastUtil.showShort("关注成功");
                        }
                    });
                }
            });
        }
    }
}
