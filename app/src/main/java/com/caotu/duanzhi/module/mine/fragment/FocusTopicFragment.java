package com.caotu.duanzhi.module.mine.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.Http.bean.UserFocusBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.refresh_header.MyListMoreView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author mac
 * @日期: 2018/11/5
 * @describe TODO
 */
public class FocusTopicFragment extends BaseStateFragment<UserBean> implements
        BaseQuickAdapter.OnItemClickListener {

    String mUserId;
    boolean isMe;

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new FocusTopicAdapter();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.layout_no_refresh;
    }

    @Override
    protected void initViewListener() {
        adapter.setOnItemClickListener(this);
        adapter.setLoadMoreView(new MyListMoreView());
    }

    /**
     * 设置数据,关键参数:用户id,和是否是本人(UI相关)
     */
    public void setDate(String userId, boolean isMine) {
        mUserId = userId;
        isMe = isMine;
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("pageno", "" + position);
        map.put("pagesize", "20");
        //关注用户和话题的区别
        map.put("followtype", "1");
        map.put("userid", mUserId);

        OkGo.<BaseResponseBean<UserFocusBean>>post(HttpApi.USER_MY_FOCUS)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<UserFocusBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<UserFocusBean>> response) {
                        List<UserFocusBean.RowsBean> rows = response.body().getData().getRows();
                        List<UserBean> beans = DataTransformUtils.getMyFocusDataBean(rows, isMe, true);
                        setDate(load_more, beans);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<UserFocusBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }

    @Override
    public int getEmptyImage() {
        return R.mipmap.no_guanzhu;
    }

    @Override
    public String getEmptyText() {
        return "还没有关注任何话题哦";
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        // TODO: 2018/11/5 话题详情
        UserBean content = (UserBean) adapter.getData().get(position);
        HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic, content.userid);
    }

    /**
     * 内部实现
     */
    public class FocusTopicAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {
        public FocusTopicAdapter() {
            super(R.layout.item_search_topic);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, UserBean item) {
            helper.setText(R.id.tv_topic_title, item.username);
            ImageView topicImage = helper.getView(R.id.iv_topic_image);
            GlideUtils.loadImage(item.userheadphoto, R.mipmap.shenlue_logo, topicImage);
            helper.setGone(R.id.topic_user_num, false);

            TextView follow = helper.getView(R.id.tv_user_follow);
            if (item.isMe) {
                follow.setText("取消关注");
            } else {
                follow.setEnabled(!item.isFocus);
                follow.setText(item.isFocus ? "已关注" : "+  关注");
            }

            follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //只有关注操作,没有取消关注的操作,只有在自己主页才能取消,他人主页下关注完后不能取消关注了
                    if (item.isMe) {
                        requestFocus(item, follow, helper.getAdapterPosition(), false, item.userid, true);
                    } else {
                        if (item.isFocus) return;
                        requestFocus(item, follow, helper.getAdapterPosition(), true, item.userid, false);
                    }
                }
            });
        }

        public void requestFocus(UserBean item, TextView v, int adapterPosition, boolean b, String userId, boolean isMe) {
            CommonHttpRequest.getInstance().requestFocus(userId, "1", b, new JsonCallback<BaseResponseBean<String>>() {
                @Override
                public void onSuccess(Response<BaseResponseBean<String>> response) {
                    if (isMe) {
                        remove(adapterPosition);
                        ToastUtil.showShort("取消关注成功！");
                    } else {
                        item.isFocus = true;
                        ToastUtil.showShort("关注成功！");
                        v.setText("已关注");
                        v.setEnabled(false);
                        UmengHelper.event(UmengStatisticsKeyIds.follow_topic);
                    }
                }
            });
        }
    }
}