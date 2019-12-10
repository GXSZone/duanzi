package com.caotu.duanzhi.module.search;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.FastClickListener;
import com.caotu.duanzhi.view.widget.AvatarWithNameLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class SearchResultFragment extends BaseStateFragment<UserBean> implements
        BaseQuickAdapter.OnItemClickListener, SearchDate {
    String searchWord;

    @Override
    protected void initView(View inflate) {
        super.initView(inflate);
        mSwipeLayout.setEnableRefresh(false);
    }

    @Override
    public int getEmptyImage() {
        return R.mipmap.no_tiezi;
    }

    @Override
    public String getEmptyText() {
        return "哎呀，什么都没有找到";
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        adapter = new SearchAtUserAdapter();
        adapter.setOnItemClickListener(this);
        return adapter;
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        if (TextUtils.isEmpty(searchWord)) return;
        if (DateState.init_state == load_more) return;
        HashMap<String, String> hashMapParams = CommonHttpRequest.getInstance().getHashMapParams();
        hashMapParams.put("pageno", position + "");
        hashMapParams.put("pagesize", pageSize);
        hashMapParams.put("querystr", searchWord);
        OkGo.<BaseResponseBean<List<UserBaseInfoBean.UserInfoBean>>>post(HttpApi.SEARCH_USER)
                .upJson(new JSONObject(hashMapParams))
                .execute(new JsonCallback<BaseResponseBean<List<UserBaseInfoBean.UserInfoBean>>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<List<UserBaseInfoBean.UserInfoBean>>> response) {
                        List<UserBean> list = DataTransformUtils.changeSearchUser(response.body().getData());
                        setDate(load_more, list);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<List<UserBaseInfoBean.UserInfoBean>>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });

    }

    @Override
    public void setDate(String trim) {
        searchWord = trim;
        //注意索引
        position = 1;
        getNetWorkDate(DateState.refresh_state);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        UserBean content = (UserBean) adapter.getData().get(position);
        if (getActivity() instanceof SearchActivity) {
            ((SearchActivity) getActivity()).backSetResult(content);
        }
    }

    /**
     * 现在就该处用到,以后要是有复用的再抽出去单独成类
     */
    public class SearchAtUserAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {

        public SearchAtUserAdapter() {
            super(R.layout.item_user_info);
        }

        @Override
        protected void convert(BaseViewHolder helper, UserBean item) {
            AvatarWithNameLayout nameLayout = helper.getView(R.id.group_user_avatar);
            nameLayout.setUserText(item.username, "段友号：" + item.uno);
            nameLayout.load(item.userheadphoto, item.guajianurl, item.authpic);

            //关注按钮的模版代码
            TextView follow = helper.getView(R.id.iv_selector_is_follow);
            follow.setVisibility(MySpUtils.isMe(item.userid) ? View.GONE : View.VISIBLE);
            follow.setText(item.isFocus ? "已关注" : "+  关注");
            follow.setEnabled(!item.isFocus);
            follow.setTag(UmengStatisticsKeyIds.follow_user);
            follow.setOnClickListener(new FastClickListener() {
                @Override
                protected void onSingleClick() {
                    CommonHttpRequest.getInstance().requestFocus(item.userid, "2", true,
                            new JsonCallback<BaseResponseBean<String>>() {
                                @Override
                                public void onSuccess(Response<BaseResponseBean<String>> response) {
                                    follow.setText("已关注");
                                    follow.setEnabled(false);
                                    item.isFocus = true;
                                    ToastUtil.showShort("关注成功");
                                }
                            });
                }
            });
            helper.setGone(R.id.tv_user_auth_name, !TextUtils.isEmpty(item.authname));
            helper.setText(R.id.tv_user_auth_name, item.authname);
        }
    }
}
