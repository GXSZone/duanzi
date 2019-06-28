package com.caotu.duanzhi.module.mine.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.ThemeBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.FastClickListener;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;

import java.util.List;

/**
 * @author mac
 * @日期: 2018/11/5
 * @describe TODO
 */
public class FocusTopicAdapter extends FocusAdapter {
    public FocusTopicAdapter(@Nullable List<ThemeBean> data) {
        super(data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ThemeBean item) {
        super.convert(helper, item);
        helper.setOnClickListener(R.id.iv_item_image, v ->
                HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic,
                        item.getUserId()));
    }

    @Override
    public void initFollowState(boolean isMe, boolean isFocus, ImageView follow, ThemeBean item) {
        if (isMe) {
            follow.setSelected(true);
        } else {
            follow.setEnabled(!isFocus);
        }
    }

    @Override
    public void initFollowClick(BaseViewHolder helper, ThemeBean item, boolean isMe) {
        View view = helper.getView(R.id.iv_selector_is_follow);
        view.setTag(UmengStatisticsKeyIds.follow_topic);
        view.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                //只有关注操作,没有取消关注的操作,只有在自己主页才能取消,他人主页下关注完后不能取消关注了
                if (isMe) {
                    requestFocus(view, helper.getAdapterPosition(), "1", false, item.getUserId(), isMe);
                } else {
                    if (item.isFocus()) return;
                    requestFocus(view, helper.getAdapterPosition(), "1", !item.isFocus(), item.getUserId(), isMe);
                }
            }
        });

    }

    public void requestFocus(View v, int adapterPosition, String s, boolean b, String userId, boolean isMe) {
        CommonHttpRequest.getInstance().requestFocus(userId, s, b, new JsonCallback<BaseResponseBean<String>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<String>> response) {
                ImageView isFocusView = (ImageView) v;
                if (isMe) {
                    if (AppUtil.listHasDate(getData())) {
                        remove(adapterPosition);
                    }
                    ToastUtil.showShort("取消关注成功！");
                } else {
                    ToastUtil.showShort("关注成功！");
                    isFocusView.setEnabled(false);
                }
            }
        });
    }

}
