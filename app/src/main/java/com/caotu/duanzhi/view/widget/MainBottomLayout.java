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

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.UmengHelper;
import com.caotu.duanzhi.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.MySpUtils;
import com.ruffian.library.widget.RTextView;

//https://github.com/shetmobile/MeowBottomNavigation 炫酷底部栏
public class MainBottomLayout extends LinearLayout implements View.OnClickListener {


    private TextView mHomeTab, mDiscoverTab, mNoticeTab, mMineTab;

    private int currentIndex = 0;
    public BottomClickListener listener;
    private RTextView viewRed;
    private View settingRedTip;
    int colorSelected = Color.parseColor("#6D5444");
    int colorNormal = Color.parseColor("#C7C7C7");
    private View noticeView;

    public View getNoticeView() {
        return noticeView;
    }

    public MainBottomLayout(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public MainBottomLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public boolean isShowTip() {
        return MySpUtils.getBoolean(MySpUtils.SP_ENTER_SETTING, false);
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
        noticeView = rootView.findViewById(R.id.rl_notice_tab);
        noticeView.setOnClickListener(this);
        rootView.findViewById(R.id.rl_mine_tab).setOnClickListener(this);

        viewRed = rootView.findViewById(R.id.view_red);
        addView(rootView);

        setDrawableColor(mHomeTab, true);
        setDrawableColor(mDiscoverTab, false);
        setDrawableColor(mNoticeTab, false);
        setDrawableColor(mMineTab, false);
    }

    public void showRed(int count) {
        if (viewRed != null) {
            viewRed.setVisibility(count > 0 ? VISIBLE : GONE);
            viewRed.setText(count > 99 ? "99+" : count + "");
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
                        CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.discover_find);
                        UmengHelper.event(UmengStatisticsKeyIds.discover);
                        setDrawableColor(mDiscoverTab, true);
                        setDrawableColor(mHomeTab, false);
                        setDrawableColor(mNoticeTab, false);
                        setDrawableColor(mMineTab, false);
                        break;
                    case 2:
                        UmengHelper.event(UmengStatisticsKeyIds.notice);
                        setDrawableColor(mDiscoverTab, false);
                        setDrawableColor(mHomeTab, false);
                        setDrawableColor(mNoticeTab, true);
                        setDrawableColor(mMineTab, false);
                        break;
                    case 3:
                        CommonHttpRequest.getInstance().statisticsApp(CommonHttpRequest.AppType.mine_me);
                        UmengHelper.event(UmengStatisticsKeyIds.my);
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
        // TODO: 2019/3/29 由于底部tab栏只有顶部图片所以这里直接取消遍历,第二个就是顶部drawable
        if (isSelected) {
            drawables[1].setColorFilter(new PorterDuffColorFilter(colorSelected, PorterDuff.Mode.SRC_IN));
        } else {
            drawables[1].clearColorFilter();
        }
    }

    public interface BottomClickListener {
        void tabSelector(int index);

        void tabPublish();

        void isFullScreen(boolean yes);
    }
}
