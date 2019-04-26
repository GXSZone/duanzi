package com.caotu.duanzhi.module.mine;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.AuthBean;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.UmengHelper;
import com.caotu.duanzhi.UmengStatisticsKeyIds;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.LazyLoadFragment;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.other.AndroidInterface;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.widget.HeadZoomScrollView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.sunfusheng.GlideImageView;

import java.util.List;

public class MineFragment extends LazyLoadFragment implements View.OnClickListener {

    private ImageView mIvTopicImage;
    private TextView praiseCount, focusCount, fansCount, userName, userSign, userNum;
    private String userid;
    private TextView userAuthAName, postCount;
    private View redTip;
    private LinearLayout hasMedal;
    private GlideImageView userLogos, medalOneImage, medalTwoImage, userGuanjian;
    private GlideImageView userBg;
    private ImageView citizen_web;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView(View inflate) {
        mIvTopicImage = inflate.findViewById(R.id.iv_user_avatar);
        userGuanjian = inflate.findViewById(R.id.iv_user_headgear);
        userSign = inflate.findViewById(R.id.tv_user_sign);

        inflate.findViewById(R.id.ll_click_focus).setOnClickListener(this);
        inflate.findViewById(R.id.ll_click_fans).setOnClickListener(this);

        inflate.findViewById(R.id.tv_click_my_post).setOnClickListener(this);
        inflate.findViewById(R.id.tv_click_my_comment).setOnClickListener(this);
        inflate.findViewById(R.id.tv_click_my_collection).setOnClickListener(this);
        inflate.findViewById(R.id.tv_click_share_friend).setOnClickListener(this);
        inflate.findViewById(R.id.tv_click_my_feedback).setOnClickListener(this);
        inflate.findViewById(R.id.rl_click_setting).setOnClickListener(this);
        inflate.findViewById(R.id.tv_click_look_history).setOnClickListener(this);

        userLogos = inflate.findViewById(R.id.ll_user_logos);
        userAuthAName = inflate.findViewById(R.id.tv_user_logo_name);
        redTip = inflate.findViewById(R.id.red_point_tip);

        postCount = inflate.findViewById(R.id.tv_post_count);
        praiseCount = inflate.findViewById(R.id.tv_praise_count);
        focusCount = inflate.findViewById(R.id.tv_focus_count);
        fansCount = inflate.findViewById(R.id.tv_fans_count);
        userName = inflate.findViewById(R.id.tv_user_name);
        userNum = inflate.findViewById(R.id.tv_user_number);
        hasMedal = inflate.findViewById(R.id.ll_parent_medal);
        medalOneImage = inflate.findViewById(R.id.iv_medal_one);
        medalTwoImage = inflate.findViewById(R.id.iv_medal_two);
        inflate.findViewById(R.id.tv_click_my_check).setOnClickListener(this);

        citizen_web = inflate.findViewById(R.id.citizen_web);
        citizen_web.setOnClickListener(this);
        View edit = inflate.findViewById(R.id.edit_info);
        edit.setOnClickListener(this);
        View user_header = inflate.findViewById(R.id.fl_user_avatar);

        user_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userBaseInfoBean == null || userBaseInfoBean.getUserInfo() == null) return;
                HelperForStartActivity.openImageWatcher(userBaseInfoBean.getUserInfo().getUserheadphoto(),
                        userBaseInfoBean.getUserInfo().guajianh5url,
                        userBaseInfoBean.getUserInfo().getGuajianurl());
            }
        });


        HeadZoomScrollView scrollView = inflate.findViewById(R.id.header_scrollview);
        userBg = inflate.findViewById(R.id.iv_user_bg);
        View viewBg = inflate.findViewById(R.id.view_user_bg);
        // TODO: 2019/1/4 一行代码给imageview加遮罩
