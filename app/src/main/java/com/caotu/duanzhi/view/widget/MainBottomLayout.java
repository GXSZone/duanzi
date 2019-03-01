package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.MySpUtils;

public class MainBottomLayout extends LinearLayout implements View.OnClickListener {


    private TextView mHomeTab, mDiscoverTab, mNoticeTab, mMineTab;

    private int currentIndex = 0;
    public BottomClickListener listener;
    private View viewRed;
    private View settingRedTip;
    int colorSelected = Color.parseColor("#6D5444");
    int colorNormal = Color.parseColor("#C7C7C7");

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
        rootView.findViewById(R.id.iv_publish_click).setOnClickListener(this);
        settingRedTip = rootView.findViewById(R.id.setting_tip);
        settingRedTip.setVisibility(isShowTip() ? GONE : VISIBLE);

        mHomeTab = rootView.findViewById(R.id.home_tab);
        mDiscoverTab = rootView.findViewById(R.id.discover_tab);
        mNoticeTab = rootView.findViewById(R.id.notice_tab);
        mMineTab = rootView.findViewById(R.id.mine_tab);

        mHomeTab.setOnClickListener(this);
        mDiscoverTab.setOnClickListener(this);
        //点击事件给父控件,子控件太小
        findViewById(R.id.rl_notice_tab).setOnClickListener(this);
        findViewById(R.id.rl_mine_tab).setOnClickListener(this);

        viewRed = rootView.findViewById(R.id.view_red);
        addView(rootView);

        setDrawableColor(mHomeTab, true);
        setDrawableColor(mDiscoverTab, false);
        setDrawableColor(mNoticeTab, false);
        setDrawableColor(mMineTab, false);
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
            case R.id.home_tab:
                if (currentIndex == 0) return;
                if (listener != null) {
                    listener.tabSelector(0);
                }
                break;
            case R.id.discover_tab:
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
            case R.id.rl_notice_tab:
                if (currentIndex == 2) return;
                if (listener != null) {
                    listener.tabSelector(2);
                }
                break;
            case R.id.rl_mine_tab:
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
                        setDrawableColor(mDiscoverTab, true);
                        setDrawableColor(mHomeTab, false);
                        setDrawableColor(mNoticeTab, false);
                        setDrawableColor(mMineTab, false);
                        break;
                    case 2:
                        setDrawableColor(mDiscoverTab, false);
                        setDrawableColor(mHomeTab, false);
                        setDrawableColor(mNoticeTab, true);
                        setDrawableColor(mMineTab, false);
                        break;
                    case 3:
                        setDrawableColor(mDiscoverTab, false);
                        setDrawableColor(mHomeTab, false);
                        setDrawableColor(mNoticeTab, false);
                        setDrawableColor(mMineTab, true);
                        break;

                    default:
                        setDrawableColor(mHomeTab, true);
                        setDrawableColor(mDiscoverTab, false);
                        setDrawableColor(mNoticeTab, false);
                        setDrawableColor(mMineTab, false);
                        break;
                }
            }
        });
    }

    /**
     * 图片上色
     */
    public void setDrawableColor(TextView textView, boolean isSelected) {
        textView.setSelected(isSelected);
        Drawable[] drawables = textView.getCompoundDrawables();
        for (int i = 0, size = drawables.length; i < size; i++) {
            if (null != drawables[i]) {
                drawables[i].setColorFilter(new PorterDuffColorFilter(isSelected ? colorSelected : colorNormal,
                        PorterDuff.Mode.SRC_IN));
            }
        }
    }

    public interface BottomClickListener {
        void tabSelector(int index);

        void tabPublish();

        void isFullScreen(boolean yes);
    }
}
