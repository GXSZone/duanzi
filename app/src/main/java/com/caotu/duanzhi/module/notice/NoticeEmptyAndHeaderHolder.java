package com.caotu.duanzhi.module.notice;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.Http.bean.NoticeBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.home.ITabRefresh;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DateUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ParserUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.dialog.NoticeReadTipDialog;
import com.caotu.duanzhi.view.widget.MainBottomLayout;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.RImageView;
import com.ruffian.library.widget.RTextView;
import com.sunfusheng.GlideImageView;

import java.util.Date;

/**
 * 处理通知头布局和空布局的view初始化和数据绑定
 */
public class NoticeEmptyAndHeaderHolder implements View.OnClickListener {

    int goodCount, followCount, commentCount, redCount;
    RTextView mRedOne, mRedTwo, mRedThree;
    TextView likeAndCollection, newFocus, atComment;
    RImageView itemUserPhoto;
    GlideImageView itemAuthImage;
    TextView itemNoticeTime, itemUserName, itemNoticeText;
    View itemRedTip;
    ITabRefresh IView;
    MainBottomLayout bottomLayout;

    public NoticeEmptyAndHeaderHolder(ITabRefresh view) {
        IView = view;
    }

    public void initView(View rootView, View emptyView) {
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        if (runningActivity instanceof MainActivity) {
            bottomLayout = ((MainActivity) runningActivity).getBottomLayout();
        }
        //未登录页面
        itemUserPhoto = emptyView.findViewById(R.id.iv_notice_user);
        itemAuthImage = emptyView.findViewById(R.id.iv_user_auth);
        itemNoticeTime = emptyView.findViewById(R.id.notice_time);
        itemUserName = emptyView.findViewById(R.id.tv_item_user);
        itemNoticeText = emptyView.findViewById(R.id.notice_text);
        itemRedTip = emptyView.findViewById(R.id.red_point_tip);
        emptyView.findViewById(R.id.login_bt).setOnClickListener(this);

        likeAndCollection = rootView.findViewById(R.id.tv_like_and_collection);
        newFocus = rootView.findViewById(R.id.tv_new_focus);
        atComment = rootView.findViewById(R.id.tv_at_comment);

        mRedOne = rootView.findViewById(R.id.red_one);
        mRedTwo = rootView.findViewById(R.id.red_two);
        mRedThree = rootView.findViewById(R.id.red_three);

        likeAndCollection.setOnClickListener(this);
        newFocus.setOnClickListener(this);
        atComment.setOnClickListener(this);
        rootView.findViewById(R.id.notice_title).setOnClickListener(this);
    }

    //只绑定一次数据
    boolean hasBindItem = false;