//        userBg.setColorFilter(DevicesUtils.getColor(R.color.image_bg));
        scrollView.setZoomView(userBg);
        scrollView.setZoomView2(viewBg);
        scrollView.setMoveViews(citizen_web, edit, user_header, userName, userLogos);
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
                    }
                });
    }

    private UserBaseInfoBean userBaseInfoBean;

    private void bindUserInfo(UserBaseInfoBean data) {
        if (data == null) return;
        userBaseInfoBean = data;
        praiseCount.setText(Int2TextUtils.toText(data.getGoodCount()));
        fansCount.setText(Int2TextUtils.toText(data.getBeFollowCount()));
        focusCount.setText(Int2TextUtils.toText(data.getFollowCount()));
        postCount.setText(Int2TextUtils.toText(data.getContentCount()));
        UserBaseInfoBean.UserInfoBean userInfo = data.getUserInfo();
        if (userInfo.getCardinfo() != null && userInfo.getCardinfo().cardurljson != null) {
            userBg.load(userInfo.getCardinfo().cardurljson.getBgurl(), R.mipmap.my_bg_moren);
        } else {
            userBg.load("", R.mipmap.my_bg_moren);
        }
        // TODO: 2019/3/14 我的页面请求频繁,只有不相等才去开启服务,因为其他情况在APP启动和登录情况下已经做好处理
        if (!TextUtils.equals(userInfo.getUsername(), MySpUtils.getMyName())
                && getActivity() != null) {
            HelperForStartActivity.startVideoService(true);
        }
        //保存用户信息
        userid = userInfo.getUserid();
        MySpUtils.putString(MySpUtils.SP_MY_ID, userid);
        // TODO: 2018/11/17 保存这两个参数是为了发表内容的时候可以从SP里拿到用户信息
        MySpUtils.putString(MySpUtils.SP_MY_AVATAR, userInfo.getUserheadphoto());
        MySpUtils.putString(MySpUtils.SP_MY_NAME, userInfo.getUsername());
        MySpUtils.putString(MySpUtils.SP_MY_NUM, userInfo.getUno());
        GlideUtils.loadImage(userInfo.getUserheadphoto(), R.mipmap.touxiang_moren,mIvTopicImage);
        userGuanjian.load(userInfo.getGuajianurl());
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
        } else {
            userSign.setText("这是个神秘的段友~");
        }
        if (!TextUtils.isEmpty(userInfo.getUno())) {
            userNum.setVisibility(View.VISIBLE);
            userNum.setText(String.format("段友号:%s", userInfo.getUno()));
        } else {
            userNum.setVisibility(View.GONE);
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
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_click_look_history:
                BaseBigTitleActivity.openBigTitleActivity(BaseBigTitleActivity.HISTORY);
                CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.mine_history);
                UmengHelper.event(UmengStatisticsKeyIds.my_history);
                break;
            case R.id.citizen_web:
                if (userBaseInfoBean == null) return;
                // String styleurl = userBaseInfoBean.getUserInfo().getCardinfo().cardurljson.getStyleurl();
                String styleurl = userBaseInfoBean.getUserInfo().getCardh5url();
                if (TextUtils.isEmpty(styleurl)) return;
                HelperForStartActivity.checkUrlForSkipWeb("内含公民卡",
                        styleurl, AndroidInterface.type_user);
                break;
            case R.id.tv_click_my_check:
                if (userBaseInfoBean == null || userBaseInfoBean.getUserInfo() == null) return;
                String checkurl = userBaseInfoBean.getUserInfo().getCheckurl();
                if (TextUtils.isEmpty(checkurl)) return;
                HelperForStartActivity.checkUrlForSkipWeb("我要审核", checkurl, AndroidInterface.type_user);
                break;
            case R.id.edit_info:
                if (userBaseInfoBean == null || userBaseInfoBean.getUserInfo() == null) return;
                MyInfoActivity.openMyInfoActivity(userBaseInfoBean.getUserInfo(), new MyInfoActivity.InfoCallBack() {
                    @Override
                    public void callback() {
                        fetchData();
                    }
                });
                break;

            case R.id.ll_click_focus:
                if (!TextUtils.isEmpty(userid)) {
                    HelperForStartActivity.openFocus(userid);
                }
                UmengHelper.event(UmengStatisticsKeyIds.my_follow);
                CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.mine_follow);
                break;
            case R.id.ll_click_fans:
                if (!TextUtils.isEmpty(userid)) {
                    HelperForStartActivity.openFans(userid);
                }
                UmengHelper.event(UmengStatisticsKeyIds.my_fans);
                CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.mine_fan);
                break;
            case R.id.tv_click_my_post:
                UmengHelper.event(UmengStatisticsKeyIds.my_production);
                BaseBigTitleActivity.openBigTitleActivity(BaseBigTitleActivity.POST_TYPE);
                CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.mine_content);
                break;
            case R.id.tv_click_my_comment:
                UmengHelper.event(UmengStatisticsKeyIds.my_comments);
                BaseBigTitleActivity.openBigTitleActivity(BaseBigTitleActivity.MY_COMMENTS);
                CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.mine_comment);
                break;
            case R.id.tv_click_my_collection:
                UmengHelper.event(UmengStatisticsKeyIds.my_collection);
                BaseBigTitleActivity.openBigTitleActivity(BaseBigTitleActivity.COLLECTION_TYPE);
                CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.mine_collect);
                break;
            case R.id.tv_click_share_friend:
                // TODO: 2018/12/4 打开推荐好友页面
                HelperForStartActivity.openShareCard();
                UmengHelper.event(UmengStatisticsKeyIds.my_recommend_friends);
                CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.mine_recomment);
                break;
            case R.id.tv_click_my_feedback:
                HelperForStartActivity.openFeedBack();
                UmengHelper.event(UmengStatisticsKeyIds.my_help);
                CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.mine_help);
                break;
            case R.id.rl_click_setting:
                HelperForStartActivity.openSetting();
                UmengHelper.event(UmengStatisticsKeyIds.my_set);
                CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.mine_set);
                break;
        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if (getActivity() != null && getActivity() instanceof MainActivity) {
//            ((MainActivity) getActivity()).clearRed();
//        }
//
//        boolean isShowTip = MySpUtils.getBoolean(MySpUtils.SP_ENTER_SETTING, false);
//        redTip.setVisibility(!isShowTip ? View.VISIBLE : View.GONE);
//    }
}
