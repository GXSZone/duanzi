package com.caotu.duanzhi.module.mine;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.AuthBean;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.DiscoverBannerBean;
import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.module.home.ILoginEvent;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.module.other.BannerHelper;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.other.AndroidInterface;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.sunfusheng.GlideImageView;
import com.zhouwei.mzbanner.MZBannerView;

public class MineFragment extends BaseFragment implements View.OnClickListener, ILoginEvent {

    private ImageView mIvTopicImage, userBg, citizen_web;
    private TextView praiseCount, focusCount, fansCount, userName, userSign, userNum, userAuthAName, postCount;
    private String userid;
    private GlideImageView userLogos, userGuanjian;
    private MZBannerView<DiscoverBannerBean.BannerListBean> bannerView;
    private View loginGroup;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_mine_new;
    }

    @Override
    public boolean isNeedLazyLoadDate() {
        return true;
    }

    @Override
    public void fragmentInViewpagerVisibleToUser() {
        if (!LoginHelp.isLogin()) {
            loginOut();
            return;
        }
        login();
    }

    public void getUserDate() {
        OkGo.<BaseResponseBean<UserBaseInfoBean>>post(HttpApi.GET_USER_BASE_INFO)
                .upJson("{}") //不要怀疑,就是这么优秀
                .execute(new JsonCallback<BaseResponseBean<UserBaseInfoBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<UserBaseInfoBean>> response) {
                        UserBaseInfoBean data = response.body().getData();
                        bindUserInfo(data);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<UserBaseInfoBean>> response) {
                        loginOut();
                        MySpUtils.putBoolean(MySpUtils.SP_ISLOGIN, false);
                        super.onError(response);
                    }
                });
    }

    @Override
    protected void initDate() {
        BannerHelper.getInstance().getBannerDate(bannerView, HttpApi.MINE_BANNER, 1);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (bannerView != null) {
            bannerView.pause();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (bannerView == null) return;
        if (isVisibleToUser) {
            bannerView.start();
        } else {
            bannerView.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bannerView != null) {
            bannerView.start();
        }
    }

    @Override
    protected void initView(View inflate) {
        loginGroup = inflate.findViewById(R.id.login_view_group);
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
        inflate.findViewById(R.id.tv_click_setting).setOnClickListener(this);
        inflate.findViewById(R.id.tv_click_look_history).setOnClickListener(this);
        inflate.findViewById(R.id.tv_click_my_check).setOnClickListener(this);

        userLogos = inflate.findViewById(R.id.ll_user_logos);
        userAuthAName = inflate.findViewById(R.id.tv_user_logo_name);
//        View redTip = inflate.findViewById(R.id.red_point_tip);

        postCount = inflate.findViewById(R.id.tv_post_count);
        praiseCount = inflate.findViewById(R.id.tv_praise_count);
        focusCount = inflate.findViewById(R.id.tv_focus_count);
        fansCount = inflate.findViewById(R.id.tv_fans_count);
        userName = inflate.findViewById(R.id.tv_user_name);
        userName.setOnClickListener(this);
        userNum = inflate.findViewById(R.id.tv_user_number);

        userBg = inflate.findViewById(R.id.iv_user_bg);
        citizen_web = inflate.findViewById(R.id.citizen_web);
        citizen_web.setOnClickListener(this);
        inflate.findViewById(R.id.edit_info).setOnClickListener(this);
        mIvTopicImage.setOnClickListener(this);
        userBg.setOnClickListener(this);
        bannerView = inflate.findViewById(R.id.mine_banner);
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
            GlideUtils.loadImage(userInfo.getCardinfo().cardurljson.getBgurl(), R.mipmap.my_bg_moren, userBg);
        } else {
            GlideUtils.loadImage(R.mipmap.my_bg_moren, userBg);
        }
        // TODO: 2019/3/14 我的页面请求频繁,只有不相等才去开启服务,因为其他情况在APP启动和登录情况下已经做好处理
        if (!TextUtils.equals(userInfo.getUsername(), MySpUtils.getMyName())) {
            HelperForStartActivity.startVideoService(true);
        }
        //保存用户信息
        userid = userInfo.getUserid();
        MySpUtils.putString(MySpUtils.SP_MY_ID, userid);
        // TODO: 2018/11/17 保存这两个参数是为了发表内容的时候可以从SP里拿到用户信息
        MySpUtils.putString(MySpUtils.SP_MY_AVATAR, userInfo.getUserheadphoto());
        MySpUtils.putString(MySpUtils.SP_MY_NAME, userInfo.getUsername());
        MySpUtils.putString(MySpUtils.SP_MY_NUM, userInfo.getUno());
        MySpUtils.putString(MySpUtils.SP_MY_LOCATION, userInfo.location);
        GlideUtils.loadImage(userInfo.getUserheadphoto(), R.mipmap.touxiang_moren, mIvTopicImage);
        userGuanjian.load(userInfo.getGuajianurl());

        userName.setVisibility(TextUtils.isEmpty(userInfo.getUsername()) ? View.INVISIBLE : View.VISIBLE);
        userName.setText(userInfo.getUsername());
//        userName.setCompoundDrawablePadding(DevicesUtils.dp2px(10));
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

        userNum.setVisibility(TextUtils.isEmpty(userInfo.getUno()) ? View.INVISIBLE : View.VISIBLE);
        userNum.setText(String.format("段友号:%s", userInfo.getUno()));

        AuthBean auth = data.getUserInfo().getAuth();
        if (auth != null && !TextUtils.isEmpty(auth.getAuthid())) {
            String coverUrl = VideoAndFileUtils.getCover(auth.getAuthpic());
            userLogos.load(coverUrl);
            userLogos.setOnClickListener(v -> WebActivity.openWeb("用户勋章", auth.getAuthurl(), true));
            if (!TextUtils.isEmpty(auth.getAuthword())) {
                userAuthAName.setVisibility(View.VISIBLE);
                userAuthAName.setText(auth.getAuthword());
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.iv_user_bg:
            case R.id.tv_user_name:
                if (!LoginHelp.isLogin()) {
                    UmengHelper.event(UmengStatisticsKeyIds.mhead_login);
                    LoginHelp.goLogin();
                }
                break;
            case R.id.iv_user_avatar:
                if (!LoginHelp.isLogin()) {
                    UmengHelper.event(UmengStatisticsKeyIds.mhead_login);
                    LoginHelp.goLogin();
                    return;
                }
                if (userBaseInfoBean == null || userBaseInfoBean.getUserInfo() == null) return;
                HelperForStartActivity.openImageWatcher(userBaseInfoBean.getUserInfo().getUserheadphoto(),
                        userBaseInfoBean.getUserInfo().guajianh5url,
                        userBaseInfoBean.getUserInfo().getGuajianurl());
            case R.id.tv_click_look_history:
                if (!LoginHelp.isLogin()) {
                    UmengHelper.event(UmengStatisticsKeyIds.mhistory_login);
                    LoginHelp.goLogin();
                    return;
                }
                BaseBigTitleActivity.openBigTitleActivity(BaseBigTitleActivity.HISTORY);
                CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.mine_history);
                UmengHelper.event(UmengStatisticsKeyIds.my_history);
                break;
            case R.id.citizen_web:
                if (userBaseInfoBean == null) return;
                UmengHelper.event(UmengStatisticsKeyIds.my_card);
                String styleurl = userBaseInfoBean.getUserInfo().getCardh5url();
                if (TextUtils.isEmpty(styleurl)) return;
                HelperForStartActivity.checkUrlForSkipWeb("内含公民卡",
                        styleurl, AndroidInterface.type_user);
                break;
            case R.id.tv_click_my_check:
                if (!LoginHelp.isLogin()) {
                    UmengHelper.event(UmengStatisticsKeyIds.maudit_login);
                    LoginHelp.goLogin();
                    return;
                }
                if (userBaseInfoBean == null || userBaseInfoBean.getUserInfo() == null) return;
                String checkurl = userBaseInfoBean.getUserInfo().getCheckurl();
                if (TextUtils.isEmpty(checkurl)) return;
                HelperForStartActivity.checkUrlForSkipWeb("我要审核", checkurl, AndroidInterface.type_user);
                break;
            case R.id.edit_info:
                UmengHelper.event(UmengStatisticsKeyIds.personal_page);
                HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, MySpUtils.getMyId());
                break;
            case R.id.ll_click_focus:
                if (!LoginHelp.isLogin()) {
                    UmengHelper.event(UmengStatisticsKeyIds.mfollow_login);
                    LoginHelp.goLogin();
                    return;
                }
                if (!TextUtils.isEmpty(userid)) {
                    HelperForStartActivity.openFocus(userid);
                }
                UmengHelper.event(UmengStatisticsKeyIds.my_follow);
                CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.mine_follow);
                break;
            case R.id.ll_click_fans:
                if (!LoginHelp.isLogin()) {
                    UmengHelper.event(UmengStatisticsKeyIds.mfans_login);
                    LoginHelp.goLogin();
                    return;
                }
                if (!TextUtils.isEmpty(userid)) {
                    HelperForStartActivity.openFans(userid);
                }
                UmengHelper.event(UmengStatisticsKeyIds.my_fans);
                CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.mine_fan);
                break;
            case R.id.tv_click_my_post:
                if (!LoginHelp.isLogin()) {
                    UmengHelper.event(UmengStatisticsKeyIds.mworks_login);
                    LoginHelp.goLogin();
                    return;
                }
                UmengHelper.event(UmengStatisticsKeyIds.my_production);
                BaseBigTitleActivity.openBigTitleActivity(BaseBigTitleActivity.POST_TYPE);
                CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.mine_content);
                break;
            case R.id.tv_click_my_comment:
                if (!LoginHelp.isLogin()) {
                    UmengHelper.event(UmengStatisticsKeyIds.mcomments_login);
                    LoginHelp.goLogin();
                    return;
                }
                UmengHelper.event(UmengStatisticsKeyIds.my_comments);
                BaseBigTitleActivity.openBigTitleActivity(BaseBigTitleActivity.MY_COMMENTS);
                CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.mine_comment);
                break;
            case R.id.tv_click_my_collection:
                if (!LoginHelp.isLogin()) {
                    UmengHelper.event(UmengStatisticsKeyIds.mcollection_login);
                    LoginHelp.goLogin();
                    return;
                }
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
            case R.id.tv_click_setting:
                HelperForStartActivity.openSetting();
                UmengHelper.event(UmengStatisticsKeyIds.my_set);
                CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.mine_set);
                break;
        }
    }

    @Override
    public void login() {
        loginGroup.setVisibility(View.VISIBLE);
        getUserDate();
    }

    @Override
    public void loginOut() {
        loginGroup.setVisibility(View.GONE);
        userName.setText("未登录");
        userName.setCompoundDrawables(null, null, null, null);
        GlideUtils.loadImage(R.mipmap.touxiang_moren, mIvTopicImage);
        GlideUtils.loadImage(R.mipmap.my_bg_moren, userBg);
        userSign.setText("花几秒钟登录，做一个有身份的段友");
        praiseCount.setText("0");
        fansCount.setText("0");
        focusCount.setText("0");
        postCount.setText("0");
    }
}
