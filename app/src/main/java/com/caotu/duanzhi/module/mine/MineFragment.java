package com.caotu.duanzhi.module.mine;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.NoticeBean;
import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.LazyLoadFragment;
import com.caotu.duanzhi.module.home.ContentDetailActivity;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.RTextView;

public class MineFragment extends LazyLoadFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ImageView mIvTopicImage;
    private RTextView mEditInfo;
    SwipeRefreshLayout swipeRefreshLayout;
    private TextView mTvClickMyPost, mTvClickMyComment, mTvClickMyCollection, mTvMyNotice, mTvClickMyFeedback, mTvClickSetting;
    private TextView praiseCount, focusCount, fansCount, userName, userSign, userNum;
    private View mRedPointNotice;
    private String userid;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView(View inflate) {
        swipeRefreshLayout = inflate.findViewById(R.id.refresh_swipe);
        mIvTopicImage = inflate.findViewById(R.id.iv_user_avatar);
        mIvTopicImage.setElevation(5.0f);
        mEditInfo = inflate.findViewById(R.id.edit_info);
        mEditInfo.setOnClickListener(this);
        userSign = inflate.findViewById(R.id.tv_user_sign);
        mTvClickMyPost = inflate.findViewById(R.id.tv_click_my_post);
        mTvClickMyPost.setOnClickListener(this);
        mTvClickMyComment = inflate.findViewById(R.id.tv_click_my_comment);
        mTvClickMyComment.setOnClickListener(this);
        mTvClickMyCollection = inflate.findViewById(R.id.tv_click_my_collection);
        mTvClickMyCollection.setOnClickListener(this);
        mTvMyNotice = inflate.findViewById(R.id.tv_my_notice);
        mRedPointNotice = inflate.findViewById(R.id.red_point_notice);

        mTvClickMyFeedback = inflate.findViewById(R.id.tv_click_my_feedback);
        mTvClickMyFeedback.setOnClickListener(this);
        mTvClickSetting = inflate.findViewById(R.id.tv_click_setting);
        mTvClickSetting.setOnClickListener(this);

        inflate.findViewById(R.id.rl_click_my_notice).setOnClickListener(this);
//        inflate.findViewById(R.id.ll_click_praise).setOnClickListener(this);
        inflate.findViewById(R.id.ll_click_focus).setOnClickListener(this);
        inflate.findViewById(R.id.ll_click_fans).setOnClickListener(this);

        praiseCount = inflate.findViewById(R.id.tv_praise_count);
        focusCount = inflate.findViewById(R.id.tv_focus_count);
        fansCount = inflate.findViewById(R.id.tv_fans_count);
        userName = inflate.findViewById(R.id.tv_user_name);
        userNum = inflate.findViewById(R.id.tv_user_number);
        swipeRefreshLayout.setOnRefreshListener(this);

    }

    @Override
    public void fetchData() {
        OkGo.<BaseResponseBean<UserBaseInfoBean>>post(HttpApi.GET_USER_BASE_INFO)
                .upJson("{}")
                .execute(new JsonCallback<BaseResponseBean<UserBaseInfoBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<UserBaseInfoBean>> response) {
                        UserBaseInfoBean data = response.body().getData();
                        bindUserInfo(data);
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<UserBaseInfoBean>> response) {
//                        ToastUtil.showShort("获取基本信息失败！");
                        swipeRefreshLayout.setRefreshing(false);
                        super.onError(response);
                    }
                });

        CommonHttpRequest.getInstance().requestNoticeCount(new JsonCallback<BaseResponseBean<NoticeBean>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<NoticeBean>> response) {
                NoticeBean bean = response.body().getData();
                try {
                    int goodCount = Integer.parseInt(bean.good);
                    int commentCount = Integer.parseInt(bean.comment);
                    int followCount = Integer.parseInt(bean.follow);
                    int noteCount = Integer.parseInt(bean.note);
                    if (goodCount + commentCount + followCount + noteCount > 0) {
                        mRedPointNotice.setVisibility(View.VISIBLE);
                    } else {
                        mRedPointNotice.setVisibility(View.GONE);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private UserBaseInfoBean userBaseInfoBean;

    private void bindUserInfo(UserBaseInfoBean data) {
        userBaseInfoBean = data;
        praiseCount.setText(Int2TextUtils.toText(data.getGoodCount()));
        fansCount.setText(Int2TextUtils.toText(data.getBeFollowCount()));
        focusCount.setText(Int2TextUtils.toText(data.getFollowCount()));
        UserBaseInfoBean.UserInfoBean userInfo = data.getUserInfo();
        //保存用户信息
        userid = userInfo.getUserid();
        MySpUtils.putString(MySpUtils.SP_MY_ID, userid);
        // TODO: 2018/11/17 保存这两个参数是为了发表内容的时候可以从SP里拿到用户信息
        MySpUtils.putString(MySpUtils.SP_MY_AVATAR, userInfo.getUserheadphoto());
        MySpUtils.putString(MySpUtils.SP_MY_NAME, userInfo.getUsername());
        MySpUtils.putString(MySpUtils.SP_MY_NUM, userInfo.getUno());
        GlideUtils.loadImage(userInfo.getUserheadphoto(), mIvTopicImage, true);
        userName.setText(userInfo.getUsername());
        userName.setCompoundDrawablePadding(DevicesUtils.dp2px(10));

        Drawable rightIconSex = DevicesUtils.getDrawable(R.mipmap.my_boy);
        if ("1".equals(userInfo.getUsersex())) {
            rightIconSex = DevicesUtils.getDrawable(R.mipmap.my_girl);
        }
        rightIconSex.setBounds(0, 0, rightIconSex.getMinimumWidth(), rightIconSex.getMinimumHeight());
        userName.setCompoundDrawables(null, null, rightIconSex, null);
        if (!TextUtils.isEmpty(userInfo.getUsersign())) {
            userSign.setText(userInfo.getUsersign());
        }
        if (!TextUtils.isEmpty(userInfo.getUno())) {
            userNum.setText(userInfo.getUno());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.edit_info:
                MyInfoActivity.openMyInfoActivity(userBaseInfoBean.getUserInfo(), new MyInfoActivity.InfoCallBack() {
                    @Override
                    public void callback() {
                        fetchData();
                    }
                });
                break;
//            case R.id.ll_click_praise: 没有点击事件
//                break;
            case R.id.ll_click_focus:
                HelperForStartActivity.openFocus(userid);
                break;
            case R.id.ll_click_fans:
                HelperForStartActivity.openFans(userid);
                break;
            case R.id.tv_click_my_post:
                BaseBigTitleActivity.openBigTitleActivity(BaseBigTitleActivity.POST_TYPE);
                break;
            case R.id.tv_click_my_comment:
                BaseBigTitleActivity.openBigTitleActivity(BaseBigTitleActivity.MY_COMMENTS);
                break;
            case R.id.tv_click_my_collection:
                BaseBigTitleActivity.openBigTitleActivity(BaseBigTitleActivity.COLLECTION_TYPE);
                break;
            case R.id.rl_click_my_notice:
                mRedPointNotice.setVisibility(View.GONE);
                //消除我的tab的小红点
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).clearRed();
                } else {
                    Activity runningActivity = MyApplication.getInstance().getRunningActivity();
                    if (runningActivity instanceof MainActivity) {
                        ((MainActivity) runningActivity).clearRed();
                    }
                }
                HelperForStartActivity.openNotice();
                break;
            case R.id.tv_click_my_feedback:
                HelperForStartActivity.openFeedBack();
                break;
            case R.id.tv_click_setting:
                HelperForStartActivity.openSetting();
                break;
        }
    }

    @Override
    public void onRefresh() {
        fetchData();
    }
}
