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
import com.caotu.duanzhi.utils.MySpUtils;

public class MainBottomLayout extends LinearLayout implements View.OnClickListener {


    private ImageView mIvHome;
    private View mLineHomeTab;

    private ImageView mIvMineTab;
    private View mLineMineTab;

    private int currentIndex = 0;
    public BottomClickListener listener;
    private View viewRed;
    private ImageView mIvDiscoverTab;
    private View mLineDiscoverTab;
    private ImageView mIvNoticeTab;
    private View mLineNoticeTab;
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
        mIvHome.setSelected(true);
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
                switch (position) {
                    case 1:
                        mIvDiscoverTab.setSelected(true);
                        mLineDiscoverTab.setVisibility(VISIBLE);

                        mIvHome.setSelected(false);
                        mLineHomeTab.setVisibility(INVISIBLE);

                        mIvMineTab.setSelected(false);
                        mLineMineTab.setVisibility(INVISIBLE);

                        mIvNoticeTab.setSelected(false);
                        mLineNoticeTab.setVisibility(INVISIBLE);
                        break;
                    case 2:
                        mIvNoticeTab.setSelected(true);
                        mLineNoticeTab.setVisibility(VISIBLE);

                        mIvHome.setSelected(false);
                        mLineHomeTab.setVisibility(INVISIBLE);

                        mIvMineTab.setSelected(false);
                        mLineMineTab.setVisibility(INVISIBLE);

                        mIvDiscoverTab.setSelected(false);
                        mLineDiscoverTab.setVisibility(INVISIBLE);
                        break;
                    case 3:
                        mIvMineTab.setSelected(true);
                        mLineMineTab.setVisibility(VISIBLE);

                        mIvNoticeTab.setSelected(false);
                        mLineNoticeTab.setVisibility(INVISIBLE);

                        mIvHome.setSelected(false);
                        mLineHomeTab.setVisibility(INVISIBLE);

                        mIvDiscoverTab.setSelected(false);
                        mLineDiscoverTab.setVisibility(INVISIBLE);
                        break;

                    default:
                        mIvHome.setSelected(true);
                        mLineHomeTab.setVisibility(VISIBLE);

                        mIvMineTab.setSelected(false);
                        mLineMineTab.setVisibility(INVISIBLE);

                        mIvDiscoverTab.setSelected(false);
                        mLineDiscoverTab.setVisibility(INVISIBLE);

                        mIvNoticeTab.setSelected(false);
                        mLineNoticeTab.setVisibility(INVISIBLE);
                        break;
                }
            }
        });
    }

    public interface BottomClickListener {
        void tabSelector(int index);

        void tabPublish();
    }
}
