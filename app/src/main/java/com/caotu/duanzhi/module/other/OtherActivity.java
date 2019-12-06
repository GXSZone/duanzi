package com.caotu.duanzhi.module.other;

import androidx.fragment.app.Fragment;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.detail.ILoadMore;
import com.caotu.duanzhi.module.detail_scroll.DetailGetLoadMoreDate;
import com.caotu.duanzhi.module.home.fragment.IHomeRefresh;
import com.caotu.duanzhi.utils.HelperForStartActivity;

import java.util.List;

/**
 * 他人主页和话题详情和通知里多人点赞列表共用
 * 针对有列表有头布局的封装,只需要更换adapter就可以了
 * activity 相当于是一个空格子
 */

public class OtherActivity extends BaseActivity implements DetailGetLoadMoreDate {

    private TopicDetailFragment fragment;

    @Override
    protected int getLayoutView() {
        return R.layout.layout_just_framelayout;
    }

    /**
     * 壁纸头像下的图片下载不加水印
     *
     * @return
     */
    public String isSpecialTopic() {
        return fragment == null ? null : fragment.topicId;
    }

    @Override
    protected void initView() {
        fullScreen(this);
        String extra = getIntent().getStringExtra(HelperForStartActivity.key_other_type);
        String id = getIntent().getStringExtra(HelperForStartActivity.key_user_id);

        if (HelperForStartActivity.type_other_topic.equals(extra)) {

            boolean hasFollow = getIntent().getBooleanExtra(HelperForStartActivity.key_topic_follow, false);
//            ImageView view = findViewById(R.id.iv_go_publish);
//            view.setVisibility(View.VISIBLE);
//            view.setOnClickListener(v -> {
//                if (topicInfoBean != null) {
//                    TopicItemBean topicItemBean = new TopicItemBean();
//                    topicItemBean.tagalias = topicInfoBean.getTagalias();
//                    topicItemBean.tagid = topicInfoBean.getTagid();
//                    topicItemBean.tagimg = topicInfoBean.getTagimg();
//                    HelperForStartActivity.openPublishFromTopic(topicItemBean);
//                } else {
//                    HelperForStartActivity.openPublish(v);
//                }
//            });
            fragment = new TopicDetailFragment();
            fragment.setDate(id, hasFollow);
            turnToFragment(null, fragment, R.id.fl_fragment_content);

        } else if (HelperForStartActivity.type_other_praise.equals(extra)) {
            int friendCount = getIntent().getIntExtra("friendCount", 2);
            OtherParaiseUserFragment fragment = new OtherParaiseUserFragment();
            //这个相当于在他人页面的用户列表,只有已关注和未关注两个状态
            fragment.setDate(id, false, friendCount);
            turnToFragment(null, fragment, R.id.fl_fragment_content);
        }
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
