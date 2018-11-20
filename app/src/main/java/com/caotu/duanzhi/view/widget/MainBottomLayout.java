package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
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
    private View viewRed;


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
        viewRed = rootView.findViewById(R.id.view_red);
        addView(rootView);
        mIvHome.setSelected(true);
    }

    public void showRed(boolean isShow) {
        if (viewRed!=null){
            viewRed.setVisibility(isShow ? VISIBLE : GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_home_click:
                if (currentIndex == 0) return;
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

    public void bindViewPager(ViewPager slipViewPager) {
        if (slipViewPager == null) return;
        slipViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    currentIndex = 0;
                    mIvHome.setSelected(true);
                    mLineHomeTab.setVisibility(VISIBLE);
                    mIvMineTab.setSelected(false);
                    mLineMineTab.setVisibility(INVISIBLE);
                } else if (position == 1) {
                    currentIndex = 1;
                    mIvMineTab.setSelected(true);
                    mLineMineTab.setVisibility(VISIBLE);
                    mIvHome.setSelected(false);
                    mLineHomeTab.setVisibility(INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public interface BottomClickListener {
        void tabSelector(int index);
    }
}
