package com.caotu.duanzhi.module.mine;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NotificationUtil;
import com.ruffian.library.widget.RTextView;

import java.util.ArrayList;
import java.util.List;

public class NoticeSettingActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    /**
     * 未开启
     */
    private TextView mTvNoticeEnable;
    /**
     * 你可能错过重要消息通知，点击开启
     */
    private RTextView mNoticeAllTip;
    private Switch mContentSwitch;
    /**
     * 滴 滴滴！接收每日精彩内容推送
     */
    private RTextView mCommentNoticeTip;
    private Switch mInteractiveSwitch;
    /**
     * 及时接收其他段友的互动消息
     */
    private RTextView mInteractiveTip;
    private Switch mInteractiveCommentReplySwitch, mInteractiveLikeSwitch,
            mInteractiveFollowSwitch, mInteractiveTimeSwitch;

    private List<Switch> switches = new ArrayList<>();
    private boolean notificationEnable;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_notice_settinng;
    }

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.ll_click_go_notice).setOnClickListener(this);
        mTvNoticeEnable = (TextView) findViewById(R.id.tv_notice_enable);
        mNoticeAllTip = (RTextView) findViewById(R.id.notice_all_tip);
        mContentSwitch = (Switch) findViewById(R.id.content_switch);
        mCommentNoticeTip = (RTextView) findViewById(R.id.comment_notice_tip);
        mInteractiveSwitch = (Switch) findViewById(R.id.interactive_switch);
        mInteractiveTip = (RTextView) findViewById(R.id.interactive_tip);
        mInteractiveCommentReplySwitch = (Switch) findViewById(R.id.interactive_comment_reply_switch);
        mInteractiveLikeSwitch = (Switch) findViewById(R.id.interactive_like_switch);
        mInteractiveFollowSwitch = (Switch) findViewById(R.id.interactive_follow_switch);
        mInteractiveTimeSwitch = (Switch) findViewById(R.id.interactive_time_switch);

        switches.add(mInteractiveCommentReplySwitch);
        switches.add(mInteractiveLikeSwitch);
        switches.add(mInteractiveFollowSwitch);
        switches.add(mInteractiveTimeSwitch);

        mContentSwitch.setOnCheckedChangeListener(this);
        mInteractiveSwitch.setOnCheckedChangeListener(this);

        mInteractiveCommentReplySwitch.setOnCheckedChangeListener(this);
        mInteractiveLikeSwitch.setOnCheckedChangeListener(this);
        mInteractiveFollowSwitch.setOnCheckedChangeListener(this);
        mInteractiveTimeSwitch.setOnCheckedChangeListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        notificationEnable = NotificationUtil.notificationEnable(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String key;
        switch (buttonView.getId()) {
            case R.id.content_switch:
                key = MySpUtils.SP_SWITCH_COMMENT;
                break;
            case R.id.interactive_switch:
                key = MySpUtils.SP_SWITCH_INTERACTIVE;
                break;
            case R.id.interactive_comment_reply_switch:
                key = MySpUtils.SP_SWITCH_REPLY;
                break;
            case R.id.interactive_like_switch:
                key = MySpUtils.SP_SWITCH_LIKE;
                break;
            case R.id.interactive_follow_switch:
                key = MySpUtils.SP_SWITCH_FOLLOW;
                break;
            case R.id.interactive_time_switch:
                key = MySpUtils.SP_SWITCH_TIME;
                break;
            default:
                key = MySpUtils.SP_SWITCH_COMMENT;
                break;
        }
        MySpUtils.putBoolean(key, isChecked);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
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
    protected void onPause() {
        MySpUtils.putBoolean(MySpUtils.SP_ENTER_SETTING, true);
        super.onPause();
    }
}
