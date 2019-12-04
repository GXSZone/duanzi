package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.ruffian.library.widget.RTextView;

//https://github.com/shetmobile/MeowBottomNavigation 炫酷底部栏
//#48004 java.lang.StackOverflowError
//        stack size 8MB
//        android.view.ViewGroup.resetResolvedLayoutDirection(ViewGroup.java:7185)
public class MainBottomLayout extends FrameLayout implements View.OnClickListener {


    private TextView mHomeTab, mDiscoverTab, mNoticeTab, mMineTab;

    private int currentIndex = 0;
    public BottomClickListener listener;
    private RTextView viewRed;

    public MainBottomLayout(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public MainBottomLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        //这里注意下区别,这样就不需要addview了
//        LayoutInflater.from(context).inflate(R.layout.main_bottom_layout, this);
        View rootView = LayoutInflater.from(context).inflate(R.layout.main_bottom_layout, this, false);
        mHomeTab = rootView.findViewById(R.id.home_tab);
        mDiscoverTab = rootView.findViewById(R.id.discover_tab);
        mNoticeTab = rootView.findViewById(R.id.notice_tab);
        mMineTab = rootView.findViewById(R.id.mine_tab);

        mHomeTab.setOnClickListener(this);
        mDiscoverTab.setOnClickListener(this);
        mNoticeTab.setOnClickListener(this);
        mMineTab.setOnClickListener(this);
        rootView.findViewById(R.id.iv_publish_click).setOnClickListener(this);


        viewRed = rootView.findViewById(R.id.view_red);
        removeAllViews();
        addView(rootView);
        mHomeTab.setSelected(true);

    }

    public void showRed(int count) {
        if (viewRed != null) {
            viewRed.setVisibility(count > 0 ? VISIBLE : GONE);
            viewRed.setText(count > 99 ? "99+" : count + "");
        }
    }

    @Override
    public void onClick(View v) {
        if (listener == null || mViewpager == null) return;
        switch (v.getId()) {
            case R.id.discover_tab:
                if (currentIndex == 1) {
                    listener.tabSelectorDouble(1);
                    return;
                }
                if (CommonHttpRequest.teenagerIsOpen){
                    HelperForStartActivity.openTeenager(CommonHttpRequest.teenagerIsOpen,
                            CommonHttpRequest.teenagerPsd);
                    return;
                }
                mViewpager.setCurrentItem(1, false);
                listener.tabSelector(1);

                break;
            case R.id.iv_publish_click:
                if (listener != null) {
                    listener.tabPublish();
                }
                break;
            case R.id.notice_tab:
                if (currentIndex == 2) {
                    listener.tabSelectorDouble(2);
                    return;
                }
                mViewpager.setCurrentItem(2, false);
                listener.tabSelector(2);
                break;
            case R.id.mine_tab:
                if (currentIndex == 3) {
                    listener.tabSelectorDouble(3);
                    return;
                }
                mViewpager.setCurrentItem(3, false);
                listener.tabSelector(3);
                break;
            default:
                if (currentIndex == 0) {
                    listener.tabSelectorDouble(0);
                    return;
                }
                mViewpager.setCurrentItem(0, false);
                listener.tabSelector(0);
                break;
        }
    }

    public void setListener(BottomClickListener listener) {
        this.listener = listener;
    }

    ViewPager mViewpager;

    public void bindViewPager(ViewPager slipViewPager) {
        if (slipViewPager == null) return;
        mViewpager = slipViewPager;
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
        if (isSelected) {
            textView.animate().scaleX(1.15f).scaleY(1.15f)
                    .setInterpolator(new CycleInterpolator(0.5f));
        } else {
            textView.setScaleX(1.0f);
            textView.setScaleY(1.0f);
        }
    }

    public interface BottomClickListener {
        void tabSelector(int index);

        void tabSelectorDouble(int index);

        void tabPublish();

        void isFullScreen(boolean yes);
    }
}
