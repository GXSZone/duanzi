package com.caotu.duanzhi.module.other;

import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.AuthBean;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.RedundantBean;
import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.UmengHelper;
import com.caotu.duanzhi.UmengStatisticsKeyIds;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseVideoFragment;
import com.caotu.duanzhi.other.AndroidInterface;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.FastClickListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.RImageView;
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
    private RImageView mIvUserAvatar;
    /**
     * 关注
     */
    private RTextView mEditInfo;
    private TextView mTvPraiseCount, mTvFocusCount, mTvFansCount, postCount;
    private int fanNumber;
    private TextView mUserNum, mUserSign, userAuthAName, mTvUserName;
    private GlideImageView userLogos;
    private LinearLayout hasMedal;
    private GlideImageView medalOneImage, medalTwoImage, userBg, userGuanjian;
    private View titleBar;
    private ImageView citizen_web;

    @Override
    public int getEmptyViewLayout() {
        return R.layout.layout_empty_has_header;
    }

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
                            mSwipeLayout.finishRefresh();

//                            mSwipeLayout.setRefreshing(false);
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
        params.put("userid", userId);
        OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.USER_WORKSHOW)
                .upJson(new JSONObject(params))
                .execute(new JsonCallback<BaseResponseBean<RedundantBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<RedundantBean>> response) {
                        List<MomentsDataBean> rows = response.body().getData().getRows();
                        setDate(load_more, rows);
                        //回调给滑动详情页数据
                        if (DateState.load_more == load_more && dateCallBack != null) {
                            dateCallBack.loadMoreDate(rows);
                            dateCallBack = null;
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<RedundantBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });

    }

    private UserBaseInfoBean userBaseInfoBean;

    private void bindUserInfo(UserBaseInfoBean data) {
        userBaseInfoBean = data;
        mTvPraiseCount.setText(Int2TextUtils.toText(data.getGoodCount()));
        mTvFansCount.setText(Int2TextUtils.toText(data.getBeFollowCount()));
        mTvFocusCount.setText(Int2TextUtils.toText(data.getFollowCount()));
        postCount.setText(Int2TextUtils.toText(data.getContentCount()));
        UserBaseInfoBean.UserInfoBean userInfo = data.getUserInfo();
        GlideUtils.loadImage(userInfo.getUserheadphoto(), R.mipmap.touxiang_moren, mIvUserAvatar);
        //头像挂件
        userGuanjian.load(userInfo.getGuajianurl());
        if (userInfo.getCardinfo() != null && userInfo.getCardinfo().cardurljson != null) {
            userBg.load(userInfo.getCardinfo().cardurljson.getBgurl(), R.mipmap.my_bg_moren);
        } else {
            userBg.load("", R.mipmap.my_bg_moren);
        }
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
        //默认是男头像
        Drawable rightIconSex = DevicesUtils.getDrawable(R.mipmap.my_boy);
        if ("1".equals(userInfo.getUsersex())) {
            rightIconSex = DevicesUtils.getDrawable(R.mipmap.my_girl);
        }
        rightIconSex.setBounds(0, 0, rightIconSex.getMinimumWidth(), rightIconSex.getMinimumHeight());
        mTvUserName.setCompoundDrawables(null, null, rightIconSex, null);
        if (!TextUtils.isEmpty(userInfo.getUsersign())) {
            mUserSign.setText(userInfo.getUsersign());
        }
        if (!TextUtils.isEmpty(userInfo.getUno())) {
            mUserNum.setVisibility(View.VISIBLE);
            mUserNum.setText(String.format("段友号:%s", userInfo.getUno()));
        } else {
            mUserNum.setVisibility(View.GONE);
        }

        AuthBean auth = data.getUserInfo().getAuth();
        if (auth != null && !TextUtils.isEmpty(auth.getAuthid())) {
            String coverUrl = VideoAndFileUtils.getCover(auth.getAuthpic());
            userLogos.load(coverUrl);
            userLogos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebActivity.openWeb("用户勋章", auth.getAuthurl(), true);
                }
            });
            if (!TextUtils.isEmpty(auth.getAuthword())) {
                userAuthAName.setVisibility(View.VISIBLE);
                userAuthAName.setText(auth.getAuthword());
            }
        }

        List<UserBaseInfoBean.UserInfoBean.HonorlistBean> honorlist = userInfo.getHonorlist();
        if (honorlist != null && honorlist.size() > 0) {
            hasMedal.setVisibility(View.VISIBLE);
            medalOneImage.load(honorlist.get(0).levelinfo.pic2);
            if (honorlist.size() >= 2) {
                medalTwoImage.load(honorlist.get(1).levelinfo.pic2);
            }
            medalOneImage.setOnClickListener(v ->
                    HelperForStartActivity.openUserMedalDetail(honorlist.get(0)));

            medalTwoImage.setOnClickListener(v -> {
                if (honorlist.size() >= 2) {
                    HelperForStartActivity.openUserMedalDetail(honorlist.get(1));
                }
            });
        } else {
            hasMedal.setVisibility(View.GONE);
        }
        boolean hasCard = userInfo.getCardinfo() == null || userInfo.getCardinfo().cardurljson == null
                || TextUtils.isEmpty(userInfo.getCardinfo().cardurljson.getStyleurl());
        citizen_web.setVisibility(hasCard ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getEmptyImage() {
        return R.mipmap.no_tiezi;
    }

    @Override
    public String getEmptyText() {
        return "他还在修炼，暂时没有发帖哦";
    }

    int mScrollY = 0;
    int headerHeight = DevicesUtils.dp2px(200);

    @Override
    protected void initViewListener() {
        super.initViewListener();
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.other_user_header_view, mRvContent, false);
        initHeaderView(headerView);
        if (getActivity() != null && getActivity() instanceof OtherActivity) {
            titleBar = ((OtherActivity) getActivity()).getTitleBar();
        }
        //设置头布局
        mRvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mScrollY += dy;
//                if (dy == 0 || mScrollY > headerHeight) return;
                float scrollY = Math.min(headerHeight, mScrollY);
                float percent = scrollY / headerHeight;
                percent = Math.min(1, percent);
                if (titleBar != null) {
                    titleBar.setAlpha(percent);
                }
            }
        });
        adapter.setHeaderView(headerView);
        adapter.setHeaderAndEmpty(true);
    }

    public void setDate(String id) {
        userId = id;
    }

    public String getUserId() {
        return userId;
    }

    public void initHeaderView(View view) {
        UmengHelper.event(UmengStatisticsKeyIds.user_detail);
        mIvUserAvatar = view.findViewById(R.id.iv_user_avatar);
        mEditInfo = view.findViewById(R.id.edit_info);
        mEditInfo.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestFocus(userId, "2", true, new JsonCallback<BaseResponseBean<String>>() {
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
        });
        userBg = view.findViewById(R.id.iv_user_bg);
        mTvUserName = view.findViewById(R.id.tv_user_name);
        mTvPraiseCount = view.findViewById(R.id.tv_praise_count);
        mTvFocusCount = view.findViewById(R.id.tv_focus_count);
        mTvFansCount = view.findViewById(R.id.tv_fans_count);
        postCount = view.findViewById(R.id.tv_post_count);
        view.findViewById(R.id.ll_click_focus).setOnClickListener(this);
        view.findViewById(R.id.ll_click_fans).setOnClickListener(this);

        mUserNum = view.findViewById(R.id.tv_user_number);
        mUserSign = view.findViewById(R.id.tv_user_sign);

        userLogos = view.findViewById(R.id.ll_user_logos);
        userAuthAName = view.findViewById(R.id.tv_user_logo_name);

        hasMedal = view.findViewById(R.id.ll_parent_medal);
        medalOneImage = view.findViewById(R.id.iv_medal_one);
        medalTwoImage = view.findViewById(R.id.iv_medal_two);
        userGuanjian = view.findViewById(R.id.iv_user_headgear);
        citizen_web = view.findViewById(R.id.citizen_web);
        citizen_web.setOnClickListener(this);
        view.findViewById(R.id.fl_user_avatar).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.fl_user_avatar:
                if (userBaseInfoBean == null || userBaseInfoBean.getUserInfo() == null) return;
                HelperForStartActivity.openImageWatcher(userBaseInfoBean.getUserInfo().getUserheadphoto(),
                        //挂件H5的跳转链接
                        userBaseInfoBean.getUserInfo().guajianh5url,
                        //挂件的图片url
                        userBaseInfoBean.getUserInfo().getGuajianurl());
                break;
            case R.id.ll_click_focus:
                UmengHelper.event(UmengStatisticsKeyIds.my_follow);
                HelperForStartActivity.openFocus(userId);
                break;
            case R.id.ll_click_fans:
                UmengHelper.event(UmengStatisticsKeyIds.my_fans);
                HelperForStartActivity.openFans(userId);
                break;
            case R.id.citizen_web:
                if (userBaseInfoBean == null) return;
                // String styleurl = userBaseInfoBean.getUserInfo().getCardinfo().cardurljson.getStyleurl();
                String styleurl = userBaseInfoBean.getUserInfo().getCardh5url();
                if (TextUtils.isEmpty(styleurl)) return;
                WebActivity.USER_ID = userBaseInfoBean.getUserInfo().getUserid();
                HelperForStartActivity.checkUrlForSkipWeb("内含公民卡",
                        styleurl, AndroidInterface.type_other_user);
                break;
        }
    }
}
