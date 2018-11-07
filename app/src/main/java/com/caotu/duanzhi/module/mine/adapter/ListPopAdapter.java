package com.caotu.duanzhi.module.mine.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;

import java.util.List;

/**
 * @author mac
 * @日期: 2018/11/7
 * @describe TODO
 */
public class ListPopAdapter extends BaseAdapter {
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
        notifyDataSetChanged();
    }

    public void setSelectItem(String position) {
        initIndex = position;
        notifyDataSetChanged();
    }
}
