package com.caotu.duanzhi.module.other;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.RedundantBean;
import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseVideoFragment;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.RTextView;
import com.sunfusheng.GlideImageView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mac
 * @日期: 2018/11/5
 * @describe 他人主页
 */
public class OtherUserFragment extends BaseVideoFragment implements View.OnClickListener {

    String userId;
    private GlideImageView mIvUserAvatar;
    /**
     * 关注
     */
    private RTextView mEditInfo;
    private TextView mTvUserName;
    /**
     * 352
     */
    private TextView mTvPraiseCount;
    /**
     * 111
     */
    private TextView mTvFocusCount;
    private LinearLayout mLlClickFocus;
    /**
     * 0
     */
    private TextView mTvFansCount;
    private LinearLayout mLlClickFans;
    private int fanNumber;

    @Override
    protected void getNetWorkDate(int load_more) {
        if (DateState.init_state == load_more || DateState.refresh_state == load_more) {
            Map<String, String> map = new HashMap<>();
            map.put("userid", userId);
            OkGo.<BaseResponseBean<UserBaseInfoBean>>post(HttpApi.GET_USER_BASE_INFO)
                    .upJson(new JSONObject(map))
                    .execute(new JsonCallback<BaseResponseBean<UserBaseInfoBean>>() {
                        @Override
                        public void onSuccess(Response<BaseResponseBean<UserBaseInfoBean>> response) {
                            UserBaseInfoBean data = response.body().getData();
                            bindUserInfo(data);
                            mSwipeLayout.setRefreshing(false);
                        }

                        @Override
                        public void onError(Response<BaseResponseBean<UserBaseInfoBean>> response) {
                            errorLoad();
                            super.onError(response);
                        }
                    });
        }
        HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
        params.put("pageno", "" + position);
        params.put("pagesize", pageSize);
        params.put("userid", MySpUtils.getMyId());
        OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.USER_WORKSHOW)
                .upJson(new JSONObject(params))
                .execute(new JsonCallback<BaseResponseBean<RedundantBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<RedundantBean>> response) {
                        List<MomentsDataBean> rows = response.body().getData().getContentList();
                        setDate(load_more, rows);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<RedundantBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });

    }

    private void bindUserInfo(UserBaseInfoBean data) {
        mTvPraiseCount.setText(Int2TextUtils.toText(data.getGoodCount()));
        mTvFansCount.setText(Int2TextUtils.toText(data.getBeFollowCount()));
        mTvFocusCount.setText(Int2TextUtils.toText(data.getFollowCount()));
        UserBaseInfoBean.UserInfoBean userInfo = data.getUserInfo();
        mIvUserAvatar.apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .loadCircle(userInfo.getUserheadphoto(), R.mipmap.ic_launcher);

        mTvUserName.setText(userInfo.getUsername());
        if (getActivity() != null) {
            ((OtherActivity) getActivity()).setTitleText(userInfo.getUsername());
        }

        boolean isFollow = "1".equals(userInfo.getIsfollow());
        mEditInfo.setText(isFollow ? "已关注" : "关注");
        mEditInfo.setEnabled(!isFollow);

        String beFollowCount = data.getBeFollowCount();
        try {
            fanNumber = Integer.parseInt(beFollowCount);
        } catch (NumberFormatException e) {
            fanNumber = -1;
            e.printStackTrace();
        }

    }

    @Override
    public int getEmptyImage() {
        return R.mipmap.no_tiezi;
    }

    @Override
    public String getEmptyText() {
        return "他还在修炼，暂时没有发帖哦";
    }

    @Override
    protected void initViewListener() {
        super.initViewListener();
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.other_user_header_view, mRvContent, false);
        initHeaderView(headerView);
        //设置头布局
        adapter.setHeaderView(headerView);
        adapter.setHeaderAndEmpty(true);
    }

    public void setDate(String id) {
        userId = id;
    }


    public void initHeaderView(View view) {
        mIvUserAvatar = (GlideImageView) view.findViewById(R.id.iv_user_avatar);
        mEditInfo = (RTextView) view.findViewById(R.id.edit_info);
        mEditInfo.setOnClickListener(this);
        mTvUserName = (TextView) view.findViewById(R.id.tv_user_name);
        mTvPraiseCount = (TextView) view.findViewById(R.id.tv_praise_count);
//        mLlClickPraise = (LinearLayout) view.findViewById(R.id.ll_click_praise);
//        mLlClickPraise.setOnClickListener(this);
        mTvFocusCount = (TextView) view.findViewById(R.id.tv_focus_count);
        mLlClickFocus = (LinearLayout) view.findViewById(R.id.ll_click_focus);
        mLlClickFocus.setOnClickListener(this);
        mTvFansCount = (TextView) view.findViewById(R.id.tv_fans_count);
        mLlClickFans = (LinearLayout) view.findViewById(R.id.ll_click_fans);
        mLlClickFans.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.edit_info:
                requestFocus();
                break;
//            case R.id.ll_click_praise:
//                break;
            case R.id.ll_click_focus:
                HelperForStartActivity.openFocus(userId);
                break;
            case R.id.ll_click_fans:
                HelperForStartActivity.openFans(userId);
                break;
        }
    }

    public void requestFocus() {
        CommonHttpRequest.getInstance().<String>requestFocus(userId, "2", true, new JsonCallback<BaseResponseBean<String>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<String>> response) {
                mEditInfo.setText("已关注");
                mEditInfo.setEnabled(false);
                if (mTvFansCount != null && fanNumber != -1) {
                    mTvFansCount.setText(String.valueOf(fanNumber + 1));
                }
                ToastUtil.showShort("关注成功！");
            }
        });

    }
}
