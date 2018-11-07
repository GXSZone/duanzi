package com.caotu.duanzhi.module.mine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.widget.TextView;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.mine.fragment.FansFragment;
import com.caotu.duanzhi.module.mine.fragment.MyCollectionFragment;
import com.caotu.duanzhi.module.mine.fragment.MyCommentFragment;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;

public class BaseBigTitleActivity extends BaseActivity {
    public static String KEY_TITLE = "title";
    public static final int FANS_TYPE = 200;
    public static final int COLLECTION_TYPE = 201;
    public static final int POST_TYPE = 202;
    public static final int MY_COMMENTS = 203;
    //我的粉丝  我的收藏  我的帖子  我的评论
    private TextView mText;

    @Override
    protected void initView() {
        mText = findViewById(R.id.tv_base_title);

        mText.post(() -> {
            Shader shader_horizontal = new LinearGradient(0, 0,
                    mText.getWidth(), 0,
                    DevicesUtils.getColor(R.color.color_FF8787),
                    DevicesUtils.getColor(R.color.color_FF698F),
                    Shader.TileMode.CLAMP);
            mText.getPaint().setShader(shader_horizontal);
        });
        int intExtra = getIntent().getIntExtra(KEY_TITLE, POST_TYPE);
        switch (intExtra) {
            case FANS_TYPE:
                String userId = getIntent().getStringExtra(HelperForStartActivity.key_user_id);
                boolean isMe = MySpUtils.isMe(userId);
                mText.setText(isMe ? "我的粉丝" : "他的粉丝");
                FansFragment fansFragment = new FansFragment();
                fansFragment.setDate(userId, isMe);
                turnToFragment(null, fansFragment, R.id.fl_fragment_content);
                break;
            case COLLECTION_TYPE:
                mText.setText("我的收藏");
                turnToFragment(null, new MyCollectionFragment(), R.id.fl_fragment_content);
                break;
            case MY_COMMENTS:
                mText.setText("我的评论");
                turnToFragment(null, new MyCommentFragment(), R.id.fl_fragment_content);
                break;
            default:
                mText.setText("我的帖子");
                turnToFragment(null, new MineFragment(), R.id.fl_fragment_content);
                break;
        }
    }

    public static void openBigTitleActivity(int type) {
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        Intent intent = new Intent(runningActivity,
                BaseBigTitleActivity.class);
        intent.putExtra(KEY_TITLE, type);

        runningActivity.startActivity(intent);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_base_big_title;
    }


}
