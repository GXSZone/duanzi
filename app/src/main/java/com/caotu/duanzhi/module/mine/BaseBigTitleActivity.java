package com.caotu.duanzhi.module.mine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.widget.TextView;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.utils.DevicesUtils;

public class BaseBigTitleActivity extends BaseActivity {
    public static String KEY_TITLE = "title";
    public static final int FANS_TYPE = 200;
    public static final int COLLECTION_TYPE = 201;
    public static final int POST_TYPE = 202;
    //我的粉丝  我的收藏  我的帖子
    private TextView mText;

    @Override
    protected void initView() {
        mText = (TextView) findViewById(R.id.tv_base_title);

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
                mText.setText("我的粉丝");
                turnToFragment(null, new MineFragment(), R.id.fl_fragment_content);
                break;
            case COLLECTION_TYPE:
                mText.setText("我的收藏");
                turnToFragment(null, new MineFragment(), R.id.fl_fragment_content);
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
                WebActivity.class);
        intent.putExtra(KEY_TITLE, type);
        runningActivity.startActivity(intent);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_base_big_title;
    }


}
