package com.caotu.duanzhi.module.notice;

import android.text.TextUtils;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.utils.HelperForStartActivity;

/**
 * 通知头布局的跳转过来页面
 */

public class NoticeHeaderActivity extends BaseActivity {

    @Override
    protected int getLayoutView() {
        return R.layout.activity_notice_header;
    }

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        TextView titleView = findViewById(R.id.tv_other_user_name);
        String extra = getIntent().getStringExtra(HelperForStartActivity.key_other_type);
        String friendId = getIntent().getStringExtra("friendId");
        String titleText;
        switch (extra) {
            case HelperForStartActivity.KEY_NOTICE_AT_AND_COMMENT:
            case HelperForStartActivity.KEY_NOTICE_COMMENT:
                titleText = "@与评论";
                break;
            case HelperForStartActivity.KEY_NOTICE_FOLLOW:
                titleText = "新增关注";
                break;
            case HelperForStartActivity.KEY_NOTICE_LIKE:
                titleText = "新增点赞";
                break;
            default:
                String friendName = getIntent().getStringExtra("friendName");
                if (!TextUtils.isEmpty(friendName)) {
                    titleText = friendName;
                } else {
                    titleText = "消息";
                }
                break;
        }
        titleView.setText(titleText);
        NoticeHeaderFragment fragment = new NoticeHeaderFragment();
        fragment.setDate(extra, friendId);
        turnToFragment(null, fragment, R.id.fl_fragment_content);
    }
}
