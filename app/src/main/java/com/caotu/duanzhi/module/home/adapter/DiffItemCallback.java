package com.caotu.duanzhi.module.home.adapter;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.chad.library.adapter.base.diff.BaseQuickDiffCallback;
import java.util.List;

public class DiffItemCallback extends BaseQuickDiffCallback<MomentsDataBean> {
    public DiffItemCallback(@Nullable List<MomentsDataBean> newList) {
        super(newList);
    }

    /**
     * 判断是否是同一个item
     *
     * @param oldItem New data
     * @param newItem old Data
     * @return
     */
    @Override
    protected boolean areItemsTheSame(@NonNull MomentsDataBean oldItem, @NonNull MomentsDataBean newItem) {
        return TextUtils.equals(oldItem.getContentid(), newItem.getContentid());
    }

    /**
     * 当是同一个item时，再判断内容是否发生改变
     *
     * @param oldItem New data
     * @param newItem old Data
     * @return
     */
    @Override
    protected boolean areContentsTheSame(@NonNull MomentsDataBean oldItem, @NonNull MomentsDataBean newItem) {
        //其中之一有变化有需要刷新页面
        boolean hasChangeComment = oldItem.getContentcomment() != newItem.getContentcomment();
        boolean hasChangeLike = oldItem.getContentgood() != newItem.getContentgood();
        boolean hasChangeBad = oldItem.getContentbad() != newItem.getContentbad();
        boolean hasChangeCollection = TextUtils.equals(oldItem.getIscollection(), newItem.getIscollection());
        return hasChangeComment || hasChangeLike || hasChangeBad || hasChangeCollection;
    }

    /**
     * 可选实现
     * 如果需要精确修改某一个view中的内容，请实现此方法。
     * 如果不实现此方法，或者返回null，将会直接刷新整个item。
     *
     * @param oldItem Old data
     * @param newItem New data
     * @return Payload info. if return null, the entire item will be refreshed.
     */
//    @Override
//    protected Object getChangePayload(@NonNull MomentsDataBean oldItem, @NonNull MomentsDataBean newItem) {
//        return null;
//    }
}
