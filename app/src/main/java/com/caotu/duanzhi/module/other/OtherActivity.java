package com.caotu.duanzhi.module.other;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.TopicInfoBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.home.ILoadMore;
import com.caotu.duanzhi.module.home.fragment.IHomeRefresh;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.FastClickListener;
import com.lzy.okgo.model.Response;
import com.sunfusheng.GlideImageView;

import java.util.List;

/**
 * 他人主页和话题详情和通知里多人点赞列表共用
 * 针对有列表有头布局的封装,只需要更换adapter就可以了
 */
public class OtherActivity extends BaseActivity {

    public TextView mTvOtherUserName;
    public GlideImageView topicImage;
    public TextView topicName;
    public ImageView isFollow;
    private LinearLayout layout;
    private TopicDetailFragment fragment;

    public LinearLayout getLayout() {
        return layout;
    }

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        ViewStub viewStub = findViewById(R.id.view_stub_is_topic_detail);
        mTvOtherUserName = findViewById(R.id.tv_other_user_name);
        View line = findViewById(R.id.view_line);
        String extra = getIntent().getStringExtra(HelperForStartActivity.key_other_type);
        String id = getIntent().getStringExtra(HelperForStartActivity.key_user_id);

        if (HelperForStartActivity.type_other_user.equals(extra)) {
            line.setVisibility(View.GONE);
            mTvOtherUserName.setVisibility(View.VISIBLE);
            OtherUserFragment fragment = new OtherUserFragment();
            fragment.setDate(id);
            turnToFragment(null, fragment, R.id.fl_fragment_content);

        } else if (HelperForStartActivity.type_other_topic.equals(extra)) {
            line.setVisibility(View.VISIBLE);
            mTvOtherUserName.setVisibility(View.GONE);
            try {
                //如果没有被inflate过，使用inflate膨胀
                layout = (LinearLayout) viewStub.inflate();
                topicImage = layout.findViewById(R.id.iv_topic_image);
                topicName = layout.findViewById(R.id.tv_topic_name);
                isFollow = layout.findViewById(R.id.iv_topic_follow);
            } catch (Exception e) {
                //如果使用inflate膨胀报错，就说明已经被膨胀过了，使用setVisibility方法显示
                viewStub.setVisibility(View.VISIBLE);
            }
            //初始值不可见
            layout.setAlpha(0f);
            ImageView view = findViewById(R.id.iv_go_publish);
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new FastClickListener() {
                @Override
                protected void onSingleClick() {
                    HelperForStartActivity.openPublish();
                }
            });
            fragment = new TopicDetailFragment();
            fragment.setDate(id);
            turnToFragment(null, fragment, R.id.fl_fragment_content);

        } else if (HelperForStartActivity.type_other_praise.equals(extra)) {
            line.setVisibility(View.VISIBLE);
            mTvOtherUserName.setVisibility(View.VISIBLE);
            mTvOtherUserName.setText("点赞的人");
            OtherParaiseUserFragment fragment = new OtherParaiseUserFragment();
            //这个相当于在他人页面的用户列表,只有已关注和未关注两个状态
            fragment.setDate(id, false);
            turnToFragment(null, fragment, R.id.fl_fragment_content);
        }

    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_other;
    }

    /**
     * 给fragment设置标题用
     *
     * @param titleText
     */
    public void setTitleText(String titleText) {
        mTvOtherUserName.setText(titleText);
    }

    public void bindTopic(TopicInfoBean data) {
        topicImage.load(data.getTagimg(), R.mipmap.image_default, 3);
        topicName.setText(data.getTagname());
        //1关注 0未关注
        if (LikeAndUnlikeUtil.isLiked(data.getIsfollow())) {
            isFollow.setEnabled(false);
        }
        isFollow.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                //虽然不可见但是可以点击,判断透明度更靠谱
                if (layout.getAlpha() < 0.8) return;
                CommonHttpRequest.getInstance().<String>requestFocus(data.getTagid(), "1", true, new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        isFollow.setEnabled(false);
                        if (fragment != null) {
                            fragment.changeFollow();
                        }
                        ToastUtil.showShort("关注成功");
                    }
                });
            }
        });
    }

    public void changeFollowState() {
        if (isFollow != null) {
            isFollow.setEnabled(false);
        }
    }

    /**
     * 用于加载更多逻辑
     * @param callBack
     */
    public void getLoadMoreDate(ILoadMore callBack) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null && fragments.size() > 0) {
            if (fragments.get(0) instanceof IHomeRefresh) {
                ((IHomeRefresh) fragments.get(0)).loadMore(callBack);
            }
        }
    }
}