    public void setNoticeItemDate(MessageDataBean.RowsBean item) {
        if (hasBindItem) return;
        GlideUtils.loadImage(item.friendphoto, itemUserPhoto, false);
        itemUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, item.friendid);
            }
        });
        ViewGroup viewGroup = (ViewGroup) itemUserPhoto.getParent();
        viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UmengHelper.event(UmengStatisticsKeyIds.notice_not_login_item);
                HelperForStartActivity.openFromNotice(HelperForStartActivity.KEY_NOTICE_NOT_LOGIN, item.friendid, item.friendname);
            }
        });
        itemAuthImage.load(item.authPic);
        String timeText = "";
        try {
            Date start = DateUtils.getDate(item.createtime, DateUtils.YMDHMS);
            timeText = DateUtils.showTimeText(start);
        } catch (Exception e) {
            e.printStackTrace();
        }

        itemNoticeTime.setText(timeText);
        itemUserName.setText(item.friendname);
        itemNoticeText.setText(ParserUtils.htmlToJustAtText(item.notetext));
        itemRedTip.setVisibility(TextUtils.equals("0", item.readflag) ? View.VISIBLE : View.GONE);
        hasBindItem = true;
    }

    @Override
    public void onClick(View v) {
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        switch (v.getId()) {
            case R.id.tv_like_and_collection:
                if (!LoginHelp.isLogin()) {
                    UmengHelper.event(UmengStatisticsKeyIds.message_praise_login);
                    LoginHelp.goLogin();
                } else {
                    HelperForStartActivity.openFromNotice(HelperForStartActivity.KEY_NOTICE_LIKE);
                    mRedOne.setVisibility(View.INVISIBLE);
                    if (bottomLayout != null) {
                        bottomLayout.showRed(redCount - goodCount);
                    }
                    CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.msg_like);
                    UmengHelper.event(UmengStatisticsKeyIds.notice_like);
                }
                break;
            case R.id.tv_new_focus:
                UmengHelper.event(UmengStatisticsKeyIds.message_concern_login);
                if (!LoginHelp.isLogin()) {
                    LoginHelp.goLogin();
                } else {
                    HelperForStartActivity.openFromNotice(HelperForStartActivity.KEY_NOTICE_FOLLOW);
                    mRedTwo.setVisibility(View.INVISIBLE);
                    if (bottomLayout != null) {
                        bottomLayout.showRed(redCount - followCount);
                    }
                    CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.msg_follow);
                    UmengHelper.event(UmengStatisticsKeyIds.notice_follow);
                }
                break;
            case R.id.tv_at_comment:
                UmengHelper.event(UmengStatisticsKeyIds.message_comments_login);
                if (!LoginHelp.isLogin()) {
                    LoginHelp.goLogin();
                } else {
                    HelperForStartActivity.openFromNotice(HelperForStartActivity.KEY_NOTICE_AT_AND_COMMENT);
                    mRedThree.setVisibility(View.INVISIBLE);
                    if (bottomLayout != null) {
                        bottomLayout.showRed(redCount - commentCount);
                    }
                    CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.msg_comment);
                    UmengHelper.event(UmengStatisticsKeyIds.at_comments);
                }
                break;
            case R.id.notice_title:
                if (!LoginHelp.isLoginAndSkipLogin()) return;
                boolean hasShow = MySpUtils.getBoolean(MySpUtils.SP_READ_DIALOG, false);
                if (!hasShow) {
                    NoticeReadTipDialog dialog = new NoticeReadTipDialog(runningActivity,
                            new NoticeReadTipDialog.ButtomClick() {
                                @Override
                                public void ok() {
                                    setNoticeRead(runningActivity);
                                }
                            });
                    dialog.show();
                    MySpUtils.putBoolean(MySpUtils.SP_READ_DIALOG, true);
                } else {
                    if (redCount > 0) {
                        setNoticeRead(runningActivity);
                    } else {
                        ToastUtil.showShort("暂无新消息通知哦～");
                    }
                }
            case R.id.login_bt:
                UmengHelper.event(UmengStatisticsKeyIds.message_login);
                if (!LoginHelp.isLogin()) {
                    LoginHelp.goLogin();
                }
                break;
            default:
                break;
        }
    }


    public void setNoticeRead(Activity runningActivity) {
        OkGo.<BaseResponseBean<String>>post(HttpApi.MSG_ALL_READ)
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        mRedOne.setVisibility(View.INVISIBLE);
                        mRedTwo.setVisibility(View.INVISIBLE);
                        mRedThree.setVisibility(View.INVISIBLE);
                        if (runningActivity instanceof MainActivity) {
                            //该数字是为了方便,只要能减成负数就行
                            ((MainActivity) runningActivity).changeBottomRed(0);
                        }
                        ToastUtil.showShort("全部设置为已读");
                        IView.refreshDateByTab();
                    }
                });
    }

    public void setNoticeCountBean(NoticeBean bean) {
        try {
            goodCount = Integer.parseInt(bean.good);
            followCount = Integer.parseInt(bean.follow);
            //@ 和评论混在一起了
            commentCount = Integer.parseInt(bean.comment) + Integer.parseInt(bean.call);

            mRedOne.setVisibility(goodCount > 0 ? View.VISIBLE : View.INVISIBLE);
            mRedOne.setText(goodCount > 99 ? "99+" : bean.good);

            mRedTwo.setVisibility(followCount > 0 ? View.VISIBLE : View.INVISIBLE);
            mRedTwo.setText(followCount > 99 ? "99+" : bean.follow);

            mRedThree.setVisibility(commentCount > 0 ? View.VISIBLE : View.INVISIBLE);
            mRedThree.setText(commentCount > 99 ? "99+" : commentCount + "");

            redCount = goodCount + commentCount + followCount + Integer.parseInt(bean.note);
            if (bottomLayout != null) {
                bottomLayout.showRed(redCount);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void clearRedNotice() {
        if (mRedOne == null) return;
        mRedOne.setVisibility(View.INVISIBLE);
        mRedTwo.setVisibility(View.INVISIBLE);
        mRedThree.setVisibility(View.INVISIBLE);
    }
}
