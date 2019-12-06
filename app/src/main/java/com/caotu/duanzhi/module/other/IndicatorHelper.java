package com.caotu.duanzhi.module.other;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.viewpager.widget.ViewPager;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.view.widget.ColorFlipPagerTitleView;
import com.caotu.duanzhi.view.widget.MyPagerIndicator;
import com.caotu.duanzhi.view.widget.ScaleTransitionPagerTitleView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

/**
 * 指示器的汇总类,省的到处拉屎
 */
public class IndicatorHelper {
    public static final String[] CHANNELS = {"推荐", "视频", "图片", "段子"};
    public static final String[] SEARCH_TYPE = {"综合", "帖子", "段友", "话题"};
    public static final String[] TITLES = {"作品", "评论"};
    public static final String[] TITLES2 = {"作品", "评论", "收藏"};

    public static final String[] FINDS = {"我的关注", "话题广场"};
    public static final String[] FOCUS = {"话题", "用户"};


    public static void initIndicator(Context context, ViewPager mViewpager,
                                     MagicIndicator mMagicIndicator, String[] titles) {
        CommonNavigator commonNavigator7 = new CommonNavigator(context);
        commonNavigator7.setAdjustMode(true);
        commonNavigator7.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorFlipPagerTitleView(context);
                simplePagerTitleView.setText(titles[index]);
                simplePagerTitleView.setNormalColor(DevicesUtils.getColor(R.color.color_cccccc));
                simplePagerTitleView.setSelectedColor(DevicesUtils.getColor(R.color.color_333333));
                simplePagerTitleView.setTextSize(18);
                simplePagerTitleView.getPaint().setFakeBoldText(true);
                simplePagerTitleView.setOnClickListener(v -> mViewpager.setCurrentItem(index));
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(UIUtil.dip2px(context, 3));
                indicator.setLineWidth(UIUtil.dip2px(context, 20));
                indicator.setRoundRadius(UIUtil.dip2px(context, 3));
                indicator.setStartInterpolator(new AccelerateInterpolator());
//                indicator.setYOffset(DevicesUtils.dp2px(8));
                indicator.setEndInterpolator(new DecelerateInterpolator(2.0f));
                indicator.setColors(Color.parseColor("#FF698F"));
                return indicator;
            }
        });
        mMagicIndicator.setNavigator(commonNavigator7);
        ViewPagerHelper.bind(mMagicIndicator, mViewpager);
    }

    public static void homeIndicator(Context context, ViewPager mViewpager,
                                     MagicIndicator mMagicIndicator, String[] titles) {
        CommonNavigator commonNavigator = new CommonNavigator(context);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                simplePagerTitleView.setNormalColor(DevicesUtils.getColor(R.color.color_333333));
                simplePagerTitleView.setSelectedColor(Color.BLACK);
                simplePagerTitleView.setText(titles[index]);
                simplePagerTitleView.setOnClickListener(v -> mViewpager.setCurrentItem(index));
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return new MyPagerIndicator(context);
            }
        });
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mViewpager);
    }

    /**
     * 关注页面指示器
     * @param context
     * @param mViewpager
     * @param mMagicIndicator
     * @param titles
     */
    public static void initFocusIndicator(Context context, ViewPager mViewpager,
                                     MagicIndicator mMagicIndicator, String[] titles) {
        CommonNavigator commonNavigator = new CommonNavigator(context);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                //这个是颜色框移动的效果
                ClipPagerTitleView clipPagerTitleView = new ClipPagerTitleView(context);
                clipPagerTitleView.setText(titles[index]);
                clipPagerTitleView.setTextSize(DevicesUtils.dp2px(30));
                clipPagerTitleView.setTextColor(DevicesUtils.getColor(R.color.color_BBBCCD));
                clipPagerTitleView.setClipColor(DevicesUtils.getColor(R.color.color_FF698F));
                clipPagerTitleView.setOnClickListener(v -> mViewpager.setCurrentItem(index));
                return clipPagerTitleView;
                //这个是选中和未选中的颜色渐变的效果
//                SimplePagerTitleView titleView = new ScaleTransitionPagerTitleView(context);
//                titleView.setText(titles[index]);
//                titleView.setTextSize(30);
//                titleView.setNormalColor(DevicesUtils.getColor(R.color.color_BBBCCD));
//                titleView.setSelectedColor(DevicesUtils.getColor(R.color.color_FF698F));
//                titleView.setOnClickListener(v -> mViewpager.setCurrentItem(index));
//                return titleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null ;
            }
        });
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mViewpager);
    }
}
