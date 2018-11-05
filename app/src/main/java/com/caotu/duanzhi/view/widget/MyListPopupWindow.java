package com.caotu.duanzhi.view.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.ListPopupWindow;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.mine.MyNoticeActivity;
import com.caotu.duanzhi.utils.DevicesUtils;

import java.util.List;

/**
 * @author WEIGE
 * @email qlw@xkeshi.com
 * @date 2017/7/27
 * @discription
 */

public class MyListPopupWindow extends ListPopupWindow {

    private ListPopAdapter mAdapter;
    private Activity context;
    ItemChangeTextListener listener;

    public MyListPopupWindow(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListPopupWindow(MyNoticeActivity myNoticeActivity, String s, List<String> list, ItemChangeTextListener callback) {
        super(myNoticeActivity);
        listener = callback;
        initView(myNoticeActivity, list, s);
    }


    private void initView(Activity context, List<String> items, String selected) {
        this.context = context;
        // ListView适配器
        mAdapter = new ListPopAdapter(items, selected);
        setAdapter(mAdapter);
        // 选择item的监听事件
        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                mAdapter.setSelectItem(pos);
                mAdapter.notifyDataSetChanged();
                if (listener != null) {
                    listener.itemSelected(view, pos, items.get(pos));
                }
            }
        });
        // 对话框的宽高
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
//        this.setAnimationStyle(R.style.popwindow_anim);
        // ListPopupWindow的锚,弹出框的位置是相对当前View的位置
        // this.setAnchorView(mTvSelectAllClassify);
        // ListPopupWindow 距锚view的距离-------针对有大背景图的
//        this.setVerticalOffset(ViewUtil.dp2Px(context, 10));
        setModal(true);
        setBackgroundDrawable(new ColorDrawable(0xb0000000));
//        try {
//            Class aClass = this.getClass().getSuperclass();
//            Field field = aClass.getDeclaredField("mPopup");
//            field.setAccessible(true);
//            PopupWindow window = (PopupWindow) field.get(this);
//            Class clazz = window.getClass().getSuperclass();
//            Field elevation = clazz.getDeclaredField("mElevation");
//            elevation.setAccessible(true);
//            elevation.setFloat(window, 0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void backgroundAlpha(float v) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = v;
        context.getWindow().setAttributes(lp);
    }

    /**
     * 用于设置初始化选中状态
     *
     * @param position
     */
    public void setSelectInit(int position) {
        mAdapter.setSelectItem(position);
        mAdapter.notifyDataSetChanged();
    }

    public void setSelectInitString(String position) {
        mAdapter.setSelectItem(position);
        mAdapter.notifyDataSetChanged();
    }

    public interface ItemChangeTextListener {
        void itemSelected(View v, int position, String selected);
    }

    private class ListPopAdapter extends BaseAdapter {
        List<String> strings;
        String initIndex;

        public ListPopAdapter(List<String> items, String selected) {
            strings = items;
            initIndex = selected;
        }

        @Override
        public int getCount() {
            return strings == null ? 0 : strings.size();
        }

        @Override
        public Object getItem(int position) {
            return strings == null ? null : strings.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pop_list_item_layout, parent, false);
            TextView textView = convertView.findViewById(R.id.item_text);
            if (initIndex.equals(strings.get(position))) {
                textView.setTextColor(DevicesUtils.getColor(R.color.color_FF8787));
            } else {
                textView.setTextColor(DevicesUtils.getColor(R.color.color_171F24));
            }
            textView.setText(strings.get(position));
            return convertView;
        }

        public void setSelectItem(int position) {
            initIndex = strings.get(position);
        }

        public void setSelectItem(String position) {
            initIndex = position;
        }
    }
}




