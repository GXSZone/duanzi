package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.MySpUtils;

public class MainBottomLayout extends LinearLayout implements View.OnClickListener {


    private ImageView mIvHome, mIvDiscoverTab, mIvNoticeTab, mIvMineTab;
    private TextView mLineHomeTab, mLineDiscoverTab, mLineNoticeTab, mLineMineTab;

    private int currentIndex = 0;
    public BottomClickListener listener;
    private View viewRed;
    private View settingRedTip;


    public MainBottomLayout(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public MainBottomLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public boolean isShowTip() {
        boolean settingTip = MySpUtils.getBoolean(MySpUtils.SP_ENTER_SETTING, false);
        boolean isShowHistoryTip = MySpUtils.getBoolean(MySpUtils.SP_ENTER_HISTORY, false);
        return settingTip && isShowHistoryTip;
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.main_bottom_layout, this, false);
//        View rootView = inflate(context, R.layout.main_bottom_layout, null);
        rootView.findViewById(R.id.ll_home_click).setOnClickListener(this);
        rootView.findViewById(R.id.ll_mine_click).setOnClickListener(this);
        rootView.findViewById(R.id.ll_discover).setOnClickListener(this);
        rootView.findViewById(R.id.ll_notice_click).setOnClickListener(this);
        rootView.findViewById(R.id.iv_publish_click).setOnClickListener(this);
        settingRedTip = rootView.findViewById(R.id.setting_tip);

        settingRedTip.setVisibility(isShowTip() ? GONE : VISIBLE);
        mIvHome = rootView.findViewById(R.id.iv_home);
        mLineHomeTab = rootView.findViewById(R.id.line_home_tab);

        mIvDiscoverTab = rootView.findViewById(R.id.iv_discover_tab);
        mLineDiscoverTab = rootView.findViewById(R.id.line_discover_tab);

        mIvNoticeTab = rootView.findViewById(R.id.iv_notice_tab);
        mLineNoticeTab = rootView.findViewById(R.id.line_notice_tab);

        mIvMineTab = rootView.findViewById(R.id.iv_mine_tab);
        mLineMineTab = rootView.findViewById(R.id.line_mine_tab);

        viewRed = rootView.findViewById(R.id.view_red);
        addView(rootView);
        mIvHome.setColorFilter(Color.parseColor("#6D5444"));
    }

    public void showRed(boolean isShow) {
        if (viewRed != null) {
            viewRed.setVisibility(isShow ? VISIBLE : GONE);
        }
    }

    public void hideSettingTipRed() {
        if (settingRedTip != null) {
            settingRedTip.setVisibility(isShowTip() ? GONE : VISIBLE);
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
            case R.id.ll_discover:
                if (currentIndex == 1) return;
                if (listener != null) {
                    listener.tabSelector(1);
                }
                break;
            case R.id.iv_publish_click:
                if (listener != null) {
                    listener.tabPublish();
                }
                break;
            case R.id.ll_notice_click:
                if (currentIndex == 2) return;
                if (listener != null) {
                    listener.tabSelector(2);
                }
                break;
            case R.id.ll_mine_click:
                if (currentIndex == 3) return;
                if (listener != null) {
                    listener.tabSelector(3);
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
        slipViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
                if (listener != null) {
                    listener.isFullScreen(position == 3);
                }
                switch (position) {
                    case 1:
                        mIvDiscoverTab.setColorFilter(Color.parseColor("#6D5444"));
                        mLineDiscoverTab.setSelected(true);

                        mIvHome.setColorFilter(DevicesUtils.getColor(R.color.transparent));
                        mLineHomeTab.setSelected(false);

                        mIvMineTab.setColorFilter(DevicesUtils.getColor(R.color.transparent));
                        mLineMineTab.setSelected(false);

                        mIvNoticeTab.setColorFilter(DevicesUtils.getColor(R.color.transparent));
                        mLineNoticeTab.setSelected(false);
                        break;
                    case 2:
                        mIvNoticeTab.setColorFilter(Color.parseColor("#6D5444"));
                        mLineNoticeTab.setSelected(true);

                        mIvHome.setColorFilter(DevicesUtils.getColor(R.color.transparent));
                        mLineHomeTab.setSelected(false);

                        mIvMineTab.setColorFilter(DevicesUtils.getColor(R.color.transparent));
                        mLineMineTab.setSelected(false);

                        mIvDiscoverTab.setColorFilter(DevicesUtils.getColor(R.color.transparent));
                        mLineDiscoverTab.setSelected(false);
                        break;
                    case 3:
                        mIvMineTab.setColorFilter(Color.parseColor("#6D5444"));
                        mLineMineTab.setSelected(true);

                        mIvNoticeTab.setColorFilter(DevicesUtils.getColor(R.color.transparent));
                        mLineNoticeTab.setSelected(false);

                        mIvHome.setColorFilter(DevicesUtils.getColor(R.color.transparent));
                        mLineHomeTab.setSelected(false);

                        mIvDiscoverTab.setColorFilter(DevicesUtils.getColor(R.color.transparent));
                        mLineDiscoverTab.setSelected(false);
                        break;

                    default:
                        mIvHome.setColorFilter(Color.parseColor("#6D5444"));
                        mLineHomeTab.setSelected(true);

                        mIvMineTab.setColorFilter(DevicesUtils.getColor(R.color.transparent));
                        mLineMineTab.setSelected(false);

                        mIvDiscoverTab.setColorFilter(DevicesUtils.getColor(R.color.transparent));
                        mLineDiscoverTab.setSelected(false);

                        mIvNoticeTab.setColorFilter(DevicesUtils.getColor(R.color.transparent));
                        mLineNoticeTab.setSelected(false);
                        break;
                }
            }
        });
    }

    public interface BottomClickListener {
        void tabSelector(int index);

        void tabPublish();

        void isFullScreen(boolean yes);
    }
}
