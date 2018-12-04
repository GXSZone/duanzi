package com.caotu.duanzhi.view.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.luck.picture.lib.tools.ScreenUtils;

import java.util.List;

/**
 * @author WEIGE
 * @email qlw@xkeshi.com
 * @date 2017/7/27
 * @discription
 */

public class MyListPopupWindow extends PopupWindow {

    private ListPopAdapter mAdapter;
    ItemChangeTextListener listener;
    private Animation animationIn, animationOut;
    private View window;
    private ListView listView;
    private boolean isDismiss = false;

    public MyListPopupWindow(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListPopupWindow(Activity myNoticeActivity, String s, List<String> list, ItemChangeTextListener callback) {
        super(myNoticeActivity);
        listener = callback;
        window = LayoutInflater.from(myNoticeActivity).inflate(R.layout.pop_listview, null);
        this.setWidth(ScreenUtils.getScreenWidth(myNoticeActivity));
        this.setHeight(ScreenUtils.getScreenHeight(myNoticeActivity));
        this.setAnimationStyle(R.style.WindowStyle);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        this.setBackgroundDrawable(new ColorDrawable(Color.argb(123, 0, 0, 0)));
        animationIn = AnimationUtils.loadAnimation(myNoticeActivity, R.anim.photo_album_show);
        animationOut = AnimationUtils.loadAnimation(myNoticeActivity, R.anim.photo_album_dismiss);
        initView(list, s);
    }


    private void initView(List<String> items, String selected) {
        this.setContentView(window);
        window.findViewById(R.id.list_parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        listView = window.findViewById(R.id.list_view);
        // ListView适配器
        mAdapter = new ListPopAdapter(items, selected);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.itemSelected(view, position, items.get(position));
                }
                dismiss();
            }
        });

    }

    @Override
    public void showAsDropDown(View anchor) {
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                Rect rect = new Rect();
                anchor.getGlobalVisibleRect(rect);
                int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
                setHeight(h);
            }
            super.showAsDropDown(anchor);
            isDismiss = false;
            listView.startAnimation(animationIn);
//            StringUtils.modifyTextViewDrawable(picture_title, drawableUp, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        if (isDismiss) {
            return;
        }
        isDismiss = true;
        listView.startAnimation(animationOut);
        dismiss();
        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isDismiss = false;
                MyListPopupWindow.super.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
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




