package com.caotu.duanzhi.module.search;

import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.Http.bean.UserFocusBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.view.RvDecoration.StickyDecoration;
import com.caotu.duanzhi.view.RvDecoration.listener.GroupListener;
import com.caotu.duanzhi.view.widget.AvatarWithNameLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ 用户的选择页面,这个包括一开始展示的关注用户,还有搜索后的结果用户
 */
public class AtUserFragment extends BaseStateFragment<UserBean> implements
        BaseQuickAdapter.OnItemClickListener {

    private List<UserBean> atUserList;

    @Override
    protected BaseQuickAdapter getAdapter() {
        StickyDecoration decoration = StickyDecoration.Builder
                .init(new GroupListener() {
                    @Override
                    public String getGroupName(int position) {
                        //组名回调
                        List<UserBean> data = adapter.getData();
                        if (data.size() > position) {
                            return data.get(position).groupId;
                        }
                        return null;
                    }
                })
                .setGroupBackground(DevicesUtils.getColor(R.color.color_f5f6f8))    //背景色
                .setGroupHeight(DevicesUtils.dp2px(40))       //高度
                .setGroupTextColor(DevicesUtils.getColor(R.color.color_3f4557))                     //字体颜色
                .setGroupTextSize(DevicesUtils.dp2px(14))      //字体大小
                .setTextSideMargin(DevicesUtils.dp2px(20))    // 边距   靠左时为左边距  靠右时为右边距
                .build();
        mRvContent.addItemDecoration(decoration);
        return new AtUserAdapter();
    }

    @Override
    protected void initViewListener() {
        mSwipeLayout.setEnableRefresh(false);
        adapter.setOnItemClickListener(this);
        // TODO: 2019-08-28 这里的数据还得加上我本地记录的用户
        atUserList = MySpUtils.getAtUserList();
    }


    @Override
    protected void getNetWorkDate(int load_more) {
        if (!LoginHelp.isLogin()) {
            dealList(null, load_more);
            return;
        }
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("pageno", "" + position);
        map.put("pagesize", "17");
        map.put("followtype", "2");
        map.put("userid", MySpUtils.getMyId());

        OkGo.<BaseResponseBean<UserFocusBean>>post(HttpApi.USER_MY_FOCUS)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<UserFocusBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<UserFocusBean>> response) {
                        List<UserFocusBean.RowsBean> rows = response.body().getData().getRows();
                        dealList(rows, load_more);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<UserFocusBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }

    private void dealList(List<UserFocusBean.RowsBean> rows, int load_more) {
        List<UserBean> list = DataTransformUtils.changeFocusUserToAtUser(rows);
        if (load_more == DateState.init_state || load_more == DateState.refresh_state) {
            if (list == null) {
                list = new ArrayList<>();
            }
            if (atUserList != null && !atUserList.isEmpty()) {
                for (UserBean bean : atUserList) {
                    bean.groupId = "我最近@的人";
                }
                list.addAll(0, atUserList);
            }
        }
        setDate(load_more, list);
    }

    @Override
    public int getEmptyImage() {
        return R.mipmap.no_tiezi;
    }

    @Override
    public String getEmptyText() {
        return "你还没有@任何人哦～";
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        UserBean content = (UserBean) adapter.getData().get(position);
        if (getActivity() instanceof SearchActivity) {
            ((SearchActivity) getActivity()).backSetResult(content);
        }
    }

    /**
     * 直接用内部类的形式,只有改页面用到
     */
    public class AtUserAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {
        public AtUserAdapter() {
            super(R.layout.item_user_info);
        }

        @Override
        protected void convert(BaseViewHolder helper, UserBean item) {
            helper.setGone(R.id.iv_selector_is_follow,false);
            //"我关注的人" : "我最近@的人"
            AvatarWithNameLayout nameLayout = helper.getView(R.id.group_user_avatar);
            //第二个参数待定
            nameLayout.setUserText(item.username, item.authname);
            nameLayout.load(item.userheadphoto, item.guajianurl, item.authpic);
        }
    }
}
