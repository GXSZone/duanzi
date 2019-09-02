package com.caotu.duanzhi.module.setting;

import android.text.TextUtils;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.NoticeSettingBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NotificationUtil;
import com.caotu.duanzhi.view.dialog.NotifyEnableDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.RTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoticeSettingActivity extends BaseActivity implements View.OnClickListener {


    /**
     * 未开启
     */
    private TextView mTvNoticeEnable;
    /**
     * 你可能错过重要消息通知，点击开启
     */
    private RTextView mNoticeAllTip;
    private Switch mContentSwitch;
    private Switch mCommentReplySwitch, mLikeSwitch, mFollowSwitch, mTimeSwitch, mSoundPushSwitch;

    private List<View> switchEnableView = new ArrayList<>();
    private boolean notificationEnable;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_notice_settinng;
    }

    @Override
    protected void initView() {
        UmengHelper.event(UmengStatisticsKeyIds.message_set);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.ll_click_go_notice).setOnClickListener(this);
        mTvNoticeEnable = findViewById(R.id.tv_notice_enable);
        mNoticeAllTip = findViewById(R.id.notice_all_tip);
        mContentSwitch = findViewById(R.id.content_switch);

        mCommentReplySwitch = findViewById(R.id.interactive_comment_reply_switch);
        mLikeSwitch = findViewById(R.id.interactive_like_switch);
        mFollowSwitch = findViewById(R.id.interactive_follow_switch);
        mTimeSwitch = findViewById(R.id.interactive_time_switch);
        mSoundPushSwitch = findViewById(R.id.push_sound_switch);

        View viewById = findViewById(R.id.view_content_switch);
        viewById.setOnClickListener(this);
        View viewById1 = findViewById(R.id.view_interactive_comment_reply_switch);
        viewById1.setOnClickListener(this);
        View viewById2 = findViewById(R.id.view_interactive_like_switch);
        viewById2.setOnClickListener(this);
        View viewById3 = findViewById(R.id.view_interactive_follow_switch);
        viewById3.setOnClickListener(this);
        View viewById4 = findViewById(R.id.view_interactive_time_switch);
        viewById4.setOnClickListener(this);
        View viewById5 = findViewById(R.id.view_push_sound_switch);
        viewById5.setOnClickListener(this);

        switchEnableView.add(viewById);
        switchEnableView.add(viewById1);
        switchEnableView.add(viewById2);
        switchEnableView.add(viewById3);
        switchEnableView.add(viewById4);
        switchEnableView.add(viewById5);


        mContentSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Set<String> tags = new HashSet<>();
            tags.add("content");
            if (isChecked) {
                JPushManager.getInstance().deleteTags(buttonView.getContext(), tags);
            } else {
                JPushManager.getInstance().addTags(buttonView.getContext(), tags);
            }
        });
        boolean notificationEnable = NotificationUtil.notificationEnable(this);
        boolean hasShowed = MySpUtils.getBoolean(MySpUtils.KEY_SETTING_NOTIFY_DIALOG, false);
        if (!notificationEnable && !hasShowed) {
            mTvNoticeEnable.postDelayed(() -> {
                /**
                 * 检查通知的开关是否打开
                 */
                NotifyEnableDialog dialog = new NotifyEnableDialog(NoticeSettingActivity.this);
                dialog.show();
                MySpUtils.putBoolean(MySpUtils.KEY_SETTING_NOTIFY_DIALOG, true);
            }, 200);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        notificationEnable = NotificationUtil.notificationEnable(this);
        bindViewDate();
    }

    private void bindViewDate() {
        if (!notificationEnable) {
            mNoticeAllTip.setVisibility(View.VISIBLE);
            mTvNoticeEnable.setText("未开启");
            mTvNoticeEnable.setTextColor(DevicesUtils.getColor(R.color.color_FF698F));
            mCommentReplySwitch.setChecked(false);
            mLikeSwitch.setChecked(false);
            mFollowSwitch.setChecked(false);
            mTimeSwitch.setChecked(false);

            for (View view : switchEnableView) {
                view.setVisibility(View.VISIBLE);
            }
            mContentSwitch.setChecked(false);

        } else {
            mNoticeAllTip.setVisibility(View.GONE);
            mTvNoticeEnable.setText("已开启");
            mTvNoticeEnable.setTextColor(DevicesUtils.getColor(R.color.color_747E8A));
            getNoticeSetting();

            for (View view : switchEnableView) {
                view.setVisibility(View.GONE);
            }
        }

        boolean pushSoundIsOpen = MySpUtils.getPushSoundIsOpen();
        mSoundPushSwitch.setChecked(pushSoundIsOpen);

    }

    private void getNoticeSetting() {
        OkGo.<BaseResponseBean<NoticeSettingBean>>post(HttpApi.NOTICE_GET_SETTING)
                .execute(new JsonCallback<BaseResponseBean<NoticeSettingBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<NoticeSettingBean>> response) {
                        NoticeSettingBean data = response.body().getData();
                        if (data == null) return;
                        mContentSwitch.setChecked(TextUtils.equals("1", data.contentswitch));
                        mCommentReplySwitch.setChecked(TextUtils.equals("1", data.commentswitch));
                        mLikeSwitch.setChecked(TextUtils.equals("1", data.goodswitch));
                        mFollowSwitch.setChecked(TextUtils.equals("1", data.followswitch));
                        mTimeSwitch.setChecked(TextUtils.equals("1", data.quietswitch));
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.view_content_switch:
            case R.id.view_interactive_comment_reply_switch:
            case R.id.view_interactive_like_switch:
            case R.id.view_interactive_time_switch:
            case R.id.view_interactive_follow_switch:
                NotifyEnableDialog dialog = new NotifyEnableDialog(this);
                dialog.show();
                break;
            case R.id.ll_click_go_notice:
                NotificationUtil.open(this);
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        boolean checked = mSoundPushSwitch.isChecked();
        MySpUtils.setPushSound(checked);
        UmengHelper.event(UmengStatisticsKeyIds.push_sound);
        HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
        if (notificationEnable) {
            params.put("commentswitch", mCommentReplySwitch.isChecked() ? "1" : "0");
            params.put("contentswitch", mContentSwitch.isChecked() ? "1" : "0");
            params.put("followswitch", mFollowSwitch.isChecked() ? "1" : "0");
            params.put("goodswitch", mLikeSwitch.isChecked() ? "1" : "0");
            params.put("quietswitch", mTimeSwitch.isChecked() ? "1" : "0");
        }
        params.put("mainswitch", notificationEnable ? "1" : "0");
        CommonHttpRequest.getInstance().noticeSetting(params);
        super.onDestroy();
    }
}
