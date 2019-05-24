package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.ruffian.library.widget.RTextView;

//https://github.com/shetmobile/MeowBottomNavigation 炫酷底部栏
public class MainBottomLayout extends RelativeLayout implements View.OnClickListener {


    private TextView mHomeTab, mDiscoverTab, mNoticeTab, mMineTab;

    private int currentIndex = 0;
    public BottomClickListener listener;
    private RTextView viewRed;
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

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.main_bottom_layout, this, false);
        rootView.findViewById(R.id.iv_publish_click).setOnClickListener(this);
        mHomeTab = rootView.findViewById(R.id.home_tab);
        mDiscoverTab = rootView.findViewById(R.id.discover_tab);
        mNoticeTab = rootView.findViewById(R.id.notice_tab);
        mMineTab = rootView.findViewById(R.id.mine_tab);

        noticeView = rootView.findViewById(R.id.rl_msg_tab);
        noticeView.setOnClickListener(this);
        rootView.findViewById(R.id.rl_mine_tab).setOnClickListener(this);
        rootView.findViewById(R.id.rl_home_tab).setOnClickListener(this);
        rootView.findViewById(R.id.rl_find_tab).setOnClickListener(this);

        viewRed = rootView.findViewById(R.id.view_red);
        addView(rootView);

        mHomeTab.setSelected(true);
        mHomeTab.setBackgroundResource(R.drawable.small_pic);
//        setDrawableColor(mHomeTab, true);
//        setDrawableColor(mDiscoverTab, false);
//        setDrawableColor(mNoticeTab, false);
//        setDrawableColor(mMineTab, false);
    }

    public void showRed(int count) {
        if (viewRed != null) {
            viewRed.setVisibility(count > 0 ? VISIBLE : GONE);
            viewRed.setText(count > 99 ? "99+" : count + "");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_home_tab:
                if (currentIndex == 0) return;
                if (listener != null) {
                    listener.tabSelector(0);
                }
                break;
            case R.id.rl_find_tab:
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
            case R.id.rl_msg_tab:
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
        if (isSelected) {
            textView.setBackgroundResource(R.drawable.small_pic);
            textView.animate().scaleXBy(0.2f).scaleYBy(0.2f)
                    .setInterpolator(new CycleInterpolator(0.5f));
        } else {
            textView.setBackground(null);
        }
    }

    public interface BottomClickListener {
        void tabSelector(int index);

        void tabPublish();

        void isFullScreen(boolean yes);
    }
}
