package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.caotu.duanzhi.Http.bean.TopicItemBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.AppUtil;
import com.ruffian.library.widget.RCheckBox;

import java.util.List;

/**
 * 改类以后可以作为单选操作,如果有选中状态会取消其他已经选中的view
 */
public class OneSelectedLayout extends LinearLayout implements View.OnClickListener {
    public OneSelectedLayout(Context context) {
        super(context);
    }

    public OneSelectedLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    List<TopicItemBean> mDates;

    /**
     * 供外面调用,省去操作,封装在内部addView
     *
     * @param dates
     */
    public void setDates(List<TopicItemBean> dates) {
        if (!AppUtil.listHasDate(dates)) return;
        mDates = dates;
        if (getChildCount() > 0) removeAllViews();
        for (int i = 0; i < dates.size(); i++) {
            RCheckBox inflate = (RCheckBox) LayoutInflater.from(getContext()).inflate(R.layout.publish_item_topics, this, false);
            inflate.setText(dates.get(i).tagalias);
            inflate.setOnClickListener(this);
            addView(inflate);
        }
    }


    @Override
    public void onClick(View v) {
        //check 事件比点击事件早,在点击事件里获取check状态已经check生效
        if (v instanceof CheckBox) {
            if (!((CheckBox) v).isChecked()) {
                if (listener != null) {
                    listener.itemSelect(null);
                }
                return;
            }
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            CheckBox childAt = (CheckBox) getChildAt(i);
            if (v == childAt) {
                if (listener != null && mDates != null) {
                    listener.itemSelect(mDates.get(i));
                }
                Log.i("OneSelectedLayout", "选中监听回调");
                continue;
            }
            childAt.setChecked(false);
        }
    }

    public void clearAllCheck() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            CheckBox childAt = (CheckBox) getChildAt(i);
            childAt.setChecked(false);
        }
    }

    ItemSelectedListener listener;

    public void setListener(ItemSelectedListener listener) {
        this.listener = listener;
    }

    public interface ItemSelectedListener {
        void itemSelect(TopicItemBean bean);
    }
}
