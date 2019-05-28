package com.caotu.duanzhi.module.mine;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.detail.ILoadMore;
import com.caotu.duanzhi.module.detail_scroll.DetailGetLoadMoreDate;
import com.caotu.duanzhi.module.home.fragment.IHomeRefresh;
import com.caotu.duanzhi.module.mine.fragment.FansFragment;
import com.caotu.duanzhi.module.mine.fragment.HistoryFragment;
import com.caotu.duanzhi.module.mine.fragment.MyCollectionFragment;
import com.caotu.duanzhi.module.mine.fragment.MyCommentFragment;
import com.caotu.duanzhi.module.mine.fragment.MyPostFragment;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.youngfeng.snake.annotations.EnableDragToClose;

import java.util.List;
@EnableDragToClose
public class BaseBigTitleActivity extends BaseActivity implements DetailGetLoadMoreDate {
    public static String KEY_TITLE = "title";
    public static final int FANS_TYPE = 200;
    public static final int COLLECTION_TYPE = 201;
    public static final int POST_TYPE = 202;
    public static final int MY_COMMENTS = 203;
    public static final int HISTORY = 204;
    //我的粉丝  我的收藏  我的帖子  我的评论
    private TextView mText;
    private ImageView historyDelete;

    public ImageView getHistoryDelete() {
        return historyDelete;
    }

    public TextView getmText() {
        return mText;
    }

    @Override
    protected void initView() {
        mText = findViewById(R.id.tv_title_big);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

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
            case HISTORY:
                mText.setText("浏览历史");
                historyDelete = findViewById(R.id.iv_history_delete);
                historyDelete.setVisibility(View.VISIBLE);
                turnToFragment(null, new HistoryFragment(), R.id.fl_fragment_content);
                break;
            default:
                mText.setText("我的帖子");
                turnToFragment(null, new MyPostFragment(), R.id.fl_fragment_content);
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
