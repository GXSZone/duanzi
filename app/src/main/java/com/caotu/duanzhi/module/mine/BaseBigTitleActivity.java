package com.caotu.duanzhi.module.mine;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
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
import com.caotu.duanzhi.module.mine.fragment.MyLikeFragment;
import com.caotu.duanzhi.module.mine.fragment.MyPostFragment;
import com.caotu.duanzhi.module.other.UserCommentFragment;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.List;

public class BaseBigTitleActivity extends BaseActivity implements DetailGetLoadMoreDate {
    public static final int FANS_TYPE = 200;
    public static final int COLLECTION_TYPE = 201;
    public static final int POST_TYPE = 202;
    public static final int MY_COMMENTS = 203;
    public static final int HISTORY = 204;
    public static final int LIKE = 205;
    //我的粉丝  我的收藏  我的帖子  我的评论
//    private TextView mText;
    private ImageView historyDelete;

    public ImageView getHistoryDelete() {
        return historyDelete;
    }


    @Override
    protected void initView() {
        CollapsingToolbarLayout mText = findViewById(R.id.titleId);
        Toolbar toolbar = findViewById(R.id.toolbar_back);
        toolbar.setNavigationOnClickListener(v -> finish());
        int intExtra = getIntent().getIntExtra("title", POST_TYPE);
        switch (intExtra) {
            case FANS_TYPE:
                String userId = getIntent().getStringExtra(HelperForStartActivity.key_user_id);
                boolean isMe = MySpUtils.isMe(userId);
                mText.setTitle(isMe ? "我的粉丝" : "他的粉丝");
                FansFragment fansFragment = new FansFragment();
                fansFragment.setDate(userId, isMe);
                turnToFragment(null, fansFragment, R.id.fl_fragment_content);
                break;
            case COLLECTION_TYPE:
                mText.setTitle("我的收藏");
                turnToFragment(null, new MyCollectionFragment(), R.id.fl_fragment_content);
                break;
            case MY_COMMENTS:
                mText.setTitle("我的评论");
                UserCommentFragment fragment = new UserCommentFragment();
                fragment.setDate(MySpUtils.getMyId());
                turnToFragment(null, fragment, R.id.fl_fragment_content);
                break;
            case HISTORY:
                mText.setTitle("浏览历史");
                historyDelete = findViewById(R.id.iv_history_delete);
                historyDelete.setVisibility(View.VISIBLE);
                turnToFragment(null, new HistoryFragment(), R.id.fl_fragment_content);
                break;
            case LIKE:
                turnToFragment(null, new MyLikeFragment(), R.id.fl_fragment_content);
                break;
            default:
                mText.setTitle("我的帖子");
                turnToFragment(null, new MyPostFragment(), R.id.fl_fragment_content);
                break;
        }
    }

    public static void openBigTitleActivity(int type) {
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        Intent intent = new Intent(runningActivity, BaseBigTitleActivity.class);
        intent.putExtra("title", type);
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
        if (!AppUtil.listHasDate(fragments)) return;
        if (fragments.get(0) instanceof IHomeRefresh) {
            ((IHomeRefresh) fragments.get(0)).loadMore(callBack);
        }
    }
}
