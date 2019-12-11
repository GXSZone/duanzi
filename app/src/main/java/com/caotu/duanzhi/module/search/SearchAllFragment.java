package com.caotu.duanzhi.module.search;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.RedundantBean;
import com.caotu.duanzhi.Http.bean.SearchAllBean;
import com.caotu.duanzhi.Http.bean.TopicInfoBean;
import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseVideoFragment;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.ParserUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.FastClickListener;
import com.caotu.duanzhi.view.widget.StateView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * 综合搜索
 */
public class SearchAllFragment extends BaseVideoFragment implements
        BaseQuickAdapter.OnItemClickListener, SearchDate {
    String searchWord;
    private String searchid;

    @Override
    public int getPageSize() {
        return 10;
    }

    @Override
    protected void initView(View inflate) {
        super.initView(inflate);
        mStatesView.setCurrentState(StateView.STATE_EMPTY);
    }


    @Override
    protected void getNetWorkDate(int load_more) {
        if (TextUtils.isEmpty(searchWord)) return;
        // TODO: 2019-12-10  加载更多调用接口是搜索内容接口 ,还有只有头布局没有内容
        HashMap<String, String> hashMapParams = CommonHttpRequest.getInstance().getHashMapParams();
        if (DateState.load_more == load_more) {
            hashMapParams.put("pageno", searchid);
            hashMapParams.put("pagesize", pageSize);
            hashMapParams.put("querystr", searchWord);
            OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.SEARCH_CONTENT)
                    .upJson(new JSONObject(hashMapParams))
                    .execute(new JsonCallback<BaseResponseBean<RedundantBean>>() {
                        @Override
                        public void onSuccess(Response<BaseResponseBean<RedundantBean>> response) {
                            searchid = response.body().getData().searchid;
                            setDate(load_more, response.body().getData().getContentList());
                        }

                        @Override
                        public void onError(Response<BaseResponseBean<RedundantBean>> response) {
                            errorLoad();
                            super.onError(response);
                        }
                    });

        } else {
            mStatesView.setCurrentState(StateView.STATE_LOADING);
            hashMapParams.put("pageno", "1");
            hashMapParams.put("pagesize", pageSize);
            hashMapParams.put("querystr", searchWord);
            OkGo.<BaseResponseBean<SearchAllBean>>post(HttpApi.SEARCH_ALL)
                    .upJson(new JSONObject(hashMapParams))
                    .execute(new JsonCallback<BaseResponseBean<SearchAllBean>>() {
                        @Override
                        public void onSuccess(Response<BaseResponseBean<SearchAllBean>> response) {
                            SearchAllBean data = response.body().getData();
                            searchid = data.searchid;
                            dealHeaderView(data);
                            setDate(load_more, data);
                        }

                        @Override
                        public void onError(Response<BaseResponseBean<SearchAllBean>> response) {
                            errorLoad();
                            super.onError(response);
                        }
                    });
        }
    }

    private void dealHeaderView(SearchAllBean data) {
        if (data == null) return;
        List<UserBean> userBeanList = DataTransformUtils.changeSearchUser(data.userList);
        List<TopicInfoBean> tagList = data.tagList;
        if (!AppUtil.listHasDate(userBeanList) &&
                !AppUtil.listHasDate(tagList) &&
                !AppUtil.listHasDate(data.contentList)) {
            adapter.removeAllHeaderView();
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View headerView = inflater.inflate(R.layout.layout_searchall_header, mRvContent, false);
        adapter.setHeaderView(headerView);
        adapter.setHeaderAndEmpty(true);
        View topicMore = headerView.findViewById(R.id.tv_topic_more);
        View userMore = headerView.findViewById(R.id.click_user_more);
        LinearLayout topicParent = headerView.findViewById(R.id.topic_parent);
        LinearLayout userParent = headerView.findViewById(R.id.ll_user_parent);

        //加个判断优化测量
        if (topicParent.getChildCount() > 0) {
            topicParent.removeAllViews();
        }
        if (userParent.getChildCount() > 0) {
            userParent.removeAllViews();
        }
        if (AppUtil.listHasDate(tagList)) {
            topicParent.setVisibility(View.VISIBLE);
            headerView.findViewById(R.id.title1).setVisibility(View.VISIBLE);
            for (int i = 0; i < tagList.size(); i++) {
                if (i == 3) break;
                View inflate = inflater.inflate(R.layout.item_search_topic, topicParent, false);
                bindTopicItem(inflate, tagList.get(i));
                topicParent.addView(inflate);
            }
        } else {
            topicParent.setVisibility(View.GONE);
            headerView.findViewById(R.id.title1).setVisibility(View.GONE);
        }
        if (AppUtil.listHasDate(userBeanList)) {
            userParent.setVisibility(View.VISIBLE);
            headerView.findViewById(R.id.title2).setVisibility(View.VISIBLE);
            for (int i = 0; i < userBeanList.size(); i++) {
                if (i == 4) break;
                View inflate = inflater.inflate(R.layout.layout_search_user_item, userParent, false);
                bindUserItem(inflate, userBeanList.get(i));
                userParent.addView(inflate);
            }
        } else {
            userParent.setVisibility(View.GONE);
            headerView.findViewById(R.id.title2).setVisibility(View.GONE);
        }

        headerView.findViewById(R.id.title3).setVisibility(AppUtil.listHasDate(data.contentList) ?
                View.VISIBLE : View.GONE);

        topicMore.setVisibility(tagList != null && tagList.size() > 3 ? View.VISIBLE : View.GONE);
        userMore.setVisibility(userBeanList != null && userBeanList.size() > 4 ? View.VISIBLE : View.GONE);

        topicMore.setOnClickListener(v -> {
            if (getParentFragment() instanceof SearchParentFragment) {
                ((SearchParentFragment) getParentFragment()).changeItem(3);
            }
        });

        userMore.setOnClickListener(v -> {
            if (getParentFragment() instanceof SearchParentFragment) {
                ((SearchParentFragment) getParentFragment()).changeItem(2);
            }
        });
    }

    private void bindUserItem(View inflate, UserBean userBean) {
        ImageView imageView = inflate.findViewById(R.id.iv_search_user_avatar);
        GlideUtils.loadImage(userBean.userheadphoto, imageView, false);
        TextView userName = inflate.findViewById(R.id.tv_search_user_name);
        //姓名标红
        userName.setText(ParserUtils.setMarkupText(userBean.username, searchWord,
                DevicesUtils.getColor(R.color.color_FF698F)));
        inflate.setOnClickListener(v ->
                HelperForStartActivity.openOther(HelperForStartActivity.type_other_user,
                        userBean.userid));

    }

    private void bindTopicItem(View inflate, TopicInfoBean tagBean) {
        TextView title = inflate.findViewById(R.id.tv_topic_title);
        title.setText(ParserUtils.setMarkupText(tagBean.getTagalias(), searchWord,
                DevicesUtils.getColor(R.color.color_FF698F)));

        ImageView topicImage = inflate.findViewById(R.id.iv_topic_image);
        GlideUtils.loadImage(tagBean.getTagimg(), R.mipmap.shenlue_logo, topicImage);
        TextView userNum = inflate.findViewById(R.id.topic_user_num);
        userNum.setText(Int2TextUtils.toText(tagBean.activecount).concat("段友参与讨论"));

        boolean isFollow = LikeAndUnlikeUtil.isLiked(tagBean.getIsfollow());
        TextView follow = inflate.findViewById(R.id.tv_user_follow);
        follow.setEnabled(!isFollow);
        follow.setText(isFollow ? "已关注" : "+  关注");
        follow.setTag(UmengStatisticsKeyIds.follow_topic);
        follow.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestFocus(tagBean.getTagid(), "1", true, new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        follow.setText("已关注");
                        follow.setEnabled(false);
//                        item.setIsfollow("1");
                        ToastUtil.showShort("关注成功");
                    }
                });
            }
        });
        inflate.setOnClickListener(v ->
                HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic,
                        tagBean.getTagid()));
    }

    @Override
    public void setDate(String trim) {
        if (TextUtils.equals(trim, searchWord)) return;
        if (TextUtils.isEmpty(trim))return;
        searchWord = trim;
        //注意索引
        position = 1;
        getNetWorkDate(DateState.init_state);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.layout_no_refresh;
    }

    @Override
    public int getEmptyImage() {
        return R.mipmap.no_tiezi;
    }

    @Override
    public String getEmptyText() {
        return "找了又找，还是没找到相关内容";
    }


    protected void setDate(int load_more, SearchAllBean date) {
        List<TopicInfoBean> tagList = date.tagList;
        List<UserBaseInfoBean.UserInfoBean> userList = date.userList;
        List<MomentsDataBean> contentList = date.contentList;
        List<MomentsDataBean> newDate = DataTransformUtils.getContentNewBean(contentList);
        if (adapter == null) return;
        if (AppUtil.listHasDate(tagList) || AppUtil.listHasDate(userList)) {
            if (!AppUtil.listHasDate(newDate)) {
                adapter.setEmptyView(new Space(getContext()));
            }
        }
        if (!AppUtil.listHasDate(tagList) &&
                !AppUtil.listHasDate(userList) &&
                !AppUtil.listHasDate(newDate)) {
            mStatesView.setCurrentState(StateView.STATE_EMPTY);
        } else {
            mStatesView.setCurrentState(StateView.STATE_CONTENT);
        }

        if (load_more == DateState.refresh_state || load_more == DateState.init_state) {
            adapter.setNewData(newDate);
            if (newDate != null && newDate.size() < getPageSize()) {
                adapter.loadMoreEnd();
            }
        } else {
            if (AppUtil.listHasDate(newDate)) {
                adapter.addData(newDate);
            }
            if (newDate == null || newDate.size() < getPageSize()) {
                adapter.loadMoreEnd();
            } else {
                adapter.loadMoreComplete();
            }
        }
        position++;
        //回调给滑动详情页数据
        if (DateState.load_more == load_more && dateCallBack != null) {
            dateCallBack.loadMoreDate(newDate);
            dateCallBack = null;
        }
    }
}
