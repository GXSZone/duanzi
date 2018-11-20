package com.caotu.duanzhi.module.mine;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.utils.GlideUtils;
import com.ruffian.library.widget.RImageView;

public class NoticeDetailActivity extends BaseActivity {

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        String title = getIntent().getStringExtra("title");
        String userPh = getIntent().getStringExtra("userPh");
        String content = getIntent().getStringExtra("content");
        String time = getIntent().getStringExtra("time");

        TextView mTitle = findViewById(R.id.title);
        TextView name = findViewById(R.id.tv_user_name);
        mTitle.setText(title);
        name.setText(title);

        RImageView mIvNoticeImage = findViewById(R.id.iv_notice_image);
        GlideUtils.loadImage(userPh, mIvNoticeImage, true);

        TextView mTvNoticeText = findViewById(R.id.tv_notice_text);

        mTvNoticeText.setText(content);
        String nowTime = time.substring(0, 4) + "-" + time.substring(4, 6) + "-" + time.substring(6, 8);
        TextView mTvTime = findViewById(R.id.tv_time);
        mTvTime.setText(nowTime);

    }


    public static void openNoticeDetail(String title, String userPh, String content, String time) {
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        Intent intent = new Intent(runningActivity, NoticeDetailActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("userPh", userPh);
        intent.putExtra("content", content);
        intent.putExtra("time", time);
        runningActivity.startActivity(intent);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_notice_detail;
    }
}