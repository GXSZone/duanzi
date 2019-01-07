package com.caotu.duanzhi.module.mine;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.AuthBean;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.LazyLoadFragment;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.other.WebActivity;
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
    private TextView userAuthAName;
    private View redTip, historyRedTip;
    private LinearLayout hasMedal;
    private GlideImageView userLogos, medalOneImage, medalTwoImage, userGuanjian;

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
        boolean isShowTip = MySpUtils.getBoolean(MySpUtils.SP_ENTER_SETTING, false);
        redTip.setVisibility(!isShowTip ? View.VISIBLE : View.GONE);

        historyRedTip = inflate.findViewById(R.id.history_red_point_tip);
        boolean isShowHistoryTip = MySpUtils.getBoolean(MySpUtils.SP_ENTER_HISTORY, false);
        historyRedTip.setVisibility(!isShowHistoryTip ? View.VISIBLE : View.GONE);

        praiseCount = inflate.findViewById(R.id.tv_praise_count);
        focusCount = inflate.findViewById(R.id.tv_focus_count);
        fansCount = inflate.findViewById(R.id.tv_fans_count);
        userName = inflate.findViewById(R.id.tv_user_name);
        userNum = inflate.findViewById(R.id.tv_user_number);
        hasMedal = inflate.findViewById(R.id.ll_parent_medal);
        medalOneImage = inflate.findViewById(R.id.iv_medal_one);
        medalTwoImage = inflate.findViewById(R.id.iv_medal_two);
        inflate.findViewById(R.id.tv_click_my_check).setOnClickListener(this);

        View citizen_web = inflate.findViewById(R.id.citizen_web);
        citizen_web.setOnClickListener(this);
        View edit = inflate.findViewById(R.id.edit_info);
        edit.setOnClickListener(this);
        View user_header = inflate.findViewById(R.id.fl_user_avatar);

        HeadZoomScrollView scrollView = inflate.findViewById(R.id.header_scrollview);
        ImageView userBg = inflate.findViewById(R.id.iv_user_bg);
        // TODO: 2019/1/4 一行代码给imageview加遮罩
        userBg.setColorFilter(DevicesUtils.getColor(R.color.image_bg));
        scrollView.setZoomView(userBg);
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
        UserBaseInfoBean.UserInfoBean userInfo = data.getUserInfo();
        //保存用户信息
        userid = userInfo.getUserid();
        MySpUtils.putString(MySpUtils.SP_MY_ID, userid);
        // TODO: 2018/11/17 保存这两个参数是为了发表内容的时候可以从SP里拿到用户信息
        MySpUtils.putString(MySpUtils.SP_MY_AVATAR, userInfo.getUserheadphoto());
        MySpUtils.putString(MySpUtils.SP_MY_NAME, userInfo.getUsername());
        MySpUtils.putString(MySpUtils.SP_MY_NUM, userInfo.getUno());
        GlideUtils.loadImage(userInfo.getUserheadphoto(), mIvTopicImage, true);
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
            userNum.setText("段友号:" + userInfo.getUno());
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
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_click_look_history:
                BaseBigTitleActivity.openBigTitleActivity(BaseBigTitleActivity.HISTORY);
                MySpUtils.putBoolean(MySpUtils.SP_ENTER_HISTORY, true);
                historyRedTip.setVisibility(View.GONE);
                if (getActivity() != null && getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).clearRed();
                }
                break;
            case R.id.citizen_web:
                HelperForStartActivity.checkUrlForSkipWeb("公民卡", "url");
                break;
            case R.id.tv_click_my_check:
                if (userBaseInfoBean == null || userBaseInfoBean.getUserInfo() == null) return;
                String checkurl = userBaseInfoBean.getUserInfo().getCheckurl();
                if (TextUtils.isEmpty(checkurl)) return;
                HelperForStartActivity.checkUrlForSkipWeb("我要审核", checkurl);
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
                break;
            case R.id.ll_click_fans:
                if (!TextUtils.isEmpty(userid)) {
                    HelperForStartActivity.openFans(userid);
                }
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
            case R.id.tv_click_share_friend:
                // TODO: 2018/12/4 打开推荐好友页面
                HelperForStartActivity.openShareCard();
                break;
            case R.id.tv_click_my_feedback:
                HelperForStartActivity.openFeedBack();
                break;
            case R.id.rl_click_setting:
                HelperForStartActivity.openSetting();
                redTip.setVisibility(View.GONE);
                if (getActivity() != null && getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).clearRed();
                }
                break;
        }
    }
}
