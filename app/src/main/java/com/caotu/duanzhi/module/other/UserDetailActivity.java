package com.caotu.duanzhi.module.other;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.AuthBean;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.base.MyFragmentAdapter;
import com.caotu.duanzhi.module.detail.ILoadMore;
import com.caotu.duanzhi.module.detail_scroll.DetailGetLoadMoreDate;
import com.caotu.duanzhi.module.home.fragment.IHomeRefresh;
import com.caotu.duanzhi.module.mine.MyInfoActivity;
import com.caotu.duanzhi.module.mine.fragment.UserCollectionFragment;
import com.caotu.duanzhi.other.FastClickListener;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.ReportDialog;
import com.caotu.duanzhi.view.widget.TitleView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.RTextView;
import com.sunfusheng.GlideImageView;

import net.lucode.hackware.magicindicator.MagicIndicator;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 他人主页和个人主页
 */

public class UserDetailActivity extends BaseActivity implements DetailGetLoadMoreDate, View.OnClickListener {

    private TitleView titleView;
    public String mUserId;
    private ImageView mIvUserAvatar, userBg;
    private RTextView tvFollow;
    private TextView mTvPraiseCount, mTvFocusCount, mTvFansCount, mTvLocation, mTvHotCount,
            mUserNum, mUserSign, userAuthAName;
    private int fanNumber;
    private LinearLayout hasMedal;
    private GlideImageView medalOneImage, medalTwoImage, userGuanjian, userLogos;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_other_user;
    }

    public static void start(Context context, String id) {
        if (TextUtils.isEmpty(id)) return;
        if (context instanceof UserDetailActivity) {
            String getmUserId = ((UserDetailActivity) context).getmUserId();
            if (TextUtils.equals(getmUserId, id)) return;
        }
        Intent starter = new Intent(context, UserDetailActivity.class);
        starter.putExtra("userId", id);
        context.startActivity(starter);
    }

    public String getmUserId() {
        return mUserId;
    }

    private List<Fragment> fragments = new ArrayList<>();

    protected void initView() {
        titleView = findViewById(R.id.title_view);

        mUserId = getIntent().getStringExtra("userId");
        initHeaderView();

        TextView userType = findViewById(R.id.user_type);
        if (MySpUtils.isMe(mUserId)) {
            UmengHelper.event(UmengStatisticsKeyIds.my_production);
            userType.setText("我的\n勋章");
        } else {
            UmengHelper.event(UmengStatisticsKeyIds.others_production);
            UmengHelper.event(UmengStatisticsKeyIds.user_detail);
            userType.setText("他的\n勋章");
        }
    }

    boolean isInitViewpager;

    private void initViewPager(boolean collection) {
        if (isInitViewpager) return;
        MagicIndicator mMagicIndicator = findViewById(R.id.magic_indicator);
        ViewPager mViewpager = findViewById(R.id.viewpager);

        OtherUserFragment otherUserFragment = new OtherUserFragment();
        otherUserFragment.setDate(mUserId);
        fragments.add(otherUserFragment);
        UserCommentFragment commentFragment = new UserCommentFragment();
        commentFragment.setDate(mUserId);
        fragments.add(commentFragment);

        if (collection) {
            UserCollectionFragment collectionFragment = new UserCollectionFragment();
            collectionFragment.setDate(mUserId);
            fragments.add(collectionFragment);
        }
        mViewpager.setOffscreenPageLimit(2);
        mViewpager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), fragments));
        IndicatorHelper.initIndicator(this, mViewpager, mMagicIndicator,
                collection ? IndicatorHelper.TITLES2 : IndicatorHelper.TITLES);

        mViewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    if (MySpUtils.isMe(mUserId)) {
                        UmengHelper.event(UmengStatisticsKeyIds.my_production);
                    } else {
                        UmengHelper.event(UmengStatisticsKeyIds.others_production);
                    }
                } else if (position == 2) {
                    UmengHelper.event(UmengStatisticsKeyIds.user_collection);
                } else {
                    if (MySpUtils.isMe(mUserId)) {
                        UmengHelper.event(UmengStatisticsKeyIds.my_comments);
                    } else {
                        UmengHelper.event(UmengStatisticsKeyIds.others_comments);
                    }
                }
                releaseAllVideo();
            }
        });
        isInitViewpager = true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        Map<String, String> map = new HashMap<>();
        map.put("userid", mUserId);
        OkGo.<BaseResponseBean<UserBaseInfoBean>>post(HttpApi.GET_USER_BASE_INFO)
                .tag(this)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<UserBaseInfoBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<UserBaseInfoBean>> response) {
                        UserBaseInfoBean data = response.body().getData();
                        bindUserInfo(data);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<UserBaseInfoBean>> response) {
                        ToastUtil.showShort("用户不存在");
                        super.onError(response);
                    }
                });
    }

    UserBaseInfoBean userBaseInfoBean;

    private void bindUserInfo(UserBaseInfoBean data) {
        userBaseInfoBean = data;
        mTvPraiseCount.setText(Int2TextUtils.toText(data.getGoodCount()));
        mTvFansCount.setText(Int2TextUtils.toText(data.getBeFollowCount()));
        mTvFocusCount.setText(Int2TextUtils.toText(data.getFollowCount()));

        UserBaseInfoBean.UserInfoBean userInfo = data.getUserInfo();
        initViewPager(LikeAndUnlikeUtil.isLiked(userInfo.collectionswitch));
        GlideUtils.loadImage(userInfo.getUserheadphoto(), R.mipmap.touxiang_moren, mIvUserAvatar);
        //头像挂件
        userGuanjian.load(userInfo.getGuajianurl());
        if (userInfo.getCardinfo() != null && userInfo.getCardinfo().cardurljson != null) {
            GlideUtils.loadImage(userInfo.getCardinfo().cardurljson.getBgurl(), R.mipmap.my_bg_moren, userBg);
        } else {
            GlideUtils.loadImage(R.mipmap.my_bg_moren, userBg);
        }

        titleView.setTitleText(userInfo.getUsername());
        if (MySpUtils.isMe(mUserId)) {
            tvFollow.setText("编辑");
        } else {
            boolean isFollow = "1".equals(userInfo.getIsfollow());
            tvFollow.setText(isFollow ? "已关注" : "关注");
            tvFollow.setEnabled(!isFollow);
        }
        String gohottimes = userInfo.gohottimes;
        if (TextUtils.isEmpty(gohottimes) || TextUtils.equals("0", gohottimes)) {
            mTvHotCount.setVisibility(View.GONE);
        } else {
            mTvHotCount.setVisibility(View.VISIBLE);
            mTvHotCount.setText(Int2TextUtils.toText(gohottimes).concat("次上热门"));
        }

        String beFollowCount = data.getBeFollowCount();
        try {
            fanNumber = Integer.parseInt(beFollowCount);
        } catch (NumberFormatException e) {
            fanNumber = -1;
            e.printStackTrace();
        }
        //默认是男头像
        Drawable rightIconSex = null;
        if ("1".equals(userInfo.getUsersex())) {
            rightIconSex = DevicesUtils.getDrawable(R.mipmap.my_girl);
        } else if (TextUtils.equals("0", userInfo.getUsersex())) {
            rightIconSex = DevicesUtils.getDrawable(R.mipmap.my_boy);
        }
        if (rightIconSex != null) {
            rightIconSex.setBounds(0, 0, rightIconSex.getMinimumWidth(), rightIconSex.getMinimumHeight());
            mUserNum.setCompoundDrawables(null, null, rightIconSex, null);
        }
        if (!TextUtils.isEmpty(userInfo.getUsersign())) {
            mUserSign.setText(userInfo.getUsersign());
        }
        if (!TextUtils.isEmpty(userInfo.getUno())) {
            mUserNum.setVisibility(View.VISIBLE);
            mUserNum.setText(String.format("段友号:%s", userInfo.getUno()));
        } else {
            mUserNum.setVisibility(View.GONE);
        }
        //地理位置
        String location = data.getUserInfo().location;
        if (!TextUtils.isEmpty(data.getUserInfo().location) && data.getUserInfo().location.contains(",")) {
            String[] split = data.getUserInfo().location.split(",");
            if (split.length > 1) {
                location = split[1];
            }
        }
        mTvLocation.setText(location);
        mTvLocation.setVisibility(TextUtils.isEmpty(location) ? View.GONE : View.VISIBLE);

        if (!TextUtils.isEmpty(userInfo.authname)) {
            userAuthAName.setVisibility(View.VISIBLE);
            userAuthAName.setText(userInfo.authname);
        } else {
            userAuthAName.setVisibility(View.GONE);
        }
        AuthBean auth = data.getUserInfo().getAuth();
        if (auth != null && !TextUtils.isEmpty(auth.getAuthid())) {
            String coverUrl = VideoAndFileUtils.getCover(auth.getAuthpic());
            userLogos.setVisibility(View.VISIBLE);
            userLogos.load(coverUrl);
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

    public void initHeaderView() {
        mIvUserAvatar = findViewById(R.id.iv_user_avatar);
        mIvUserAvatar.setOnClickListener(this);
        tvFollow = findViewById(R.id.iv_topic_follow);
        tvFollow.setTag(UmengStatisticsKeyIds.follow_user);
        tvFollow.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                if (MySpUtils.isMe(mUserId)) {
                    // TODO: 2019-06-11 修改信息后的同步问题
                    if (userBaseInfoBean == null || userBaseInfoBean.getUserInfo() == null) return;
                    MyInfoActivity.openMyInfoActivity(userBaseInfoBean.getUserInfo());
                    return;
                }
                CommonHttpRequest.getInstance().requestFocus(mUserId, "2", true, new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        tvFollow.setText("已关注");
                        tvFollow.setEnabled(false);
                        if (mTvFansCount != null && fanNumber != -1) {
                            mTvFansCount.setText(String.valueOf(fanNumber + 1));
                        }
                        ToastUtil.showShort("关注成功！");
                    }
                });
            }
        });
        userBg = findViewById(R.id.iv_user_bg);

        mTvPraiseCount = findViewById(R.id.tv_praise_count);
        mTvFocusCount = findViewById(R.id.tv_focus_count);
        mTvFansCount = findViewById(R.id.tv_fans_count);

        findViewById(R.id.ll_click_focus).setOnClickListener(this);
        findViewById(R.id.ll_click_fans).setOnClickListener(this);

        mUserNum = findViewById(R.id.tv_user_number);
        mUserSign = findViewById(R.id.tv_user_sign);

        userLogos = findViewById(R.id.iv_user_logos);
        userAuthAName = findViewById(R.id.tv_logo_and_location);

        hasMedal = findViewById(R.id.ll_parent_medal);
        medalOneImage = findViewById(R.id.iv_medal_one);
        medalTwoImage = findViewById(R.id.iv_medal_two);
        userGuanjian = findViewById(R.id.iv_user_headgear);
        mTvLocation = findViewById(R.id.tv_user_location);
        mTvHotCount = findViewById(R.id.tv_hot_count);

        titleView.setClickListener(() -> {
            ReportDialog dialog = new ReportDialog(UserDetailActivity.this);
            dialog.setIdAndType(mUserId, 2);
            dialog.show();
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.iv_user_avatar:
                if (userBaseInfoBean == null || userBaseInfoBean.getUserInfo() == null) return;
                HelperForStartActivity.openImageWatcher(userBaseInfoBean.getUserInfo().getUserheadphoto(),
                        //挂件H5的跳转链接
                        userBaseInfoBean.getUserInfo().guajianh5url,
                        //挂件的图片url
                        userBaseInfoBean.getUserInfo().getGuajianurl());
                break;
            case R.id.ll_click_focus:
                UmengHelper.event(UmengStatisticsKeyIds.my_follow);
                HelperForStartActivity.openFocus(mUserId);
                break;
            case R.id.ll_click_fans:
                UmengHelper.event(UmengStatisticsKeyIds.my_fans);
                HelperForStartActivity.openFans(mUserId);
                break;
        }
    }

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
