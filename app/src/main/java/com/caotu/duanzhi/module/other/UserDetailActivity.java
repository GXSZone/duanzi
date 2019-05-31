package com.caotu.duanzhi.module.other;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.detail.ILoadMore;
import com.caotu.duanzhi.module.detail_scroll.DetailGetLoadMoreDate;
import com.caotu.duanzhi.module.home.fragment.IHomeRefresh;
import com.ruffian.library.widget.RImageView;
import com.ruffian.library.widget.RTextView;
import com.sunfusheng.GlideImageView;
import com.youngfeng.snake.annotations.EnableDragToClose;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * 他人主页和个人主页
 */
@EnableDragToClose()
public class UserDetailActivity extends AppCompatActivity implements DetailGetLoadMoreDate {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user);
        initView();
    }

    static String mUserId;
    private MagicIndicator mMagicIndicator;
    private ViewPager mViewpager;
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
    private ImageView citizen_web;

    public static void start(Context context, String id) {
        if (TextUtils.equals(mUserId, id)) return;
        if (TextUtils.isEmpty(id)) return;
        Intent starter = new Intent(context, UserDetailActivity.class);
        mUserId = id;
        context.startActivity(starter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUserId = null;
    }



    private List<Fragment> fragments = new ArrayList<>();


    protected void initView() {
//        mMagicIndicator = findViewById(R.id.magic_indicator);
        mViewpager = findViewById(R.id.viewpager);
//        IndicatorHelper.initUserDetailIndicator(this, mViewpager, mMagicIndicator);

            fragments.add(new TestRvFragment());
            fragments.add(new TestRvFragment());

        mViewpager.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), fragments,IndicatorHelper.TITLES));
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Map<String, String> map = new HashMap<>();
//        map.put("userid", mUserId);
//        OkGo.<BaseResponseBean<UserBaseInfoBean>>post(HttpApi.GET_USER_BASE_INFO)
//                .tag(this)
//                .upJson(new JSONObject(map))
//                .execute(new JsonCallback<BaseResponseBean<UserBaseInfoBean>>() {
//                    @Override
//                    public void onSuccess(Response<BaseResponseBean<UserBaseInfoBean>> response) {
//                        UserBaseInfoBean data = response.body().getData();
////                        bindUserInfo(data);
//                    }
//                });
//    }

