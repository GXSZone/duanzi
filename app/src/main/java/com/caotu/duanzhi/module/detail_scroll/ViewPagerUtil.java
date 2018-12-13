package com.caotu.duanzhi.module.detail_scroll;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;

public class ViewPagerUtil {
    /**
     * 给ViewPager绑定自定义的滚动监听
     *
     * @param viewPager
     * @param onPageScrollListener
     */
    public static void bind(@NonNull ViewPager viewPager, @NonNull OnPageScrollListener onPageScrollListener) {
        final ViewPagerHelper helper = new ViewPagerHelper();
        // 给helper设置滚动监听
        helper.setOnPageScrollListener(onPageScrollListener);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                helper.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                helper.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                helper.onPageScrollStateChanged(state);
            }
        });
    }

}
