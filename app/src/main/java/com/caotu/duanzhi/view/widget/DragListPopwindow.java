package com.caotu.duanzhi.view.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;

import java.util.List;

public class DragListPopwindow extends ListPopupWindow {

    /**
     * @param context      上下文
     * @param string       获取到未打开列表时显示的值
     * @param list         需要显示的列表的集合
     * @param itemsOnClick listview在activity中的点击监听事件
     */

    public DragListPopwindow(Activity context,  String string, final List<String> list, AdapterView.OnItemClickListener itemsOnClick) {
        super(context);
        // 设置SelectPicPopupWindow弹出窗体的宽
//        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        //    this.setWidth(view.getWidth());
//        // 设置SelectPicPopupWindow弹出窗体的高
//        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
       setBackgroundDrawable(dw);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        //  this.setAnimationStyle(R.style.AnimationPreview);
        setAdapter(new SpinnerPopAdapter(list, string));
        this.setOnItemClickListener(itemsOnClick);
    }


    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            setAnchorView(parent);
            darkenBackground(0.9f);//弹出时让页面背景回复给原来的颜色降低透明度，让背景看起来变成灰色
        }
    }

    /**
     * 关闭popupWindow
     */
    public void dismissPopupWindow() {
        this.dismiss();
        darkenBackground(1f);//关闭时让页面背景回复为原来的颜色
    }

    /**
     * 改变背景颜色，主要是在PopupWindow弹出时背景变化，通过透明度设置
     */
    private void darkenBackground(Float bgcolor) {
        Activity context = MyApplication.getInstance().getRunningActivity();
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgcolor;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    public class SpinnerPopAdapter extends BaseAdapter {
        private List<String> content;
        private String seletedText;

        public SpinnerPopAdapter(List<String> content, String selector) {
            this.content = content;
            seletedText = selector;
        }

        @Override
        public int getCount() {
            return content == null ? 0 : content.size();
        }

        @Override
        public Object getItem(int position) {
            return content.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pop_list_item_layout, parent, false);
            TextView itemText = convertView.findViewById(R.id.item_text);
            itemText.setText(content.get(position));
            if (seletedText.equals(content.get(position))){
                itemText.setTextColor(DevicesUtils.getColor(R.color.color_FF698F));
            }else {
                itemText.setTextColor(DevicesUtils.getColor(R.color.color_171F24));
            }
            return convertView;
        }

    }
}