//    private void bindUserInfo(UserBaseInfoBean data) {
//        userBaseInfoBean = data;
//        mTvPraiseCount.setText(Int2TextUtils.toText(data.getGoodCount()));
//        mTvFansCount.setText(Int2TextUtils.toText(data.getBeFollowCount()));
//        mTvFocusCount.setText(Int2TextUtils.toText(data.getFollowCount()));
//        postCount.setText(Int2TextUtils.toText(data.getContentCount()));
//        UserBaseInfoBean.UserInfoBean userInfo = data.getUserInfo();
//        GlideUtils.loadImage(userInfo.getUserheadphoto(), R.mipmap.touxiang_moren, mIvUserAvatar);
//        //头像挂件
//        userGuanjian.load(userInfo.getGuajianurl());
//        if (userInfo.getCardinfo() != null && userInfo.getCardinfo().cardurljson != null) {
//            userBg.load(userInfo.getCardinfo().cardurljson.getBgurl(), R.mipmap.my_bg_moren);
//        } else {
//            userBg.load("", R.mipmap.my_bg_moren);
//        }
//        mTvUserName.setText(userInfo.getUsername());
//        if (getActivity() != null) {
//            ((OtherActivity) getActivity()).setTitleText(userInfo.getUsername());
//        }
//
//        boolean isFollow = "1".equals(userInfo.getIsfollow());
//        mEditInfo.setText(isFollow ? "已关注" : "关注");
//        mEditInfo.setEnabled(!isFollow);
//
//        String beFollowCount = data.getBeFollowCount();
//        try {
//            fanNumber = Integer.parseInt(beFollowCount);
//        } catch (NumberFormatException e) {
//            fanNumber = -1;
//            e.printStackTrace();
//        }
//        //默认是男头像
//        Drawable rightIconSex = DevicesUtils.getDrawable(R.mipmap.my_boy);
//        if ("1".equals(userInfo.getUsersex())) {
//            rightIconSex = DevicesUtils.getDrawable(R.mipmap.my_girl);
//        }
//        rightIconSex.setBounds(0, 0, rightIconSex.getMinimumWidth(), rightIconSex.getMinimumHeight());
//        mTvUserName.setCompoundDrawables(null, null, rightIconSex, null);
//        if (!TextUtils.isEmpty(userInfo.getUsersign())) {
//            mUserSign.setText(userInfo.getUsersign());
//        }
//        if (!TextUtils.isEmpty(userInfo.getUno())) {
//            mUserNum.setVisibility(View.VISIBLE);
//            mUserNum.setText(String.format("段友号:%s", userInfo.getUno()));
//        } else {
//            mUserNum.setVisibility(View.GONE);
//        }
//
//        AuthBean auth = data.getUserInfo().getAuth();
//        if (auth != null && !TextUtils.isEmpty(auth.getAuthid())) {
//            String coverUrl = VideoAndFileUtils.getCover(auth.getAuthpic());
//            userLogos.load(coverUrl);
//            userLogos.setOnClickListener(v -> WebActivity.openWeb("用户勋章", auth.getAuthurl(), true));
//            if (!TextUtils.isEmpty(auth.getAuthword())) {
//                userAuthAName.setVisibility(View.VISIBLE);
//                userAuthAName.setText(auth.getAuthword());
//            }
//        }
//
//        List<UserBaseInfoBean.UserInfoBean.HonorlistBean> honorlist = userInfo.getHonorlist();
//        if (honorlist != null && honorlist.size() > 0) {
//            hasMedal.setVisibility(View.VISIBLE);
//            medalOneImage.load(honorlist.get(0).levelinfo.pic2);
//            if (honorlist.size() >= 2) {
//                medalTwoImage.load(honorlist.get(1).levelinfo.pic2);
//            }
//            medalOneImage.setOnClickListener(v ->
//                    HelperForStartActivity.openUserMedalDetail(honorlist.get(0)));
//
//            medalTwoImage.setOnClickListener(v -> {
//                if (honorlist.size() >= 2) {
//                    HelperForStartActivity.openUserMedalDetail(honorlist.get(1));
//                }
//            });
//        } else {
//            hasMedal.setVisibility(View.GONE);
//        }
//        boolean hasCard = userInfo.getCardinfo() == null || userInfo.getCardinfo().cardurljson == null
//                || TextUtils.isEmpty(userInfo.getCardinfo().cardurljson.getStyleurl());
//        citizen_web.setVisibility(hasCard ? View.GONE : View.VISIBLE);
//    }
//public void initHeaderView(View view) {
//    UmengHelper.event(UmengStatisticsKeyIds.user_detail);
//    mIvUserAvatar = view.findViewById(R.id.iv_user_avatar);
//    mEditInfo = view.findViewById(R.id.edit_info);
//    mEditInfo.setOnClickListener(new FastClickListener() {
//        @Override
//        protected void onSingleClick() {
//            CommonHttpRequest.getInstance().requestFocus(userId, "2", true, new JsonCallback<BaseResponseBean<String>>() {
//                @Override
//                public void onSuccess(Response<BaseResponseBean<String>> response) {
//                    mEditInfo.setText("已关注");
//                    mEditInfo.setEnabled(false);
//                    if (mTvFansCount != null && fanNumber != -1) {
//                        mTvFansCount.setText(String.valueOf(fanNumber + 1));
//                    }
//                    ToastUtil.showShort("关注成功！");
//                }
//            });
//        }
//    });
//    userBg = view.findViewById(R.id.iv_user_bg);
//    mTvUserName = view.findViewById(R.id.tv_user_name);
//    mTvPraiseCount = view.findViewById(R.id.tv_praise_count);
//    mTvFocusCount = view.findViewById(R.id.tv_focus_count);
//    mTvFansCount = view.findViewById(R.id.tv_fans_count);
//    postCount = view.findViewById(R.id.tv_post_count);
//    view.findViewById(R.id.ll_click_focus).setOnClickListener(this);
//    view.findViewById(R.id.ll_click_fans).setOnClickListener(this);
//
//    mUserNum = view.findViewById(R.id.tv_user_number);
//    mUserSign = view.findViewById(R.id.tv_user_sign);
//
//    userLogos = view.findViewById(R.id.ll_user_logos);
//    userAuthAName = view.findViewById(R.id.tv_user_logo_name);
//
//    hasMedal = view.findViewById(R.id.ll_parent_medal);
//    medalOneImage = view.findViewById(R.id.iv_medal_one);
//    medalTwoImage = view.findViewById(R.id.iv_medal_two);
//    userGuanjian = view.findViewById(R.id.iv_user_headgear);
//    citizen_web = view.findViewById(R.id.citizen_web);
//    citizen_web.setOnClickListener(this);
//    view.findViewById(R.id.fl_user_avatar).setOnClickListener(this);
//}
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            default:
//                break;
//            case R.id.fl_user_avatar:
//                if (userBaseInfoBean == null || userBaseInfoBean.getUserInfo() == null) return;
//                HelperForStartActivity.openImageWatcher(userBaseInfoBean.getUserInfo().getUserheadphoto(),
//                        //挂件H5的跳转链接
//                        userBaseInfoBean.getUserInfo().guajianh5url,
//                        //挂件的图片url
//                        userBaseInfoBean.getUserInfo().getGuajianurl());
//                break;
//            case R.id.ll_click_focus:
//                UmengHelper.event(UmengStatisticsKeyIds.my_follow);
//                HelperForStartActivity.openFocus(userId);
//                break;
//            case R.id.ll_click_fans:
//                UmengHelper.event(UmengStatisticsKeyIds.my_fans);
//                HelperForStartActivity.openFans(userId);
//                break;
//            case R.id.citizen_web:
//                if (userBaseInfoBean == null) return;
//                // String styleurl = userBaseInfoBean.getUserInfo().getCardinfo().cardurljson.getStyleurl();
//                String styleurl = userBaseInfoBean.getUserInfo().getCardh5url();
//                if (TextUtils.isEmpty(styleurl)) return;
//                WebActivity.USER_ID = userBaseInfoBean.getUserInfo().getUserid();
//                HelperForStartActivity.checkUrlForSkipWeb("内含公民卡",
//                        styleurl, AndroidInterface.type_other_user);
//                break;
//        }
//    }
    /**
     * 用于加载更多逻辑
     *
     * @param callBack
     */
    @Override
    public void getLoadMoreDate(ILoadMore callBack) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() > 0) {
            if (fragments.get(0) instanceof IHomeRefresh) {
                ((IHomeRefresh) fragments.get(0)).loadMore(callBack);
            }
        }
    }
}
