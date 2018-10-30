package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.caotu.duanzhi.R;

public class MainBottomLayout extends LinearLayout implements View.OnClickListener {


    private ImageView mIvHome;
    private View mLineHomeTab;

    private ImageView mIvPublishClick;

    private ImageView mIvMineTab;
    private View mLineMineTab;

    private int currentIndex = 0;
    public BottomClickListener listener;


    public MainBottomLayout(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public MainBottomLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.main_bottom_layout, this, false);
//        View rootView = inflate(context, R.layout.main_bottom_layout, null);
        rootView.findViewById(R.id.ll_home_click).setOnClickListener(this);
        rootView.findViewById(R.id.ll_mine_click).setOnClickListener(this);
        mIvHome = rootView.findViewById(R.id.iv_home);
        mLineHomeTab = rootView.findViewById(R.id.line_home_tab);
        mIvPublishClick = rootView.findViewById(R.id.iv_publish_click);
        mIvPublishClick.setOnClickListener(this);
        mIvMineTab = rootView.findViewById(R.id.iv_mine_tab);
        mLineMineTab = rootView.findViewById(R.id.line_mine_tab);
        addView(rootView);
        mIvHome.setSelected(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_home_click:
                if (currentIndex == 0) return;

                currentIndex = 0;
                mIvHome.setSelected(true);
                mLineHomeTab.setVisibility(VISIBLE);
                mIvMineTab.setSelected(false);
                mLineMineTab.setVisibility(INVISIBLE);

                if (listener != null) {
                    listener.tabSelector(0);
                }
                break;
            case R.id.iv_publish_click:
                if (listener != null) {
                    listener.tabSelector(1);
                }
                break;
            case R.id.ll_mine_click:
                if (currentIndex == 1) return;
                currentIndex = 1;
                mIvMineTab.setSelected(true);
                mLineMineTab.setVisibility(VISIBLE);
                mIvHome.setSelected(false);
                mLineHomeTab.setVisibility(INVISIBLE);
                if (listener != null) {
                    listener.tabSelector(2);
                }
                break;
            default:
                break;
        }
    }

    public void setListener(BottomClickListener listener) {
        this.listener = listener;
    }

   public interface BottomClickListener {
        void tabSelector(int index);
    }
}
